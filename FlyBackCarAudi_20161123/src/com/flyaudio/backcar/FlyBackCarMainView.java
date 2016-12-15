package com.flyaudio.backcar;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;

import com.flyaudio.backcar.MsgType;
import com.flyaudio.globaldefine.FlyaudioIntent;
import com.flyaudio.globaldefine.FlyaudioProperties;
import com.autochips.backcar.BackCar;

import android.app.Presentation; //for backoutvideo
import android.hardware.display.DisplayManager;
import android.view.Window;
import android.util.DisplayMetrics;

import java.io.DataInputStream;
import java.io.InputStream;

import android.widget.FrameLayout;
import android.os.Bundle;
import android.os.SystemProperties;

import com.autochips.dvr.DVR;

import android.widget.Button;
import android.view.Gravity;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.MotionEvent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup.LayoutParams;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.widget.ImageView;
import android.os.Message;
import android.os.Handler;
import android.os.Messenger;

import java.util.ArrayList;

import com.flyaudio.globaldefine.*;

import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.Drawable;

import com.flyaudio.backcar913.*;
import com.flyaudio.tool.PageViewManager;

import android.graphics.Paint;

import com.flyaudio.backcar.util.*;
import com.flyaudio.backcar.modules.BaseModule;
import com.flyaudio.backcar.trackbitmap.BackCarTrack;
import com.flyaudio.tool.GLTrackViewToolHelper;

public class FlyBackCarMainView {
	public static final String ACTION_BACKCAR_PREPARE_START = "android.backcar.action.PREPARE_START";
	public static final String ACTION_BACKCAR_STARTED = "android.backcar.action.STARTED";
	public static final String ACTION_BACKCAR_FINISH = "android.backcar.action.FINISH";
	private static String TAG = "FlyBackCarMainView";
	private BackCarService mBackCarService;
	public OverLayManager mOverLayManager;
	private WindowManager mWM = null;
	public View mBackCarMainView = null;
	public BackCarView mBackCarView = null;
	public FlyGLSurfaceView flyBackCarView = null;
	private BackCar913View mBackCar913View = null;
	private static final int PAL_WIDTH = 720;
	private static final int PAL_HEIGHT = 576;
	private boolean mBCView = false;
	private int mSigHeight = PAL_HEIGHT;
	private int mSigWeight = PAL_WIDTH;
	private BackCarPresentation mRearBackCarUI = null;
	private FrameLayout layout = null;

	private View mainLayoutView = null;

	public View videoBtnView = null;
	private View extraView = null;
	private View blackpage = null;
	private View externUi  = null;
	private FrameLayout.LayoutParams layoutparam;
	private boolean uw = true;
	public boolean frontflag = true;
	private static int FRONTVIEW = 1;
	private static int BACKFLAG = 2;
	private static int CVBBACKFLAG = 3;

	private int retryGetLayout = 3;

	private int width = 0;
	private int height = 0;
	private int cartype = 0;
	private int curentid = 0;
	WindowManager.LayoutParams lp = null;
	WindowManager.LayoutParams vdolp = null;
	private SharedPreferences mSharedPreferences;
	String shared_info = "backcar_config";

	BackCarModule mBackCarModule;
	BackCarTrack mBackCarTrack = null;

