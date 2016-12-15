package com.flyaudio.object;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Button;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.*;

public class FlyButton extends Button implements OnKeyListener,
		OnFocusChangeListener {

	public FlyButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		Log.i("backcarFly", "dispatchKeyEvent -----------1--" );
		// TODO Auto-generated constructor stub
	}

	public FlyButton(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		Log.i("backcarFly", "dispatchKeyEvent ----------2--" );
		
		
	}

	public FlyButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		Log.i("backcarFly", "dispatchKeyEvent -----------3--" );
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
		Log.i("backcarFly", "dispatchKeyEvent " + event.getKeyCode());
		return super.dispatchKeyEvent(event);
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		Log.i("backcarFly", "dispatchKeyEvent " + event.getKeyCode());
		return false;
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		// TODO Auto-generated method stub
		Log.i("backcarFly", "dispatchKeyEvent ");
	}

}
