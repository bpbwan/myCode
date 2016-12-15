package com.flyaudio.backcar913;

import android.util.Log;
import android.view.View;
import com.autochips.inputsource.InputSourceClient;
import com.autochips.inputsource.DIGIN;
import com.autochips.inputsource.DIGIN;
import com.autochips.inputsource.InputSourceClient;
import com.autochips.inputsource.InputSource;
import android.os.Message;
import android.os.Handler;
import android.os.Messenger;
import com.flyaudio.backcar.*;

import android.view.SurfaceHolder;
import com.flyaudio.backcar.MsgType;

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
	private BackCar913Service mBackCar913Service = null;
	public int RearView = 0;

	static int start = 0;
	static int stop = 0;
	static int DgInit = 0;

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
		mVideoSrcType = InputSource.SOURCE_TYPE_DIGITALIN;
		mDigInFmt = DIGIN.DIG_FMT_601_1280_720_p;
		if (null == mInputSourceV) {
			mInputSourceV = new DIGIN();
			Log.d(TAG, "new   DIGIN: " + (mInputSourceV));

			((DIGIN) mInputSourceV).setOnSignalListener(mListenerSignal);
			((DIGIN) mInputSourceV).setOnCbmCmdListener(mListenerCbmCmd);
		}
	}

	public void DIGIN_Init(SurfaceHolder holder) {

		if (carview != null) {
			carview.setInputClient(mInputSourceV);
			// carview.getHolder().setFixedSize(1280, 720);
		}
		Log.i(TAG, "DIGIN_init holder  " + holder);
		setVideoSource(mVideoSrcType, mVideoPort);
		mInputSourceV.setDestination(InputSource.DEST_TYPE_FRONT);
		setMirror(RearView);
	}

	public void setBackCarService(BackCar913Service mservice) {
		mBackCar913Service = mservice;

	}

	public void setBackcarView(BackCarView backcarview) {
		// Log.i(TAG, "BackCarView new set  "+backcarview);
		carview = backcarview;
	}

	private void setMirror(int isrear) {
		switch (isrear) {
		case MIRROR_N:
			mInputSourceV.setMirror(NONE_MIRROR);
			break;
		case MIRROR_H:
			mInputSourceV.setMirror(H_MIRROR);
			break;
		}

	}

	public void setRearView(int flag) {
		// switch(flag)
		// {
		// case 1:RearView = 1; break; //front view
		// case 2:RearView = 2; break; //rear view
		// }
		RearView = flag;
	}

	public void msetMirror(int m) {
		if (carview != null)
			carview.setRearMirror(m);
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

	private void hideCurrentSurfaceView() {
		if (carview != null)
			carview.setVisibility(View.INVISIBLE);
	}

	public void startPlayVideo(SurfaceHolder holder) {
		Log.d(TAG, "startPlayVideo(): play video enter");
		int retValueVideo = 0;
		if (mInputSourceV == null) {
			Log.d(TAG,
					"startPlayVideo(): there is no Video so cannot start play");
		} else {
			mInputSourceV.setDisplay(holder);
			Play();
			// retValueVideo = mInputSourceV.play();
			// playing = 1;
		}

		if (InputSource.ERR_CBM_NOT_ALLOWED == retValueVideo) {
			Log.d(TAG, "startPlayVideo(): CBM not allow video play!");

			if (mInputSourceV != null) {
				Stop();
				mInputSourceV.release();
				mInputSourceV = null;
			}
		}
	}

	private InputSourceClient.OnSignalListener mListenerSignal = new InputSourceClient.OnSignalListener() {
		public void onSignal(int msg, int param1, int param2) {

			Log.i(TAG, "onSignal msg:" + msg);
			switch (msg) {
			case SIGNAL_READY:
				Log.i(TAG, "Get SIGNAL_READY");
				// myHandler.removeMessages(UPDATE_SIGNAL_STATE);
				// mWidth = param1;
				// mHeight = param2;

				if (carview != null) {
					carview.SetAVINSignalType(param1, param2);
					Log.i(TAG, "Front Video Show: Video Size1: " + param1 + "X"
							+ param2);
				}
				mBackCar913Service.mBackCarService.setRequestParam(param1,
						param2);
				mBackCar913Service.mBackCarService.getBackCarModule()
						.SIGNAL_READY();
				break;

			case SIGNAL_LOST:

				// if(mBackCar913Service!=null)
				// mBackCar913Service.mBackCarService.getBackCarModule().SIGNAL_LOST();
				Log.i(TAG, "Get SIGNAL_LOST");
				break;

			default:
				return;
			}
		}
	};

	private InputSourceClient.OnCbmCmdListener mListenerCbmCmd = new InputSourceClient.OnCbmCmdListener() {

		public void onCmd(int what, int extra) {
			Log.i(TAG, "OnCbmCmdlListener : what " + what + " extra " + extra);

			switch (what) {
			case InputSourceClient.INPUTSOURCE_CBM_STOP:
				Log.i(TAG, "OnCbmCmdlListener :  INPUTSOURCE_CBM_STOP " );
				break;

			case InputSourceClient.INPUTSOURCE_CBM_START:
				if (InputSource.DEST_TYPE_FRONT_REAR == extra) {
					Log.d(TAG, "Show Presentation");
				}

			default:
				break;
			}
		}
	};

	private void showCurrentSurfaceView() {
		if (carview != null)
			carview.setVisibility(View.VISIBLE);
	}

	public void setVideoSource(int inputSrcType, int videoid) {
		Log.d(TAG, "setSource enter");
		int retValueVideo;
		int width = 0;
		int height = 0;
		Log.d(TAG, "setSource DIGITALIN!");

		retValueVideo = ((DIGIN) mInputSourceV).setSource(inputSrcType,
				mDigInFmt, 0);

		if (InputSource.ERR_FAILED == retValueVideo) {
			Log.d(TAG, "setSource L_FAILED!");

			if (mInputSourceV != null) {
				Stop();
				mInputSourceV.release();
				mInputSourceV = null;
			}
		}
		// mPreDigInFmt = mDigInFmt;
		switch (mDigInFmt) {
		case DIGIN.DIG_FMT_601_1280_720_p:
			width = 1280;
			height = 720;
			break;
		default:
			Log.i(TAG, "setSource():digin format is not correct!!!");
			break;
		}
		if (carview != null)
			carview.SetAVINSignalType(width, height);
	}

	/*
	 * final static int NONE = 0; final static int FRONT = 1; final static int
	 * REAR = 2; private int FronOrRear = NONE; private boolean frontSignal =
	 * false; private boolean rearSignal = false;
	 * 
	 * private InputSourceClient.OnSignalListener mCheckDeviceSignal = new
	 * InputSourceClient.OnSignalListener() {
	 * 
	 * public void onSignal(int msg, int param1,int param2) { boolean isSignal =
	 * false; Log.i(TAG,"onSignal msg:"+msg); switch (msg) { case SIGNAL_READY:
	 * Log.i(TAG,"Get SIGNAL_READY"); isSignal = true; break;
	 * 
	 * case SIGNAL_LOST: Log.i(TAG,"Get SIGNAL_LOST"); isSignal = false; break;
	 * 
	 * default: return; } switch(FronOrRear) { case FRONT: frontSignal =
	 * isSignal; DIGIN_Deinit(); //checkDeivce(); break; case REAR: rearSignal =
	 * isSignal; DIGIN_Deinit(); // mBackCar913Service.deviceSetup(frontSignal,
	 * rearSignal); break; } } };
	 * 
	 * public void checkDeviceTimeout() { switch(FronOrRear) { case FRONT:
	 * DIGIN_Deinit(); checkDeivce(); break; case REAR: DIGIN_Deinit(); //
	 * mBackCar913Service.deviceSetup(frontSignal, rearSignal); break; } } void
	 * checkDeivce() {
	 * 
	 * switch(FronOrRear) { case NONE: FronOrRear = FRONT;
	 * mBackCar913Service.mBackCarModule.ToModule913CameraCtl((byte)0x01);
	 * Log.i(TAG,"FronOrRear FRONT"); break; case FRONT:FronOrRear = REAR;
	 * mBackCar913Service.mBackCarModule.ToModule913CameraCtl((byte)0x00);
	 * Log.i(TAG,"FronOrRear REAR"); break; case REAR: break; }
	 * checkDeivceStart(); } public void checkDeivceStart() { if(DgInit == 1)
	 * DIGIN_Deinit(); DgInit = 1; mVideoPort = 1; mVideoSrcType =
	 * InputSource.SOURCE_TYPE_DIGITALIN; mDigInFmt =
	 * DIGIN.DIG_FMT_601_1280_720_p; if (null == mInputSourceV) { mInputSourceV
	 * = new DIGIN(); Log.d(TAG, "new   DIGIN: " + (mInputSourceV));
	 * 
	 * ((DIGIN)mInputSourceV).setOnSignalListener(mCheckDeviceSignal);
	 * ((DIGIN)mInputSourceV).setOnCbmCmdListener(mListenerCbmCmd); }
	 * setVideoSource(mVideoSrcType, mVideoPort);
	 * mInputSourceV.setDestination(InputSource.DEST_TYPE_FRONT); Play();
	 * mBackCar913Service.DealBackCar913Messages(MsgType.MSG_SENGDELAY,
	 * MsgType.MSG_BACKCAR_913_CHECKDEVICE, MsgType.DIGINCHECK_TIMEOUT); }
	 */
}