	final int BASEOVERAY = 0;
	final int BACKCARVDO = BASEOVERAY+1;
	final int BACKCAR_TRACKBG = BACKCARVDO+1;
	public final int BACKCAR_TRACK = BACKCAR_TRACKBG+1;
	final int BACKCARUI = BACKCAR_TRACK+1;
	final int BALCKPAGE = BACKCARUI+1;
	public FlyBackCarMainView(BackCarService mService) {
		mBackCarService = mService;
	}
	View mglSView;
	View track_Bg;
	View track_lack_view;
	Display display ;
	public void initBackCarWM() {
		mWM = (WindowManager) mBackCarService
				.getSystemService(Context.WINDOW_SERVICE);
		display = mWM.getDefaultDisplay();
		width = display.getWidth();
		height = display.getHeight();
		
		mOverLayManager = new OverLayManager(mWM);
		// mScreenW = display.getWidth();
		// mScreenH = display.getHeight();
		// Log.i(TAG, "getScreenSize - mScreenW: " + mScreenW + ", mScreenH: "+
		// mScreenH);
		lp = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		lp.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN;
		lp.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
		lp.format = PixelFormat.TRANSLUCENT;
		
		
		vdolp = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		vdolp.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN;
		vdolp.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
		vdolp.format = PixelFormat.RGBA_8888;
		mSharedPreferences = mBackCarService.getSharedPreferences(shared_info,
				0);
		
		mBackCarTrack = BackCarTrack.getInstance();
//     	 lp.width = 800;
//    	 vdolp.width = 800;
////		 lp.height = 480;

	}
	LayoutInflater inflater;
	public void InitBackCarView() {

		inflater = (LayoutInflater) mBackCarService
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		int currentpageid = FlyaudioProperties.GetBackCarMainpageid();
		curentid = currentpageid;
		getCartype();
		mBackCarMainView = inflater.inflate(R.layout.main, null);

		mBackCarModule = mBackCarService.getBackCarModule();

		initBackCarView();

		initCarUiView(curentid);
		initFlyBackCarView();
		mBackCarService.SetFlyUIObjProperty((ViewGroup) mBackCarMainView);
		uw = true;

		Log.i(TAG, "InitBackCarMainView ");

	}
	View vdoView;
	void initBackCarView() // surfaceview
	{
		blackpage = inflater.inflate(R.layout.black_page, null);
		vdoView = inflater.inflate(R.layout.fly_camera_layout, null);
		mBackCarView = (BackCarView) vdoView.findViewById(R.id.camera);
		setBackCarViewFlag(!mBackCarService.mbackcarflag);
		mBackCarView.getHolder().setFixedSize(mSigWeight, mSigHeight);
		mBackCarView.setImgFormat(mBackCarService.mgetBackCarMode()); // ������ 913
																		// ��û��Ƶ
		mBackCarService.setCarView(mBackCarView);
		mBackCarView.getBackCarServiceInstance(mBackCarService);
		mBackCarView.setBackcarMode(mBackCarService.getBackCarMode());
		mBackCarView.setLayoutSize(width, height);
		Log.i(TAG, "initBackCarView");

	}

	void initFlyBackCarView() // �켣 ui
	{
		mglSView = GLTrackViewToolHelper.getInstance(mBackCarService).getglView();
		flyBackCarView = GLTrackViewToolHelper.getInstance(mBackCarService).getGlview();

		if (mBackCarService.special_large_screen) {
			android.view.ViewGroup.LayoutParams lvp = new android.view.ViewGroup.LayoutParams(
					mBackCarService.special_screen_width, height);
			FrameLayout.LayoutParams lvp2 = new FrameLayout.LayoutParams(
					mBackCarService.special_screen_width, height);

			flyBackCarView.setLayoutParams(lvp2);

			// mBackCarMainView.setLayoutParams(lvp2);
			// mBackCarView.setLayoutParams(lvp);
			layout.setLayoutParams(lvp2);
		}

	}

	public void initCarUiView(int currentpageid) // ��ͨ���� �� �״��ť��
	{
		track_Bg = mBackCarService.proxyContext.getLayout(PageID.PAGE_TRACK_BG);
		if(track_Bg != null)
			mBackCarService.SetFlyUIObjProperty((ViewGroup) track_Bg);
		Log.d(TAG, "track_Bg  "+track_Bg);
		addLayoutView(2);
		layout = (FrameLayout) mBackCarMainView.findViewById(R.id.addViewId);
		layoutparam = new FrameLayout.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT);
		mainLayoutView = mBackCarService.proxyContext.getLayout(currentpageid);

		if (layout != null && mainLayoutView != null) {
			layout.addView(mainLayoutView, layoutparam);
			videoBtnView = (View) mBackCarService.proxyContext
					.getLayout(PageID.PAGE_913VIDEO_UI);
			if (videoBtnView != null) {
				ArrayList<String> tag = new ArrayList<String>();
				tag.add(BackCarTag.VIDEOPAGE);
				FlyBaseListener videobutton = new FlyBaseListener("913Button",
						(ViewGroup) videoBtnView, 0, 0, tag);
				FlyBaseListener.focus(FlyUtil
						.HexToTag(BackCarTag.SET150VIDEO_CID));
				mBackCarService.SetFlyUIObjProperty((ViewGroup) videoBtnView);
				Get913View().init913CarView2();
			}

		} else {
			if (retryGetLayout > 0) {
				retryGetLayout--;
				initCarUiView(curentid);
			}
			return;
		}

