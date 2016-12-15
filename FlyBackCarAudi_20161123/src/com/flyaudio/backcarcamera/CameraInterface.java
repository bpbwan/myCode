package com.flyaudio.backcarcamera;

import java.io.IOException;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.SurfaceHolder;
import android.hardware.Camera.ErrorCallback;
import android.hardware.Camera.PreviewCallback;
import com.flyaudio.backcar.MsgType;

public class CameraInterface {
	private static final String TAG = "BackCarCameraInterface";
	private Camera mCamera;
	private Camera.Parameters mParams;
	private boolean isPreviewing = false;
	private float mPreviwRate = -1f;
	private static CameraInterface mCameraInterface;
	CamOpenOverCallback mCallback = null;

	public interface CamOpenOverCallback {
		public void cameraHasOpened(SurfaceHolder h);

		public void sendMsgToService(byte m);

		public void setGetEventLock(boolean flag);
	}

	private CameraInterface() {

	}

	public static synchronized CameraInterface getInstance() {
		if (mCameraInterface == null) {
			mCameraInterface = new CameraInterface();
		}
		return mCameraInterface;
	}

	/**
	 * ´ò¿ªCamera
	 * 
	 * @param callback
	 */
	public void doOpenCamera(SurfaceHolder holder, CamOpenOverCallback callback) {

		synchronized (this) {
			if (mCamera != null)
				doStopCamera();
			Log.i(TAG, "Camera open....");
			try {

				mCallback = callback;
				mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);

			} catch (Exception e) {
				// TODO: handle exception
				// no camera devices
				mCamera = null;
				callback.sendMsgToService(MsgType.MSG_RETRYOPEN);
				e.printStackTrace();
			}

			if (null != mCamera) {
				callback.sendMsgToService(MsgType.MSG_OPEN);
				Log.i(TAG, "Camera open over....");
				// callback.cameraHasOpened(holder);
				// doStartPreview(holder, 0, null);
			}
		}
	}

	/**
	 * ¿ªÆôÔ¤ÀÀ
	 * 
	 * @param holder
	 * @param previewRate
	 */
	public void doStartPreview(SurfaceHolder holder, float previewRate,
			CamOpenOverCallback callback) {

		Log.i(TAG, "doStartPreview...");
		if (holder != null && null != mCamera) {

			if (isPreviewing) {
				Log.i(TAG, "isPreviewing is true and  wait doStopCamera...");
				doStopPreview();

			}

			callback.setGetEventLock(false);
			/*
			 * mParams = mCamera.getParameters(); Size previewSize =
			 * CamParaUtil.getInstance().getPropPreviewSize(
			 * mParams.getSupportedPreviewSizes(), previewRate, 800); Log.d(TAG,
			 * "previewSize.width:"
			 * +previewSize.width+" , previewSize.height:"+previewSize.height);
			 * mParams.setPreviewSize(previewSize.width, previewSize.height);
			 * 
			 * mCamera.setParameters(mParams);
			 */
			try {
				mCamera.setPreviewDisplay(holder);
				mCamera.setErrorCallback(mErrorCallback);

				mCamera.startPreview();// ¿ªÆôÔ¤ÀÀ
				Log.i(TAG, "startPreview");
				isPreviewing = true;
				callback.sendMsgToService(MsgType.MSG_HASOPEN);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.i(TAG, "startPreview  +e " + e);
				callback.sendMsgToService(MsgType.MSG_STARTFAIL);
			}

			callback.setGetEventLock(true);
		} else if (null == mCamera) {

			callback.sendMsgToService(MsgType.MSG_RETRYPREVIEW);

		}
	}

	public void doStopPreview() {
		try {
			if (null != mCamera && isPreviewing) {
				mCamera.stopPreview();
				Log.i(TAG, "doStopPreview");
				isPreviewing = false;
			} else
				Log.i(TAG, "mCamera is null so not doStopPreview");
		} catch (Exception e) {
			// TODO: handle exception
			Log.e(TAG, "catch stoppreview  " + e);
		}
	}

	/**
	 * Í£Ö¹Ô¤ÀÀ£¬ÊÍ·ÅCamera
	 */
	public void doStopCamera() {

		try {
			if (null != mCamera) {
				Log.e(TAG, "doStopCamera");
				// mCamera.setPreviewCallback(null);
				doStopPreview();
				mCamera.release();
				Log.e(TAG, "doStopCamera release");
				mCamera = null;

			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	public void reStopCamera() {

		try {

			if (null != mCamera) {
				Log.e(TAG, "reStopCamera");
				doStopPreview();
				mCamera.release();
				Log.e(TAG, "reStopCamera release");
				mCamera = null;
			}

		} catch (Exception e) {
			// TODO: handle exception
			Log.e(TAG, "doStopCamera+e    " + e);
		}
	}

	private ErrorCallback mErrorCallback = new ErrorCallback() {
		@Override
		public void onError(int arg0, Camera arg1) {
			// TODO Auto-generated method stub
			Log.e(TAG, "²¶×½µ½cameraÒì³££º" + arg0);

		}
	};

}
