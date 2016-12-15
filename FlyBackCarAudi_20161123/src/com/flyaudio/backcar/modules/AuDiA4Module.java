package com.flyaudio.backcar.modules;

import java.util.Locale;
import java.util.HashMap;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import android.content.Intent;
import android.os.Messenger;
import android.util.Log;

import com.flyaudio.backcar913.*;
import com.flyaudio.backcar.*;
import com.flyaudio.globaldefine.PageID;
import com.flyaudio.backcar.BackCarModule;
import com.flyaudio.backcar.BackCarService;
import com.flyaudio.backcar.MsgType;
import com.flyaudio.backcar913.BackCar913Service;
import com.flyaudio.globaldefine.FlyaudioIntent;
import com.flyaudio.globaldefine.FlyaudioProperties;
import com.flyaudio.globaldefine.PageID;
import com.flyaudio.globaldefine.UIAction;
import com.flyaudio.msg.HandleServiceMsg;

import android.os.HandlerThread;
import android.os.Message;
import android.os.Handler;
import android.os.Messenger;
import android.os.Bundle;
import android.os.SystemProperties;

import com.flyaudio.backcar.animator.*;

import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import com.xdroid.animation.anim.TelescopicAnimation.TelescopicTargetCallback;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.flyaudio.backcar.util.*;
import com.flyaudio.globaldefine.ForeGround;
import com.autochips.backcar.BackCar;
import com.flyaudio.xml.XMLTool;
import com.flyaudio.xml.RadarChangeMapXMLParser;
import com.flyaudio.keyevent.BaseKeyListener.KeyListenerCallback;
import com.flyaudio.keyevent.*;

public class AuDiA4Module extends BaseModule {

	public String TAG = "DDDAuDiA4Module";
	static AuDiA4Module mAuDiA4Module;
	public String TrackBgTag;
	ParamToJni mParamToJni = new ParamToJni();
	BackCarViewChange mBackCarViewChange = new BackCarViewChange();

	protected final static String F_S_Str = "f_sound_level";
	protected final static String R_S_Str = "r_sound_level";
	protected final static String F_T_Str = "f_tone_level";
	protected final static String R_T_Str = "r_tone_level";
	protected final static String Floart_Radar_Scale = "f_radar_scale";

	protected static int Front_sound_level = 1;
	protected static int Rear_sound_level = 1;
	protected static int Front_tone_level = 1;
	protected static int Rear_tone_level = 1;
	protected static float FloatRadarScale = 0.5f;

	protected String radarMapFileName = "frontradarmap";
	protected String radarMapFileName2 = "rearradarmap";

	protected HashMap<Integer, HashMap<String, Integer>> frontRadarChangeMap;
	protected HashMap<Integer, HashMap<String, Integer>> rearRadarChangeMap;
	protected byte[] lock = new byte[0];

	protected int RearTrackLevel = -1;
	protected int FrontTrackLevel = -1;
	protected int RadarAngle = 0;

	public static AuDiA4Module getInstance(BackCarService Service) {
		if (mAuDiA4Module == null) {
			mAuDiA4Module = new AuDiA4Module(Service);
			setup();

		}
		return mAuDiA4Module;
	}

	public AuDiA4Module(BackCarService Service) {
		super(Service);
		loadRadarChangeMapFromXML();
	}

	public static void setup() {

		Front_sound_level = mSharedPreferences.getInt(F_S_Str, 1);
		Rear_sound_level = mSharedPreferences.getInt(R_S_Str, 1);
		Front_tone_level = mSharedPreferences.getInt(F_T_Str, 1);
		Rear_tone_level = mSharedPreferences.getInt(R_T_Str, 1);
		FloatRadarScale = mSharedPreferences.getFloat(Floart_Radar_Scale, 0.5f);

	}

	@Override
	public void initFr_mode(int value) {
		switch (value) {
		case MsgType.FR_MODE:
			mBackCarService.MakeAndSendMessageButton(BackCarTag.Audi_FR_MODE);
			break;
		case MsgType.F_MODE:
			mBackCarService.MakeAndSendMessageButton(BackCarTag.Audi_F_MODE);
			break;
		case MsgType.R_MODE:
			mBackCarService.MakeAndSendMessageButton(BackCarTag.Audi_R_MODE);
			break;
		case MsgType.F_ORIR_MODE:
			mBackCarService.MakeAndSendMessageButton(BackCarTag.Audi_F_R_MODE);

		}
	}

	@Override
	public void DealPowerOff() {
		super.DealPowerOff();
		removeFloatRadar(1);

	}

	public void sendMessageToModule(Message msg) {
		myMsgHandler.sendMessage(msg);

	}

	public void BaseModulepreStart() {
		Log.d(TAG, "BaseModulepreStart");
		mBackCarService.showFloatApp(false, 0);
		sendBroadcastToSystemUI("com.flyaudio.backcar_0",
				"info_icon_backcar.png", "", 1);
		Start_InitFR_ModeUI(mBackCarService.get913Camera_fr_mode());
		if (!GetPowerState())
			BLACKPAGESHOW();
		getGLTrackHelper().getGlview().setDraw(false);

	}

	public void BaseModulepreStop() {
		Log.d(TAG, "BaseModulepreStop");
		BeforeSystemExit();
		mBackCarService.getFlyBackCarMainView().LayoutRemoveViewWithPageID(
				PageID.PAGE_913A4SETTING);
		getGLTrackHelper().getGlview().setDraw(false);

	}

	@Override
	public void BLACKPAGESHOW() {
		DealBackCarMessages(MsgType.MSG_REMOVE, MsgType.SHOW_TRACKBG, null, 0);
		Message msg = Message.obtain();
		msg.what = MsgType.SHOW_TRACKBG;
		DealBackCarMessages(MsgType.MSG_SENGDELAY, MsgType.SHOW_TRACKBG, msg,
				MsgType.BLACKPAGE_REMOVE_TIMEOUT);
		Log.d(TAG, "BLACKPAGESHOW ");
		mBackCar913Service.ShowBgView(1, false); // not show guiji
		mBackCarModule.ShowUiView(BackCarTag.A4_SMALL_CAR_BG, true);
//  xiao ping bu yao heiye
//		if (IsCVBVdoMode())
//			mBackCar913Service.setBackCarView(false);
//		super.BLACKPAGESHOW();

	}

	@Override
	public void BLACKPAGEHIDE() {

		super.BLACKPAGEHIDE();
		mBackCarModule.ShowUiView(BackCarTag.A4_SMALL_CAR_BG, false);
	}

	@Override
	public void SetColor_BackCar() {
		BackCar.SetBrightness(65);
		BackCar.BC_SetHueLevel(64);
		BackCar.BC_SetSaturationLevel(157);

	}

	@Override
	public void SetColor_ARM2() {
		BackCar.BC_SetHueLevel(48); // 48
		BackCar.BC_SetSaturationLevel(mBackCarService.Arm2_Sta); // 52

	}

	@Override
	public void removeMessageToModule(int what, int time) {
		DealBackCarMessages(MsgType.MSG_REMOVE, what, null, time);

	}

	public void DealBackCarMessages(int DealMessages, int what,
			Message iMsgType, int time) {
		if (DealMessages == MsgType.MSG_SENG) {
			myMsgHandler.sendMessage(iMsgType);
		}
		if (DealMessages == MsgType.MSG_SENGDELAY) {
			myMsgHandler.sendMessageDelayed(iMsgType, time);
		}
		if (DealMessages == MsgType.MSG_REMOVE) {
			myMsgHandler.removeMessages(what);
		}
	}

	@Override
	public void FloatRadarExit(int msg) {

		if (msg == SENDFLOAT_CLOSE)
			mBackCarModule.ToModule913Close_SYS((byte) 0);

	}

	@Override
	public void DealWithTrackAngle(Bundle data) {
		DealWithBackCarAllStall(data);
	}

	@Override
	public void InitFrontRearDeviceUI(int fr_mode) {
		mBackCar913Service.change_AuDiA4_FR_ModeUI(null, null, fr_mode);

	}

	int trunToTrackLevel(int data) {
		return (int) (0.189 * (data - 50) + 1); // (data-50)*14/80+1; 0.175
												// (data-50)*14/74+1; 0.189
	}

	public int GetChangeMultiRadarUi_(int mControlID, int level) {
		if (frontRadarChangeMap != null) {
			int ret = 0;
			int value = RadarAngle;
			HashMap<String, Integer> tmp = frontRadarChangeMap.get(value);
			if (tmp != null) {
				int maxRadar1 = tmp.get(FlyUtil.HexToTag(mControlID));
				if (maxRadar1 < level)
					ret = 1;
				else
					ret = 0;

				return ret;
			}
		}
		return 0;

	}

	@Override
	public int GetChangeMultiRadarUi(int mControlID, int level) {
		synchronized (lock) {
			switch (mControlID) {
			case BackCarTag.BIG_RADAR_UP1:
			case BackCarTag.BIG_RADAR_UP2:
			case BackCarTag.BIG_RADAR_UP3:
			case BackCarTag.BIG_RADAR_UP4:
				if (isDStall() && isParkBrake())
					return GetChangeMultiRadarUi_(mControlID, level);
				break;
			case BackCarTag.BIG_RADAR_DOWN1:
			case BackCarTag.BIG_RADAR_DOWN2:
			case BackCarTag.BIG_RADAR_DOWN3:
			case BackCarTag.BIG_RADAR_DOWN4:
				if (isRStall() && isParkBrake())
					return 1;
				break;
			}

			return super.GetChangeMultiRadarUi(mControlID, level);
		}

	}

	@Override
	public void DealWithRSTALL(Bundle data) {
		if (isRStall() && isParkBrake()) {
			final int angle = data.getInt(BackCarTag.INTKEY);

			final int value = trunToTrackLevel(angle);
			// saveEditToPreference("rearRadarAngle", value);
			RadarAngle = value;
			// if(RearTrackLevel != value){
			// Log.d("DDD",
			// "DealWithRSTALL  "+angle+"  "+trunToTrackLevel(angle));
			// RearTrackLevel = value;
			mBackCarModule.ChangeUIObjBackground(BackCarTag.RADAR_REAR_TRACK,
					(byte) value);
		}
		// }
	}

	public int ReflashRadarWithCid(int cid, int value) {
		mBackCarModule.ReflashMultiUIObjBackground(cid, -1);
		return 0;
	}

	@Override
	public void DealWithDSTALL(Bundle data) {
		if (isParkBrake() && isDStall()) {
			final int angle = data.getInt(BackCarTag.INTKEY);
			final int value = trunToTrackLevel(angle);
			// saveEditToPreference("frontRadarAngle", value);
			RadarAngle = value;
			// if(FrontTrackLevel != value){
			// FrontTrackLevel = value;
			ReflashRadarWithCid(BackCarTag.BIG_RADAR_UP1, value);
			ReflashRadarWithCid(BackCarTag.BIG_RADAR_UP2, value);
			ReflashRadarWithCid(BackCarTag.BIG_RADAR_UP3, value);
			ReflashRadarWithCid(BackCarTag.BIG_RADAR_UP4, value);

			// Log.d("DDD", "DealWithDSTALL  " + angle + "  " + value);

			mBackCarModule.ChangeUIObjBackground(BackCarTag.RADAR_FRONT_TRACK,
					(byte) value);
		}
		// }
	}