		int lpBottom = mSharedPreferences.getInt("AuxLine_Height", 0);
		if (lpBottom != 0) {
			View v = mainLayoutView.findViewWithTag(BackCarTag.AuxLine);
			if (v != null) {
				FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) v
						.getLayoutParams();
				lp.bottomMargin = lpBottom;
				v.setLayoutParams(lp);
			}
		}

		String extern_UI = "extern_backcar_ui_" + BackCarService.ui_carType;

	   externUi = (View) mBackCarService.proxyContext.getLayoutFromSkin(
				PluginProxyContext.externContext, extern_UI);

		if (externUi != null) {
			Log.d(TAG, "extern_UI " + extern_UI);
			layout.addView(externUi, layoutparam);
		}

		String TrackBg_name = "extern_trackbg_" + BackCarService.ui_carType;
		Drawable TrackBg_D = mBackCarService.proxyContext.getDrawableFromSkin(
				PluginProxyContext.externContext, TrackBg_name);
		if (TrackBg_D != null) {

			replaceDrawable(BackCarTag.TRACKBG, TrackBg_D);
		}

		String AuxLine_up = "auxline_right_" + BackCarService.ui_carType;
		replaceDrawable(FlyUtil.HexToTag(BackCarTag.AUXLINE_UP),
				mBackCarService.proxyContext.getDrawableFromSkin(
						PluginProxyContext.externContext, AuxLine_up));
		String AuxLine_down = "auxline_left_" + BackCarService.ui_carType;
		replaceDrawable(FlyUtil.HexToTag(BackCarTag.AUXLINE_DOWN),
				mBackCarService.proxyContext.getDrawableFromSkin(
						PluginProxyContext.externContext, AuxLine_down));
		Log.i(TAG, "initCarUiView");

	}
	
 public void BlackPageShow(){
	 if(blackpage!= null){
		 
		 WindowManager.LayoutParams lps = new WindowManager.LayoutParams(
					WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		 
		 if(!BaseModule.getBaseModule().isInKeygurad())
			 lps.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
		 lps.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN;
		 lps.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
		 lps.width = 2000;
		 lps.gravity = Gravity.LEFT;
		 lps.height = WindowManager.LayoutParams.MATCH_PARENT;
		 lps.x = 0;
		 mOverLayManager.AddOverLayToWM(blackpage, lps, BALCKPAGE);
		 Log.d(TAG, "BlackPageShow");
	 }
 }
 
 public void BlackPageShow(int w, int h, int top, int gravity){
	 if(blackpage!= null){
		 WindowManager.LayoutParams lps = new WindowManager.LayoutParams(
					WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY);
		 lps.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN;
		 lps.systemUiVisibility =  View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
		 lps.width = w;
		 lps.gravity = gravity;
		 lps.height = h;
		 lps.y = top;
		 mOverLayManager.AddOverLayToWM(blackpage, lps, BALCKPAGE);
		 Log.d(TAG, "BlackPageShow 2");
	 }
 }
 public void BlackPageHide(){
	 mOverLayManager.RemoveOverLayById(BALCKPAGE);
	 Log.d(TAG, "BlackPageHide");
 }
 
 public void TranceTrackAndTrackBg(){
	 if(track_Bg != null)
	 track_Bg.setVisibility(View.INVISIBLE);
	Log.d(TAG, "sTranceTrackAndTrackBg");
	}
	
	
	
	public void ReBackTrackAndTrackBg(){

    	Log.d(TAG, "ReBackTrackAndTrackBg");
    	if(track_Bg != null)
		track_Bg.setVisibility(View.VISIBLE);
	}
 
 
	public SharedPreferences getPreferences() {
		return mSharedPreferences;
	}

	void replaceDrawable(String UItag, Drawable b) {
		if (b != null) {
			final View v = findChildViewWithTag(UItag);
			if (v == null)
				return;
			v.setBackgroundDrawable(b);
		}
	}

	private Integer getIntFromExtern(String name, int Default) {
		return mBackCarService.proxyContext.getIntegerFromSkin(
				PluginProxyContext.externContext, name, Default);
	}

	void setCarUiLayoutMargin(String UItag, int leftMrin, int topMrin,
			int rightMrin, int bottomMrin) {

		final View v = findChildViewWithTag(UItag);
		if (v == null)
			return;

		FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) v.getLayoutParams();
		if (leftMrin != -1)
			lp.leftMargin = leftMrin;
		if (topMrin != -1)
			lp.topMargin = topMrin;
		if (rightMrin != -1)
			lp.rightMargin = rightMrin;
		if (bottomMrin != -1)
			lp.bottomMargin = bottomMrin;

		v.setLayoutParams(lp);
	}

	public void LayoutAddViewWithPageID(View v, int layoutpageid) {
		if (v != null) {
			if (!PageViewManager.FindHasPageView(layoutpageid)) {
				ArrayList<String> tag = new ArrayList<String>();
				tag.add(PageID.GetPageTag(layoutpageid));
				Log.d(TAG, "LayoutAddViewWithPageID  "+tag+layoutpageid);
				FlyBaseListener setlight = new FlyBaseListener(""
						+ layoutpageid, (ViewGroup) v, 0, 0, tag);
				mBackCarService.SetFlyUIObjProperty((ViewGroup) v);

				layout.addView(v, layoutparam);

				PageViewManager.AddPageView(layoutpageid, v);
			}
		}
	}

	public void LayoutRemoveViewWithPageID(int layoutpageid) {
		FlyBaseListener.searchNameAndDel(""+layoutpageid);
		if (PageViewManager.FindHasPageView(layoutpageid)) {
			layout.removeView(PageViewManager.GetPageView(layoutpageid));
//			Log.d("DDDD", "LayoutRemoveViewWithPageID  "+layoutpageid);
			PageViewManager.RemodePageView(layoutpageid);
		

		}
	}

	public void LayoutRemoveAllPageView() {
		PageViewManager.RemoveAllPageView(layout);
	}

	View m913BtnView = null;

	public void addLayoutView(int type) {
		if (layout == null)
			return;

		switch (type) {
		case 1:
			if (m913BtnView == null)
				layout.addView(videoBtnView, layoutparam);
			m913BtnView = videoBtnView;
			break;
		case 2:
			if (m913BtnView != null)
				layout.removeView(videoBtnView);
			m913BtnView = null;
			break;
		}
	}

	public void SetCurLightLayout(int type, View v) {
		if (type == 1)
			LayoutAddViewWithPageID(v, PageID.PAGE_913SETLIGHT);
		else if (type == 2)
			LayoutRemoveViewWithPageID(PageID.PAGE_913SETLIGHT);
		/*
		 * WindowManager.LayoutParams mParams = new
		 * WindowManager.LayoutParams(); mParams.type =
		 * WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;// ϵͳ��ʾwindow
		 * mParams.format = PixelFormat.TRANSLUCENT;// ֧��͸�� //mParams.format =
		 * PixelFormat.RGBA_8888; //mParams.flags |=
		 * WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;// ���� mParams.width =
		 * 230;//���ڵĿ�͸� mParams.height = 480; mParams.x = 570;//����λ�õ�ƫ����
		 * mParams.y = 0;
		 */
	}

	public void setCurLightLayout(int type) {
		SetCurLightLayout(type,
				mBackCarService.proxyContext.getLayout(PageID.PAGE_913SETLIGHT));
	}

	public BackCar913View Get913View() {
		return mBackCarService.mBackCar913Service.GetBackCar913View();
	}

	public View getView() {
		return mainLayoutView;
	}

	public View findChildViewWithTag(String tag) {
		
		
		View ret =  mainLayoutView.findViewWithTag(tag);
		if(ret == null)
			ret = layout.findViewWithTag(tag);
		//if(ret == null)
		//	ret = vdoView.findViewWithTag(tag);
		if(track_Bg != null && ret == null)
			ret = track_Bg.findViewWithTag(tag);
		return ret;
	}

	public boolean isUIViewDisable(String tag) {
		final View v = findChildViewWithTag(tag);
		boolean show = false;
		try {
			int state_visl = v.getVisibility();
			if (state_visl == View.VISIBLE)
				show = true;
		} catch (Exception e) {
			Log.d(TAG, "UIParentViewShow " + e);
		}
		return show;
	}

	public void layoutParentViewShow(String tag, boolean show,
			boolean needReversal) {
		final View v = (View) layout.findViewWithTag(tag);
		try {
			final ViewGroup vp = (ViewGroup) v.getParent();
			if (needReversal) {
				int state_visl = vp.getVisibility();
				if (state_visl == View.VISIBLE)
					show = false;
				else
					show = true;
			}
			if (show)
				vp.setVisibility(View.VISIBLE);
			else
				vp.setVisibility(View.GONE);
		} catch (Exception e) {
			// Log.d(TAG, "UIParentViewShow "+e);
		}
	}

	public boolean UIParentViewShow(String tag, boolean show,
			boolean needReversal) {
		final View v = findChildViewWithTag(tag);
		try {
			final ViewGroup vp = (ViewGroup) v.getParent();
			if (needReversal) {
				int state_visl = vp.getVisibility();
				if (state_visl == View.VISIBLE)
					show = false;
				else
					show = true;
			}
			if (show)
				vp.setVisibility(View.VISIBLE);
			else
				vp.setVisibility(View.GONE);
		} catch (Exception e) {
			// Log.d(TAG, "UIParentViewShow "+e);
			show = uiParentViewShow(tag, show, needReversal);
		}
		return show;
	}

	public boolean uiParentViewShow(String tag, boolean show,
			boolean needReversal) {
		final View v = layout.findViewWithTag(tag);
		try {
			final ViewGroup vp = (ViewGroup) v.getParent();
			if (needReversal) {
				int state_visl = vp.getVisibility();
				if (state_visl == View.VISIBLE)
					show = false;
				else
					show = true;
			}
			if (show)
				vp.setVisibility(View.VISIBLE);
			else
				vp.setVisibility(View.GONE);

		} catch (Exception e) {
			// Log.d(TAG, "uiParentViewShow "+e);
		}
		return show;
	}

	void SetTestBtnLayout(String tag, int evx, int evy) {

		View v = mBackCarMainView.findViewWithTag(tag);
		FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) v
				.getLayoutParams();
		if ((lp.bottomMargin - evy) > 0 && (lp.bottomMargin - evy) < 600)
			lp.bottomMargin -= evy;
		if ((lp.leftMargin + evx) > 0 && (lp.leftMargin + evx) < 1440)
			lp.leftMargin += evx;
		v.setLayoutParams(lp);

		int point_913[] = new int[4];
		mBackCarService.getSurface913Point(point_913);
		if (tag.equals(FlyUtil.HexToTag(BackCarTag.UITEST913_180))) {
			final int x_180 = point_913[0] + evx;
			final int y_180 = point_913[1] + evy;
			if (x_180 > 400 && x_180 <= 1440)
				point_913[0] += evx;
			if (y_180 > 300 && y_180 <= 720)
				point_913[1] += evy;
		} else {
			final int x_150 = point_913[2] + evx;
			final int y_150 = point_913[3] + evy;
			if (x_150 > 400 && x_150 <= 1440)
				point_913[2] += evx;
			if (y_150 > 300 && y_150 <= 720)
				point_913[3] += evy;
		}

		mBackCarService.setSurface913Point(point_913);
	}

	public void saveEditToPreference(String st, int value) {
		Editor editor = mSharedPreferences.edit();
		editor.putInt(st, value);
		editor.commit();

	}

	public void saveEditToPreference(String st, boolean value) {
		Editor editor = mSharedPreferences.edit();
		editor.putBoolean(st, value);
		editor.commit();

	}

	public void setAuxLineHeight(int ev_Y) {
		final View v = findChildViewWithTag(BackCarTag.AuxLine);
		if (v == null || cartype == 175)
			return;
		FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) v
				.getLayoutParams();
		if ((lp.bottomMargin - ev_Y) > 0 && (lp.bottomMargin - ev_Y) < 300)
			lp.bottomMargin -= ev_Y;
		v.setLayoutParams(lp);

		saveEditToPreference("AuxLine_Height", lp.bottomMargin);

	}

	public void mSetTrailMode(int mode) {
		flyBackCarView.setTrailMode(mode);
		mBackCarService.getBackCarModule().tellmoduleRefAngle((byte) 0x0);
	}

	public void setBackCarViewFlag(boolean flag) {
		if (mBackCarView != null)
			mBackCarView.setmFlag(flag);

	}

	public BackCarView getBackCarView() {
		return mBackCarView;
	}

	public int getScreenW() {
		return width;
	//	return 800;//display.getWidth();
	}

	public int getScreenH() {
		return height;
	//	return  display.getHeight();
	}

	public void ShowBackCarView() {
		mBackCarModule.getBaseModulePreStart();
		mBackCarService.setReversing(true);
		setrearview_xian();

		mBackCarTrack.sendParam((byte) 0x18, 2, 1);

		if (mBackCarMainView == null) {
			Log.e(TAG, "mBackCarMainView == null");
			return;
		}

		

		if (mBCView == false){
			mOverLayManager.AddOverLayToWM(vdoView, vdolp, BACKCARVDO);
		
			if(track_Bg != null)
				mOverLayManager.AddOverLayToWM(track_Bg, lp, BACKCAR_TRACKBG);
			mOverLayManager.AddOverLayToWM(mglSView, lp, BACKCAR_TRACK);
			mOverLayManager.AddOverLayToWM(mBackCarMainView, lp, BACKCARUI);
			//mWM.addView(mBackCarMainView, lp);
		
			//	GLTrackViewToolHelper.getInstance(mBackCarService).onResume();
		}
		mBCView = true;
		
		// mBackCarService.StopTXZ();

		mBackCarModule.BaseModuleStart();
		
		Intent intentPrepareStart = new Intent(ACTION_BACKCAR_PREPARE_START);
		if (null != intentPrepareStart) {
			try {
				mBackCarService.SendBroadcast(intentPrepareStart);

			} catch (Exception e) {
				Log.e(TAG,
						"sendStickBroadcast ACTION_BACKCAR_PREPARE_START exception occurred: "
								+ e);
			}
		}
		Log.d(TAG, " ShowBackCarView ");
	}

	public void RemoveBackCarView() {
		mBackCarService.setReversing(false);
	//	mBackCarView.setVisibility(View.INVISIBLE);
		mBackCarModule.getBaseModulePreStop();

		flyBackCarView.setDraw(false);

		if (mBCView == true) {
			GLTrackViewToolHelper.getInstance(mBackCarService).onPause();
			//mOverLayManager.RemoveAllOverLayView();
			//mWM.removeView(mBackCarMainView);
			mOverLayManager.RemoveOverLayById(BACKCAR_TRACK);
			mOverLayManager.RemoveOverLayById(BACKCAR_TRACKBG);
			mOverLayManager.RemoveAllOverLayView();
			LayoutRemoveAllPageView();
			addLayoutView(2); // ���������ֵ�ȥ��
			mOverLayManager.RemoveOverLayById(BACKCARVDO);
		}

		mBCView = false;

		mBackCarService.DealBackCarMessages(MsgType.MSG_REMOVE,
				MsgType.MSG_BACKCAR_STARTACTIVITY, 0);
		// mBackCarService.showHYUNDAIEasyTouch(true);
		Intent intentFinish = new Intent(ACTION_BACKCAR_FINISH);
		if (null != intentFinish) {
			try {
				mBackCarService.SendBroadcast(intentFinish);
			} catch (Exception e) {
				Log.e(TAG,
						"sendBroadcast ACTION_BACKCAR_FINISH exception occurred: "
								+ e);
			}
		}

		if (mBackCarService.mbackcarflag) {
			mBackCarView.mflag = false;
		}

		mBackCarModule.BaseModuleExit();

		Log.i(TAG, "RemoveBackCarView");
	}

	public void needChangeParamJni(int w, int h) {
		flyBackCarView.needChangeParamJni(w, h);
	
	}

	public void RemoveBackCarViewPowerOff() {
		if (mBCView) {
			mWM.removeView(mBackCarMainView);
			mBCView = false;
			// Log.i(TAG, "MSG_BACKCAR_STOP - mWM.removeView");
		}
	}

	public void setRearview_xian(boolean flag) {
		Log.i(TAG, "setRearview_xian ");
		if (flag)
			Get913View().mBackCar913Service.ShowBgView(1, true);
		else
			Get913View().mBackCar913Service.ShowBgView(2, false); // del
	}

	public void setFronView(boolean flag) {
		setRearview_xian(flag);
		flyBackCarView.setDraw(flag);
	}

	public boolean isSetShowTrack(){
		return frontflag;
	}
	public void setCvbFronView() {
		flyBackCarView.setDraw(frontflag);
		mSetTrailMode(CVBBACKFLAG);
		if(BaseModule.getBaseModule().DEBUG)
			Log.d(TAG, "setCvbFronView  "+frontflag);
		mBackCarModule.ShowUiView(BackCarTag.bg913Front_150, false);
		mBackCarModule.ShowUiView(BackCarTag.bg913Rear_150, false);
		mBackCarModule.ShowUiView(BackCarTag.bgcvb, frontflag);
		mBackCarModule.tellmoduleRefAngle((byte) 0);

	}

	public void BackToCvbFrontView() {

		if (mBackCarService.hasDigInVideo)
			setCvbFronView();

	}

	public void mSetFronView() {
		setFronView(frontflag);
	}

	public void RequestLayout(int mWidth, int mHeight) {
		if (mSigHeight != mHeight || mSigWeight != mWidth) {
			mSigHeight = mHeight;
			mSigWeight = mWidth;
			// if(mBackCarService.mgetBackCarMode() !=
			// mBackCarService.BACKCAR_914)
			mBackCarView.getHolder().setFixedSize(mWidth, mHeight);
			// else {
			// mSigHeight = PAL_HEIGHT;
			// mSigWeight = PAL_WIDTH;
			// }
		}
		// mBackCarView.requestLayout();
		// mBackCarModule.tellmoduleRefAngle((byte)0);
	//	mBackCarView.setVisibility(View.VISIBLE);
		// mBackCarView.setSystemUiVisibility(View.INVISIBLE );
	}

	public void getCartype() {
		cartype = mBackCarService.cartype;
	}

	public void updateUI(byte[] data, int len, int messageType) {
		// TODO Auto-generated method stub
		if (mBackCarMainView == null)
			return;

		int FlyUIControlID = (int) ((data[3] & 0xFF) << 24)
				| (int) ((data[4] & 0xFF) << 16)
				| (int) ((data[5] & 0xFF) << 8) | (data[6]) & 0xFF;

		String controlID = String.format("%08x", FlyUIControlID);

		// Log.i(TAG, "controlID = "+controlID);459040 --70120
		if (flyBackCarView != null && FlyUIControlID == 460288 && len > 12) // 0x70600
		{

			int angle = (int) data[11] & 0xFF;
			if (mBackCarService.getBaseModule().DEBUG_L2)
				Log.i(TAG, "data [10] = " + data[10] + " data[11] "
						+ data[11] + " data[12] " + data[12]+"  "+angle);

			if (mBackCarService.needToDealGuiji) {
				angle = DealTrackRever(data[10], angle);
			}
			Message tmpMsg = Message.obtain();
			tmpMsg.what = 1;
			tmpMsg.arg1 = angle;
			myMsgHandler.sendMessage(tmpMsg);
			return;
		} else if (FlyUIControlID == 460080)
			mBackCarModule.FromModuleMsgCtrAuxLine((byte) data[9]);
		else if (FlyUIControlID == 459028) // 00070114 //phone call
			mBackCarModule.FromModuleMsgCtrPhoneCall((byte) data[9]);

		if (mBackCarMainView != null) {
			View iv = mBackCarMainView.findViewWithTag(controlID);
			if (iv != null) {
				mBackCarService.getUIMsgCenter().SetFlyUIObjProperty(iv);
			}
		}
		if(track_Bg != null){
			View iv2 = track_Bg.findViewWithTag(controlID);
		if (iv2 != null) {
			mBackCarService.getUIMsgCenter().SetFlyUIObjProperty(iv2);
		}
		}
	}

	Handler myMsgHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
		//	flyBackCarView.update(0, (int) msg.arg1);
			GLTrackViewToolHelper.getInstance(mBackCarService).setAngle((int) msg.arg1);
			mBackCarModule.DealWithTrackAngle((int) msg.arg1);
		}
	};

	private int DealTrackRever(int data, int angle) {

		if (data == 0)
			angle = 90 - angle;
		else
			angle = 90 + angle;

		return angle;

	}

	public void setrearview_xian() {
		String ss = SystemProperties.get("persist.fly.benz.backaid"); //
		if (ss.equals("0"))
			frontflag = false;
		else
			frontflag = true;

		//flyBackCarView.setDraw(frontflag);
	}

	public void DealMainView(int type, int cid, boolean flag) {
		String controlID = String.format("%08x", cid);
		View iv = mBackCarMainView.findViewWithTag(controlID);
		if (iv == null)
			return;
		switch (type) {
		case 0:
			iv.requestFocus();
			break;
		case 1:
			if (flag)
				iv.setVisibility(View.VISIBLE);
			else
				iv.setVisibility(View.GONE);
			break;
		default:
			break;
		}

	}

	public void extra_Ui_Pagejump(int pageId) {

		if (extraView != null)
			layout.removeView(extraView);
		extraView = null;

		String pageID = FlyaudioIntent.GetExtraPage(pageId);
		String extra_page = "extern_backcar_ui_xxx" + "_" + pageID;
		Log.d(TAG, "Extern_page " + extra_page);
		extraView = (View) mBackCarService.proxyContext.getLayoutFromSkin(
				PluginProxyContext.externContext, extra_page);

		if (extraView != null) {
			Log.d(TAG, "Extern_page " + extra_page);
			layout.addView(extraView, layoutparam);
		}

	}

	public void DetechBackCarMode(int mode){
		if(externUi != null){
		Log.d(TAG, "externUi  not null");
		if(mode == mBackCarService.BACKCAR_CVB)
			externUi.setVisibility(View.VISIBLE);
		else 
			externUi.setVisibility(View.GONE);
		}
	}

	
	
	
	
	private class BackCarPresentation extends Presentation {

		public BackCarView mVideoView = null;

		public BackCarPresentation(BackCarService mService, Display display) {
			super(mService, display);
		}

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			// Be sure to call the super class
			super.onCreate(savedInstanceState);

			Window win = getWindow();
			WindowManager.LayoutParams params = win.getAttributes();
			// params.type = WindowManager.LayoutParams.FIRST_SYSTEM_WINDOW +
			// 26;
			params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;

			setContentView(R.layout.main);

			mVideoView = (BackCarView) this.findViewById(R.id.bcview);
			mVideoView.mIsRear = true;
			mBackCarView.setVisibility(View.VISIBLE);
			// mVideoView.getHolder().setFormat(ImageFormat.YV12);
			// mVideoView.getHolder().setFormat(ImageFormat.RGB_565);
			// mVideoView.getHolder().setFixedSize(PAL_WIDTH, PAL_HEIGHT);
			Log.i(TAG, "BackCarPresentation -> onCreate: " + mVideoView);
		}
	};

	/*
	 * 
	 * 
	 * private void showRearBackCarUI() { Log.i("BBB", "rearbackcarUI"); if
	 * (null == mRearBackCarUI) { DisplayManager mDisplayManager =
	 * (DisplayManager
	 * )mBackCarService.getSystemService(Context.DISPLAY_SERVICE); String
	 * displayCategory = DisplayManager.DISPLAY_CATEGORY_PRESENTATION; Display[]
	 * displays = mDisplayManager.getDisplays(displayCategory);
	 * 
	 * Log.i("BBB", "There are currently " + displays.length +
	 * " displays connected."); for (Display display : displays) { Log.i("BBB",
	 * "	" + display); } if (displays.length < 1) { Log.e(TAG,"only get ("+
	 * displays.length+")display instance"); return; }
	 * 
	 * mRearBackCarUI = new BackCarPresentation(mBackCarService, displays[0]);
	 * if (null == mRearBackCarUI) { return; }
	 * 
	 * } Log.i("BBB", "end ofUI"); mRearBackCarUI.show(); }
	 * 
	 * private void hideRearBackCarUI() { if (null != mRearBackCarUI) {
	 * Log.d(TAG, "hideRearBackCarUI "); mRearBackCarUI.hide();
	 * mRearBackCarUI.dismiss(); mRearBackCarUI = null; } }
	 */
}
