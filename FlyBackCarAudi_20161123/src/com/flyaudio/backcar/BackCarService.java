package com.flyaudio.backcar;

import java.util.Stack;
import java.io.InputStream;
import java.io.IOException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.List;
import java.util.HashMap;
import java.util.Iterator;
import java.lang.InterruptedException;
import java.util.ArrayList;
import java.io.IOException;
import java.io.InputStream;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.app.Activity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.net.MailTo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Handler;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.UserHandle;

import android.util.Log;
import com.autochips.dvr.DVR.DVRNativeEvent;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.view.Display;
import android.view.View;
import android.os.SystemProperties;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import android.cbm.CBManager;
import com.flyaudio.object.FlyEnumGaugeObj;
import com.flyaudio.data.UIDataStoreCenter;
import com.flyaudio.globaldefine.FlyaudioIntent;
import com.autochips.backcar.BackCar;
import com.flyaudio.msg.FlyUIMsgClient;
import com.flyaudio.msg.HandleServiceMsg;
import com.flyaudio.object.FlyTextObj;
import com.flyaudio.object.FlyUIObj;
import com.flyaudio.msg.FlyUIMsgClient;
import com.flyaudio.backcar.MsgType;
import com.flyaudio.backcar.PluginProxyContext;
import com.autochips.dvr.DVR;
import com.autochips.inputsource.InputSourceClient;
import com.autochips.inputsource.InputSource;

import com.flyaudio.backcar.modules.*;
import com.flyaudio.backcar913.*;
import com.flyaudio.backcarcamera.*;
import java.util.Arrays;
import com.flyaudio.tool.TrackCap;
import com.flyaudio.tool.SetContent;
import com.flyaudio.tool.TrackToolHelper;
import com.flyaudio.tool.GLTrackViewToolHelper;

import com.flyaudio.globaldefine.PageID;
import com.flyaudio.backcar.trackbitmap.BackCarTrack;
import com.flyaudio.xml.XMLTool;
import com.flyaudio.xml.PanoramaCarXMLParser;

/**
 * @author jinxing
 * 
 */
public class BackCarService extends Service {
	private static final String TAG = "BackCarService";
	private static final String TrackOpenCap = "com.backcar.trackdebug.open";
	private static final String TrackCloseCap = "com.backcar.trackdebug.close";
	private static final String SETCOLORCONTENT = "com.flyaudio.backcar.setcolor";
	private static final String TRACKTOOL = "com.flyaudio.backcar.track";
	private static final String GLTRACKVIEWTOOL = "com.flyaudio.backcar.gltrack";

	public static final String ACTION_QB_POWERON = "autochips.intent.action.QB_POWERON";
	public static final String ACTION_QB_POWEROFF = "autochips.intent.action.QB_POWEROFF";
	public static final String ACTION_QB_POWERSUSPEND = "com.flyaudio.ap.broadcast";
	public static final String USER_PRESENT = "android.intent.action.USER_PRESENT";
	public static final String ACTION_PREQB_POWERON = "autochips.intent.action.PREQB_POWERON";
	public static final String UPDATE_PKG_NAME = "com.android.provision";
	public static final String FLYDEBUG = "flyaudio.intent.action.Debug";
	public static final String SVBACKCARMODE = "flyaudio.intent.action.SvBackCarMode";
	public static final String BOOT_OK = "flyaudio.intent.action.BOOT_OK";
	public static final String USEDVR = "flyaudio.intent.action.DVR_USING";
	public static final String BACKCARIN = "flyaudio.intent.action.BackCarEnter";
	public static final String NAVIGATIONBAR = "com.android.NavigationBar.show";
	public static final String DEBUGACTION = "com.flyaudio.backcar.debug";

	private static final String UPDATEOSDVIEW = "flyaudio.systemui.updateosdview";
	public static final String STOPTXZ_ACTION = "com.flyaudio.stop.txz";
	public static final String START_SHOW_KEYGUARD = "com.flyaudio.action.SHOW_KEYGUARD";
	public static final String HYUNDAIEasyTouch = "android.action.EasyTouch";
	public static final String BENZFLOAT = "fly.backcar.benzfloat.show";
	public static final String SVREQUEST_ACTION = "fly.backcar.hdsv.requeststatus";
	public static int infoType1 = 0;
	public static int infoType2 = 0;

	// ����gama
	public boolean needToSetGama = true;

	// �Ƿ����ǰ���ӵ�Ƥ��
	public boolean hasDigInVideo = false;

	// ��Ҫ���״�����ٴ���
	public boolean needToDealRadar = false;

	// ��Ҫ�Թ켣����ٴ���
	public boolean needToDealGuiji = false;

	// �Ƿ��ǿ��Է����Ĵ���
	public boolean special_large_screen = false;
	// �����?
	public int special_screen_width = 1024;

	// �з����İ����?
	public int special_halfscreen_width = 800;

	// �Ƿ������dvr���
	public boolean hasFloatApp = false;

	// �Ƿ���Ҫ����D��Э���lpc
	public boolean needTellStallDTolpc = false;

	// ������Ƶ�Ƿ���Ҫ����������
	public boolean needHalfScreen = false;

	// �����켣Ҳ�Ƿ���Ҫ����������
	public boolean needChangeJniParam = false;

	// ����״�ҳ���
	public int floatRadarWidth = 0;

	// ǰ����toplayout�����߶ȣ���������ƵҪ���ƹ̶��߶�
	public int DigInVdoTopMargin = 0;

	private boolean getPowerOn = false;
	public int BackCarMode = -1;
	public final static int BACKCAR_NONE = 0;
	public final static int BACKCAR_USB = 1;
	public final static int BACKCAR_914 = 2;
	public final static int BACKCAR_CVB = 3;
	public final static int BACKCAR_SV = 4;
	public final static int BACKCAR_CVB_SV = 5;
	public final static int NONE_BACKCAR = 0xff;

	// private Thread mBCThread = null;
	public int mWidth;
	public int mHeight;
	private boolean mNeedInit = true;
	public boolean mTellFastBootStopLogo = false;
	private boolean isUpdateRunning = false;
	private static boolean mRunnableExit = true;
	public BackCar backcar;
	private static CBManager gCBM = null;
	private static BackCarService gInst = null;
	private Activity mActivity = null;
	public boolean mIsReversing = false;
	public PluginProxyContext proxyContext;
	public BackCarUsbCamera mUsbDvr = new BackCarUsbCamera();
	// public DigIn mDigIn = new DigIn();

	public BackCarView carview = null;

	private int times = 0;
	public int Starttimes = 0;
	private int Stoptimes = 0;
	public long globleTime = 0;
	public boolean bcStart = false;
	public boolean bcStop = true;
	private boolean flyDebug = false; // for tangyuanqing
	private int color_state = MsgType.COLOR_ARM2;
	public boolean mbackcarflag = false;
	public boolean disableAssist = false;

	// 有些机型是全景默认没有辅助线，backcarmodule 也可控，但升级时倒车那边控不了，测试提出修改此问题
	// private int[] arrayCarType = {160,808,825};
	// private String[] stringarrCarType = {"g6z132_51"};

	private ArrayList<Integer> m_360ViewCartype;
	private ArrayList<String> m_360ViewCartype2;

	private byte bcommonGammaData[] = { 0, 1, 3, 5, 7, 9, 11, 13, 16, 18, 21,
			24, 26, 29, 32, 35, 39, 43, 47, 51, 56, 61, 66, 72, 78, 84, 90, 97,
			103, 110, 116, 123, (byte) 129, (byte) 135, (byte) 141, (byte) 147,
			(byte) 153, (byte) 159, (byte) 165, (byte) 170, (byte) 175,
			(byte) 181, (byte) 186, (byte) 191, (byte) 195, (byte) 200,
			(byte) 205, (byte) 209, (byte) 213, (byte) 216, (byte) 220,
			(byte) 224, (byte) 227, (byte) 230, (byte) 233, (byte) 235,
			(byte) 237, (byte) 240, (byte) 242, (byte) 244, (byte) 246,
			(byte) 248, (byte) 250, (byte) 253 };
	/*
	 * private byte bbackcarGammaData[]={ 0,4,8,12,16,20,24,28,
	 * 32,36,40,44,48,52,56,60, 64,68,72,76,80,84,88,92,
	 * 96,100,104,108,112,116,120,124,
	 * (byte)128,(byte)132,(byte)136,(byte)140,(byte
	 * )144,(byte)148,(byte)152,(byte)156,
	 * (byte)160,(byte)164,(byte)168,(byte)172
	 * ,(byte)176,(byte)180,(byte)184,(byte)188,
	 * (byte)192,(byte)196,(byte)200,(byte
	 * )204,(byte)208,(byte)212,(byte)216,(byte)220,
	 * (byte)224,(byte)228,(byte)232
	 * ,(byte)236,(byte)240,(byte)244,(byte)248,(byte)252 };
	 */
	private byte bbackcarGammaData[] = { 0, 6, 12, 18, 25, 31, 37, 43, 49, 54,
			60, 66, 72, 78, 83, 89, 95, 100, 106, 111, 116, 121, 126,
			(byte) 130, (byte) 135, (byte) 139, (byte) 144, (byte) 148,
			(byte) 152, (byte) 156, (byte) 160, (byte) 164, (byte) 168,
			(byte) 172, (byte) 175, (byte) 179, (byte) 182, (byte) 186,
			(byte) 189, (byte) 192, (byte) 195, (byte) 198, (byte) 201,
			(byte) 204, (byte) 207, (byte) 210, (byte) 213, (byte) 215,
			(byte) 218, (byte) 221, (byte) 223, (byte) 226, (byte) 228,
			(byte) 230, (byte) 233, (byte) 235, (byte) 237, (byte) 240,
			(byte) 242, (byte) 244, (byte) 247, (byte) 249, (byte) 251,
			(byte) 253 };

