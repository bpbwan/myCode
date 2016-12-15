package com.flyaudio.object;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.LinearLayout;

public class FlyLinearLayout extends LinearLayout {
	public FlyLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public FlyLinearLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
		Log.i("backcarFly", "dispatchKeyEvent " + event.getKeyCode());
		return super.dispatchKeyEvent(event);
	}

}
