package com.flyaudio.backcarcamera;

import android.content.BroadcastReceiver;
import android.view.WindowManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Handler;
import android.os.Messenger;
import android.util.Log;
import com.autochips.dvr.DVR.DVRNativeEvent;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.view.Display;
import android.view.View;
import android.os.SystemProperties;
import com.flyaudio.backcar.MsgType;
import com.autochips.dvr.DVR;
import java.util.List;
import java.util.HashMap;
import java.util.Iterator;
import android.content.Context;
import android.content.Intent;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;

import com.flyaudio.backcarcamera.CameraInterface.CamOpenOverCallback;
import com.flyaudio.backcar.BackCarView;
import com.flyaudio.backcar.BackCarService;

import android.graphics.Point;
import android.os.Bundle;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.cbm.CBManager;

public class BackCarUsbCamera {
	public static final String DVRMSG = "flyaudio.intent.action.BackCarDvr";

	public final static int STATE_IDLE = (0x01 << 0);
	public final static int STATE_CAMERA_DETATCH = (0x01 << 5);
	public static int mDvrState = STATE_IDLE;
	private final static int mCamVendorId = 3141;
	public final static int mCamProductId = 25446;
	private int mResolutionX = 1280;
	private int mResolutionY = 720;
	public BackCarView vSurfaceView = null;
	private BackCarService mServer = null;
	private static final String TAG = "BackCarCamera";
	public boolean mIsDvrUsing = false;
	public static BackCarUsbCamera UsbCm = null;
	public CameraActivity cameraview = new CameraActivity();
	private CBManager gCBM = null;
	public int retryTimes = 0;

	public static BackCarUsbCamera getUsbCm() {
		return UsbCm;
	}

	public void DVR_Init(BackCarService mService) {
		mServer = mService;
		vSurfaceView = mServer.getFlyBackCarMainView().mBackCarView;
		String ss = SystemProperties.get("persist.backcar.mode");
		if (ss.equals("")) {
			SystemProperties.set("persist.backcar.mode", "3");
		}
		UsbCm = this;
		regiestUsbDevAttachedReceiver();
	}

	public void setDvrUsing(boolean flag) {
		mIsDvrUsing = flag;
	}

	public boolean isDvrUsing() {
		return mIsDvrUsing;
	}

	public void DVR_Deint() {
		unregiestUsbDevAttachedReceiver();
		// CloseCamera();
		stopDVR();
		Log.i(TAG, "Stop DVR: dvr deinit");

	}

	public void preStartDvr() {
		// startDVR();
	}

	public void sendDVRMsg(int m) {

	}

	public void startDVR() {

		DealBackCarMessages(MsgType.MSG_REMOVE, MsgType.MSG_BACKCAR_RETRYEXIT,
				0);
		DealBackCarMessages(MsgType.MSG_REMOVE, MsgType.MSG_BACKCAR_RETRY, 0);
		retryTime = 1;
		setDVRinit(true);
		OpenCamera(null);

		if (gCBM == null)
			gCBM = new CBManager();

		if (gCBM != null) {
			int ret = gCBM.requestAsync(CBManager.SRC_BACKCAR,
					CBManager.BC_ARM1);
			Log.i(TAG, "***Carmera cbm return value is " + ret);
			Log.i(TAG, "Request cbm (BC_ARM1)");
		}

	}

	public void stopDVR() {
		retryTime = 0;
		DealBackCarMessages(MsgType.MSG_REMOVE, MsgType.MSG_BACKCAR_RETRY, 0);
		mServer.DealBackCarMessages(MsgType.MSG_REMOVE,
				MsgType.MSG_CHECK_SIGNAL, 0);
		DealBackCarMessages(MsgType.MSG_REMOVE,
				MsgType.MSG_BACKCAR_RETRYPREVIEW, 0);

		CloseCamera();
		setDVRinit(false);
		if (null != gCBM) {
			gCBM.release();
			Log.i(TAG, "Camera Release cbm (BC_ARM1)");
			gCBM = null;
		}

	}

	public void exit() {

	}

	public void setCarView(BackCarView v) {
		vSurfaceView = v;
	}

	public void setCarViewVisble(boolean flag) {
		if (vSurfaceView == null)
			return;
		if (!flag)
			vSurfaceView.setVisibility(View.GONE);
	}

	private boolean isUsbDvr() {
		String ss = SystemProperties.get("persist.backcar.mode");
		if (!ss.equals(""))
			mServer.BackCarMode = Integer.valueOf(ss).intValue();
		if (mServer.BackCarMode == BackCarService.BACKCAR_USB) {
			Log.i(TAG, "isUsbDvr:  [ " + ss + " ]");
			return true;
		} else
			return false;
	}

	private boolean isDVRinit() {
		if (SystemProperties.get("com.fly.backcar.dvrinit").equals("1"))
			return true;
		else
			return false;
	}

