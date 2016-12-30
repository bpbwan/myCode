package com.flyaudio.backcar.modules;

import java.util.Locale;

import com.flyaudio.backcar913.BackCarTag;
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

public class G6zModule extends BaseModule {

	String TAG = "G6zModule";
	static G6zModule mG6zModule;
	private static int Backcar_Bri = 54;
	private static int Backcar_Con = 36;
	private static int Backcar_Hue = 65;
	private static int Backcar_Sta = 51;

	private static int Backcar_vdoBri = 30;// 23;
	private static int Backcar_scrCon = 20;// 17;
	private static int Backcar_scrHue = 55;// 54;
	private static int Backcar_scrSta = 70;// 68;

	protected int touch_times = 0;
	boolean setcolor = false;

	public static G6zModule getInstance(BackCarService Service) {
		if (mG6zModule == null) {
			mG6zModule = new G6zModule(Service);
		}
		return mG6zModule;
	}

	public G6zModule(BackCarService Service) {
		super(Service);

	}

	public void OrginBackToFLyaudio() {

	}

	public void sendMessageToModule(Message msg) {
		myMsgHandler.sendMessage(msg);

	}

	public void BaseModulepreStart() {
		getFlyBackCarMainView().DetechBackCarMode(mBackCarService.getBackCarMode());
		
	}

	public void DealActivityStop() {

	}

	public void BaseModulepreStop() {
		if (mBackCarTrack.isTrackInit())
			mBackCarTrack.sendParam((byte) 0x18, 2, 0);
	}

	@Override
	public void showNoticeView(boolean disableAssist, boolean show) {
		// if (/*!disableAssist || */AuxLine_show)
		Log.d(TAG, "showNoticeView " + show);
		showAuxButton(show);

	}

	@Override
	public void FromModuleMsgCtrAuxLine(byte data) {

		m_ShowAuxButton(data);
	}

	@Override
	public void FromModuleMsgCtrPhoneCall(byte data) {
		if (data == 1)
			SETTINGCOLORHIDE();
	}

	@Override
	public void SetColor_BackCar() {
		// BackCar.SetBrightness(Backcar_vdoBri); //FLYSEEKBAR1
		// BackCar.BC_SetContrastLevel(Backcar_scrCon); //25 FLYSEEKBAR5
		// BackCar.BC_SetHueLevel(Backcar_scrHue); //FLYSEEKBAR7
		// BackCar.BC_SetSaturationLevel(Backcar_scrSta); // FLYSEEKBAR6
		if (mBackCarService.BackCarMode == mBackCarService.BACKCAR_USB)
			return;

		VDO_Bri = getPreferenceInt("vdobri", Backcar_vdoBri);
		TVD_Con = getPreferenceInt("scrcon", Backcar_scrCon);
		TVD_Hue = getPreferenceInt("scrhue", Backcar_scrHue);
		TVD_Sta = getPreferenceInt("scrsta", Backcar_scrSta);

		// VDO_Bri = getPreferenceInt("vdoBriseekbar",50);
		// TVD_Con = getPreferenceInt("scrConseekbar",50);
		// TVD_Hue = getPreferenceInt("scrHueseekbar",50);
		// TVD_Sta = getPreferenceInt("scrStaseekbar",50);

		// mBackCarModule.setFlySeekBarProcess(BackCarTag.FLYSEEKBAR1, VDO_Bri);
		// mBackCarModule.setFlySeekBarProcess(BackCarTag.FLYSEEKBAR5, TVD_Con);
		// mBackCarModule.setFlySeekBarProcess(BackCarTag.FLYSEEKBAR7, TVD_Hue);
		// mBackCarModule.setFlySeekBarProcess(BackCarTag.FLYSEEKBAR6, TVD_Sta);

		/*
		 * BackCar.BC_SetBrightnessLevel(Backcar_Bri);
		 * BackCar.BC_SetContrastLevel(Backcar_Con);
		 * BackCar.BC_SetHueLevel(Backcar_Hue);
		 * BackCar.BC_SetSaturationLevel(Backcar_Sta);
		 */
	}

	public void SetColor_ARM2() {
		BackCar.BC_SetBrightnessLevel(50);
		mBackCarService.ReSet_Screen_Color();

	}

