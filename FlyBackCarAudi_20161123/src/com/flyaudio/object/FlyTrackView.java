package com.flyaudio.object;

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
import com.flyaudio.backcar.modules.BaseModule;
import com.flyaudio.backcar.trackbitmap.BackCarTrack;
import com.flyaudio.backcar.BackCarService;
import com.flyaudio.backcar.FlyBackCarView;
import com.flyaudio.backcar.R;

import android.os.SystemProperties;
public class FlyTrackView extends FlyBackCarView {

	private String TAG = "FlyTrackView";

	public int pbuf[] = new int[16];

	public View mbackmainview = null;
	// public FlyBackCarMainView flymainview = null;
	private LinearLayout lid;
	private SeekBar sb;// add end

	private Button btn19;
	private Button btn20;
	private Button btn1;
	private Button btn2;
	public PoiF poiU = new PoiF();
	public PoiF poiDL = new PoiF();
	public PoiF poiDR = new PoiF();
	public PoiF poiL = new PoiF();
	public PoiF poiR = new PoiF();
	public PoiF poi1 = new PoiF();
	public PoiF poi2 = new PoiF();
	public PoiF poi3 = new PoiF();
	public PoiF poi4 = new PoiF();

	public int lwide = 0;
	private int testAngle = 92;
	private Paint mUIPaint = null;
	private Paint Pnt = null;
	private Paint ptt = null;

	public void init() {
		super.init();

		mUIPaint = new Paint();
		Pnt = new Paint();
		ptt = new Paint();

		mUIPaint.setColor(Color.GREEN);
		Pnt.setColor(Color.BLUE);
		Pnt.setStyle(Paint.Style.STROKE);
		ptt.setColor(Color.GRAY);
		ptt.setStyle(Paint.Style.STROKE);
	}

	// Canvas can= null;
	// Paint paint =null;
	public FlyTrackView(Context context) {
		super(context);

		init();
	}

	public FlyTrackView(Context context, AttributeSet attrs) {
		super(context, attrs);

		init();
	}

