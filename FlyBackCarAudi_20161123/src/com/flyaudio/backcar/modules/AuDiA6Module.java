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



public class AuDiA6Module extends AuDiA4Module {

	String TAG = "AuDiA3Module";
	static AuDiA6Module mAuDiA3Module;
	public boolean LowCarConfig = false;
	
	
	public static AuDiA6Module getInstance(BackCarService Service) {
		if (mAuDiA3Module == null) {
			mAuDiA3Module = new AuDiA6Module(Service);
			setup();

		}
		return mAuDiA3Module;
	}

	public AuDiA6Module(BackCarService Service) {
		super(Service);

	}

	

	
	@Override
	public BaseKeyListener SetOnKeyListener(String mkeyStr, View vm, KeyListenerCallback cb){
		if (mkeyStr.equals(BackCarTag.RADARPAGE913))
						vm.setOnKeyListener(AuDiA6KeyListener.getInstance(cb).pageRadar);
					else if (mkeyStr.equals(BackCarTag.PAGE_CVB_LAYOUT)) {
						vm.setOnKeyListener(AuDiA6KeyListener.getInstance(cb).CVBPage);
					} else
						vm.setOnKeyListener(AuDiA6KeyListener.getInstance(cb));
					vm.setOnFocusChangeListener(AuDiA6KeyListener
							.getInstance(cb));
				return (BaseKeyListener) AuDiA6KeyListener.getInstance(cb);

	};
	
	public void BaseModuleStart(){
		super.BaseModuleStart();
	
	}



	public void Ctr_SmallSetting_ID_method() {
		super.Ctr_SmallSetting_ID_method();

	}
	
	public void Ctr_BigSetting_ID_method() {
		if(LowCarConfig)
			return;
		

		
		if (Not913ModeAndNotStartByUser()) {
			BackCarService.mBackCarService.MakeAndSendMessageWithBundle(
					MsgType.FLYOBJ, BackCarTag.A4_AUDI_NOT_913_SET_CID, null);
			return;
		}
		mBackCarModule.ToModule913RequestFRSound((byte) 0);
		View PageSetting = PluginProxyContext.getInstance().getLayout(
				PageID.PAGE_913A4SETTING);

		if (mBackCarService.getReversing()){
			
			mBackCar913Service.setBackCarView(false); //add test
			
			mBackCarService.getFlyBackCarMainView().LayoutAddViewWithPageID(
					PageSetting, PageID.PAGE_913A4SETTING);
		}else {


			mBackCarService.getFlyFloatRadarView().LayoutAddViewWithPageID(
					PageSetting, PageID.PAGE_913A4SETTING);
		}

		if (isFloatRadarFull()) {

			mBackCarService.getBackCar913Service()
					.ChangeBigAndSmallRadarTextUI(
							BackCarTag.A4_ChangeBigOrSmall_radar, 1);

		} else {

			mBackCarService.getBackCar913Service()
					.ChangeBigAndSmallRadarTextUI(
							BackCarTag.A4_ChangeBigOrSmall_radar, 0);
		}


//		mBackCarService.getBackCar913Service().change_audiA4_sound_levelUI(
//				BackCarTag.A4_AUDI_F_SOUND_VUALE, Front_sound_level);
//
//		mBackCarService.getBackCar913Service().change_audiA4_sound_levelUI(
//				BackCarTag.A4_AUDI_R_SOUND_VUALE, Rear_sound_level);

		FlyBaseListener.focus(FlyUtil
				.HexToTag(BackCarTag.Audi_SELECTBUTTON));
		
		change_AuDiA4_FR_ModeUI(mBackCarService.get913Camera_fr_mode());
	
	}
	
	public void Ctr_Setting_Back(){
		super.Ctr_Setting_Back();
		Log.d("DDDD", "Ctr_Setting_Back");
		if(mBackCarService.getBackCarMode() == mBackCarService.BACKCAR_914
				|| mBackCar913Service.is913StartByUser())
			DigIn913Mode();
	}
	public void Ctr_BackCarView_Layout(int width, int height, int gravity){
			super.Ctr_BackCarView_Layout(width, height, gravity);
	}

	public void reSizeBgView() {
		super.reSizeBgView();
	}
	
}
