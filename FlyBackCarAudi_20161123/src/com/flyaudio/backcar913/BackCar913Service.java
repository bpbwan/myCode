package com.flyaudio.backcar913;

import java.util.Stack;
import java.io.InputStream;
import java.io.IOException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.List;
import java.util.HashMap;
import java.util.Iterator;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.app.Activity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.net.MailTo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Handler;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.view.Display;
import android.view.View;
import android.os.SystemProperties;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.view.SurfaceHolder;

import android.cbm.CBManager;
import com.flyaudio.object.FlyEnumGaugeObj;
import com.flyaudio.data.UIDataStoreCenter;
import com.flyaudio.globaldefine.FlyaudioIntent;
import com.autochips.backcar.BackCar;
import com.flyaudio.msg.FlyUIMsgClient;
import com.flyaudio.msg.HandleServiceMsg;
import com.flyaudio.object.FlyTextObj;
import com.flyaudio.object.FlyUIObj;
import com.flyaudio.msg.FlyUIMsgClient;
import com.flyaudio.backcar.BackCarService;
//import com.flyaudio.backcar.DigIn;
import com.flyaudio.backcar.FlyBackCarMainView;
import com.flyaudio.backcar.FlyaudioBackCarActivity;
import com.flyaudio.backcar.MsgType;
import com.flyaudio.backcar.PluginProxyContext;
import com.autochips.dvr.DVR;
import com.autochips.inputsource.InputSourceClient;
import com.autochips.inputsource.InputSource;
import com.flyaudio.backcar.*;
import com.flyaudio.backcar.util.*;
import com.flyaudio.backcar.modules.*;

public class BackCar913Service {

	private static final String TAG = "BackCar913Service";
	public BackCarService mBackCarService = null;
	private BackCar913View mBackCar913View = null;
	public BackCarModule mBackCarModule = null;
	public final static int CAMERA_913_FRONT = 1;
	public final static int CAMERA_913_REAR = 0;

	private boolean mBackCar913StallD = false;
	public static boolean BackCar913StartByUser = false; //
	public int curCamera = 0;
	public int curCameramode = 0;
	public int curLayout = 0;
	public DigIn mDigIn = new DigIn();
	FlyHomeKeyListener mHomeKeyListener = null;
	public int oldSave_frMode = -1;

	static BackCar913Service gInst = null;

	private boolean delayLock = false; // �ӳ���Ϊ��913����ͷ�ϵ������ӳ٣��ӳ��ڼ��ֶ�����ǰ�󲻴���
	private boolean frontDevice = true;
	private boolean rearDevice = true;
	private boolean lock913 = false;
	private boolean mRunnableExit = false;
	private boolean powerSupplyDelay = false;

	public static enum ProtocolState {
		P_STALL, R_STALL, N_STALL, D_STALL, S_STALL
	};

	private ProtocolState mState_913 = ProtocolState.P_STALL;
	private ProtocolState old_mState_913 = ProtocolState.P_STALL;

	public BackCar913Service(BackCarService mService) {
		mBackCarService = mService;
		mBackCar913View = new BackCar913View(this);
		mBackCarModule = mBackCarService.getBackCarModule();
		mBackCarModule.setUpBackCar913Service(this);
		mHomeKeyListener = new FlyHomeKeyListener(mService);
		gInst = this;

		//int ret = mBackCarService.get913Camera_fr_mode();
		//setUpFrontRearDevice(ret);

	}

	public BackCar913View GetBackCar913View() {
		return mBackCar913View;
	}

	public static BackCar913Service getInstance() {
		return (gInst);
	}

	

	public void deviceSetup(boolean frontSignal, boolean rearSignal) {
		this.frontDevice = frontSignal;
		this.rearDevice = rearSignal;
	}

	public void GetBackCarServiceData(byte[] data) {
		// Log.d(TAG, "GetBackCarServiceData ||  ");

		Message msg = myServiceMsgHandler.obtainMessage();

		Bundle dataTemp = new Bundle();
		dataTemp.putByteArray("BackCarServiceData", data);
		msg.what = 2;
		msg.setData(dataTemp);
		myServiceMsgHandler.sendMessage(msg);
	}

	public void GetBackCarModuleData(byte[] data) {
		// Log.d(TAG, "GetBackCarModuleData ||  ");

		Message msg = myServiceMsgHandler.obtainMessage();

		Bundle dataTemp = new Bundle();
		dataTemp.putByteArray("BackCarModuleData", data);
		msg.what = 3;
		msg.setData(dataTemp);
		myServiceMsgHandler.sendMessage(msg);
	}
	
	
	public void ServiceMsgHandlerDelay(int MsgType, int timeout) {
		// Log.d(TAG, "GetBackCarModuleData ||  ");
		ServiceMsgHandlerRemove(MsgType);
		Message msg = myServiceMsgHandler.obtainMessage();
		msg.what = MsgType;
		myServiceMsgHandler.sendMessageDelayed(msg, timeout);
	}
	
