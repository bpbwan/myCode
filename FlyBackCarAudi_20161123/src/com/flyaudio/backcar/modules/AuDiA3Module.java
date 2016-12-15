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

public class AuDiA3Module extends AuDiA4Module {

	int tmpRadarShow = -1;

	String TAG = "DDDAuDiA3Module";
	static AuDiA3Module mAuDiA3Module;
	public boolean LowCarConfig = false;
	final String EDGE_TRACK = "edge_track_bg_level";

	public static AuDiA3Module getInstance(BackCarService Service) {
		if (mAuDiA3Module == null) {
			mAuDiA3Module = new AuDiA3Module(Service);
			setup();

		}
		return mAuDiA3Module;
	}

	public AuDiA3Module(BackCarService Service) {
		super(Service);

		WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
		mParams.type = WindowManager.LayoutParams.TYPE_KEYGUARD - 1
				| WindowManager.LayoutParams.FLAG_FULLSCREEN
				| WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
		mParams.format = PixelFormat.TRANSLUCENT;
		mParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
		mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		mParams.gravity = Gravity.LEFT | Gravity.TOP;
		mParams.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;

		mBackCarService.getFlyFloatRadarView().setWindownParams(mParams);
		LowCarConfig = false;
		// dipeiche mei you radar
		hasNoBntExit = true;
		saveEditToPreference(Floart_Radar_Scale, 0.5f);
	}

	@Override
	public void SetColor_BackCar() {
		BackCar.SetBrightness(0);
		BackCar.BC_SetHueLevel(80);
		BackCar.BC_SetSaturationLevel(249);
		BackCar.BC_SetContrastLevel(41);
	}

	@Override
	public void SetColor_ARM2() {
		mBackCarService.ReSet_Screen_Color();
	}

	public void BaseModulepreStart() {
		super.BaseModulepreStart();
		if (LowCarConfig) {
			getFlyBackCarMainView().UIParentViewShow(
					FlyUtil.HexToTag(BackCarTag.small_car), false, false);
			final View v = getFlyBackCarMainView().findChildViewWithTag(
					BackCarTag.Setting_STR);
			if (v != null)
				v.setVisibility(View.GONE);
			final View v2 = getFlyBackCarMainView().findChildViewWithTag(
					FlyUtil.HexToTag(BackCarTag.A4_AUDI_OSD_SET_CID));
			if (v2 != null)
				v2.setVisibility(View.GONE);
		}

		FindViewWithCidAndSetVisiable(
				BackCarTag.AUDI_913_REAR_ENUM_EDGETRACK_UI, View.GONE);
		FindViewWithCidAndSetVisiable(
				BackCarTag.AUDI_913_FRONT_ENUM_EDGETRACK_UI, View.GONE);
		FindViewWithCidAndSetVisiable(
				BackCarTag.AUDI_CVB_REAR_ENUM_EDGETRACK_UI, View.GONE);

		// if backcarmode is cvb_sv not show osd ui
		// final int bg_level = getPreferenceInt(EDGE_TRACK, 0);
		mBackCarModule.ChangeUIObjBackground(
				BackCarTag.AUDI_BNT_EDGETRACK_CHANGE, (byte) 0);

	}

	public void Ctr_home_event_method() {
		if (!mBackCarModule.getBackCarState()) {
			if (mBackCarService.getReversing()
					|| mBackCarService.getFlyFloatRadarView().isShowing())
				mBackCarModule.ToModule913Close_SYS((byte) 0);
			key_backcar913stop();
			removeFloatRadar(-1);
			FloatRadarViewRemove();

		}
	}

	public void OrginBackToFLyaudio() {

	}

	public void FloatRadarViewRemove() {
		mBackCarService.getFlyFloatRadarView().RemoveAllOverlayView();
		mBackCarService.getFlyFloatRadarView().LayoutRemoveViewWithPageID(
				PageID.PAGE_913A4SETTING);
		removeFloatRadar(-1);
	}

	protected void Ctr_ModuleExit_method() {
		if (IsCVBVdoMode()) {

			mBackCarService.getFlyBackCarMainView().LayoutRemoveViewWithPageID(
					PageID.PAGE_CVB_LAYOUT);

		} else
			mBackCar913Service.ShowBgView(1, false); // not show
														// guiji
		DealBackCarMessages(MsgType.MSG_REMOVE, MsgType.GEY_FLYPAGE, null, 0);
		sendBroadcastToSystemUI("com.flyaudio.backcar_0",
				"info_icon_backcar.png", "", 0);

		BaseModuleExit();
		changeRadarMultiUiPStall();
	}

	public void BackCar_StallEnter_method() {
		needReShowVideo = true;

	}

	public void BaseModuleStart() {
		mBackCarService.StopTXZ();

		if (!mBackCarModule.GetDigInCameraOn_OffStatus())
			mBackCarModule.ToModule913CameraOn_Off((byte) 0x1);

		switch (mBackCarService.mgetBackCarMode()) {
		case BackCarService.BACKCAR_914:
			FindViewWithTagAndSetVisiable(PageID.PAGE_VDO_OSD_UI_TAG,
					View.VISIBLE);
			mBackCarService.MakeAndSendMessageWithBundle(MsgType.FLYOBJ,
					BackCarTag.DigInShowMainView, null);
			break;
		case BackCarService.BACKCAR_CVB_SV:
			if (mBackCarModule.getBackCarState())
				FindViewWithTagAndSetVisiable(PageID.PAGE_VDO_OSD_UI_TAG,
						View.GONE);
			else
				FindViewWithTagAndSetVisiable(PageID.PAGE_VDO_OSD_UI_TAG,
						View.VISIBLE);
			mBackCar913Service.ShowBgView(2, false);
			break;
		default:
			FindViewWithTagAndSetVisiable(PageID.PAGE_VDO_OSD_UI_TAG,
					View.VISIBLE);
			View v3 = getFlyBackCarMainView()
					.findChildViewWithTag(
							FlyUtil.HexToTag(BackCarTag.AUDI_CVB_REAR_ENUM_EDGETRACK_UI));

			if (!getEdge_trackbg_status()) {
				v3.setVisibility(View.GONE);
				getFlyBackCarMainView().setCvbFronView();
			} else {
				mBackCar913Service.ShowBgView(2, false);
				getGLTrackHelper().getGlview().setDraw(false);
				v3.setVisibility(View.VISIBLE);
			}
		}
	}

