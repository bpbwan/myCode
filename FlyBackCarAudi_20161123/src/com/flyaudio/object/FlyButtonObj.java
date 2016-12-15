package com.flyaudio.object;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.flyaudio.backcar.R;
import com.flyaudio.globaldefine.UIAction;
import com.flyaudio.backcar.BackCarService;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.Canvas;
import com.flyaudio.backcar913.FlyBaseListener;
import com.flyaudio.backcar913.BackCarTag;

import android.graphics.Rect;

public class FlyButtonObj extends FlyUIObj implements View.OnClickListener {
	final String TAG = "BackCarActivity";
	protected boolean m_bActionUI = true;
	public boolean m_bTouchable = true;
	protected boolean m_bDownEvent = true;
	protected boolean m_bUpEvent = true;

	protected boolean m_bTimer = false;
	protected int m_timerDelay = 300;
	protected int m_ExTimerDelay = 0;
	protected int m_timerTimes = 0;
	Bitmap bitmap2 = null;

	private int m_Data = 0;
	protected int m_curTimerTimes = 0;// 鐢ㄤ簬瀹氭椂鍣ㄦ鏁拌鏁�
	protected boolean m_isTouching = false;

	public boolean GetActionUI() {
		return m_bActionUI;
	}

	public boolean isM_bTouchable() {
		return m_bTouchable;
	}

	public void setM_bTouchable(boolean m_bTouchable) {
		this.m_bTouchable = m_bTouchable;
	}

	public void setBackGroud(int imgIndex) {
		if (imgIndex < 0)
			return;
		// if(m_Size>0 && imgIndex>m_Size)return;
		// m_Data = imgIndex;
		try {
			getBackground().setLevel(imgIndex);
		} catch (Exception e) {
		}
		// invalidate();
	}

	public int getBackGroudLevel() {
		return getBackground().getLevel();
	}

	public void changeBackGroud() {
		m_Data = getBackGroudLevel();
		m_Data = m_Data == 0 ? 1 : 0;
		setBackGroud(m_Data);
	}

	Handler handler = new Handler();
	Runnable runnable = new Runnable() {
		public void run() {
			// TODO Auto-generated method stub
			if (m_timerTimes == 0)// 涓�鐩村惊鐜�
			{
				handler.postDelayed(this, m_ExTimerDelay);
			} else {
				if (m_curTimerTimes > 0) {
					handler.postDelayed(this, m_ExTimerDelay);
					m_curTimerTimes--;
				} else {
					return;
				}
			}
			byte[] paramBuf = new byte[1];
			paramBuf[0] = 0x00;
			MakeAndSendMessageToModule(mControlID,
					UIAction.UIACTION_MOUSETIMER, paramBuf);
			bLongTouch = true;
		}
	};

	public FlyButtonObj(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = getContext().obtainStyledAttributes(attrs,
				R.styleable.FlyButtonObj);
		m_bActionUI = a.getBoolean(R.styleable.FlyButtonObj_actionUI, true);
		m_bTouchable = a.getBoolean(R.styleable.FlyButtonObj_touchAble, true);
		m_bDownEvent = a.getBoolean(R.styleable.FlyButtonObj_downEvent, true);
		m_bUpEvent = a.getBoolean(R.styleable.FlyButtonObj_upEvent, true);

		m_bTimer = a.getBoolean(R.styleable.FlyButtonObj_bTimer, false);
		m_timerDelay = a.getInt(R.styleable.FlyButtonObj_btimerDelay, 300);
		m_ExTimerDelay = a.getInt(R.styleable.FlyButtonObj_bExTimeDelay,
				m_timerDelay);
		m_timerTimes = a.getInt(R.styleable.FlyButtonObj_btimerTimes, 0);
		a.recycle();
		mOtherBtn = a.getBoolean(R.styleable.FlyButtonObj_otherBtn, false);
		mNeedJudgment = a.getBoolean(R.styleable.FlyButtonObj_NeedJudgment,
				false);
		this.setOnClickListener(this);
	}

	boolean bLongTouch = false;
	boolean mOtherBtn = false;
	boolean mNeedJudgment = false;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub

		// super.onTouchEvent(event);
		if (m_bTouchable == false) {
			return true;
		}

