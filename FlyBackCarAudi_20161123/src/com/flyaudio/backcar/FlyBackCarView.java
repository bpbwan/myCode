package com.flyaudio.backcar;

import android.content.Context;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import java.util.Date;
import java.lang.System;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;

import com.autochips.backcar.BackCar;
import android.widget.Button;
import android.widget.SlidingDrawer;
import android.widget.ImageView;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import android.util.AttributeSet;
import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;
import android.util.Xml;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Bitmap.Config;
import java.lang.Math;
import android.graphics.Path;
import android.graphics.RectF;

import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.util.LinkedList;
import java.util.Queue;
import com.flyaudio.data.BcData;
import com.flyaudio.backcar.trackbitmap.BackCarTrack;
import com.flyaudio.backcar.util.FlyUtil;
import com.flyaudio.backcar.modules.BaseModule;

public class FlyBackCarView extends View {

	final static int QUEUESIZE = 3;
	public String TAG = "FlyBackCarView";

	public int mScreenW = 1024;
	public int mScreenH = 600;
	public Paint Pt = null;
	public Paint mPt = null;
	public Paint mPaint = null;
	public Resources res;

	public boolean draw_pline = true;
	public boolean setMode = false;

	public double mtmp = 0;
	public double oldAngle = -1;
	public long timp2 = 0;
	public long timp1 = 0;
	public float mbuf[] = new float[80];
	public Bitmap Rm = null;
	public BcData bcdata[] = new BcData[4];
	public RectF oval = null;

	public int trailmode = 1;
	public int clip = 20;
	public int offset = -30;
	public int offsetY = 108;
	public int clipWigth = 0;

	public Bitmap bp = null;
	public PluginProxyContext mPluginProxyContext = null;

	public int trackW = -1;
	public int trackH = -1;

	public boolean needChangeParam = false;
	public Queue<Double> queue = new LinkedList<Double>();
	public BackCarTrack mBackCarTrack = null;
	private Canvas m_canvas = new Canvas();
	public int arcHeight = 300; // 214 300 840 400
	public int gyRange = 13;

	public int clip150_f = 20;
	public int offset150_f = 0;
	public int offsetY150_f = 110;
	public int clipWigth150_f = 20;
	public int gyRange150_f = 13;

	public int clip150_r = 4;
	public int offset150_r = 29;
	public int offsetY150_r = 102;
	public int clipWigth150_r = 54;
	public int gyRange150_r = 13;
	public int trackMode = 0;
	private int oldJniW = -1;
	private int oldJniH = -1;
	public void init() {

		Pt = new Paint();
		Pt.setColor(Color.YELLOW);
		mPt = new Paint();
		mPt.setColor(Color.YELLOW);
		mPt.setStyle(Paint.Style.STROKE);
		mPt.setAntiAlias(true);
		mPt.setStrokeWidth(5);
		mPaint = new Paint();
		mPaint.setColor(Color.RED);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setAntiAlias(true);
		mPaint.setStrokeWidth(5);

		Rm = Bitmap.createBitmap(4, 10, Config.ARGB_8888);
		if (Rm != null)
			fillRectBmp(4, 10, Rm, 0xFFFFFF00);
		bp = Bitmap.createBitmap(800, 300, Config.ARGB_8888);
		oval = new RectF();
		oval.set(0, 0, bp.getWidth(), bp.getHeight() - 40);
		// can = new Canvas(bp);
		// paint = new Paint();
		setWillNotDraw(false);

		mBackCarTrack = BackCarTrack.getInstance();
		new Thread(mDealAngle).start();

	}

	// Canvas can= null;
	// Paint paint =null;
	public FlyBackCarView(Context context) {
		super(context);

		init();
	}

	public FlyBackCarView(Context context, AttributeSet attrs) {
		super(context, attrs);

		init();
	}

