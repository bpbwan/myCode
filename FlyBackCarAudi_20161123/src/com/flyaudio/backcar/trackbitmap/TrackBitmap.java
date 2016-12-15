package com.flyaudio.backcar.trackbitmap;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Paint.Align;
import android.graphics.Bitmap.Config;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.util.Log;
import android.os.Handler;
import android.os.SystemClock;

import java.lang.Math;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;

import com.flyaudio.backcar.BackCarService;
import com.flyaudio.backcar.PluginProxyContext;
import com.flyaudio.backcar.modules.BaseModule;
import com.flyaudio.backcar.trackbitmap.TrackBitmapQueue.BitmapLock;
import com.flyaudio.backcar.util.FlyUtil;

public class TrackBitmap {

	final static int QUEUESIZE = 5;
	private String TAG = "BackCarTrack";

	static double SaveTmpAngle = 90;
	public int mScreenW = 1024;
	public int mScreenH = 600;
	public int trailmode = 1;
	private int clip = 20;
	private int offset = -30;
	private int offsetY = 108;
	private int clipWigth = 0;
	public boolean draw_pline = true;
	boolean NeedRefresh = false;

	private Paint RLpointPt = null; // �����켣 ����
	private Paint LinePaint = null;
	private Paint BitmapPaint = null;

	RectF oval = null;
	private Bitmap bottomArc = null;
	private Bitmap MidVerticabmp = null;
	private Bitmap TopVerticabmp = null;
	Bitmap trackBitmap[] = new Bitmap[5];

	PluginProxyContext mPluginProxyContext = null;
	TrackBitmapQueue mBitmapQueue;
	BackCarTrack mTrack;
	Queue<Double> queue = new LinkedList<Double>();
	private float mbuf[] = null;// new float[80];

	private double mtmp = 90;
	private long timp2 = 0;
	private long timp1 = 0;

	private int arcHeight = 300; // 214 300 840 400
	private int gyRange = 13;

	private int clip150_f = 20;
	private int offset150_f = 0;
	private int offsetY150_f = 110;
	private int clipWigth150_f = 20;
	private int gyRange150_f = 13;

	private int clip150_r = 4;
	private int offset150_r = 29;
	private int offsetY150_r = 102;
	private int clipWigth150_r = 54;
	private int gyRange150_r = 13;
	int trackMode = 0;

	float paintW = 10;
	private float gy1 = 0;
	private float gy2 = 0;

	final double maxAngle = 140.0;
	final double minAngle = 40.0;

	private int trackW = -1;
	private int trackH = -1;

	static TrackBitmapCallbacks mCallback;
	private byte[] lock = new byte[0];
	private int oldTrackMode = -1; 
	public interface TrackBitmapCallbacks {
		public void reRender();
	}

	public TrackBitmap() {
		init();

	}

	
	private void init() {

		RLpointPt = new Paint();
		RLpointPt.setColor(Color.YELLOW);

		LinePaint = new Paint();
		LinePaint.setColor(Color.YELLOW);
		LinePaint.setStyle(Paint.Style.STROKE);
		LinePaint.setAntiAlias(true);
		LinePaint.setStrokeWidth(5);

		BitmapPaint = new Paint();
		BitmapPaint.setColor(Color.RED);
		BitmapPaint.setStyle(Paint.Style.STROKE);
		BitmapPaint.setAntiAlias(true);
		BitmapPaint.setStrokeWidth(5);

		MidVerticabmp = Bitmap.createBitmap(4, 10, Config.ARGB_8888);
		if (MidVerticabmp != null)
			fillRectBmp(4, 10, MidVerticabmp, 0xFFFFFF00);

		mTrack = BackCarTrack.getInstance();
		final int w = FlyUtil.GET_SCREEN_W();
		final int h = FlyUtil.GET_SCREEN_H();
		for (int i = 0; i < 5; i++)
			trackBitmap[i] = Bitmap.createBitmap(w, h, Config.ARGB_4444);
		mBitmapQueue = new TrackBitmapQueue(null);
		mBitmapQueue.fillBitmapLoopPool(trackBitmap);
	}