	protected void Ctr_StartByUser_Method() {
		needReShowVideo = true;
		super.Ctr_StartByUser_Method();
	}

	public void CarSpeedLimit() {
		FloatRadarViewRemove();
	}

	@Override
	public void backcarstop() {
		// super.backcarstop();
		int ipageid = FlyaudioProperties.GetBackCarpageid();
		Log.d(TAG, "backcarstop");
		if (ipageid != PageID.PAGE_NOVIDEO_TIP
				&& mBackCarService.getBackCarMode() == mBackCarService.BACKCAR_914) {
			mBackCarModule.TurnModuleStop();
			mBackCarModule.setBackCarLock(false);
			mBackCarService.DealBackCarMessages(MsgType.MSG_REMOVE,
					MsgType.MSG_CHECK_SIGNAL, 0);
			if (!mBackCarService.getFlyFloatRadarView().isShowing())
				mBackCar913Service.set913StartByUser(true);
			else
				mBackCarModule.RemoveBackCarView();
			if (DEBUG)
				Log.d(TAG, " ipageid != PAGE_NOVIDEO_TIP 914");
			needReShowVideo = true;
			// DigIn913Mode();

			// else {
			// mBackCarModule.RemoveBackCarView();
			// showFloatRadar(PageID.PAGE_913FLOATRADAR);
			// }
		} else {
			if (DEBUG)
				Log.d(TAG, " ipageid == PAGE_NOVIDEO_TIP 914");

			mBackCarModule.RemoveBackCarView();
			mBackCarModule.setCurPageID(0x0);
			mBackCarModule.RemoveRadarView();
			mBackCarModule.removeActivity();
			mBackCarModule.TurnModuleStop();
			FloatRadarViewRemove();
			mBackCarModule.setBackCarLock(false);
			mBackCarService.DealBackCarMessages(MsgType.MSG_REMOVE,
					MsgType.MSG_CHECK_SIGNAL, 0);
		}

		tmpRadarShow = -1;
	}

	@Override
	public BaseKeyListener SetOnKeyListener(String mkeyStr, View vm,
			KeyListenerCallback cb) {

		if (mkeyStr.equals(BackCarTag.RADARPAGE913))
			vm.setOnKeyListener(AuDiA3KeyListener.getInstance(cb).pageRadar);
		else if (mkeyStr.equals(BackCarTag.PAGE_CVB_LAYOUT)) {
			vm.setOnKeyListener(AuDiA3KeyListener.getInstance(cb).CVBPage);
		} else
			vm.setOnKeyListener(AuDiA3KeyListener.getInstance(cb));
		vm.setOnFocusChangeListener(AuDiA3KeyListener.getInstance(cb));
		return (BaseKeyListener) AuDiA3KeyListener.getInstance(cb);

	};

	@Override
	public void DealWithTrackAngle(Bundle data) {

	}

	@Override
	public int GetChangeMultiRadarUi(int mControlID, int level) {
		return -1;
	}

	public void changeRadarMultiUiRStall() {

	}

	public void changeRadarMultiUiPStall() {

	}

	public void changeRadarMultiUiDStall() {

	}

	protected void Ctr_Back_Event_method() {
		if (!mBackCarModule.getBackCarState()) {
			if (mBackCarService.getReversing()
					|| mBackCarService.getFlyFloatRadarView().isShowing())
				mBackCarModule.ToModule913Close_SYS((byte) 0);
			mBackCar913Service.keyCodeBackExit();
			FloatRadarViewRemove();

		}
	}

	public void Ctr_Close_SYS_ID_method() {

		if (mBackCarService.getReversing()) // \C9\E8\D6\C3ҳ\D4ڵ\B9\B3\B5\C0\EF\C3\E6
											// \B7\F1\D4\F2\D4\DA\CD\E2\C3\E6
		{
			mBackCarModule.click913QuitBackcar();
		} else {
			removeFloatRadar(-1);

		}
	}

	@Override
	protected void Ctr_BackTo_Setting_ID_method() {
		FloatRemoveViewWithPageID(PageID.PAGE_REAR_TONE_SET);
		FloatRemoveViewWithPageID(PageID.PAGE_REAR_SOUND_SET);
		FloatRemoveViewWithPageID(PageID.PAGE_FRONT_TONE_SET);
		FloatRemoveViewWithPageID(PageID.PAGE_FRONT_SOUND_SET);
	};

