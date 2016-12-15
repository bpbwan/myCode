package com.flyaudio.backcar.modules;

import com.flyaudio.backcar.BackCarService;
import com.flyaudio.backcar.FlyBackCarMainView;

import android.os.Message;
import android.os.Bundle;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;

import com.flyaudio.backcar.MsgType;
import com.flyaudio.backcar913.BackCarTag;

import android.os.SystemProperties;

import com.flyaudio.backcar913.BackCar913Service;
import com.flyaudio.backcar.BackCarModule;

import android.os.SystemProperties;

import com.flyaudio.backcar913.FlyBaseListener;

import android.app.KeyguardManager;
import android.content.Context;

import com.flyaudio.backcar.trackbitmap.BackCarTrack;
import com.flyaudio.backcar.util.FlyUtil;
import com.autochips.backcar.BackCar;
import com.flyaudio.globaldefine.PageID;
import com.flyaudio.keyevent.BaseKeyListener.KeyListenerCallback;
import com.flyaudio.keyevent.*;
import com.flyaudio.tool.GLTrackViewToolHelper;

public abstract class BaseModule {

	public static final String USER_PRESENT = "android.intent.action.USER_PRESENT";

	public String TAG = "BackCarBaseModule";
	public static BackCarModule mBackCarModule;
	public static BaseModule mBaseModule;
	public static BackCarService mBackCarService;
	public static BackCar913Service mBackCar913Service;
	public static SharedPreferences mSharedPreferences;
	public int DigInVieoWay = 0; // 0 150f, 1 180f, 2 150r 3 180r
	public String DigInSet = "DigInVideoSetWay";
	public int TrackJniW = -1;
	public int TrackJniH = -1;
	public int CurrentCarPage = -1;
	public boolean needReShowVideo = false;
	private int navigationbar = -1;
	public final int FULLSCREEN = 0;
	public final int HALFSCREEN = 1;
	public final int SENDFLOAT_CLOSE = 0;
	public final int NO_SENDFLOAT_CLOSE = 1;

	public boolean DEBUG = false;
	public boolean DEBUG_L2 = false;
	public BackCarTrack mBackCarTrack = null;
	public BackCar913Service.ProtocolState STALL;

	public boolean PowerState = false;
	public boolean AuxLine_show = true;
	public boolean powerOnFirstTime = false;
	public boolean ParkBrake = false;
	//bu hui rang daleida shou an jiang yingxiang tuichu
	public static boolean hasNoBntExit = false;
	public boolean RightVdoStatus = false;
	
	public abstract void BaseModulepreStart();

	public abstract void BaseModulepreStop();

	public abstract void OrginBackToFLyaudio();

	public abstract void sendMessageToModule(Message msg);

	public abstract void DealActivityStop();

	public static BaseModule getBaseModule() {

		return mBaseModule;

	}

	public void Save913CurrentVideoWay(int way) {

		DigInVieoWay = way;
		saveEditToPreference(DigInSet, DigInVieoWay);
	}

	public int get913CurrentVideoWay() {
		int ret = -1;
		ret = mSharedPreferences.getInt(DigInSet, -1);

		return ret;
	}

	public BaseModule(BackCarService Service) {
		mBackCarService = Service;
		mBaseModule = this;
		mSharedPreferences = mBackCarService.getPreferencesFromService();
		mBackCarModule = mBackCarService.getBackCarModule();
		mBackCar913Service = mBackCarService.getBackCar913Service();
		TrackJniW = mSharedPreferences.getInt("TrackJniW", -1);
		TrackJniH = mSharedPreferences.getInt("TrackJniH", -1);
		mBackCarTrack = BackCarTrack.getInstance();
		STALL = mBackCar913Service.GetProtocolState();

		PowerState = getPreferenceBoolean("PowerState", false);
		// ����ǳ�ʼ��û�����ߵľ�Ĭ�ϲ���ʾ������
		AuxLine_show = !mBackCarService.disableAssist;

		int ret = mBackCarService.get913Camera_fr_mode();
		setUpFrontRearDevice(ret);
	}