	public void SIGNAL_READY() {
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

	public int VDO_Bri = 5;
	public int TVD_Bri = 5;
	public int TVD_Con = 5;
	public int TVD_Hue = 5;
	public int TVD_Sta = 5;

	Handler myMsgHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			Bundle data = msg.getData();
			if ((int) msg.what == MsgType.FLYOBJ) {

				switch ((int) msg.arg1) { // flyButtonObj
				case BackCarTag.AUXLINE_UP:
					setAuxLineHeight(-5);
					break;
				case BackCarTag.AUXLINE_DOWN:
					setAuxLineHeight(5);
					break;
				case BackCarTag.SETTINGBACK:
					mBackCarService.getFlyBackCarMainView().UIParentViewShow(
							BackCarTag.SETTINGBACK_TAG, false, false);
					break;
				case BackCarTag.SETBRIGHT_LEFT:

					VDO_Bri = AdjustSeekBarWithValue_reduce(VDO_Bri);

					mBackCarModule.setFlySeekBarProcess(BackCarTag.FLYSEEKBAR1,
							VDO_Bri);
					break;
				case BackCarTag.SETBRIGHT_RIGHT:

					VDO_Bri = AdjustSeekBarWithValue_plus(VDO_Bri);

					mBackCarModule.setFlySeekBarProcess(BackCarTag.FLYSEEKBAR1,
							VDO_Bri);
					break;
				case BackCarTag.SETCONTRAST_LEFT:
					TVD_Con = AdjustSeekBarWithValue_reduce(TVD_Con);

					mBackCarModule.setFlySeekBarProcess(BackCarTag.FLYSEEKBAR5,
							TVD_Con);
					break;
				case BackCarTag.SETCONTRAST_RIGHT:
					TVD_Con = AdjustSeekBarWithValue_plus(TVD_Con);

					mBackCarModule.setFlySeekBarProcess(BackCarTag.FLYSEEKBAR5,
							TVD_Con);
					break;
				case BackCarTag.SETHUE_LEFT:
					int value_l = GetHue_R_Value(TVD_Hue);
					value_l = AdjustSeekBarWithValue_reduce(value_l);

					mBackCarModule.setFlySeekBarProcess(BackCarTag.FLYSEEKBAR7,
							value_l);
					break;
				case BackCarTag.SETHUE_RIGHT:
					int value = GetHue_R_Value(TVD_Hue);
					value = AdjustSeekBarWithValue_plus(value);

					mBackCarModule.setFlySeekBarProcess(BackCarTag.FLYSEEKBAR7,
							value);
					break;
				case BackCarTag.SETSTATURA_LEFT:
					TVD_Sta = AdjustSeekBarWithValue_reduce(TVD_Sta);

					mBackCarModule.setFlySeekBarProcess(BackCarTag.FLYSEEKBAR6,
							TVD_Sta);
					break;
				case BackCarTag.SETSTATURA_RIGHT:
					TVD_Sta = AdjustSeekBarWithValue_plus(TVD_Sta);

					mBackCarModule.setFlySeekBarProcess(BackCarTag.FLYSEEKBAR6,
							TVD_Sta);
					break;

				case BackCarTag.TOUCH_BG:
					final String touch_state = data
							.getString(BackCarTag.STRKEY);
					if (touch_state.equals(BackCarTag.TOUCH_DOWN)) {

						showSetAuxBotton(true);
						// SETTINGCOLORHIDE();
						// DealBaseModuleMessages(MsgType.MSG_REMOVE,MsgType.SHOW_SETTING_COLOR,
						// 0);
						// DealBaseModuleMessages(MsgType.MSG_SENGDELAY,MsgType.SHOW_SETTING_COLOR,
						// MsgType.SHOW_SETTING_TIMEOUT);
						// if (DEBUG_L2)
						// touch_to_exit_iothread();
					} else if (touch_state.equals(BackCarTag.TOUCH_UP)) {
						// DealBaseModuleMessages(MsgType.MSG_REMOVE,MsgType.SHOW_SETTING_COLOR,
						// 0);

					}
				
			if(DEBUG_L2)
				mBackCarService.testRunning();
					break;

				case BackCarTag.AUXLINE:

					try {
						final String touch_state2 = data
								.getString(BackCarTag.STRKEY);
						if (touch_state2.equals(BackCarTag.TOUCH_MOVE)) {
							int evn_xy[] = data.getIntArray(BackCarTag.INTKEY);
							mBackCarService.getFlyBackCarMainView()
									.setAuxLineHeight(evn_xy[1]);
						}
					} catch (Exception e) {
					}
					break;

				default:

					break;
				}

			} else if ((int) msg.what == MsgType.COMMON) {

				switch ((int) msg.arg1) {
				case BackCarTag.MODULEENTER:
					moduleEnter();
					break;
				case BackCarTag.MODULEEXIT:
					mBackCarService.getFlyBackCarMainView().UIParentViewShow(
							BackCarTag.SETTINGBACK_TAG, false, false);
					BLACKPAGEHIDE();
					break;

				}

			} else if ((int) msg.what == MsgType.REQUEST_SYNCANGLE) {
				getGLTrackHelper().getGlview().requestSyncAngle();
				if (mBackCarModule.getBackCarState()) {
					Message t_msg = Message.obtain();
					t_msg.what = MsgType.REQUEST_SYNCANGLE;
					DealBackCarMessages(myMsgHandler, MsgType.MSG_SENGDELAY, 0,
							t_msg, MsgType.REQUEST_SYNCANGLE_TIMEOUT);
				}
			}
		}
	};

	void touch_to_exit_iothread() {
		touch_times++;
		if (touch_times > 4)
			mBackCarService.exit_io_thread();

	}

	@Override
	public void SetVdoBrightness(int progress) {
		// Log.d("DDD", " bri before "+progress);
		VDO_Bri = progress;
		final int tmpbri = GetSEEKBAR_S_Value(progress, Backcar_vdoBri);
		saveEditToPreference("vdoBriseekbar", progress);
		super.SetVdoBrightness(tmpbri);

	}

	@Override
	public void SetScrHueLevel(int progress) {
		TVD_Hue = progress;
		saveEditToPreference("scrHueseekbar", progress);
		final int tmphue = GetHue_S_Value(progress);
		if (mBackCarModule.getsignal())
			super.SetScrHueLevel(tmphue);

	}

	@Override
	public void SetScrSaturationLevel(int progress) {
		// Log.d("DDD", " sta before "+progress);
		TVD_Sta = progress;
		final int tmpsta = GetSEEKBAR_S_Value(progress, Backcar_scrSta);
		saveEditToPreference("scrStaseekbar", progress);
		if (mBackCarModule.getsignal())
			super.SetScrSaturationLevel(tmpsta);

	}

	@Override
	public void SetScrContrastLevel(int progress) {
		// Log.d("DDD", " con before "+progress);
		TVD_Con = progress;
		final int tmpcon = GetSEEKBAR_S_Value(progress, Backcar_scrCon);
		saveEditToPreference("scrConseekbar", progress);
		if (mBackCarModule.getsignal())
			super.SetScrContrastLevel(tmpcon);

	}

	// ɫ������ҳ �����嵹������ʾ����ɫ��
	@Override
	public void SETTINGCOLORSHOW() {
		if (mBackCarService.BackCarMode == mBackCarService.BACKCAR_USB)
			return;

		boolean isShow = mBackCarService.getFlyBackCarMainView()
				.UIParentViewShow(BackCarTag.SETTINGBACK_TAG, false, true);
		InitSettingColorView(isShow);
		super.SETTINGCOLORSHOW();

	}

	public void InitSettingColorView(boolean flag) {
		if (flag) {
			SetColor_BackCar();
		}

	}

	public void hasSetColor(boolean flag) {
		this.setcolor = flag;
	}

	public boolean getSetColor() {
		return this.setcolor;
	}

	public void m_ShowAuxButton(byte data) {
		if (data == 0)
			AuxLine_show = false;
		else if (data == 1)
			AuxLine_show = true;

		if (AuxLine_show) {
			boolean needShow = mBackCarService.getFlyBackCarMainView()
					.isUIViewDisable(BackCarTag.Notice_STR);
			showAuxButton(needShow);
		}
	}

	private void showAuxButton(boolean show) {
		if (!AuxLine_show)
			show = false;

		mBackCarService.getFlyBackCarMainView().UIParentViewShow(
				FlyUtil.HexToTag(BackCarTag.AUXLINE_UP), show, false);
		mBackCarService.getFlyBackCarMainView().UIParentViewShow(
				FlyUtil.HexToTag(BackCarTag.AUXLINE_DOWN), show, false);

	}

	void setAuxLineHeight(int ev_Y) {
		mBackCarService.DealBackCarMessages(MsgType.MSG_REMOVE,
				MsgType.MSG_STOPNOTICE, 0);
		mBackCarService.getFlyBackCarMainView().setAuxLineHeight(ev_Y);
		mBackCarService.DealBackCarMessages(MsgType.MSG_SENGDELAY,
				MsgType.MSG_STOPNOTICE, MsgType.STOPNOTICE_TIMEOUT);
	}

	public void showSetAuxBotton(boolean flag) {
		if (!AuxLine_show) // û�и�������ʾ�Ͳ���ʾ����ť
			return;
		boolean needShow = mBackCarService.getFlyBackCarMainView()
				.isUIViewDisable(BackCarTag.Notice_STR);
		mBackCarService.DealBackCarMessages(MsgType.MSG_REMOVE,
				MsgType.MSG_STOPNOTICE, 0);

		mBackCarModule.showNoticeView(!needShow);
		// mBackCarModule.showNoticeView(flag);
		mBackCarService.DealBackCarMessages(MsgType.MSG_SENGDELAY,
				MsgType.MSG_STOPNOTICE, MsgType.STOPNOTICE_TIMEOUT);

	}

	@Override
	public void backcarstop() {
		mBackCarModule.RemoveBackCarView();

		int ipageid = FlyaudioProperties.GetBackCarpageid();
		mBackCarModule.setCurPageID(ipageid);

		if ((mBackCarModule.getCurPageID() == PageID.PAGE_NOVIDEO_TIP || mBackCarModule
				.getCurPageID() == 0x0)) {
			Log.d(TAG,
					"backcarstop"
							+ Integer
									.toHexString(mBackCarModule.getCurPageID())
									.toUpperCase());
			mBackCarModule.RemoveRadarView();
			mBackCarModule.removeActivity();
			mBackCarModule.TurnModuleStop();
		} else {

			if (mBackCarModule.getRadarViewState() == false)
				mBackCarModule.initRadarView(mBackCarModule.getCurPageID());
			mBackCarModule.ShowRadarView();
			// SystemProperties.set("fly.backcar.radar", ""); //for platform
			// test
			// mBackCarService.DealBackCarMessages(MsgType.MSG_SENGDELAY,MsgType.MSG_BACKCAR_STOP,
			// 3000);
		}
		mBackCarModule.setBackCarLock(false);
		mBackCarService.DealBackCarMessages(MsgType.MSG_REMOVE,
				MsgType.MSG_CHECK_SIGNAL, 0);
		mBackCarService.DealBackCarMessages(MsgType.MSG_REMOVE,
				MsgType.MSG_STOPNOTICE, 0);

	}

	public void BLACKPAGEHIDE() {
		Log.d("DDDD", "BLACKPAGEHIDE");
		mBackCarService.getBackCarModule().ShowUiView(BackCarTag.BLACKPAGE,
				false);
	}

	@Override
	  public void SetTrackDraw(boolean flag){
		  super.SetTrackDraw(flag);
			getGLTrackHelper().getGlview().setDraw(flag);
			
			if(flag){
				if(mBackCarService.getBackCarMode() == mBackCarService.BACKCAR_CVB){
					mBackCarModule.ShowUiView(BackCarTag.bgusb_camera, false);
					mBackCarModule.ShowUiView(BackCarTag.bgcvb, true);
				}else if(mBackCarService.getBackCarMode() == mBackCarService.BACKCAR_USB){
					mBackCarModule.ShowUiView(BackCarTag.bgcvb, false);
					mBackCarModule.ShowUiView(BackCarTag.bgusb_camera, true);
				}
			}else {
				mBackCarModule.ShowUiView(BackCarTag.bgusb_camera, false);
				mBackCarModule.ShowUiView(BackCarTag.bgcvb, false);
			}
	  }
	
	public void moduleEnter() {

		if (IsCVBVdoMode())
			mBackCarService.getFlyBackCarMainView().mSetTrailMode(3);
		else if (mBackCarService.getBackCarMode() == mBackCarService.BACKCAR_USB)
			mBackCarService.getFlyBackCarMainView().mSetTrailMode(4);

		// in keyguard but not in android updateing
		if (isInKeygurad() && !mBackCarService.ISUPDATE()) {
			DealBaseModuleMessages(MsgType.MSG_REMOVE,
					MsgType.BLACKPAGE_REMOVE, 0);
			mBackCarService.getBackCarModule().ShowUiView(BackCarTag.BLACKPAGE,
					true);
			Log.d("DDDB", "isInKeygurad  ");
		}
		GLTrackonResume();

		SetTrackDraw(isTrackOpen());
		
		getGLTrackHelper().getGlview().requestSyncAngle();
		getGLTrackHelper().getGlview().requestSyncAngle();
		getGLTrackHelper().getGlview().requestSyncAngle();

		
		Message msg = Message.obtain();
		msg.what = MsgType.REQUEST_SYNCANGLE;
		DealBackCarMessages(myMsgHandler, MsgType.MSG_SENGDELAY, 0, msg,
				MsgType.REQUEST_SYNCANGLE_TIMEOUT);

		touch_times = 0;
	}
}
