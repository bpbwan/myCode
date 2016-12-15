package com.flyaudio.backcar.modules;

import java.util.Locale;















import com.flyaudio.backcar913.*;
import com.flyaudio.backcar.*;
import com.flyaudio.globaldefine.PageID;
import com.flyaudio.backcar.BackCarModule;
import com.flyaudio.backcar.BackCarService;
import com.flyaudio.backcar.MsgType;
import com.flyaudio.backcar.PluginProxyContext;
import com.flyaudio.backcar913.BackCar913Service;
import com.flyaudio.globaldefine.FlyaudioIntent;
import com.flyaudio.globaldefine.FlyaudioProperties;
import com.flyaudio.globaldefine.PageID;
import com.flyaudio.globaldefine.UIAction;
import com.flyaudio.msg.HandleServiceMsg;
import com.flyaudio.backcar.animator.*;
import com.xdroid.animation.anim.TelescopicAnimation.TelescopicTargetCallback;
import com.flyaudio.backcar.util.*;
import com.flyaudio.globaldefine.ForeGround;
import com.autochips.backcar.BackCar;
import com.flyaudio.keyevent.BaseKeyListener.KeyListenerCallback;
import com.flyaudio.keyevent.*;

import android.os.Message;
import android.os.Handler;
import android.os.Messenger;
import android.os.Bundle;
import android.os.SystemProperties;
import android.view.WindowManager;
import android.view.View;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.Context;
import android.content.Intent;
import android.os.Messenger;
import android.util.Log;
import android.view.Gravity;
import android.graphics.PixelFormat;



public class AuDiQ3Module extends AuDiA3Module {

	String TAG = "BackCarAuDiQ3Module";
	static AuDiQ3Module mAuDiQ3Module;
	public boolean LowCarConfig = false;

	
	public static AuDiQ3Module getInstance(BackCarService Service) {
		if (mAuDiQ3Module == null) {
			mAuDiQ3Module = new AuDiQ3Module(Service);
			setup();

		}
		return mAuDiQ3Module;
	}

	public AuDiQ3Module(BackCarService Service) {
		super(Service);
	}

	
	

	
	@Override
	public BaseKeyListener SetOnKeyListener(String mkeyStr, View vm, KeyListenerCallback cb){
	
		if (mkeyStr.equals(BackCarTag.RADARPAGE913))
						vm.setOnKeyListener(AuDiQ3KeyListener.getInstance(cb).pageRadar);
					else if (mkeyStr.equals(BackCarTag.PAGE_CVB_LAYOUT)) {
						vm.setOnKeyListener(AuDiQ3KeyListener.getInstance(cb).CVBPage);
					} else
						vm.setOnKeyListener(AuDiQ3KeyListener.getInstance(cb));
					vm.setOnFocusChangeListener(AuDiQ3KeyListener
							.getInstance(cb));
				return (BaseKeyListener) AuDiQ3KeyListener.getInstance(cb);

	};
	

