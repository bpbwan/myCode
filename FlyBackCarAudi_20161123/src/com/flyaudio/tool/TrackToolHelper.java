package com.flyaudio.tool;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;

import android.util.Log;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.flyaudio.backcar.BackCarService;
import com.flyaudio.backcar.FlyBackCarMainView;

import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;
import android.os.Message;

import java.io.IOException;

import android.os.Handler;
import android.view.View.OnClickListener;

import com.flyaudio.backcar.R;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.graphics.Bitmap;
import android.graphics.PixelFormat;

import com.flyaudio.backcar913.BackCarTag;
import com.flyaudio.globaldefine.PageID;
import com.flyaudio.object.FlyTrackView;
import com.flyaudio.backcar.util.FlyUtil;

public class TrackToolHelper {
	BackCarService mBackCarService;
	static TrackToolHelper mTrackToolHelper;
	FlyTrackView mFlyTrackView;
	View TrackMainView = null;

	public static TrackToolHelper getInstance(BackCarService Service) {
		if (mTrackToolHelper == null) {
			mTrackToolHelper = new TrackToolHelper(Service);

		}
		return mTrackToolHelper;
	}

	public TrackToolHelper(BackCarService Service) {

		mBackCarService = Service;
		init();
	}

	void init() {

		if (TrackMainView != null)
			return;

		LayoutInflater inflater = (LayoutInflater) mBackCarService
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		TrackMainView = inflater.inflate(0x7f030007, null);
			//	inflater.inflate(R.layout.track_tool_helper, null);
		Log.d("DDD", "TrackMainView  "+TrackMainView);
		mFlyTrackView = (FlyTrackView) TrackMainView
				.findViewById(R.id.tracktoolview); // add
		Log.d("DDDDD", "mFlyTrackView "+mFlyTrackView);
		if(mFlyTrackView == null )
			return;
		
		mFlyTrackView.setup(mBackCarService.proxyContext);
		// mFlyTrackView.setWithHeight(FlyUtil., height);
		mFlyTrackView.setDraw(true);
		mFlyTrackView.mbackmainview = TrackMainView; // add
		mFlyTrackView.afterInit();

	}

	public void show() {

		
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		lp.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN;
		lp.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
		lp.format = PixelFormat.TRANSLUCENT;
		//lp.width = 800;
	//	lp.x = 0;
		GLTrackViewToolHelper.getInstance(mBackCarService).getGlview().setDraw(false);
		GLTrackViewToolHelper.getInstance(mBackCarService).onPause();
		mBackCarService.getFlyBackCarMainView().mOverLayManager
		.RemoveOverLayById(mBackCarService.getFlyBackCarMainView().BACKCAR_TRACK);
		if(TrackMainView != null)
		mBackCarService.getFlyBackCarMainView().mOverLayManager.AddOverLayToWM(TrackMainView, lp, 10);
		//		TrackMainView, PageID.PAGE_COMMON_TRACKTOOL);

	//	mFlyTrackView.needChangeParamJni(744,480);
	}

	public void hide() {
//		mBackCarService.getFlyBackCarMainView().LayoutRemoveViewWithPageID(
//				PageID.PAGE_COMMON_SETTING);
//		mBackCarService.getFlyBackCarMainView().flyBackCarView
//				.setVisibility(View.VISIBLE);
	}


}