	@Override
	protected void Ctr_Rotate_Rear_Tone_UI_ID_method(Bundle data) {
		final String tmp = data.getString(BackCarTag.STRKEY);
		if ("onClick".equals(tmp)) {
			FloatRemoveViewWithPageID(PageID.PAGE_REAR_TONE_SET);
			mBackCarModule.SetRotateUiView(
					BackCarTag.AUDI_ROTATE_REAR_TONE_denote, Rear_tone_level);
			FlyBaseListener.focus(FlyUtil
					.HexToTag(BackCarTag.AUDI_REAR_TONE_CID));
		} else if ("onFlyKey".equals(tmp)) {
			Ctr_BackTo_Setting_ID_method();
			Ctr_BigSetting_ID_method();
		} else {
			Rear_tone_level = data.getInt(BackCarTag.INTKEY);
			tellModuleRSoundLevel(Rear_sound_level, Rear_tone_level);
		}

	};

	@Override
	protected void Ctr_Rotate_Rear_Sound_UI_ID_method(Bundle data) {
		final String tmp = data.getString(BackCarTag.STRKEY);
		if ("onClick".equals(tmp)) {
			FloatRemoveViewWithPageID(PageID.PAGE_REAR_SOUND_SET);
			mBackCarModule.SetRotateUiView(
					BackCarTag.AUDI_ROTATE_REAR_SOUND_denote, Rear_sound_level);
			FlyBaseListener.focus(FlyUtil
					.HexToTag(BackCarTag.A4_AUDI_REAR_SOUND_CID));
		} else if ("onFlyKey".equals(tmp)) {
			Ctr_BackTo_Setting_ID_method();
			Ctr_BigSetting_ID_method();
		} else {
			Rear_sound_level = data.getInt(BackCarTag.INTKEY);
			tellModuleRSoundLevel(Rear_sound_level, Rear_tone_level);
		}
	};

	@Override
	protected void Ctr_Rotate_Front_Tone_UI_ID_method(Bundle data) {
		final String tmp = data.getString(BackCarTag.STRKEY);
		if ("onClick".equals(tmp)) {
			FloatRemoveViewWithPageID(PageID.PAGE_FRONT_TONE_SET);
			mBackCarModule.SetRotateUiView(
					BackCarTag.AUDI_ROTATE_FRONT_TONE_denote, Front_tone_level);

			FlyBaseListener.focus(FlyUtil
					.HexToTag(BackCarTag.AUDI_FRONT_TONE_CID));
		} else if ("onFlyKey".equals(tmp)) {
			Ctr_BackTo_Setting_ID_method();
			Ctr_BigSetting_ID_method();
		} else {
			Front_tone_level = data.getInt(BackCarTag.INTKEY);
			tellModuleFSoundLevel(Front_sound_level, Front_tone_level);
		}

	};

	@Override
	protected void Ctr_Rotate_Front_Sound_UI_ID_method(Bundle data) {
		final String tmp = data.getString(BackCarTag.STRKEY);
		if ("onClick".equals(tmp)) {
			FloatRemoveViewWithPageID(PageID.PAGE_FRONT_SOUND_SET);
			mBackCarModule.SetRotateUiView(
					BackCarTag.AUDI_ROTATE_FRONT_SOUND_denote,
					Front_sound_level);
			FlyBaseListener.focus(FlyUtil
					.HexToTag(BackCarTag.A4_AUDI_FRONT_SOUND_CID));
		} else if ("onFlyKey".equals(tmp)) {
			Ctr_BackTo_Setting_ID_method();
			Ctr_BigSetting_ID_method();
		} else {
			Front_sound_level = data.getInt(BackCarTag.INTKEY);
			tellModuleFSoundLevel(Front_sound_level, Front_tone_level);
		}

	};

	public void Ctr_F_VOICE_LEVEL_Method(Bundle data) {
		final int fsound[] = data.getIntArray(BackCarTag.INTKEY);
		Log.d(TAG, "Ctr_F_VOICE_LEVEL_Method  fsound[] " + fsound[1] + " "
				+ fsound[0]);
		Front_tone_level = fsound[0];
		Front_sound_level = fsound[1];
		mBackCarModule.SetRotateUiView(
				BackCarTag.AUDI_ROTATE_FRONT_SOUND_denote, Front_sound_level);
		mBackCarModule.SetRotateUiView(
				BackCarTag.AUDI_ROTATE_FRONT_TONE_denote, Front_tone_level);

		saveEditToPreference(F_S_Str, Front_sound_level);
		saveEditToPreference(F_T_Str, Front_tone_level);

	}

	public void Ctr_R_VOICE_LEVEL_Method(Bundle data) {
		final int rsound[] = data.getIntArray(BackCarTag.INTKEY);
		Log.d(TAG, "Ctr_R_VOICE_LEVEL_Method  rsound[] " + rsound[1] + " "
				+ rsound[0]);
		Rear_tone_level = rsound[0];
		Rear_sound_level = rsound[1];

		mBackCarModule.SetRotateUiView(
				BackCarTag.AUDI_ROTATE_REAR_SOUND_denote, Rear_sound_level);
		mBackCarModule.SetRotateUiView(BackCarTag.AUDI_ROTATE_REAR_TONE_denote,
				Rear_tone_level);
		saveEditToPreference(R_S_Str, Rear_sound_level);
		saveEditToPreference(R_T_Str, Rear_tone_level);
	}

	@Override
	protected void Ctr_Rear_Tone_ID_method() {

		if (DEBUG)
			Log.d(TAG, "Ctr_Rear_Tone_ID_method ");
		FloatAddViewWithPageID(
				PluginProxyContext.getInstance().getLayout(
						PageID.PAGE_REAR_TONE_SET), PageID.PAGE_REAR_TONE_SET);
		mBackCarModule.SetRotateUiView(BackCarTag.AUDI_ROTATE_REAR_TONE_UI,
				Rear_tone_level);

		FlyBaseListener.focus(FlyUtil
				.HexToTag(BackCarTag.AUDI_ROTATE_REAR_TONE_UI));

	};

