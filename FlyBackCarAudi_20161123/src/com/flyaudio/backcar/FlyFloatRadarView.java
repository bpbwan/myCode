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
import com.flyaudio.globaldefine.PageID;
import com.flyaudio.backcar.PluginProxyContext;
import com.flyaudio.backcar913.*;

import java.util.ArrayList;

import com.flyaudio.globaldefine.*;

import android.view.Gravity;
import android.graphics.PixelFormat;

import com.flyaudio.tool.ExtraPageManager;
import com.flyaudio.tool.PageViewManager;
import com.flyaudio.backcar.util.*;
import com.flyaudio.backcar.modules.*;

public class FlyFloatRadarView {
	private static String TAG = "AuDiFlyFloatRadarView";
	private BackCarService mBackCarService;
	public int mScreenW = 800;
	// private int mScreenH = 600;
	private View mRadarView = null;
	private WindowManager mRadarWM = null;
	private PluginProxyContext proxycontext = null;
	public static FlyBaseListener mFlyListener = null;
	private int curLayout = 0;
	WindowManager.LayoutParams mParams = null;
	WindowManager.LayoutParams mParams2 = null;
	private boolean currentShowing = false;
	private View PageSettingView = null;
	ExtraPageManager mExtraPageManager = new ExtraPageManager();

	OverLayManager olm = null;

	public FlyFloatRadarView(BackCarService mService, PluginProxyContext proxy) {
		proxycontext = proxy;
		mBackCarService = mService;

	}

	public void initFloatRadarView(int layout) {
		// if(mRadarView != null &&curLayout == layout)
		// return;
		mRadarView = proxycontext.getLayout(layout);
		if (mRadarView != null) {
			curLayout = layout;
			ArrayList<String> tag2 = new ArrayList<String>();
			tag2.add(BackCarTag.RADARPAGE913);
			FlyBaseListener choice = new FlyBaseListener("913floatradar",
					(ViewGroup) mRadarView, 0, 0, tag2);
			mFlyListener = choice;

			mBackCarService.SetFlyUIObjProperty((ViewGroup) mRadarView);
		} else
			Log.d(TAG, "BackCarService jumpPage Failed layout:" + layout);
	}

	public void initRadarWM() {
		mRadarWM = (WindowManager) mBackCarService
				.getSystemService(Context.WINDOW_SERVICE);

		olm = new OverLayManager(mRadarWM);

		mParams = new WindowManager.LayoutParams();
		mParams.type = WindowManager.LayoutParams.TYPE_KEYGUARD - 1
				| WindowManager.LayoutParams.FLAG_FULLSCREEN
				| WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
		mParams.format = PixelFormat.TRANSLUCENT;// ֧��͸��
		mParams.flags = // WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM |
		WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
		// if(mBackCarService.needHalfScreen)
		// mParams.width = mBackCarService.special_screen_width; //�̶�ס��С��
		// �����������״�ҳ�ȳ����Ļ����Ҵ��ڷ���״̬��bug
		mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		mParams.gravity = Gravity.RIGHT | Gravity.TOP;
		mParams.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
		currentShowing = false;

		/*
		 * mParams2 = new WindowManager.LayoutParams(); mParams2.type =
		 * WindowManager.LayoutParams.TYPE_KEYGUARD - 1 |
		 * WindowManager.LayoutParams.FLAG_FULLSCREEN |
		 * WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN; mParams2.format =
		 * PixelFormat.TRANSLUCENT;// ֧��͸�� mParams2.flags =
		 * WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
		 * 
		 * mParams2.width = WindowManager.LayoutParams.WRAP_CONTENT;// ���ڵĿ�͸�
		 * mParams2.height = WindowManager.LayoutParams.WRAP_CONTENT;
		 * mParams2.gravity = Gravity.RIGHT | Gravity.TOP;
		 * 
		 * mParams2.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
		 */
	}

	public void setWindownParams(WindowManager.LayoutParams winParam) {
		mParams = winParam;
	}