	public void setupCallback(TrackBitmapCallbacks callback) {
		mCallback = callback;

	}

	public void setup(PluginProxyContext m_pluginProxyContext, int w, int h) {
		setWithHeight(w, h);
		this.mPluginProxyContext = m_pluginProxyContext;
		arcHeight = mPluginProxyContext.getInteger("arcHeight", arcHeight);
		trackMode = mPluginProxyContext.getInteger("trackMode", trackMode);

		String trackColor = mPluginProxyContext.getString("trackColor", null);
		if (trackColor != null)
			RLpointPt.setColor(Color.parseColor(trackColor));
		Log.i(TAG, "trackMode  " + trackMode);
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
	}

	private double QueueRef() {
		synchronized (lock) {
			double ret = 0;
			try{
			if (queue.size() != 0)
				ret = queue.poll();
		}catch(NoSuchElementException e){
			
		}
			return ret;
		}
	}

	private void QueueClear(){
		if(queue.size() > 0){
			try{
		for(int i = 0;i < queue.size();i++){
			queue.poll();
			}
			}catch(Exception e){
				
			}
		}
	}
	
	private void OfferQueue(double data) {
		// Log.i(TAG, "OfferQueue data   "+data);
		if (queue.size() >= QUEUESIZE)
			QueueRef();
		queue.offer(data);
	}

	private boolean isQuequeNull() {
		if (queue.size() != 0)
			return false;
		else
			return true;
	}

	public void setWithHeight(int w, int h) {
		mScreenW = w;
		mScreenH = h;
		bottomArc = Bitmap.createBitmap(mScreenW, arcHeight, Config.ARGB_8888);
		oval = new RectF();
		oval.set(0, 0, bottomArc.getWidth(), bottomArc.getHeight() - 40);

	}

	public void setTrackWH(int trackw, int trackh) {
		trackW = trackw;
		trackH = trackh;
		Log.d("DDD", "setTrackWH " + trackW + " " + trackH);
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
		}
		