	private void NeedChangeEdgeRadar(int radar_cid, int edge_radar_bg,   int cmd){
		switch(cmd){
		case 0x0E:
			if(edge_radar_bg >0){
				mBackCarModule.ChangeMultiUIObjBackground(radar_cid, (byte)0);
				mBackCarModule.ShowUiView(edge_radar_bg, false);
			}
			break;
		case 0x00:
			if(edge_radar_bg >0) {
				mBackCarModule.ShowUiView(edge_radar_bg, true);
				}
			break;
		case 0x01:
			if(edge_radar_bg >0)
				mBackCarModule.ShowUiView(edge_radar_bg, true);
				ChangeMultiUIObjBackground(radar_cid, 0);
			break;
		case 0x03:
			if(edge_radar_bg >0)
				mBackCarModule.ShowUiView(edge_radar_bg, true);
			ChangeMultiUIObjBackground(radar_cid, 1);
			break;
		}
	}
	
	
	//radar deal
	@Override
	public void EdgeRadarDealWithData(byte[] paramer){
		if(DEBUG){
			String ss = "";
			for(int i = 0; i<paramer.length; i++)
				ss = ss+paramer[i];
			Log.d("DDDD", " EdgeRadarDealWithData "+paramer.length+"  "+ss);
//		注： 0x0E 为隐藏  0x00 为显示透明图  0x01显示白色
//	     0x03 显示红色
		
		}
		if(!mBackCarService.getFlyFloatRadarView().isShowing()){
			NeedChangeEdgeRadar(0x00070121, 0, paramer[1]);
			NeedChangeEdgeRadar(0x00070122, 0, paramer[2]);
			NeedChangeEdgeRadar(0x00070124, 0, paramer[3]);
			NeedChangeEdgeRadar(0x00070125, 0, paramer[4]);
			NeedChangeEdgeRadar(0x00070127, 0, paramer[5]);
			NeedChangeEdgeRadar(0x00070128, 0, paramer[6]);
			NeedChangeEdgeRadar(0x0007012a, 0, paramer[7]);
			NeedChangeEdgeRadar(0x0007012b, 0, paramer[8]);
			
			if(paramer.length > 15){
			NeedChangeEdgeRadar(0x00070221, 0x00070421, paramer[9]);
			NeedChangeEdgeRadar(0x00070222, 0x00070422, paramer[10]);
			NeedChangeEdgeRadar(0x00070224, 0x00070424, paramer[11]);
			NeedChangeEdgeRadar(0x00070225, 0x00070425, paramer[12]);
			NeedChangeEdgeRadar(0x00070227, 0x00070427, paramer[13]);
			NeedChangeEdgeRadar(0x00070228, 0x00070428, paramer[14]);
			NeedChangeEdgeRadar(0x0007022a, 0x0007042a, paramer[15]);
			NeedChangeEdgeRadar(0x0007022b, 0x0007042b, paramer[16]);
			}
		}else {
			NeedChangeEdgeRadar(0x00070021, 0, paramer[1]);
			NeedChangeEdgeRadar(0x00070022, 0, paramer[2]);
			NeedChangeEdgeRadar(0x00070024, 0, paramer[3]);
			NeedChangeEdgeRadar(0x00070025, 0, paramer[4]);
			NeedChangeEdgeRadar(0x00070027, 0, paramer[5]);
			NeedChangeEdgeRadar(0x00070028, 0, paramer[6]);
			NeedChangeEdgeRadar(0x0007002a, 0, paramer[7]);
			NeedChangeEdgeRadar(0x0007002b, 0, paramer[8]);
			
			if(paramer.length > 15){
			NeedChangeEdgeRadar(0x00070321, 0x00070421, paramer[9]);
			NeedChangeEdgeRadar(0x00070322, 0x00070422, paramer[10]);
			NeedChangeEdgeRadar(0x00070324, 0x00070424, paramer[11]);
			NeedChangeEdgeRadar(0x00070325, 0x00070425, paramer[12]);
			NeedChangeEdgeRadar(0x00070327, 0x00070427, paramer[13]);
			NeedChangeEdgeRadar(0x00070328, 0x00070428, paramer[14]);
			NeedChangeEdgeRadar(0x0007032a, 0x0007042a, paramer[15]);
			NeedChangeEdgeRadar(0x0007032b, 0x0007042b, paramer[16]);
			}
			
		}

	}


	public void onStopDelay(){
		mBackCarService.DealBackCarMessages( MsgType.MSG_SENGDELAY, MsgType.MSG_ONSTOP_DELAY,
				MsgType.MSG_ONSTOP_DELAY_TIMEOUT);
		
	}
	public void onStopDelayCancel(){
		mBackCarService.DealBackCarMessages( MsgType.MSG_REMOVE, MsgType.MSG_ONSTOP_DELAY,
				0);
	}
	

	
	


	

	

	
	
	
	

	
	

}
