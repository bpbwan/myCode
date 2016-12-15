package com.flyaudio.backcar;

import java.lang.String;

import com.flyaudio.backcar.modules.BaseModule;

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
	int direction, ret;
	int index = 0;
	int show_flag = 1;
	private View myView = null;
	private int resume = 1;
	private int stop = 2;

	public static boolean mustDestroy = false;
	InnerRecevier mRecevier = new InnerRecevier();
	private IntentFilter mFilter = new IntentFilter(
			Intent.ACTION_CLOSE_SYSTEM_DIALOGS);

	public static void setMustDestroy(boolean flag) {
		mustDestroy = flag;
	}

	public boolean onTouchEvent(MotionEvent event) {
		// Log.i(TAG, "FlyaudioBackCarActivity - onTouchEvent");
		// myView.invalidate();
		// int action = event.getAction();
		// Log.i(TAG, " " + action);
		Log.i(TAG, "getX " + event.getX() + "getY " + event.getY());
		if (null != BackCarService.getInstance()) {
			BackCarService.getInstance().SetColor((int) event.getX(),
					(int) event.getY());
		}
		return true;

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		// Log.i(TAG, "onCreate");

		super.onCreate(savedInstanceState);

	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.i(TAG, "onKeyDown");

		if (keyCode == KeyEvent.KEYCODE_BACK /*
											 * || keyCode ==
											 * KeyEvent.KEYCODE_MENU
											 */) {
			Log.i(TAG, "KEY BACK  KEY MENU");

			// TellBackCarService();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	protected void onPause() {
		Log.i(TAG, "pause");
//		BaseModule.getBaseModule().onStopDelay();
		// stopWatch();
		super.onPause();
		
	
	}

	protected void onResume() {
		Log.i(TAG, "onResume " + this);
		if (null != BackCarService.getInstance()) {
			BackCarService.getInstance().setActivity(this);
		}
//		BaseModule.getBaseModule().onStopDelayCancel();
		// startWatch();
		super.onResume();
	}

	protected void onStart() {

		super.onStart();
	}

	protected void onRestart() {
		super.onRestart();
	}

	protected void onStop() {
		Log.i(TAG, "stop");
//		BaseModule.getBaseModule().onStopDelayCancel();
		if (!mustDestroy)
			resetLayout(true);

		super.onStop();
		// if(isNeedFinsh())
		// finish();
	}

	protected void onDestroy() {

		Log.i(TAG, "destroy");
		mustDestroy = false;
		super.onDestroy();
		// gInst = null;
	}

	protected void onSaveInstanceState(Bundle savedInstanceState) {

		// Log.i(TAG, "oSaveInstanceState");
		// super.onSaveInstanceState(savedInstanceState);
	}

	/*
	 * public class CrossView extends View {
	 * 
	 * public CrossView(Context context) { super(context); } }
	 */
	// public static final String ACTION_BACKCAR_CLOSEVIEW =
	// "autochips.intent.action.BACKCAR_CLOSEVIEW";

	public void TellBackCarModule(int task) {

		// if (null != BackCarService.getInstance()) {
		//
		// BackCarService.getInstance().getBackCarModule().ActivityTask(task);
		// }

	}

	private boolean isNeedFinsh() {
		return BackCarService.getInstance().getBackCarModule().isNeedFinsh();
	}

	public void resetLayout(boolean v) {
		if (null != BackCarService.getInstance()) {
			BackCarService.getInstance().resumeTheActivity(v, this);
		}
	}

	void startWatch() {
		if (mRecevier != null) {
			this.registerReceiver(mRecevier, mFilter);
		}
	}

	void stopWatch() {
		if (mRecevier != null) {
			this.unregisterReceiver(mRecevier);
		}
	}

	private class InnerRecevier extends BroadcastReceiver {
		final String SYSTEM_DIALOG_REASON_KEY = "reason";
		final String SYSTEM_DIALOG_REASON_GLOBAL_ACTIONS = "globalactions";
		final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
		final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
				String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
				if (reason != null) {
					Log.i(TAG, "action:" + action + ",reason:" + reason);
					if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
						// �̰�home��
						Log.i(TAG, "onReceive homekey+++++++++++++");
						BackCarService.getInstance().isHomeKey = true;
					} else if (reason.equals(SYSTEM_DIALOG_REASON_RECENT_APPS)) {
						// ����home��
						Log.i(TAG, "onReceive recentapps+++++++++++++");
					}

				}
			}
		}

	}

}