	public void initFr_mode(int value) {
	}

	public void SetColor_BackCar() {
	}

	public void SetColor_ARM2() {
	}

	public void FloatRadarExit(int msg) {
	}

	public void showFloatRadar(int page) {
	}

	public void removeFloatRadar(int msg) {
	}

	public void showNoticeView(boolean disableAssist, boolean show) {
	}

	public void FromModuleMsgCtrAuxLine(byte data) {
	}

	public void FromModuleMsgCtrPhoneCall(byte data) {
	}

	public void removeChoiceView() {
	}

	public void showChoiceView() {
	}

	public void DealWithTrackAngle(Bundle data) {
	}

	public void DealWithPSTALL(Bundle data) {
	}

	public void DealWithRSTALL(Bundle data) {
	}

	public void DealWithNSTALL(Bundle data) {
	}

	public void DealWithDSTALL(Bundle data) {
	}

	public int GetChangeMultiRadarUi(int mControlID, int level) {
		return 0;
	}

	public void FloatRadarViewRemove() {
	}

	public void removeMessageToModule(int what, int time) {
	}

	public void InitFrontRearDeviceUI(int frmode) {
	};

	public void CarLeftKeyDeal() {
	}

	public void hideDanKuangSetting() {
	}

	public void EdgeRadarDealWithData(byte[] paramer) {
	}

	public void FromLpcRadarShow(){}
	public void FromLpcRadaHide(){}
	public BaseKeyListener SetOnKeyListener(String mkeyStr, View vm,
			KeyListenerCallback cb) {
		return null;
	};

	public void changeRadarMultiUiDStall() {
	}

	public void changeRadarMultiUiRStall() {
	}
	public void onStopDelay(){}
	public void onStopDelayCancel(){}
	
	public void backcarstop() {
		mBackCarModule.RemoveBackCarView();
		mBackCarModule.setCurPageID(0x0);
		mBackCarModule.RemoveRadarView();
		mBackCarModule.removeActivity();
		mBackCarModule.TurnModuleStop();

		mBackCarModule.setBackCarLock(false);
		mBackCarService.DealBackCarMessages(MsgType.MSG_REMOVE,
				MsgType.MSG_CHECK_SIGNAL, 0);
		mBackCarService.DealBackCarMessages(MsgType.MSG_REMOVE,
				MsgType.MSG_STOPNOTICE, 0);
	}

	public void saveEditToPreference(String st, int value) {
		Editor editor = mSharedPreferences.edit();
		editor.putInt(st, value);
		editor.commit();

	}

	public void saveEditToPreference(String st, float value) {
		Editor editor = mSharedPreferences.edit();
		editor.putFloat(st, value);
		editor.commit();

	}

	public void saveEditToPreference(String st, boolean value) {
		Editor editor = mSharedPreferences.edit();
		editor.putBoolean(st, value);
		editor.commit();

	}

	public int getPreferenceInt(String st, int df_value) {
		return mSharedPreferences.getInt(st, df_value);
	}

	public boolean getPreferenceBoolean(String st, boolean df_value) {
		return mSharedPreferences.getBoolean(st, df_value);
	}

	public void LayoutAddViewWithPageID(View v, int pageid) {
		mBackCarService.getFlyBackCarMainView().LayoutAddViewWithPageID(v,
				pageid);
	}

	public void FloatAddViewWithPageID(View v, int pageid) {
		mBackCarService.getFlyFloatRadarView().WMAddViewWithPageID(v, pageid);
	}

	public void LayoutRemoveViewWithPageID(int pageid) {
		mBackCarService.getFlyBackCarMainView().LayoutRemoveViewWithPageID(
				pageid);
	}

	public void FloatRemoveViewWithPageID(int pageid) {
		mBackCarService.getFlyFloatRadarView().WMRemoveViewWithPageID(pageid);
	}

