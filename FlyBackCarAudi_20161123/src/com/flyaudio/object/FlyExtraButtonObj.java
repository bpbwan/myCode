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

public class FlyExtraButtonObj extends FlyButtonObj implements
		View.OnClickListener {
	final String TAG = "BackCarActivity";

	public FlyExtraButtonObj(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		// super.onTouchEvent(event);

		switch (event.getAction()) {
		case MotionEvent.ACTION_CANCEL:
			Log.d("DDD", "ACTION_CANCEL");
			handler.removeCallbacks(runnable);
			break;

		case MotionEvent.ACTION_DOWN:
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
}