	@Override
	protected void Ctr_Front_Tone_ID_method() {
		if (DEBUG)
			Log.d(TAG, "Ctr_Front_Tone_ID_method ");
		FloatAddViewWithPageID(
				PluginProxyContext.getInstance().getLayout(
						PageID.PAGE_FRONT_TONE_SET), PageID.PAGE_FRONT_TONE_SET);
		mBackCarModule.SetRotateUiView(BackCarTag.AUDI_ROTATE_FRONT_TONE_UI,
				Front_tone_level);

		FlyBaseListener.focus(FlyUtil
				.HexToTag(BackCarTag.AUDI_ROTATE_FRONT_TONE_UI));

	};

	@Override
	public void Ctr_Rear_Sound_ID_method() {
		if (DEBUG)
			Log.d(TAG, "Ctr_Rear_Sound_ID_method ");
		FloatAddViewWithPageID(
				PluginProxyContext.getInstance().getLayout(
						PageID.PAGE_REAR_SOUND_SET), PageID.PAGE_REAR_SOUND_SET);
		mBackCarModule.SetRotateUiView(BackCarTag.AUDI_ROTATE_REAR_SOUND_UI,
				Rear_sound_level);

		FlyBaseListener.focus(FlyUtil
				.HexToTag(BackCarTag.AUDI_ROTATE_REAR_SOUND_UI));
	}

	@Override
	public void Ctr_Front_Sound_ID_method() {
		if (DEBUG)
			Log.d(TAG, "Ctr_Front_Sound_ID_method ");
		FloatAddViewWithPageID(
				PluginProxyContext.getInstance().getLayout(
						PageID.PAGE_FRONT_SOUND_SET),
				PageID.PAGE_FRONT_SOUND_SET);
		mBackCarModule.SetRotateUiView(BackCarTag.AUDI_ROTATE_FRONT_SOUND_UI,
				Front_sound_level);

		FlyBaseListener.focus(FlyUtil
				.HexToTag(BackCarTag.AUDI_ROTATE_FRONT_SOUND_UI));
	}

	public void FromLpcRadarShow() {

		SystemProperties.set("fly.backcar.radar", "1");

		if (mBackCarModule.getBackCarState())
			return;
		if (mBackCarService.mgetBackCarMode() == mBackCarService.BACKCAR_914) {
			if (!mBackCarService.getReversing())
				mBackCarService.Start913ByUser();
		} else {
			if (!mBackCarService.getReversing()) {
				
				//status bar  hide
				mBackCarService.showFloatApp(false, 0);
				BaseModule.getBaseModule().showFloatRadar(
						PageID.PAGE_913FLOATRADAR);
				if (BaseModule.getBaseModule().isShowNavigationbar()) {
					BaseModule.getBaseModule().SyncScreenSize(800, 480);
				}
			}
		}
	}

	public void FromLpcRadaHide() {
		if (mBackCarModule.getBackCarState())
			return;
		removeFloatRadar(PageID.PAGE_913FLOATRADAR);
		
		//status bar show
		mBackCarService.syncFloatExitVideo();

		if (!mBackCarModule.getBackCarState())
			mBackCarService.MakeAndSendMessageWithBundle(MsgType.COMMON,
					BackCarTag.BACKKEYEVENT, null);
	}

	@Override
	public void RadarGetFromLpc(byte[] paramer) {
		if (mBackCarService.getBackCarMode() != mBackCarService.BACKCAR_SV) {
			Log.d("DDD", " cmd 7 paramer[1] " + paramer[1] + "  tmpRadarShow "
					+ tmpRadarShow);
			if (tmpRadarShow == paramer[1])
				return;
			tmpRadarShow = paramer[1];
			if (paramer[1] == 0) {
				SystemProperties.set("fly.backcar.radar", "0");

				DealBaseModuleMessages(MsgType.MSG_REMOVE,
						MsgType.FLOATRADAR_SHOW_DELAY, 0);

				DealBaseModuleMessages(MsgType.MSG_REMOVE,
						MsgType.FLOATRADAR_HIDE_DELAY, 0);
				DealBaseModuleMessages(MsgType.MSG_SENGDELAY,
						MsgType.FLOATRADAR_HIDE_DELAY,
						MsgType.FLOATRADAR_HIDE_DELAY_TIMEOUT);
			} else {
				DealBaseModuleMessages(MsgType.MSG_REMOVE,
						MsgType.FLOATRADAR_HIDE_DELAY, 0);
				DealBaseModuleMessages(MsgType.MSG_REMOVE,
						MsgType.FLOATRADAR_SHOW_DELAY, 0);
				DealBaseModuleMessages(MsgType.MSG_SENGDELAY,
						MsgType.FLOATRADAR_SHOW_DELAY,
						MsgType.FLOATRADAR_SHOW_DELAY_TIMEOUT);
			}
		}
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


		isFirstTimePowerOff();


	}
	@Override
	public void showFloatRadar(int pageid) {
		// if (!mBackCarService.getReversing())
		if (!mBackCarService.getFlyFloatRadarView().isShowing()) {

			if (!mBackCarService.getReversing()) {
				mBackCarModule.setRadarViewState(true);
				mBackCarModule.showActivity();

			}
			mBackCarService.getFlyFloatRadarView().initFloatRadarView(pageid);
			mBackCarService.getFlyFloatRadarView().ShowFloatRadarView();
			FlyBaseListener.focus(FlyUtil
					.HexToTag(BackCarTag.A4_AUDI_Float_Radar_Bg));
		}

	}