		requestSyncAngle();
		NeedRefresh = true;
	}

	public void requestSyncAngle(){
		OfferQueue(SaveTmpAngle);
	}
	
	public void stopDrawing() {
		draw_pline = false;
	}

	public void startDrawing() {
		draw_pline = true;

	}

	public void needChangeParamJni(int w, int h) {
		trackW = w;
		trackH = h;
		NeedRefresh = true;
		OfferQueue(mtmp);
	}

	public void DoChangeParamToJni(int w, int h) {

		if (w < 0 || h < 0)
			return;
		// Log.d("DDD", "FLyTrack "+w+" "+h);
		float[] msgToTrack = new float[3];
		msgToTrack[0] = mTrack.TrackCarType();
		msgToTrack[1] = w;
		msgToTrack[2] = h;
		mTrack.sendParam((byte) 0x15, 3, msgToTrack);
		NeedRefresh = false;
	}

	public static int sync_sleeptime = 10;

	public static void notifyComsumeSync(final int left_size) {
		switch (left_size) {
		case 0:
			sync_sleeptime = 0;
			break;
		case 1:
			sync_sleeptime = 5;
			break;
		case 2:
			sync_sleeptime = 10;
			break;
		case 3:
			sync_sleeptime = 15;
			break;
		case 4:
			sync_sleeptime = 30;
			break;
		}
		if(BaseModule.getBaseModule().DEBUG_L2)
				Log.d("DDD", "notifyComsumeSync " + left_size);
	}

	public class DealAngle implements Runnable {
		private boolean consumeStop = true;

		public void stop() {
			consumeStop = false;
		}

		@Override
		public void run() {

			while (consumeStop) {
				final double angle = QueueRef();// mtmp

				if (!BackCarTrack.checkHasTrack())
					return;
				
				if (angle == 0) {
						int sleeptime = 10;

					/*
					 * Ŀ��Ϊ�˷�ֹ����bitmap��죬 �����ÿ���һbitmapƽ��15ms ���Ĳ�����ʱ��ƽ��10-18ms
					 */
					try {

						Thread.sleep(sleeptime);
					} catch (Exception e) {
						// Log.e(TAG, "BackCar Exception e");
						e.printStackTrace();
					}

				} else if (draw_pline && angle != 0) {

					try {
						final int synctime = sync_sleeptime;
						Thread.sleep(synctime);
					} catch (Exception e) {
						// Log.e(TAG, "BackCar Exception e");
						e.printStackTrace();
					}
					mtmp = angle;
					BitmapLock bplock = mBitmapQueue.getLoopBitmap();
					bplock.setLock();

					timp1 = System.currentTimeMillis();
					Canvas canvas = new Canvas(bplock.bp);

					DoChangeParamToJni(trackW, trackH);
//					if(oldTrackMode != trailmode){
//						if(oldTrackMode != -1)
//							bitmapQueueClear();
//						
//						oldTrackMode = trailmode;
//						
//					}
						
					mTrack.requestAngle(angle, trailmode);
					mbuf = mTrack.getMBuf();

					if (mTrack.getRL_point() > 0) {
						if(BaseModule.getBaseModule().DEBUG_L2)
							Log.d("DDD", "draw_pline  "+angle);
						Paint paint = new Paint();
						paint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
						canvas.drawPaint(paint);

						canvas.drawPoints(mTrack.lbuf, 0, mTrack.maxPoint,
								RLpointPt);

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
							float m = getRealY(mbuf[4], mbuf[5], mbuf[6],
									mbuf[7], mdel);
							if (mbuf[5] < mbuf[7])
								m = -m;

							/* ���?�� */
							Canvas can = new Canvas(bottomArc);

							can.drawPaint(paint);
							/* ��������Ȳó����ߣ������º�ɫ���� */
							ClipCanRect(can, trailmode, range, mbuf[18],
									mbuf[16]);
							can.drawOval(oval, BitmapPaint);
							canvas.drawBitmap(bottomArc, offset, offsetY,
									BitmapPaint);

							/* ���������ߵ���б�Ⱥͳ��ȼ��� */
							mbuftmp = mbuf[16] - mbuf[18];
							mLengthl = (int) (mbuftmp / 6);
							mLengthr = (int) (mbuftmp / (6 + range / 50));
							mtmpr = getRgradient(mbuf[16] - offset - 30
									- mLengthr, mbuf[16] - offset - 30);
							mtmpl = getLgradient(mbuf[18] - offset + 30,
									mbuf[18] - offset + 30 + mLengthr);

							/* �������ɫ�����������������߶� Ϊ������߾���ƽ�� */
							mbuf[17] = gy1 - gyRange + offsetY; // 13
							mbuf[19] = gy2 - gyRange + offsetY;

							for (mi = 16; mi < 76; mi += 4) {
								LinePaint.setStrokeWidth(paintW);
								mbuftmp = mbuf[mi] - mbuf[mi + 2];
								if (angle > 90) {
									mLengthl = (int) (mbuftmp / 6);
									mLengthr = (int) (mbuftmp / (6 + range / 50));
								} else {
									mLengthl = (int) (mbuftmp / (6 - range / 50));
									mLengthr = (int) (mbuftmp / 6);
								}

								if (mbuf[mi + 1] > mbuf[1]) /* �ұߵ�������б�� */
								{
									Path path = new Path();
									path.moveTo(mbuf[mi] - mdel, mbuf[mi + 1]);
									path.quadTo(mbuf[mi] - mdel, mbuf[mi + 1],
											mbuf[mi] - mLengthr - mdel,
											mbuf[mi + 1] + (int) mLengthr
													* mtmpr);
									canvas.drawPath(path, LinePaint);
								}// right line
								if (mbuf[mi + 3] > mbuf[3])/* ��ߵ�������б�� */
								{
									Path path = new Path();
									path.moveTo(mbuf[mi + 2] + mdel,
											mbuf[mi + 3]);
									path.quadTo(mbuf[mi + 2] + mdel,
											mbuf[mi + 3], mbuf[mi + 2]
													+ mLengthl + mdel,
											mbuf[mi + 3] + (int) mLengthl
													* mtmpl);
									canvas.drawPath(path, LinePaint);
								}
								if (mbuf[mi + 1] >= mbuf[5]
										&& mbuf[mi + 5] < mbuf[5]) /* �м� ��ɫ�� */
								{
									Path path = new Path();
									path.moveTo(mbuf[4] - mdel, mbuf[5] - m - 2);
									path.quadTo(mbuf[4] - mdel,
											mbuf[5] - m - 2, mbuf[6] + mdel,
											mbuf[7] + m - 2);
									LinePaint.setStrokeWidth(3);
									canvas.drawPath(path, LinePaint); // the mid
																		// line
								}
								// mtmpr = (float)0.17-del*(float)0.18/16;
								// mtmpl = (float)0.17-del*(float)0.18/16;

								/* ����ÿ���ߵ���б�Ȳ�ͬ���ؼ�����б�� */
								if (angle > 95) {
									mtmpr = (float) mtmpr
											- (float) (0.18 + 0.22 * range / 50)
											/ 16;
									mtmpl = (float) mtmpl
											- (float) (0.18 - 0.2 * range / 50)
											/ 16;
								} else if (angle < 85) {
									mtmpr = (float) mtmpr
											- (float) (0.18 + 0.2 * range / 50)
											/ 16;
									mtmpl = (float) mtmpl
											- (float) (0.18 - 0.25 * range / 50)
											/ 16;
								} else {
									mtmpr = (float) mtmpr - (float) 0.18 / 16;
									mtmpl = (float) mtmpl - (float) 0.18 / 16;
								}
								mdel -= 1;
								del++;
								paintW -= 0.2;
							}
							LinePaint.setStrokeWidth(3);
							m = getRealY(mbuf[0], mbuf[1], mbuf[2], mbuf[3],
									mdel);
							if (mbuf[1] < mbuf[3])
								m = -m;

							/* ������� */
							Path path = new Path();
							path.moveTo(mbuf[0] - mdel, mbuf[1] - m - 2);
							path.quadTo(mbuf[0] - mdel, mbuf[1] - m - 2,
									mbuf[2] + mdel, mbuf[3] + m - 2);
							canvas.drawPath(path, LinePaint);// the first line

							/* ���ɫ���м�����ƫб�� */
							int bmpH = 12 + 12 * (int) Math.abs(range) / 40;
							TopVerticabmp = Bitmap.createBitmap(4, bmpH,
									Config.ARGB_8888);
							if (TopVerticabmp != null)
								fillRectBmp(4, bmpH, TopVerticabmp, 0xFFFFFF00);
							if (TopVerticabmp != null) { // first line mid point
								LinePaint.setStrokeWidth(5);
								float degrees = 60 * range / 50;
								Bitmap temBmp = rotateBitmap(TopVerticabmp,
										degrees);
								LinePaint.setStrokeWidth(2);
								canvas.drawBitmap(temBmp,
										(float) (mbuf[0] + mbuf[2]) / 2
												- (int) temBmp.getWidth() / 2,
										(float) (mbuf[1] + mbuf[3]) / 2
												- (int) temBmp.getHeight() / 2
												- 2, null);
							}

							/* �м���ɫ���м�����ƫб�� */
							if (MidVerticabmp != null) {// mid line mid point
								float degree = 20 * range / 50; // getDegrees(mbuf[0],mbuf[1],mbuf[2],mbuf[3]);
								Bitmap temBmp2 = rotateBitmap(MidVerticabmp,
										degree);
								LinePaint.setStrokeWidth(1);
								canvas.drawBitmap(temBmp2,
										(float) (mbuf[4] + mbuf[6]) / 2
												- (int) temBmp2.getWidth() / 2,
										(float) (mbuf[5] + mbuf[7]) / 2
												- (int) temBmp2.getHeight() / 2
												- 2, null);
								// temBmp2.recycle();
							}
							break;
						default:
							break;
						}

					}

					bplock.unLock();
					mBitmapQueue.OfferQueue(bplock);
					// if(mCallback != null)
					// mCallback.reRender();

					// long time = System.currentTimeMillis();
					// timp2 = time - timp1;

					// Log.d("DDD",
					// "OfferQueue  "+angle+" usetime "+timp2+" ms");
				}

			}

			Log.d(TAG, "trackbitmap thread out");
		}
	}

	public Bitmap comsumeBitmap() {
		return mBitmapQueue.QueueRef("comsumebitmap");
	}

	public void update(double angle) {
		double mAngle = angle;
		if (mAngle > maxAngle)
			mAngle = maxAngle;
		if (mAngle < minAngle)
			mAngle = minAngle;

		SaveTmpAngle = mAngle;
		OfferQueue(mAngle);

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

	private float getRealY(float x1, float y1, float x2, float y2, float m) {
		float y = y2 - y1;
		float x = x2 - x1;
		float ret = y / x * m;
		return (float) Math.abs(ret);
	}

	public Bitmap rotateBitmap(Bitmap bitmap, float degrees) {
		if (degrees == 0 || null == bitmap) {
			return bitmap;
		}
		android.graphics.Matrix matrix = new android.graphics.Matrix();
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
		}
		return mtmpl;
	}

	public void ClipCanRect(Canvas can, int mode, float range, float m18,
			float m16) {

		switch (mode) {
		case 1:
			if (range > 0)
				can.clipRect(m18 + clip, bottomArc.getHeight() / 2, m16
						- clipWigth, bottomArc.getHeight());
			else
				can.clipRect(m18 + clip, bottomArc.getHeight() / 2, m16
						- clipWigth, bottomArc.getHeight());
			break;
		case 2:
			if (range > 0)
				can.clipRect(m18 - clip, bottomArc.getHeight() / 2, m16
						- clipWigth, bottomArc.getHeight());
			else
				// can.clipRect(m18+24+clip,bp.getHeight()/2,
				// m16-24+30-8*range/50, bp.getHeight());
				can.clipRect(m18 - clip, bottomArc.getHeight() / 2, m16
						- clipWigth, bottomArc.getHeight());
			break;

		}
	}

	private float getRgradient(float x1, float x2) {
		int i = 0;
		int x = (int) x1;
		int mx = (int) x2;
		float y1 = 0, y2 = 0;
		for (i = bottomArc.getHeight() / 2; i < bottomArc.getHeight(); i++) {
			if (bottomArc.getPixel(x, i) == 0xFFFF0000)
				y1 = i;

		}
		for (i = bottomArc.getHeight() / 2; i < bottomArc.getHeight(); i++) {
			if (bottomArc.getPixel(mx, i) == 0xFFFF0000)
				y2 = i;
			gy1 = y2;
		}

		return -(y2 - y1) / (x2 - x1);
	}

	private float getLgradient(float x1, float x2) {
		int i = 0;
		int x = (int) x1;
		int mx = (int) x2;
		float y1 = 0, y2 = 0;
		for (i = bottomArc.getHeight() / 2; i < bottomArc.getHeight(); i++) {
			if (bottomArc.getPixel(x, i) != 0)
				y1 = i;
			gy2 = y1;
		}
		for (i = bottomArc.getHeight() / 2; i < bottomArc.getHeight(); i++) {
			if (bottomArc.getPixel(mx, i) != 0)
				y2 = i;
		}
		return (y2 - y1) / (x2 - x1);
	}

	static Thread mTrackThread;
	static DealAngle mDealAngle;

	public void onPause() {
		Log.i(TAG, " onPause");

		if (mDealAngle != null) {
			mDealAngle.stop();
			bitmapQueueClear();
			mDealAngle = null;
			mTrackThread = null;
		}
	}

	public void bitmapQueueClear(){
		mBitmapQueue.Clear();
	}
	
	public void ClearBitmapLeftOne(){
		//if(mBitmapQueue.size() < 2){
		//}
		mBitmapQueue.LeftOne();
		OfferQueue(mtmp);
		
		NeedRefresh = true;
		BaseModule.getBaseModule().getBackCarModule().tellmoduleRefAngle((byte) 0);
	}
	
	public void onResume() {
		Log.i(TAG, " onResume");
		if (BackCarTrack.checkHasTrack()) {
			if(mDealAngle != null){
				onPause();
			}
			mDealAngle = new DealAngle();
			mTrackThread = new Thread(mDealAngle);
			bitmapQueueClear();
			QueueClear();
			mTrackThread.start();
		}
	}
	

}
