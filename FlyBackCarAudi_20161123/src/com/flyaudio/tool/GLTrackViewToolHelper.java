package com.flyaudio.tool;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;

import android.util.Log;
import android.view.ViewGroup;
import android.view.WindowManager;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import com.flyaudio.backcar.BackCarService;
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
import com.flyaudio.backcar.FlyGLSurfaceView;
import com.flyaudio.backcar.util.OverLayManager;



public class GLTrackViewToolHelper  extends BaseToolHelper{
	static GLTrackViewToolHelper mGLTrackViewToolHelper;

	FlyGLSurfaceView mglSView;
	
	public static GLTrackViewToolHelper getInstance(BackCarService Service) {
		if (mGLTrackViewToolHelper == null) {
			mGLTrackViewToolHelper = new GLTrackViewToolHelper(Service);

		}
		return mGLTrackViewToolHelper;
	}

	public GLTrackViewToolHelper(BackCarService Service) {
		
		super(Service);
		init();
	}

	protected void init() {
		super.init();

		mainView = inflater.inflate(R.layout.gl_surfavelayout_test, null);

		mglSView = (FlyGLSurfaceView) mainView
				.findViewById(R.id.glsurface); // add
		mglSView.setup(mBackCarService.proxyContext,
			FlyUtil.GET_SCREEN_W(), FlyUtil.GET_SCREEN_H());
		Log.d("GL", "init  ");
		mglSView.setDraw(true);
		mglSView.setFocusableInTouchMode(false);

	}


	public View getglView(){
		return mainView;
	}

	public void show() {
	WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
	mParams.type = WindowManager.LayoutParams.TYPE_KEYGUARD - 1
			| WindowManager.LayoutParams.FLAG_FULLSCREEN
			| WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
	mParams.format = PixelFormat.TRANSLUCENT;
	mParams.flags =WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
	mParams.height = WindowManager.LayoutParams.FILL_PARENT;
	
	mParams.width = WindowManager.LayoutParams.FILL_PARENT;
	mParams.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;


	//mBackCarService.getFlyBackCarMainView().mOverLayManager.addView(mainView, mParams);


		
	//		mBackCarService.getFlyBackCarMainView().flyBackCarView
	//			.setVisibility(View.GONE);
	//	showView(mainView,  0x666);

		mglSView.onResume();
		mglSView.update(90);
	}

	public void hide() {
		super.hide();
			mBackCarService.getFlyBackCarMainView().flyBackCarView
				.setVisibility(View.VISIBLE);

		mglSView.onPause();
	}

	public void onPause(){
		if(mglSView != null)
			mglSView.onPause();
	}
	public void onResume(){
		if(mglSView != null)
			mglSView.onResume();
	}
public void setAngle(int data){
	if(mglSView != null)
	mglSView.update(data);

	}
 public FlyGLSurfaceView  getGlview(){
	 
	 return mglSView;
 }

}