	@Override
	public void removeFloatRadar(int needSendMsgToLpc) {
		super.removeFloatRadar(needSendMsgToLpc);
		if (!mBackCarService.getReversing()) {
			mBackCarModule.setRadarViewState(false);
			mBackCarModule.removeActivity();
		}
		mBackCarService.getFlyFloatRadarView().RemoveAllOverlayView();
	}

	public void ResetSmallRotateUiDenote() {
		mBackCarModule.SetRotateUiView(
				BackCarTag.AUDI_ROTATE_FRONT_SOUND_denote, Front_sound_level);
		mBackCarModule.SetRotateUiView(
				BackCarTag.AUDI_ROTATE_FRONT_TONE_denote, Front_tone_level);
		mBackCarModule.SetRotateUiView(
				BackCarTag.AUDI_ROTATE_REAR_SOUND_denote, Rear_sound_level);
		mBackCarModule.SetRotateUiView(BackCarTag.AUDI_ROTATE_REAR_TONE_denote,
				Rear_tone_level);
	}

	public void Ctr_SmallSetting_ID_method() {
		if (LowCarConfig)
			return;

		if (DEBUG)
			Log.d(TAG, "Ctr_SmallSetting_ID_method");

		if (mBackCarService.getFlyFloatRadarView().isPageSettingShowing()) {
			CarLeftKeyDeal();
			Ctr_Setting_Back();
			return;
		}
		mBackCarModule.ToModule913RequestFRSound((byte) 0);
		if (IsCVBVdoMode()) {
			View PageSetting = PluginProxyContext.getInstance().getLayout(
					PageID.PAGE_CVBA4SETTING_LAYOUT);
			mBackCarService.getFlyFloatRadarView().LayoutAddViewWithPageID(
					PageSetting, PageID.PAGE_913A4SETTING);

			FlyBaseListener.focus(FlyUtil
					.HexToTag(BackCarTag.A4_AUDI_FRONT_SOUND_CID));
		} else {
			View PageSetting = PluginProxyContext.getInstance().getLayout(
					PageID.PAGE_913A4SETTING);
			mBackCarService.getFlyFloatRadarView().LayoutAddViewWithPageID(
					PageSetting, PageID.PAGE_913A4SETTING);
			// mBackCar913Service.setBackCarView(false);
			FlyBaseListener.focus(FlyUtil
					.HexToTag(BackCarTag.Audi_SELECTBUTTON));
		}
	}

	public void Ctr_Setting_Back() {
		if (DEBUG)
			Log.d(TAG, "Ctr_Setting_Back  ");
		super.Ctr_Setting_Back();
		mBackCar913Service.setBackCarView(true);
		if (mBackCar913Service.is913StartByUser()
				|| mBackCarService.getBackCarMode() == mBackCarService.BACKCAR_914)
			DigIn913Mode();

		mBackCarService.getFlyFloatRadarView().LayoutRemoveViewWithPageID(
				PageID.PAGE_913A4SETTING);
	}

	public void Ctr_BigSetting_ID_method() {
		if (LowCarConfig)
			return;
		// Log.d("DDD", "A4_AUDI_SET_CID");

		if (Not913ModeAndNotStartByUser()) {
			BackCarService.mBackCarService.MakeAndSendMessageWithBundle(
					MsgType.FLYOBJ, BackCarTag.A4_AUDI_NOT_913_SET_CID, null);
			return;
		}
		if (mBackCarService.getFlyFloatRadarView().isPageSettingShowing()) {
			CarLeftKeyDeal();
			Ctr_Setting_Back();
			return;
		}
		mBackCarModule.ToModule913RequestFRSound((byte) 0);

		if (IsCVBVdoMode()) {
			View PageSetting = PluginProxyContext.getInstance().getLayout(
					PageID.PAGE_CVBA4SETTING_LAYOUT);
			mBackCarService.getFlyFloatRadarView().LayoutAddViewWithPageID(
					PageSetting, PageID.PAGE_913A4SETTING);

			FlyBaseListener.focus(FlyUtil
					.HexToTag(BackCarTag.A4_AUDI_FRONT_SOUND_CID));
		} else {
			View PageSetting = PluginProxyContext.getInstance().getLayout(
					PageID.PAGE_913A4SETTING);
			mBackCarService.getFlyFloatRadarView().LayoutAddViewWithPageID(
					PageSetting, PageID.PAGE_913A4SETTING);

			FlyBaseListener.focus(FlyUtil
					.HexToTag(BackCarTag.Audi_SELECTBUTTON));
		}

	}

	public void Ctr_BackCarView_Layout(int width, int height, int gravity) {
		// if (isFloatRadarFull())
		// FlyUtil.ChangeViewLayout(mBackCarService.getBackCarView(), -1, -1,
		// Gravity.LEFT, 268);
		// else
		// FlyUtil.ChangeViewLayout(mBackCarService.getBackCarView(), -1, -1,
		// Gravity.LEFT, 0);
		//
		super.Ctr_BackCarView_Layout(width, height, gravity);
	}

	protected void Ctr_bnt_floatRadar_show_method() {

		if (mBackCarService.getFlyFloatRadarView().isPageSettingShowing())
			return;

		if (DEBUG)
			Log.d(TAG, "Ctr_bnt_floatRadar_show_method ");
		if (mBackCarService.getFlyFloatRadarView().isShowing()) {
			removeFloatRadar(PageID.PAGE_913FLOATRADAR);
			Ctr_Setting_Back();
		} else {

			showFloatRadar(PageID.PAGE_913FLOATRADAR);
			// mBackCar913Service.setBackCarView(false);
		}
	}

	protected void CVBBackCarMode() {
		super.CVBBackCarMode();

	}

