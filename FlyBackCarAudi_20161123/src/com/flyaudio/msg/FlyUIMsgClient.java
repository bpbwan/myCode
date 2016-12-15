package com.flyaudio.msg;
//FlyaudioUI 接收消息的客户端
/*   接收的消息包括
 * 1. 来自UIService的消息 通过Messager传输
 * 2. 来自OSDService的消息 广播接收器接收
 * 3. 来自系统或者APP的状态通知 Phone Navigation Contacts SystemUI等发来的消息 
 * */
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.flyaudio.backcar.BackCarService;
import com.flyaudio.msg.HandleServiceMsg;

public class FlyUIMsgClient {
	private final int REGISTER_UIACTIVITY_CLIENT = 220;
	private HandleServiceMsg mServiceMsgHandler;
	private BackCarService mBackCarService;
	public FlyUIMsgClient(BackCarService mService){
		mBackCarService = mService;
		Context realContext = mBackCarService.getApplicationContext();
		//ContextWrapper cw = new ContextWrapper(realActivity);
		mServiceMsgHandler = new HandleServiceMsg(mBackCarService);
		mMessenger = new Messenger(mServiceMsgHandler);
		realContext.bindService(new Intent("cn.flyaudio.android.flyaudioservice.BackCarService"),
				mConnection, Context.BIND_AUTO_CREATE);
		Log.d("BackCarClient", "----FlyUIMsgClient-----");
		initMsgReceiver();
	}
	public HandleServiceMsg getHandleServiceMsg(){
		return mServiceMsgHandler;
	}
	private void initMsgReceiver(){

	}
	
	private void unInitMsgReceiver(){

	}

	/*
	 * 来自UIService的消息 通过Messager传输
	 * */
	
	private Messenger mService = null;
	private Messenger mMessenger = null;
	
	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			mService = new Messenger(service);
			mBackCarService.setmService(mService);
			try {
				Message msg = Message.obtain(null,REGISTER_UIACTIVITY_CLIENT);
				msg.replyTo = mMessenger;
				mService.send(msg);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void onServiceDisconnected(ComponentName className) {
			// This is called when the connection with the service has been
			// unexpectedly disconnected - process crashed.
			Log.d("BackCarClient", "--------onServiceDisconnected-------");
			if (mService == null) {
				try {
					Message msg = Message.obtain(null,REGISTER_UIACTIVITY_CLIENT);
					msg.replyTo = mMessenger;
					mService.send(msg);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	};



}