	@Override
	public BaseKeyListener SetOnKeyListener(String mkeyStr, View vm,
			KeyListenerCallback cb) {

		if (mkeyStr.equals(BackCarTag.RADARPAGE913))
			vm.setOnKeyListener(AuDiA4KeyListener.getInstance(cb).pageRadar);
		else if (mkeyStr.equals(BackCarTag.PAGE_CVB_LAYOUT)) {
			Log.d(TAG, "SetOnKeyListener  CVB");
			vm.setOnKeyListener(AuDiA4KeyListener.getInstance(cb).CVBPage);
		} else
			vm.setOnKeyListener(AuDiA4KeyListener.getInstance(cb));
		vm.setOnFocusChangeListener(AuDiA4KeyListener.getInstance(cb));
		return (BaseKeyListener) AuDiA4KeyListener.getInstance(cb);

	};

	public void Io913_startVideo() {
		Bundle data = new Bundle();
		data.putInt(BackCarTag.INTKEY, BackCar913Service.CAMERA_913_REAR);
		mBackCarService.MakeAndSendMessageWithBundle(MsgType.FLYOBJ,
				BackCarTag.STALLENTER, data);

	}

	protected void Ctr_ModuleExit_method() {
		if (mBackCarService.getBackCarMode() == mBackCarService.BACKCAR_CVB) {
			mBackCarService.getFlyBackCarMainView().LayoutRemoveViewWithPageID(
					PageID.PAGE_CVB_LAYOUT);

		} else 	if (mBackCarService.getBackCarMode() == mBackCarService.BACKCAR_CVB_SV) {
			mBackCarService.getFlyBackCarMainView().LayoutRemoveViewWithPageID(
					PageID.PAGE_CVB_SV_LAYOUT);
			
		} else
			mBackCar913Service.ShowBgView(1, false); // not show
														// guiji
		DealBackCarMessages(MsgType.MSG_REMOVE, MsgType.GEY_FLYPAGE, null, 0);
		sendBroadcastToSystemUI("com.flyaudio.backcar_0",
				"info_icon_backcar.png", "", 0);
		mBackCarModule.ToModule913Close_SYS((byte) 0);
		BaseModuleExit();
		changeRadarMultiUiPStall();
	}

	public void changeRadarMultiUi(int f_pos, int r_pos) {
		mBackCarModule.ChangeMultiUIObjBackground(BackCarTag.BIG_RADAR_DOWN1,
				r_pos);
		mBackCarModule.ChangeMultiUIObjBackground(BackCarTag.BIG_RADAR_DOWN2,
				r_pos);
		mBackCarModule.ChangeMultiUIObjBackground(BackCarTag.BIG_RADAR_DOWN3,
				r_pos);
		mBackCarModule.ChangeMultiUIObjBackground(BackCarTag.BIG_RADAR_DOWN4,
				r_pos);

		mBackCarModule.ChangeMultiUIObjBackground(BackCarTag.BIG_RADAR_UP1,
				f_pos);
		mBackCarModule.ChangeMultiUIObjBackground(BackCarTag.BIG_RADAR_UP2,
				f_pos);
		mBackCarModule.ChangeMultiUIObjBackground(BackCarTag.BIG_RADAR_UP3,
				f_pos);
		mBackCarModule.ChangeMultiUIObjBackground(BackCarTag.BIG_RADAR_UP4,
				f_pos);
	}

	// \CAǵ\B9\B3\B5ʱ \B1\E4\B8\FC\C0״\EF\B9켣\C0״\EF\B9켣\D1\D5ɫ
	public void changeRadarMultiUiRStall() {
		if (isParkBrake() && isRStall()) {

			mBackCarModule.ChangeUIObjBackground(BackCarTag.RADAR_REAR_TRACK,
					(byte) RadarAngle);
			changeRadarMultiUi(0, 1);
		} else {
			changeRadarMultiUi(0, 0);
			mBackCarModule.ChangeUIObjBackground(BackCarTag.RADAR_REAR_TRACK,
					(byte) 0);

		}
		// int value = getPreferenceInt("rearRadarAngle", 0);
		// mBackCarModule.ChangeUIObjBackground(BackCarTag.RADAR_REAR_TRACK,
		// (byte) value);
	}

	public void changeRadarMultiUiDStall() {
		if (isParkBrake() && isDStall()) {
			Log.d(TAG, "changeRadarMultiUiDStall  ");
			changeRadarMultiUi(1, 0);
			mBackCarModule.ChangeUIObjBackground(BackCarTag.RADAR_FRONT_TRACK,
					(byte) RadarAngle);
		} else {
			changeRadarMultiUi(0, 0);
			mBackCarModule.ChangeUIObjBackground(BackCarTag.RADAR_FRONT_TRACK,
					(byte) 0);
		}
		// int value = getPreferenceInt("frontRadarAngle", 0);
		// mBackCarModule.ChangeUIObjBackground(BackCarTag.RADAR_FRONT_TRACK,
		// (byte)value);
	}

	// \BBص\BD\B7\C7r\BA\CDd \B5\B2״̬һ\C2\C9\CA\C7ȫ\B0\D7ɫ\C0״\EF\BA\CD
	// \CE\DE\C0״\EF\B9켣
	public void changeRadarMultiUiPStall() {
		changeRadarMultiUi(0, 0);
		mBackCarModule.ChangeUIObjBackground(BackCarTag.RADAR_FRONT_TRACK,
				(byte) 0);
		mBackCarModule.ChangeUIObjBackground(BackCarTag.RADAR_REAR_TRACK,
				(byte) 0);
	}

	public void Ctr_F_MODE_ID_method() {
		setFR_MODE_UI(1, 0, 0, 0);
		Save913CurrentVideoWay(0);
		mBackCarService.set913Camera_fr_mode(MsgType.F_MODE);
		FlyBaseListener.focus(FlyUtil.HexToTag(BackCarTag.Audi_SELECTBUTTON));

	}

	public void Ctr_R_MODE_ID_method() {
		setFR_MODE_UI(0, 1, 0, 0);
		Save913CurrentVideoWay(2);
		mBackCarService.set913Camera_fr_mode(MsgType.R_MODE);
		FlyBaseListener.focus(FlyUtil.HexToTag(BackCarTag.Audi_SELECTBUTTON));

	}

	public void Ctr_FR_MODE_ID_method() {
		setFR_MODE_UI(0, 0, 1, 0);
		mBackCarService.set913Camera_fr_mode(MsgType.FR_MODE);
		FlyBaseListener.focus(FlyUtil.HexToTag(BackCarTag.Audi_SELECTBUTTON));

	}

	public void Ctr_F_R_MODE_ID_method() {
		setFR_MODE_UI(0, 0, 0, 1);
		Save913CurrentVideoWay(0);
		mBackCarService.set913Camera_fr_mode(MsgType.F_ORIR_MODE);
		FlyBaseListener.focus(FlyUtil.HexToTag(BackCarTag.Audi_SELECTBUTTON));

	}

	// public void DelayShowTrackBg() {
	// DealBackCarMessages(MsgType.MSG_REMOVE, MsgType.SHOW_TRACKBG, null, 0);
	// Message msg = Message.obtain();
	// msg.what = MsgType.SHOW_TRACKBG;
	// DealBackCarMessages(MsgType.MSG_SENGDELAY, MsgType.SHOW_TRACKBG, msg,
	// MsgType.SHOW_TRACKBG_TIMEOUT);
	//
	// }

	void configTrackBlackPage() {
		int width = 0;
		if (isFloatRadarFull()) {
			width = FlyUtil.GET_SCREEN_W() - getFloatRadarSize();
		} else {
			width = FlyUtil.GET_SCREEN_W();
		}
		TrackBlackPageShow(width, 372, 58, Gravity.LEFT);

	}

	public void setNoticeBackgroud() {
		switch (get913CurrentVideoWay()) {
		case 0:
		case 1:
			mBackCarModule.ChangeUIObjBackground(BackCarTag.DOWNLINE_NOTICE,
					(byte) 0);
			break;
		case 2:
		case 3:
			mBackCarModule.ChangeUIObjBackground(BackCarTag.DOWNLINE_NOTICE,
					(byte) 1);
			break;

		}
	}

	public void Ctr_F_150Vdo_ID_method() {

		initAudi_A4_Button();
		DealBackCarMessages(MsgType.MSG_REMOVE, MsgType.SHOW_TRACKBG, null, 0);
		mBackCarModule.showFlyButtonBg(BackCarTag.A4_F_150VIDEO_CID, (byte) 1);
		if (mBackCarModule.isShowing913RearVideo() || needReShowVideo) {
			// mBackCar913Service.ShowBgView(2, false); // not show guiji
			getGLTrackHelper().getGlview().setFrameNullTime(-1);
			mBackCarService.getFlyBackCarMainView().TranceTrackAndTrackBg();
			setAllTrackBgHide();
		}
		// DealBackCarMessages(MsgType.MSG_REMOVE, MsgType.FRONT150VDO, null,
		// 0);
		// DealBackCarMessages(MsgType.MSG_SENGDELAY, MsgType.FRONT150VDO, null,
		// 200);
		sendMsgDelay(MsgType.FRONT150VDO, 200);
	}

	public void front_150_vdo_delay() {
		if (mBackCarModule.isShowing913RearVideo() || needReShowVideo) {
			// mBackCar913Service.ShowBgView(2, false); // not show guiji
			mBackCar913Service.front913Video();
			sendMsgDelay(MsgType.SETDRAW_TRACK, MsgType.SETDRAW_TRACK_TIMEOUT);
			needReShowVideo = false;
		}
		BackCar913Service.Mode913.direct = 1;
		Save913CurrentVideoWay(0);
		mBackCarModule.set913SourceRect(2);
		BackCar913Service.Mode913.focusTag = FlyUtil
				.HexToTag(BackCarTag.A4_F_150VIDEO_CID);
		FlyBaseListener.focus(FlyUtil.HexToTag(BackCarTag.A4_F_150VIDEO_CID));
		ChangeParamToJni();
		reSizeBgView();

		mBackCarService.getFlyBackCarMainView()
				.mSetTrailMode(FlyUtil.FRONTVIEW);

		sendMsgChangeTitleText(BackCarTag.A4_AUDI_TITLE, 1);
		mBackCarService.getFlyBackCarMainView().LayoutRemoveViewWithPageID(
				PageID.PAGE_913A4SETTING);

		mBackCar913Service.setBackGround();
		sendMsgDelay(MsgType.TRACK_REBACK, MsgType.TRACK_REBACK_TIMEOUT);
		setNoticeBackgroud();

	}

	public void Ctr_F_180Vdo_ID_method() {
		initAudi_A4_Button();
		DealBackCarMessages(MsgType.MSG_REMOVE, MsgType.SHOW_TRACKBG, null, 0);
		mBackCarModule.showFlyButtonBg(BackCarTag.A4_F_180VIDEO_CID, (byte) 1);
		if (mBackCarModule.isShowing913RearVideo() || needReShowVideo) {

			mBackCar913Service.front913Video();
			needReShowVideo = false;
		}
		BackCar913Service.Mode913.direct = 1;
		Save913CurrentVideoWay(1);
		mBackCarModule.set913SourceRect(1);
		BackCar913Service.Mode913.focusTag = FlyUtil
				.HexToTag(BackCarTag.A4_F_180VIDEO_CID);
		FlyBaseListener.focus(FlyUtil.HexToTag(BackCarTag.A4_F_180VIDEO_CID));
		mBackCar913Service.ShowBgView(1, false); // not show guiji
		sendMsgChangeTitleText(BackCarTag.A4_AUDI_TITLE, 0);
		mBackCarService.getFlyBackCarMainView().LayoutRemoveViewWithPageID(
				PageID.PAGE_913A4SETTING);
		setNoticeBackgroud();

	}

