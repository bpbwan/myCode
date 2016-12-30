package com.android.flyaudioui.object;

import java.util.HashMap;

import com.android.launcher2.Launcher;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.os.RemoteException;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class FlyUIObj extends ImageView{
	
	protected int mControlID;
	protected HashMap<Integer, Object> mData;
	
	private Context mContext;

	public FlyUIObj(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        mContext = context;
        mData = new HashMap<Integer, Object>();
        String tag = (String) getTag();
        if(tag != null){
            mControlID = Integer.parseInt(tag, 16);
            tag = null;
        }
    }
	 public FlyUIObj(Context c)
	    {
	        super(c);
	    }
    public static void MakeAndSendMessageToModule(int ControlID, byte ControlType, byte[] param){
          Log.i("@@@@@@", "MakeAndSendMessage---ControlID------"+ControlID);
          Log.i("@@@@@@", "MakeAndSendMessage---ControlType------"+ControlType);
          Log.i("@@@@@", ": "+ControlID);
          for (int i = 0; i <param.length; i++) {
              Log.i("launcher", "MakeAndSendMessage---ControlType-------param.length:"+param.length+"-->param["+i+"]="+param[i]);
          }
            int sendbufLen = 9 + param.length;
            byte[] sendbuf = new byte[sendbufLen];
            sendbuf[0] = (byte) 0xff;
            sendbuf[1] = 0x55;
            sendbuf[2] = (byte) (param.length + 6);
            sendbuf[3] = (byte) ((ControlID >> 24) & 0xff);
            sendbuf[4] = (byte) ((ControlID >> 16) & 0xff);
            sendbuf[5] = (byte) ((ControlID >> 8) & 0xff);
            sendbuf[6] = (byte) (ControlID & 0xff);
            sendbuf[7] = ControlType;
            for (int i = 0; i < param.length; i++) {
                sendbuf[8 + i] = param[i];
            }
            //2013.3.28
            Message msg = Message.obtain(null,10);
            Bundle bundle = new Bundle();
            bundle.putByteArray("data", sendbuf);
            bundle.putInt("len", sendbufLen);
            msg.setData(bundle);
            //2013.09.06 kai 解决launcher与Service未连接时会弹框报nullpoint的问题。
            try {
               if (Launcher.app.getMessenger() != null)
				Launcher.app.getMessenger().send(msg);
			else
				Toast.makeText(Launcher.app, "与Service失去连接……",
						Toast.LENGTH_LONG).show();
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        
        //2013.3.28
        
    }
    public void setTouchable(boolean bTouch){
		
	}
    //main method
    public void handleControlUIMessage(byte[] data, int len){
        switch(data[7]){
        case 0:{
            if(data[8] == 0)  setBooleanData(false);
            else setBooleanData(true);
        }
            break;
        case 1:{
            int whichStatus = 
                    (((int)(data[8]&0xFF)<<24)
                    |((int)(data[9]&0xFF)<<16)
                    |((int)(data[10]&0xFF)<<8)
                    |(data[11]&0xFF));
            setIntegerData(whichStatus);
        }
            break;
        case 2:{
            int mParamLen = len - 9;
            String str = "";
            if(mParamLen >= 0 && mParamLen < 255){
                char[] mParam = new char[mParamLen];
                for(int i=0; i<mParamLen;){
                    mParam[i] = (char) (((data[9 + i] & 0xff) << 8) | (data[8 + i]) & 0xff);
                    str = str + Character.toString(mParam[i]);
                    i = i + 2;
                }
                setStringData(str);
            }
        }
            break;
        case (byte)0xFF:{
            int commandLen = len - 9;
            if(commandLen >= 0 && commandLen < 255){
                byte[] command = new byte[commandLen];
                for(int i=0; i<commandLen; i++){
                    command[i] = data[8+i];
                }
                setCommand(command);
            }
        }
            break;
        }
    }
    
    public HashMap<Integer, Object> getData(){
        return this.mData;
    }
    
    //接口
    public void setBooleanData(boolean data){
        mData.put(0, data);
    }
    
    public void setIntegerData(int data){
        mData.put(1, data);
    }
    
    public void setStringData(String data){
        mData.put(2, data);
    }
    
    public void setCommand(byte[] command){
        switch(command[0]){
        case 0x00:
        {
            if(command[1] == 0)
                setDisplay(false);
            else
                setDisplay(true);
        }
        break;
        case 0x0F:
            break;
        }
    }
    
    protected void setDisplay(boolean mVisible){
        if(mVisible) setVisibility(View.VISIBLE);
        else setVisibility(View.GONE);
    }
    
    public int getControlID(){
        return mControlID;
    }
    
    public interface FlyUIObjListener{
        void MakeAndSendMessageToModule(int controlID, byte controlType, byte[] param);
    }

}