	public void needToChangeParamToJni(int w, int h) { // �켣��С��Ҫ�仯�ģ�ͨ��BackCarTrackȥ����
		if (TrackJniW != w || TrackJniH != h) {
			TrackJniW = w;
			TrackJniH = h;

		} else {
			return;
		}
		saveEditToPreference("TrackJniW", TrackJniW);
		saveEditToPreference("TrackJniH", TrackJniH);
		ChangeParamToJni_(w, h);

	}

	public void ChangeParamToJni_(int w, int h) {
		mBackCarService.getFlyBackCarMainView().needChangeParamJni(w, h);

	}

	public void ChangeParamToJni() {
		if (TrackJniW != -1 && TrackJniH != -1)
			mBackCarService.getFlyBackCarMainView().needChangeParamJni(
					TrackJniW, TrackJniH);

	}

	/*
	 * use for digin 913 �ϵ���Ƶ��������
	 */
	public void BLACKPAGESHOW() {
		DealBaseModuleMessages(MsgType.MSG_REMOVE, MsgType.BLACKPAGE_REMOVE, 0);
		// mBackCarService.getBackCarModule().ShowUiView(BackCarTag.BLACKPAGE,
		// true);
		DealBaseModuleMessages(MsgType.MSG_SENGDELAY, MsgType.BLACKPAGE_REMOVE,
				MsgType.BLACKPAGE_REMOVE_TIMEOUT);
		mBackCarService.getFlyBackCarMainView().BlackPageShow();
		isFirstTimePowerOff();
	}

	public void BLACKPAGEHIDE() {
		mBackCarService.getFlyBackCarMainView().BlackPageHide();
		// mBackCarService.getBackCarModule().ShowUiView(BackCarTag.BLACKPAGE,
		// false);
	}

	// G6ƽ̨ ��ɫ�ʵ���ҳ�����غ���ʾ
	public void SETTINGCOLORHIDE() {

		mBackCarService.getFlyBackCarMainView().UIParentViewShow(
				BackCarTag.SETTINGBACK_TAG, false, false);
	}

	public void SETTINGCOLORSHOW() {
		Log.d(TAG, "SETTINGCOLORSHOW  ");
		// TODO:
	}

	public void TrackBlackPageShow(int w, int h, int top, int gravity) {
		// DealBaseModuleMessages(MsgType.MSG_REMOVE, MsgType.TRACKBLACKPAGE,
		// 0);
		// mBackCarService.getFlyBackCarMainView().BlackPageShow(w, h, top,
		// gravity);
		// DealBaseModuleMessages(MsgType.MSG_SENGDELAY, MsgType.TRACKBLACKPAGE,
		// MsgType.TRACKBLACKPAGE_REMOVE_TIMEOUT);
	}

	public void TrackBlackPageHide() {
		// mBackCarService.getFlyBackCarMainView().BlackPageHide();
	}

