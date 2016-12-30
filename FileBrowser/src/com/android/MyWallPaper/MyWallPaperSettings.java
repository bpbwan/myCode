package com.android.MyWallPaper;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

public class MyWallPaperSettings extends Activity{
	String TAG = "AAAA";
@Override
protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	
	Log.d(TAG, "onCreate");
	Button view = new Button(this);
	setContentView(view);
}

@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.d(TAG, "onResume");
	}

@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.d(TAG, "onPause");
	}

@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.d(TAG, "onDestroy");
	}
}