		switch (event.getAction()) {
		case MotionEvent.ACTION_CANCEL:
			Log.d("DDD", "ACTION_CANCEL");
			handler.removeCallbacks(runnable);

			if (m_bActionUI && !mOtherBtn) {
				getBackground().setLevel(0);
			}

			break;

		case MotionEvent.ACTION_DOWN:
			if (m_bActionUI && !mOtherBtn) {
				getBackground().setLevel(1);
			}
			if (m_bDownEvent) {
				m_isTouching = true;
				Log.d("mouse", "button  touch down");
				byte[] paramBuf = new byte[1];
				paramBuf[0] = 0x00;
				// Log.d("DDD", "mControlID "+mControlID);

				if (mNeedJudgment) {
					try {
						drawableToBitamp(getBackground());
						if (bitmap2.getPixel((int) (event.getX()),
								((int) event.getY())) == 0)
							return false;
					} catch (Exception e) {
						Log.d(TAG, "Excption " + e);
					}
				}

				if (mControlID == BackCarTag.CHOICE_BNT
						|| mControlID == BackCarTag.CHOICE_BNT2) { // choice btn
																	// 914
					// drawableToBitamp(getBackground());
					// if(bitmap2.getPixel((int)(event.getX()),((int)event.getY()))==0)
					// return false;
					if (mControlID == BackCarTag.CHOICE_BNT)
						FlyBaseListener.setObjLevel(BackCarTag.CHOICECIRCLE, 2);
					else
						FlyBaseListener.setObjLevel(BackCarTag.CHOICECIRCLE, 1);
					getBackground().setLevel(2);
				}
				FlyBaseListener.focus((String) this.getTag());
				MakeAndSendMessageToModule(mControlID,
						UIAction.UIACTION_MOUSEDOWN, paramBuf);
				// this.requestFocus();

			}
			if (m_bTimer) {
				m_curTimerTimes = m_timerTimes;
				handler.postDelayed(runnable, m_timerDelay);
			}
			break;
		case MotionEvent.ACTION_UP:
			if (m_bActionUI && !mOtherBtn) {
				getBackground().setLevel(0);
			}

			Rect rect = new Rect();
			getGlobalVisibleRect(rect);

			if (getWidth() < event.getX() || event.getX() < 0
					|| event.getY() < 0 || getHeight() < event.getY()) {
				FlyBaseListener.oldBtnFocus();
				break;
			}

			if (mControlID == BackCarTag.CHOICE_BNT
					|| mControlID == BackCarTag.CHOICE_BNT2) // 俩个前后视选择按钮
				FlyBaseListener.setObjLevel(BackCarTag.CHOICECIRCLE, 0);

			if (m_bUpEvent) {
				Log.d("mouse", "button touch up" + bLongTouch);
				if (bLongTouch) {
					bLongTouch = false;
				} else {
					byte[] paramBuf = new byte[1];
					paramBuf[0] = 0x00;
					// MakeAndSendMessageToModule(mControlID,
					// UIAction.UIACTION_MOUSEUP, paramBuf);
				}

				BackCarService.mBackCarService
						.MakeAndSendMessageButton(mControlID);

			}

			if (m_bTimer) {
				handler.removeCallbacks(runnable);
			}

			break;
		default:
			break;
		}
		return true;

	}

	// +++++++++++++++++++++++++++++//
	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		if (m_isTouching) {
			m_isTouching = false;
			return;
		}
		
		Log.d("mouse", "onClick " + view.getTag());
		// byte[] paramBuf = new byte[1];
		// paramBuf[0] = 0x00;
		// MakeAndSendMessageToModule(mControlID,
		// UIAction.UIACTION_MOUSEDOWN, paramBuf);
		BackCarService.mBackCarService.MakeAndSendMessageButton(mControlID);
	}

	protected void drawableToBitamp(Drawable drawable) {
		int w = drawable.getIntrinsicWidth();
		int h = drawable.getIntrinsicHeight();
		Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
				: Bitmap.Config.RGB_565;
		bitmap2 = Bitmap.createBitmap(w, h, config);
		Canvas canvas = new Canvas(bitmap2);
		drawable.setBounds(0, 0, w, h);
		drawable.draw(canvas);
	}

}