	private byte bCRVGammaData[] = { 0, 9, 19, 29, 38, 47, 56, 65, 73, 82, 90,
			98, 106, 113, 121, (byte) 128, (byte) 135, (byte) 142, (byte) 149,
			(byte) 155, (byte) 162, (byte) 168, (byte) 174, (byte) 180,
			(byte) 185, (byte) 191, (byte) 196, (byte) 202, (byte) 207,
			(byte) 211, (byte) 216, (byte) 220, (byte) 224, (byte) 227,
			(byte) 231, (byte) 234, (byte) 237, (byte) 239, (byte) 242,
			(byte) 244, (byte) 246, (byte) 248, (byte) 249, (byte) 251,
			(byte) 252, (byte) 253, (byte) 254, (byte) 255, (byte) 255,
			(byte) 255, (byte) 255, (byte) 255, (byte) 255, (byte) 255,
			(byte) 255, (byte) 255, (byte) 255, (byte) 255, (byte) 255,
			(byte) 255, (byte) 255, (byte) 255, (byte) 255, (byte) 255 };
	private int ygain_common = 0;
	private int ugain_common = 0;
	private int vgain_common = 0;
	private static int ygain_crv = 0xD4;
	private static int ugain_crv = 0x98;
	private static int vgain_crv = 0XDC;
	private String AgreeStatus = USER_PRESENT;

	private FlyUIMsgClient mFlyUIMsgClient = null;
	private UIDataStoreCenter mUIMsgCenter = null;
	private BackCarModule mBackCarModule = null;
	private boolean bHaveStartActivity = false;
	public Messenger mService = null;
	public static BackCarService mBackCarService;
	private FlyRadarView mFlyRadarView = null;
	private FlyFloatRadarView mFlyFloatRadarView = null;
	public FlyBackCarMainView mFlyBackCarMainView = null;
	public int cartype = 214;
	public static String ui_carType = null;
	public BackCar913Service mBackCar913Service = null;
	private TestBackCarMode mTestBackCarMode = null;
	private BackCarTrack mBackCarTrack = null;
	BaseModule mBaseModule = null;

	public boolean isFirstTimeStart = true;

	public static BackCarService getInstance() {
		return (gInst);
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		Log.i(TAG, "Enter backcar_service onCreate");
		// for QuickBoot
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_QB_POWERON);
		filter.addAction(ACTION_QB_POWEROFF);
		filter.addAction(ACTION_QB_POWERSUSPEND);
		filter.addAction(Intent.ACTION_USER_PRESENT);
		filter.addAction(ACTION_PREQB_POWERON);
		filter.addAction(FLYDEBUG);
		filter.addAction(USEDVR);
		filter.addAction(BOOT_OK);
		filter.addAction(BACKCARIN);
		filter.addAction(TrackOpenCap);
		filter.addAction(TrackCloseCap);
		filter.addAction(NAVIGATIONBAR);
		filter.addAction(SETCOLORCONTENT);
		filter.addAction(START_SHOW_KEYGUARD);
		filter.addAction(DEBUGACTION);
		filter.addAction(TRACKTOOL);
		filter.addAction(GLTRACKVIEWTOOL);
		filter.addAction(SVREQUEST_ACTION);
		registerReceiver(mQuickBootListener, filter);

		BackCar.config(1);
		backcar = new BackCar();
		backcar.setOnSignalListener(mListenerSignal);
		mBackCarService = this;
		gInst = this;
		mFlyUIMsgClient = new FlyUIMsgClient(this);
		proxyContext = new PluginProxyContext(this);

		String ManchineNum = SystemProperties.get("fly.version.mcu");

		ui_carType = ManchineNum.substring(0, 9).toLowerCase();
		ui_carType = ui_carType.replace("-", "_");
		char c[] = ManchineNum.toCharArray();
		if (!ManchineNum.equals("")) {
			if (c[0] >= '0' && c[0] <= '9' && c[1] >= '0' && c[1] <= '9'
					&& c[2] >= '0' && c[2] <= '9')
				cartype = (c[0] - '0') * 100 + (c[1] - '0') * 10 + (c[2] - '0');
			else
				cartype = (c[3] - '0') * 100 + (c[4] - '0') * 10 + (c[5] - '0');
		}
		proxyContext.loadCarTypeRes(cartype);
		initCarTypeChoice(cartype);

		mUIMsgCenter = new UIDataStoreCenter(this, proxyContext.getResources(),
				proxyContext.getrawid("objdata"));
		mFlyRadarView = new FlyRadarView(this, proxyContext);
		mFlyFloatRadarView = new FlyFloatRadarView(this, proxyContext);
		mBackCarModule = new BackCarModule(this);
		mFlyBackCarMainView = new FlyBackCarMainView(this);
		mBackCar913Service = new BackCar913Service(this);
		mBackCar913Service.set913DiginBackcarService();

		mBackCarTrack = new BackCarTrack();
		// mTestBackCarMode = new TestBackCarMode(this);
		fordebug();
		mRunnableExit = true;

		Backcarinit();

		super.onCreate();
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.i(TAG, "onBind");
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// Log.i(TAG, "onStartCommand , intent: " + intent + " flags: " + flags
		// + " startId: " + startId);
		// Backcarinit();

