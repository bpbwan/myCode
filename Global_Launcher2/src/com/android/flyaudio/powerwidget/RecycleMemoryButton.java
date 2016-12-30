package com.android.flyaudio.powerwidget;

import com.android.launcher.R;
import com.android.launcher2.Launcher;


import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender.SendIntentException;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class RecycleMemoryButton extends PowerButton {
    private boolean enabled = false;
    private RamInfo mRaminfo;
   
    public RecycleMemoryButton() { mType = BUTTON_MEMORY; }

    @Override
    protected void updateState(Context context) {
        
        if (enabled) {
        	setUI("quick_on","quick_button","power_button_text_color");
            mState = STATE_ENABLED;
            enabled = false;
            Log.d(TAG,"recyclebutton ... on ");
        } else {
        	setUI("quick_off","quick_button","power_button_text_color_d");
           
            mState = STATE_DISABLED;
         
            Log.d(TAG,"recyclebutton ... off ");
        }
    }

    int i = 0;
    int j = 0;
    int saveMemory = 0; 
    int saveTask=0;
    @Override
    protected void toggleState(Context context) {
//        PhoneStatusBar mPhoneStatusBar= new PhoneStatusBar();
        onClickMethod(context);
       
    }
    
   
    protected void onClickMethod(Context context) {
        mContext = context;
        mRaminfo = new RamInfo(context);
        Long usedMemory_start =mRaminfo.getUesdMemory();
//        Log.d(TAG,"usedMemory_start = "+usedMemory_start);
        i = mRaminfo.getRunningAppSize();
        
        killRunnintApp(context);
        enabled = true;
//        Log.d(TAG,"com.android.recycle.memory  before.........sendbroadcast()...");
        Message msg = new Message();
        mHandler.sendMessageDelayed(msg, 2000);
        
        j = mRaminfo.getRunningAppSize();
      
        Log.d(TAG,"i = " + i +" j = " + j);
        
        Long usedMemory_end =mRaminfo.getUesdMemory(); 
        saveMemory = (int)((usedMemory_start -usedMemory_end)/1024);
        saveTask=(i-j);        
        Log.d(TAG,"usedMemory_end = "+usedMemory_end);
        Toast_RecycleMemory(context);
        
        context.sendBroadcast(new Intent().setAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
    }

    protected void   Toast_RecycleMemory(Context context) { 
        String toastText =null;
        if(saveMemory<=0){
           
            toastText =Launcher.flyLauncher.getStringFromSkin(Launcher.skinContext, "toastnone");
            Log.d(TAG,"toastText");
            
        }
        else{
            String temp;
           
            temp =Launcher.flyLauncher.getStringFromSkin(Launcher.skinContext, "toasttext");
            toastText=String.format(temp,saveTask , saveMemory); 
        }
     // LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
      //View toastRoot =   inflater.inflate(R.layout.fly_toast, null);
      
      View toastRoot =Launcher.flyLauncher.getLayoutFromSkin(Launcher.skinContext, "fly_toast");
      Toast toast=new Toast(context);
      toast.setView(toastRoot);
      TextView tv=(TextView)toastRoot.findViewById(Launcher.flyLauncher.getIdFromSkin(Launcher.skinContext, "TextViewInfo", "id"));
      tv.setText(toastText);;
      toast.show();
        //Toast.makeText(context, toastText,Toast.LENGTH_SHORT).show();  
    }
    
    private Context mContext;
    
    Handler mHandler = new Handler(){
   
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            Log.d(TAG,"com.android.recycle.memory  sendbroadcast()...");
            enabled = false;
            mContext.sendBroadcast(new Intent().setAction("com.android.recycle.memory"));
        
        }
    };
    
    
    

    
    
    
    @Override
    protected IntentFilter getBroadcastIntentFilter() {
        Log.d(TAG,"memoryrycle getBroadcastIntentFilter");
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.android.recycle.memory");
        
        return filter;
    }
    
    

    @Override
    protected boolean handleLongClick(Context context) {
        onClickMethod(context);
        return true;
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
        }
        };
    
    
    public void killRunnintApp(Context context) {
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> mRunningProcess = mActivityManager.getRunningAppProcesses();

        for (ActivityManager.RunningAppProcessInfo amProcess : mRunningProcess){
            if(amProcess.processName.startsWith("cld.navi.")
                    || amProcess.processName.equals("com.mapabc.android")
                    || amProcess.processName.equals("android.process.media")
                    || amProcess.processName.equals("cn.flyaudio.media"))
                continue; //过滤不杀的应用
            else {
                if (!amProcess.processName.equals("com.tencent.qqmusic"))
                    mActivityManager.killBackgroundProcesses(amProcess.processName);
                else
                    forceKillApp(mActivityManager, amProcess.processName); //强制关闭进程
            }
            Log.d(TAG, amProcess.processName);
        }
        
    }

    //强制关闭进程，这个方法需要添加android.permission.FORCE_STOP_PACKAGES权限，及加入系统进程 android:sharedUserId="android.uid.system"
    public void forceKillApp(ActivityManager am, String packageName) {
        Method forceStopPackage = null;
        try {
            forceStopPackage = am.getClass().getDeclaredMethod("forceStopPackage", String.class);
            forceStopPackage.setAccessible(true);  
            forceStopPackage.invoke(am, packageName);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }  
    }
    
}





