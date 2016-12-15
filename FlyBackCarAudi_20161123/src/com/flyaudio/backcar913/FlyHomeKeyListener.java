package com.flyaudio.backcar913;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.app.Service;
import com.flyaudio.backcar.BackCarService;
import android.util.Log;
import com.flyaudio.backcar.MsgType;
import com.flyaudio.backcar.util.*;

public class FlyHomeKeyListener extends BroadcastReceiver {
	private IntentFilter mFilter = new IntentFilter(
			Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
	private String TAG = "BackCarHomeKEY";
	final String SYSTEM_DIALOG_REASON_KEY = "reason";
	final String SYSTEM_DIALOG_REASON_GLOBAL_ACTIONS = "globalactions";
	final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
	final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
	private FlyHomeKeyListener homekey_recevier = null;
	private BackCarService mService = null;

	/*
	 * homekey = 3 testcmd # input keyevent 3
	 */
	public FlyHomeKeyListener(BackCarService service) {
		InitHomeKeyListener(service);

	}

	private void InitHomeKeyListener(BackCarService service) {

		mService = service;
		homekey_recevier = this;
	}

	public void startWatch() {
		if (mService != null) {
			mService.registerReceiver(homekey_recevier, mFilter);
		}
	}

	public void stopWatch() {
		if (mService != null) {
			mService.unregisterReceiver(homekey_recevier);
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
			String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
			if (reason != null) {
				// Log.i(TAG, "action:" + action + ",reason:" + reason);
				if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
					// ¶Ì°´home¼ü
					Log.i(TAG, "onReceive homekey+++++++++++++");
					mService.MakeAndSendMessageWithBundle(MsgType.COMMON,
							BackCarTag.HOMEEYEVENT, null);
				} else if (reason.equals(SYSTEM_DIALOG_REASON_RECENT_APPS)) {
					// ³¤°´home¼ü
					Log.i(TAG, "onReceive recentapps+++++++++++++");
				}

			}
		}
	}
}