	public FlyBackCarView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		init();
	}

	public double QueueRef() {
		double ret = 0;
		synchronized (this) {

			if (queue.size() != 0)
				ret = queue.poll();
		}
		return ret;
	}

	public void OfferQueue(double data) {
		// Log.i(TAG, "OfferQueue data   "+data);
		if (queue.size() >= QUEUESIZE)
			QueueRef();
		queue.offer(data);
	}

	public boolean isQuequeNull() {
		if (queue.size() != 0)
			return true;
		else
			return false;
	}

	public void setTrackWH(int trackw, int trackh) {
		trackW = trackw;
		trackH = trackh;
		Log.d("DDD", "setTrackWH " + trackW + " " + trackH);
	}

	public void setWithHeight(int w, int h) {
		mScreenW = w;
		mScreenH = h;
		bp = Bitmap.createBitmap(mScreenW, arcHeight, Config.ARGB_8888);
		oval = new RectF();
		oval.set(0, 0, bp.getWidth(), bp.getHeight() - 40);

	}

	public void setup(PluginProxyContext m_pluginProxyContext) {
		this.mPluginProxyContext = m_pluginProxyContext;
		arcHeight = mPluginProxyContext.getInteger("arcHeight", arcHeight);
		trackMode = mPluginProxyContext.getInteger("trackMode", trackMode);
		String trackColor = mPluginProxyContext.getString("trackColor", null);

		if (trackColor != null)
			Pt.setColor(Color.parseColor(trackColor));

		Log.i(TAG, "trackMode  " + trackMode + " " + trackColor);
		clip150_f = mPluginProxyContext.getInteger("clip150_f", clip150_f);
		offset150_f = mPluginProxyContext
				.getInteger("offset150_f", offset150_f);
		offsetY150_f = mPluginProxyContext.getInteger("offsetY150_f",
				offsetY150_f);
		clipWigth150_f = mPluginProxyContext.getInteger("clipWigth150_f",
				clipWigth150_f);
		gyRange150_f = mPluginProxyContext.getInteger("gyRange150_f",
				gyRange150_f);

		clip150_r = mPluginProxyContext.getInteger("clip150_r", clip150_r);
		offset150_r = mPluginProxyContext
				.getInteger("offset150_r", offset150_r);
		offsetY150_r = mPluginProxyContext.getInteger("offsetY150_r",
				offsetY150_r);
		clipWigth150_r = mPluginProxyContext.getInteger("clipWigth150_r",
				clipWigth150_r);
		gyRange150_r = mPluginProxyContext.getInteger("gyRange150_r",
				gyRange150_r);

		setWithHeight(FlyUtil.GET_SCREEN_W(), FlyUtil.GET_SCREEN_H());
		int trackw = BaseModule.getBaseModule().getPreferenceInt("TrackJniW",
				-1);
		int trackh = BaseModule.getBaseModule().getPreferenceInt("TrackJniH",
				-1);
		setTrackWH(trackw, trackh);
		setResource(mPluginProxyContext.getResources(), 0);
	}

	public void setTrailMode(int mode) {
		Log.i(TAG, "setTrailMode  " + mode); // 840 clip 20 clipWigth 20 offsetY
												// 52 offset 0 gyRange 20 (150f)
												// && offsetY 60
												// 214 clip 25 clipWigth 25
												// offsetY 110 offset 0 gyRange
												// 13 (150f) && clip 4 clipWigth
												// 54 offsetY 102 offset 29
												// (150r)
		trailmode = mode;
		if (trailmode == 1) {
			clip = clip150_f;
			offset = offset150_f;
			offsetY = offsetY150_f;
			clipWigth = clipWigth150_f;
			gyRange = gyRange150_f;
		} // 840
		else if (trailmode == 2) {
			clip = clip150_r;
			offset = offset150_r;
			offsetY = offsetY150_r;
			clipWigth = clipWigth150_r;
			gyRange = gyRange150_r;
		} else if (trailmode == 3) {
			clip = clip150_r;
			offset = offset150_r;
			offsetY = offsetY150_r;
			clipWigth = clipWigth150_r;
			gyRange = gyRange150_r;
		}
	}

	public void setResource(Resources ress, int id) {
		res = ress;
	}

	public void SendParam(byte cmd, int len, float[] data) {
		mBackCarTrack.sendParam(cmd, len, data);
	}

	public void sendParam(byte cmd, int len, float data) {
		float[] sendMsg = new float[2];
		sendMsg[0] = data;
		SendParam(cmd, len, sendMsg);
		if (cmd == 0x14)
			setMode = !setMode;
		Log.i(TAG, "sendParam command :" + cmd);
		invalidate();
	}

	public void setDraw(boolean isdraw) {
		draw_pline = isdraw;

		Log.i(TAG, " FlyBackCarView setdraw  " + isdraw);
		invalidate();
	}

	public boolean isSetMode() {
		return setMode;
	}

	public boolean consumeStop = false;

	public void update(int dist, int angle) {

		update((double) angle);
	}

	
	public class DealAngle implements Runnable {
		private boolean consumeStop = true;

		public void stop() {
			consumeStop = false;
		}

		@Override
		public void run() {
			while(true){
			if(m_canvas!=null)			
				onDraw(m_canvas);
			else {
				Log.d("TTT", "m_canvas null");
			}
			try {
						Thread.sleep(10);
					} catch (Exception e) {
						// Log.e(TAG, "BackCar Exception e");
						e.printStackTrace();
					}
			}
		}
	}

	static DealAngle mDealAngle;


	public void update(double angle) {

		double mAngle = angle;
		if (mAngle > 140)
			mAngle = 140;
		if (mAngle < 40)
			mAngle = 40;
		mtmp = mAngle;
		
		if(oldAngle != mAngle){
			OfferQueue(mAngle);
			oldAngle = mAngle;
			//if(m_canvas!=null)			
			//onDraw(m_canvas);
			//else 
			//if(m_canvas == null)
			invalidate();
			if (!consumeStop && draw_pline) {
			// Log.d("DDDB", "consumeStop ");
			consumeStop = true;
			//invalidate();
			}
		}
		
	}

	public void fillRectBmp(int w, int h, Bitmap bmp, int color) {
		int x, y;
		for (x = 0; x < w; x++)
			for (y = 0; y < h; y++)
				bmp.setPixel(x, y, color);
	}

	public void crealRectBmp(int w, int h, int w1, int h1, Bitmap bmp, int color) {
		int x, y;
		for (x = w; x < w1; x++)
			for (y = h; y < h1; y++)
				bmp.setPixel(x, y, color);
	}

	public float getRealY(float x1, float y1, float x2, float y2, float m) {
		float y = y2 - y1;
		float x = x2 - x1;
		float ret = y / x * m;
		return (float) Math.abs(ret);
	}

	public Bitmap rotateBitmap(Bitmap bitmap, float degrees) {
		if (degrees == 0 || null == bitmap) {
			return bitmap;
		}
		Matrix matrix = new Matrix();
		matrix.postRotate(degrees);
		Bitmap bmpf = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), matrix, false);
		return bmpf;
	}

	public float getRmtmp(int mode, float range) {
		float mtmpr = 0;
		switch (mode) {
		case 1:
			if (range > 0)
				mtmpr = (float) 0.20 + range * (float) 0.001;// 0.5*getMathAngle(mbuf[16]
																// ,mbuf[17],
																// mbuf[20],
																// mbuf[21]);
			else
				mtmpr = (float) 0.20 - range * (float) 0.0004;
			break;
		case 2:
			if (range > 0)
				mtmpr = (float) 0.20 + range * (float) 0.0002;
			else
				mtmpr = (float) 0.20 - range * (float) 0.0001;
			break;
		case 3:
			if (range > 0)
				mtmpr = (float) 0.20 + range * (float) 0.0002;
			else
				mtmpr = (float) 0.20 - range * (float) 0.0001;
			break;
		}
		return mtmpr;
	}

	public float getLmtmp(int mode, float range) {
		float mtmpl = 0;
		switch (mode) {
		case 1:
			if (range > 0)
				mtmpl = (float) 0.20 - range * (float) 0.0002;// 0.5*getMathAngle(mbuf[18]
																// ,mbuf[19],
																// mbuf[22],
																// mbuf[23]);
			else
				mtmpl = (float) 0.20 - range * (float) 0.001;
			break;

		case 2:
			if (range > 0)
				mtmpl = (float) 0.20 - range * (float) 0.0002;
			else
				mtmpl = (float) 0.20 - range * (float) 0.0002;
			break;
		case 3:
			if (range > 0)
				mtmpl = (float) 0.20 - range * (float) 0.0002;
			else
				mtmpl = (float) 0.20 - range * (float) 0.0002;
			break;
		}
		return mtmpl;
	}

	public void ClipCanRect(Canvas can, int mode, float range, float m18,
			float m16) {

		switch (mode) {
		case 1:
			if (range > 0)
				can.clipRect(m18 + clip, bp.getHeight() / 2, m16 - clipWigth,
						bp.getHeight());
			else
				can.clipRect(m18 + clip, bp.getHeight() / 2, m16 - clipWigth,
						bp.getHeight());
			break;
		case 2:
			if (range > 0)
				can.clipRect(m18 - clip, bp.getHeight() / 2, m16 - clipWigth,
						bp.getHeight());
			else
				// can.clipRect(m18+24+clip,bp.getHeight()/2,
				// m16-24+30-8*range/50, bp.getHeight());
				can.clipRect(m18 - clip, bp.getHeight() / 2, m16 - clipWigth,
						bp.getHeight());
			break;
		case 3:
			if (range > 0)
				can.clipRect(m18 - clip, bp.getHeight() / 2, m16 - clipWigth,
						bp.getHeight());
			else
				// can.clipRect(m18+24+clip,bp.getHeight()/2,
				// m16-24+30-8*range/50, bp.getHeight());
				can.clipRect(m18 - clip, bp.getHeight() / 2, m16 - clipWigth,
						bp.getHeight());
			break;
		}
	}

	public float getRgradient(float x1, float x2) {
		int i = 0;
		int x = (int) x1;
		int mx = (int) x2;
		float y1 = 0, y2 = 0;
		for (i = bp.getHeight() / 2; i < bp.getHeight(); i++) {
			if (bp.getPixel(x, i) == 0xFFFF0000)
				y1 = i;

		}
		for (i = bp.getHeight() / 2; i < bp.getHeight(); i++) {
			if (bp.getPixel(mx, i) == 0xFFFF0000)
				y2 = i;
			gy1 = y2;
		}

		return -(y2 - y1) / (x2 - x1);
	}

	public float getLgradient(float x1, float x2) {
		int i = 0;
		int x = (int) x1;
		int mx = (int) x2;
		float y1 = 0, y2 = 0;
		for (i = bp.getHeight() / 2; i < bp.getHeight(); i++) {
			if (bp.getPixel(x, i) != 0)
				y1 = i;
			gy2 = y1;
		}
		for (i = bp.getHeight() / 2; i < bp.getHeight(); i++) {
			if (bp.getPixel(mx, i) != 0)
				y2 = i;
		}
		return (y2 - y1) / (x2 - x1);
	}

	public void needChangeParamJni(int w, int h) {
		trackW = w;
		trackH = h;

		needChangeParam = true;
		Log.d("TTT", "needChangeParamJni");
		invalidate();

		// BackCarService.mBackCarService.getBackCarModule().tellmoduleRefAngle((byte)0);
	}

	public void ChangeParamToJni_(int w, int h) {

		if (trackW < 0 || trackH < 0)
			return;
		if(oldJniW != w || oldJniH != h ){
		// Log.d("DDD", "FLyTrack "+w+" "+h);
		float[] msgToTrack = new float[3];
		msgToTrack[0] = BackCarService.mBackCarService.cartype;
		msgToTrack[1] = w;
		msgToTrack[2] = h;
		oldJniW = w;
		oldJniH = h;
		SendParam((byte) 0x15, 3, msgToTrack);

		// if(needChangeParam)
		}
		needChangeParam = false;

	}

	public Bitmap bmp = null;
	public float paintW = 10;
	public float gy1 = 0;
	public float gy2 = 0;

	@Override
	public void draw(Canvas canvas) {
		Log.d("TTT",  "draw 0");
		m_canvas = canvas;
		//onDraw(canvas);
		Log.d("TTT",  "draw 1");
	}

	@Override
	public void onDraw(Canvas canvas) {

		double angle = QueueRef();// mtmp
		Log.d("TTT",  "onDraw 0");
		if (angle != 0)
			mtmp = angle;
		else
			angle = mtmp;
		if (!draw_pline || !BackCarTrack.checkHasTrack() || angle == 0)
			return;
		Log.d("TTT",  "onDraw 1");
		synchronized (this) {
		
			// mBackCarTrack.setTrailmode(trailmode);

			ChangeParamToJni_(trackW, trackH);
			Log.d("TTT",  "onDraw 2");
			mBackCarTrack.requestAngle(angle, trailmode);
			mbuf = mBackCarTrack.getMBuf();
			Log.d("TTT",  "onDraw 3");
			if (mBackCarTrack.getRL_point() > 0) {
				canvas.drawPoints(mBackCarTrack.lbuf, 0,
						mBackCarTrack.maxPoint, Pt);
				// canvas.drawPoints(lbuf, 0, maxPoint, Pt);
				// Log.d("DDDB", "ondraw ");
				Log.d("TTT",  "onDraw 4");
				switch (trackMode) {
				case 100: // trackmode benz use
					int mdel = 30;
					int mi = 16;
					int del = 0;
					float mLengthl,
					mLengthr,
					mtmpr,
					mtmpl,
					mbuftmp;
					paintW = 5;
					float range = (float) angle - 90;
					float m = getRealY(mbuf[4], mbuf[5], mbuf[6], mbuf[7], mdel);
					if (mbuf[5] < mbuf[7])
						m = -m;

					/* 清理画布 */
					Canvas can = new Canvas(bp);
					Paint paint = new Paint();
					paint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
					can.drawPaint(paint);
					/* 根据俩点宽度裁出弧线，画底下红色弧线 */
					ClipCanRect(can, trailmode, range, mbuf[18], mbuf[16]);
					can.drawOval(oval, mPaint);
					canvas.drawBitmap(bp, offset, offsetY, mPaint);

					/* 左右俩边线的倾斜度和长度计算 */
					mbuftmp = mbuf[16] - mbuf[18];
					mLengthl = (int) (mbuftmp / 6);
					mLengthr = (int) (mbuftmp / (6 + range / 50));
					mtmpr = getRgradient(mbuf[16] - offset - 30 - mLengthr,
							mbuf[16] - offset - 30);
					mtmpl = getLgradient(mbuf[18] - offset + 30, mbuf[18]
							- offset + 30 + mLengthr);

					/* 最底下绿色左右俩条线另外计算高度 为了贴近弧线尽量平衡 */
					mbuf[17] = gy1 - gyRange + offsetY; // 13
					mbuf[19] = gy2 - gyRange + offsetY;

					for (mi = 16; mi < 76; mi += 4) {
						mPt.setStrokeWidth(paintW);
						mbuftmp = mbuf[mi] - mbuf[mi + 2];
						if (angle > 90) {
							mLengthl = (int) (mbuftmp / 6);
							mLengthr = (int) (mbuftmp / (6 + range / 50));
						} else {
							mLengthl = (int) (mbuftmp / (6 - range / 50));
							mLengthr = (int) (mbuftmp / 6);
						}

						if (mbuf[mi + 1] > mbuf[1]) /* 右边的所有倾斜线 */
						{
							Path path = new Path();
							path.moveTo(mbuf[mi] - mdel, mbuf[mi + 1]);
							path.quadTo(mbuf[mi] - mdel, mbuf[mi + 1], mbuf[mi]
									- mLengthr - mdel, mbuf[mi + 1]
									+ (int) mLengthr * mtmpr);
							canvas.drawPath(path, mPt);
						}// right line
						if (mbuf[mi + 3] > mbuf[3])/* 左边的所有倾斜线 */
						{
							Path path = new Path();
							path.moveTo(mbuf[mi + 2] + mdel, mbuf[mi + 3]);
							path.quadTo(mbuf[mi + 2] + mdel, mbuf[mi + 3],
									mbuf[mi + 2] + mLengthl + mdel,
									mbuf[mi + 3] + (int) mLengthl * mtmpl);
							canvas.drawPath(path, mPt);
						}
						if (mbuf[mi + 1] >= mbuf[5] && mbuf[mi + 5] < mbuf[5]) /*
																				 * 中间
																				 * 绿色线
																				 */
						{
							Path path = new Path();
							path.moveTo(mbuf[4] - mdel, mbuf[5] - m - 2);
							path.quadTo(mbuf[4] - mdel, mbuf[5] - m - 2,
									mbuf[6] + mdel, mbuf[7] + m - 2);
							mPt.setStrokeWidth(3);
							canvas.drawPath(path, mPt); // the mid line
						} // 閿熸枻鎷� 閿熸枻鎷�
						// mtmpr = (float)0.17-del*(float)0.18/16;
						// mtmpl = (float)0.17-del*(float)0.18/16;

						/* 由于每条线的倾斜度不同，重计算倾斜度 */
						if (angle > 95) {
							mtmpr = (float) mtmpr
									- (float) (0.18 + 0.22 * range / 50) / 16;
							mtmpl = (float) mtmpl
									- (float) (0.18 - 0.2 * range / 50) / 16;
						} else if (angle < 85) {
							mtmpr = (float) mtmpr
									- (float) (0.18 + 0.2 * range / 50) / 16;
							mtmpl = (float) mtmpl
									- (float) (0.18 - 0.25 * range / 50) / 16;
						} else {
							mtmpr = (float) mtmpr - (float) 0.18 / 16;
							mtmpl = (float) mtmpl - (float) 0.18 / 16;
						}
						mdel -= 1;
						del++;
						paintW -= 0.2;
					}
					mPt.setStrokeWidth(3);
					m = getRealY(mbuf[0], mbuf[1], mbuf[2], mbuf[3], mdel);
					if (mbuf[1] < mbuf[3])
						m = -m;

					/* 最顶的绿线 */
					Path path = new Path();
					path.moveTo(mbuf[0] - mdel, mbuf[1] - m - 2);
					path.quadTo(mbuf[0] - mdel, mbuf[1] - m - 2,
							mbuf[2] + mdel, mbuf[3] + m - 2);
					canvas.drawPath(path, mPt);// the first line

					/* 最顶绿色线中间纵向偏斜线 */
					int bmpH = 12 + 12 * (int) Math.abs(range) / 40;
					bmp = Bitmap.createBitmap(4, bmpH, Config.ARGB_8888);
					if (bmp != null)
						fillRectBmp(4, bmpH, bmp, 0xFFFFFF00);
					if (bmp != null) { // first line mid point
						mPt.setStrokeWidth(5);
						float degrees = 60 * range / 50;
						Bitmap temBmp = rotateBitmap(bmp, degrees);
						mPt.setStrokeWidth(2);
						canvas.drawBitmap(
								temBmp,
								(float) (mbuf[0] + mbuf[2]) / 2
										- (int) temBmp.getWidth() / 2,
								(float) (mbuf[1] + mbuf[3]) / 2
										- (int) temBmp.getHeight() / 2 - 2,
								null);
					}

					/* 中间绿色线中间纵向偏斜线 */
					if (Rm != null) {// mid line mid point
						float degree = 20 * range / 50; // getDegrees(mbuf[0],mbuf[1],mbuf[2],mbuf[3]);
						Bitmap temBmp2 = rotateBitmap(Rm, degree);
						mPt.setStrokeWidth(1);
						canvas.drawBitmap(
								temBmp2,
								(float) (mbuf[4] + mbuf[6]) / 2
										- (int) temBmp2.getWidth() / 2,
								(float) (mbuf[5] + mbuf[7]) / 2
										- (int) temBmp2.getHeight() / 2 - 2,
								null);
						// temBmp2.recycle();
					}
					break;
				default:
					break;
				}

				// timp1 = System.currentTimeMillis();

			}
			Log.d("TTT",  "onDraw 6");
			if (isQuequeNull()){
				Log.d("TTT",  "onDraw 7");
				//invalidate();
				//onDraw(canvas);
				Log.d("TTT",  "onDraw 8");
			}else
				consumeStop = false;

		}
		Log.d("TTT",  "onDraw 9");
	}

}