	private void CVB_edge_trackbg_change() {
		View v3 = getFlyBackCarMainView().findChildViewWithTag(
				FlyUtil.HexToTag(BackCarTag.AUDI_CVB_REAR_ENUM_EDGETRACK_UI));
		FindViewWithCidAndSetVisiable(
				BackCarTag.AUDI_913_REAR_ENUM_EDGETRACK_UI, View.GONE);
		FindViewWithCidAndSetVisiable(
				BackCarTag.AUDI_913_FRONT_ENUM_EDGETRACK_UI, View.GONE);

		if (getEdge_trackbg_status()) {
			FindViewWithCidAndSetVisiable(
					BackCarTag.AUDI_CVB_REAR_ENUM_EDGETRACK_UI, View.GONE);
			mBackCarModule.ChangeUIObjBackground(
					BackCarTag.AUDI_BNT_EDGETRACK_CHANGE, (byte) 0);
			saveEditToPreference(EDGE_TRACK, 0);
			getFlyBackCarMainView().setCvbFronView();
		} else {
			mBackCar913Service.ShowBgView(2, false);
			getGLTrackHelper().getGlview().setDraw(false);
			FindViewWithCidAndSetVisiable(
					BackCarTag.AUDI_CVB_REAR_ENUM_EDGETRACK_UI, View.VISIBLE);
			mBackCarModule.ChangeUIObjBackground(
					BackCarTag.AUDI_BNT_EDGETRACK_CHANGE, (byte) 1);
			saveEditToPreference(EDGE_TRACK, 1);
		}
	}

	private void edge_trackbg_change(View v) {
		// if (v.getVisibility() == View.VISIBLE) {
		// v.setVisibility(View.GONE);
		// mBackCarModule.ChangeUIObjBackground(
		// BackCarTag.AUDI_BNT_EDGETRACK_CHANGE, (byte) 0);
		// saveEditToPreference(EDGE_TRACK, 0);
		// mBackCar913Service.setBackGround();
		// } else {
		// mBackCar913Service.ShowBgView(1, false); // not show guiji
		// mBackCarModule.ChangeUIObjBackground(
		// BackCarTag.AUDI_BNT_EDGETRACK_CHANGE, (byte) 1);
		// saveEditToPreference(EDGE_TRACK, 1);
		// v.setVisibility(View.VISIBLE);
		// }
		//
		if (getEdge_trackbg_status()) {
			v.setVisibility(View.GONE);
			mBackCarModule.ChangeUIObjBackground(
					BackCarTag.AUDI_BNT_EDGETRACK_CHANGE, (byte) 0);
			saveEditToPreference(EDGE_TRACK, 0);
			mBackCar913Service.setBackGround();
		} else {
			mBackCar913Service.ShowBgView(1, false); // not show guiji
			mBackCarModule.ChangeUIObjBackground(
					BackCarTag.AUDI_BNT_EDGETRACK_CHANGE, (byte) 1);
			saveEditToPreference(EDGE_TRACK, 1);
			v.setVisibility(View.VISIBLE);
		}
	}

	private void DigIn_edge_trackbg_change() {

		final int vdoway = get913CurrentVideoWay();
		View v = getFlyBackCarMainView().findChildViewWithTag(
				FlyUtil.HexToTag(BackCarTag.AUDI_913_REAR_ENUM_EDGETRACK_UI));
		View v2 = getFlyBackCarMainView().findChildViewWithTag(
				FlyUtil.HexToTag(BackCarTag.AUDI_913_FRONT_ENUM_EDGETRACK_UI));
		FindViewWithCidAndSetVisiable(
				BackCarTag.AUDI_CVB_REAR_ENUM_EDGETRACK_UI, View.GONE);
		if (mBackCarModule.isShowing913RearVideo()) {
			FindViewWithCidAndSetVisiable(
					BackCarTag.AUDI_913_FRONT_ENUM_EDGETRACK_UI, View.GONE);
			if (vdoway == 2) {
				edge_trackbg_change(v);
			} else {
				FindViewWithCidAndSetVisiable(
						BackCarTag.AUDI_913_REAR_ENUM_EDGETRACK_UI, View.GONE);
			}
		} else {
			FindViewWithCidAndSetVisiable(
					BackCarTag.AUDI_913_REAR_ENUM_EDGETRACK_UI, View.GONE);
			if (vdoway == 0) {
				edge_trackbg_change(v2);
			} else {
				FindViewWithCidAndSetVisiable(
						BackCarTag.AUDI_913_FRONT_ENUM_EDGETRACK_UI, View.GONE);
			}
		}
	}

	// 1 show dege_track_bg
	public boolean getEdge_trackbg_status() {
		final View v = getFlyBackCarMainView().findChildViewWithTag(
				FlyUtil.HexToTag(BackCarTag.AUDI_BNT_EDGETRACK_CHANGE));
		if (v != null) {
			// int level = FlyBaseListener.getObjBgLevel(FlyUtil
			// .HexToTag(BackCarTag.AUDI_BNT_EDGETRACK_CHANGE));

			if (v != null) {
				int level = v.getBackground().getLevel();
				if (level == 1 || level == 2)
					return true;
			}
		}

		return false;
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

		if (!getEdge_trackbg_status())
			mBackCar913Service.setBackGround();
		else {
			getGLTrackHelper().getGlview().setDraw(false);
			FindViewWithCidAndSetVisiable(
					BackCarTag.AUDI_913_REAR_ENUM_EDGETRACK_UI, View.GONE);
			FindViewWithCidAndSetVisiable(
					BackCarTag.AUDI_913_FRONT_ENUM_EDGETRACK_UI, View.VISIBLE);
			FindViewWithCidAndSetVisiable(
					BackCarTag.AUDI_CVB_REAR_ENUM_EDGETRACK_UI, View.GONE);
		}

		AUDI_BNT_EDGETRACK_CHANGE_150();
		sendMsgDelay(MsgType.TRACK_REBACK, MsgType.TRACK_REBACK_TIMEOUT);

	}