	public void Ctr_R_150Vdo_ID_method() {
		initAudi_A4_Button();
		FlyBaseListener.focus(FlyUtil.HexToTag(BackCarTag.A4_R_150VIDEO_CID));
		mBackCarModule.showFlyButtonBg(BackCarTag.A4_R_150VIDEO_CID, (byte) 1);
		if (!mBackCarModule.isShowing913RearVideo() || needReShowVideo) {
			getGLTrackHelper().getGlview().setFrameNullTime(-1);
			mBackCarService.getFlyBackCarMainView().TranceTrackAndTrackBg();
			setAllTrackBgHide();

		}
		// DealBackCarMessages(MsgType.MSG_REMOVE, MsgType.REAR150VDO, null, 0);
		// DealBackCarMessages(MsgType.MSG_SENGDELAY, MsgType.REAR150VDO, null,
		// 200);
		sendMsgDelay(MsgType.REAR150VDO, 200);
	}

	public void rear_150_vdo_delay() {
		if (!mBackCarModule.isShowing913RearVideo() || needReShowVideo) {
			// mBackCar913Service.ShowBgView(2, false); // not show guiji
			mBackCar913Service.rear913Video();
			// if(!needReShowVideo)
			sendMsgDelay(MsgType.SETDRAW_TRACK, MsgType.SETDRAW_TRACK_TIMEOUT);
			needReShowVideo = false;
		}

		BackCar913Service.Mode913.direct = 0;
		Save913CurrentVideoWay(2);
		mBackCarModule.set913SourceRect(2);
		BackCar913Service.Mode913.focusTag = FlyUtil
				.HexToTag(BackCarTag.A4_R_150VIDEO_CID);
		FlyBaseListener.focus(FlyUtil.HexToTag(BackCarTag.A4_R_150VIDEO_CID));
		reSizeBgView();
		ChangeParamToJni();
		mBackCarService.getFlyBackCarMainView().mSetTrailMode(FlyUtil.BACKFLAG);

		sendMsgChangeTitleText(BackCarTag.A4_AUDI_TITLE, 2);
		mBackCarService.getFlyBackCarMainView().LayoutRemoveViewWithPageID(
				PageID.PAGE_913A4SETTING);
		if (mBackCarModule.getBackCarState())
			mBackCarModule.TellModuleStart();

		mBackCar913Service.setBackGround();
		sendMsgDelay(MsgType.TRACK_REBACK, MsgType.TRACK_REBACK_TIMEOUT);
		setNoticeBackgroud();
	}

	public void Ctr_R_180Vdo_ID_method() {
		initAudi_A4_Button();
		DealBackCarMessages(MsgType.MSG_REMOVE, MsgType.SHOW_TRACKBG, null, 0);
		mBackCarModule.showFlyButtonBg(BackCarTag.A4_R_180VIDEO_CID, (byte) 1);
		if (!mBackCarModule.isShowing913RearVideo() || needReShowVideo) {
			mBackCar913Service.rear913Video();
			needReShowVideo = false;
		}
		BackCar913Service.Mode913.direct = 0;
		Save913CurrentVideoWay(3);
		mBackCarModule.set913SourceRect(1);
		BackCar913Service.Mode913.focusTag = FlyUtil
				.HexToTag(BackCarTag.A4_R_180VIDEO_CID);
		FlyBaseListener.focus(FlyUtil.HexToTag(BackCarTag.A4_R_180VIDEO_CID));
		mBackCar913Service.ShowBgView(1, false); // not show guiji
		sendMsgChangeTitleText(BackCarTag.A4_AUDI_TITLE, 3);
		mBackCarService.getFlyBackCarMainView().LayoutRemoveViewWithPageID(
				PageID.PAGE_913A4SETTING);
		setNoticeBackgroud();
	}

	public void Ctr_BigSetting_ID_method() {
		if (Not913ModeAndNotStartByUser()) {
			BackCarService.mBackCarService.MakeAndSendMessageWithBundle(
					MsgType.FLYOBJ, BackCarTag.A4_AUDI_NOT_913_SET_CID, null);
			return;
		}
		mBackCarModule.ToModule913RequestFRSound((byte) 0);
		View PageSetting = PluginProxyContext.getInstance().getLayout(
				PageID.PAGE_913A4SETTING);

		if (mBackCarService.getReversing())
			mBackCarService.getFlyBackCarMainView().LayoutAddViewWithPageID(
					PageSetting, PageID.PAGE_913A4SETTING);
		else {

			mBackCarService.getFlyFloatRadarView().LayoutAddViewWithPageID(
					PageSetting, PageID.PAGE_913A4SETTING);
		}
		initAudi_A4_select_Button();

		if (isFloatRadarFull()) {

			mBackCarService.getBackCar913Service()
					.ChangeBigAndSmallRadarTextUI(
							BackCarTag.A4_ChangeBigOrSmall_radar, 1);

		} else {

			mBackCarService.getBackCar913Service()
					.ChangeBigAndSmallRadarTextUI(
							BackCarTag.A4_ChangeBigOrSmall_radar, 0);
		}

		FlyBaseListener.focus(FlyUtil
				.HexToTag(BackCarTag.A4_AUDI_ClOSE_SYS_CID));
		mBackCarService.getBackCar913Service().change_audiA4_sound_levelUI(
				BackCarTag.A4_AUDI_F_SOUND_VUALE, Front_sound_level);

		mBackCarService.getBackCar913Service().change_audiA4_sound_levelUI(
				BackCarTag.A4_AUDI_R_SOUND_VUALE, Rear_sound_level);

		change_AuDiA4_FR_ModeUI(mBackCarService.get913Camera_fr_mode());
		setNoticeBackgroud();
	}

	public void Ctr_SmallSetting_ID_method() {
		mBackCarModule.ToModule913RequestFRSound((byte) 0);
		View CVBPageSetting = PluginProxyContext.getInstance().getLayout(
				PageID.PAGE_CVBA4SETTING_LAYOUT);

		if(mBackCarService.getBackCarMode() == mBackCarService.BACKCAR_CVB_SV)
			CVBPageSetting = PluginProxyContext.getInstance().getLayout(
					PageID.PAGE_CVBSV_SETTING_LAYOUT);
		
		if (mBackCarService.getReversing())
			mBackCarService.getFlyBackCarMainView().LayoutAddViewWithPageID(
					CVBPageSetting, PageID.PAGE_913A4SETTING);
		else {

			mBackCarService.getFlyFloatRadarView().LayoutAddViewWithPageID(
					CVBPageSetting, PageID.PAGE_913A4SETTING);
		}
		initAudi_A4_select_Button();

		if (isFloatRadarFull()) {

			mBackCarService.getBackCar913Service()
					.ChangeBigAndSmallRadarTextUI(
							BackCarTag.A4_ChangeBigOrSmall_radar, 1);

		} else {

			mBackCarService.getBackCar913Service()
					.ChangeBigAndSmallRadarTextUI(
							BackCarTag.A4_ChangeBigOrSmall_radar, 0);
		}

		FlyBaseListener.focus(FlyUtil
				.HexToTag(BackCarTag.A4_AUDI_FRONT_SOUND_CID));
		mBackCarService.getBackCar913Service().change_audiA4_sound_levelUI(
				BackCarTag.A4_AUDI_F_SOUND_VUALE, Front_sound_level);

		mBackCarService.getBackCar913Service().change_audiA4_sound_levelUI(
				BackCarTag.A4_AUDI_R_SOUND_VUALE, Rear_sound_level);

	}

	public void Ctr_Close_SYS_ID_method() {

		if (mBackCarService.getReversing()) // \C9\E8\D6\C3ҳ\D4ڵ\B9\B3\B5\C0\EF\C3\E6
											// \B7\F1\D4\F2\D4\DA\CD\E2\C3\E6
		{
			mBackCarModule.click913QuitBackcar();
			mBackCarModule.ToModule913Close_SYS((byte) 0);
		} else {
			removeFloatRadar(0);

		}
	}

	public void Ctr_CVB_Setting_ID_method() {
		if (mBackCarService.getReversing()) // CVB\C9\E8\D6\C3ҳ\D4ڵ\B9\B3\B5\C0\EF\C3\E6
											// \B7\F1\D4\F2\D4\DA\CD\E2\C3\E6
		{
			BackCarService.mBackCarService.getFlyBackCarMainView()
					.LayoutRemoveViewWithPageID(PageID.PAGE_913A4SETTING);
			FlyBaseListener.focus(FlyUtil
					.HexToTag(BackCarTag.A4_AUDI_NOT_913_SET_CID));

		} else {
			removeFloatRadar(0);
		}

	}

	public void Ctr_Front_Sound_ID_method() {
		hideDanKuangSetting();
		// initAudi_A4_select_Button(); 1117
		mBackCarModule.showFlyButtonBg(BackCarTag.A4_AUDI_FRONT_SOUND_CID,
				(byte) 1);
		FlyBaseListener.UIParentViewShow(
				FlyUtil.HexToTag(BackCarTag.A4_AUDI_FRONT_SOUND_Low), true,
				false);
		FlyBaseListener.UIParentViewShow(
				FlyUtil.HexToTag(BackCarTag.A4_AUDI_REAR_SOUND_Low), false,
				false);
		FlyBaseListener.UIParentViewShow(
				FlyUtil.HexToTag(BackCarTag.Audi_F_MODE), false, false);
		show_F_soundUi_set(Front_sound_level);
		FlyBaseListener.focus(FlyUtil
				.HexToTag(BackCarTag.A4_AUDI_FRONT_SOUND_Mid));

	}

	public void Ctr_Rear_Sound_ID_method() {
		hideDanKuangSetting();
		// initAudi_A4_select_Button(); 1117
		mBackCarModule.showFlyButtonBg(BackCarTag.A4_AUDI_REAR_SOUND_CID,
				(byte) 1);
		FlyBaseListener.UIParentViewShow(
				FlyUtil.HexToTag(BackCarTag.A4_AUDI_REAR_SOUND_Low), true,
				false);

		FlyBaseListener.UIParentViewShow(
				FlyUtil.HexToTag(BackCarTag.A4_AUDI_FRONT_SOUND_Low), false,
				false);
		FlyBaseListener.UIParentViewShow(
				FlyUtil.HexToTag(BackCarTag.Audi_F_MODE), false, false);
		show_R_soundUi_set(Rear_sound_level);
		FlyBaseListener.focus(FlyUtil
				.HexToTag(BackCarTag.A4_AUDI_REAR_SOUND_Mid));

	}

	public void Ctr_RadarZoomInOROut_ID_method() {
		hideDanKuangSetting();
		// initAudi_A4_select_Button(); 1117
		if (mBackCarService.getReversing())
			CtrlPageSettingInBackCar();
		else
			CtrlPageSettingOutSideBackCar();

	}

	public void Ctr_CVB_RadarZoomInOROut_ID_method() {
		if (mBackCarService.getReversing())
			CVBCtrlPageSettingInBackCar();
		else
			CtrlPageSettingOutSideBackCar();

	}

