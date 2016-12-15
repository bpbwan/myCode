package com.flyaudio.backcar.modules;

import java.util.Locale;

import android.view.View;
import android.content.Context;
import android.content.Intent;
import android.os.Messenger;
import android.util.Log;

import com.flyaudio.backcar913.*;
import com.flyaudio.backcar.*;
import com.flyaudio.backcar913.BackCar913Service;
import com.flyaudio.globaldefine.FlyaudioIntent;
import com.flyaudio.globaldefine.FlyaudioProperties;
import com.flyaudio.globaldefine.PageID;
import com.flyaudio.globaldefine.UIAction;
import com.flyaudio.msg.HandleServiceMsg;
import com.flyaudio.tool.GLTrackViewToolHelper;

import android.os.Message;
import android.os.Handler;
import android.os.Messenger;
import android.os.Bundle;

import com.flyaudio.backcar.util.*;
import com.flyaudio.keyevent.*;
import com.flyaudio.keyevent.BaseKeyListener.KeyListenerCallback;

import android.os.SystemProperties;

public class BenzModule extends BaseModule {

	String TAG = "BenzModule";
	static BenzModule mBenzModule;
	public int diret913 = 0;

	public static BenzModule getInstance(BackCarService Service) {
		if (mBenzModule == null) {
			mBenzModule = new BenzModule(Service);
		}
		return mBenzModule;
	}

	public BenzModule(BackCarService Service) {
		super(Service);

	}

	@Override
	public void initFr_mode(int value) {
		switch (value) {
		case MsgType.FR_MODE:
			mBackCarService
					.MakeAndSendMessageButton(BackCarTag.CAMERA913_FR_MODE);
			break;
		case MsgType.F_MODE:
			mBackCarService
					.MakeAndSendMessageButton(BackCarTag.CAMERA913_F_MODE);
			break;
		case MsgType.R_MODE:
			mBackCarService
					.MakeAndSendMessageButton(BackCarTag.CAMERA913_R_MODE);
			break;
		case MsgType.F_ORIR_MODE:
			mBackCarService
					.MakeAndSendMessageButton(BackCarTag.CAMERA913_F_R_MODE);

		}
	}

	public void OrginBackToFLyaudio() {

	}

	public void sendMessageToModule(Message msg) {
		myMsgHandler.sendMessage(msg);

	}

	public void BaseModulepreStart() {

	}

	public void DealActivityStop() {

	}
	
	@Override
	public BaseKeyListener SetOnKeyListener(String mkeyStr, View vm, KeyListenerCallback cb){
							if (mkeyStr.equals(BackCarTag.SETLIGHTPAGE))
						vm.setOnKeyListener(BenzKeyListener.getInstance(cb).benz_KeyListener2);
					else
						vm.setOnKeyListener(BenzKeyListener.getInstance(cb).benz_KeyListener);
					vm.setOnFocusChangeListener(BenzKeyListener
							.getInstance(cb));

					return (BaseKeyListener) BenzKeyListener.getInstance(cb);

	};

	
	@Override
	public void InitFrontRearDeviceUI(int fr_mode){
		mBackCar913Service.change_FR_ModeUI(FlyBaseListener.mfindViewWithTag(FlyUtil
							.HexToTag(BackCarTag.CHOICE_BNT)),
							FlyBaseListener.mfindViewWithTag(FlyUtil
									.HexToTag(BackCarTag.CHOICE_BNT2)), fr_mode);

		}

	public void BaseModulepreStop() {
		BackCarService.mBackCarService.getFlyBackCarMainView()
				.LayoutRemoveViewWithPageID(PageID.PAGE_913SETLIGHT);
		if (mBackCarTrack.isTrackInit())
			mBackCarTrack.sendParam((byte) 0x18, 2, 0);
	}

	@Override
	public void BLACKPAGESHOW() {
		mBackCarModule.ShowUiView(BackCarTag.A4_SMALL_CAR_BG, true);
		super.BLACKPAGESHOW();

	}

	@Override
	public void BLACKPAGEHIDE() {

		super.BLACKPAGEHIDE();
		mBackCarModule.ShowUiView(BackCarTag.A4_SMALL_CAR_BG, false);
	}

	Handler myMsgHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			int lightLevel = -1;
			int oldLightLevel = 0;