		if (intent == null) {
			Log.i(TAG,
					"onStartCommand, reset after Terminate Reset Native Resource");
			BackCar.reset();
		} else if (startId == 0x01) {
			int starttime = BackCar.BC_Ioctl(0x04);
			Log.i(TAG, "onStartCommand, starttime " + starttime);
			if (starttime != 0) {
				Log.i(TAG, "onStartCommand, reset Apk with no result");
				BackCar.reset();
			}

		}
		return super.onStartCommand(intent, flags, startId);
	}

	public void load360ViewCarTypeFromXML(String fileName, int car) {
		InputStream inStream = null;

		inStream = proxyContext.getXmlInRawFromSkin(
				PluginProxyContext.externContext, fileName);
		try {
			if (inStream != null) {

				// inStream = res.openRawResource(resid);

				PanoramaCarXMLParser parser = new PanoramaCarXMLParser();
				XMLTool.parse(inStream, parser);
				m_360ViewCartype = parser.getIntCarType();
				m_360ViewCartype2 = parser.getStringCarType();

				for (Integer I : m_360ViewCartype) {
					Log.d(TAG, "360ViewCartype " + I);
					if (I == car) {
						disableAssist = true;
						break;
					}
				}

				for (String S : m_360ViewCartype2) {
					Log.d(TAG, "360ViewCartypes " + S);
					if (S.equals(ui_carType)) {
						disableAssist = true;
						break;
					}
				}
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			Log.d(TAG, "load360ViewCarTypeFromXML fail ");
		}
	}

	private void initCarTypeChoice(int car) {

		load360ViewCarTypeFromXML("cartype360view", car);

		needToSetGama = proxyContext.getBool("need_to_set_gama", true);
		hasDigInVideo = proxyContext.getBool("has_digin_video", false);
		needToDealGuiji = proxyContext.getBool("need_to_deal_guiji", false);
		needToDealRadar = proxyContext.getBool("need_to_deal_radar", false);
		special_large_screen = proxyContext.getBool("special_large_screen",
				false);
		special_screen_width = proxyContext.getInteger("special_screen_width",
				special_screen_width);
		special_halfscreen_width = proxyContext.getInteger(
				"special_halfscreen_width", special_halfscreen_width);
		hasFloatApp = proxyContext.getBool("hasFloatApp", false);
		needTellStallDTolpc = proxyContext
				.getBool("needTellStallDTolpc", false);
		needHalfScreen = proxyContext.getBool("needHalfScreen", false);
		needChangeJniParam = proxyContext.getBool("needChangeJniParam", false);
		floatRadarWidth = proxyContext.getInteger("floatRadarWidth",
				floatRadarWidth);
		DigInVdoTopMargin = proxyContext.getInteger("digIn_vdo_topmargin",
				DigInVdoTopMargin);

		Log.d(TAG, "initCarTypeChoice needToSetGama " + needToSetGama
				+ " hasDigInVideo " + hasDigInVideo + " hasFloatApp "
				+ hasFloatApp);
		Log.d(TAG, "initCarTypeChoice needToDealGuiji " + needToDealGuiji
				+ " needToDealRadar " + needToDealRadar);
		Log.i(TAG, "disableAssist =" + disableAssist);
	}

	private void Backcarinit() {
		gInst = this;

		mFlyBackCarMainView.initBackCarWM();
		mFlyRadarView.initRadarWM();
		mFlyFloatRadarView.initRadarWM();
		setBackCarMode();
		// if(getPowerOn && needHalfScreen){
		// }else
		mBackCarTrack.Init(cartype);
		intModules();
		mFlyBackCarMainView.InitBackCarView();

		mBackCarModule.tellmoduleViewisInit((byte) 0);

		if (disableAssist)
			mBackCarModule.showReverseView(false);
		mUsbDvr.DVR_Init(this);

		mBackCar913Service.ProtocolRunnableStart();
		new Thread(new BackCarRunnable()).start();
		AgreeStatus = SystemProperties.get("persist.fly.keyguard.status");
		Log.i(TAG, "Backcarinit: AgreeStatus: " + AgreeStatus);
	}

	void intModules() {
		switch (FlyBaseListener.carTag) {
		case G6Z:
			mBaseModule = G6zModule.getInstance(this);
			break;
		case HYUNDAI:
			mBaseModule = HyundaiModule.getInstance(this);
			break;
		case BENZ:
			mBaseModule = BenzModule.getInstance(this);
			break;
		case AUDIA3:
			mBaseModule = AuDiA3Module.getInstance(this);
			break;
		case AUDIA4:
			mBaseModule = AuDiA4Module.getInstance(this);
			break;
		case AUDIA6:
			mBaseModule = AuDiA6Module.getInstance(this);
			break;
		case AUDIQ3:
			mBaseModule = AuDiQ3Module.getInstance(this);
			break;
		}

	}

	Handler msgHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case MsgType.MSG_BACKCAR_START:
				Log.i(TAG, "MSG_BACKCAR_START");

				mBackCarModule.backcarstart();

				break;
			case MsgType.MSG_BACKCAR_START_913:
				Log.i(TAG, "MSG_BACKCAR_START_913");
				mBackCarModule.backcarstart913();
				break;
			case MsgType.MSG_BACKCAR_STOP:
				Log.i(TAG, "MSG_BACKCAR_STOP");
				mBackCarModule.backcarstop();
				setBcStop(true);
				mBackCar913Service.set913Lock(false);
				break;

			case MsgType.MSG_BACKCAR_STARTACTIVITY:
				Log.i(TAG, "MSG_BACKCAR_STARTACTIVITY");
				mBackCarModule.showActivity();
				break;

			case MsgType.MSG_BACKCAR_STOPACTIVITY:
				Log.i(TAG, "MSG_BACKCAR_STOPACTIVITY");
				getBaseModule().Msg_BackCar_StopActivity();
				break;
			case MsgType.MSG_BACKCAR_RUNNING:
				mBackCarModule.Running();
				break;
			case MsgType.MSG_SIGNAL_READY:
				Log.i(TAG, "MSG_SIGNAL_READY");

				getBaseModule().SIGNAL_READY();
				mBackCarModule.tellmoduleshowView(true);
				getFlyBackCarMainView().RequestLayout(mWidth, mHeight);
				mBackCarModule.showsignview(true);
				break;
			case MsgType.MSG_SIGNAL_LOST:
				Log.i(TAG, "MSG_SIGNAL_LOST");
				mBackCarModule.showsignview(false);
				break;
			case MsgType.MSG_CHECK_SIGNAL:
				Log.i(TAG, "MSG_CHECK_SIGNAL");
				mBackCarModule.checksignal();
				break;
			// case MsgType.MSG_MONITOR:
			// Log.i(TAG, "MSG_MONITOR");
			// mBackCarModule.MonitorActivity();
			// break;
			case MsgType.MSG_STOPNOTICE:
				Log.i(TAG, "MSG_STOPNOTICE");
				mBackCarModule.showNoticeView(false);
				break;
			case MsgType.MSG_SYSTEMEXIT:
				Log.i(TAG, "MSG_SYSTEMEXIT");
				mBackCarModule.backcarstop();

				getBaseModule().BeforeSystemExit();
				System.exit(0);
				break;
			case MsgType.MSG_SETGAMMA:

				if (!getBaseModule().isInKeygurad()
						&& mBackCarModule.getBackCarState()) {
					if (canWeStopLogo()) {
						setHasStopLogo();
					}
					SetBackCarGamma(MsgType.COLOR_BackCar);

				}
				break;
			/*
			 * case MsgType.MSG_BACKCAR_EXIT: Log.i(TAG, "MSG_BACKCAR_EXIT");
			 * 
			 * switch (BackCarMode) { case BACKCAR_NONE: break; case
			 * BACKCAR_USB: CameraDeInit(); break; // case BACKCAR_914:
			 * DigIn_deinit();break; case BACKCAR_CVB: break; } break; case
			 * MsgType.MSG_BACKCAR_CHOICE: switch (BackCarMode) { case
			 * BACKCAR_NONE: break; case BACKCAR_USB: // mWidth = 640; mHeight =
			 * 480; setRequestParam(640, 480); // mUsbDvr.startDVR(); break;
			 * 
			 * // case BACKCAR_914: DigIn_init(); break; case BACKCAR_CVB:
			 * break; } break;
			 */
			case MsgType.MSG_SHOWACTIVITY:
				Log.i(TAG, "1s showActivity"); // 为锟斤拷 锟斤拷证锟斤拷锟斤拷锟斤拷锟斤拷 锟斤拷锟斤拷
												// 去锟斤拷锟缴硷拷状态锟斤拷 ,锟截碉拷resume
				if (mIsReversing)
					StartActvity();
				break;
			case MsgType.MSG_BACKTO913:
				Log.i(TAG, "MSG_BACKTO913");
				mBackCarModule.ShowUiView(BackCarTag.BLACKPAGE, false);
				mBackCarModule.backcar_to913();
				setBcStop(true);
				break;
			case MsgType.MSG_913TOOTHER:
				mBackCarModule.m913ToOther();
				break;
			case MsgType.MSG_DELAYSTOP:
				IOSTOP913();
			case MsgType.BLACKPAGE:
				/*
				 * if (!mBackCarModule.getsignal() &&
				 * mBackCarService.getBackCarMode() ==
				 * mBackCarService.BACKCAR_CVB)
				 * mBackCarModule.ShowUiView(BackCarTag.BLACKPAGE, true);
				 */
				break;
			case MsgType.MSG_913START_CVB:
				STARTCVB_IN_913();
				break;
			case MsgType.MSG_TIME_COUNT:
//				DealBackCarMessages(MsgType.MSG_SENGDELAY,
//						MsgType.MSG_TIME_COUNT, 1000);
//				globleTime++;
//				if (globleTime > 60)
//					globleTime = 1;
//				Log.d(TAG, "  globleTime   " + globleTime);
				break;
			case MsgType.MSG_ONSTOP_DELAY:
				if(!FlyaudioBackCarActivity.mustDestroy)
					resumeTheActivity(true, mActivity);
					break;
			default:

				break;
			}
		}
	};

	BroadcastReceiver mQuickBootListener = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			final String action = intent.getAction();
			if (null == action) {
				Log.i(TAG, "mQuickBootListener - action is null");
				return;
			}

			if (ACTION_PREQB_POWERON.equals(action)) {
				Log.i(TAG, "mQuickBootListener - ACTION_PREQB_POWERON received");
				// Log.i(TAG,
				// "mQuickBootListener resume Backcar Playback begin");
				BackCar.resume();
				mTellFastBootStopLogo = true;

				Backcarinit();
			} else if (ACTION_QB_POWEROFF.equals(action)) {
				Log.i(TAG, "mQuickBootListener - ACTION_QB_POWEROFF received");
				// Log.i(TAG, "mQuickBootListener stop Backcar Playback begin");

				mTellFastBootStopLogo = true;
				BackCar.suspend();
				SystemProperties.set("fly.backcar.init", "0");
				mBackCarModule.BACKCAR_STOP();
				getBackCarModule().backcarMode = 3;
				mUsbDvr.DVR_Deint();

				BaseModule.getBaseModule().DealPowerOff();
				// mFlyBackCarMainView.RemoveBackCarViewPowerOff();
				// mBackCarModule.removeActivity();
				// Log.i(TAG, "mQuickBootListener stop Backcar end");
			} else if (ACTION_QB_POWERSUSPEND.equals(action)) {
				Log.i(TAG,
						"mQuickBootListener - ACTION_QB_POWERSUSPEND received");
				mRunnableExit = true;
				mBackCar913Service.ProtocolRunnableStop();
			} else if (Intent.ACTION_USER_PRESENT.equals(action)) {
				AgreeStatus = SystemProperties
						.get("persist.fly.keyguard.status");
				Log.i(TAG, "mQuickBootListener - ACTION_USER_PRESENT status"
						+ AgreeStatus);

				BaseModule.getBaseModule().SyncUser_PresentState();
				getPowerOn = false;
				isUpdateRunning = false;
				mBackCarModule.SetGammaFromNoticePage();
				mbackcarflag = true;
				getFlyBackCarMainView().setBackCarViewFlag(!mbackcarflag);

			} else if (USEDVR.equals(action)) {
				Log.i(TAG, "mQuickBootListener USEDVR");
				mUsbDvr.exit();
			} else if (BOOT_OK.equals(action)) {
				if (mIsReversing)
					DealBackCarMessages(MsgType.MSG_SENGDELAY,
							MsgType.MSG_SHOWACTIVITY, 1500);
				Log.i(TAG, "mQuickBootListener BOOT_OK");

			} else if (BACKCARIN.equals(action)) {
				// TODO
			} else if (FLYDEBUG.equals(action)) {
				// Log.i(TAG, "mQuickBootListener - FLYDEBUG status" +
				// AgreeStatus);
				byte[] bytes = intent.getByteArrayExtra("data");
				if (bytes[7] == (byte) 0xff && bytes[8] == (byte) 0xe0) {
					if (bytes[9] == (byte) 0x01) {
						flyDebug = true;
						// SystemProperties.set("persist.backcar.debug","on");
						Log.i("flydebug", "debug on");
						flyDebug(flyDebug);
					} else {
						// SystemProperties.set("persist.backcar.debug","off");
						// SystemProperties.set("persist.backcar.BuffErr", "0");
						// SystemProperties.set("persist.backcar.error", "0");
						flyDebug(flyDebug);
						flyDebug = false;
					}
				}
			} else if (START_SHOW_KEYGUARD.equals(action)) {
				Log.d(TAG, "BackCarGetAction      --START_SHOW_KEYGUARD");

			} else if (TrackOpenCap.equals(action)) {
				Log.d("DDD", "TrackOpenCap");
				getBaseModule().DEBUG_L2 = true;
				TrackCap.getInstance(mBackCarService).show();
			} else if (TrackCloseCap.equals(action)) {
				Log.d("DDD", "TrackCloseCap");
				TrackCap.getInstance(mBackCarService).hide();
			} else if (NAVIGATIONBAR.equals(action)) {
				int data = intent.getIntExtra("show", 0);
				getBaseModule().setNavigationbar(data);

			} else if (SETCOLORCONTENT.equals(action)) {
				SetContent.getInstance(mBackCarService).show();

			} else if (TRACKTOOL.equals(action)) {
				TrackToolHelper.getInstance(mBackCarService).show();

			} else if (GLTRACKVIEWTOOL.equals(action))
				GLTrackViewToolHelper.getInstance(mBackCarService).show();
			else if (DEBUGACTION.equals(action)) {
				final int mdata = intent.getIntExtra("data", 1);
				if (mdata == 1) {
					getBaseModule().DEBUG = true;
					getBaseModule().DEBUG_L2 = true;
					DealBackCarMessages(MsgType.MSG_SENGDELAY,
							MsgType.MSG_TIME_COUNT, 1000);
				} else
					getBaseModule().DEBUG = false;
				
				mBackCarModule.showsignview(true); //remove no_signal page
			} else if (SVREQUEST_ACTION.equals(action)) {
				// 申請是在倒車
				if (mBackCarModule.getBackCarState()&& mgetBackCarMode() == mBackCarService.BACKCAR_SV)
					sendSVCarMsg(1);
			}

		}

	};

	/*
	 * ==================================================broadcast Send
	 * start======================================
	 */

	public void SendBroadcast(Intent mintent) {
		this.sendBroadcastAsUser(mintent, new UserHandle(
				UserHandle.USER_CURRENT));
		// this.sendBroadcast(mintent);
	}

	public void StopTXZ() {
		Log.d(TAG, " StopTXZ ");
		Intent stopTXZIntent = new Intent();
		stopTXZIntent.setAction(STOPTXZ_ACTION);
		SendBroadcast(stopTXZIntent);

	}

	public void Start913ByUser() {
		Log.d(TAG, " Start913ByUser ");
		Intent Start913ByUser = new Intent();
		Start913ByUser.setAction("android.intent.backcar.frontRearCamera");
		SendBroadcast(Start913ByUser);

	}
	
	private void sendSVCarMsg(int m) {
		Intent mintent = new Intent();
		mintent.setAction("flyaudio.intent.action.SvBackCarMode");
		mintent.putExtra("backcar", m);
		mintent.setPackage("com.autochips.HDMI");
		Log.d("DDDD", "sendSVCarMsg "+m);
		SendBroadcast(mintent);
	}

	private void sendBackCarStatus(int m) {
		Intent mintent = new Intent();
		mintent.setAction("flyaudio.intent.action.BackCarStatus");
		mintent.putExtra("backcar", m);
		// mintent.setPackage("com.autochips.HDMI");
		SendBroadcast(mintent);

	}

	public void showHYUNDAIEasyTouch(boolean show) {
		if (FlyBaseListener.carTag == FlyBaseListener.CAR.HYUNDAI) {
			Log.d("DDD", "showHYUNDAIEasyTouch " + show);
			if (show)
				sendBroadcastToNavigationBar(HYUNDAIEasyTouch, "data", 1);
			else
				sendBroadcastToNavigationBar(HYUNDAIEasyTouch, "data", 0);
		}
	}

	/*
	 * 分屏 0 全屏 1 分屏
	 */
	public void showNavigationBar(boolean flag) {
		if (!special_large_screen) // 目前长屏只有840机型。 802
			return;
		if (flag)
			sendBroadcastToNavigationBar(NAVIGATIONBAR, "show", 0);
		else
			sendBroadcastToNavigationBar(NAVIGATIONBAR, "show", 1);
	}

	/*
	 * # 0 hide # 1 show
	 * 
	 * @data 0 video page data -1 choicepage data 2 video exit
	 */
	public void showFloatApp(boolean flag, int data) {
		if (!hasFloatApp) // 840 802
			return;
		// if(BaseModule.getBaseModule() != null){

		// if(BaseModule.getBaseModule().CurrentCarPage() == 0x01)
		// return;
		// }
		Log.d("DDD", "showFloatApp " + flag);
		if (flag)
			sendBroadcastToFloatApp(BENZFLOAT, "show", 1, data);
		else
			sendBroadcastToFloatApp(BENZFLOAT, "show", 0, data);
	}

	// data 2 video exit
	public void syncFloatExitVideo() {
		if (!hasFloatApp) // 840 802
			return;
		sendBroadcastToFloatApp(BENZFLOAT, "show", 2, 2);
	}

	public void sendBroadcastToNavigationBar(String action, String key,
			int data1) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(action);
		intent.putExtra(key, data1);

		SendBroadcast(intent);
	}

	/**
	 * 发送广播给分屏悬浮应用
	 * 
	 * @param action
	 * @param key
	 * @param data1
	 *            是否显示悬浮按钮 data2 区分是返回的选择页
	 * 
	 */
	public void sendBroadcastToFloatApp(String action, String key, int data1,
			int data2) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(action);
		byte[] data = new byte[2];
		data[0] = (byte) data1;
		data[1] = (byte) data2;
		intent.putExtra(key, data);

		SystemProperties.set("tmp.floatapp.show", "" + data1);

		SendBroadcast(intent);

	}

	/*
	 * 奥迪A4L的广播，在systemui 状态栏显示对应底图
	 * 
	 * 
	 * ID : pagekagename_ID
	 */
	public void sendBroadcastToSystemUI(String ID, String icon, String content,
			int type) {
		Intent intent = new Intent(UPDATEOSDVIEW);
		intent.putExtra("id", ID);
		intent.putExtra("icon", icon);
		intent.putExtra("content", content);
		intent.putExtra("type", type);
		SendBroadcast(intent);
	}

	/*
	 * ==================================================broadcast Send
	 * end======================================
	 */
	private void fordebug() {
		/* ...........for debug............. */
		if (SystemProperties.get("persist.backcar.debug").equals("on"))
			flyDebug = true;
		if (SystemProperties.get("persist.syserr.test").equals("1"))
			flyDebug = true;
		if (SystemProperties.get("tmp.backcar.flag").equals("on")) {

			if (flyDebug) {
				String s = SystemProperties.get("persist.backcar.error");
				int fs = 0;
				if (!("").equals(s))
					fs = Integer.valueOf(s).intValue() + 1;
				// SystemProperties.set("persist.backcar.error", ""+fs);
			}
			mbackcarflag = true;
		} else {
			SystemProperties.set("tmp.backcar.flag", "on");
			String s2 = SystemProperties.get("persist.backcar.error");

		}
		/* ...........for debug............. */
	}

	private void setBackCarMode() {
		String ss = SystemProperties.get("persist.backcar.mode");
		if (ss.equals("")) {
			if (hasDigInVideo) {
				SystemProperties.set("persist.backcar.mode", "0"); // 0 -none 1
																	// -usb 2
																	// -913 3-
																	// cvb 4-
																	// quanjing
				Log.i(TAG, "BackCarMode is null  setmode yuanche");
			} else {
				SystemProperties.set("persist.backcar.mode", "3");
				Log.i(TAG, "BackCarMode is null  setmode cvb");
			}
		} else
			BackCarMode = Integer.valueOf(ss).intValue();
		SystemProperties.set("fly.backcar.init", "1");

	}

	public int mgetBackCarMode() {
		String ss = SystemProperties.get("persist.backcar.mode");
		int s = 3;
		if (ss != null)
			s = Integer.valueOf(ss).intValue();
		return s;
	}

	public void setRequestParam(int param1, int param2) {
		mWidth = param1;
		mHeight = param2;
	}

	private BackCar.OnSignalListener mListenerSignal = new BackCar.OnSignalListener() {
		public void onSignal(int msg, int param1, int param2) {
			// Log.i(TAG,"onSignal, msg:" + msg + " param1: " + param1 +
			// " param2: " + param2);
			switch (msg) {
			case BackCar.SIGNAL_READY:

				// Log.i(TAG, "Signal is on!");
				mWidth = param1;
				mHeight = param2;
				if (isCVBVdoMode())
					mBackCarModule.SIGNAL_READY();
				break;

			case BackCar.SIGNAL_LOST:
				// Log.i(TAG, "Signal is off!");
				if (isCVBVdoMode())
					mBackCarModule.SIGNAL_LOST();
				break;

			default:
				return;
			}
		}
	};

	public void setActivity(Activity activity) {
		// Log.i(TAG, "setActivity - mIsReversing = " + getReversing());
		if ((null != mActivity) && (mActivity != activity)) {
			// Log.i(TAG,
			// "It is repetition start activity! So finish the older!");
			Log.d(TAG, "old activity finsh");
			mActivity.finish();

		}
		mActivity = activity;
		if (!getReversing() && !mBackCarModule.getRadarViewState()) {
			msgHandler.removeMessages(MsgType.MSG_BACKCAR_STOPACTIVITY);
			Log.i(TAG, "stop activity");
			msgHandler.sendEmptyMessage(MsgType.MSG_BACKCAR_STOPACTIVITY);
		}
	}

	public void setActivityEmpty() {

		mActivity = null;
	}

	public void detachUsb() {
		DealBackCarMessages(MsgType.MSG_REMOVE, MsgType.MSG_SETGAMMA, 0);
		SetBackCarGamma(MsgType.COLOR_ARM2);
		DealBackCarMessages(MsgType.MSG_REMOVE, MsgType.MSG_CHECK_SIGNAL, 0);
		DealBackCarMessages(MsgType.MSG_REMOVE, MsgType.MSG_SIGNAL_READY, 0);
		DealBackCarMessages(MsgType.MSG_SENGDELAY, MsgType.MSG_SIGNAL_LOST, 0);
	}

	public void setCarView(BackCarView v) {
		Log.i(TAG, "surface view  carview");
		carview = v;
		// mBackCar913Service.setBackcarView(v);
		mUsbDvr.setCarView(v);
	}

	public BackCarView getBackCarView() {
		return mFlyBackCarMainView.getBackCarView();
	}

	public int getspecialFullWidth() {
		return special_screen_width;

	}

	public int getspecialHalfWidth() {
		return special_halfscreen_width;

	}

	public int getBackCarMode() {
		return BackCarMode;
	}

	public void setBcStop(boolean flag) {
		Log.d(TAG, "setBcStop  " + flag);
		bcStop = flag;
	}

	public boolean canWeStopLogo() {
		return mTellFastBootStopLogo;
	}

	public void setHasStopLogo() {
		Log.d(TAG, "setHasStopLogo ctl.stop");
		try {
			Runtime runtime = Runtime.getRuntime();
			runtime.exec("su");
			runtime.exec("setprop  ctl.stop bootanim");
		} catch (IOException e) {
			Log.e("Exception when doBack", e.toString());
		}

		SystemProperties.set("ctl.stop", "bootanim");
		mTellFastBootStopLogo = false;
	}

	public void SetFlyUIObjProperty(ViewGroup vg) {
		for (int i = 0; i < vg.getChildCount(); i++) {
			View v = vg.getChildAt(i);
			if (v instanceof ViewGroup) {
				SetFlyUIObjProperty((ViewGroup) v);
			} else {
				mUIMsgCenter.SetFlyUIObjProperty(v);
			}
		}
	}

	public UIDataStoreCenter getUIMsgSaveCenter() {
		return mUIMsgCenter;
	}

	public Messenger getmService() {
		return mService;
	}

	public FlyUIMsgClient getFlyUIMsgClient() {
		return mFlyUIMsgClient;
	}

	public UIDataStoreCenter getUIMsgCenter() {
		return mUIMsgCenter;
	}

	public BackCarModule getBackCarModule() {
		return mBackCarModule;
	}

	public BackCar913Service getBackCar913Service() {
		return mBackCar913Service;
	}

	public BaseModule getBaseModule() {
		return mBaseModule;
	}

	public AuDiA4Module getAudiA4Module() {
		return AuDiA4Module.getInstance(this);
	}

	public SharedPreferences getPreferencesFromService() {
		return getFlyBackCarMainView().getPreferences();
	}

	public void setmService(Messenger mService) {
		this.mService = mService;
	}

	public void setReversing(boolean mIsReversing) {
		this.mIsReversing = mIsReversing;
	}

	public boolean getReversing() {
		return mIsReversing;
	}

	public boolean isHomeKey = false;

	public void resumeTheActivity(boolean v, Activity activity) {
		mBackCarModule.setNeedFinshActivity(false);
		if (mBackCarModule.getBackCarState() && mIsReversing)
			StartActvity();
		else if (mBackCar913Service.is913StartByUser()
				|| mBackCar913Service.getBackCar913StallDState()) {

			if (activity == mActivity)
				BaseModule.getBaseModule().DealActivityStop();

		}

	}

	public boolean isCVBVdoMode() {
		if (BackCarMode == BACKCAR_CVB || BackCarMode == BACKCAR_CVB_SV)
			return true;
		else
			return false;
	}

	public void StartActvity() {
		// DealBackCarMessages(MsgType.MSG_SENGDELAY, MsgType.BLACKPAGE, 0);
		Intent intentBCActvity = new Intent(this.getApplicationContext(),
				FlyaudioBackCarActivity.class);
		intentBCActvity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		StartActvity(intentBCActvity);
		Log.i(TAG, "showActivity - BackCarActivity");
	}

	public void StartActvity(Intent intentBCActvity) {

		// this.startActivity(intentBCActvity); // for back to acvivity resume
		this.startActivityAsUser(intentBCActvity, new UserHandle(
				UserHandle.USER_CURRENT)); // for back to acvivity resume

	}

	public FlyRadarView getFlyRadarView() {
		return mFlyRadarView;
	}

	public FlyFloatRadarView getFlyFloatRadarView() {
		return mFlyFloatRadarView;

	}

	public FlyBackCarMainView getFlyBackCarMainView() {
		return mFlyBackCarMainView;
	}

	public void resetContentView(int layout) {
		// TODO Auto-generated method stub
		if (mBackCarModule.getCurPageID() != layout
				|| mBackCarModule.getRadarViewState() == false)
			jumpPage(layout);
	}

	public Activity getActivity() {
		return mActivity;
	}

	public final void MakeAndSendMessage(int ControlID, byte ControlType,
			byte[] param) {
		// Log.d("send", "MakeAndSendMessage : " + ControlID);

		int sendbufLen = 9 + param.length;
		if (sendbufLen > 512)
			return;

		byte[] sendbuf = new byte[sendbufLen];
		sendbuf[0] = (byte) 0xff;
		sendbuf[1] = 0x55;
		sendbuf[2] = (byte) (param.length + 6);
		sendbuf[3] = (byte) ((ControlID >> 24) & 0xff);
		sendbuf[4] = (byte) ((ControlID >> 16) & 0xff);
		sendbuf[5] = (byte) ((ControlID >> 8) & 0xff);
		sendbuf[6] = (byte) (ControlID & 0xff);
		sendbuf[7] = ControlType;
		for (int i = 0; i < param.length; i++) {
			sendbuf[8 + i] = param[i];
		}

		Message msg = Message.obtain(null, 10);
		Bundle bundle = new Bundle();
		bundle.putByteArray("data", sendbuf);
		bundle.putInt("len", sendbufLen);
		msg.setData(bundle);
		try {
			if (null != mService)
				mService.send(msg);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public final void MakeAndSendMessageButton(int ControlID) {
		mBackCarModule
				.MakeAndSendMessageButton(MsgType.FLYOBJ, ControlID, null);
	}

	public final void MakeAndSendMessageFocus(int tag) {
		mBackCarModule.MakeAndSendMessageButton(MsgType.OBJFOCUS, tag, null);
	}

	public final void MakeAndSendMessageObjVisble(int tag) {
		mBackCarModule.MakeAndSendMessageButton(MsgType.OBJVISIBLE, tag, null);
	}

	public final void MakeAndSendMessageBgChange(int tag) {
		mBackCarModule.MakeAndSendMessageButton(MsgType.OBJSETBG, tag, null);
	}

	public final void MakeAndSendMessageLight(int tag) {
		mBackCarModule.MakeAndSendMessageButton(MsgType.SETLIGHT, tag, null);
	}

	public final void MakeAndSendMessageWithBundle(int way, int tag,
			Bundle vaule) {
		mBackCarModule.MakeAndSendMessageButton(way, tag, vaule);
	}

	public boolean ISUPDATE() {
		return isUpdateRunning;
	}

	public final void getSurface913Point(int xy[]) {
		if (xy.length < 4)
			return;
		xy[0] = getBackCarView().Point_913_180_X;
		xy[1] = getBackCarView().Point_913_180_Y;
		xy[2] = getBackCarView().Point_913_150_X;
		xy[3] = getBackCarView().Point_913_150_Y;
	}

	public final void setSurface913Point(int xy[]) {
		if (xy.length < 4)
			return;
		getBackCarView().Point_913_180_X = xy[0];
		getBackCarView().Point_913_180_Y = xy[1];
		getBackCarView().Point_913_150_X = xy[2];
		getBackCarView().Point_913_150_Y = xy[3];
	}

	public void jumpPage(int layout) {
		// Log.d(TAG, " layout :" + layout);
		mBackCarModule.RemoveRadarView();
		mBackCarModule.setCurPageID(layout);

		if (mBackCarModule.getsignal()) {
			return;
		}
		// msgHandler.removeMessages(MsgType.MSG_BACKCAR_STARTACTIVITY);
		msgHandler.sendEmptyMessage(MsgType.MSG_BACKCAR_STARTACTIVITY);
		mBackCarModule.initRadarView(layout);
		mBackCarModule.ShowRadarView();
	}

	public void getCurrentgama() {
		/*
		 * BackCar.BC_SetContrastLevel(36); BackCar.BC_SetHueLevel(65);
		 * BackCar.BC_SetSaturationLevel(51);
		 */
	}

	public int Arm2_Bri = 0;
	public int Arm2_Con = 0;
	public int Arm2_Hue = 0;
	public int Arm2_Sta = 0;

	private void init() {
		BackCar.GetFB0GammaTbl(bcommonGammaData);
		ygain_common = BackCar.BC_GetYGainLevel();
		ugain_common = BackCar.BC_GetUGainLevel();
		vgain_common = BackCar.BC_GetVGainLevel();

		Arm2_Bri = BackCar.BC_GetBrightnessLevel();
		Arm2_Con = BackCar.BC_GetContrastLevel();
		Arm2_Hue = BackCar.BC_GetHueLevel();
		Arm2_Sta = BackCar.BC_GetSaturationLevel();

		Log.d(TAG, "isFirstTimeStart " + isFirstTimeStart);
		if (!isFirstTimeStart) {
			Arm2_Bri = getBaseModule().getPreferenceInt("Arm2_Bri", Arm2_Bri);
			Arm2_Con = getBaseModule().getPreferenceInt("Arm2_Con", Arm2_Con);
			Arm2_Hue = getBaseModule().getPreferenceInt("Arm2_Hue", Arm2_Hue);
			Arm2_Sta = getBaseModule().getPreferenceInt("Arm2_Sta", Arm2_Sta);
			if (needToSetGama)
				BackCar.SetFB0GammaTbl(bcommonGammaData);
			getBaseModule().SetColor_ARM2();

			// if not firsttime start send onetime show floatapp
			syncFloatExitVideo();
		} else {

			getBaseModule().saveEditToPreference("Arm2_Bri", Arm2_Bri);
			getBaseModule().saveEditToPreference("Arm2_Con", Arm2_Con);
			getBaseModule().saveEditToPreference("Arm2_Hue", Arm2_Hue);
			getBaseModule().saveEditToPreference("Arm2_Sta", Arm2_Sta);
		}

		Log.d(TAG, "BC_GetContrastLevel " + Arm2_Con + " hue " + Arm2_Hue
				+ " sta " + Arm2_Sta + " bri " + Arm2_Bri);
		// Log.i(TAG, "BC_GetUGainLevel Y:U:V " + ygain_common + " : " +
		// ugain_common + " : " + vgain_common);

		mBackCarModule.initLocalLanguage();
		mBackCarModule.initpageid();
		try {
			if (Settings.Secure.getInt(getContentResolver(),
					Settings.Secure.DEVICE_PROVISIONED) == 0) {
				isUpdateRunning = true;
				Log.i(TAG, "isUpdateRunnig true");
			}
		} catch (SettingNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		flyDebug(true);
	}

	public void STARTCVB_IN_913() {
		getBackCarView().deinitDigIn();
		mBackCarModule.RemoveBackCarView();
		mBackCarModule.setsignal(false);
		BackCar.config(1);
		BackCar.start();

		msgHandler.sendEmptyMessage(MsgType.MSG_913TOOTHER);

	}

	void IOSTOP913() {
		mBackCar913Service.set913Lock(true); // before exit over lock
		byte stopData[] = { MsgType.TRANSFER_BACKCAR_913_STOP };
		setServiceMsgTo913(stopData);
	}

	public void BACKCAR_START() {
		switch (mgetBackCarMode()) {
		case BACKCAR_914: // 913 will not go here

			break;
		case BACKCAR_CVB_SV:
		case BACKCAR_CVB:
			if (mBackCar913Service.BackCar913StartByUser) {
				msgHandler.sendEmptyMessage(MsgType.MSG_913START_CVB);
			} else {
				BackCar.config(1);
				BackCar.start();
				msgHandler.sendEmptyMessage(MsgType.MSG_BACKCAR_START);
			}
			break;
		case BACKCAR_USB:
			CameraInit();
			if (mBackCar913Service.BackCar913StartByUser) {
				getBackCarView().deinitDigIn();
				mBackCarModule.RemoveBackCarView();
				msgHandler.sendEmptyMessage(MsgType.MSG_913TOOTHER);
			} else
				msgHandler.sendEmptyMessage(MsgType.MSG_BACKCAR_START);
			break;
		default:
			break;
		}

	}

	public void BACKCAR_STOP() {

		if (isCVBVdoMode()) {
			msgHandler.sendEmptyMessageDelayed(MsgType.MSG_SYSTEMEXIT,
					MsgType.SYSTEMEXIT_TIMEOUT);
			BackCar.stop();

		} else if (BackCarMode == BACKCAR_USB) {
			CameraDeInit();

		}

		msgHandler.removeMessages(MsgType.MSG_SYSTEMEXIT);
		msgHandler.removeMessages(MsgType.MSG_SIGNAL_LOST);
		if (mBackCar913Service.BackCar913StartByUser)
			msgHandler.sendEmptyMessage(MsgType.MSG_BACKTO913);
		else {
			msgHandler.sendEmptyMessage(MsgType.MSG_BACKCAR_STOP);
		}
	}

	public void DealBackCarMessages(int DealMessages, int iMsgType, int time) {
		if (DealMessages == MsgType.MSG_SENG) {
			msgHandler.sendEmptyMessage(iMsgType);
		}
		if (DealMessages == MsgType.MSG_SENGDELAY) {
			msgHandler.sendEmptyMessageDelayed(iMsgType, time);
		}
		if (DealMessages == MsgType.MSG_REMOVE) {
			msgHandler.removeMessages(iMsgType);
		}
	}

	public void ReSet_Screen_Color() {
		BackCar.BC_SetContrastLevel(Arm2_Con);
		BackCar.BC_SetHueLevel(48);
		BackCar.BC_SetSaturationLevel(Arm2_Sta);

	}

	public void SetBackCarGamma(int state) {
		// 鍊间负ACTION_SCREEN_ON 琛ㄧず寮�灞�
		// ACTION_SCREEN_OFF 琛ㄧず 閿佸睆
		// ACTION_USER_PRESENT 琛ㄧず 瑙ｉ攣锛堝氨鏄笉鍦ㄥ悓鎰忛〉闈級
		if (!AgreeStatus.equals(USER_PRESENT) && !isUpdateRunning)
			return;
		if (!needToSetGama) {

			if (state == MsgType.COLOR_ARM2
					&& color_state != MsgType.COLOR_ARM2 && isCVBVdoMode()) {
				color_state = state;
				getBaseModule().SetColor_ARM2();

			} else if (state == MsgType.COLOR_BackCar
					&& color_state != MsgType.COLOR_BackCar && isCVBVdoMode()) {
				color_state = state;

				getBaseModule().SetColor_BackCar();

			}
			return;
		}
		if (state == MsgType.COLOR_ARM2 && color_state != MsgType.COLOR_ARM2) {

			BackCar.SetFB0GammaTbl(bcommonGammaData);
			color_state = state;

			/*
			 * if(mBackCarModule.getSetColor()) { //
			 * BackCar.BC_SetBrightnessLevel(50); //50
			 * BackCar.BC_SetContrastLevel(Arm2_Con); //25
			 * BackCar.BC_SetHueLevel(48); //48
			 * BackCar.BC_SetSaturationLevel(Arm2_Sta); //52 }
			 */
			getBaseModule().SetColor_ARM2();

			Log.i(TAG, "SetBackCarGamma COLOR_ARM2 ");
		} else if (state == MsgType.COLOR_BackCar
				&& color_state != MsgType.COLOR_BackCar) {

			color_state = state;

			if (mBackCarModule.getsignal())// 鍥犱负寤舵椂鐨勫師鍥狅紝淇濊瘉娌℃湁缁檚top锛�
			{
				if (isCVBVdoMode())
					BackCar.SetFB0GammaTbl(bCRVGammaData);// bCRVGammaData

				getBaseModule().SetColor_BackCar();

				Log.i(TAG, "SetBackCarGamma .COLOR_BackCar  ");
			}
			// BackCar.BC_SetYGainLevel(GET_GAIN_VALUE(ygain_crv) );
			// BackCar.BC_SetUGainLevel(GET_GAIN_VALUE(ugain_crv) );
			// BackCar.BC_SetVGainLevel(GET_GAIN_VALUE(vgain_crv) );

		} else {
			Log.e(TAG, "SetBackCarGamma " + state);
		}
		// Log.i("BBB2",
		// "b "+BackCar.BC_GetBrightnessLevel()+" c "+BackCar.BC_GetContrastLevel()+" h "+BackCar.BC_GetHueLevel()
		// +"s "+BackCar.BC_GetSaturationLevel());
		// Log.i(TAG, "BC_GetUGainLevel Y:U:V " + BackCar.BC_GetYGainLevel() +
		// " : " + BackCar.BC_GetUGainLevel() + " : " +
		// BackCar.BC_GetVGainLevel());
	}

	void printColor() {
		int Bri = BackCar.BC_GetBrightnessLevel();
		int Con = BackCar.BC_GetContrastLevel();
		int Hue = BackCar.BC_GetHueLevel();
		int Sta = BackCar.BC_GetSaturationLevel();
		Log.d("DDD", "B " + Bri + " C " + Con + " H " + Hue + " S " + Sta);
	}

	private int GET_PERCENTAGE_VALUE(int value) {
		return ((value * 100) / 255);
	}

	private int GET_HUE_VALUE(int value) {
		return (value * 100 / 0x3F);

	}

	private int GET_GAIN_VALUE(int value) {
		// return (value *100/0xFF);
		return (value);
	}

	public void SetColor(int mx, int my) {
		// BackCar.SetBrightness(GET_PERCENTAGE_VALUE(0x88));
		// BackCar.SetHue(GET_HUE_VALUE(0x1E));
		// BackCar.SetSaturation(GET_PERCENTAGE_VALUE(0x7E));
		/*
		 * int mhue = 0; int msat = 0; int mbrness = 0; if(my < 200) { mbrness =
		 * (mx -10)/10;
		 * 
		 * BackCar.SetBrightness(mbrness); Log.i(TAG, "mbrness " + mbrness);
		 * }else if(my < 400){ mhue = (mx -10)/10; BackCar.SetHue(mhue);
		 * Log.i(TAG, "mhue " + mhue); }else{ msat = (mx -10)/10;
		 * BackCar.SetSaturation(msat); Log.i(TAG, "msat " + msat); }
		 */
	}

	/*
	 * @ fr_mode 913 fr mode # 0 front rear both # 1 only front # 2 only rear #
	 * 3 our front origin_car rear
	 */
	public void set913Camera_fr_mode(int fr_mode) {
		SystemProperties.set("persist.camera.fr.mode", "" + fr_mode);
		getBaseModule().setUpFrontRearDevice(fr_mode);

	}

	public int get913Camera_fr_mode() {
		int ret = 0;
		String s = SystemProperties.get("persist.camera.fr.mode");
		if (!("").equals(s))
			ret = Integer.valueOf(s).intValue();
		else
			SystemProperties.set("persist.camera.fr.mode", "0");
		return ret;
	}

	void flyDebug(boolean flag) // for tangyuanqing
	{
		if (flag) {
			String s = SystemProperties.get("persist.backcar.error");
			int fs = Starttimes;
			byte low = (byte) (fs % 255);
			byte high = (byte) ((fs >> 8) & 0xFF);
			mBackCarModule.tellmoduleErrTimes((byte) 0x01, low, high);
			String s2 = SystemProperties.get("persist.backcar.BuffErr");
			fs = Stoptimes;
			low = (byte) (fs % 255);
			high = (byte) ((fs >> 8) & 0xFF);

			mBackCarModule.tellmoduleErrTimes((byte) 0x02, low, high);
		}
	}

	// TestBackCarMode -------------------------//

	public void IO_START() {

		Starttimes++;
		Log.i(TAG, "IO_START EVENT_BACKCAR_START event " + Starttimes);
		String ss = SystemProperties.get("persist.backcar.mode");
		Log.i(TAG, "BackCarMode is [ " + ss + " ]");
		if (!ss.equals(""))
			BackCarMode = Integer.valueOf(ss).intValue();
		if (BACKCAR_SV == BackCarMode)
			sendSVCarMsg(1);
		else if (BACKCAR_NONE == BackCarMode)
			Log.i(TAG, "Backcar yuanche in");
		else if (BACKCAR_914 == BackCarMode) {
			byte startData[] = { MsgType.TRANSFER_BACKCAR_913_START };
			setServiceMsgTo913(startData);
		} else {

			mBackCarModule.BACKCAR_START();

		}

	}

	public void IO_STOP() {

		setBcStop(false);
		Stoptimes++;
		Log.i(TAG, "IO_STOP EVENT_BACKCAR_STOP event " + Stoptimes);

		if (BACKCAR_SV == BackCarMode) {
			sendSVCarMsg(0);
			setBcStop(true);
		} else if (BACKCAR_NONE == BackCarMode) {
			Log.i(TAG, "Backcar yuanche out");
			setBcStop(true);
		} else if (BACKCAR_914 == BackCarMode) {
			byte stopData[] = { MsgType.TRANSFER_BACKCAR_913_STOP };
			setServiceMsgTo913(stopData);

		} else {

			mBackCarModule.BACKCAR_STOP();

		}

	}

	public void getModuleMsg(byte data[]) {
		// Log.d("backcarServiceMsg", " getModuleMsg ############## " +
		// HandleServiceMsg.bytes2HexString( data));
		Message msg = myHandleModuleMsg.obtainMessage();

		Bundle dataTemp = new Bundle();
		dataTemp.putByteArray("BACKCARMODULE", data);
		msg.what = 2;
		msg.setData(dataTemp);
		myHandleModuleMsg.sendMessage(msg);
	}

	public void setServiceMsgTo913(byte data[]) {
		mBackCar913Service.GetBackCarServiceData(data);
	}

	public void setModuleMsgTo913(byte data[]) {
		mBackCar913Service.GetBackCarModuleData(data);
	}

	public void setUserEnterMsgTo913(byte data[]) {
		mBackCar913Service.GetBackCarEnterByUser(data);
	}

	void syncBackCar() {
		String ss = SystemProperties.get("tmp.backcar.sync");
		byte data[] = new byte[2];

		Log.d("DDD", "syncBackCar   [ " + ss + " ]");
		if (!ss.equals("")) {
			data[0] = (byte) 0x02;
			isFirstTimeStart = false;
		} else
			data[0] = 0;
		BackCar.syncAppStateToJNI(data);
	}

	private void CameraInit() {
		setRequestParam(640, 480);
		mUsbDvr.startDVR();
	}

	private void CameraDeInit() {
		mUsbDvr.stopDVR();
	}

	public void exit_io_thread() {
		mRunnableExit = true;
		mBackCarModule.BACKCAR_STOP();
	}

	public void testRunning() {
		Message tmpMsg = Message.obtain();
		tmpMsg.what = MsgType.MSG_BACKCAR_RUNNING;
		msgHandler.sendMessage(tmpMsg);
	}

	public class BackCarRunnable implements Runnable {
		@Override
		public void run() {
			Log.i(TAG, "BackCar Service Thread entry");
			if (null == gCBM) {
				// Log.i(TAG,"Create cbm object! \r\n");
				gCBM = new CBManager();
			}

			if (mNeedInit) {
				Log.d("DDD", "mNeedInit ");
				syncBackCar();
				BackCar.init();
				SystemProperties.set("tmp.backcar.sync", "2");
				Log.d("DDD", "BackCar.init() ");
			}

			if (null != gCBM) {
				Log.i(TAG, "Inform cbm arm1 is ready !");
				gCBM.systemReady();
				gCBM = null;
			}

			if (mRunnableExit)
				mRunnableExit = false;
			else {
				Log.d("DDD", "mRunnableExit  exit ");
				return;
			}
			init();
			bcStop = true;

			while (!mRunnableExit) {
				if (!bcStop)
					continue;
				Message msg = BackCar.getEvent();

				if (mRunnableExit) {
					Log.e("DDD", "BackCar mRunnableExit");
					break;
				}
				if (null == msg) {
					Log.e(TAG, "BackCar break null == msg");
					// break;
				}
				if (BackCar.EVENT_BACKCAR_START == msg.what) {
					Starttimes++;
					// globleTime = System.currentTimeMillis();

					Log.i(TAG, "Receive EVENT_BACKCAR_START event "
							+ Starttimes);
					String ss = SystemProperties.get("persist.backcar.mode");
					Log.i(TAG, "BackCarMode is [ " + ss + " ]");
					if (!ss.equals(""))
						BackCarMode = Integer.valueOf(ss).intValue();
					if (BACKCAR_SV == BackCarMode) {
						sendSVCarMsg(1);
						mBackCarModule.SetBackCar913Status(true);
					} else if (BACKCAR_NONE == BackCarMode)
						Log.i(TAG, "Backcar yuanche in");
					else if (BACKCAR_914 == BackCarMode) {
						DealBackCarMessages(MsgType.MSG_REMOVE,
								MsgType.MSG_DELAYSTOP, 0);
						mBackCar913Service.set913StallState(1);
						byte startData[] = { MsgType.TRANSFER_BACKCAR_913_START };
						setServiceMsgTo913(startData);
					} else {
						mBackCar913Service.set913StallState(1); // 2016 -10-31

						mBackCarModule.BACKCAR_START();
						if (needTellStallDTolpc)
							mBackCar913Service.tellModuleStallDStatus(true);
					}
					/* ...........for debug............. */
					// if(SystemProperties.get("persist.backcar.debug").equals("on"))
					// flyDebug = true;
					// else flyDebug = false;
					// if(SystemProperties.get("persist.syserr.test").equals("1"))
					// // for test
					// flyDebug = true;
					flyDebug(flyDebug);
					/* ...........for debug............. */
				} else if (BackCar.EVENT_BACKCAR_STOP == msg.what) {
					setBcStop(false);
					Stoptimes++;

					Log.i(TAG, "Receive EVENT_BACKCAR_STOP event " + Stoptimes);

					if (BACKCAR_SV == BackCarMode) {
						sendSVCarMsg(0);
						mBackCarModule.SetBackCar913Status(false);
						setBcStop(true);
					} else if (BACKCAR_NONE == BackCarMode) {
						Log.i(TAG, "Backcar yuanche out");
						setBcStop(true);
					} else if (BACKCAR_914 == BackCarMode) {
						mBackCar913Service.DealBackCar913Messages(
								MsgType.MSG_REMOVE,
								MsgType.MSG_BACKCAR_913_U_START, 0);
						if (FlyBaseListener.getCur_fr_mode() == MsgType.FR_MODE) {
							mBackCar913Service.setBackCar913StallDState(false);
							DealBackCarMessages(MsgType.MSG_SENGDELAY,
									MsgType.MSG_DELAYSTOP,
									MsgType.MSG_DELAYSTOP_TIMEOUT);
						} else if (FlyBaseListener.getCur_fr_mode() == MsgType.R_MODE)
							IOSTOP913();
						else {
							IOSTOP913();
							setBcStop(true);
						}

					} else {
						mBackCar913Service.set913StallState(0); // 2016 -10-31

						mBackCarModule.BACKCAR_STOP();

					}

					/* ...........for debug............. */
					flyDebug(flyDebug);
					/* ...........for debug............. */
				} else if (BackCar.EVENT_BACKCAR_RUNNING == msg.what) {

					// Log.i(TAG, "Receive EVENT_BACKCAR_RUNNING event");
					// testRunning();
			// mBackCarService.testRunning();
				} else if (BackCar.EVENT_BACKCAR_ERROR == msg.what) {
					Log.e(TAG,
							"BackCar break Receive EVENT_BACKCAR_ERROR event");
					// msg.recycle();
					/* ...........for debug............. */
					// if(flyDebug) {
					// String srr =
					// SystemProperties.get("persist.backcar.BuffErr");
					// int times = 1;
					// if(!("").equals(srr))
					// times = Integer.valueOf(srr).intValue()+1;
					// SystemProperties.set("persist.backcar.BuffErr",
					// ""+times);
					// }
					/* ...........for debug............. */
					// break;
				}
				try {
					msg.recycle();
					Thread.sleep(10);
				} catch (Exception e) {
					// Log.e(TAG, "BackCar Exception e");
					e.printStackTrace();
				}
			}
			setBcStop(true);
			Log.i(TAG, "BackCar Service Thread exit");
		}
	}

	Handler myHandleModuleMsg = new Handler() {
		public void handleMessage(android.os.Message msg) {

			if (msg.what == 2) {
				Bundle data = msg.getData();
				byte paramer[] = data.getByteArray("BACKCARMODULE");
				// Log.d(TAG, "myCarMsgHandler ||  " +
				// bytes2HexString(paramer));
				byte cmd = paramer[0];
				switch (cmd) {
				case 0x01:
					if (paramer.length > 2) {
						// int m = (paramer[1]&0x07); //
						int m = paramer[1];
						Log.d("BackCar913", "get stall from module " + m);
						mBackCar913Service.set913StallState(m);

						// int m = (data[1]&0x07); //000b:P 001b:R 010b:N 100b:D
						// 0 1 2 4
						// int n = (data[2] & 0x03); //Bit0:宸﹁浆鍚戠伅 Bit1:鍙宠浆鍚戠伅 0
						// 1 2
					}
					break;
				case 0x02: // car sudu > =10
					byte stallDStop[] = { MsgType.TRANSFER_STALLD_913_EXIT };
					setModuleMsgTo913(stallDStop);
					break;
				case 0x03:
					int CurCarPage = paramer[1];

					Log.d("DDD", "---------CurCarPage----" + CurCarPage);
					if (BaseModule.getBaseModule().CurrentCarPage() == CurCarPage)
						break;

					BaseModule.getBaseModule().GetNotifyCurrentCarPage(
							CurCarPage);

					switch (CurCarPage) {

					case 0x00:
						break; // 初始页面
					case 0x01:
						break; // origin car page
					case 0x02: // flyaudio page
						if (needHalfScreen)
							BaseModule.getBaseModule().OrginBackToFLyaudio();
						break;
					case 0x03:
						break; // phone call
					case 0x04:
						break; // phone hand
					case 0x05: // quanjing 360
						break;
					}
					break;

				case 0x05:

					break;
				case 0x06:
					break;
				case 0x07:
					BaseModule.getBaseModule().RadarGetFromLpc(paramer);
					
					break;
				case 0x08:
					int mCmd = paramer[1];
					if(mCmd<0)
						mCmd+=256;
					int msgV[] = new int[2];
					msgV[0] = paramer[2];
					msgV[1] = paramer[3];
					Log.d("DDD", " msgV[0] " + msgV[0] + "  " + msgV[1]+"  mCmd "+mCmd+"  "+paramer[1]);
					Bundle voiceBD = new Bundle();
					voiceBD.putIntArray(BackCarTag.INTKEY, msgV);
					switch (mCmd) {
					case -112:
					case 0x90: // A4 or A6 Front A4 1 4 9 低中高 paramer[2]
								// //tonelevel paramer[3] soundlevel
						mBackCarModule.SendCommonMsgToBaseModule(
								BackCarTag.F_VOICE_LEVEL, voiceBD);
						break;
					case -111:
					case 0x91: // A4 or A6 Rear
						mBackCarModule.SendCommonMsgToBaseModule(
								BackCarTag.R_VOICE_LEVEL, voiceBD);
						break;

					}
					break;
				case 0x09:
					Log.d("DDDB", " 0x09 shousha " + paramer[1]);
					BaseModule.getBaseModule().setParkBrake(paramer[1]);
					break;
				case 0x0a:
					// AUDI Q3雷达色彩
					// 注： 0x0E 为隐藏 0x00 为显示透明图 0x01显示白色
					// 0x03 显示红色
//					if(BaseModule.getBaseModule().DEBUG)
						Log.d("DDDB", " 0x0a  edgeRadar " );
					BaseModule.getBaseModule().EdgeRadarDealWithData(paramer);
					break;
				}
			}
		}
	};
}