	public void reFocusAbleRadarWM() {

		mParams.type = WindowManager.LayoutParams.TYPE_KEYGUARD - 1
				| WindowManager.LayoutParams.FLAG_FULLSCREEN
				| WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
		mParams.format = PixelFormat.TRANSLUCENT;// ֧��͸��
		mParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;

		mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;// ���ڵĿ�͸�
		mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		mParams.gravity = Gravity.RIGHT | Gravity.TOP;
		mParams.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;

		mRadarWM.updateViewLayout(mRadarView, mParams);
	}

	public void reFocusDisableRadarWM() {
		mParams.type = WindowManager.LayoutParams.TYPE_KEYGUARD - 1
				| WindowManager.LayoutParams.FLAG_FULLSCREEN
				| WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
		mParams.format = PixelFormat.TRANSLUCENT;// ֧��͸��
		mParams.flags = // WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM |
		WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
				| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
		mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;// ���ڵĿ�͸�
		mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		mParams.gravity = Gravity.RIGHT | Gravity.TOP;
		mParams.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;

		mRadarWM.updateViewLayout(mRadarView, mParams);
	}

	public void setFocusable(boolean flag) {

		if (mRadarView != null && isShowing()) {
			Log.d(TAG, "setFocusable  " + flag);
			if (flag) {
				reFocusAbleRadarWM();
				FlyBaseListener.focus(FlyUtil
						.HexToTag(BackCarTag.A4_AUDI_Float_Radar_Bg));
			}

		}

	}

	public void SyncScreenSize(int screenw, int screenh) {
		Log.d(TAG, "SyncScreenSize  ");
		/*
		 * WindowManager.LayoutParams tmpParam = new
		 * WindowManager.LayoutParams(); tmpParam.type =
		 * WindowManager.LayoutParams.TYPE_KEYGUARD-1 |
		 * WindowManager.LayoutParams.FLAG_FULLSCREEN |
		 * WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN; tmpParam.format =
		 * PixelFormat.TRANSLUCENT ;// ֧��͸�� tmpParam.flags =//
		 * WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM |
		 * WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
		 * 
		 * //tmpParam.width = screenw;
		 * 
		 * tmpParam.gravity = Gravity.RIGHT|Gravity.TOP;
		 * tmpParam.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
		 * 
		 * mRadarWM.updateViewLayout(mRadarView, tmpParam);
		 */
		FlyBaseListener.focus(FlyUtil
				.HexToTag(BackCarTag.A4_AUDI_Float_Radar_Bg));

	}

	public boolean isShowing() {
		return currentShowing;

	}

	public View getMainView() {
		if (mRadarView != null)
			return mRadarView.findViewWithTag(BackCarTag.RADARPAGECHILD);
		return null;
	}

	public View getAllView() {

		return mRadarView;
	}

	public void ShowFloatRadarView() {
		currentShowing = true;
		if (mRadarView != null) {

			mRadarWM.addView(mRadarView, mParams);

			Log.i(TAG, "ShowRadarView - mRadarWM.addView");
		} else
			currentShowing = false;
	}

	public void RemoveFloatRadarView(int needSendMsgToLpc) {

		if (currentShowing && mRadarView != null) {
			BaseModule.getBaseModule().FloatRadarExit(needSendMsgToLpc);
			mRadarWM.removeView(mRadarView);
			currentShowing = false;

			olm.RemoveAllOverLayView();
			Log.d(TAG, "RemoveFloatRadarView ");
		}
	}

