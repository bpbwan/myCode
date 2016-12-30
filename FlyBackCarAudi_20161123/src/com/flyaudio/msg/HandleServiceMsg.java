package com.flyaudio.msg;

import com.flyaudio.backcar.BackCarService;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class HandleServiceMsg extends Handler {

    private final int JUMP_PAGE = 221;
    private final int UPDATE_UI = 222;
    
    private static final String tag = "BackCarMsg";
    private BackCarService mBackCarService;
    private HandleJumpPageMsg mHandJumpPage;
    private HandleUIMessage mUIHandler;


    private static ChildThread mChildThread = null;
    public HandleJumpPageMsg getmHandJumpPage() {
        if (mHandJumpPage != null)
            Log.d(tag, "mHandJumpPage != null");
        return mHandJumpPage;
    }

    public HandleServiceMsg(BackCarService mService) {
    	mBackCarService = mService;
        mHandJumpPage = new HandleJumpPageMsg(mBackCarService);
        mUIHandler = new HandleUIMessage();
        
//        if( mChildThread != null){
//        	mChildThread.mChildHandler. getLooper().quit();
//        }
//        mChildThread = new ChildThread();
//        mChildThread.start();
    }

    @Override
    public void handleMessage(Message msg) {
        // TODO Auto-generated method stub
    	//Log.d(tag, "handleMessage" + msg.what);

        switch (msg.what) {
        case JUMP_PAGE:
        	
            int pageid = msg.arg1;
            pageid &= 0xFFFF;
 //           Log.d(tag, "JUMP_PAGE " + String.format("%8x", pageid));
            JumpPageProc(pageid);
            
            break;
        case UPDATE_UI:
        	
            Bundle bundle = msg.getData();
            int len = bundle.getInt("len");
            int type = bundle.getInt("type");
            byte[] data = bundle.getByteArray("data");
            analysisData(data, len, type);
        
//            if(mChildThread.mChildHandler != null){
//            	Message msg1 = mChildThread.mChildHandler.obtainMessage();
//            	msg1.copyFrom(msg);
//            	mChildThread.mChildHandler.sendMessage(msg1);
//            }
            break;

        }
    }

    private void JumpPageProc(int pageid) {
        // TODO Auto-generated method stub
        mHandJumpPage.JumpPage(pageid);
    }


    public void analysisData(byte[] data, int len, int messageType) {
        // TODO Auto-generated method stub.
        if (len > 0) {
        	//Log.d("backcarMsg", " analysisData ############## " + HandleServiceMsg.bytes2HexString( data));
     	if(mBackCarService.getBaseModule().DEBUG_L2)
     		Log.d(tag, "UPDATE_UI :" + bytes2HexString(data) );
		
        	if( true == AnalyMsgFromModule(data, len, messageType)){
        		byte param[] = new byte[len - 8];
        		System.arraycopy(data, 8, param, 0, param.length);
        		mBackCarService.getModuleMsg(param);
        		return;
        	}
     	if(mBackCarService.needToDealRadar&&len>11){
			data[11] = DealBenzRadar(data);
     		}
       		mBackCarService.getUIMsgSaveCenter().handleControlUIMessage(data,len);
        	mBackCarService.getFlyBackCarMainView().updateUI(data,len,messageType);
			mBackCarService.getFlyRadarView().updateUI(data,len,messageType);
			mBackCarService.getFlyFloatRadarView().updateUI(data,len,messageType);

        }
    }
    
    public boolean AnalyMsgFromModule(byte[] data, int len, int messageType){
    	//0x71000
    	if(len < 9)
    		return false;
    	int FlyUIControlID = (int) ((data[3] & 0xFF) << 24)
                | (int) ((data[4] & 0xFF) << 16)
                | (int) ((data[5] & 0xFF) << 8) | (data[6]) & 0xFF;
	
        String controlID = String.format("%08x", FlyUIControlID);


        if(FlyUIControlID == 462848 && data[7] == (byte)0xFF){	  //0x71000
        	return true;
        }
        
        return false;
    }
    
    public byte DealBenzRadar(byte data[])
   {
       int FlyUIControlID = (int) ((data[3] & 0xFF) << 24) | (int) ((data[4] & 0xFF) << 16)       
      | (int) ((data[5] & 0xFF) << 8) | (data[6]) & 0xFF;     	
	if(FlyUIControlID>=459041&&FlyUIControlID<=459051)//0x00070121  ~0x007012b
	{			
		if(FlyUIControlID!=459043&&FlyUIControlID!=459046&&FlyUIControlID!=459049)
			{
			//Log.i("BBB"," FlyUIControlID "+FlyUIControlID+"data "+data[11]);
			switch(FlyUIControlID)
			{
			case 459041:  //0x70121
			case 459045:	//0x70125
							if(data[11]>=10)			
								data[11] = (byte)0x00;		
							else if(data[11]>=0&&data[11]<=3) //3	//3		
								data[11] = (byte)0x01;		
								else if(data[11]>3&&data[11]<10) //10			
									data[11] = (byte)0x02;		
					break;
			case 459047:			//0x70127
			case 459051:			//0x7012b
						if(data[11]>=14)			
						data[11] = (byte)0x00;		
						else if(data[11]>=0&&data[11]<=3) //3	//3		
						data[11] = (byte)0x01;		
						else if(data[11]>3&&data[11]<14) //16			
						data[11] = (byte)0x02;		
					break;
			}
		}   	
	}
	return data[11];
   }
	
    public void JumpPagetest(int pageid) {
        // TODO Auto-generated method stub
        mHandJumpPage.JumpPage(pageid);
    }
    
    public static String bytes2HexString(byte[] b) {  
        String ret = "";  
        for (int i = 0; i < b.length; i++) {  
         String hex = Integer.toHexString(b[ i ] & 0xFF);  
         if (hex.length() == 1) {  
          hex = '0' + hex;  
         }  
         ret += hex.toUpperCase();  
        }  
        return ret;  
      } 
    
    
    class ChildThread extends Thread {
    	 
        private static final String CHILD_TAG = "ChildThread";
        public Handler mChildHandler = null;
        Looper myLooper =null;
        public void run() {
            this.setName("ChildThread");
 
            //初始化消息循环队列，需要在Handler创建之前
          
            Looper.prepare();
            myLooper = Looper.myLooper();
            Log.d("DDDDDD", "     "+myLooper);
            mChildHandler = new Handler(myLooper) {
                @Override
                public void handleMessage(Message msg) {
                  Bundle bundle = msg.getData();
                  int len = bundle.getInt("len");
                  int type = bundle.getInt("type");
                  byte[] data = bundle.getByteArray("data");
                  analysisData(data, len, type);
                }
 
            };

            //启动子线程消息循环队列
            Looper.loop();
        }
    }
}
