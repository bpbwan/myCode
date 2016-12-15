package com.flyaudio.msg;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;


public final class HandleUIMessage extends Handler{
    
	public HandleUIMessage(){

	}
    
	@Override
	public void handleMessage(Message msg) {
		// TODO Auto-generated method stub
		int messageType = msg.arg2;
		byte[] data = (byte[]) msg.obj;
		//Log.d("ana","FlyaudioUIHandlerMessage handleMessage messageType="+messageType);
		//Log.d("ana","handleMessage messageType:"+messageType);
		
		switch(messageType){
		case 0:
			handlerControlUIMessage(msg,data);
			break;
//		case 0x02:
//			handlerOsdMessage(msg,data);
//			break;
		case 0x05://camera is ready
			handlerCameraMessage(msg, data);
			break;
		}
	}
	
	private final void handlerControlUIMessage(Message msg, byte[] data) {
		int len =  msg.what;
		int pageID = (data[4]<<8) | (data[5]);
		if(0x2001 == pageID){
			String ret = "";


		}

	}
	
	//0x05 tall me camera is ready or not
	private void handlerCameraMessage(Message msg, byte[] data) {
		// TODO Auto-generated method stub
		int messageType = data[9] & 0xff;// 参数类型
		switch(messageType){
		case 0:
//			FlyaudioUIService.cameraReady = false;
			break;
		case 1:
//			FlyaudioUIService.cameraReady = true;
			break;
		}
	}

	//0x02 osd
	private void handlerOsdMessage(Message msg, byte[] data) {

	}
	

	
	public int controlID(byte[] data){
		int FlyUIControlID = 
				(int)((data[3]&0xFF)<< 24) 
				| (int)((data[4]&0xFF) << 16)
				| (int)((data[5]&0xFF) << 8) 
				| (data[6])&0xFF;
		
		return FlyUIControlID;
	}

}
