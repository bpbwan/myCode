package com.flyaudio.backcar;

import java.lang.String;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;

public class FlyaudioBackCarActivity extends Activity {
	/** Called when the activity is first created. */

	final String TAG = "BackCarActivity";


	@Override
	public void onCreate(Bundle savedInstanceState) {

		// Log.i(TAG, "onCreate");

		super.onCreate(savedInstanceState);

		BackCarView  vm = new BackCarView(this);

		setContentView(vm);
		
	}


}
