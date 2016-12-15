package com.flyaudio.backcar.trackbitmap;

import android.os.SystemClock;
import android.util.Log;
import android.util.Xml;
import android.view.Display;
import android.view.WindowManager;
import android.content.Context;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import com.flyaudio.xml.XMLTool;
import com.flyaudio.xml.TrackParamXMLParaser;
import com.flyaudio.backcar.BackCarService;
import com.flyaudio.data.BcData;
import com.flyaudio.backcar.util.FlyUtil;

public class BackCarTrack {
	private String TAG = "BackCarTrack";
	final String fileName = "/flysystem/flyconfig/default/modelconfig/bcconfig.xml";

	public float fbuf[] = new float[30000 * 2];
	public int maxDot = 0;
	public int pbuf[] = new int[16];

	public float lbuf[] = new float[35000];
	public int maxPoint = 0;
	public float mbuf[] = new float[80];
	public BcData bcdata[] = new BcData[4];
	private int CarType = 0;
	public int Lcar = 0; // not remove , native needed
	private static boolean hasCarline = true;
	private boolean isInitTrack = false;
	private byte[] lock = new byte[0];
	private static BackCarTrack mTrack;
	static {
		try {
			System.load("/flysystem/lib/libcarline.so");
			Log.i("BackCarTrack", "load OK\n");
			// Carline_init();
		} catch (UnsatisfiedLinkError e) {
			Log.e("BackCarTrack", "cannot load carline\n");
			System.err
					.println("Cannot load carline library:\n " + e.toString());
		}
	}

	public static BackCarTrack getInstance() {
		return mTrack;
	}

	public BackCarTrack() {
		mTrack = this;

	}

	public void Init(int carType) {

		bc_preinit();
		intTrailSize(carType);
		initParam();
	}

	public static boolean checkHasTrack() {
		return hasCarline;

	}

	public void requestAngle(double angle, int trailmode) {
		synchronized (lock) {
			setTrailmode(trailmode);
			if (angle > 90 && angle <= 140)
				Change_R(angle, trailmode);
			else if (angle <= 90 && angle >= 40)
				Change_L(angle, trailmode);

			Get_RL(trailmode, 0);
		}
		
	}

	public float[] getMBuf() {
		return mbuf;
	}

	public int[] getPBuf() {

		return pbuf;
	}

	public int getRL_point() {
		return maxPoint;
	}

	private void intTrailSize(int cartype) {
		CarType = cartype;
		WindowManager WM = (WindowManager) BackCarService.getInstance()
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = WM.getDefaultDisplay();
		int width = display.getWidth();
		int high = display.getHeight();

		FlyUtil.InitScreenSize(width, high);
		float[] sendMsg = new float[3];
		sendMsg[0] = CarType;
		sendMsg[1] = width;
		sendMsg[2] = high;
		sendParam((byte) 0x15, 2, sendMsg);

		Log.d(TAG, "screenSize " + width);
	}

	private void initParam() {
		XmlDataInit();
		if (!setParam()) {
			isInitTrack = false;
			hasCarline = false;
			return;
		}
		Log.d(TAG, "Carline_init");
		Carline_init();
		isInitTrack = true;
	}

	public native int bc_preinit();

	public native int Carline_init();

	public native void Change_R(double x, int mode);

	public native void Change_L(double x, int mode);

	public native void SendParam(byte cmd, int len, float[] data);

	public native int[] Get_Map(int x, int y);

	public native int[] Get_RL(int x, int y);

	public native void Set_Param(BcData bc); // new add

	public native void setTrailmode(int mode); // mode 1-2-3-4

	public void sendParam(byte cmd, int len, float[] data) {

		mTrack.SendParam(cmd, len, data);

	}

	public void sendParam(byte cmd, int len, float data) {
		float[] senddata = new float[2];
		senddata[0] = data;
		senddata[1] = 0;
		mTrack.SendParam(cmd, len, senddata);

	}

	public boolean isTrackInit() {
		return this.isInitTrack;
	}

	private boolean setParam() {
		if (bcdata[0] == null)
			return false;

		Log.i(TAG, "setParam");
		for (int i = 0; i < 4; i++) {
			if (bcdata[i] != null)
				setToCarLine(bcdata[i]);
		}
		return true;
	}

	private boolean setToCarLine(BcData bc) {
		// if(bc.carID != CarType)
		// return false;
		Set_Param(bc);
		return true; // test
	}

	public void ReXmlDataInit() // use for jni callmethod
	{

		XmlDataInit();
		setParam();
	}

	private void XmlDataInit() {

		InputStream is;
		Log.i(TAG, "XmlDataInit");
		try {
			// is = res.openRawResource(ID);
			FileInputStream fin = new FileInputStream(fileName);
			is = new BufferedInputStream(fin);

			TrackParamXMLParaser parser = new TrackParamXMLParaser(CarType);
			XMLTool.parse(is, parser);
			parser.getBcData(bcdata);
			is.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			Log.d(TAG, "can not found bcconfig.xml file");
		}

	}

	public int TrackCarType() {
		return this.CarType;
	}
}
