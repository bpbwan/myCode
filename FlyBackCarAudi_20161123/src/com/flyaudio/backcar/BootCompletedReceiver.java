package com.flyaudio.backcar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.flyaudio.backcar.modules.AuDiA4Module;
import com.flyaudio.backcar.modules.BaseModule;
import com.flyaudio.globaldefine.PageID;

public class BootCompletedReceiver extends BroadcastReceiver {
	public static final String TAG = "BackCar BootCompletedReceiver";
	public static final String clickAction = "android.intent.backcar.frontRearCamera";
	public static final String clickActionT = "android.intent.backcar.frontRearCamera.t";
	public static final String clickActionTF = "android.intent.backcar.frontRearCamera.tf";
	private static BackCarService mBackCarService = null;

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if ("android.intent.action.BOOT_COMPLETED".equals(action)) {
			Log.i(TAG, "**********onReceive**********");

			if (null != context) {
				Intent startIntent = new Intent(
						"android.intent.action.BACKCAR_SERVICE");
				context.startService(startIntent);
			}

			Intent mintent = new Intent();
			mintent.setAction("flyaudio.intent.action.BOOT_OK");
			// mintent.setPackage("com.flyaudio.backcar");
			context.sendBroadcast(mintent);

		} else if (clickAction.equals(action)) {
			Log.d(TAG, " clickAction -------------");
			if (null != context) {
				Start913Actvity(context);
			}
		} else if (clickActionT.equals(action)) {
			Log.d(TAG, " clickAction Test Std OUT-------------");
			if (null != context) {
				mBackCarService = BackCarService.getInstance();
				if (mBackCarService == null)
					return;
				mBackCarService.IO_START();
				// mBackCarService.mBackCar913Service.getStallDStatus(false);
//				BaseModule.getBaseModule().removeFloatRadar(
//						PageID.PAGE_913FLOATRADAR);
			}

		} else if (clickActionTF.equals(action)) {
			Log.d(TAG, " clickAction Test Std IN-------------");
			mBackCarService = BackCarService.getInstance();
			// mBackCarService.mBackCar913Service.getStallDStatus(true);
			mBackCarService.IO_STOP();
//			BaseModule.getBaseModule().showFloatRadar(
//					PageID.PAGE_913FLOATRADAR);
//			if (BaseModule.getBaseModule().isShowNavigationbar())
//				mBackCarService.getFlyFloatRadarView().SyncScreenSize(800, 480);
		}
	}

	private void Start913Actvity(Context context) {
		mBackCarService = BackCarService.getInstance();
		if (mBackCarService == null)
			return;
		byte data[] = { 0x01 };
		Log.i(TAG,
				"brocastReceiver StartActvity - BackCarActivity-------------------");
		mBackCarService.mBackCar913Service.Get913BroadcastMsg(data);

	}
}