	public void AUDI_BNT_EDGETRACK_CHANGE_180() {
		if (!getEdge_trackbg_status())
			mBackCarModule.ChangeUIObjBackground(
					BackCarTag.AUDI_BNT_EDGETRACK_CHANGE, (byte) 3);
		else
			mBackCarModule.ChangeUIObjBackground(
					BackCarTag.AUDI_BNT_EDGETRACK_CHANGE, (byte) 2);
	}

	public void AUDI_BNT_EDGETRACK_CHANGE_150() {
		if (!getEdge_trackbg_status())
			mBackCarModule.ChangeUIObjBackground(
					BackCarTag.AUDI_BNT_EDGETRACK_CHANGE, (byte) 0);
		else
			mBackCarModule.ChangeUIObjBackground(
					BackCarTag.AUDI_BNT_EDGETRACK_CHANGE, (byte) 1);
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

		if (!getEdge_trackbg_status())
			mBackCar913Service.setBackGround();
		else {
			getGLTrackHelper().getGlview().setDraw(false);

			FindViewWithCidAndSetVisiable(
					BackCarTag.AUDI_913_REAR_ENUM_EDGETRACK_UI, View.VISIBLE);
			FindViewWithCidAndSetVisiable(
					BackCarTag.AUDI_913_FRONT_ENUM_EDGETRACK_UI, View.GONE);
			FindViewWithCidAndSetVisiable(
					BackCarTag.AUDI_CVB_REAR_ENUM_EDGETRACK_UI, View.GONE);

		}
		AUDI_BNT_EDGETRACK_CHANGE_150();
		sendMsgDelay(MsgType.TRACK_REBACK, MsgType.TRACK_REBACK_TIMEOUT);

	}

	public void SetDraw_Track_method() {
		if (!getEdge_trackbg_status())
			super.SetDraw_Track_method();
	}

	public void Ctr_F_180Vdo_ID_method() {
		super.Ctr_F_180Vdo_ID_method();
		FindViewWithCidAndSetVisiable(
				BackCarTag.AUDI_913_REAR_ENUM_EDGETRACK_UI, View.GONE);
		FindViewWithCidAndSetVisiable(
				BackCarTag.AUDI_913_FRONT_ENUM_EDGETRACK_UI, View.GONE);
		FindViewWithCidAndSetVisiable(
				BackCarTag.AUDI_CVB_REAR_ENUM_EDGETRACK_UI, View.GONE);
		AUDI_BNT_EDGETRACK_CHANGE_180();
	}

	public void Ctr_R_180Vdo_ID_method() {
		super.Ctr_R_180Vdo_ID_method();
		FindViewWithCidAndSetVisiable(
				BackCarTag.AUDI_913_REAR_ENUM_EDGETRACK_UI, View.GONE);
		FindViewWithCidAndSetVisiable(
				BackCarTag.AUDI_913_FRONT_ENUM_EDGETRACK_UI, View.GONE);
		FindViewWithCidAndSetVisiable(
				BackCarTag.AUDI_CVB_REAR_ENUM_EDGETRACK_UI, View.GONE);
		AUDI_BNT_EDGETRACK_CHANGE_180();
	}

	protected void Ctr_bnt_edge_trackbg_ID_method() {
		if (mBackCarService.getBackCarMode() == mBackCarService.BACKCAR_CVB
				&& !mBackCar913Service.is913StartByUser())
			CVB_edge_trackbg_change();
		else
			DigIn_edge_trackbg_change();
	}

	public void Ctr_Audi_Float_Radar_Bg_ID_method() {

	}

	public void setNoticeBackgroud() {
	}

	public void reSizeBgView() {
		super.reSizeBgView();
		// // View bg = getTargetAnimatorView();
		//
		// View bg =
		// mBackCarService.getFlyBackCarMainView().findChildViewWithTag(
		// FlyUtil.HexToTag(BackCarTag.bg913Rear_150));
		//
		// if (bg != null) {
		//
		// // FlyUtil.ChangeViewLayout(getGLTrackHelper().getGlview(),
		// // -1, -1, Gravity.LEFT, 268);
		// //
		//
		// int width = -1;
		// if (isFloatRadarFull()) {
		// width = FlyUtil.GET_SCREEN_W() - getFloatRadarSize();
		// // FlyUtil.ChangeViewLayout(getGLTrackHelper().getGlview(), -1,
		// // -1, Gravity.LEFT, 268);
		// // FlyUtil.ChangeViewLayout(getGLTrackHelper().getGlview(), -1,
		// // -1, Gravity.RIGHT, -268);
		// } else {
		// width = FlyUtil.GET_SCREEN_W();
		// // FlyUtil.ChangeViewLayout(getGLTrackHelper().getGlview(), -1,
		// // -1, Gravity.LEFT, 0);
		// // FlyUtil.ChangeViewLayout(getGLTrackHelper().getGlview(), -1,
		// // -1, Gravity.RIGHT, 0);
		// }
		// Log.d("DDD", "reSizeBgView " + width);
		// FlyUtil.ChangeViewSize(bg, width, -1);
		// FlyUtil.ChangeViewLayout(bg, -1, -1, Gravity.RIGHT, 0);
		//
		// bg = mBackCarService.getFlyBackCarMainView().findChildViewWithTag(
		// FlyUtil.HexToTag(BackCarTag.bg913Front_150));
		// FlyUtil.ChangeViewSize(bg, width, -1);
		// FlyUtil.ChangeViewLayout(bg, -1, -1, Gravity.RIGHT, 0);
		//
		// bg = mBackCarService.getFlyBackCarMainView().findChildViewWithTag(
		// FlyUtil.HexToTag(BackCarTag.bgcvb));
		// FlyUtil.ChangeViewSize(bg, width, -1);
		// FlyUtil.ChangeViewLayout(bg, -1, -1, Gravity.RIGHT, 0);
		//
		// }
	}

