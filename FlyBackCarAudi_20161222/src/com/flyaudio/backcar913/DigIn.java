package com.flyaudio.backcar913;

import android.util.Log;
import android.view.View;
import com.autochips.inputsource.InputSourceClient;
import com.autochips.inputsource.DIGIN;
import com.autochips.inputsource.InputSource;
import android.os.Message;
import android.os.Handler;
import android.os.Messenger;
import com.flyaudio.backcar.*;

import android.view.SurfaceHolder;

public class DigIn {
	public static InputSourceClient mInputSourceV = null;
	private int mVideoSrcType = InputSource.SOURCE_TYPE_NONE;
	private int mVideoPort = 0;

	public BackCarView carview = null;
	private boolean mIsSignalOn = false;
	private static final int SIGNAL_READY = 0;
	private static final int SIGNAL_LOST = 1;
	private static final int PALWIDTH = 720;
	private static final int PALHEIGHT = 576;
	public static int playing = 0;
	final int H_MIRROR = 0x20;
	final int NONE_MIRROR = 0x00;
	public static final int MIRROR_N = 1;
	public static final int MIRROR_H = 2;

	private int mDigInFmt;
	private int mCurrentVideoStatus = InputSource.STATUS_NONE;
	private int mTargetVideoStatus = InputSource.STATUS_NONE;
	private boolean mIsAllowedToPlayAudioByCBM = false;
	private String TAG = "BackCarDIGIN";

	public int RearView = 0;

	static int start = 0;
	static int stop = 0;
	static int DgInit = 0;
  private static DigIn mDigIn = null;
	
  public static DigIn getInstance(){
	  if(mDigIn == null)
		  mDigIn = new DigIn();
	  return mDigIn;
  }
  
  public DigIn(){
	  
  }
  
	public static void Play() {
		if (playing == 0 && mInputSourceV != null) {
			int retValueVideo = mInputSourceV.play();
			if (InputSource.ERR_CBM_NOT_ALLOWED == retValueVideo)
				Log.d("BackCarDIGIN", "Play(): CBM not allow video play!");
			start++;
			Log.i("BackCarDIGIN", "++++++++++++start " + start);
			playing = 1;
		}
	}

	public static void Stop() {
		if (playing == 1 && mInputSourceV != null) {
			mInputSourceV.stop();
			stop++;
			Log.i("BackCarDIGIN", "++++++stop " + stop);
			playing = 0;
		}
	}

	public static void setDisplay(SurfaceHolder holder) {
		if (mInputSourceV != null)
			mInputSourceV.setDisplay(holder);
	}

	public void create() {
		if (DgInit == 1)
			DIGIN_Deinit();
		DgInit = 1;
		mVideoPort = 1;
		if (null == mInputSourceV) {
			mInputSourceV = new DIGIN();
			Log.d(TAG, "new   DIGIN: " + (mInputSourceV));

			((DIGIN) mInputSourceV).setOnSignalListener(mListenerSignal);
		
		}
	}

	public void DIGIN_Init(SurfaceHolder holder) {


		Log.i(TAG, "DIGIN_init holder  " + holder);
		//setVideoSource(mVideoSrcType, mVideoPort);
		 ((DIGIN) mInputSourceV).setSource(InputSource.SOURCE_TYPE_DIGITALIN,
				 DIGIN.DIG_FMT_601_1280_720_p, 0);
		mInputSourceV.setDestination(InputSource.DEST_TYPE_FRONT);
	}


	public void setBackcarView(BackCarView backcarview) {
		// Log.i(TAG, "BackCarView new set  "+backcarview);
		carview = backcarview;
	}


	public void DIGIN_Deinit() {

		if (mInputSourceV != null) {

			RearView = 0;
			Stop();
			mInputSourceV.release();
			mInputSourceV = null;
		}
		DgInit = 0;
	}

	private void stop() {
		if (mInputSourceV != null)
			Stop();
	}


	private InputSourceClient.OnSignalListener mListenerSignal = new InputSourceClient.OnSignalListener() {
		public void onSignal(int msg, int param1, int param2) {

			switch (msg) {
			case SIGNAL_READY:
				Log.i(TAG, "Get SIGNAL_READY");
				break;
			case SIGNAL_LOST:

				Log.i(TAG, "Get SIGNAL_LOST");
				break;

			default:
				return;
			}
		}
	};

//
//
//	public void setVideoSource(int inputSrcType, int videoid) {
//		Log.d(TAG, "setSource enter");
//		int retValueVideo;
//		int width = 0;
//		int height = 0;
//		Log.d(TAG, "setSource DIGITALIN!");
//
//		retValueVideo = ((DIGIN) mInputSourceV).setSource(inputSrcType,
//				mDigInFmt, 0);
//
//		if (InputSource.ERR_FAILED == retValueVideo) {
//			Log.d(TAG, "setSource L_FAILED!");
//
//			if (mInputSourceV != null) {
//				Stop();
//				mInputSourceV.release();
//				mInputSourceV = null;
//			}
//		}
//		// mPreDigInFmt = mDigInFmt;
//		switch (mDigInFmt) {
//		case DIGIN.DIG_FMT_601_1280_720_p:
//			width = 1280;
//			height = 720;
//			break;
//		default:
//			Log.i(TAG, "setSource():digin format is not correct!!!");
//			break;
//		}
//		if (carview != null)
//			carview.SetAVINSignalType(width, height);
//	}

	public void DigIn_init(SurfaceHolder holder, BackCarView v) {
		Log.i(TAG, "+++DigIn_init");
		setBackcarView(v);
		create();
		DIGIN_Init(holder);
	}

	public void DigIn_deinit() {
		Log.i(TAG, "+++DigIn_deinit");
		DIGIN_Deinit();
		// mBackCarService.getBackCarView().DestroySurfaceView();
	}
}
