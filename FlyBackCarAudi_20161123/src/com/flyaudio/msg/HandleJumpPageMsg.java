package com.flyaudio.msg;


import com.flyaudio.globaldefine.FlyaudioIntent;
import com.flyaudio.backcar.BackCarService;
import com.flyaudio.backcar.modules.BaseModule;
import com.flyaudio.msg.HandleJumpPageMsg.JumpPageThread;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;



public class HandleJumpPageMsg {

    private static final String tag = "BackCarJumpPage";

    private JumpPageThread mJumpPageThread;
    private BackCarService mBackCarService;

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
        	threadJumpPage(msg.what);
        };
    };
	
    public HandleJumpPageMsg(BackCarService mService) {
    	mBackCarService = mService;
        mJumpPageThread = new JumpPageThread();
        mJumpPageThread.start();
    }
    
    public void JumpPage(int pageID)
    {
    	//Log.d(tag, "JumpPage pageID = " + pageID);
    	mJumpPageThread.jumpNewPage(pageID);
    }

    
    public void threadJumpPage(int pageID) {
        Log.d(tag, "threadJumpPage pageID = " + Integer.toHexString(pageID).toUpperCase());

        switch (pageID) {
 
        //case 0x000F:// home
            //jumpToActivity(FlyaudioAction.ACTION_START_HOME,
            //        Intent.CATEGORY_HOME, Intent.FLAG_ACTIVITY_NEW_TASK);
        //    break;

       // case 0xFF00:
            // FinishActivity
        	//暂时以此去除view；
        	
        //    break;
        case 0x0700:
        case 0x0701:
        	break;
        case 0x0702:
        	tell_UIThread_to_removeRadarView(pageID);
        	break;
        case 0x704: //not right vdo 
        	BaseModule.getBaseModule().setRightVdoStatus(false);
        	break;
        case 0x705: // in right vdo
        	BaseModule.getBaseModule().setRightVdoStatus(true);
        	break;
        default:// common pages

            if (FlyaudioIntent.PageMap.containsKey(pageID)) {
            	tell_UIThread_to_setContentView(pageID);
            } else {
                Log.d(tag, "Can't find page:" + Integer.toHexString(pageID).toUpperCase());
                tell_UIThread_to_setContentView(0);
                
            }
        }
    }
	
    private void tell_UIThread_to_setContentView(int pageID) {
 	
    	mBackCarService.resetContentView(pageID);
    }
    
    private void tell_UIThread_to_removeRadarView(int pageID)
    {
    	mBackCarService.getBackCarModule().RemoveRadarViewbyID(pageID);
    }

	private void tell_UIThread_to_changeExtraPageView(int pageID){
		mBackCarService.getBackCarModule().changeExtraPageView(pageID);
	}



	
    class JumpPageThread extends Thread {

        private int pageid = -1;

        @Override
        public void run() {
            // TODO Auto-generated method stub
            while (true) {
                Message msg = Message.obtain();
                try {
                    synchronized (this) {
                        while (pageid == -1) {
                            wait();
                        }
                        //Log.d(tag, "pageID wait = " + Integer.toHexString(pageid).toUpperCase());
                        msg.what = pageid;
                        pageid = -1;
                    }
                    handler.sendMessage(msg);
                    sleep(300);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        public void jumpNewPage(int pageid) {
            synchronized (this) {
                this.pageid = pageid;
                //Log.d(tag, "pageID notify = " + Integer.toHexString(pageid).toUpperCase());
                notify();
            }
        }
    }
}