	// public void setSufaceViewFullScreen() {
	//
	// // String size =
	// // mBackCarService.getFlyBackCarMainView().getScreenW()+"";
	// // SystemProperties.set("persist.backcar.screensize", size);
	// DealBackCarMessages(MsgType.MSG_REMOVE, MsgType.SETSURFACE, null, 0);
	// final int width = mBackCarService.getFlyBackCarMainView().getScreenW();
	// final int height = mBackCarService.getFlyBackCarMainView().getScreenH();
	// Log.d("DDD", "setSufaceViewFullScreen " + width);
	// FloatRadarScale = 0.5f;
	// saveFloatScaleAndUpdate(FloatRadarScale);
	// needToChangeParamToJni(width, height);
	// reSizeBgView();
	//
	// // mBackCarService.getBackCarView().reSizeSurfaceView(width, -1); 1117
	//
	// DealBackCarMessages(MsgType.MSG_REMOVE, MsgType.SETSURFACE, null, 0);
	// Message msg = Message.obtain();
	// msg.what = MsgType.SETSURFACE;
	// msg.arg1 = BackCarTag.SetHalfSurfaceView;
	//
	// Bundle mbd = new Bundle();
	// mbd.putInt(BackCarTag.INTKEY, width);
	// msg.setData(mbd);
	// DealBackCarMessages(MsgType.MSG_SENGDELAY, MsgType.SETSURFACE, msg,
	// MsgType.MSG_SETFULLSURFACE_TIMEOUT);
	// }
	//
	// public void setSufaceViewHalfScreen() {
	//
	// final int width = mBackCarService.getFlyBackCarMainView().getScreenW()
	// - getFloatRadarSize();
	// final int height = mBackCarService.getFlyBackCarMainView().getScreenH();
	// FloatRadarScale = 1.0f;
	// Log.d("DDD", "setSufaceViewHalfScreen " + width);
	// needToChangeParamToJni(width, height);
	// reSizeBgView();
	// saveFloatScaleAndUpdate(FloatRadarScale);
	//
	// // mBackCarService.getBackCarView()
	// // .reSizeSurfaceView(width, -1);
	//
	// DealBackCarMessages(MsgType.MSG_REMOVE, MsgType.SETSURFACE, null, 0);
	// Message msg = Message.obtain();
	// msg.what = MsgType.SETSURFACE;
	// msg.arg1 = BackCarTag.SetHalfSurfaceView;
	//
	// Bundle mbd = new Bundle();
	// mbd.putInt(BackCarTag.INTKEY, width);
	// msg.setData(mbd);
	// DealBackCarMessages(MsgType.MSG_SENGDELAY, MsgType.SETSURFACE, msg,
	// MsgType.MSG_SETHALFSURFACE_TIMEOUT);
	//
	// }
	/*
	 * 
	 * 
	 * public void setSufaceViewFullScreen() {
	 * 
	 * // String size = //
	 * mBackCarService.getFlyBackCarMainView().getScreenW()+""; //
	 * SystemProperties.set("persist.backcar.screensize", size);
	 * DealBackCarMessages(MsgType.MSG_REMOVE, MsgType.SETSURFACE, null, 0);
	 * final int width = mBackCarService.getFlyBackCarMainView().getScreenW();
	 * final int height = mBackCarService.getFlyBackCarMainView().getScreenH();
	 * Log.d("DDD", "setSufaceViewFullScreen " + width); FloatRadarScale = 0.5f;
	 * saveFloatScaleAndUpdate(FloatRadarScale); needToChangeParamToJni(width,
	 * height); reSizeBgView();
	 * 
	 * mBackCarService.getBackCarView().reSizeSurfaceView(width, -1);
	 * 
	 * }
	 * 
	 * public void setSufaceViewHalfScreen() {
	 * 
	 * final int width = mBackCarService.getFlyBackCarMainView().getScreenW() -
	 * getFloatRadarSize(); final int height =
	 * mBackCarService.getFlyBackCarMainView().getScreenH(); FloatRadarScale =
	 * 1.0f; Log.d("DDD", "setSufaceViewHalfScreen " + width);
	 * needToChangeParamToJni(width, height); reSizeBgView();
	 * saveFloatScaleAndUpdate(FloatRadarScale);
	 * 
	 * // mBackCarService.getBackCarView() // .reSizeSurfaceView(width, -1);
	 * 
	 * DealBackCarMessages(MsgType.MSG_REMOVE, MsgType.SETSURFACE, null, 0);
	 * Message msg = Message.obtain(); msg.what = MsgType.SETSURFACE; msg.arg1 =
	 * BackCarTag.SetHalfSurfaceView;
	 * 
	 * Bundle mbd = new Bundle(); mbd.putInt(BackCarTag.INTKEY, width);
	 * msg.setData(mbd); DealBackCarMessages(MsgType.MSG_SENGDELAY,
	 * MsgType.SETSURFACE, msg, MsgType.MSG_SETHALFSURFACE_TIMEOUT);
	 * 
	 * }
	 */
	

	
}