	public void ServiceMsgHandlerRemove(int MsgType) {
		// Log.d(TAG, "GetBackCarModuleData ||  ");
		myServiceMsgHandler.removeMessages(MsgType);
	}
	
	public void GetBackCarEnterByUser(byte[] data) {
		// Log.d(TAG, "GetBackCarEnterByUser ||  ");

		Message msg = myServiceMsgHandler.obtainMessage();

		Bundle dataTemp = new Bundle();
		dataTemp.putByteArray("BackCarEnterByUser", data);
		msg.what = 4;
		msg.setData(dataTemp);
		myServiceMsgHandler.sendMessage(msg);
	}

	Handler myServiceMsgHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 2) {
				Bundle data = msg.getData();
				byte paramer[] = data.getByteArray("BackCarServiceData");
				byte cmd = paramer[0];
				Log.d(TAG, " myServiceMsgHandler 2 data[0] = " + cmd);
				switch (cmd) {
				case MsgType.TRANSFER_BACKCAR_913_STOP:
					if (rearDevice)
						Backcar_913_Leave(1);
					else if (mBackCarModule.getBackCarState()) {
						Backcar_913_Leave(1);
					} else{
						mBackCarService.setBcStop(true);
						set913Lock(false);
					}
					break;
				case MsgType.TRANSFER_BACKCAR_913_START:
					if (rearDevice)
						Backcar_913_Enter(1);
					break;
				}
			} else if (msg.what == 3) {
				Bundle data = msg.getData();
				byte paramer[] = data.getByteArray("BackCarModuleData");
				byte cmd = paramer[0];

				Log.d(TAG, " myServiceMsgHandler 3 data[0] = " + cmd);
				switch (cmd) {
				case MsgType.TRANSFER_STALLD_913_STOP:
					if (frontDevice)
						getStallDStatus(false);
					else {
						mBackCarService.setBcStop(true);
						set913Lock(false);
					}
					break;
				case MsgType.TRANSFER_STALLD_913_START:

					if (frontDevice)
						getStallDStatus(true);
					else {
						mBackCarService.setBcStop(true);
						set913Lock(false);
					}
					break;
				case MsgType.TRANSFER_STALLD_913_EXIT:
					
					BaseModule.getBaseModule().CarSpeedLimit();
					if (FlyBaseListener.getCur_fr_mode() == MsgType.F_MODE
							&& getBackCar913StallDState()) {
						DealBackCar913Messages(MsgType.MSG_REMOVE,
								MsgType.MSG_BACKCAR_913_STALLD_START, 0);
						keyCodeBackExit();

						Log.i(TAG, " TRANSFER_STALLD_913_EXIT ");

					} else if (BackCar913StartByUser == false
							&& getBackCar913StallDState()) {
						Log.i(TAG,
								" TRANSFER_STALLD_913_EXIT BackCar913StartByUser false");

						DealBackCar913Messages(MsgType.MSG_REMOVE,
								MsgType.MSG_BACKCAR_913_STALLD_START, 0);
						keyCodeBackExit();

					}
					break;
				}
			} else if (msg.what == 4) {
				/*
				 * Bundle data = msg.getData(); byte paramer[] =
				 * data.getByteArray("BackCarEnterByUser"); byte cmd =
				 * paramer[0]; Log.d(TAG," myServiceMsgHandler 4 data[0] = " +
				 * cmd);
				 */
			}
			 else if (msg.what == MsgType.STALLD_STOP) {
				 set913Lock(true);
				 byte stallDStop[] = { MsgType.TRANSFER_STALLD_913_STOP };
					GetBackCarModuleData(stallDStop);
				}
			
		}
	};

	public void set913StallState(int value) {
		switch (value) {
		case 0:
			mState_913 = ProtocolState.P_STALL;
			break;
		case 1:
			mState_913 = ProtocolState.R_STALL;
			break;
		case 2:
			mState_913 = ProtocolState.N_STALL;
			break;
		case 4:
			mState_913 = ProtocolState.D_STALL;
			break;
		case 5:
			mState_913 = ProtocolState.S_STALL;
		}
		Log.d(TAG, "StallState   " + value);

	}

	public ProtocolState GetProtocolState() {
		return mState_913;
	}

	public void set913Lock(boolean flag) {
		Log.d(TAG, "set913Lock  "+flag);
		this.lock913 = flag;
	}

	private boolean get913lock() {
		return this.lock913;
	}

	public void ProtocolRunnableStart() {

		mHomeKeyListener.startWatch();
		this.mRunnableExit = false;
		set913Lock(false);
		if (mProtocolRunnable != null)
			mProtocolRunnable.stop();

		mProtocolRunnable = new ProtocolRunnable();
		new Thread(mProtocolRunnable).start();
	}

	public void ProtocolRunnableStop() {
		set913Lock(true);
		if (mProtocolRunnable != null)
			mProtocolRunnable.stop();
		mProtocolRunnable = null;

		this.mRunnableExit = true;
		mHomeKeyListener.stopWatch();
		keyCodeBackExit();
	}

	public static ProtocolRunnable mProtocolRunnable = null;

	public class ProtocolRunnable implements Runnable {

		private boolean consumeStop = true;

		public void stop() {
			Log.d("DDD", "consumeStop  exit ");
			consumeStop = false;
		}

		@Override
		public void run() {
			Log.d("DDD", "consumeStop  in ");
			set913Lock(false);
			while (consumeStop) {
				try {
					Thread.sleep(50);
				} catch (Exception e) {
					// Log.e(TAG, "BackCar Exception e");
					e.printStackTrace();
				}
				if (get913lock())
					continue;
				
				final ProtocolState currentState = mState_913;
				if (old_mState_913 != currentState
						&& mBackCarService.mgetBackCarMode() == mBackCarService.BACKCAR_914){
					Log.d(TAG, "currentState   " + currentState);
					
					switch (currentState) {
					case P_STALL:  //maybe N
						//mBackCarService.setBcStop(false);
						//set913Lock(true);
						ResetHandleMsg();
						Remove_BackCar913_StallD_Auto_Stop();
						if(getBackCar913StallDState()){
						ServiceMsgHandlerDelay(MsgType.STALLD_STOP, MsgType.STALLD_STOP_TIMEOUT);
						}
						break;
					case R_STALL:
						Remove_BackCar913_StallD_Auto_Stop();
						ServiceMsgHandlerRemove(MsgType.STALLD_STOP);
						DealBackCar913Messages(MsgType.MSG_REMOVE,
								MsgType.MSG_BACKCAR_913_STALLD_START, 0);
						if (FlyBaseListener.getCur_fr_mode() == MsgType.FR_MODE)
						{	//setBackCar913StallDState(false);
							
						}
						else if (FlyBaseListener.getCur_fr_mode() == MsgType.F_MODE) {
							byte stallDStop3[] = { MsgType.TRANSFER_STALLD_913_EXIT };
							GetBackCarModuleData(stallDStop3);
							set913Lock(false);
						}
						break;
					case N_STALL: //maybe p
					//	mBackCarService.setBcStop(false);
					//	set913Lock(true);
						ResetHandleMsg();
						Remove_BackCar913_StallD_Auto_Stop();
						if(getBackCar913StallDState()){
						ServiceMsgHandlerDelay(MsgType.STALLD_STOP, MsgType.STALLD_STOP_TIMEOUT);
						}
						break;
					case D_STALL:
					case S_STALL:
					//	mBackCarService.setBcStop(false);
						set913Lock(true);
						ServiceMsgHandlerRemove(MsgType.STALLD_STOP);
						ResetHandleMsg();
						CheckDigInPowerOnOrOff();
						mBackCarModule.ToModule913CameraOn_Off((byte) 0x01);
						mBackCarService.DealBackCarMessages(MsgType.MSG_REMOVE,
								MsgType.MSG_DELAYSTOP, 0);
						byte stallDStart[] = { MsgType.TRANSFER_STALLD_913_START };
						GetBackCarModuleData(stallDStart);
						break;
					}}
				old_mState_913 = currentState;
				
			}

			Log.i(TAG, "BackCar protocol  Thread exit");
		}
	}

	public void Get913BroadcastMsg(byte[] data) {
		Log.d(TAG, "GetCarMsg_Benz ||  ");

		Message msg = myBroadcastMsgHandler.obtainMessage();

		Bundle dataTemp = new Bundle();
		dataTemp.putByteArray("broadcast913", data);
		msg.what = 2;
		msg.setData(dataTemp);
		myBroadcastMsgHandler.sendMessage(msg);
	}

	Handler myBroadcastMsgHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 2) {
				Bundle data = msg.getData();
				byte paramer[] = data.getByteArray("broadcast913");
				byte cmd = paramer[0];
				// byte cmd = 0x1;
				Log.d(TAG, " myBroadcastMsgHandler data[0] = " + cmd);
				switch (cmd) {
				case 0x01:
					mBackCarService.getBackCarView().setBackcarMode(
							BackCarService.BACKCAR_914);
					Backcar_913_Enter(2);

					break;
				}
			}
		}
	};

	Handler msgHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {

			case MsgType.MSG_BACKCAR_913_U_START:
				mBackCarModule.BACKCAR_START_913();
				break;

			case MsgType.MSG_BACKCAR_913_STOP:
				Log.d(TAG, " MSG_BACKCAR_913_STOP mBackCar913StallD = "
						+ mBackCar913StallD);
				if (getBackCar913StallDState()) {
					// cancel
				} else {
					// stop
					BACKCAR_STOP_913();
				}
				
				break;
			case MsgType.MSG_BACKCAR_913_STALLD_START:
				// if mcurStall = D, MayActivityExist, change video or
				// StartActivity , setTimer for autoDestroy(no action|not D
				// ,cancel);
				// if mcurStall != D, cancle;
				// remove timer
				// if quit by user in 100ms ,bug;
				Log.d(TAG, " MSG_BACKCAR_913_STALLD_START mBackCar913StallD = "
						+ getBackCar913StallDState() + " mIsReversing = "
						+ mBackCarService.mIsReversing
						+ " BackCar913StartByUser = " + BackCar913StartByUser);
				if (getBackCar913StallDState()) {
					tellModuleStallDStatus(true);
					if (BackCar913StartByUser) {

						Enter913Video(CAMERA_913_FRONT);
					} else {

						STALLD_START_913();

					}

				}
				mBackCarService.setBcStop(true);
				set913Lock(false);
				break;
			case MsgType.MSG_BACKCAR_913_STALLD_AUTO_STOP:
				Log.d(TAG, " MSG_BACKCAR_913_STALLD_AUTO_STOP mBackCar913StallD = "
						+ getBackCar913StallDState() + " mIsReversing = "
						+ mBackCarService.mIsReversing
						+ " BackCar913StartByUser = " + BackCar913StartByUser
						+ " backcarState = " + mBackCarModule.getBackCarState());
				if (getBackCar913StallDState()) {
					// if no action stop
					// Backcar_913_Leave(1);
					// BACKCAR_STOP_913();
					StallDexit();
				}
				break;
			case MsgType.MSG_BACKCAR_913_ENTER_REAR_VIDEO:
				//mBackCarModule.rear913Video(FlyUtil
				//		.HexToTag(BackCarTag.SET150VIDEO_CID));
				break;
			case MsgType.MSG_BACKCAR_913_ENTER_FRONT_VIDEO:
				//mBackCarModule.front913Video(FlyUtil
				//		.HexToTag(BackCarTag.SET150VIDEO_CID));
				break;
			default:
				break;
			}
		}
	};

	public void DealBackCar913Messages(int DealMessages, int iMsgType, int time) {
		if (DealMessages == MsgType.MSG_SENG) {
			msgHandler.sendEmptyMessage(iMsgType);
		}
		if (DealMessages == MsgType.MSG_SENGDELAY) {
			msgHandler.sendEmptyMessageDelayed(iMsgType, time);
		}
		if (DealMessages == MsgType.MSG_REMOVE) {
			msgHandler.removeMessages(iMsgType);
		}
	}

	public void showActivity() {
		Intent intentBCActvity = new Intent(
				mBackCarService.getApplicationContext(),
				FlyaudioBackCarActivity.class);
		intentBCActvity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_CLEAR_TOP);
		mBackCarService.StartActvity(intentBCActvity);
		Log.i(TAG, "913showActivity - BackCarActivity");
	}

	// public void setBackCarViewflag(boolean b)
	// {
	// if(mBackCarService.getBackCarView() != null)
	// mBackCarService.getBackCarView().setflag(b);
	// }

	private void STALLD_START_913() {
		if (!mBackCarService.mIsReversing) {
			mBackCarModule.BACKCAR_START_913();
			willsetMirror();
		}
		Enter913Video(CAMERA_913_FRONT);
	}

	public void BACKCAR_STOP_913() {
		Log.d(TAG, "BACKCAR_STOP_913 BackCar913StartByUser = "
				+ BackCar913StartByUser);
		DealBackCar913Messages(MsgType.MSG_REMOVE,
				MsgType.MSG_BACKCAR_913_STALLD_AUTO_STOP,
				MsgType.MSG_BACKCAR_913_STALLD_AUTO_STOP_TIMEOUT);
		//�˳�����ʱ�������
		mBackCarService.setBcStop(false);
		if (BackCar913StartByUser == false/*
				|| FlyBaseListener.getCur_fr_mode() == MsgType.R_MODE*/) {
			mBackCarModule.BACKCAR_STOP_913();
			BackCar913StartByUser = false;

		} else {
			mBackCarModule.SetBackCar913Status(false);
			BackTo913View();
			set913Lock(false);//
			mBackCarService.setBcStop(true);
			mBackCarModule.TurnModuleStop();
		}


	}

	public void Backcar_913_Enter(int mode) {
		Log.d(TAG, "Backcar_913_Enter mode = " + mode);
		CheckDigInPowerOnOrOff();
		mBackCarModule.ToModule913CameraOn_Off((byte) 0x01);
		ResetHandleMsg();
		switch (mode) {
		case 1:// backcar io

			Backcar_913_IO_Enter();
			break;
		case 2:// by user
			Backcar_913_USR_Enter();
			break;
		}
	}

	public void Backcar_913_IO_Enter() {
		Log.d(TAG, "Backcar_913_IO_Enter BackCar913StartByUser = "
				+ BackCar913StartByUser);
		// DealBackCar913Messages(MsgType.MSG_REMOVE,MsgType.MSG_BACKCAR_913_STALLD_START,
		// 0);
		ServiceMsgHandlerRemove(MsgType.STALLD_STOP);
		DealBackCar913Messages(MsgType.MSG_REMOVE,
				MsgType.MSG_BACKCAR_913_STOP, 0);
		DealBackCar913Messages(MsgType.MSG_REMOVE,
				MsgType.MSG_BACKCAR_913_STALLD_AUTO_STOP, 0);

		if (BackCar913StartByUser == true) {
			Mode913.lastAngle = Mode913.angle;
			Mode913.lastDirect = Mode913.chioceDire;
			Mode913.lastfocusTag = Mode913.focusTag;
			Mode913.lastPage = Mode913.page;
			mBackCarModule.SetBackCar913Status(true);
			Enter913Video(CAMERA_913_REAR);

		} else {
			// Log.d(TAG," BACKCAR_START_913 BackCar913StartByUser = " +
			// BackCar913StartByUser);
			if (mBackCarService.mIsReversing) { // stllD was in
				mBackCarModule.SetBackCar913Status(true);
				Enter913Video(CAMERA_913_REAR);
				tellModuleStallDStatus(true);
				
			} else {
				willsetMirror();

				if (powerSupplyDelay) {
					DelayLock();
					DealBackCar913Messages(MsgType.MSG_SENGDELAY,
							MsgType.MSG_BACKCAR_913_U_START,
							MsgType.MSG_BACKCAR_913_U_START_TIMEOUT);
				} else	{ 
					setBackCarView(false);
					mBackCarModule.BACKCAR_START_913();
				}
					mBackCarModule.Io913_startVideo();

			}
		}
		setBackCar913StallDState(false);
		FlyBaseListener.focus(FlyUtil.HexToTag(BackCarTag.SET150VIDEO_CID));
	}

	private void Enter913Video(int type) {
		// DealBackCar913Messages(MsgType.MSG_SENG,MsgType.MSG_BACKCAR_913_ENTER_VIDEO,
		// MsgType.MSG_BACKCAR_913_STOP_TIMEOUT);
		Log.d(TAG, "Enter913Video====  " + type);

		mBackCarModule.needFlyBackCarMainView();
		if (type == CAMERA_913_REAR)
			msgHandler
					.sendEmptyMessage(MsgType.MSG_BACKCAR_913_ENTER_REAR_VIDEO);
		else if (type == CAMERA_913_FRONT)
			msgHandler
					.sendEmptyMessage(MsgType.MSG_BACKCAR_913_ENTER_FRONT_VIDEO);

		FlyBaseListener.focus(FlyUtil.HexToTag(BackCarTag.SET150VIDEO_CID));
		Bundle data = new Bundle();
		data.putInt(BackCarTag.INTKEY, type);
		mBackCarService.MakeAndSendMessageWithBundle(0, BackCarTag.STALLENTER,
				data);

		// mBackCarService.MakeAndSendMessageButton(BackCarTag.SET150VIDEO_CID);
	}

	public void Backcar_913_USR_Enter() {
		if (!isDelayLock())
			Backcar_913_Start_ByUser();
	}

	public void Backcar_913_Leave(int mode) {
		Log.d(TAG, "Backcar_913_Leave mode = " + mode);
		switch (mode) {
		case 1:// backcar io
				// BACKCAR_STOP_913();

			ResetHandleMsg();
			DealBackCar913Messages(MsgType.MSG_SENGDELAY,
					MsgType.MSG_BACKCAR_913_STOP, 0);
			// DealBackCar913Messages(MsgType.MSG_REMOVE,MsgType.MSG_BACKCAR_913_STOP,
			// MsgType.MSG_BACKCAR_913_STOP_TIMEOUT);
			// delay 500ms , if stallD have come,cancel; if stallD don't exist,
			// quit;
			// if(mIsReversing) false:destroy
			// remove timer
			break;
		case 2:// by user
			break;
		}
	}

	public void getStallDStatus(boolean bStallD) {
		Log.d(TAG, " getStallD + bStallD " + bStallD + "  mBackCar913StallD "
				+ getBackCar913StallDState());

		if (getBackCar913StallDState() == bStallD) {
			//if (bStallD == false) {
			//	mBackCarService.setBcStop(true);
				set913Lock(false);
		//	}
			return;
		}
		setBackCar913StallDState(bStallD);
		// if BackCar913StartByUser == true; change video; don't setTimer;
		// if BackCar913StartByUser == false; delay 100ms,
		// if mcurStall = D, MayActivityExist, change video or StartActivity ,
		// setTimer for autoDestroy(no action|not D ,cancel);
		// if mcurStall != D, cancle;
		// remove timer
		if (mBackCarService.mgetBackCarMode() != BackCarService.BACKCAR_914)
			return;

		if (getBackCar913StallDState()) {

			DealBackCar913Messages(MsgType.MSG_REMOVE,
					MsgType.MSG_BACKCAR_913_STOP, 0);
			DealBackCar913Messages(MsgType.MSG_REMOVE,
					MsgType.MSG_BACKCAR_913_STALLD_START, 0);

			mBackCarService.setBcStop(true);
			mBackCarModule.SetBackCar913Status(false);
			mBackCarModule.TurnModuleStop();

			if (BackCar913StartByUser == true) {
				mBackCarModule.showSignByUser(true);
				DealBackCar913Messages(MsgType.MSG_SENGDELAY,
						MsgType.MSG_BACKCAR_913_STALLD_START, 0);
				DealBackCar913Messages(MsgType.MSG_REMOVE,
						MsgType.MSG_BACKCAR_913_STALLD_AUTO_STOP, 0);
				DealBackCar913Messages(MsgType.MSG_SENGDELAY,
						MsgType.MSG_BACKCAR_913_STALLD_AUTO_STOP,
						MsgType.MSG_BACKCAR_913_STALLD_AUTO_STOP_TIMEOUT);
			} else {

				if (powerSupplyDelay) {
					DelayLock();
					DealBackCar913Messages(MsgType.MSG_SENGDELAY,
							MsgType.MSG_BACKCAR_913_STALLD_START,
							MsgType.MSG_BACKCAR_913_STALLD_START_TIMEOUT);
				} else
					DealBackCar913Messages(MsgType.MSG_SENGDELAY,
							MsgType.MSG_BACKCAR_913_STALLD_START, 0);
				DealBackCar913Messages(MsgType.MSG_SENGDELAY,
						MsgType.MSG_BACKCAR_913_STALLD_AUTO_STOP,
						MsgType.MSG_BACKCAR_913_STALLD_AUTO_STOP_TIMEOUT);
			}
		} else {
			DealBackCar913Messages(MsgType.MSG_REMOVE,
					MsgType.MSG_BACKCAR_913_STALLD_AUTO_STOP, 0);

			StallDexit();
		}
	}

	public void front913Video() {
		setBackCarView(false);
		mBackCarService.getBackCarView().setBackcarMode(
				BackCarService.BACKCAR_914);
		SetFrontRearCameraCtl((byte) 0x01); // ˳�򲻿ɱ�
		setBackCarView(true); // ˳�򲻿ɱ�
		mBackCarService.getBackCarView().setRearMirror(1);
		// setBackGround();
	}

	public void rear913Video() {
		setBackCarView(false);
		mBackCarService.getBackCarView().setBackcarMode(
				BackCarService.BACKCAR_914);
		SetFrontRearCameraCtl((byte) 0x00);
		setBackCarView(true);
		mBackCarService.getBackCarView().setRearMirror(2);
		// setBackGround();
	}

	public void setBackcarView(BackCarView bv) {
		if (mDigIn != null)
			mDigIn.setBackcarView(bv);

	}

	private void willsetMirror() {
		if (!getBackCar913StallDState()) {
			setMirror(DigIn.MIRROR_H);
			mDigIn.setRearView(DigIn.MIRROR_H);
		} else
			setMirror(DigIn.MIRROR_N);

	}

	private void StallDexit() {
		mBackCarService.setBcStop(false);
		keyCodeBackExit();
	
	}

	public void Backcar_913_Start_ByUser() {
		// start by user
		mBackCarService.setBcStop(false);
		initMode913Param();
		BackCar913StartByUser = true;
		showActivity();
		mBackCarModule.showNoticeView(true);
		mBackCarService.MakeAndSendMessageWithBundle(MsgType.COMMON,
				BackCarTag.STARTBYUSER, null);
		mBackCarService.setBcStop(true);
	}

	public void Remove_BackCar913_StallD_Auto_Stop() {
		if (getBackCar913StallDState())
			DealBackCar913Messages(MsgType.MSG_REMOVE,
					MsgType.MSG_BACKCAR_913_STALLD_AUTO_STOP, 0);
	}

	public boolean getBackCar913StallDState() {

		return mBackCar913StallD;
	}

	public void setBackCar913StallDState(boolean flag) {
		mBackCar913StallD = flag;
		Log.d(TAG, "setBackCar913StallDState " + flag);
	}

	public void tellModuleStallDStatus(boolean status) {
		// Log.i(TAG, "+++++++++++tellModuleStallDStatus+++++++++++++++++++++");
		mBackCarModule.tellmoduleStallDStatus(status);
	}

	void ResetHandleMsg() {
		DealBackCar913Messages(MsgType.MSG_REMOVE,
				MsgType.MSG_BACKCAR_913_U_START, 0);
		DealBackCar913Messages(MsgType.MSG_REMOVE,
				MsgType.MSG_BACKCAR_913_STALLD_START, 0);

	}

	void CheckDigInPowerOnOrOff() {
		if (!mBackCarModule.GetDigInCameraOn_OffStatus())
			powerSupplyDelay = true;
		else
			powerSupplyDelay = false;
	}

	void DelayLock() {
		delayLock = true;
	}

	public void DelayUnLock() {
		delayLock = false;
	}

	boolean isDelayLock() {
		return delayLock;
	}

	public void SetLightLevel(int value) {
		mBackCarModule.ToModule913CameraSetLight((byte) value);
	}

	void SetFrontRearCameraCtl(byte value) {
		Mode913.direct = value;
		mBackCarModule.ToModule913CameraCtl(value);
	}

	public void setSourceRect() {
		mBackCarService.mBackCarService.getBackCarView().setSourceRect();
	}

	public void set913ToOther() {
		Mode913.lastAngle = Mode913.angle;
		Mode913.lastDirect = Mode913.chioceDire;
		Mode913.lastPage = Mode913.page;
		Mode913.lastfocusTag = Mode913.focusTag;

	}

	public boolean is913StartByUser() {
		return BackCar913StartByUser;

	}
  public void set913StartByUser(boolean flag)
  {
	  		BackCar913StartByUser = flag;
  }
	public void keyCodeBackExit() {

		mBackCarModule.backcar913stop("getbackkey");
		
		set913Lock(false);
		mBackCarService.setBcStop(true);
	}

	public void BackTo913View() {
		Mode913.angle = Mode913.lastAngle;
		Mode913.direct = Mode913.lastDirect;
		Mode913.page = Mode913.lastPage;
		Mode913.focusTag = Mode913.lastfocusTag;
		mBackCarService.getBackCarView().setBackcarMode(
				mBackCarService.BACKCAR_914);
		select913Page(Mode913.page, Mode913.direct, Mode913.angle);
	}

	void select913Page(int page, int direct, int angle) {
		mBackCarService.MakeAndSendMessageWithBundle(MsgType.COMMON,
				BackCarTag.BACKTO913, null);

	}

	public void setBackCarView(boolean flag) {
		mBackCarService.getBackCarView().setBackCarView(flag);
	}

	// digin
	public void setBackGround() {

		mBackCarService.getFlyBackCarMainView().mSetFronView();
	}

	public void ShowBgView(int type, boolean flag) {
		Log.i(TAG, "ShowBgView" + type + flag);

		switch (type) {
		case 1:
			if (flag) {
				// Log.d("DDD", "Mode913.direct "+Mode913.direct);
				if (Mode913.direct == 1) // ǰ��
				{
					mBackCarService.getFlyBackCarMainView().mSetTrailMode(1);
					mBackCarModule.ShowUiView(BackCarTag.bg913Front_150, true);
					mBackCarModule.ShowUiView(BackCarTag.bg913Rear_150, false);
					mBackCarModule.ShowUiView(BackCarTag.bgcvb, false);

					tellmoduleRefAngle();
				} else if (Mode913.direct == 0) // ����
				{
					mBackCarService.getFlyBackCarMainView().mSetTrailMode(2);
					mBackCarModule.ShowUiView(BackCarTag.bgcvb, false);
					mBackCarModule.ShowUiView(BackCarTag.bg913Front_150, false);
					mBackCarModule.ShowUiView(BackCarTag.bg913Rear_150, true);
					tellmoduleRefAngle();
				} else {
					mBackCarService.getFlyBackCarMainView().setFronView(false);
				}

			} else
				mBackCarService.getFlyBackCarMainView().setFronView(false);

			break;
		case 2:
			mBackCarModule.ShowUiView(BackCarTag.bgcvb, false);
			mBackCarModule.ShowUiView(BackCarTag.bg913Front_150, false);
			mBackCarModule.ShowUiView(BackCarTag.bg913Rear_150, false);
			break;
		}
	}

	public void tellmoduleRefAngle() {
		mBackCarModule.tellmoduleRefAngle((byte) 0);
	}

	public void Show913_bg_view(int cid, byte pos) {
		mBackCarModule.ChangeUIObjBackground(cid, pos);
	}

	public void Show913_UI_view(int cid, boolean pos) {
		mBackCarModule.ShowUiView(cid, pos);
	}

	public void set913FlyTextObj(int cid, String text) {
		mBackCarModule.showText(cid, text);
	}

	public void set913DiginBackcarService() {
		mDigIn.setBackCarService(this);
	}

	public InputSourceClient getInputClient() {
		return mDigIn.mInputSourceV;
	}

	public void changeFront(int fr, View v) {
		mBackCar913View.changeFront(fr, v);

	}

	public void changeRear(int fr, View v) {
		mBackCar913View.changeRear(fr, v);
	}

	// benz
	public void change_FR_ModeUI(View f, View r, int fr_mode) {
		if (f == null || r == null)
			return;
		switch (fr_mode) {
		case MsgType.FR_MODE:
			mBackCar913View.change_fr_modeUi(f, r);
			break;
		case MsgType.F_MODE:
			mBackCar913View.change_f_modeUi(f, r);
			break;
		case MsgType.R_MODE:
			mBackCar913View.change_r_modeUi(f, r);
			break;
		case MsgType.F_ORIR_MODE:
			mBackCar913View.change_f_modeUi(f, r);
		}
		BenzModule.getInstance(mBackCarService).Benz913ChangeFR_UI(fr_mode);
	}

	// A4L
	public void change_AuDiA4_FR_ModeUI(View f, View r, int fr_mode) {
		boolean changeUi = false;

		if (oldSave_frMode != fr_mode) {
			changeUi = true;
			oldSave_frMode = fr_mode;

		}
		// Log.d("DDD", "change_AuDiA4_FR_ModeUI "+fr_mode);
		switch (fr_mode) {
		case MsgType.FR_MODE:
			mBackCar913View.change_audiA4_fr_modeUi(f, r, changeUi);

			break;
		case MsgType.F_MODE:
			mBackCar913View.change_audiA4_f_modeUi(f, r, changeUi);
			break;
		case MsgType.R_MODE:
			mBackCar913View.change_audiA4_r_modeUi(f, r, changeUi);
			break;
		case MsgType.F_ORIR_MODE:
			mBackCar913View.change_audiA4_f_r_modeUi(f, r, changeUi);
		}
		FlyBaseListener.UIParentViewShow(
				FlyUtil.HexToTag(BackCarTag.Audi_F_MODE), false, false);

	}

	public void change_audiA4_sound_levelUI(int cid, int vuale) {
		mBackCar913View.change_audiA4_sound_levelUI(cid, vuale);
	}

	public void change_audiA4_title_levelUI(int cid, int vuale) {
		mBackCar913View.change_audiA4_title_levelUI(cid, vuale);

	}

	public void ChangeBigAndSmallRadarTextUI(int cid, int vaule) {
		mBackCar913View.ChangeBigAndSmallRadarTextUI(cid, vaule);
	}

	public void change_TextUiColor(String tag, String color) {
		mBackCarService.getFlyRadarView().updateTextUiColor(tag, color);
	}

	public void setMirror(int m) {
		mDigIn.setBackcarView(mBackCarService.getBackCarView());
		mDigIn.msetMirror(m);
	}

	public void DigIn_init(SurfaceHolder holder) {
		Log.i(TAG, "+++DigIn_init");
		mDigIn.setBackcarView(mBackCarService.getBackCarView());
		mDigIn.create();
		mDigIn.DIGIN_Init(holder);
	}

	public void DigIn_deinit() {
		Log.i(TAG, "+++DigIn_deinit");
		mDigIn.DIGIN_Deinit();
		// mBackCarService.getBackCarView().DestroySurfaceView();
	}

	void initMode913Param() {
		Mode913.page = 0;
		Mode913.direct = 0;
		Mode913.angle = 2;
		Mode913.chioceDire = 1;
		Mode913.lastPage = 0;
		Mode913.lastDirect = 0;
		Mode913.lastAngle = 2;
		Mode913.focusTag = null;
		Mode913.lastfocusTag = null;
	}

	public static class Mode913 {
		public static int page = 0; // 0 - video 1 - choice
		public static int direct = 0; // 0 -rear 1-front
		public static int angle = 2; // 2-150 1-180

		public static int chioceDire = 0; // ѡ��ҳ��ڷ��� ǰ 1 ��0
		public static String focusTag = null;
		public static String lastfocusTag = null;
		public static int lastPage = 0;
		public static int lastDirect = 0;
		public static int lastAngle = 2;
	}
}