	public void Ctr_Select_FRMode_ID_method() {
		FlyBaseListener.UIParentViewShow(
				FlyUtil.HexToTag(BackCarTag.Audi_F_MODE), true, false);
		FlyBaseListener.UIParentViewShow(
				FlyUtil.HexToTag(BackCarTag.A4_AUDI_REAR_SOUND_Low), false,
				false);
		FlyBaseListener.UIParentViewShow(
				FlyUtil.HexToTag(BackCarTag.A4_AUDI_FRONT_SOUND_Low), false,
				false);
		initAudi_A4_select_Button();
		show_FR_Mode_Ui_set(mBackCarService.get913Camera_fr_mode());
		mBackCarModule.showFlyButtonBg(BackCarTag.Audi_SELECTBUTTON, (byte) 1);
		//FlyBaseListener.focus(FlyUtil.HexToTag(BackCarTag.Audi_F_R_MODE));

	}

	public void Ctr_SetFrontSoundLow_ID_method() {
		initAudi_A4_F_Sound_UI(Front_sound_level);
		Front_sound_level = 0;
		tellModuleFSoundLevel(1, Front_tone_level);
		show_F_soundUi_set(Front_sound_level);
		saveEditToPreference(F_S_Str, Front_sound_level);
		sendMsgChangeTextUi(BackCarTag.A4_AUDI_F_SOUND_VUALE, Front_sound_level);
		FlyBaseListener.UIParentViewShow(
				FlyUtil.HexToTag(BackCarTag.A4_AUDI_FRONT_SOUND_Low), false,
				false);
		FlyBaseListener.focus(FlyUtil
				.HexToTag(BackCarTag.A4_AUDI_FRONT_SOUND_CID));
	}

	public void Ctr_SetFrontSoundMid_ID_method() {
		initAudi_A4_F_Sound_UI(Front_sound_level);
		Front_sound_level = 1;
		tellModuleFSoundLevel(4, Front_tone_level);
		show_F_soundUi_set(Front_sound_level);
		saveEditToPreference(F_S_Str, Front_sound_level);
		sendMsgChangeTextUi(BackCarTag.A4_AUDI_F_SOUND_VUALE, Front_sound_level);
		FlyBaseListener.UIParentViewShow(
				FlyUtil.HexToTag(BackCarTag.A4_AUDI_FRONT_SOUND_Low), false,
				false);
		FlyBaseListener.focus(FlyUtil
				.HexToTag(BackCarTag.A4_AUDI_FRONT_SOUND_CID));
	}

	public void Ctr_SetFrontSoundHigh_ID_method() {
		initAudi_A4_F_Sound_UI(Front_sound_level);
		Front_sound_level = 2;
		tellModuleFSoundLevel(9, Front_tone_level);
		show_F_soundUi_set(Front_sound_level);
		saveEditToPreference(F_S_Str, Front_sound_level);
		sendMsgChangeTextUi(BackCarTag.A4_AUDI_F_SOUND_VUALE, Front_sound_level);
		FlyBaseListener.UIParentViewShow(
				FlyUtil.HexToTag(BackCarTag.A4_AUDI_FRONT_SOUND_Low), false,
				false);
		FlyBaseListener.focus(FlyUtil
				.HexToTag(BackCarTag.A4_AUDI_FRONT_SOUND_CID));
	}

	public void Ctr_SetRearSoundLow_ID_method() {
		initAudi_A4_R_Sound_UI(Rear_sound_level);
		Rear_sound_level = 0;
		tellModuleRSoundLevel(1, Rear_tone_level);
		show_R_soundUi_set(Rear_sound_level);
		saveEditToPreference(R_S_Str, Rear_sound_level);
		sendMsgChangeTextUi(BackCarTag.A4_AUDI_R_SOUND_VUALE, Rear_sound_level);
		FlyBaseListener.UIParentViewShow(
				FlyUtil.HexToTag(BackCarTag.A4_AUDI_REAR_SOUND_Low), false,
				false);
		FlyBaseListener.focus(FlyUtil
				.HexToTag(BackCarTag.A4_AUDI_REAR_SOUND_CID));

	}

	public void Ctr_SetRearSoundMid_ID_method() {

		initAudi_A4_R_Sound_UI(Rear_sound_level);

		Rear_sound_level = 1;
		tellModuleRSoundLevel(4, Rear_tone_level);
		show_R_soundUi_set(Rear_sound_level);
		saveEditToPreference(R_S_Str, Rear_sound_level);
		sendMsgChangeTextUi(BackCarTag.A4_AUDI_R_SOUND_VUALE, Rear_sound_level);
		FlyBaseListener.UIParentViewShow(
				FlyUtil.HexToTag(BackCarTag.A4_AUDI_REAR_SOUND_Low), false,
				false);
		FlyBaseListener.focus(FlyUtil
				.HexToTag(BackCarTag.A4_AUDI_REAR_SOUND_CID));

	}

	public void Ctr_SetRearSoundHigh_ID_method() {
		initAudi_A4_R_Sound_UI(Rear_sound_level);

		Rear_sound_level = 2;
		tellModuleRSoundLevel(9, Rear_tone_level);
		show_R_soundUi_set(Rear_sound_level);
		saveEditToPreference(R_S_Str, Rear_sound_level);
		sendMsgChangeTextUi(BackCarTag.A4_AUDI_R_SOUND_VUALE, Rear_sound_level);
		FlyBaseListener.UIParentViewShow(
				FlyUtil.HexToTag(BackCarTag.A4_AUDI_REAR_SOUND_Low), false,
				false);
		FlyBaseListener.focus(FlyUtil
				.HexToTag(BackCarTag.A4_AUDI_REAR_SOUND_CID));

	}

	public void Ctr_BackCarView_Layout(int width, int height, int gravity) {
		if(mBackCarModule.getBackCarState() && mBackCarService.getBackCarMode() == mBackCarService.BACKCAR_CVB_SV){
			mBackCarService.getBackCarView().reSizeSurfaceView(FlyUtil.GET_SCREEN_W(), -1);
		}
		else 
			mBackCarService.getBackCarView().reSizeSurfaceView(width, -1);
	}

	public void Ctr_Setting_Back() {
		mBackCarService.getFlyBackCarMainView().LayoutRemoveViewWithPageID(
				PageID.PAGE_913A4SETTING);
		DigInVieoWay = get913CurrentVideoWay();
		switchDigFocus(DigInVieoWay);
	}

	protected void Ctr_BackTo_Setting_ID_method() {
	};

	protected void Ctr_Rotate_Rear_Tone_UI_ID_method(Bundle data) {
	};

	protected void Ctr_Rotate_Rear_Sound_UI_ID_method(Bundle data) {
	};

	protected void Ctr_Rotate_Front_Tone_UI_ID_method(Bundle data) {
	};

	protected void Ctr_Rotate_Front_Sound_UI_ID_method(Bundle data) {
	};

	protected void Ctr_Rear_Tone_ID_method() {
	};

	protected void Ctr_Front_Tone_ID_method() {
	};

	protected void Ctr_bnt_edge_trackbg_ID_method() {
	}

	protected void Ctr_bnt_floatRadar_show_method() {
	}

	protected void BackCar_StallEnter_method() {
	}

	protected void Ctr_Audi_Float_Radar_Bg_ID_method() {
		mBackCarService
				.MakeAndSendMessageButton(BackCarTag.A4_AUDI_ClOSE_SYS_CID);

	}

	public void Ctr_home_event_method() {
		if (!mBackCarModule.getBackCarState()) {
			if (mBackCar913Service.is913StartByUser()
					|| mBackCar913Service.getBackCar913StallDState())
				key_backcar913stop();
			removeFloatRadar(0);
			FloatRadarViewRemove();
		}
	}

	protected void Ctr_StartByUser_Method() {
		mBackCarModule.setChoiceFRMode();
		mBackCarModule.showSignByUser(true);
		DigIn913Mode();
	}