			Bundle data = msg.getData();
			if ((int) msg.what == MsgType.FLYOBJ) {

				switch ((int) msg.arg1) {

				case BackCarTag.STALLENTER:
					mBackCarService
							.MakeAndSendMessageButton(BackCarTag.SET150VIDEO_CID);
					break;
				case BackCarTag.SET150VIDEO_CID:
					mBackCarModule.showFlyButtonBg(BackCarTag.SET150VIDEO_CID,
							(byte) 1);
					mBackCarModule.showFlyButtonBg(BackCarTag.SET180VIDEO_CID,
							(byte) 0);

					BackCar913Service.Mode913.focusTag = FlyUtil
							.HexToTag(BackCarTag.SET150VIDEO_CID);
					mBackCarModule.set913SourceRect(2);

					FlyBaseListener.focus(FlyUtil
							.HexToTag(BackCarTag.SET150VIDEO_CID));
					mBackCar913Service.setBackGround();
					break;
				case BackCarTag.SET180VIDEO_CID:

					mBackCarModule.showFlyButtonBg(BackCarTag.SET150VIDEO_CID,
							(byte) 0);
					mBackCarModule.showFlyButtonBg(BackCarTag.SET180VIDEO_CID,
							(byte) 1);

					BackCar913Service.Mode913.focusTag = FlyUtil
							.HexToTag(BackCarTag.SET180VIDEO_CID);
					mBackCarModule.set913SourceRect(1);

					FlyBaseListener.focus(FlyUtil
							.HexToTag(BackCarTag.SET180VIDEO_CID));
					mBackCar913Service.ShowBgView(1, false); // not show guiji
					break;
				case BackCarTag.SETLIGHT_CID:
					String s = SystemProperties.get("fly.hdmisv.lightlevel",
							"5");
					int value = Integer.parseInt(s);
					value = value < 0 ? 0 : value;
					value = value > 10 ? 10 : value;
					String sendTag = value >= 10 ? "070f00" + value : "070f000"
							+ value;

					Bundle msgTag = new Bundle();
					msgTag.putString(BackCarTag.STRKEY, sendTag);
					mBackCarService.MakeAndSendMessageWithBundle(
							MsgType.COMMON, BackCarTag.ADDLIGHTVIEW, msgTag);
					oldLightLevel = FlyBaseListener.getoldLightLevel();
					if (oldLightLevel != 0)
						mBackCarModule.showFlyButtonBg(oldLightLevel, (byte) 0);

					oldLightLevel = Integer.parseInt(sendTag, 16);
					FlyBaseListener.setObjLevel(sendTag, 1);
					FlyBaseListener.saveLightLevel(oldLightLevel);
					mBackCarModule.showLightLevel(value);
					break;
				case 0x070f0010:
					lightLevel++;
				case 0x070f0009:
					lightLevel++;
				case 0x070f0008:
					lightLevel++;
				case 0x070f0007:
					lightLevel++;
				case 0x070f0006:
					lightLevel++;
				case 0x070f0005:
					lightLevel++;
				case 0x070f0004:
					lightLevel++;
				case 0x070f0003:
					lightLevel++;
				case 0x070f0002:
					lightLevel++;
				case 0x070f0001:
					lightLevel++;
				case 0x070f0000:
					lightLevel++;
					String s2 = Integer.toString(lightLevel);
					SystemProperties.set("fly.hdmisv.lightlevel", s2);
					mBackCarModule.showFlyButtonBg(
							FlyBaseListener.getoldLightLevel(), (byte) 0);
					FlyBaseListener.saveLightLevel((int) msg.arg1);
					mBackCarModule.showFlyButtonBg((int) msg.arg1, (byte) 1);
					mBackCarModule.showLightLevel(lightLevel);
					mBackCarModule.setLightLevel(lightLevel);
					mBackCarService.MakeAndSendMessageWithBundle(
							MsgType.COMMON, BackCarTag.RMLIGHTVIEW, null);
					FlyBaseListener.focus(FlyUtil
							.HexToTag(BackCarTag.SETLIGHT_CID));
					break;
				case BackCarTag.CHOICE_BNT: // ѡ��ǰ�� 1

					mBackCarModule.showFlyButtonBg(BackCarTag.CHOICE_BNT2,
							(byte) 0);
					mBackCarModule.showFlyButtonBg(BackCarTag.CHOICE_BNT,
							(byte) 1);
					mBackCarModule.showSignByUser(true);
					mBackCarModule.front913Video(FlyUtil
							.HexToTag(BackCarTag.SET150VIDEO_CID));
					mBackCarModule.reBackLight();
					BackCar913Service.Mode913.direct = 1;
					BackCar913Service.Mode913.chioceDire = 1;
					BackCar913Service.Mode913.page = BackCarTag.VIDEOEPAGE;
					mBackCarService
							.MakeAndSendMessageButton(BackCarTag.SET150VIDEO_CID);
					break;
				case BackCarTag.CHOICE_BNT2: // 0
					mBackCarModule.showFlyButtonBg(BackCarTag.CHOICE_BNT2,
							(byte) 1);
					mBackCarModule.showFlyButtonBg(BackCarTag.CHOICE_BNT,
							(byte) 0);
					mBackCarModule.showSignByUser(true);
					mBackCarModule.rear913Video(FlyUtil
							.HexToTag(BackCarTag.SET150VIDEO_CID));
					mBackCarModule.reBackLight();
					BackCar913Service.Mode913.direct = 0;
					BackCar913Service.Mode913.chioceDire = 0;
					BackCar913Service.Mode913.page = BackCarTag.VIDEOEPAGE;
					mBackCarService
							.MakeAndSendMessageButton(BackCarTag.SET150VIDEO_CID);
					break;

				case BackCarTag.CHOICEBACK_BNT: // ����
					mBackCarModule.click913QuitBackcar();
					break;

				case BackCarTag.VIDEOTOCHOICE:
					chioce913Page(FlyUtil.HexToTag(BackCarTag.CHOICEBACK_BNT),
							BackCar913Service.Mode913.chioceDire);
					mBackCarService.showFloatApp(true, -1);// ��ʾ���
					break;

				case BackCarTag.CAMERA913_FR_MODE: // ��������840 802 ǰ����ѡ���װ��ʽ 0
													// ǰ���� 1 ����ǰ 2 ���к� 3
													// �������ǵ�ǰ��ԭ���ĺ�
					mBackCarService.getFlyRadarView().updateTextUiColor(
							BackCarTag.F_MODE_TEXT, BackCarTag.FR_TEXT_COLOR_U);
					mBackCarService.getFlyRadarView().updateTextUiColor(
							BackCarTag.R_MODE_TEXT, BackCarTag.FR_TEXT_COLOR_U);
					mBackCarService.getFlyRadarView().updateTextUiColor(
							BackCarTag.F_R_MODE_TEXT,
							BackCarTag.FR_TEXT_COLOR_U);
					mBackCarService.getFlyRadarView()
							.updateTextUiColor(BackCarTag.FR_MODE_TEXT,
									BackCarTag.FR_TEXT_COLOR_C);
					mBackCarModule.showFlyButtonBg(
							BackCarTag.CAMERA913_F_R_MODE, (byte) 0);
					mBackCarModule.showFlyButtonBg(BackCarTag.CAMERA913_F_MODE,
							(byte) 0);
					mBackCarModule.showFlyButtonBg(BackCarTag.CAMERA913_R_MODE,
							(byte) 0);
					mBackCarModule.showFlyButtonBg(
							BackCarTag.CAMERA913_FR_MODE, (byte) 1);
					mBackCarService.set913Camera_fr_mode(MsgType.FR_MODE);
					break;

				case BackCarTag.CAMERA913_F_MODE:
					mBackCarService.getFlyRadarView().updateTextUiColor(
							BackCarTag.F_MODE_TEXT, BackCarTag.FR_TEXT_COLOR_C);
					mBackCarService.getFlyRadarView().updateTextUiColor(
							BackCarTag.R_MODE_TEXT, BackCarTag.FR_TEXT_COLOR_U);
					mBackCarService.getFlyRadarView().updateTextUiColor(
							BackCarTag.F_R_MODE_TEXT,
							BackCarTag.FR_TEXT_COLOR_U);
					mBackCarService.getFlyRadarView()
							.updateTextUiColor(BackCarTag.FR_MODE_TEXT,
									BackCarTag.FR_TEXT_COLOR_U);
					mBackCarModule.showFlyButtonBg(
							BackCarTag.CAMERA913_FR_MODE, (byte) 0);
					mBackCarModule.showFlyButtonBg(BackCarTag.CAMERA913_R_MODE,
							(byte) 0);
					mBackCarModule.showFlyButtonBg(
							BackCarTag.CAMERA913_F_R_MODE, (byte) 0);
					mBackCarModule.showFlyButtonBg(BackCarTag.CAMERA913_F_MODE,
							(byte) 1);
					mBackCarService.set913Camera_fr_mode(MsgType.F_MODE);
					break;
				case BackCarTag.CAMERA913_R_MODE:
					mBackCarService.getFlyRadarView().updateTextUiColor(
							BackCarTag.F_MODE_TEXT, BackCarTag.FR_TEXT_COLOR_U);
					mBackCarService.getFlyRadarView().updateTextUiColor(
							BackCarTag.R_MODE_TEXT, BackCarTag.FR_TEXT_COLOR_C);
					mBackCarService.getFlyRadarView().updateTextUiColor(
							BackCarTag.F_R_MODE_TEXT,
							BackCarTag.FR_TEXT_COLOR_U);
					mBackCarService.getFlyRadarView()
							.updateTextUiColor(BackCarTag.FR_MODE_TEXT,
									BackCarTag.FR_TEXT_COLOR_U);
					mBackCarModule.showFlyButtonBg(
							BackCarTag.CAMERA913_FR_MODE, (byte) 0);
					mBackCarModule.showFlyButtonBg(BackCarTag.CAMERA913_F_MODE,
							(byte) 0);
					mBackCarModule.showFlyButtonBg(
							BackCarTag.CAMERA913_F_R_MODE, (byte) 0);
					mBackCarModule.showFlyButtonBg(BackCarTag.CAMERA913_R_MODE,
							(byte) 1);
					mBackCarService.set913Camera_fr_mode(MsgType.R_MODE);
					break;
				case BackCarTag.CAMERA913_F_R_MODE:
					mBackCarService.getFlyRadarView().updateTextUiColor(
							BackCarTag.F_MODE_TEXT, BackCarTag.FR_TEXT_COLOR_U);
					mBackCarService.getFlyRadarView().updateTextUiColor(
							BackCarTag.R_MODE_TEXT, BackCarTag.FR_TEXT_COLOR_U);
					mBackCarService.getFlyRadarView().updateTextUiColor(
							BackCarTag.F_R_MODE_TEXT,
							BackCarTag.FR_TEXT_COLOR_C);
					mBackCarService.getFlyRadarView()
							.updateTextUiColor(BackCarTag.FR_MODE_TEXT,
									BackCarTag.FR_TEXT_COLOR_U);
					mBackCarModule.showFlyButtonBg(
							BackCarTag.CAMERA913_FR_MODE, (byte) 0);
					mBackCarModule.showFlyButtonBg(BackCarTag.CAMERA913_F_MODE,
							(byte) 0);
					mBackCarModule.showFlyButtonBg(BackCarTag.CAMERA913_R_MODE,
							(byte) 0);
					mBackCarModule.showFlyButtonBg(
							BackCarTag.CAMERA913_F_R_MODE, (byte) 1);
					mBackCarService.set913Camera_fr_mode(MsgType.F_ORIR_MODE);
					break;
				case BackCarTag.DigInShowMainView:
					mBackCarService.getFlyBackCarMainView().mSetFronView();
					mBackCarModule.reBackLight();
					break;

				case BackCarTag.UITEST913_150:
					try {
						final String touch_state = data
								.getString(BackCarTag.STRKEY);
						if (touch_state.equals(BackCarTag.TOUCH_MOVE)) {
							int evn_xy[] = new int[2];
							evn_xy = data.getIntArray(BackCarTag.INTKEY);
							mBackCarModule.SetTestBtnLayout(
									FlyUtil.HexToTag(BackCarTag.UITEST913_150),
									evn_xy[0], evn_xy[1]);
						}
					} catch (Exception e) {
					}
					break;

				case BackCarTag.UITEST913_180:
					try {
						final String touch_state = data
								.getString(BackCarTag.STRKEY);
						if (touch_state.equals(BackCarTag.TOUCH_MOVE)) {
							int evn_xy[] = new int[2];
							evn_xy = data.getIntArray(BackCarTag.INTKEY);
							mBackCarModule.SetTestBtnLayout(
									FlyUtil.HexToTag(BackCarTag.UITEST913_180),
									evn_xy[0], evn_xy[1]);
						}
					} catch (Exception e) {
					}
					break;

				}

			} else if ((int) msg.what == MsgType.COMMON) {
				switch ((int) msg.arg1) {
				case BackCarTag.STARTBYUSER:
					chioce913Page(FlyUtil.HexToTag(BackCarTag.CHOICE_BNT), 1);
					break;

				case BackCarTag.MODULEEXIT:
					BaseModuleExit();
					break;
				case BackCarTag.MODULEENTER:
					Log.d("DDD", "MODULEENTER ");
					if (mBackCarService.getBackCarMode() == mBackCarService.BACKCAR_914
							|| mBackCar913Service.is913StartByUser())
						BLACKPAGESHOW();
					STOP_LOGO();
					BaseModuleStart();
					GLTrackViewToolHelper.getInstance(mBackCarService).onResume();
					break;
				case BackCarTag.MODULEPREENTER:

					break;

				case BackCarTag.BACKTO913:
					Log.d("DDD", "BACKTO913 ");
					BLACKPAGEHIDE();
					if (BackCar913Service.Mode913.lastPage == 1) {

						String focusTag = BackCar913Service.Mode913.lastfocusTag;
						int chioceDir = BackCar913Service.Mode913.lastDirect;
						BackCar913Service.Mode913.chioceDire = chioceDir;
						if (!focusTag.equals(FlyUtil
								.HexToTag(BackCarTag.CHOICEBACK_BNT))) {
							if (chioceDir == 1)
								focusTag = FlyUtil
										.HexToTag(BackCarTag.CHOICE_BNT);
							else
								focusTag = FlyUtil
										.HexToTag(BackCarTag.CHOICE_BNT2);
						}
						chioce913Page(focusTag, chioceDir);
						mBackCarService.showFloatApp(true, -1);// ��ʾ���
					} else { // ����ԭ��ǰ����
						if(IsCVBVdoMode()) // �����913ģʽ������show
							mBackCarModule.ShowBackCarView913();
						video913Page(BackCar913Service.Mode913.lastDirect,
								BackCar913Service.Mode913.lastAngle);
					}
					break;
				case BackCarTag.BACKKEYEVENT:
					mBackCarModule.backKeyCode_Deal();
					break;
				case BackCarTag.HOMEEYEVENT:
					if (!mBackCarModule.getBackCarState()) {
						if (mBackCar913Service.is913StartByUser()
								|| mBackCar913Service
										.getBackCar913StallDState())
							key_backcar913stop();
					}
					break;
				case BackCarTag.ADDLIGHTVIEW:
					mBackCarService.getFlyBackCarMainView()
							.setCurLightLayout(1);
					final String msgTag = data.getString(BackCarTag.STRKEY);
					FlyBaseListener.focus(msgTag);
					break;
				case BackCarTag.RMLIGHTVIEW:
					mBackCarService.getFlyBackCarMainView()
							.setCurLightLayout(2);
					break;
				case BackCarTag.ANYKEYEVENT:
					mBackCar913Service.Remove_BackCar913_StallD_Auto_Stop();
					break;
				}
			} else if ((int) msg.what == MsgType.SETLIGHT) {
				// Log.d(TAG, "showlight "+ String.format("%08x", msg.arg1));
				int value = msg.arg1 - 0x070f0000;
				if (msg.arg1 >= 0x070f0010)
					value = 10 + msg.arg1 - 0x070f0010;
				mBackCarModule.showLightLevel(value);

			}
		}
	};

	@Override
	public void front913Video(String focusTag) {
		Log.d(TAG, "front913Video");
		removeChoiceView();
		mBackCarService.getFlyBackCarMainView().addLayoutView(
				BackCarTag.ADDBTNVIEW);
		mBackCar913Service
				.changeFront(0, FlyBaseListener.mfindViewWithTag(FlyUtil
						.HexToTag(BackCarTag.SET150VIDEO_CID)));
		mBackCar913Service
				.changeFront(1, FlyBaseListener.mfindViewWithTag(FlyUtil
						.HexToTag(BackCarTag.SET180VIDEO_CID)));

		super.front913Video(focusTag);
	}

	@Override
	public void rear913Video(String focusTag) {
		Log.d(TAG, "rear913Video");
		removeChoiceView();
		mBackCarService.getFlyBackCarMainView().addLayoutView(
				BackCarTag.ADDBTNVIEW);
		mBackCar913Service
				.changeRear(0, FlyBaseListener.mfindViewWithTag(FlyUtil
						.HexToTag(BackCarTag.SET150VIDEO_CID)));
		mBackCar913Service
				.changeRear(1, FlyBaseListener.mfindViewWithTag(FlyUtil
						.HexToTag(BackCarTag.SET180VIDEO_CID)));

		super.rear913Video(focusTag);
	}

	@Override
	public void showChoiceView() {
		mBackCarModule.setCurPageID(PageID.PAGE_913CHOICE);
		mBackCarModule.initRadarView(PageID.PAGE_913CHOICE);
		mBackCarService.getFlyRadarView().ShowRadarView();
		mBackCarModule.setRadarViewState(true);
	}

	@Override
	public void removeChoiceView() {

		mBackCarService.DealBackCarMessages(MsgType.MSG_REMOVE,
				MsgType.MSG_CHECK_SIGNAL, 0);
		mBackCarService.getFlyRadarView().RemoveRadarView();
		mBackCarModule.setRadarViewState(false);
		mBackCarModule.setCurPageID(0);
	}

	void video913Page(int dire, int angle) {
		// mBackCarService.getFlyBackCarMainView().addLayoutView(BackCarTag.ADDBTNVIEW);
		if (dire == 0)
			mBackCarModule.rear913Video(null);
		else if (dire == 1)
			mBackCarModule.front913Video(null);

		if (angle == 2) {
			FlyBaseListener.focus(FlyUtil.HexToTag(BackCarTag.SET150VIDEO_CID));
			mBackCarService
					.MakeAndSendMessageButton(BackCarTag.SET150VIDEO_CID);

		} else if (angle == 1) {
			FlyBaseListener.focus(FlyUtil.HexToTag(BackCarTag.SET180VIDEO_CID));
			mBackCarService
					.MakeAndSendMessageButton(BackCarTag.SET180VIDEO_CID);
		}
	}

	public void chioce913Page(String focus, int direct) {
		removeChoiceView();

		mBackCarModule.setChoiceFRMode();
		mBackCarService.MakeAndSendMessageWithBundle(MsgType.COMMON,
				BackCarTag.RMLIGHTVIEW, null);
		showChoiceView();
		diret913 = direct;
		mBackCarService.getBackCarView().setBackCarView(false);
		// showFlyButtonBg(CHOICE_BNT, (byte)diret913);
		FlyBaseListener.focus(focus);
		mBackCarModule.ToModule913CameraSetLight((byte) 0xff);
		// save status
		BackCar913Service.Mode913.page = BackCarTag.CHIOCEPAGE;
		BackCar913Service.Mode913.focusTag = focus;
		BackCar913Service.Mode913.chioceDire = diret913;
	}

	public void Benz913choiceFR_U() {
		FlyBaseListener.setObjLevel(BackCarTag.CHOICECIRCLE, 0);
		FlyBaseListener.setObjLevel(BackCarTag.CHOICEBG, 0);
		int bglevel = BackCar913Service.Mode913.chioceDire;
		if (bglevel == 0) {
			FlyBaseListener.setObjLevel(
					FlyUtil.HexToTag(BackCarTag.CHOICE_BNT), 0);
			FlyBaseListener.setObjLevel(
					FlyUtil.HexToTag(BackCarTag.CHOICE_BNT2), 1);
		} else if (bglevel == 1) {
			FlyBaseListener.setObjLevel(
					FlyUtil.HexToTag(BackCarTag.CHOICE_BNT), 1);
			FlyBaseListener.setObjLevel(
					FlyUtil.HexToTag(BackCarTag.CHOICE_BNT2), 0);
		}

	}

	public void Benz913ChangeFR_UI(int fr_mode) {
		FlyBaseListener.setObjLevel(BackCarTag.CHOICECIRCLE, 0);
		switch (fr_mode) {
		case MsgType.FR_MODE:
			Benz913choiceFR_U();
			FlyBaseListener.findViewAndShowOrNot(BackCarTag.CHOICECIRCLE, true);
			break;
		case MsgType.F_MODE:
			FlyBaseListener.setObjLevel(BackCarTag.CHOICECARBG, 1);
			FlyBaseListener
					.findViewAndShowOrNot(BackCarTag.CHOICECIRCLE, false);
			break;
		case MsgType.R_MODE:
			FlyBaseListener.setObjLevel(BackCarTag.CHOICECARBG, 0);
			FlyBaseListener
					.findViewAndShowOrNot(BackCarTag.CHOICECIRCLE, false);
			break;
		case MsgType.F_ORIR_MODE:
			FlyBaseListener.setObjLevel(BackCarTag.CHOICECARBG, 1);
			FlyBaseListener
					.findViewAndShowOrNot(BackCarTag.CHOICECIRCLE, false);
		}
	}
}