	public Handler BaseMsgHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch ((int) msg.what) {
			case MsgType.BLACKPAGE_REMOVE:
				BLACKPAGEHIDE();
				break;
			case MsgType.STOP_SETTING_COLOR:
				SETTINGCOLORHIDE();
				break;
			case MsgType.SHOW_SETTING_COLOR:
				SETTINGCOLORSHOW();
				break;
			case MsgType.TRACKBLACKPAGE:
				TrackBlackPageHide();
				break;
			case MsgType.FLOATRADAR_SHOW_DELAY:
				FromLpcRadarShow();
				break;
			case MsgType.FLOATRADAR_HIDE_DELAY:
				FromLpcRadaHide();
				break;
			}
		}
	};

	
	
	public void DealBaseModuleMessages(int DealMessages, int iMsgType, int time) {
		if (DealMessages == MsgType.MSG_SENG) {
			BaseMsgHandler.sendEmptyMessage(iMsgType);
		}
		if (DealMessages == MsgType.MSG_SENGDELAY) {
			BaseMsgHandler.sendEmptyMessageDelayed(iMsgType, time);
		}
		if (DealMessages == MsgType.MSG_REMOVE) {
			BaseMsgHandler.removeMessages(iMsgType);
		}
	}

	public void DealBackCarMessages(Handler myMsgHandler , int DealMessages, int what,
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
	
	protected void sendMsgDelay(Handler myMsgHandler ,int msgtype, int delay) {

		DealBackCarMessages( myMsgHandler ,MsgType.MSG_REMOVE, msgtype, null, 0);
		Message msg = Message.obtain();
		msg.what = msgtype;

		DealBackCarMessages(myMsgHandler ,MsgType.MSG_SENGDELAY, msgtype, msg, delay);
	}
	
	// param focus 913 ǰ�� ��Ƶ��ʼ�� �н������
	public void front913Video(String focusTag) {
		Log.d(TAG, "front913Video");

		mBackCar913Service.front913Video();
		mBackCar913Service.setBackGround();
		FlyBaseListener.focus(focusTag);
	}

	public void rear913Video(String focusTag) {
		Log.d(TAG, "rear913Video");
		mBackCar913Service.rear913Video();
		mBackCar913Service.setBackGround();
		FlyBaseListener.focus(focusTag);
	}

	// ������������Ҫȥ��logo
	public void STOP_LOGO() {
		if (mBackCarService.canWeStopLogo()) {
			mBackCarService.setHasStopLogo();
			// mBackCarService.getFlyBackCarMainView()
			// .sendParam((byte)0x20, 3, new float[0]);
		}
	}

	public void GetNotifyCurrentCarPage(int current) {
		CurrentCarPage = current;

	}

	public int CurrentCarPage() {
		return CurrentCarPage;
	}

	public void BaseModuleStart() {
		mBackCarService.StopTXZ();

		if (!mBackCarModule.GetDigInCameraOn_OffStatus())
			mBackCarModule.ToModule913CameraOn_Off((byte) 0x1);

		switch (mBackCarService.mgetBackCarMode()) {
		case BackCarService.BACKCAR_914:
			mBackCarService.MakeAndSendMessageWithBundle(MsgType.FLYOBJ,
					BackCarTag.DigInShowMainView, null);
			break;
		case BackCarService.BACKCAR_CVB_SV:
			FindViewWithCidAndSetVisiable(PageID.PAGE_VDO_OSD_UI, View.GONE);
			mBackCar913Service.ShowBgView(2, false);
			break;
		default:
			mBackCarService.getFlyBackCarMainView().setCvbFronView();
		}
		// if (mBackCarService.mgetBackCarMode() == mBackCarService.BACKCAR_914)
		// // ����Ǳ��۵�ǰ����
		// mBackCarService.MakeAndSendMessageWithBundle(MsgType.FLYOBJ,
		// BackCarTag.DigInShowMainView, null);
		// else if(mBackCarService.mgetBackCarMode() ==
		// mBackCarService.BACKCAR_CVB_SV)
		// mBackCar913Service.ShowBgView(2, false);
		// else
		// mBackCarService.getFlyBackCarMainView().setCvbFronView();
		//

	}

	public void BaseModuleExit() {
		mBackCarModule.tellmoduleStallDStatus(false);
		mBackCarModule.ToModule913CameraSetLight((byte) 0xff);
		mBackCarModule.ToModule913CameraOn_Off((byte) 0x0);
		mBackCar913Service.setBackCar913StallDState(false);
		mBackCar913Service.set913Lock(false);
		mBackCar913Service.DelayUnLock();
		mBackCarService.setBcStop(true);
		needReShowVideo = true;
	}

	public void key_backcar913stop() {
		mBackCarModule.backcar913stop("gethomekey");
	}
	
	public void CarSpeedLimit(){
		
		
	}
	public void sendBroadcastToSystemUI(String pageName_ID, String icon,
			String content, int type) {
		mBackCarService.sendBroadcastToSystemUI(pageName_ID, icon, content,
				type);
	}

	// 1 fenping 0 quanping
	public void setNavigationbar(int data) {
		if (navigationbar != data) {
			navigationbar = data;
			Log.d(TAG, "setNavigationbar " + data);

			NavigationNotifyChange(navigationbar);
		}
	}

	public void SyncScreenSize(int w, int h) {
		mBackCarService.getFlyFloatRadarView().SyncScreenSize(w, h);

	}

	public void NavigationNotifyChange(int data) {

	}

	// ͬ��ͬ��ҳȥ���˵Ĺ㲥����
	public void SyncUser_PresentState() {
		DealBaseModuleMessages(MsgType.MSG_REMOVE, MsgType.BLACKPAGE_REMOVE, 0);
		DealBaseModuleMessages(MsgType.MSG_SENGDELAY, MsgType.BLACKPAGE_REMOVE,
				MsgType.SyncUser_TIMEOUT);
		PowerState = true;
		saveEditToPreference("PowerState", PowerState);

		mBackCarService.DealBackCarMessages(MsgType.MSG_SENGDELAY,
				MsgType.MSG_SETGAMMA, MsgType.SyncUser_TIMEOUT);
		Log.d(TAG, "SyncUser_PresentState ");
	}

	public void DealPowerOff() {
		PowerState = false;

	}

	// PowerState true �������� false ������ȥ
	public boolean GetPowerState() {
		Log.d(TAG, "PowerState " + PowerState);
		return PowerState;
	}

	public void setPowerState(boolean flag) {
		PowerState = flag;
	}

	public boolean Not913ModeAndNotStartByUser() {

		if (mBackCarService.mgetBackCarMode() == mBackCarService.BACKCAR_914
				|| mBackCar913Service.is913StartByUser())
			return false;
		return true;
	}

	public void BeforeSystemExit() {
		mBackCarService.syncFloatExitVideo();
	}

	public void SIGNAL_READY() {
		if (mBackCarService.getBackCarMode() != mBackCarService.BACKCAR_914
				&& !mBackCar913Service.is913StartByUser())
			mBackCarModule.ShowUiView(BackCarTag.BLACKPAGE, false);
	}

	// setcolor ------------------------------------------------------
	public void SetVdoBrightness(int progress) {
		Log.d("DDD", "vdobri " + progress);
		BackCar.SetBrightness(progress);
		saveEditToPreference("hasSetColor", true);
		saveEditToPreference("vdobri", progress);
	}

	public void SetVdoHue(int progress) {

		BackCar.SetHue(progress);
		saveEditToPreference("hasSetColor", true);
		saveEditToPreference("vdohue", progress);
	}

	public void SetVdoSaturation(int progress) {
		BackCar.SetSaturation(progress);
		saveEditToPreference("hasSetColor", true);
		saveEditToPreference("vdosta", progress);
	}

	public void SetScrBrightnessLevel(int progress) {
		Log.d("DDD", "scrbri " + progress);
		BackCar.BC_SetBrightnessLevel(progress);
		saveEditToPreference("scrbri", progress);
	}

	public void SetScrHueLevel(int progress) {
		Log.d("DDD", "scrhue " + progress);
		BackCar.BC_SetHueLevel(progress);
		saveEditToPreference("hasSetColor", true);
		saveEditToPreference("scrhue", progress);
	}

	public void SetScrSaturationLevel(int progress) {
		Log.d("DDD", "scrsta " + progress);
		BackCar.BC_SetSaturationLevel(progress);
		saveEditToPreference("hasSetColor", true);
		saveEditToPreference("scrsta", progress);
	}

	public void SetScrContrastLevel(int progress) {

		Log.d("DDD", "scrcon " + progress);
		BackCar.BC_SetContrastLevel(progress);
		saveEditToPreference("hasSetColor", true);
		saveEditToPreference("scrcon", progress);
	}

	// set and save //25 - 50- 75
	public int GetHue_S_Value(int value) {
		return value * (75 - 25) / 100 + 25;
	}

	// read and reset
	public int GetHue_R_Value(int value) {
		return value;
	}

	// set and save
	public int GetSEEKBAR_S_Value(int value, int reference) {
		Log.d("DDD", "value S " + value + "  " + reference);
		return value * reference / 50;
	}

	// read and reset
	public int GetSEEKBAR_R_Value(int value, int reference) {
		Log.d("DDD", "value R " + value + "  " + reference);
		return value * 50 / reference;
	}

	public int AdjustSeekBarWithValue_plus(int value) {
		int tmp = (value - 5) % 9;
		if (tmp > 0) {
			value -= tmp;
		} else if (tmp < 0) {
			value = -4; // for value+9 = 5;
		}
		value = value + 9 > 95 ? 95 : value + 9;
		return value;
	}

	public int AdjustSeekBarWithValue_reduce(int value) {
		int tmp = (value - 5) % 9;
		if (tmp != 0) {
			value += (9 - tmp);
		}
		value = value - 9 < 5 ? 5 : value - 9;
		return value;
	}

	// stall
	public void DealWithBackCarAllStall(Bundle data) {
		switch (mBackCar913Service.GetProtocolState()) {
		case P_STALL:
			DealWithPSTALL(data);
			break;
		case R_STALL:
			DealWithRSTALL(data);
			break;
		case N_STALL:
			DealWithNSTALL(data);
			break;
		case D_STALL:
		case S_STALL:
			DealWithDSTALL(data);
			break;
		}
	}

	public void setUpFrontRearDevice(int mode) {
		Log.d(TAG, "setUpFrontRearDevice " + mode);
		switch (mode) {
		case MsgType.FR_MODE:
			mBackCar913Service.deviceSetup(true, true);
			break;
		case MsgType.F_MODE:
			mBackCar913Service.deviceSetup(true, false);
			break;
		case MsgType.R_MODE:
			mBackCar913Service.deviceSetup(false, true);
			break;
		case MsgType.F_ORIR_MODE:
			mBackCar913Service.deviceSetup(true, false);
		}
		FlyBaseListener.setCur_fr_mode(mode);
		mBackCarModule.tellmodule913DeviceSelect(mode);

		InitFrontRearDeviceUI(mode);
	}

	public void setParkBrake(int data) {
		Log.d(TAG, "setParkBrake  " + data);
		if (data == 16) {
			ParkBrake = false;
		} else {
			ParkBrake = true;
		}
		if (isDStall())
			changeRadarMultiUiDStall();
		else if (isRStall()) {
			changeRadarMultiUiRStall();
		}
	}

	public boolean isParkBrake() {
		return ParkBrake;
	}

	public boolean isDStall() {
		if (mBackCar913Service.GetProtocolState() == BackCar913Service.ProtocolState.D_STALL
				|| mBackCar913Service.GetProtocolState() == BackCar913Service.ProtocolState.S_STALL)
			return true;
		else
			return false;
	}

	public boolean isRStall() {
		return mBackCar913Service.GetProtocolState() == BackCar913Service.ProtocolState.R_STALL;
	}

	public void isFirstTimePowerOff() {

		if (isInKeygurad()){
			DealBaseModuleMessages(MsgType.MSG_REMOVE,
					MsgType.BLACKPAGE_REMOVE, 0);
			Log.d("DDD", "it is in Consent page");
		} else
			Log.d("DDD", "it is not in Consent page");
	}

	// �Ƿ��� ͬ��ҳ �е���
	public boolean isInKeygurad() {
		KeyguardManager mKeyguardManager = (KeyguardManager) mBackCarService
				.getSystemService(Context.KEYGUARD_SERVICE);

		if (mKeyguardManager.inKeyguardRestrictedInputMode()) {
			return true;
		} else
			return false;

	}

	public FlyBackCarMainView getFlyBackCarMainView() {
		return mBackCarService.getFlyBackCarMainView();
	}

	public boolean isAuxLine_show() {
		return AuxLine_show;
	}

	public boolean isShowNavigationbar() {
		Log.d(TAG, "isShowNavigationbar " + navigationbar);
		return navigationbar == 1 ? true : false;
	}

	public void Io913_startVideo() {
		rear913Video(FlyUtil.HexToTag(BackCarTag.SET150VIDEO_CID));
		// BackCar913Service.Mode913.direct = diret913;
		BackCar913Service.Mode913.page = BackCarTag.VIDEOEPAGE;

		Bundle data = new Bundle();
		data.putInt(BackCarTag.INTKEY, BackCar913Service.CAMERA_913_REAR);
		mBackCarService.MakeAndSendMessageWithBundle(MsgType.FLYOBJ,
				BackCarTag.STALLENTER, data);

	}

	public void ChangeMultiUIObjBackground(int cid, int pos) {
		mBackCarModule.ChangeMultiUIObjBackground(cid, pos);
	}

	public GLTrackViewToolHelper getGLTrackHelper() {
		return GLTrackViewToolHelper.getInstance(mBackCarService);
	}

	public void GLTrackonResume() {
		if (mBackCarService.getBackCarMode() == mBackCarService.BACKCAR_CVB_SV&&mBackCarModule.getBackCarState())
			return;
		getGLTrackHelper().onResume();
	}

	public boolean IsCVBVdoMode() {
		if (mBackCarService.getBackCarMode() == mBackCarService.BACKCAR_CVB
				|| mBackCarService.getBackCarMode() == mBackCarService.BACKCAR_CVB_SV)
			return true;
		else
			return false;
	}

	public void FindViewWithTagAndSetVisiable(String tag, int show) {
		final View v = mBackCarService.getFlyBackCarMainView()
				.findChildViewWithTag(tag);
		if (v != null)
			v.setVisibility(show);
	}

	public void FindViewWithCidAndSetVisiable(int cid, int show) {
		FindViewWithTagAndSetVisiable(FlyUtil.HexToTag(cid), show);
	}

	public BackCarModule getBackCarModule() {
		return mBackCarModule;
	}
	
	public void RadarGetFromLpc(byte[] paramer){
		if (mBackCarService.getReversing() || mBackCarModule.getBackCarState() 
					|| mBackCarService.mgetBackCarMode() == mBackCarService.BACKCAR_SV )
			return;
		Log.d("DDD", "cmd 7 paramer[1] " + paramer[1]);
		if (paramer[1] == 0) {
			SystemProperties.set("fly.backcar.radar", "0");
			DealBaseModuleMessages(MsgType.MSG_REMOVE, MsgType.FLOATRADAR_SHOW_DELAY, 0);

			FromLpcRadaHide();
		} else if (GetPowerState()) {

			SystemProperties.set("fly.backcar.radar", "1");
DealBaseModuleMessages(MsgType.MSG_REMOVE, MsgType.FLOATRADAR_SHOW_DELAY, 0);
			DealBaseModuleMessages(MsgType.MSG_SENGDELAY, MsgType.FLOATRADAR_SHOW_DELAY,
					MsgType.FLOATRADAR_SHOW_DELAY_TIMEOUT);
		}
		
	}
	
	protected boolean isTrackOpen(){
		return RightVdoStatus ?  false : getFlyBackCarMainView().isSetShowTrack();
	}
	
  public void setRightVdoStatus(boolean flag){
	  	RightVdoStatus = flag;
	  	Log.d(TAG, "setRightVdoStatus  "+RightVdoStatus);
	  	if(RightVdoStatus){
	  		getGLTrackHelper().getGlview().setDraw(false);
	  	}else {
	  		getGLTrackHelper().getGlview().setDraw( getFlyBackCarMainView().isSetShowTrack());
	  	}
  }
	
  
  public void Msg_BackCar_StopActivity(){
		if (!BackCar913Service.BackCar913StartByUser ) {
			mBackCarModule.removeActivity();
			Log.i("BackCarSer", "MSG_BACKCAR_STOPACTIVITY");
		}
  }
}
