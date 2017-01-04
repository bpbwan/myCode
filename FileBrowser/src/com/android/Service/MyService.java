package com.android.Service;

import java.io.FileDescriptor;

import com.android.Service.MyServiceIBinder.Stub;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;

public class MyService extends Service{
	String TAG = "AAAService";

	
	private Messenger FromSecondMessenger = null;
	
	private  Handler  mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			
			switch((int)msg.what){
			case Util.MSG_MESSENGER_REPLYTO:
				Log.d(TAG, "mHandler  get handleMessage   "+msg.what);
				FromSecondMessenger = msg.replyTo;
				
				this.sendEmptyMessageDelayed(Util.MSG_MESSENGER_TEST, 1000);
				break;
			case Util.MSG_MESSENGER_TEST:
				if(FromSecondMessenger != null){

					Message ToSecondService = Message.obtain();
					ToSecondService.what = Util.MSG_MESSENGER_PING_SECOND;
					try {
						FromSecondMessenger.send(ToSecondService);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				this.sendEmptyMessageDelayed(Util.MSG_MESSENGER_TEST, 1000);
				}
				break;
			}
			super.handleMessage(msg);
			
		}
	};
	

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub

		Log.d(TAG, "onBind  2");
//		return mMessenager.getBinder();  //1
		
		return mMyBinder;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		Log.d(TAG, "onCreate  0");
	}
	@Override
	public void onRebind(Intent intent) {
		// TODO Auto-generated method stub
		super.onRebind(intent);
		
		Log.d(TAG, "onRebind 5");
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
	

	
	
	private MyServiceIBinder.Stub mMyBinder = new MyServiceIBinder.Stub() {
		
		@Override
		public int getPid() throws RemoteException {
			// TODO Auto-generated method stub
			Log.d(TAG, "getPid  "+getCallingPid());
			return 0;
		}
		
		@Override
		public void basicTypes(int anInt, long aLong, boolean aBoolean,
				float aFloat, double aDouble, String aString)
				throws RemoteException {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void setMessage(Messenger msg) throws RemoteException {
			// TODO Auto-generated method stub
			
			FromSecondMessenger = msg;
			mHandler.sendEmptyMessageDelayed(Util.MSG_MESSENGER_TEST, 1000);
		}
	};
	
	private Messenger mMessenager = new Messenger(mHandler);
	

}