	public FlyTrackView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		init();
	}

	private void sendBcMsg(int m) {
		// Intent mintent = new Intent();
		// mintent.setAction("autochips.intent.action.CTRLDATA");
		// flymainview.getService().getApplicationContext().sendBroadcast(mintent);
	}

	public void afterInit() {
		btn19 = (Button) mbackmainview.findViewById(R.id.btn19);
		btn20 = (Button) mbackmainview.findViewById(R.id.btn20);
		btn1 = (Button) mbackmainview.findViewById(R.id.btn1);
		btn2 = (Button) mbackmainview.findViewById(R.id.btn2);

		Log.d(TAG, " afterInit");

		sb = (SeekBar) mbackmainview.findViewById(R.id.seekBar1);

		lid = (LinearLayout) mbackmainview.findViewById(R.id.addid);

		btn19.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				sendParam((byte) 0x013, 2, 0);
				setTrailMode(2);
				setMode = false;

				sb.setProgress(mBackCarTrack.Lcar);
				sb.setVisibility(View.GONE);
			}
		});
		btn20.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				sendParam((byte) 0x014, 2, 0);
				sendBcMsg(0);

				if (!isSetMode()) {
					btn1.setVisibility(View.GONE);
					btn2.setVisibility(View.GONE);
					sb.setVisibility(View.VISIBLE);
				} else {
					btn1.setVisibility(View.VISIBLE);
					btn2.setVisibility(View.VISIBLE);
					sendParam((byte) 0x19, 2, 0);
					sb.setVisibility(View.GONE);
				}
			}
		});
		btn1.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				update(0, --testAngle);
				if (testAngle < 40)
					testAngle = 40;
				if (testAngle > 140)
					testAngle = 140;
			}
		});
		btn2.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				update(0, ++testAngle);
				if (testAngle < 40)
					testAngle = 40;
				if (testAngle > 140)
					testAngle = 140;
			}
		});
		lid.setOnTouchListener(new PicOnTouchListener());

		sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) { // TODO Auto-generated method stub
				// Log.i(TAG, "get seekbar = "+seekBar.getProgress());
				sendParam((byte) 0x06, 2, seekBar.getProgress());
			}
		});

		sendParam((byte) 0x19, 2, 0);

	}

	public void setDraw(boolean isdraw) {
		draw_pline = isdraw;

		Log.i(TAG, " FlyTrackView setdraw  " + isdraw);
		invalidate();
	}

	public boolean isSetMode() {
		return setMode;
	}

	@Override
	public void draw(Canvas canvas) {
		onDraw(canvas);

	}

	@Override
	public void onDraw(Canvas canvas) {

		double angle = QueueRef();// mtmp

		if (angle != 0)
			mtmp = angle;
		else
			angle = mtmp;
		if (!draw_pline || !BackCarTrack.checkHasTrack() || angle == 0)
			return;

		synchronized (this) {

			// mBackCarTrack.setTrailMode(trailmode);
			if(BaseModule.mBackCarService.getBackCarMode() == BackCarService.BACKCAR_CVB)
				trailmode = 3;
			else 	if(BaseModule.mBackCarService.getBackCarMode() == BackCarService.BACKCAR_914){
				if(BaseModule.mBackCarService.getBackCarModule().isShowing913RearVideo())
					trailmode = 2;
				else trailmode = 1;
			}
			
			ChangeParamToJni_(trackW, trackH);
			Log.d("DDD", "angle "+angle+" "+trailmode);
			mBackCarTrack.requestAngle(angle, trailmode);
			mBackCarTrack.Get_Map(0, 0);

			mbuf = mBackCarTrack.getMBuf();
			pbuf = mBackCarTrack.getPBuf();
		}

		if (mBackCarTrack.maxDot > 0) {
			String ss = SystemProperties.get("tmp.backcar.draw"); 
			if(ss.equals("1"))
					canvas.drawPoints(mBackCarTrack.fbuf, 0, mBackCarTrack.maxDot, mUIPaint);
			
			if (setMode) {
				lwide = (pbuf[6] - pbuf[4]) / 4;
				poiDR.x = pbuf[0] + 1;
				poiDR.y = pbuf[1] - 35;
				poiDR.x1 = pbuf[0] + lwide;
				poiDR.y1 = pbuf[1] + 35;
				poiDL.x = pbuf[0] - lwide;
				poiDL.y = pbuf[1] - 35;
				poiDL.x1 = pbuf[0];
				poiDL.y1 = pbuf[1] + 35;
				poiU.x = pbuf[2] - lwide / 5;
				poiU.y = pbuf[3] - 35;
				poiU.x1 = pbuf[2] + lwide / 5;
				poiU.y1 = pbuf[3] + 35;
				poiL.x = pbuf[4] - 40;
				poiL.y = pbuf[5] - 40;
				poiL.x1 = pbuf[4] + 40;
				poiL.y1 = pbuf[5] + 40;
				poiR.x = pbuf[6] - 40;
				poiR.y = pbuf[7] - 40;
				poiR.x1 = pbuf[6] + 40;
				poiR.y1 = pbuf[7] + 40;
				poi1.x = pbuf[8] - 30;
				poi1.y = pbuf[9] - 30;
				poi1.x1 = pbuf[8] + 30;
				poi1.y1 = pbuf[9] + 30;
				poi2.x = pbuf[10] - 30;
				poi2.y = pbuf[11] - 30;
				poi2.x1 = pbuf[10] + 30;
				poi2.y1 = pbuf[11] + 30;
				poi3.x = pbuf[12] - 30;
				poi3.y = pbuf[13] - 30;
				poi3.x1 = pbuf[12] + 30;
				poi3.y1 = pbuf[13] + 30;
				poi4.x = pbuf[14] - 30;
				poi4.y = pbuf[15] - 30;
				poi4.x1 = pbuf[14] + 30;
				poi4.y1 = pbuf[15] + 30;
				canvas.drawRect(poiDR.x, poiDR.y, poiDR.x1, poiDR.y1, Pnt);
				canvas.drawRect(poiDL.x, poiDL.y, poiDL.x1, poiDL.y1, ptt);
				canvas.drawRect(poiU.x, poiU.y, poiU.x1, poiU.y1, Pnt);
				canvas.drawRect(poiL.x, poiL.y, poiL.x1, poiL.y1, Pnt);
				canvas.drawRect(poiR.x, poiR.y, poiR.x1, poiR.y1, Pnt);
				canvas.drawRect(poi1.x, poi1.y, poi1.x1, poi1.y1, ptt);
				canvas.drawRect(poi2.x, poi2.y, poi2.x1, poi2.y1, ptt);
				canvas.drawRect(poi3.x, poi3.y, poi3.x1, poi3.y1, ptt);
				canvas.drawRect(poi4.x, poi4.y, poi4.x1, poi4.y1, ptt);
			}
		}

		if (mBackCarTrack.getRL_point() > 0) {
			canvas.drawPoints(mBackCarTrack.lbuf, 0, mBackCarTrack.maxPoint, Pt);
			// canvas.drawPoints(lbuf, 0, maxPoint, Pt);
			// Log.d("DDDB", "ondraw ");
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
				mtmpl = getLgradient(mbuf[18] - offset + 30, mbuf[18] - offset
						+ 30 + mLengthr);

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
								mbuf[mi + 2] + mLengthl + mdel, mbuf[mi + 3]
										+ (int) mLengthl * mtmpl);
						canvas.drawPath(path, mPt);
					}
					if (mbuf[mi + 1] >= mbuf[5] && mbuf[mi + 5] < mbuf[5]) /*
																			 * 中间
																			 * 绿色线
																			 */
					{
						Path path = new Path();
						path.moveTo(mbuf[4] - mdel, mbuf[5] - m - 2);
						path.quadTo(mbuf[4] - mdel, mbuf[5] - m - 2, mbuf[6]
								+ mdel, mbuf[7] + m - 2);
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
				path.quadTo(mbuf[0] - mdel, mbuf[1] - m - 2, mbuf[2] + mdel,
						mbuf[3] + m - 2);
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
									- (int) temBmp.getHeight() / 2 - 2, null);
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
									- (int) temBmp2.getHeight() / 2 - 2, null);
					// temBmp2.recycle();
				}
				break;
			default:
				break;
			}

			// timp1 = System.currentTimeMillis();

		}
		if (isQuequeNull())
			invalidate();
		else
			consumeStop = false;

	}

	private class PicOnTouchListener implements OnTouchListener {

		public static final int NONE = 0;// \C6\D5通\C7\E9\BF\F6
		public static final int DRAG = 1;
		public static final int SCALE = 2;// \CB\F5\B7\C5\C7\E9\BF\F6
											// \B6\E0\B5愦C3\FE
											// \D0\FD转\C7\E9\BF\F6
		public static final int BOMO = 3;
		public static final int UOMO = 4;
		public static final int WCLM = 5;
		public static final int WCRM = 6;
		public static final int WCSM = 7;
		public static final int AT1M = 8;
		public static final int AT2M = 9;
		public static final int AT3M = 10;
		public static final int AT4M = 11;
		public static final int AtSafe = 12;
		public int rlflag = 0;
		PointF statrPoint1; // \CA\D6指\B0\B4\CF\C2时\C6\F0始\B5\E3\D7\F8\B1\EA
		PointF statrPoint2; // \C1\BD\B8\F9\CA\D6指\B0\B4\CF\C2时\C6\F0始\B5\E3\D7\F8\B1\EA
		PointF mid1;
		PointF mid2;
		float startDis = 0;
		public int state = NONE;// 状态

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			int action = event.getAction();

			action = action & 0x000000ff;
			if (!setMode)
				return true;
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				float startx = event.getX();
				float starty = event.getY();
				if (startx >= poiL.x && startx <= poiL.x1 && starty >= poiL.y
						&& starty <= poiL.y1)
					state = WCLM;
				else if (startx >= poiR.x && startx <= poiR.x1
						&& starty >= poiR.y && starty <= poiR.y1)
					state = WCRM;
				else if (startx >= poiDR.x && startx <= poiDR.x1
						&& starty >= poiDR.y && starty <= poiDR.y1)
					state = BOMO;
				else if (startx >= poiDL.x && startx <= poiDL.x1
						&& starty >= poiDL.y && starty <= poiDL.y1)
					state = AtSafe;
				else if (startx >= poiU.x && startx <= poiU.x1
						&& starty >= poiU.y && starty <= poiU.y1)
					state = UOMO;
				else if (startx >= poi1.x && startx <= poi1.x1
						&& starty >= poi1.y && starty <= poi1.y1)
					state = AT1M;
				else if (startx >= poi2.x && startx <= poi2.x1
						&& starty >= poi2.y && starty <= poi2.y1)
					state = AT2M;
				else if (startx >= poi3.x && startx <= poi3.x1
						&& starty >= poi3.y && starty <= poi3.y1)
					state = AT3M;
				else if (startx >= poi4.x && startx <= poi4.x1
						&& starty >= poi4.y && starty <= poi4.y1)
					state = AT4M;
				else
					state = DRAG;

				statrPoint1 = new PointF(startx, starty);

				// Log.i(TAG, "down  "+"	getX " + event.getX() + "  getY " +
				// event.getY());
				break;
			// \B5\A5\B5\E3模式\BB\BB\B6\E0\B5\E3
			case MotionEvent.ACTION_POINTER_DOWN:
				float startx1 = event.getX(1);
				float starty1 = event.getY(1);
				statrPoint2 = new PointF(startx1, starty1);
				if (state == DRAG) {
					state = SCALE;
					startDis = getDistabce(statrPoint1.x, statrPoint1.y,
							statrPoint2.x, statrPoint2.y);
				} else if (state == WCLM) {
					rlflag = 0;
					state = WCSM;
					mid1 = new PointF((startx1 + statrPoint1.x) / 2,
							(starty1 + statrPoint1.y) / 2);
				} else if (state == WCRM) {
					rlflag = 1;
					state = WCSM;
					mid1 = new PointF((startx1 + statrPoint1.x) / 2,
							(starty1 + statrPoint1.y) / 2);
				}
				break;

			case MotionEvent.ACTION_POINTER_UP:
				float startx2 = event.getX();
				float starty2 = event.getY();
				if (startx2 >= poiL.x && startx2 <= poiL.x1
						&& starty2 >= poiL.y && starty2 <= poiL.y1)
					state = WCLM;
				else if (startx2 >= poiR.x && startx2 <= poiR.x1
						&& starty2 >= poiR.y && starty2 <= poiR.y1)
					state = WCRM;
				else
					state = DRAG;
				Log.i(TAG, "pointer_up " + "	getX " + event.getX() + "getY "
						+ event.getY());
				break;

			case MotionEvent.ACTION_UP:
				state = NONE;

				break;

			case MotionEvent.ACTION_MOVE:
				Log.i(TAG, "move  state :" + state + " rlflag : " + rlflag);
				float cx = event.getX() - statrPoint1.x;
				float cy = event.getY() - statrPoint1.y;
				switch (state) {
				case UOMO:
					sendParam((byte) 0x01, 2, cy);
					break;
				case BOMO:
					sendParam((byte) 0x02, 2, cy);
					break;
				case SCALE:
					float newDis = getDistabce(event.getX(0), event.getY(0),
							event.getX(1), event.getY(1));
					float scale = newDis / startDis;
					startDis = newDis;
					sendParam((byte) 0x03, 2, scale);
					break;
				case DRAG:
					if (Math.abs(cx) > Math.abs(cy))
						sendParam((byte) 0x05, 2, cx);
					else
						sendParam((byte) 0x04, 2, cy);
					break;
				case AtSafe:
					if (cy > 0)
						offsetY += 2;
					else
						offsetY -= 2;
					Log.d(TAG, "offsetY " + offsetY);
					sendParam((byte) 0x07, 2, cy);
					break;
				case WCLM:
					sendParam((byte) 0x08, 2, cx);
					break;
				case WCRM:
					sendParam((byte) 0x09, 2, cx);
					break;
				case WCSM:
					mid2 = new PointF((event.getX(0) + event.getX(1)) / 2,
							(event.getY(0) + event.getY(1)) / 2);
					switch (rlflag) {
					case 0:
						sendParam((byte) 0x0d, (int) (mid1.x - mid2.x), mid1.y
								- mid2.y);
						break;
					case 1:
						sendParam((byte) 0x0e, (int) (mid1.x - mid2.x), mid1.y
								- mid2.y);
						break;
					}
					mid1 = mid2;
					break;
				case AT1M:
					sendParam((byte) 0x0f, 2, cy);
					break;
				case AT2M:
					sendParam((byte) 0x10, 2, cy);
					break;
				case AT3M:
					sendParam((byte) 0x11, 2, cy);
					break;
				case AT4M:
					sendParam((byte) 0x12, 2, cy);
					break;
				}
				statrPoint1 = new PointF(event.getX(), event.getY());
				break;
			}
			return true;
		}
	}

	protected float getDistabce(float x1, float y1, float x2, float y2) {
		return (float) Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
	}

	private class PoiF {
		public float x;
		public float y;
		public float x1;
		public float y1;
	}

	private class PointF {
		private float x;
		private float y;

		private PointF(float x, float y) {
			this.x = x;
			this.y = y;
		}
	}

}