	private void regiestUsbDevAttachedReceiver() {
		Log.i(TAG, "regiestUsbDevAttachedReceiver");
		IntentFilter filter = new IntentFilter();
		filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
		filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
		mServer.registerReceiver(mUsbReceiver, filter);
	}

	private void unregiestUsbDevAttachedReceiver() {
		Log.i(TAG, "unregiestUsbDevAttachedReceiver");
		mServer.unregisterReceiver(mUsbReceiver);
	}

	BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
				Log.i(TAG, "USB DEV BroadcastReceiver, device attached");
				UsbDevice device = (UsbDevice) intent
						.getParcelableExtra(UsbManager.EXTRA_DEVICE);
				int vendorId = device.getVendorId();
				int productId = device.getProductId();
				Log.i(TAG, " device:" + device.getDeviceName());
				Log.i(TAG, "USB DEV BroadcastReceiver vendorId " + vendorId
						+ ", productId " + productId);
				if ((mCamVendorId != vendorId) || (mCamProductId != productId)) {
					Log.i(TAG,
							"USB DEV BroadcastReceiver, device is not our camera");
					return;
				}
				if (!device.getDeviceName().substring(9, 16).equals("usb/002")) {
					Log.i(TAG, "USB DEV BroadcastReceiver, camera not in USB0");
					return;
				}
				if (isUsbDvr()) {
					if (mServer.getBackCarModule().getBackCarState()) {
						Log.i(TAG, "is  backcaring usb attach");
						cameraview.sendMsgToService(MsgType.MSG_HASOPEN);
						restartCarmera();

					} else {
						Log.i(TAG, "is not backcaring ");
					}
				}
			} else {
				if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
					if (!isUsbDvr())
						return;
					Log.i(TAG, "USB DEV BroadcastReceiver, device detached");
					UsbDevice device = (UsbDevice) intent
							.getParcelableExtra(UsbManager.EXTRA_DEVICE);
					int vendorId = device.getVendorId();
					int productId = device.getProductId();
					Log.i(TAG, "USB DEV BroadcastReceiver vendorId " + vendorId
							+ ", productId " + productId);
					if ((mCamVendorId != vendorId)
							|| (mCamProductId != productId)) {
						Log.i(TAG,
								"USB DEV BroadcastReceiver, device is not our camera");
						return;
					}
					if (!device.getDeviceName().substring(9, 16)
							.equals("usb/002")) {
						Log.i(TAG,
								"USB DEV BroadcastReceiver, camera not in USB0");
						return;
					}
					if (mServer.getBackCarModule().getBackCarState()) {
						Log.i(TAG, "it is backcaing usb detach");

						CloseCamera();
						cameraview.sendMsgToService(MsgType.MSG_STARTFAIL);
					} else
						Log.i(TAG, "it is not backcaing usb detach");
				}
			}
		}
	};

	private void setDVRinit(boolean flag) {
		if (flag)
			SystemProperties.set("com.fly.backcar.dvrinit", "1");
		else
			SystemProperties.set("com.fly.backcar.dvrinit", "0");
	}

	public void restartCarmera() {
		DealBackCarMessages(MsgType.MSG_REMOVE, MsgType.MSG_BACKCAR_RETRY, 0);
		retryTimes = 0;
		OpenCamera(null);
		vSurfaceView.setBackCarView(false);
		vSurfaceView.setBackCarView(true);
		setDVRinit(true);
	}

	public void restartPreview() {
		SurfaceHolder holder = mServer.getFlyBackCarMainView().mBackCarView
				.getHolder();
		cameraview.cameraHasOpened(holder);
	}

	public void OpenCamera(SurfaceHolder holder) {
		cameraview.Create(holder);
	}

	public void CloseCamera() {
		retryTimes = 0;
		cameraview.Destroy();

	}

	public void StartCameraPreView(final SurfaceHolder holder) {

		DealBackCarMessages(MsgType.MSG_REMOVE,
				MsgType.MSG_BACKCAR_RETRYPREVIEW, 0);
		cameraview.cameraHasOpened(holder);
	}

	private void checkVendorProductId() {
		// check camera vendor id and product id
		UsbManager manager = (UsbManager) mServer
				.getSystemService(Context.USB_SERVICE);
		HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
		Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();

		mDvrState |= STATE_CAMERA_DETATCH;
		while (deviceIterator.hasNext()) {
			UsbDevice device = deviceIterator.next();
			int vendorId = device.getVendorId();
			int productId = device.getProductId();

			Log.i(TAG, "checkVendorProductId device vendorId " + vendorId
					+ ", productId " + productId);
			if ((mCamVendorId == vendorId) && (mCamProductId == productId)) {
				// if (device.getDeviceName().substring(9,
				// 16).equals("usb/002")||device.getDeviceName().substring(9,
				// 16).equals("usb/001")) {
				if (device.getDeviceName().substring(9, 16).equals("usb/002")) {
					mDvrState &= ~STATE_CAMERA_DETATCH;
				}
			}
		}
		Log.i(TAG, "checkVendor out");
	}

	private void DealBackCarMessages(int DealMessages, int iMsgType, int time) {
		if (DealMessages == MsgType.MSG_SENG) {
			myMsgHandler.sendEmptyMessage(iMsgType);
		}
		if (DealMessages == MsgType.MSG_SENGDELAY) {
			myMsgHandler.sendEmptyMessageDelayed(iMsgType, time);
		}
		if (DealMessages == MsgType.MSG_REMOVE) {
			myMsgHandler.removeMessages(iMsgType);
		}
	}

	Handler myMsgHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MsgType.MSG_BACKCAR_RETRY:
				restartCarmera();
				break;
			case MsgType.MSG_BACKCAR_RETRYPREVIEW:
				restartPreview();
				break;
			case MsgType.MSG_BACKCAR_RETRYEXIT:
				CameraInterface.getInstance().reStopCamera();
				System.exit(0);
				break;

			}

		}
	};
	public int retryTime = 0;

	public class CameraActivity implements CamOpenOverCallback {
		private static final String TAG = "BackCarCameraActivity";
		float previewRate = -1f;
		private boolean isPreview = false;
		byte gmsg = 0;
		SurfaceHolder Holder = null;

		public void Create(final SurfaceHolder holder) {
			Log.i(TAG, "Create  ");
			Thread openThread = new Thread() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					CameraInterface.getInstance().doOpenCamera(holder,
							CameraActivity.this);
				}
			};
			openThread.start();

		}

		public void Resume() {
			Log.i(TAG, "Resume  ");
		}

		public void Pause() {
			Log.i(TAG, "Pause  ");
			CameraInterface.getInstance().doStopPreview();

		}

		public void Destroy() {
			Log.i(TAG, "Destroy  ");
			CameraInterface.getInstance().doStopCamera();

		}

		public void initViewParams() {
			LayoutParams params = vSurfaceView.getLayoutParams();
			Point p = DisplayUtil.getScreenMetrics(mServer
					.getApplicationContext());
			params.width = p.x;
			params.height = p.y;
			previewRate = DisplayUtil.getScreenRate(mServer
					.getApplicationContext()); // 默认全屏的比例预览
			mServer.getFlyBackCarMainView().mBackCarView
					.setLayoutParams(params);

		}

		public void setGetEventLock(boolean flag) {
			mServer.setBcStop(flag);

		}

		public void sendMsgToService(byte msg) {
			gmsg = msg;
			Log.i(TAG, "sendMsgToService " + msg);
			switch (msg) {
			case MsgType.MSG_RETRYOPEN:
				if (retryTimes++ < 10)
					// mServer.detachUsb();
					DealBackCarMessages(MsgType.MSG_SENGDELAY,
							MsgType.MSG_BACKCAR_RETRY, MsgType.CAMRETRY_TIMEOUT);
				break;
			case MsgType.MSG_HASOPEN:

				if (mServer.mTellFastBootStopLogo)
					mServer.DealBackCarMessages(MsgType.MSG_SENGDELAY,
							MsgType.MSG_SIGNAL_READY, 200);
				else
					mServer.DealBackCarMessages(MsgType.MSG_SENGDELAY,
							MsgType.MSG_SIGNAL_READY, 0);
				mServer.DealBackCarMessages(MsgType.MSG_REMOVE,
						MsgType.MSG_CHECK_SIGNAL, 0);
				// mServer.DealBackCarMessages(MsgType.MSG_REMOVE,MsgType.MSG_SIGNAL_READY,
				// 0);
				break;
			case MsgType.MSG_RETRYPREVIEW:

				if (retryTime == 1)
					DealBackCarMessages(MsgType.MSG_SENGDELAY,
							MsgType.MSG_BACKCAR_RETRYPREVIEW,
							MsgType.PREVIEW_TIMEOUT);
				break;
			case MsgType.MSG_RETRYEXIT:
				if (retryTime == 0)
					DealBackCarMessages(MsgType.MSG_SENGDELAY,
							MsgType.MSG_BACKCAR_RETRYEXIT,
							MsgType.RETRYEXIT_TIMEOUT);
				break;
			case MsgType.MSG_REMOVEEXIT:
				DealBackCarMessages(MsgType.MSG_REMOVE,
						MsgType.MSG_BACKCAR_RETRYEXIT, 0);
				break;
			case MsgType.MSG_OPEN:
				break;
			case MsgType.MSG_STARTFAIL:
				mServer.DealBackCarMessages(MsgType.MSG_SENGDELAY,
						MsgType.MSG_SIGNAL_LOST, 0);
				break;
			}
		}

		public void cameraHasOpened(SurfaceHolder holder) {
			// TODO Auto-generated method stub
			CameraInterface.getInstance().doStartPreview(holder, previewRate,
					CameraActivity.this);
		}

	}
}
