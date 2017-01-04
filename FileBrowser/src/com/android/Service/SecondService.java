package com.android.Service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

public class SecondService  extends Service{
	String TAG = "AAAClient";
	private static SecondService mservice = null;
	
	public static SecondService getInstance(){
		return mservice;
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		
		Log.d(TAG, "onBind  2");
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		Log.d(TAG, "onCreate  0");
		
		SecondServiceCon  sc = new SecondServiceCon();
		Intent service = new Intent("com.android.Service.MYSERVICE");
		boolean result = getApplicationContext().bindService(service, sc, BIND_AUTO_CREATE);
		
		Log.d(TAG, "bind service "+result);
		mservice = this;
	}
	
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		
		Log.d(TAG, "onStartCommand  1");


		return super.onStartCommand(intent, flags, startId);
		
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		Log.d(TAG, "onDestroy  3");
		super.onDestroy();
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onUnbind  4");
		return super.onUnbind(intent);
	}

	
	
private Messenger mMessenger = null;
private MyServiceIBinder MSB;
private class SecondServiceCon implements ServiceConnection{

	@Override
	public void onServiceConnected(ComponentName arg0, IBinder arg1) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onServiceConnected 0  "+arg1);
		MSB = MyServiceIBinder.Stub.asInterface(arg1);
		mMessenger = new Messenger(arg1);
		
		try {

//			Message msg = new Message();
//			msg.what = Util.MSG_MESSENGER_REPLYTO;
//			msg.replyTo = replyMessenger;
//			mMessenger.send(msg);
			
			Log.d(TAG, "onServiceConnected    "+MSB.getPid());
			MSB.setMessage(replyMessenger);
			
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private Handler  mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			
			
			Log.d(TAG, "second mHandler  get handleMessage   "+msg.what);
			super.handleMessage(msg);
			
		}
	};
	
	private Messenger replyMessenger = new Messenger(mHandler);
	
	@Override
	public void onServiceDisconnected(ComponentName arg0) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onServiceDisconnected 1 "+arg0);
	}
	
}



}