	public void updateUI(byte[] data, int len, int messageType) {
		// TODO Auto-generated method stub
//		if (mRadarView == null || !currentShowing) {
//			return;
//		}
		int FlyUIControlID = (int) ((data[3] & 0xFF) << 24)
				| (int) ((data[4] & 0xFF) << 16)
				| (int) ((data[5] & 0xFF) << 8) | (data[6]) & 0xFF;

		String controlID = String.format("%08x", FlyUIControlID);
		if (mRadarView != null) {
			View v = mRadarView.findViewWithTag(controlID);
			if (v != null) {

				mBackCarService.getUIMsgCenter().SetFlyUIObjProperty(v);
			}
		}
		if (PageSettingView != null) {
			View v2 = PageSettingView.findViewWithTag(controlID);
			if (v2 != null) {
				mBackCarService.getUIMsgCenter().SetFlyUIObjProperty(v2);
			}
		}
		View v3 = olm.findViewWithTag(controlID);
		if (v3 != null) {
			mBackCarService.getUIMsgCenter().SetFlyUIObjProperty(v3);
		}
	}

	private View findChildViewWithTag(String tag) {
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

	public void LayoutAddViewWithPageID(View v, int layoutpageid) {
		if (v != null && PageSettingView == null) {
			ArrayList<String> tag = new ArrayList<String>();
			tag.add(PageID.GetPageTag(layoutpageid));
			FlyBaseListener setlight = new FlyBaseListener("" + layoutpageid,
					(ViewGroup) v, 0, 0, tag);
			mBackCarService.SetFlyUIObjProperty((ViewGroup) v);
			Log.d(TAG, "floatLayoutAddViewWithPageID " + layoutpageid);
			mRadarWM.addView(v, mParams);
			PageSettingView = v;
		}
	}

	public void LayoutRemoveViewWithPageID(int layoutpageid) {
		if (PageSettingView != null) {
			Log.d(TAG, "floatLayoutRemoveViewWithPageID " + layoutpageid);
			mRadarWM.removeView(PageSettingView);
			PageSettingView = null;

		}
	}

	public void WMAddViewWithPageID(View v, int layoutpageid) {
		Log.d(TAG, "WMAddViewWithPageID  " + layoutpageid);
		if (v != null) {

			ArrayList<String> tag = new ArrayList<String>();
			tag.add(PageID.GetPageTag(layoutpageid));
			FlyBaseListener setlight = new FlyBaseListener("" + layoutpageid,
					(ViewGroup) v, 0, 0, tag);
			mBackCarService.SetFlyUIObjProperty((ViewGroup) v);

			olm.AddOverLayToWM(v, mParams, layoutpageid);
		}
	}

	public void WMRemoveViewWithPageID(int layoutpageid) {
		Log.d(TAG, "WMRemoveViewWithPageID  " + layoutpageid);
		olm.RemoveOverLayById(layoutpageid);

	}

	public boolean isPageSettingShowing() {
		Log.d(TAG, "isPageSettingShowing  " + PageSettingView);
		return PageSettingView != null;
	}

	public void RemoveAllOverlayView(){
		Log.d(TAG, "RemoveAllOverlayView");
		olm.RemoveAllOverLayView();
	}
	/*
	 * 
	 * public void LayoutAddViewWithPageID(View v, int layoutpageid){
	 * if(v!=null&& PageSettingView == null){
	 * if(!mExtraPageManager.FindHasPageView(layoutpageid)){ ArrayList<String>
	 * tag = new ArrayList<String>(); tag.add(PageID.GetPageTag(layoutpageid));
	 * FlyBaseListener setlight = new FlyBaseListener(""+layoutpageid,
	 * (ViewGroup)v, 0,0,tag);
	 * mBackCarService.SetFlyUIObjProperty((ViewGroup)v);
	 * 
	 * mRadarWM.addView(v,mParams2); PageSettingView = v;
	 * mExtraPageManager.AddPageView(layoutpageid,v); } } }
	 * 
	 * public void LayoutRemoveViewWithPageID(int layoutpageid){
	 * if(mExtraPageManager.FindHasPageView(layoutpageid) || PageSettingView!=
	 * null){ Log.d("DDD", "LayoutRemoveViewWithPageID "+layoutpageid);
	 * 
	 * final View tmp = mExtraPageManager.GetPageView(layoutpageid);
	 * mRadarWM.removeView(tmp); mExtraPageManager.RemodePageView(layoutpageid);
	 * PageSettingView = null;
	 * 
	 * } }
	 */

}
