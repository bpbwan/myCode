package com.flyaudio.backcar;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.graphics.Color;

import com.flyaudio.globaldefine.FlyaudioIntent;
import com.flyaudio.backcar.PluginProxyContext;
import com.flyaudio.backcar913.*;
import java.util.ArrayList;
import com.flyaudio.globaldefine.*;

public class FlyRadarView {
	private static String TAG = "BackCarRadarView";
	private BackCarService mBackCarService;
	// private int mScreenW = 1024;
	// private int mScreenH = 600;
	private View mRadarView = null;
	private WindowManager mRadarWM = null;
	private PluginProxyContext proxycontext = null;
	public static FlyBaseListener mFlyListener = null;
	private int curLayout = 0;

	public FlyRadarView(BackCarService mService, PluginProxyContext proxy) {
		proxycontext = proxy;
		mBackCarService = mService;
	}

	public void initRadarView(int layout) {

		mRadarView = proxycontext.getLayout(layout);
		if (mRadarView != null) {
			Log.d(TAG, "initRadarView:" + layout);
			curLayout = layout;

			ArrayList<String> tag2 = new ArrayList<String>();
			tag2.add(BackCarTag.CHOICEPAGE913);
			FlyBaseListener choice = new FlyBaseListener("913Choice",
					(ViewGroup) mRadarView, 0, 0, tag2);
			mFlyListener = choice;

			mBackCarService.SetFlyUIObjProperty((ViewGroup) mRadarView);
		} else
			Log.d(TAG, "BackCarService jumpPage Failed layout:" + layout);
	}

	public void initRadarWM() {
		mRadarWM = (WindowManager) mBackCarService
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = mRadarWM.getDefaultDisplay();

		// mScreenW = display.getWidth();
		// mScreenH = display.getHeight();
		// Log.i(TAG, "getScreenSize - mScreenW: " + mScreenW + ", mScreenH: " +
		// mScreenH);
	}

	public void ShowRadarView() {
		// mBackCarService.setReversing(true);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);

		lp.x = 0;
		lp.y = 0;
		lp.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN;
		// if(curLayout == PageID.PAGE_NOVIDEO_TIP)
		// lp.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
		// View.SYSTEM_UI_FLAG_FULLSCREEN;
		// else
		lp.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
		if (mRadarView == null)
			return;
		mRadarWM.addView(mRadarView, lp);
		// Log.i(TAG, "ShowRadarView - mRadarWM.addView");

	}

	public void RemoveRadarView() {

		if (mRadarView == null)
			return;

		mRadarWM.removeView(mRadarView);
		mRadarView = null;
		// mBackCarService.setReversing(false);
		// Log.i(TAG, "RemoveRadarView - mRadarWM.removeView");

	}

	public void updateUI(byte[] data, int len, int messageType) {
		// TODO Auto-generated method stub
		if (mRadarView == null)
			return;
		
		int FlyUIControlID = (int) ((data[3] & 0xFF) << 24)
				| (int) ((data[4] & 0xFF) << 16)
				| (int) ((data[5] & 0xFF) << 8) | (data[6]) & 0xFF;

		String controlID = String.format("%08x", FlyUIControlID);
		View v = mRadarView.findViewWithTag(controlID);
		// Log.i(TAG, "conID   "+controlID);
		if (v != null) {
			mBackCarService.getUIMsgCenter().SetFlyUIObjProperty(v);
		}
	}

	public View findChildViewWithTag(String tag) {
		if (mRadarView != null)
			return mRadarView.findViewWithTag(tag);
		return null;
	}

	public void updateTextUiColor(String tag, String color) {
		final View v = findChildViewWithTag(tag);
		if (v != null && v instanceof TextView) {
			TextView tv = (TextView) v;
			tv.setTextColor(Color.parseColor(color));
		}
	}

}
