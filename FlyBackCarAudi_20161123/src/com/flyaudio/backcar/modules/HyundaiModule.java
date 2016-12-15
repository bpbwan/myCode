package com.flyaudio.backcar.modules;

import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.os.Messenger;
import android.util.Log;
import com.flyaudio.backcar913.*;
import com.flyaudio.backcar.*;
import com.flyaudio.globaldefine.PageID;

import com.flyaudio.backcar.BackCarModule;
import com.flyaudio.backcar.BackCarService;
import com.flyaudio.backcar913.BackCar913Service;
import com.flyaudio.globaldefine.FlyaudioIntent;
import com.flyaudio.globaldefine.FlyaudioProperties;
import com.flyaudio.globaldefine.PageID;
import com.flyaudio.globaldefine.UIAction;
import com.flyaudio.msg.HandleServiceMsg;
import android.os.Message;
import android.os.Handler;
import android.os.Messenger;
import android.os.Bundle;
import android.os.SystemProperties;
import com.flyaudio.backcar.animator.*;
import android.view.View;
import com.xdroid.animation.anim.TelescopicAnimation.TelescopicTargetCallback;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import com.flyaudio.backcar.util.*;
import com.flyaudio.globaldefine.ForeGround;
import com.autochips.backcar.BackCar;
import com.flyaudio.globaldefine.FlyaudioProperties;

public class HyundaiModule extends G6zModule {

	String TAG = "HyundaiModule";
	static HyundaiModule mHyundaiModule;

	public static HyundaiModule getInstance(BackCarService Service) {
		if (mHyundaiModule == null) {
			mHyundaiModule = new HyundaiModule(Service);
		}
		return mHyundaiModule;
	}

	public HyundaiModule(BackCarService Service) {
		super(Service);

	}

	public void BaseModulepreStart() {
		super.BaseModulepreStart();
		mBackCarService.showHYUNDAIEasyTouch(false); // 现代车的坑 悬浮框优秀级比倒车高
	}

	public void BaseModulepreStop() {
		super.BaseModulepreStop();
		mBackCarService.showHYUNDAIEasyTouch(true);
	}

	@Override
	public void SetColor_BackCar() {
		BackCar.SetBrightness(56); // 0-100
		BackCar.BC_SetHueLevel(54); // 35-135
		BackCar.BC_SetSaturationLevel(62); // 0-100
		BackCar.BC_SetContrastLevel(39); // 0-100
	}

	public void showSetAuxBotton(boolean flag) {
		if (!AuxLine_show) // 没有辅助线显示就不显示辅助按钮
			return;
		boolean needShow = mBackCarService.getFlyBackCarMainView()
				.isUIViewDisable(BackCarTag.Notice_STR);
		mBackCarService.DealBackCarMessages(MsgType.MSG_REMOVE,
				MsgType.MSG_STOPNOTICE, 0);
		mBackCarModule.showNoticeView(!needShow);
		mBackCarService.DealBackCarMessages(MsgType.MSG_SENGDELAY,
				MsgType.MSG_STOPNOTICE, MsgType.STOPNOTICE_TIMEOUT);

	}

}