	public Handler myMsgHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			Bundle data = msg.getData();
			if ((int) msg.what == MsgType.FLYOBJ) {

				switch ((int) msg.arg1) {

				case BackCarTag.STALLENTER:
					BackCar_StallEnter_method();
					if (data != null) {
						int stall = data.getInt(BackCarTag.INTKEY);
						Log.d(TAG, "stall " + stall);
						if (stall == BackCar913Service.CAMERA_913_REAR) {
							changeRadarMultiUiRStall();
							Save913CurrentVideoWay(2);
							switchDigVideoWay(2);
							ChangeParamToJni();

						} else if (stall == BackCar913Service.CAMERA_913_FRONT) {
							changeRadarMultiUiDStall();
							Save913CurrentVideoWay(0);
							switchDigVideoWay(0);
							ChangeParamToJni();

						}
					} else {
						DigInVieoWay = get913CurrentVideoWay();
						if (DigInVieoWay != -1) {
							switchDigVideoWay(DigInVieoWay);

						} else
							switchDigVideoWay(0);
					}
					ShowFloatRadarTrack();
					break;
				case BackCarTag.SET180VIDEO_CID:

					break;

				case BackCarTag.Audi_F_MODE:

					Ctr_F_MODE_ID_method();
					break;
				case BackCarTag.Audi_R_MODE:

					Ctr_R_MODE_ID_method();
					break;
				case BackCarTag.Audi_FR_MODE:

					Ctr_FR_MODE_ID_method();
					break;
				case BackCarTag.Audi_F_R_MODE:

					Ctr_F_R_MODE_ID_method();
					break;

				case BackCarTag.A4_F_150VIDEO_CID:

					Ctr_F_150Vdo_ID_method();

					break;

				case BackCarTag.A4_F_180VIDEO_CID:

					Ctr_F_180Vdo_ID_method();
					break;

				case BackCarTag.A4_R_150VIDEO_CID:

					Ctr_R_150Vdo_ID_method();
					break;

				case BackCarTag.A4_R_180VIDEO_CID:

					Ctr_R_180Vdo_ID_method();
					break;

				case BackCarTag.A4_AUDI_OSD_SET_CID:
					Log.d(TAG, "A4_AUDI_OSD_SET_CID");
				case BackCarTag.A4_AUDI_SET_CID:
					Log.d(TAG, "A4_AUDI_SET_CID");
					Ctr_BigSetting_ID_method();
					break;

				case BackCarTag.A4_AUDI_NOT_913_SET_CID:
					Log.d(TAG, "A4_AUDI_NOT_913_SET_CID");

					Ctr_SmallSetting_ID_method();

					break;
				case BackCarTag.A4_AUDI_ClOSE_SYS_CID:

					Ctr_Close_SYS_ID_method();
					break;
				case BackCarTag.A4_AUDI_CVB_CLOSE_SET:

					Ctr_CVB_Setting_ID_method();

					break;

				case BackCarTag.A4_AUDI_FRONT_SOUND_CID:

					Ctr_Front_Sound_ID_method();
					break;

				case BackCarTag.A4_AUDI_REAR_SOUND_CID:

					Ctr_Rear_Sound_ID_method();
					break;

				case BackCarTag.A4_AUDI_ScreenSize_full:// \CB\F5С\BB\F2\B7Ŵ\F3\C0״\EF

					Ctr_RadarZoomInOROut_ID_method();
					break;

				case BackCarTag.A4_AUDI_CVB_ScreenSize_full:

					Ctr_CVB_RadarZoomInOROut_ID_method();

					break;

				case BackCarTag.Audi_SELECTBUTTON:

					Ctr_Select_FRMode_ID_method();
					break;

				case BackCarTag.A4_AUDI_FRONT_SOUND_Low:

					Ctr_SetFrontSoundLow_ID_method();
					break;

				case BackCarTag.A4_AUDI_FRONT_SOUND_Mid:

					Ctr_SetFrontSoundMid_ID_method();
					break;

				case BackCarTag.A4_AUDI_FRONT_SOUND_High:

					Ctr_SetFrontSoundHigh_ID_method();
					break;

				case BackCarTag.A4_AUDI_REAR_SOUND_Low:

					Ctr_SetRearSoundLow_ID_method();
					break;

				case BackCarTag.A4_AUDI_REAR_SOUND_Mid:

					Ctr_SetRearSoundMid_ID_method();
					break;

				case BackCarTag.A4_AUDI_REAR_SOUND_High:

					Ctr_SetRearSoundHigh_ID_method();
					break;
				case BackCarTag.TOUCH_BG:

					Ctr_Setting_Back();
					// /mBackCarModule.restTrackRunnig(); //
					// mBackCarModule.testRadarRunning(); //

					break;

				case BackCarTag.DigInShowMainView:
					// mBackCarService.getFlyBackCarMainView().mSetFronView();
					break;
				case BackCarTag.A4_AUDI_Float_Radar_Bg:
					Log.d(TAG, "A4_AUDI_FLOAT_CAR_FOCUS");
					Ctr_Audi_Float_Radar_Bg_ID_method();
					break;
				/* Audi A3 --- */

				case BackCarTag.AUDI_BACKTO_SETTING_CID:
					Ctr_BackTo_Setting_ID_method();
					break;
				case BackCarTag.AUDI_ROTATE_REAR_TONE_UI:
					Ctr_Rotate_Rear_Tone_UI_ID_method(data);
					break;

				case BackCarTag.AUDI_ROTATE_REAR_SOUND_UI:
					Ctr_Rotate_Rear_Sound_UI_ID_method(data);
					break;
				case BackCarTag.AUDI_ROTATE_FRONT_TONE_UI:
					Ctr_Rotate_Front_Tone_UI_ID_method(data);
					break;
				case BackCarTag.AUDI_ROTATE_FRONT_SOUND_UI:
					Ctr_Rotate_Front_Sound_UI_ID_method(data);
					break;
				case BackCarTag.AUDI_REAR_TONE_CID:
					Ctr_Rear_Tone_ID_method();
					break;
				case BackCarTag.AUDI_FRONT_TONE_CID:
					Ctr_Front_Tone_ID_method();
					break;

				/* Audi A3 --- */
				case BackCarTag.AUDI_BNT_EDGETRACK_CHANGE:
					if (DEBUG)
						Log.d(TAG, "AUDI_BNT_EDGETRACK_CHANGE");
					Ctr_bnt_edge_trackbg_ID_method();
					break;
				case BackCarTag.AUDI_BIG_RADAR_SHOW_ID:
					if (DEBUG)
						Log.d(TAG, "AUDI_BIG_RADAR_SHOW_ID");
					Ctr_bnt_floatRadar_show_method();
					break;

				}
			} else if ((int) msg.what == MsgType.COMMON) {
				switch ((int) msg.arg1) {
				case BackCarTag.STARTBYUSER:

					Ctr_StartByUser_Method();
					// mBackCarModule.reBackLight();

					break;
				case BackCarTag.MODULEEXIT:

					Ctr_ModuleExit_method();
					break;
				case BackCarTag.MODULEENTER:
					Log.d(TAG, "MODULEENTER fs " + FloatRadarScale);

					if (mBackCarModule.getBackCarState() && IsCVBVdoMode()) {
						if (mBackCarService.getBackCarMode() == mBackCarService.BACKCAR_CVB){
							mBackCarService.getFlyBackCarMainView()
									.LayoutAddViewWithPageID(PluginProxyContext.getInstance()
													.getLayout(PageID.PAGE_CVB_LAYOUT),PageID.PAGE_CVB_LAYOUT);
						}else {
							mBackCarService.getFlyBackCarMainView()
							.LayoutAddViewWithPageID(PluginProxyContext.getInstance()
											.getLayout(PageID.PAGE_CVB_SV_LAYOUT),PageID.PAGE_CVB_SV_LAYOUT);
						}
						BackCarshowFloatRadar();
						CVBBackCarMode();
						floatCVBRadarReMeasue(FloatRadarScale);
						STOP_LOGO();
						mBackCarService.getFlyBackCarMainView()
								.mSetTrailMode(3);
						ChangeParamToJni();

						sendMsgDelay(MsgType.SETDRAW_TRACK,
								MsgType.SETDRAW_TRACK_TIMEOUT);

						GLTrackonResume();
						mBackCar913Service.setBackCarView(true);
						FlyBaseListener.focus(FlyUtil
								.HexToTag(BackCarTag.A4_AUDI_NOT_913_SET_CID));
					} else {
						mBackCar913Service.ShowBgView(1, false); // not show
																	// guiji

						BackCarshowFloatRadar();
						// if(!GetPowerState())
						// BLACKPAGESHOW();

						floatRadarReMeasue(FloatRadarScale);

						sendMsgDelay(MsgType.SHOW_TRACK,
								MsgType.SHOW_TRACK_TIMEOUT);
						STOP_LOGO();
					}

					mBackCarModule.ToModule913OPEN_SYS((byte) 0x01);
					ShowFloatRadarTrack();
					BaseModuleStart();

					break;
				case BackCarTag.MODULEPREENTER:
					// Log.d("DDD", "MODULEENTER fs "+FloatRadarScale);
					// floatRadarReMeasue(FloatRadarScale);
					mBackCarService.showFloatApp(false, 0);
					break;

				case BackCarTag.F_VOICE_LEVEL:

					Ctr_F_VOICE_LEVEL_Method(data);
					break;
				case BackCarTag.R_VOICE_LEVEL:
					Ctr_R_VOICE_LEVEL_Method(data);
					break;

				case BackCarTag.BACKTO913:
					Log.d("DDD", "BACKTO913 ");
					// if(mBackCarService.getBackCarMode() !=
					// mBackCarService.BACKCAR_914)
					// mBackCar913Service.keyCodeBackExit();
					// else {
					BLACKPAGEHIDE();
					if (IsCVBVdoMode())// \C8\E7\B9\FB\CA\C7913ģʽ\B2\BB\D3\C3\D4\D9show 
					{
						mBackCarModule.ShowBackCarView913();
					// needReShowVideo = true;
				}	else {
						if (!mBackCarService.getReversing())
							mBackCarModule.ShowBackCarView913();
						
					}
					DigIn913Mode();
					changeRadarMultiUiPStall();
					// }
					break;
				case BackCarTag.BACKKEYEVENT:
					Ctr_Back_Event_method();
					break;
				case BackCarTag.HOMEEYEVENT:
					Ctr_home_event_method();
					break;
				case BackCarTag.ANYKEYEVENT:
					mBackCar913Service.Remove_BackCar913_StallD_Auto_Stop();
					break;

				}
			} else if ((int) msg.what == MsgType.SETSURFACE) {
				int width = data.getInt(BackCarTag.INTKEY);

				switch ((int) msg.arg1) {
				case BackCarTag.SetHalfSurfaceView:
					Ctr_BackCarView_Layout(width, -1, -1);

					break;

				}

			} else if ((int) msg.what == MsgType.REMOVE_FLOATRADAR) {
				removeFloatRadar(0);
			} else if ((int) msg.what == MsgType.GEY_FLYPAGE) {

				needReShowVideo = true;
				DigIn913Mode();
			} else if ((int) msg.what == MsgType.RESIZESURFACE) {
				BackCarshowFloatRadar();

			} else if ((int) msg.what == MsgType.SHOW_TRACKBG) {
				if (mBackCarService.getBackCarMode() == mBackCarService.BACKCAR_914)
					mBackCar913Service.setBackGround(); // show guiji

			} else if ((int) msg.what == MsgType.SHOW_TRACK) {
				if (mBackCarService.getBackCarMode() == mBackCarService.BACKCAR_914) {
					int trackmode = 0;
					if (mBackCarModule.getBackCarState())
						trackmode = FlyUtil.BACKFLAG;
					else if (mBackCar913Service.getBackCar913StallDState())
						trackmode = FlyUtil.FRONTVIEW;
					else {
						trackmode = get913CurrentVideoWay();
						if (trackmode == 0 || trackmode == 1)
							trackmode = FlyUtil.FRONTVIEW;
						else
							trackmode = FlyUtil.BACKFLAG;
					}
					if (trackmode == FlyUtil.BACKFLAG)
						BackCar913Service.Mode913.direct = 0;
					else
						BackCar913Service.Mode913.direct = 1;

					mBackCarService.getFlyBackCarMainView().mSetTrailMode(
							trackmode);
				}
				Log.d(TAG, "GLTrackViewToolHelper onResume");
				GLTrackonResume();
				if(get913CurrentVideoWay() == 1 ||get913CurrentVideoWay() == 3 )
					getGLTrackHelper().getGlview().setDraw(false);
				
				Message msg2 = Message.obtain();
				msg2.what = MsgType.REQUEST_SYNCANGLE;
				 DealBackCarMessages(myMsgHandler ,MsgType.MSG_SENGDELAY , 0 ,
						 msg2,MsgType.REQUEST_SYNCANGLE_TIMEOUT );
				// sendMsgDelay(MsgType.SETDRAW_TRACK,
				// MsgType.SETDRAW_TRACK_TIMEOUT);
			} else if ((int) msg.what == MsgType.SETDRAW_TRACK) {
				SetDraw_Track_method();
			} else if ((int) msg.what == MsgType.TRACK_REBACK) {
				mBackCarService.getFlyBackCarMainView().ReBackTrackAndTrackBg();
			} else if ((int) msg.what == MsgType.FRONT150VDO) {
				front_150_vdo_delay();
			} else if ((int) msg.what == MsgType.REAR150VDO) {
				rear_150_vdo_delay();
			}	
			else if((int) msg.what ==MsgType.REQUEST_SYNCANGLE){
				getGLTrackHelper().getGlview().requestSyncAngle();
				getGLTrackHelper().getGlview().requestSyncAngle();
				getGLTrackHelper().getGlview().requestSyncAngle();
			}

		}
	};

	
	protected void  Ctr_Back_Event_method(){
	if (!mBackCarModule.getBackCarState()) {
		mBackCar913Service.keyCodeBackExit();
		FloatRadarViewRemove();
	}
	}
	
	public void Ctr_F_VOICE_LEVEL_Method(Bundle data) {
		final int fsound[] = data.getIntArray(BackCarTag.INTKEY);
		Log.d(TAG, "fsound[] " + fsound[1] + " " + fsound[0]);
		Front_tone_level = fsound[0];
		if (fsound[1] < 3)
			Front_sound_level = 0;
		else if (fsound[1] < 7)
			Front_sound_level = 1;
		else if (fsound[1] < 10)
			Front_sound_level = 2;
		saveEditToPreference(F_S_Str, Front_sound_level);
		sendMsgChangeTextUi(BackCarTag.A4_AUDI_F_SOUND_VUALE, Front_sound_level);
	}

	public void Ctr_R_VOICE_LEVEL_Method(Bundle data) {
		final int rsound[] = data.getIntArray(BackCarTag.INTKEY);
		Log.d(TAG, "rsound[] " + rsound[1] + " " + rsound[0]);
		Rear_tone_level = rsound[0];
		if (rsound[1] < 3)
			Rear_sound_level = 0;
		else if (rsound[1] < 7)
			Rear_sound_level = 1;
		else if (rsound[1] < 10)
			Rear_sound_level = 2;

		saveEditToPreference(R_S_Str, Rear_sound_level);
		sendMsgChangeTextUi(BackCarTag.A4_AUDI_R_SOUND_VUALE, Rear_sound_level);

	}

	public void SetDraw_Track_method() {
		boolean b = mBackCarService.getFlyBackCarMainView().isSetShowTrack();
		getGLTrackHelper().getGlview().setDraw(b);
	}

	protected void sendMsgDelay(int msgtype, int delay) {

		DealBackCarMessages(MsgType.MSG_REMOVE, msgtype, null, 0);
		Message msg = Message.obtain();
		msg.what = msgtype;

		DealBackCarMessages(MsgType.MSG_SENGDELAY, msgtype, msg, delay);
	}

	protected void setFR_MODE_UI(int f, int r, int fr, int f_r) {
		mBackCarModule.showFlyButtonBg(BackCarTag.Audi_FR_MODE, (byte) fr);
		mBackCarModule.showFlyButtonBg(BackCarTag.Audi_F_MODE, (byte) f);
		mBackCarModule.showFlyButtonBg(BackCarTag.Audi_R_MODE, (byte) r);
		mBackCarModule.showFlyButtonBg(BackCarTag.Audi_F_R_MODE, (byte) f_r);

	}

	protected void ShowFloatRadarTrack() {
		switch (mBackCar913Service.GetProtocolState()) {
		case R_STALL:
			mBackCarModule.ShowUiView(BackCarTag.RADAR_REAR_TRACK, true);
			mBackCarModule.ShowUiView(BackCarTag.RADAR_FRONT_TRACK, false);
			mBackCarModule.ChangeMultiUIObjBackground(BackCarTag.BIG_RADAR_UP1,
					0);
			mBackCarModule.ChangeMultiUIObjBackground(BackCarTag.BIG_RADAR_UP2,
					0);
			mBackCarModule.ChangeMultiUIObjBackground(BackCarTag.BIG_RADAR_UP3,
					0);
			mBackCarModule.ChangeMultiUIObjBackground(BackCarTag.BIG_RADAR_UP4,
					0);
			break;
		case D_STALL:
		case S_STALL:
			mBackCarModule.ShowUiView(BackCarTag.RADAR_REAR_TRACK, false);
			mBackCarModule.ShowUiView(BackCarTag.RADAR_FRONT_TRACK, true);

			mBackCarModule.ChangeMultiUIObjBackground(
					BackCarTag.BIG_RADAR_DOWN1, 0);
			mBackCarModule.ChangeMultiUIObjBackground(
					BackCarTag.BIG_RADAR_DOWN2, 0);
			mBackCarModule.ChangeMultiUIObjBackground(
					BackCarTag.BIG_RADAR_DOWN3, 0);
			mBackCarModule.ChangeMultiUIObjBackground(
					BackCarTag.BIG_RADAR_DOWN4, 0);
			break;
		default:
			mBackCarModule.ShowUiView(BackCarTag.RADAR_REAR_TRACK, false);
			mBackCarModule.ShowUiView(BackCarTag.RADAR_FRONT_TRACK, false);
			break;
		}

	}

	public void change_AuDiA4_FR_ModeUI(int fr_mode) {

		switch (fr_mode) {
		case MsgType.FR_MODE:
			mBackCar913Service.GetBackCar913View().change_audiA4_fr_modeui();

			break;
		case MsgType.F_MODE:
			mBackCar913Service.GetBackCar913View().change_audiA4_f_modeui();
			break;
		case MsgType.R_MODE:
			mBackCar913Service.GetBackCar913View().change_audiA4_r_modeui();
			break;
		case MsgType.F_ORIR_MODE:
			mBackCar913Service.GetBackCar913View().change_audiA4_f_r_modeui();
		}
		FlyBaseListener.UIParentViewShow(
				FlyUtil.HexToTag(BackCarTag.Audi_F_MODE), false, false);

	}

	void Start_InitFR_ModeUI(int fr_mode) {
		switch (fr_mode) {
		case MsgType.FR_MODE:
			mBackCar913Service.GetBackCar913View().change_audiA4_fr_modeUi(
					null, null, true);

			break;
		case MsgType.F_MODE:
			mBackCar913Service.GetBackCar913View().change_audiA4_f_modeUi(null,
					null, true);
			break;
		case MsgType.R_MODE:
			mBackCar913Service.GetBackCar913View().change_audiA4_r_modeUi(null,
					null, true);
			break;
		case MsgType.F_ORIR_MODE:
			mBackCar913Service.GetBackCar913View().change_audiA4_f_r_modeUi(
					null, null, true);
		}
		FlyBaseListener.UIParentViewShow(
				FlyUtil.HexToTag(BackCarTag.Audi_F_MODE), false, false);
	}

	public void tellModuleFSoundLevel(int level) {

		tellModuleFSoundLevel(level, Front_tone_level);
	}

	public void tellModuleRSoundLevel(int level) {
		tellModuleRSoundLevel(level, Rear_tone_level);

	}

	void tellModuleFSoundLevel(int level, int tone) {
		if (DEBUG)
			Log.d(TAG, "tellModuleFSoundLevel    s   " + level
					+ "   t  " + tone);
		mBackCarModule.ToModule913FRSoundLevel((byte) 0x90, (byte) level,
				(byte) tone);
	}

	void tellModuleRSoundLevel(int level, int tone) {

		if (DEBUG)
			Log.d(TAG, "tellModuleRSoundLevel    s   " + level
					+ "   t  " + tone);
		mBackCarModule.ToModule913FRSoundLevel((byte) 0x91, (byte) level,
				(byte) tone);

	}

	boolean isFloatRadarFull() {
		if (FloatRadarScale == 1.0f)
			return true;
		return false;
	}

	void CVBCtrlPageSettingInBackCar() {
		if (!isFloatRadarFull()) {

			ClickNoScaleShowFullFloatRadar();
			mBackCarService.getBackCar913Service()
					.ChangeBigAndSmallRadarTextUI(
							BackCarTag.A4_ChangeBigOrSmall_radar, 1);
		} else {
			ClickNoScaleShowHalfFloatRadar();
			mBackCarService.getBackCar913Service()
					.ChangeBigAndSmallRadarTextUI(
							BackCarTag.A4_ChangeBigOrSmall_radar, 0);
		}
		FlyBaseListener.focus(FlyUtil
				.HexToTag(BackCarTag.A4_AUDI_NOT_913_SET_CID));

		LayoutRemoveViewWithPageID(PageID.PAGE_913A4SETTING);

	}

	void CtrlPageSettingInBackCar() {
		if (!isFloatRadarFull()) {

			ClickNoScaleShowFullFloatRadar();
			mBackCarService.getBackCar913Service()
					.ChangeBigAndSmallRadarTextUI(
							BackCarTag.A4_ChangeBigOrSmall_radar, 1);
		} else {
			ClickNoScaleShowHalfFloatRadar();
			mBackCarService.getBackCar913Service()
					.ChangeBigAndSmallRadarTextUI(
							BackCarTag.A4_ChangeBigOrSmall_radar, 0);
		}
		FlyBaseListener.focus(FlyUtil.HexToTag(BackCarTag.A4_F_150VIDEO_CID));

		mBackCarService.getFlyBackCarMainView().LayoutRemoveViewWithPageID(
				PageID.PAGE_913A4SETTING);

	}

	void CtrlPageSettingOutSideBackCar() {
		if (!isFloatRadarFull()) {
			ClickNoScaleShowFullFloatRadar();
			mBackCarService.getBackCar913Service()
					.ChangeBigAndSmallRadarTextUI(
							BackCarTag.A4_ChangeBigOrSmall_radar, 1);

		} else {
			ClickNoScaleShowHalfFloatRadar();
			mBackCarService.getBackCar913Service()
					.ChangeBigAndSmallRadarTextUI(
							BackCarTag.A4_ChangeBigOrSmall_radar, 0);

		}

	}

	public void reSizeBgView() {
		// View bg = getTargetAnimatorView();

		View bg = mBackCarService.getFlyBackCarMainView().findChildViewWithTag(
				FlyUtil.HexToTag(BackCarTag.bg913Rear_150));

		if (bg != null) {
			int width = -1;
			if (isFloatRadarFull()) {
				width = FlyUtil.GET_SCREEN_W() - getFloatRadarSize();
			} else {
				width = FlyUtil.GET_SCREEN_W();
			}
			Log.d(TAG, "reSizeBgView " + width);

			FlyUtil.ChangeViewSize(bg, width, -1);
			bg = mBackCarService.getFlyBackCarMainView().findChildViewWithTag(
					FlyUtil.HexToTag(BackCarTag.bg913Front_150));
			FlyUtil.ChangeViewSize(bg, width, -1);

			bg = mBackCarService.getFlyBackCarMainView().findChildViewWithTag(
					FlyUtil.HexToTag(BackCarTag.bgcvb));
			FlyUtil.ChangeViewSize(bg, width, -1);

		}
	}

	public void setAllTrackBgHide() {

		View bg = mBackCarService.getFlyBackCarMainView().findChildViewWithTag(
				FlyUtil.HexToTag(BackCarTag.bg913Rear_150));

		bg.setVisibility(View.INVISIBLE);

		bg = mBackCarService.getFlyBackCarMainView().findChildViewWithTag(
				FlyUtil.HexToTag(BackCarTag.bg913Front_150));
		bg.setVisibility(View.INVISIBLE);

		Log.d(TAG, "setAllTrackBgHide");
		bg.invalidate();
	}

	public void CarLeftKeyDeal() {
		mBackCarService.getFlyFloatRadarView().LayoutRemoveViewWithPageID(
				PageID.PAGE_913A4SETTING);
		LayoutRemoveViewWithPageID(PageID.PAGE_913A4SETTING);
		if (mBackCarModule.getBackCarState() && IsCVBVdoMode())
			FlyBaseListener.focus(FlyUtil
					.HexToTag(BackCarTag.A4_AUDI_NOT_913_SET_CID));
		else {
			DigInVieoWay = get913CurrentVideoWay();
			switch (mBackCarService.get913Camera_fr_mode()) {
			case MsgType.FR_MODE:

				break;
			case MsgType.F_MODE:
				DigInVieoWay = 0;
				break;
			case MsgType.R_MODE:
				DigInVieoWay = 2;
				break;
			case MsgType.F_ORIR_MODE:
				DigInVieoWay = 0;
			}
			switchDigFocus(DigInVieoWay);
		}
	}

	void switchDigFocus(int way) {
		// if(mBackCarService.getBackCarMode() == mBackCarService.BACKCAR_914)
		switch (way) {
		case 0:
			FlyBaseListener.focus(FlyUtil
					.HexToTag(BackCarTag.A4_F_150VIDEO_CID));
			break;
		case 1:
			FlyBaseListener.focus(FlyUtil
					.HexToTag(BackCarTag.A4_F_180VIDEO_CID));
			break;
		case 2:
			FlyBaseListener.focus(FlyUtil
					.HexToTag(BackCarTag.A4_R_150VIDEO_CID));
			break;
		case 3:
			FlyBaseListener.focus(FlyUtil
					.HexToTag(BackCarTag.A4_R_180VIDEO_CID));
			break;
		}
	}

	public void DealActivityStop() {
		if (mBackCarService.getReversing() && !mBackCarModule.getBackCarState()) {
			DealBackCarMessages(MsgType.MSG_REMOVE, MsgType.GEY_FLYPAGE, null,
					0);
			mBackCar913Service.keyCodeBackExit();
		}
	}

	public void OrginBackToFLyaudio() {
		if (mBackCarModule.getBackCarState()) {
			// 913 \B5\B9\B3\B5\D6\D0ʱ\BA\F2 \B4\A6\C0\ED\B7ɸ\E8ҳ\C3\E6
			if (mBackCarService.getReversing()
					&& mBackCarService.getBackCarMode() == mBackCarService.BACKCAR_914) {
				// 11 17 xiugai
				// DealBackCarMessages(MsgType.MSG_REMOVE, MsgType.GEY_FLYPAGE,
				// null, 0);
				// Message msg = Message.obtain();
				// msg.what = MsgType.GEY_FLYPAGE;
				// DealBackCarMessages(MsgType.MSG_SENGDELAY,
				// MsgType.GEY_FLYPAGE,
				//
				// msg, MsgType.MSG_GEY_FLYPAGE_TIMEOUT);

			} else {
				// \B7\C7914 ģʽ\B5\B9\B3\B5
				BackCarshowFloatRadar();
			}
		}

		if (mBackCarService.getReversing())
			mBackCarService.showFloatApp(false, 0);// \CAյ\BD\B7ɸ\E8ҳ\C3\E6\D4\D9ͬ\B2\BDһ\B4\CE\D2\FE\B2\D8\D0\FC\B8\A1\BF\F2\CF\C2ȥ
	}

	public void DigIn913Mode() {
		DigInVieoWay = get913CurrentVideoWay();
		Log.d(TAG, "DigIn913Mode get913CurrentVideoWay " + DigInVieoWay);
		if (DigInVieoWay != -1) {
			switchDigVideoWay(DigInVieoWay);
		} else
			switchDigVideoWay(0);

	}

	protected void CVBBackCarMode() {
		if (!mBackCarModule.getBackCarState())
			DigIn913Mode();
		else {
			/*
			 * final int width =
			 * mBackCarService.getFlyBackCarMainView().getScreenW(); final int
			 * height = mBackCarService.getFlyBackCarMainView().getScreenH();
			 * 
			 * floatRadarReMeasue(0.5f); ChangeParamToJni_(width, height);
			 * 
			 * final View rearBg = mBackCarService.getFlyBackCarMainView()
			 * .findChildViewWithTag
			 * (FlyUtil.HexToTag(BackCarTag.bg913Rear_150));
			 * 
			 * FlyUtil.ChangeViewSize(rearBg,width, -1);
			 * mBackCarService.getBackCarView() .reSizeCVBSurfaceView(width,
			 * 430);
			 */
		}
	}

	void switchDigVideoWay(int way) {
		// if(mBackCarService.getBackCarMode() == mBackCarService.BACKCAR_914)
		Log.d(TAG, "switchDigVideoWay  " + way);
		switch (way) {
		case 0:
			mBackCarService
					.MakeAndSendMessageButton(BackCarTag.A4_F_150VIDEO_CID);
			break;
		case 1:
			mBackCarService
					.MakeAndSendMessageButton(BackCarTag.A4_F_180VIDEO_CID);
			break;
		case 2:
			mBackCarService
					.MakeAndSendMessageButton(BackCarTag.A4_R_150VIDEO_CID);
			break;
		case 3:
			mBackCarService
					.MakeAndSendMessageButton(BackCarTag.A4_R_180VIDEO_CID);
			break;
		}
	}

	void floatRadarReMeasue(float s) {
		if (s == 1.0f) {

			removeFloatRadar(1);
			mBackCarService.getFlyBackCarMainView().uiParentViewShow(
					FlyUtil.HexToTag(BackCarTag.A4_AUDI_SET_CID), true, false);

		} else {

			removeFloatRadar(1);
			mBackCarService.getFlyBackCarMainView().uiParentViewShow(
					FlyUtil.HexToTag(BackCarTag.A4_AUDI_SET_CID), false, false);
		}
	}

	void floatCVBRadarReMeasue(float s) {
		Log.d(TAG, "floatCVBRadarReMeasue ");
		if (s == 1.0f) {

			removeFloatRadar(1);
			mBackCarService.getFlyBackCarMainView().uiParentViewShow(
					FlyUtil.HexToTag(BackCarTag.A4_AUDI_SET_CID), true, false);
			FlyBaseListener.focus(FlyUtil
					.HexToTag(BackCarTag.A4_AUDI_NOT_913_SET_CID));

		} else {

			removeFloatRadar(1);
			mBackCarService.getFlyBackCarMainView().uiParentViewShow(
					FlyUtil.HexToTag(BackCarTag.A4_AUDI_SET_CID), false, false);
			FlyBaseListener.focus(FlyUtil
					.HexToTag(BackCarTag.A4_AUDI_NOT_913_SET_CID));
		}
	}

	void saveFloatScaleAndUpdate(float value) {
		Log.d(TAG, "saveFloatScaleAndUpdate  " + value);
		FloatRadarScale = value;
		saveEditToPreference(Floart_Radar_Scale, FloatRadarScale);
		if (mBackCarService.getReversing())
			floatRadarReMeasue(FloatRadarScale);

	}

	void sendMsgChangeTextUi(int cid, int vuale) {
		mBackCarService.getBackCar913Service().change_audiA4_sound_levelUI(cid,
				vuale);

	}

	void sendMsgChangeTitleText(int cid, int vuale) {
		mBackCarService.getBackCar913Service().change_audiA4_title_levelUI(cid,
				vuale);
	}

	void show_F_soundUi_set(int level) {
		switch (level) {
		case 0:
			mBackCarModule.showFlyButtonBg(BackCarTag.A4_AUDI_FRONT_SOUND_Low,
					(byte) 1);
			break;
		case 1:
			mBackCarModule.showFlyButtonBg(BackCarTag.A4_AUDI_FRONT_SOUND_Mid,
					(byte) 1);
			break;
		case 2:
			mBackCarModule.showFlyButtonBg(BackCarTag.A4_AUDI_FRONT_SOUND_High,
					(byte) 1);
		}
	}

	void show_R_soundUi_set(int level) {
		switch (level) {
		case 0:
			mBackCarModule.showFlyButtonBg(BackCarTag.A4_AUDI_REAR_SOUND_Low,
					(byte) 1);
			break;
		case 1:
			mBackCarModule.showFlyButtonBg(BackCarTag.A4_AUDI_REAR_SOUND_Mid,
					(byte) 1);
			break;
		case 2:
			mBackCarModule.showFlyButtonBg(BackCarTag.A4_AUDI_REAR_SOUND_High,
					(byte) 1);
		}

	}

	void show_FR_Mode_Ui_set(int mode) {
		switch (mode) {
		case 0:
			mBackCarModule.showFlyButtonBg(BackCarTag.Audi_FR_MODE, (byte) 1);
			FlyBaseListener.focus(FlyUtil.HexToTag(BackCarTag.Audi_FR_MODE));
			break;
		case 1:
			mBackCarModule.showFlyButtonBg(BackCarTag.Audi_F_MODE, (byte) 1);
			FlyBaseListener.focus(FlyUtil.HexToTag(BackCarTag.Audi_F_MODE));
			break;
		case 2:
			mBackCarModule.showFlyButtonBg(BackCarTag.Audi_R_MODE, (byte) 1);
			FlyBaseListener.focus(FlyUtil.HexToTag(BackCarTag.Audi_R_MODE));
			break;
		case 3:
			mBackCarModule.showFlyButtonBg(BackCarTag.Audi_F_R_MODE, (byte) 1);
			FlyBaseListener.focus(FlyUtil.HexToTag(BackCarTag.Audi_F_R_MODE));
		}

	}

	void initAudiA4_Set_UI() {
		/*
		 * View tmpv =
		 * FlyBaseListener.mfindViewWithTag(BackCarTag.A4_AUDI_SET_str);
		 * if(isFloatRadarFull()){
		 * mBackCarModule.showFlyButtonBg(BackCarTag.A4_AUDI_SET_CID, (byte)0);
		 * 
		 * int right = getFloatRadarSize();
		 * 
		 * FlyUtil.ChangeViewPisition(tmpv, right, -1); } else {
		 * mBackCarModule.showFlyButtonBg(BackCarTag.A4_AUDI_SET_CID, (byte)1);
		 * 
		 * FlyUtil.ChangeViewPisition(tmpv ,0, -1); }
		 */
	}

	void initAudi_A4_F_Sound_UI(int level) {
		switch (level) {
		case 0:
			mBackCarModule.showFlyButtonBg(BackCarTag.A4_AUDI_FRONT_SOUND_Low,
					(byte) 0);
			break;
		case 1:
			mBackCarModule.showFlyButtonBg(BackCarTag.A4_AUDI_FRONT_SOUND_Mid,
					(byte) 0);
			break;
		case 2:
			mBackCarModule.showFlyButtonBg(BackCarTag.A4_AUDI_FRONT_SOUND_High,
					(byte) 0);
		}
	}

	void initAudi_A4_R_Sound_UI(int level) {
		switch (level) {
		case 0:
			mBackCarModule.showFlyButtonBg(BackCarTag.A4_AUDI_REAR_SOUND_Low,
					(byte) 0);
			break;
		case 1:
			mBackCarModule.showFlyButtonBg(BackCarTag.A4_AUDI_REAR_SOUND_Mid,
					(byte) 0);
			break;
		case 2:
			mBackCarModule.showFlyButtonBg(BackCarTag.A4_AUDI_REAR_SOUND_High,
					(byte) 0);
		}
	}

	public void hideDanKuangSetting() {
		initAudi_A4_select_Button();
		FlyBaseListener.UIParentViewShow(
				FlyUtil.HexToTag(BackCarTag.A4_AUDI_REAR_SOUND_Low), false,
				false);
		FlyBaseListener.UIParentViewShow(
				FlyUtil.HexToTag(BackCarTag.A4_AUDI_FRONT_SOUND_Low), false,
				false);
		FlyBaseListener.UIParentViewShow(
				FlyUtil.HexToTag(BackCarTag.Audi_F_MODE), false, false);

	}

	void initAudi_A4_select_Button() {
		mBackCarModule.showFlyButtonBg(BackCarTag.A4_AUDI_FRONT_SOUND_CID,
				(byte) 0);
		mBackCarModule.showFlyButtonBg(BackCarTag.A4_AUDI_REAR_SOUND_CID,
				(byte) 0);
		mBackCarModule.showFlyButtonBg(BackCarTag.Audi_SELECTBUTTON, (byte) 0);
	}

	void initAudi_A4_Button() {
		Log.d("cid", "initAudi_A4_Button0 ");
		switch (mBackCarService.get913Camera_fr_mode()) {

		case MsgType.FR_MODE:
			mBackCarModule.showFlyButtonBg(BackCarTag.A4_F_150VIDEO_CID,
					(byte) 0);
			mBackCarModule.showFlyButtonBg(BackCarTag.A4_F_180VIDEO_CID,
					(byte) 0);
			mBackCarModule.showFlyButtonBg(BackCarTag.A4_R_150VIDEO_CID,
					(byte) 0);
			mBackCarModule.showFlyButtonBg(BackCarTag.A4_R_180VIDEO_CID,
					(byte) 0);
			break;
		case MsgType.F_ORIR_MODE:
		case MsgType.F_MODE:
			mBackCarModule.showFlyButtonBg(BackCarTag.A4_F_150VIDEO_CID,
					(byte) 0);
			mBackCarModule.showFlyButtonBg(BackCarTag.A4_F_180VIDEO_CID,
					(byte) 0);
			mBackCarModule.showFlyButtonBg(BackCarTag.A4_R_150VIDEO_CID,
					(byte) 2);
			mBackCarModule.showFlyButtonBg(BackCarTag.A4_R_180VIDEO_CID,
					(byte) 2);
			break;
		case MsgType.R_MODE:
			mBackCarModule.showFlyButtonBg(BackCarTag.A4_F_150VIDEO_CID,
					(byte) 2);
			mBackCarModule.showFlyButtonBg(BackCarTag.A4_F_180VIDEO_CID,
					(byte) 2);
			mBackCarModule.showFlyButtonBg(BackCarTag.A4_R_150VIDEO_CID,
					(byte) 0);
			mBackCarModule.showFlyButtonBg(BackCarTag.A4_R_180VIDEO_CID,
					(byte) 0);

			break;

		}
		Log.d("cid", "initAudi_A4_Button1 ");
	}

	@Override
	public void NavigationNotifyChange(int data) {
		super.NavigationNotifyChange(data);
		removeFloatRadar(1);
		if (data == FULLSCREEN) {
			if (mBackCarService.getReversing())
				// if (isFloatRadarFull())
				// mBackCarService.getBackCarView().reSizeSurfaceView(
				// mBackCarService.getFlyBackCarMainView()
				// .getScreenW(), -1);

				DealBackCarMessages(MsgType.MSG_REMOVE, MsgType.RESIZESURFACE,
						null, 0);
			Message msg = Message.obtain();
			msg.what = MsgType.RESIZESURFACE;
			DealBackCarMessages(MsgType.MSG_SENGDELAY, MsgType.RESIZESURFACE,
					msg, MsgType.RESIZESURFACE_TIMEOUT);

		}
	}

	// floatradarpage such as AuDi A4(556)

	
	public void FromLpcRadarShow(){
		
		if (!mBackCarService.getReversing()) {
			BaseModule.getBaseModule().showFloatRadar(
					PageID.PAGE_913FLOATRADAR);
			if (BaseModule.getBaseModule().isShowNavigationbar()) {
				BaseModule.getBaseModule().SyncScreenSize(800, 480);
			}
		}
	}
	
	public void FromLpcRadaHide(){
		removeFloatRadar(
				PageID.PAGE_913FLOATRADAR);
	}
	@Override
	public void showFloatRadar(int pageid) {
		if (!mBackCarService.getReversing())
			if (!mBackCarService.getFlyFloatRadarView().isShowing()) {

				mBackCarService.getFlyFloatRadarView().initFloatRadarView(
						pageid);
				mBackCarService.getFlyFloatRadarView().ShowFloatRadarView();
				FlyBaseListener.focus(FlyUtil
						.HexToTag(BackCarTag.A4_AUDI_Float_Radar_Bg));
			}
		/*
		 * DealBackCarMessages(MsgType.MSG_REMOVE, MsgType.REMOVE_FLOATRADAR,
		 * null, 0); Message msg = Message.obtain(); msg.what =
		 * MsgType.REMOVE_FLOATRADAR; DealBackCarMessages(MsgType.MSG_SENGDELAY,
		 * MsgType.REMOVE_FLOATRADAR, msg,
		 * MsgType.MSG_REMOVE_FLOATRADAR_TIMEOUT);
		 */

	}

	@Override
	public void removeFloatRadar(int needSendMsgToLpc) {

		mBackCarService.getFlyFloatRadarView().RemoveFloatRadarView(
				needSendMsgToLpc);
		mBackCarService.getFlyFloatRadarView().LayoutRemoveViewWithPageID(
				PageID.PAGE_913A4SETTING);

	}

	public void BackCarshowFloatRadar() {

		if (isFloatRadarFull())
			setSufaceViewHalfScreen();
		else
			setSufaceViewFullScreen();
	}

	public void setSufaceViewFullScreen() {

		// String size =
		// mBackCarService.getFlyBackCarMainView().getScreenW()+"";
		// SystemProperties.set("persist.backcar.screensize", size);
		DealBackCarMessages(MsgType.MSG_REMOVE, MsgType.SETSURFACE, null, 0);
		final int width = FlyUtil.GET_SCREEN_W();
		final int height = mBackCarService.getFlyBackCarMainView().getScreenH();
		Log.d(TAG, "setSufaceViewFullScreen " + width);
		FloatRadarScale = 0.5f;
		saveFloatScaleAndUpdate(FloatRadarScale);
		needToChangeParamToJni(width, height);
		reSizeBgView();

		// mBackCarService.getBackCarView().reSizeSurfaceView(width, -1); 1117

		DealBackCarMessages(MsgType.MSG_REMOVE, MsgType.SETSURFACE, null, 0);
		Message msg = Message.obtain();
		msg.what = MsgType.SETSURFACE;
		msg.arg1 = BackCarTag.SetHalfSurfaceView;

		Bundle mbd = new Bundle();
		mbd.putInt(BackCarTag.INTKEY, width);
		msg.setData(mbd);
		DealBackCarMessages(MsgType.MSG_SENGDELAY, MsgType.SETSURFACE, msg,
				MsgType.MSG_SETFULLSURFACE_TIMEOUT);
	}

	public void setSufaceViewHalfScreen() {

		final int width = FlyUtil.GET_SCREEN_W() - getFloatRadarSize();
		final int height = mBackCarService.getFlyBackCarMainView().getScreenH();
		FloatRadarScale = 1.0f;
		Log.d(TAG, "setSufaceViewHalfScreen " + width);
		needToChangeParamToJni(width, height);
		reSizeBgView();
		saveFloatScaleAndUpdate(FloatRadarScale);

		// mBackCarService.getBackCarView()
		// .reSizeSurfaceView(width, -1);

		DealBackCarMessages(MsgType.MSG_REMOVE, MsgType.SETSURFACE, null, 0);
		Message msg = Message.obtain();
		msg.what = MsgType.SETSURFACE;
		msg.arg1 = BackCarTag.SetHalfSurfaceView;

		Bundle mbd = new Bundle();
		mbd.putInt(BackCarTag.INTKEY, width);
		msg.setData(mbd);
		DealBackCarMessages(MsgType.MSG_SENGDELAY, MsgType.SETSURFACE, msg,
				MsgType.MSG_SETHALFSURFACE_TIMEOUT);

	}

	public int getFloatRadarSize() {
		// View radar = mBackCarService.getFlyFloatRadarView().getMainView();
		// if(radar!=null){
		// if(radar.getMeasuredWidth() == 0)
		// radar.requestLayout(); //getMeasuredWidth =0 frist time bug
		return mBackCarService.floatRadarWidth; // config.xml
		// }
		// Log.d("DDD", "radar null");
		// return 0;
	}

	public void ClickNoScaleShowFullFloatRadar() {

		setSufaceViewHalfScreen();

	}

	public void ClickNoScaleShowHalfFloatRadar() {

		setSufaceViewFullScreen();

	}

	public void ClickScaleBigShowFloatRadar(int pageid) {
		saveFloatScaleAndUpdate(1.0f);
		clickSetSufaceViewHalfScreen();
	}

	public void ClickScaleSmallFloatRadar(int pageid) {
		saveFloatScaleAndUpdate(0.5f);
		clickSetSufaceViewFullScreen();

	}

	public void clickSetSufaceViewFullScreen() {

		View radar = mBackCarService.getFlyFloatRadarView().getMainView();
		// String size =
		// mBackCarService.getFlyBackCarMainView().getScreenW()+"";
		// SystemProperties.set("persist.backcar.screensize", size);

		int width = FlyUtil.GET_SCREEN_W();

		View aView = getTargetAnimatorView();
		if (aView != null)
			new StretchViewAnimator(aView).telescopicAnimation(-1, width,
					mParamToJni);
		new StretchViewAnimator(mBackCarService.getBackCarView())
				.telescopicAnimation(-1, width, mBackCarViewChange);

		new ScalehalfAnimator(mBackCarService.getFlyFloatRadarView()
				.getMainView()).telescopicAnimation(1.0f, 0.5f, 1.0f, 0.5f);

		// mBackCarModule.showFlyButtonBg(BackCarTag.A4_AUDI_SET_CID, (byte)0);
		final View tmpv = FlyBaseListener.mfindViewWithTag(FlyUtil
				.HexToTag(BackCarTag.A4_AUDI_SET_CID));

	}

	// A4_AUDI_Float_Radar_Bg

	public void clickSetSufaceViewHalfScreen() {
		View radar = mBackCarService.getFlyFloatRadarView().getMainView();
		int width = FlyUtil.GET_SCREEN_W() - radar.getMeasuredWidth();
		String size = width + "";
		SystemProperties.set("persist.backcar.screensize", size);

		View aView = getTargetAnimatorView();

		if (aView != null)
			new StretchViewAnimator(aView).telescopicAnimation(-1, width,
					mParamToJni);
		new StretchViewAnimator(mBackCarService.getBackCarView())
				.telescopicAnimation(-1, width, mBackCarViewChange);

		new ScalehalfAnimator(radar)
				.telescopicAnimation(0.5f, 1.0f, 0.5f, 1.0f);

		// mBackCarModule.showFlyButtonBg(BackCarTag.A4_AUDI_SET_CID, (byte)1);

	}

	View getTargetAnimatorView() {
		int way = get913CurrentVideoWay();
		if (way == 2)
			TrackBgTag = FlyUtil.HexToTag(BackCarTag.bg913Rear_150);
		else if (way == 0)
			TrackBgTag = FlyUtil.HexToTag(BackCarTag.bg913Front_150);

		return mBackCarService.getFlyBackCarMainView().findChildViewWithTag(
				TrackBgTag); // test
	}

	public void loadRadarChangeMapFromXML() {
		InputStream inStream = null;

		inStream = PluginProxyContext.getInstance().getXmlInRawFromSkin(
				PluginProxyContext.externModuleContext, radarMapFileName);
		try {
			if (inStream != null) {
				Log.d(TAG, "loadRadarChangeMapFromXML   not null ");

				RadarChangeMapXMLParser parser_front = new RadarChangeMapXMLParser();
				XMLTool.parse(inStream, parser_front);
				frontRadarChangeMap = parser_front.getRadarChangeMap();

				/*
				 * for(int i = 1; i <15; i++){ HashMap<String, Integer> tmp =
				 * rearRadarChangeMap.get(i); Iterator<Map.Entry<String,
				 * Integer>> iterators = tmp.entrySet().iterator(); while
				 * (iterators.hasNext()) { Entry<String, Integer> entry =
				 * iterators.next(); Log.d("DDD",
				 * "getValue  "+entry.getValue()+"  "+entry.getKey()); } }
				 */

			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			Log.d(TAG, "loadRadarChangeMapFromXML fail ");
		}
	}

	public class ParamToJni implements TelescopicTargetCallback {

		@Override
		public void TelescopicCallback(int w, int h) {
			needToChangeParamToJni(w, h);

		}

		@Override
		public void TelescopicCallback() {

		}
	}

	public class BackCarViewChange implements TelescopicTargetCallback {

		@Override
		public void TelescopicCallback(int w, int h) {
			mBackCarService.getBackCarView().SetHodlerFixSize(w, h);

		}

		@Override
		public void TelescopicCallback() {

		}
	}

}
