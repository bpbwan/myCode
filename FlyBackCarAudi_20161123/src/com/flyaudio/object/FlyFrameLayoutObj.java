package com.flyaudio.object;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.FrameLayout;
import android.view.MotionEvent;
import com.flyaudio.backcar.R;

public class FlyFrameLayoutObj extends FrameLayout {
	private String TAG = "BackCarFrame";

	public FlyFrameLayoutObj(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
		Log.i(TAG, "dispatchKeyEvent " + event.getKeyCode());
		// return super.dispatchKeyEvent(event);
		return false;
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		Log.i(TAG, "dispatchTouchEvent ");
		boolean result = super.dispatchTouchEvent(ev);

		return result;

	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		//
		Log.i(TAG, "onInterceptTouchEvent ");
		/*
		 * final int action = MotionEventCompat.getActionMasked(ev); // Always
		 * handle the case of the touch gesture being complete. if (action ==
		 * MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) { // Do
		 * not intercept touch event, let the child handle it return false; }
		 */
		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		// Log.d(TAG, "### is Clickable  ");
		// return super.onTouchEvent(ev);
		return false;
	}

}
