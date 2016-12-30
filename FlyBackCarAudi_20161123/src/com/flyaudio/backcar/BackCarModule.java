package com.flyaudio.backcar;

import java.util.Locale;
import java.util.Random;

import android.content.Context;
import android.content.Intent;
import android.os.Messenger;
import android.util.Log;
import com.flyaudio.backcar913.*;
import com.autochips.backcar.BackCar;
import com.flyaudio.backcar.modules.*;
import com.flyaudio.backcar.util.*;
import com.flyaudio.tool.GLTrackViewToolHelper;

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

public class BackCarModule {
	private BackCarService mBackCarService;
	private boolean bshowRadarView = false;
	private boolean bsignal = false;
	private boolean bbackcarstate = false;
	private boolean bHaveTellModule = false;
	private boolean bshowactivity = false;
	// 閸旂姳閲滈柨渚婄礉闁夸椒缍囬懢宄板絿BackCar.getEvent()閻ㄥ嫭瀵氭禒銈忕礉娣囨繆鐦夐崣顏囧厴 start -> stop ->
	// start -> stop
	private boolean bbackcarlock = false;
	private boolean bcStartRuning = false;
	public int backcarMode = 3; // 3 == BACKCAR_CVB
	private int iPageID = 0;
	private String TAG = "BackCarModule";
	private BackCar913Service mBackCar913Service = null;
	private int DigInOn_offStatus = 0;
	private int CameraCtlMode = 0;

	public BackCarModule(BackCarService mService) {
		mBackCarService = mService;

	}

	public void setUpBackCar913Service(BackCar913Service server) {
		mBackCar913Service = server;

	}

	// 闁氨鐓odule閿涘異ackcar鏉╂稑鍙嗛崐鎺曟簠/radar閺勫墽銇氭い鐢告桨
	public void tellmoduleshowView(boolean bshow) {
		byte ControlType = (byte) 0XFE;
		byte[] paramBuf = new byte[2];

		paramBuf[0] = 0x02;// 娴狅綀銆傿ACKCAR video type
		if (bshow)
			paramBuf[1] = 0x01;
		else
			paramBuf[1] = 0x00;
		mBackCarService.MakeAndSendMessage(0x0200FE, ControlType, paramBuf);
	}

	public void tellmoduleErrTimes(byte errIndex, byte times, byte times2) {
		byte ControlType = (byte) 0XFE;
		byte[] paramBuf = new byte[4];
		paramBuf[0] = (byte) 0xE0;
		paramBuf[1] = errIndex;
		paramBuf[2] = times;
		paramBuf[3] = times2;

		mBackCarService.MakeAndSendMessage(0x0200FE, ControlType, paramBuf);
	}

	public void tellmoduleRefAngle(byte Index) {

		byte[] paramBuf = new byte[3];
		paramBuf[0] = (byte) 0xE1;
		paramBuf[1] = (byte) 0x20;
		paramBuf[2] = (byte) 0x08;

		Log.i(TAG, "tellmoduleRefAngle xxxxx");

		mBackCarService.MakeAndSendMessage(0x00, UIAction.UIACTION_PARAMETER,
				paramBuf);
	}

	//��d����Э���lpc �����߽���d����
	public void tellmoduleStallDStatus(boolean flag) {
		if (mBackCarService.mgetBackCarMode() == mBackCarService.BACKCAR_914) {
			byte[] data = new byte[4];
			data[0] = (byte) 0xE1;
			data[1] = (byte) 0x20;
			data[2] = (byte) 0x09;
			if (flag)
				data[3] = (byte) 0x1;
			else
				data[3] = (byte) 0x0;
			MakeAndSendMessageToModule(0x00, UIAction.UIACTION_PARAMETER, data);
			Log.i(TAG, "tellmoduleStallDStatus  = " + flag);
		}

	}

//����lpc��ǰѡ�������һ��ǰ����ģʽ
	public void tellmodule913DeviceSelect(int fr_mode) {
		byte[] data = new byte[4];
		data[0] = (byte) 0xE1;
		data[1] = (byte) 0x20;
		data[2] = (byte) 0x0A;
		byte mode = (byte) fr_mode;
		data[3] = mode;
		MakeAndSendMessageToModule(0x00, UIAction.UIACTION_PARAMETER, data);
		Log.i(TAG, "tellmodule913DeviceSelect_mode  = " + mode);

	}

	public void tellmoduleViewisInit(byte viewinit) {
		byte ControlType = (byte) 0XFE;
		byte[] paramBuf = new byte[2];
		paramBuf[0] = (byte) 0xE2;
		paramBuf[1] = viewinit;
		mBackCarService.MakeAndSendMessage(0x0200FE, ControlType, paramBuf);
	}

	//֪ͨ�����˳������ڴ�����
	public void TurnModuleStop() {
		if (!GetTellModuleStatus())
			return;
		byte[] data = new byte[2];

		data[0] = 0x00;
		data[1] = 0x02;
		MakeAndSendMessageToModule(0x02, UIAction.UIACTION_MOUSEUP, data);
		Log.i(TAG, "TurnOff");
		SetTellModuleStatus(false);
	}

	//֪ͨ�������룬���ڴ����
	public void TellModuleStart() {
		if (GetTellModuleStatus())
			return;
		byte[] data = new byte[2];
		data[0] = 0x00;
		data[1] = 0x01;
		MakeAndSendMessageToModule(0x02, UIAction.UIACTION_MOUSEUP, data);
		Log.i(TAG, "TurnOn");
		SetTellModuleStatus(true);
	}

	//�л�ǰ����������Ƶ  ǰ �������ͷ
	public void ToModule913CameraCtl(byte mode) {
		byte[] data = new byte[3];
		data[0] = 0x00;
		data[1] = 0x01;
		data[2] = mode;
		MakeAndSendMessageToModule(0x03, UIAction.UIACTION_PARAMETER, data);
		CameraCtlMode = mode;
		Log.i(TAG, "ToModule913CameraCtl mode = " + mode);
	}

	public void ToModule913CameraSetLight(byte lv) {
		byte[] data = new byte[3];
		data[0] = 0x00;
		data[1] = 0x02;
		data[2] = lv;
		MakeAndSendMessageToModule(0x03, UIAction.UIACTION_PARAMETER, data);
		Log.i(TAG, "ToModule913CameraSetLight mode = " + lv);
	}

	
	//����ǰ���ӵĹ��磬
	public void ToModule913CameraOn_Off(byte isOn) // isOn 1 on 0 Off
	{
		byte[] data = new byte[3];
		data[0] = 0x00;
		data[1] = 0x03;
		data[2] = isOn;
		MakeAndSendMessageToModule(0x03, UIAction.UIACTION_PARAMETER, data);
		DigInOn_offStatus = isOn;
		Log.i(TAG, "ToModule913CameraOn_Off isOn = " + isOn);
	}

	/*
	 * @ Param FR 0x90 qian 0x91 hou
	 */

	public void ToModule913FRSoundLevel(byte FR, byte sound, byte tone) {
		byte[] data = new byte[5];
		data[0] = 0x00;
		data[1] = 0x04;
		data[2] = FR;
		data[3] = tone;
		data[4] = sound;
		MakeAndSendMessageToModule(0x03, UIAction.UIACTION_PARAMETER, data);
		if(BaseModule.getBaseModule().DEBUG)
		Log.i(TAG, "ToModule913FRSoundLevel S " + sound + " T " + tone);
	}

	//���ƹر��״�ϵͳ�Ͱµ�ԭ�������
	public void ToModule913Close_SYS(byte value) {
		byte[] data = new byte[3];
		data[0] = 0x00;
		data[1] = 0x05;
		MakeAndSendMessageToModule(0x03, UIAction.UIACTION_PARAMETER, data);
		Log.i(TAG, "ToModule913Close_SYS " + value);
	}
	
	//���ƹر��״�ϵͳ�Ͱµ�ԭ�������
	public void ToModule913OPEN_SYS(byte value) {
		byte[] data = new byte[3];
		data[0] = 0x00;
		data[1] = 0x07;
		MakeAndSendMessageToModule(0x03, UIAction.UIACTION_PARAMETER, data);
		Log.i(TAG, "ToModule913OPEN_SYS ");

	}

	public void ToModule913RequestFRSound(byte value) // isOn 1 on 0 Off
	{
		byte[] data = new byte[3];
		data[0] = 0x00;
		data[1] = 0x06;
		MakeAndSendMessageToModule(0x03, UIAction.UIACTION_PARAMETER, data);
		Log.i(TAG, "ToModule913RequestFRSound " + value);
	}

	public boolean GetDigInCameraOn_OffStatus() {
		if (DigInOn_offStatus == 1)
			return true;
		return false;
	}

	public boolean isShowing913RearVideo() {
		if (CameraCtlMode == 0)
			return true;
		return false;
	}

	// 閺勵垰鎯侀張澶婏拷鏉烇箒顬呮０鎴滀繆閸欏嚖绱濋弰鍓с仛閻╃绨叉い鐢告桨
	public void showsignview(boolean bsignOn) {

		setsignal(bsignOn);
		// 閸婃帟婧呴悩鑸碉拷閺冭埖澧犳径鍕倞娣団�冲娇閺勫墽銇氶敍锟�
		// 閺堝閲渂ug閸︺劋绨柅锟藉毉閸婃帟婧呴弮鏈电窗瀵よ泛鎮楅張濉秓 sign娑撳﹥娼甸敍灞芥礈濮濄倕濮炲銈呭灲閺傤厼顦甸悶锟�
		if (!getBackCarState() && !BackCar913Service.BackCar913StartByUser
				&& !mBackCar913Service.getBackCar913StallDState()) {
			Log.i(TAG,
					"It is not backcaring and not 913StartByUser ,not mBackCar913StallD"); //
			return;
		}
		int ipageid = 0;
		// Log.i(TAG, "showsignview bsignOn "+ bsignOn);
		if (bsignOn) {
			// 缂佸棜濡径鍕倞
			// 1閿涙艾婀梿鐤彧妞ょ敻娼伴崐鎺曟簠
			// 2閿涙艾锟芥潪锔炬畱鐟欏棝顣堕弶銉ф畱濮ｆ棁绶濋幈顤庣礉娴兼艾鍘涢崙娲祫鏉堥箖銆夐崘宥呭毉閸婃帟婧呮い锟�
			// ipageid = getCurPageID();
			ipageid = FlyaudioProperties.GetBackCarpageid();
			RemoveRadarView();
			setCurPageID(ipageid);
			mBackCarService.DealBackCarMessages(MsgType.MSG_REMOVE,
					MsgType.MSG_SETGAMMA, 0);
			mBackCarService.DealBackCarMessages(MsgType.MSG_SENGDELAY,
					MsgType.MSG_SETGAMMA, MsgType.SETGAMMA_TIMEOUT);

			// if(getsignal())//閸ョ姳璐熷鑸垫閻ㄥ嫬甯崶鐙呯礉娣囨繆鐦夊▽鈩冩箒缂佹獨top閿涳拷
			// mBackCarService.SetBackCarGamma(MsgType.COLOR_BackCar);
		} else {
			// 閺冪姳淇婇崣椋庢纯閹恒儲娼甸梿鐤彧妞ゅ灚鍨ㄩ弮鐘侯瀰妫版垿銆�
			ipageid = FlyaudioProperties.GetBackCarpageid();
			mBackCarService.getFlyUIMsgClient().getHandleServiceMsg()
					.JumpPagetest(ipageid);
		}

	}

	// 閺堫亝甯撮崐鎺曟簠鐟欏棝顣剁痪鍖＄礉閸婃帟婧呴敍灞惧絹缁�鐑樻￥閸婃帟婧呯憴鍡涱暥娣団�冲娇閹绘劗銇�/闂嗙柉鎻弰鍓с仛閿涘矂鍣伴悽銊ユ閺冭埖顥戝ù锟�
	// 鐠囥儰淇婇崣閿嬵棏濞村妲搁悽鈺〆ssage婢跺嫮鎮婇敍瀹籭gnal
	// on閻ㄥ嫭妞傞崐娆掝嚉message鐏忚京绮板〒鍛存珟閿涘苯鑻熸稉宥勭窗閹笛嗩攽鐠囥儲鎼锋担锟�
	// 闁槒绶稉濠冨妳鐟欏鐥呴張澶夌矆娑斿牓妫舵０姗堢礉婢堆傜泛閸欘垯浜掗幐鍥ь嚤娑擄拷绗呴敍锟�
	// 娴ｅ棙绁寸拠鏇炲絺閻滃府绱濈拠顨奺ssage楠炶埖鐥呴張澶嬬闂勩倧绱掗敍浣告礈濮濄倧绱掗崝鐘差檵娴滃攦etsignal閸掋倖鏌�

	public void checksignal() {
		// Log.i(TAG, "checksignal  "+ getsignal());
		int curpageid = getCurPageID();
		if (getsignal() || !getBackCarState()
				|| curpageid == PageID.PAGE_913CHOICE) {
			Log.i(TAG, "checksignal not backcaring " + getBackCarState()
					+ " or is signal " + getsignal()
					+ " or curpage is PAGE_913CHOICE");
			return;
		}
		int ipageid = FlyaudioProperties.GetBackCarpageid();
		mBackCarService.getFlyUIMsgClient().getHandleServiceMsg()
				.JumpPagetest(ipageid);
	}

	void changePage(int mode) {
		if (mode == mBackCarService.BACKCAR_CVB || mode == mBackCarService.BACKCAR_CVB_SV) {
			// mBackCarService.getFlyBackCarMainView().addLayoutView(BackCarTag.RMBTNVIEW);
			// mBackCarService.getFlyBackCarMainView().setFronView(false);
		} else if (mode == mBackCarService.BACKCAR_914) {
			mBackCarService.getFlyBackCarMainView().addLayoutView(
					BackCarTag.ADDBTNVIEW);

			Io913_startVideo();
		}
	}

	// 閺勫墽銇氶崐鎺曟簠闂嗙柉鎻い鐢告桨 濞夈劍鍓版穱婵嗙摠瑜版挸澧犻梿鐤彧妞わ拷
	private void ShowBackCarView() // cvb or camera will run this
	{
		int ipageid = getCurPageID();

		if (BackCar913Service.BackCar913StartByUser) // not 913 in
		{
			Log.i(TAG, "913StartByUser true");

			mBackCar913Service.set913ToOther();
			// will be camera or cvb
			mBackCarService.getBaseModule().removeChoiceView();
			mBackCarService.getBackCarView().setBackcarMode(
					mBackCarService.mgetBackCarMode());
			mBackCarService.getFlyBackCarMainView().ShowBackCarView();

		} else {

			if (BackCarService.BACKCAR_914 == mBackCarService.getBackCarView()
					.retCurBackCarMode()) {
				// backcarMode = mBackCarService.getBackCarMode();
				mBackCarService.getFlyBackCarMainView().InitBackCarView();
				tellmoduleViewisInit((byte) 0);
			}
			mBackCarService.getBackCarView().setBackcarMode(
					mBackCarService.getBackCarMode());
			// mBackCarService.getBackCarView().setBackCarView(true);
			mBackCarService.getFlyBackCarMainView().ShowBackCarView();

			RemoveRadarView();
			setCurPageID(ipageid);
		}
		tellmoduleRefAngle((byte) 0x0);

	}

	// 閸忔娊妫撮崐鎺曟簠闂嗙柉鎻い鐢告桨
	public void RemoveBackCarView() {
		mBackCarService.getFlyBackCarMainView().RemoveBackCarView();
	}

	// 閸掓繂顬婇崠鏈darView
	public void initRadarView(int layout) {
		mBackCarService.getFlyRadarView().initRadarView(layout);
	}

	// 閺勫墽銇歊adarView
	public void ShowRadarView() {
		if (getRadarViewState() == false)
			mBackCarService.getFlyRadarView().ShowRadarView();
		setRadarViewState(true);
		TellModuleStart();
	}

	// 缁夊娅嶳adarView
	public void RemoveRadarView() {
		if (getRadarViewState()) {
			mBackCarService.getFlyRadarView().RemoveRadarView();
			setRadarViewState(false);
		}
		setCurPageID(0);
	}

	// 闁氨鐓odule闁氨鐓￠崗鎶芥４閸婃帟婧呴梿鐤彧妞ょ敻娼伴敍锟�

	public void RemoveRadarViewbyID(int pageid) {

		if (BackCar913Service.BackCar913StartByUser == true
				|| mBackCarService.mgetBackCarMode() == BackCarService.BACKCAR_914) {

			return;
		}
		if(BaseModule.getBaseModule().hasNoBntExit)
			return;
		
		if (getBackCarState()) {
			setCurPageID(0);
			Log.i(TAG, "It is backcaring,so don't show radar page");
			return;
		}
		
		if(BaseModule.getBaseModule().DEBUG)
		Log.d(TAG, "RemoveRadarViewbyID 913 BackCar913StartByUser = "
				+ BackCar913Service.BackCar913StartByUser);
		if (getRadarViewState()) {
			TurnModuleStop();
		}
		RemoveRadarView();
		// removeActivity();
		mBackCarService.DealBackCarMessages(MsgType.MSG_REMOVE,
				MsgType.MSG_BACKCAR_STOPACTIVITY, 0);
		mBackCarService.DealBackCarMessages(MsgType.MSG_SENGDELAY,
				MsgType.MSG_BACKCAR_STOPACTIVITY,
				MsgType.RESUME_ACTIVITY_TIMEOUT);
	}

	// 像 RAV4带有额外页面的在倒车中可以控的，跳页，看倒车皮肤会明白
	public void changeExtraPageView(int extraPageId) {
		mBackCarService.getFlyBackCarMainView().extra_Ui_Pagejump(extraPageId);

	}

	// 闁氨鐓″锟芥儙閸婃帟婧卾iew閿涘本澧界悰宀嬬磼
	public void backcarstart() {

		// JudgeManchineNum();//wty

		showNoticeView(true);
		ShowBackCarView();
		showActivity();
		mBackCarService.DealBackCarMessages(MsgType.MSG_SENGDELAY,
				MsgType.MSG_CHECK_SIGNAL, MsgType.SIGNALREADY_TIMEOUT);
		mBackCarService.DealBackCarMessages(MsgType.MSG_SENGDELAY,
				MsgType.MSG_STOPNOTICE, MsgType.STOPNOTICE_TIMEOUT);

	}

	// 闁氨鐓￠崗鎶芥４閸婃帟婧卾iew閿涘本澧界悰宀嬬磼
	public void backcarstop() {
		BaseModule.getBaseModule().backcarstop();
		/*
		 * RemoveBackCarView();
		 * 
		 * int ipageid = FlyaudioProperties.GetBackCarpageid();
		 * setCurPageID(ipageid);
		 * 
		 * if(BaseModule.getBaseModule() != null){ setCurPageID(0x0); }
		 * 
		 * if((getCurPageID() == PageID.PAGE_NOVIDEO_TIP || getCurPageID() ==
		 * 0x0)) { Log.d(TAG, "backcarstop" +
		 * Integer.toHexString(getCurPageID()).toUpperCase());
		 * RemoveRadarView(); removeActivity(); TurnModuleStop(); } else {
		 * 
		 * if(getRadarViewState()==false) initRadarView(getCurPageID());
		 * ShowRadarView(); // SystemProperties.set("fly.backcar.radar", "");
		 * //for platform test //
		 * mBackCarService.DealBackCarMessages(MsgType.MSG_SENGDELAY
		 * ,MsgType.MSG_BACKCAR_STOP, 3000); } setBackCarLock(false);
		 * mBackCarService
		 * .DealBackCarMessages(MsgType.MSG_REMOVE,MsgType.MSG_CHECK_SIGNAL, 0);
		 * mBackCarService
		 * .DealBackCarMessages(MsgType.MSG_REMOVE,MsgType.MSG_STOPNOTICE, 0);
		 */
	}

	// 鐠佸墽鐤嗛梿鐤彧妞ょ敻娼伴弰鍓с仛閻樿埖锟�
	public void setRadarViewState(boolean bshowRadarView) {
		this.bshowRadarView = bshowRadarView;
	}

	// 閼惧嘲褰囬梿鐤彧妞ょ敻娼伴弰鍓с仛閻樿埖锟�
	public boolean getRadarViewState() {
		return bshowRadarView;
	}

	// 鐠佸墽鐤嗛弰顖氭儊閺堝顬呮０鎴滀繆閸欙拷
	public void setsignal(boolean bsignal) {
		this.bsignal = bsignal;
	}

	// 閼惧嘲褰囬弰顖氭儊閺堝顬呮０鎴滀繆閸欙拷
	public boolean getsignal() {
		return bsignal;
	}

	// 鐠佸墽鐤嗛崐鎺曟簠閻樿埖锟�
	private void setBackCarState(boolean bstate) {
		Log.d(TAG, "setBackCarState =======>" + bstate);
		this.bbackcarstate = bstate;
	}

	// 閼惧嘲褰囬崐鎺曟簠閻樿埖锟�
	public boolean getBackCarState() {
		return bbackcarstate;
	}

	// 鐠佸墽鐤嗚ぐ鎾冲妞ょ敻娼癐D
	public void setCurPageID(int iPageID) {
		this.iPageID = iPageID;
		// Log.d(TAG, "setCurPageID" +
		// Integer.toHexString(iPageID).toUpperCase());
	}

	// 閼惧嘲褰囪ぐ鎾冲妞ょ敻娼癐D
	public int getCurPageID() {
		return iPageID;
	}

	// 閸旂姴鍙嗛柌宥咁樉start娣団�冲娇闁匡拷
	public void setBackCarLock(boolean bbackcarlock) {
		this.bbackcarlock = bbackcarlock;
	}

	public boolean getBackCarLock() {
		return bbackcarlock;
	}

	private boolean GetTellModuleStatus() {
		return bHaveTellModule;
	}

	private void SetTellModuleStatus(boolean bstart) {
		this.bHaveTellModule = bstart;
	}

	// 濡拷绁磗tart娣団�冲娇閿涘本澧界悰锟�

	public void BACKCAR_START() {
		// setsignal(false);
		// 闂冨弶顒涢柌宥咁樉START閿涳拷

		if (getBackCarLock()) {
			Log.i(TAG, "It is repetition BACKCAR_START");
			return;
		}
		setBackCarLock(true);
		if (isNeedSetLanguage() || bneedchangpageid()) {
			// Log.i(TAG, "It is need change Language");
			mBackCarService.getFlyBackCarMainView().InitBackCarView();
			tellmoduleViewisInit((byte) 0);
	// 对一些全景车型在安装界面的时候判断去掉辅助线，module那时未起来控不了。
			showReverseView(BaseModule.getBaseModule().isAuxLine_show());
		}
		mBackCarService.BACKCAR_START();
		setBackCarState(true);
		TellModuleStart();
	}

	// 濡拷绁磗top娣団�冲娇閿涘本澧界悰锟�
	public void BACKCAR_STOP() {
		// 闂冨弶顒涢柌宥咁樉STOP閿涳拷

		if (!getBackCarLock()) {
			mBackCarService.setBcStop(true);
			Log.i(TAG, "It is repetition BACKCAR_STOP.");
			RemoveRadarView();
			return;
		}

		setsignal(false);
		mBackCarService.DealBackCarMessages(MsgType.MSG_REMOVE,
				MsgType.MSG_SIGNAL_READY, 0);
		mBackCarService.DealBackCarMessages(MsgType.MSG_REMOVE,
				MsgType.MSG_SETGAMMA, 0);
		mBackCarService.SetBackCarGamma(MsgType.COLOR_ARM2);
		mBackCarService.BACKCAR_STOP();
		setBackCarState(false);
	}

	public void SIGNAL_READY() {
		mBackCarService.DealBackCarMessages(MsgType.MSG_REMOVE,
				MsgType.MSG_CHECK_SIGNAL, 0);
		mBackCarService.DealBackCarMessages(MsgType.MSG_REMOVE,
				MsgType.MSG_SIGNAL_LOST, 0);
		mBackCarService.DealBackCarMessages(MsgType.MSG_SENG,
				MsgType.MSG_SIGNAL_READY, 0);
	}

	public void SIGNAL_LOST() {
		mBackCarService.DealBackCarMessages(MsgType.MSG_REMOVE,
				MsgType.MSG_SETGAMMA, 0);
		mBackCarService.SetBackCarGamma(MsgType.COLOR_ARM2);
		mBackCarService.DealBackCarMessages(MsgType.MSG_REMOVE,
				MsgType.MSG_CHECK_SIGNAL, 0);
		mBackCarService.DealBackCarMessages(MsgType.MSG_REMOVE,
				MsgType.MSG_SIGNAL_READY, 0);
		mBackCarService.DealBackCarMessages(MsgType.MSG_SENGDELAY,
				MsgType.MSG_SIGNAL_LOST, MsgType.SIGNAL_LOSE_TIMEOUT);
	}

	public void showActivity() {
		mBackCarService.DealBackCarMessages(MsgType.MSG_REMOVE,
				MsgType.MSG_BACKCAR_STOPACTIVITY, 0);
		Intent intentBCActvity = new Intent(
				mBackCarService.getApplicationContext(),
				FlyaudioBackCarActivity.class);
		intentBCActvity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_CLEAR_TOP);
		mBackCarService.StartActvity(intentBCActvity);
		Log.i(TAG, "showActivity - BackCarActivity");
	}

	public void removeActivity() {
		if (null != mBackCarService.getActivity()) {
			mBackCarService.getActivity().finish();
			mBackCarService.setActivityEmpty();
			Log.i(TAG, "removeActivity - BackCarActivity finish ");
		} else {
			Log.i(TAG, "No Activity to removeActivity!");
		}
		tellmoduleshowView(false);
	}

	public void SetActivityStatus(boolean flag) {
		bshowactivity = flag;
	}

	public boolean GetActivityStatus() {
		return bshowactivity;
	}

	private String LocalLanguage;

	public void initLocalLanguage() {
		Locale locale = mBackCarService.getResources().getConfiguration().locale;
		LocalLanguage = locale.getLanguage();
	}

	public boolean isNeedSetLanguage() {
		Locale locale = mBackCarService.getResources().getConfiguration().locale;
		String language = locale.getLanguage();
		if (LocalLanguage == null || language == null)
			return false;
		if (language.matches(LocalLanguage))
			return false;
		LocalLanguage = language;
		return true;
	}

	private int currentReverseType = 0;
	private int ReverseType = 0;

	public void initpageid() {
		currentReverseType = FlyaudioProperties.GetBackCarMainpageid();
	}

	public boolean bneedchangpageid() {
		if (!FlyaudioProperties.needchangpage())
			return false;

		int currentcartype = 0;
		int currentcamera = 0;
		String CARTYPE = SystemProperties.get("fly.set.cartype");
		String CAMERATYPE = SystemProperties.get("fly.set.cameratype");
		if (("").equals(CARTYPE)) {
			currentcartype = 0;
		} else if (CARTYPE.equals("ES")) {
			currentcartype = 0;
		} else if (CARTYPE.equals("NX")) {
			currentcartype = 1;
		} else if (CARTYPE.equals("IS")) {
			currentcartype = 2;
		}

		if (("").equals(CAMERATYPE)) {
			currentcamera = 0;
		} else if (CAMERATYPE.equals("0")) {
			currentcamera = 0;
		} else if (CAMERATYPE.equals("1")) {
			currentcamera = 1;
		}

		if (currentcartype == 0)
			currentReverseType = PageID.PAGE_es250_02_VIDEO;
		else if (currentcartype == 1 && currentcamera == 0)
			currentReverseType = PageID.PAGE_nx300_01_VIDEO;
		else if (currentcartype == 1 && currentcamera == 1)
			currentReverseType = PageID.PAGE_nx300_02_VIDEO;
		else if (currentcartype == 2 && currentcamera == 0)
			currentReverseType = PageID.PAGE_is250_01_VIDEO;
		else if (currentcartype == 2 && currentcamera == 1)
			currentReverseType = PageID.PAGE_is250_01_VIDEO;

		if (currentReverseType == ReverseType)
			return false;
		Log.i("JumpPage", "currentReverseType + !" + currentReverseType);
		ReverseType = currentReverseType;
		return true;
	}

	// 娴犲骸鎮撻幇蹇涖�夐崶鐐存降閿涘矁顔曠純顔芥▔缁�鐑樻櫏閺嬶拷
	public void SetGammaFromNoticePage() {
		if (getsignal()) {
			mBackCarService.DealBackCarMessages(MsgType.MSG_REMOVE,
					MsgType.MSG_SETGAMMA, 0);
			mBackCarService.DealBackCarMessages(MsgType.MSG_SENGDELAY,
					MsgType.MSG_SETGAMMA, 500);
		}
	}

	private void MakeAndSendMessageToModule(int controlID, byte controlType,
			byte[] param) {
		mBackCarService.MakeAndSendMessage(controlID, controlType, param);
	}

	int GET_HUE_VALUE(int value) {
		return value * 0x3F / 100;
	}

	int GET_STA_VALUE(int value) {
		return value * 0xFF / 100;
	}

	/*-------------------------------------------SendUIMsg Method---------------------------------------------------*/
	public void showNoticeView(boolean show) {
		byte[] sendbuf = new byte[11];
		int ControlID = 0x00070113;

		byte ControlType = (byte) 0xff;

		sendbuf[0] = (byte) 0xff;
		sendbuf[1] = 0x55;
		sendbuf[2] = (byte) (4 + 6);
		sendbuf[3] = (byte) ((ControlID >> 24) & 0xff);
		sendbuf[4] = (byte) ((ControlID >> 16) & 0xff);
		sendbuf[5] = (byte) ((ControlID >> 8) & 0xff);
		sendbuf[6] = (byte) (ControlID & 0xff);
		sendbuf[7] = ControlType;
		sendbuf[8] = 00;
		if (show)
			sendbuf[9] = 01;
		else
			sendbuf[9] = 00;
		sendbuf[10] = 00;
		if (!getBackCarLock())
			return;
		BaseModule.getBaseModule().showNoticeView(
				mBackCarService.disableAssist, show);

		mBackCarService.getFlyUIMsgClient().getHandleServiceMsg()
				.analysisData(sendbuf, 11, 0);

		// ShowUiView(BackCarTag.AUXLINE_UP_PARENT,show);
		// ShowUiView(BackCarTag.AUXLINE_DOWN_PARENT,show);
		// !mBackCarService.disableAssist

	}

	public void showReverseView(boolean flag) {
		int ControlID = 0x00070530;
		byte[] sendbuf = new byte[11];
		byte ControlType = (byte) 0xff;
		byte bit = 0;
		if (flag)
			bit = 1;
		else
			bit = 0;
		sendbuf[0] = (byte) 0xff;
		sendbuf[1] = 0x55;
		sendbuf[2] = (byte) (4 + 4);
		sendbuf[3] = (byte) ((ControlID >> 24) & 0xff);
		sendbuf[4] = (byte) ((ControlID >> 16) & 0xff);
		sendbuf[5] = (byte) ((ControlID >> 8) & 0xff);
		sendbuf[6] = (byte) (ControlID & 0xff);
		sendbuf[7] = ControlType;
		sendbuf[8] = 00;
		sendbuf[9] = bit;
		sendbuf[10] = 00;
		mBackCarService.getFlyUIMsgClient().getHandleServiceMsg()
				.analysisData(sendbuf, 11, 0);
	}

	public void showCarView(boolean big) {

		int ControlID = 0x00070120;
		byte[] sendbuf = new byte[13];
		byte ControlType = (byte) 0xff;
		if (big)
			ControlID = 0x00070020;
		sendbuf[0] = (byte) 0xff;
		sendbuf[1] = 0x55;
		sendbuf[2] = (byte) (4 + 4);
		sendbuf[3] = (byte) ((ControlID >> 24) & 0xff);
		sendbuf[4] = (byte) ((ControlID >> 16) & 0xff);
		sendbuf[5] = (byte) ((ControlID >> 8) & 0xff);
		sendbuf[6] = (byte) (ControlID & 0xff);
		sendbuf[7] = ControlType;
		sendbuf[8] = 00;
		sendbuf[9] = 01;
		sendbuf[10] = 00;
		sendbuf[11] = 00;
		sendbuf[12] = 00;
		mBackCarService.getFlyUIMsgClient().getHandleServiceMsg()
				.analysisData(sendbuf, 13, 0);

	}

	public void showBigRadarView(int index, byte pos) {
		byte[] sendbuf = new byte[13];
		int ControlID = 0x00070021;

		byte ControlType = 0x01;
		switch (index) {
		case 0:
			ControlID = 0x00070021;
			break;
		case 1:
			ControlID = 0x00070022;
			break;
		case 2:
			ControlID = 0x00070024;
			break;
		case 3:
			ControlID = 0x00070025;
			break;
		case 4:
			ControlID = 0x00070027;
			break;
		case 5:
			ControlID = 0x00070028;
			break;
		case 6:
			ControlID = 0x0007002a;
			break;
		case 7:
			ControlID = 0x0007002b;
			break;
		case 8:
			showCarView(true);
			break;
		case 9:
			ControlID = 0x00070321;
			break;
		case 10:
			ControlID = 0x00070322;
			break;
		case 11:
			ControlID = 0x00070324;
			break;
		case 12:
			ControlID = 0x00070325;
			break;
		case 13:
			ControlID = 0x00070327;
			break;
		case 14:
			ControlID = 0x00070328;
			break;
		case 15:
			ControlID = 0x0007032a;
			break;
		case 16:
			ControlID = 0x0007032b;
			break;
		}
		sendbuf[0] = (byte) 0xff;
		sendbuf[1] = 0x55;
		sendbuf[2] = (byte) (4 + 6);
		sendbuf[3] = (byte) ((ControlID >> 24) & 0xff);
		sendbuf[4] = (byte) ((ControlID >> 16) & 0xff);
		sendbuf[5] = (byte) ((ControlID >> 8) & 0xff);
		sendbuf[6] = (byte) (ControlID & 0xff);
		sendbuf[7] = ControlType;
		sendbuf[8] = 00;
		sendbuf[9] = 00;
		sendbuf[10] = 00;
		sendbuf[11] = pos;
		sendbuf[12] = 00;

		mBackCarService.getFlyUIMsgClient().getHandleServiceMsg()
				.analysisData(sendbuf, 13, 0);
	}

	public void showSmallRadarView(int index, byte pos) {
		byte[] sendbuf = new byte[13];
		int ControlID = 0x00070121;

		byte ControlType = 0x01;
		switch (index) {
		case 0:
			ControlID = 0x00070121;

			break;
		case 1:
			ControlID = 0x00070122;
			break;
		case 2:
			ControlID = 0x00070124;
			break;
		case 3:
			ControlID = 0x00070125;
			break;
		case 4:
			ControlID = 0x00070127;
			break;
		case 5:
			ControlID = 0x00070128;
			break;
		case 6:
			ControlID = 0x0007012a;
			break;
		case 7:
			ControlID = 0x0007012b;
			break;
		case 8:
			showCarView(false);
			break;
		case 9:
			ControlID = 0x00070221;
			break;
		case 10:
			ControlID = 0x00070222;
			break;
		case 11:
			ControlID = 0x00070224;
			break;
		case 12:
			ControlID = 0x00070225;
			break;
		case 13:
			ControlID = 0x00070227;
			break;
		case 14:
			ControlID = 0x00070228;
			break;
		case 15:
			ControlID = 0x0007022a;
			break;
		case 16:
			ControlID = 0x0007022b;
			break;
		}
		sendbuf[0] = (byte) 0xff;
		sendbuf[1] = 0x55;
		sendbuf[2] = (byte) (4 + 6);
		sendbuf[3] = (byte) ((ControlID >> 24) & 0xff);
		sendbuf[4] = (byte) ((ControlID >> 16) & 0xff);
		sendbuf[5] = (byte) ((ControlID >> 8) & 0xff);
		sendbuf[6] = (byte) (ControlID & 0xff);
		sendbuf[7] = ControlType;
		sendbuf[8] = 00;
		sendbuf[9] = 00;
		sendbuf[10] = 00;
		sendbuf[11] = pos;
		sendbuf[12] = 00;

		mBackCarService.getFlyUIMsgClient().getHandleServiceMsg()
				.analysisData(sendbuf, 13, 0);
	}

	public void showReverseTrack2(byte pos, int lr) {
		byte[] sendbuf = new byte[13];
		int ControlID = 0x00070600;
		byte ControlType = 0x01;
		sendbuf[0] = (byte) 0xff;
		sendbuf[1] = 0x55;
		sendbuf[2] = (byte) (4 + 6);
		sendbuf[3] = (byte) ((ControlID >> 24) & 0xff);
		sendbuf[4] = (byte) ((ControlID >> 16) & 0xff);
		sendbuf[5] = (byte) ((ControlID >> 8) & 0xff);
		sendbuf[6] = (byte) (ControlID & 0xff);
		sendbuf[7] = ControlType;
		sendbuf[8] = 00;
		sendbuf[9] = 00;
		sendbuf[10] = (byte) lr;
		sendbuf[11] = pos;
		sendbuf[12] = 00;

		mBackCarService.getFlyUIMsgClient().getHandleServiceMsg()
				.analysisData(sendbuf, 13, 0);
	}

	public void showReverseTrack(byte pos) {
		byte[] sendbuf = new byte[13];
		int ControlID = 0x00070600;
		byte ControlType = 0x01;
		sendbuf[0] = (byte) 0xff;
		sendbuf[1] = 0x55;
		sendbuf[2] = (byte) (4 + 6);
		sendbuf[3] = (byte) ((ControlID >> 24) & 0xff);
		sendbuf[4] = (byte) ((ControlID >> 16) & 0xff);
		sendbuf[5] = (byte) ((ControlID >> 8) & 0xff);
		sendbuf[6] = (byte) (ControlID & 0xff);
		sendbuf[7] = ControlType;
		sendbuf[8] = 00;
		sendbuf[9] = 00;
		sendbuf[10] = 00;
		sendbuf[11] = pos;
		sendbuf[12] = 00;

		mBackCarService.getFlyUIMsgClient().getHandleServiceMsg()
				.analysisData(sendbuf, 13, 0);
	}

	public void ChangeUIObjBackground(int cid, byte pos) {
		byte[] sendbuf = new byte[13];
		int ControlID = cid;
		byte ControlType = 0x01;
		sendbuf[0] = (byte) 0xff;
		sendbuf[1] = 0x55;
		sendbuf[2] = (byte) (4 + 6);
		sendbuf[3] = (byte) ((ControlID >> 24) & 0xff);
		sendbuf[4] = (byte) ((ControlID >> 16) & 0xff);
		sendbuf[5] = (byte) ((ControlID >> 8) & 0xff);
		sendbuf[6] = (byte) (ControlID & 0xff);
		sendbuf[7] = ControlType;
		sendbuf[8] = 00;
		sendbuf[9] = 00;
		sendbuf[10] = 00;
		sendbuf[11] = pos;
		sendbuf[12] = 00;

		mBackCarService.getFlyUIMsgClient().getHandleServiceMsg()
				.analysisData(sendbuf, 13, 0);
	}

	public void ShowUiView(int CtlID, boolean show) {
		byte[] sendbuf = new byte[11];
		int ControlID = CtlID;

		byte ControlType = (byte) 0xff;

		sendbuf[0] = (byte) 0xff;
		sendbuf[1] = 0x55;
		sendbuf[2] = (byte) (4 + 6);
		sendbuf[3] = (byte) ((ControlID >> 24) & 0xff);
		sendbuf[4] = (byte) ((ControlID >> 16) & 0xff);
		sendbuf[5] = (byte) ((ControlID >> 8) & 0xff);
		sendbuf[6] = (byte) (ControlID & 0xff);
		sendbuf[7] = ControlType;
		sendbuf[8] = 00;
		if (show)
			sendbuf[9] = 01;
		else
			sendbuf[9] = 00;
		sendbuf[10] = 00;
		mBackCarService.getFlyUIMsgClient().getHandleServiceMsg()
				.analysisData(sendbuf, 11, 0);

	}

	public void showText(int cid, String text) {
		if (text == null)
			return;
		int i = 0;
		byte[] buf = text.getBytes();

		int msize = 8 + buf.length;
		byte[] sendbuf = new byte[msize];
		int ControlID = cid;
		byte ControlType = (byte) 3;

		sendbuf[0] = (byte) 0xff;
		sendbuf[1] = 0x55;
		sendbuf[2] = (byte) (msize - 1);
		sendbuf[3] = (byte) ((ControlID >> 24) & 0xff);
		sendbuf[4] = (byte) ((ControlID >> 16) & 0xff);
		sendbuf[5] = (byte) ((ControlID >> 8) & 0xff);
		sendbuf[6] = (byte) (ControlID & 0xff);
		sendbuf[7] = ControlType;

		for (i = 0; i < buf.length; i++)
			sendbuf[8 + i] = buf[i];
		mBackCarService.getFlyUIMsgClient().getHandleServiceMsg()
				.analysisData(sendbuf, msize, 0);

	}

	public void setFlySeekBarProcess(int CtlID, int process) {
		byte[] sendbuf = new byte[13];
		int ControlID = CtlID;

		byte ControlType = (byte) 0xff;

		sendbuf[0] = (byte) 0xff;
		sendbuf[1] = 0x55;
		sendbuf[2] = (byte) (4 + 7);
		sendbuf[3] = (byte) ((ControlID >> 24) & 0xff);
		sendbuf[4] = (byte) ((ControlID >> 16) & 0xff);
		sendbuf[5] = (byte) ((ControlID >> 8) & 0xff);
		sendbuf[6] = (byte) (ControlID & 0xff);
		sendbuf[7] = ControlType;
		sendbuf[8] = (byte) 0x11;
		sendbuf[9] = (byte) ((process >> 24) & 0xff);
		sendbuf[10] = (byte) ((process >> 16) & 0xff);
		sendbuf[11] = (byte) ((process >> 8) & 0xff);
		sendbuf[12] = (byte) ((process >> 0) & 0xff);

		mBackCarService.getFlyUIMsgClient().getHandleServiceMsg()
				.analysisData(sendbuf, 13, 0);

	}

	public void SetRotateUiView(int CtlID, int level) {
		byte[] sendbuf = new byte[12];
		int ControlID = CtlID;

		byte ControlType = (byte) 0x02;

		sendbuf[0] = (byte) 0xff;
		sendbuf[1] = 0x55;
		sendbuf[2] = (byte) (4 + 6);
		sendbuf[3] = (byte) ((ControlID >> 24) & 0xff);
		sendbuf[4] = (byte) ((ControlID >> 16) & 0xff);
		sendbuf[5] = (byte) ((ControlID >> 8) & 0xff);
		sendbuf[6] = (byte) (ControlID & 0xff);
		sendbuf[7] = ControlType;
		sendbuf[8] = (byte) ((level >> 24) & 0xff);
		sendbuf[9] = (byte) ((level >> 16) & 0xff);
		sendbuf[10] = (byte) ((level >> 8) & 0xff);
		sendbuf[11] = (byte) ((level >> 0) & 0xff);
		mBackCarService.getFlyUIMsgClient().getHandleServiceMsg()
				.analysisData(sendbuf, 12, 0);

	}

	// FlyMultiEnumGaugeObj eg.
	public void ChangeMultiUIObjBackground(int cid, int index) {
		byte[] sendbuf = new byte[13];
		int ControlID = cid;
		byte ControlType = 0x02;
		sendbuf[0] = (byte) 0xff;
		sendbuf[1] = 0x55;
		sendbuf[2] = (byte) (4 + 6);
		sendbuf[3] = (byte) ((ControlID >> 24) & 0xff);
		sendbuf[4] = (byte) ((ControlID >> 16) & 0xff);
		sendbuf[5] = (byte) ((ControlID >> 8) & 0xff);
		sendbuf[6] = (byte) (ControlID & 0xff);
		sendbuf[7] = ControlType;
		sendbuf[8] = 00;
		sendbuf[9] = 00;
		sendbuf[10] = 00;
		sendbuf[11] = (byte) index;
		sendbuf[12] = 00;

		mBackCarService.getFlyUIMsgClient().getHandleServiceMsg()
				.analysisData(sendbuf, 13, 0);
	}

	// FlyMultiEnumGaugeObj eg.
	public void ReflashMultiUIObjBackground(int cid, int index) {
		byte[] sendbuf = new byte[13];
		int ControlID = cid;
		byte ControlType = 0x03;
		sendbuf[0] = (byte) 0xff;
		sendbuf[1] = 0x55;
		sendbuf[2] = (byte) (4 + 6);
		sendbuf[3] = (byte) ((ControlID >> 24) & 0xff);
		sendbuf[4] = (byte) ((ControlID >> 16) & 0xff);
		sendbuf[5] = (byte) ((ControlID >> 8) & 0xff);
		sendbuf[6] = (byte) (ControlID & 0xff);
		sendbuf[7] = ControlType;
		sendbuf[8] = 00;
		sendbuf[9] = 00;
		sendbuf[10] = 00;
		sendbuf[11] = (byte) index;
		sendbuf[12] = 00;

		mBackCarService.getFlyUIMsgClient().getHandleServiceMsg()
				.analysisData(sendbuf, 13, 0);
	}

	public void SetFlyButtonObjTouchMode(int cid, boolean touchMode) {

		byte[] sendbuf = new byte[13];

		byte touch = 0;
		int ControlID = cid;
		byte ControlType = (byte) 0xff;

		if (touchMode)
			touch = 0x01;
		sendbuf[0] = (byte) 0xff;
		sendbuf[1] = 0x55;
		sendbuf[2] = (byte) (4 + 6);
		sendbuf[3] = (byte) ((ControlID >> 24) & 0xff);
		sendbuf[4] = (byte) ((ControlID >> 16) & 0xff);
		sendbuf[5] = (byte) ((ControlID >> 8) & 0xff);
		sendbuf[6] = (byte) (ControlID & 0xff);
		sendbuf[7] = ControlType;
		sendbuf[8] = 0x10;
		sendbuf[9] = touch;
		sendbuf[10] = 00;
		sendbuf[11] = 00;
		sendbuf[12] = 00;

		mBackCarService.getFlyUIMsgClient().getHandleServiceMsg()
				.analysisData(sendbuf, 13, 0);

	}

	private byte pos = 5;
	private int Reversepos = 50;
    private int s_index = 0;
	public void Running() {

		restTrackRunnig();
		/*
		 * Random mRandom=new Random(); int ranNumber = mRandom.nextInt(2);
		 * 
		 * ChangeMultiUIObjBackground(0x00070021, ranNumber);
		 */
	testRadarRunning();

	}

	public void restTrackRunnig() {
		int lefeOrRight = -1;
		int tmp = 0;

		Reversepos++;
		if (Reversepos > 130) // 179
			Reversepos = 50;
		// Log.i(TAG,"Running " + pos + "Reversepos: " + Reversepos);
		if (mBackCarService.needToDealGuiji) {
			tmp = Reversepos - 90;
			lefeOrRight = tmp > 0 ? 1 : 0;
			tmp = tmp > 0 ? tmp : -tmp;
			showReverseTrack2((byte) tmp, lefeOrRight);
		} else
			showReverseTrack((byte) Reversepos);

	//	GLTrackViewToolHelper.getInstance(mBackCarService).setAngle(Reversepos);
	}

	public void testRadarRunning() {
		pos++;
		if (pos > 12) {
			pos = 0;
		}
		
		ShowUiView(0x00070421, true);
		ShowUiView(0x00070422, true);
		ShowUiView(0x00070424, true);
		ShowUiView(0x00070425, true);
		ShowUiView(0x00070427, true);
		ShowUiView(0x00070428, true);
	    ShowUiView(0x0007042a, true);
		ShowUiView(0x0007042b, true);

		showBigRadarView(0, pos);
		showBigRadarView(1, pos);
		showBigRadarView(2, pos);
		showBigRadarView(3, pos);
		showBigRadarView(4, pos);
		showBigRadarView(5, pos);
		showBigRadarView(6, pos);
		showBigRadarView(7, pos);
		showBigRadarView(8, pos);
		showBigRadarView(9, pos);
		showBigRadarView(10, pos);
		showBigRadarView(11, pos);
		showBigRadarView(12, pos);
		showBigRadarView(13, pos);
		showBigRadarView(14, pos);
		showBigRadarView(15, pos);
		showBigRadarView(16, pos);

		showSmallRadarView(0,pos); showSmallRadarView(1,pos);
		 showSmallRadarView(2,pos); showSmallRadarView(3,pos);
		 showSmallRadarView(4,pos); showSmallRadarView(5,pos);
		showSmallRadarView(6,pos); showSmallRadarView(7,pos);
		showSmallRadarView(8,pos);showSmallRadarView(9,pos);
		showSmallRadarView(10,pos);showSmallRadarView(11,pos);
		showSmallRadarView(12,pos);showSmallRadarView(13,pos);
		showSmallRadarView(14,pos);showSmallRadarView(15,pos);
		showSmallRadarView(16,pos);
		
//		ChangeMultiUIObjBackground(0x00070121, s_index);ChangeMultiUIObjBackground(0x00070122, s_index);
//		ChangeMultiUIObjBackground(0x00070124, s_index);ChangeMultiUIObjBackground(0x00070125, s_index);
//		ChangeMultiUIObjBackground(0x00070127, s_index);ChangeMultiUIObjBackground(0x00070128, s_index);
//		ChangeMultiUIObjBackground(0x0007012a, s_index);ChangeMultiUIObjBackground(0x0007012b, s_index);
//		ChangeMultiUIObjBackground(0x00070221, s_index);ChangeMultiUIObjBackground(0x00070222, s_index);
//		ChangeMultiUIObjBackground(0x00070224, s_index);ChangeMultiUIObjBackground(0x00070225, s_index);
//		ChangeMultiUIObjBackground(0x00070227, s_index);ChangeMultiUIObjBackground(0x00070228, s_index);
//		ChangeMultiUIObjBackground(0x0007022a, s_index);ChangeMultiUIObjBackground(0x0007022b, s_index);
//		
//		ChangeMultiUIObjBackground(0x00070021, s_index);ChangeMultiUIObjBackground(0x00070022, s_index);
//		ChangeMultiUIObjBackground(0x00070024, s_index);ChangeMultiUIObjBackground(0x00070025, s_index);
//		ChangeMultiUIObjBackground(0x00070027, s_index);ChangeMultiUIObjBackground(0x00070028, s_index);
//		ChangeMultiUIObjBackground(0x0007002a, s_index);ChangeMultiUIObjBackground(0x0007002b, s_index);
//		ChangeMultiUIObjBackground(0x00070321, s_index);ChangeMultiUIObjBackground(0x00070322, s_index);
//		ChangeMultiUIObjBackground(0x00070324, s_index);ChangeMultiUIObjBackground(0x00070325, s_index);
//		ChangeMultiUIObjBackground(0x00070327, s_index);ChangeMultiUIObjBackground(0x00070328, s_index);
//		ChangeMultiUIObjBackground(0x0007032a, s_index);ChangeMultiUIObjBackground(0x0007032b, s_index);
		if(s_index == 0 ){
			s_index = 1;
		}else {
			s_index = 0;

		}
	
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++ 913
	// louji+++++++++++++++++++++++++++++++//

	public void SetBackCar913Status(boolean status) {
		setBackCarState(status);
	}

	public void backcarstart913() {

		//showActivity(); //1117
		ShowBackCarView913();
		showActivity();
		//Io913_startVideo();
		// mBackCarService.DealBackCarMessages(MsgType.MSG_SENGDELAY,MsgType.MSG_CHECK_SIGNAL,
		// MsgType.SIGNALREADY_TIMEOUT);
		// mBackCarService.DealBackCarMessages(MsgType.MSG_SENGDELAY,MsgType.MSG_STOPNOTICE,
		// MsgType.STOPNOTICE_TIMEOUT);

	}

	public boolean isneedFinshActivity = false;

	public boolean isNeedFinsh() {
		// Log.d(TAG, "isNeedFinsh "+isneedFinshActivity);
		return isneedFinshActivity;

	}

	public void setNeedFinshActivity(boolean flag) {
		isneedFinshActivity = flag;
	}

	void exitStateReset() {
		setsignal(false);
		setBackCarState(false);
		BackCar913Service.BackCar913StartByUser = false;
		mBackCar913Service.setBackCar913StallDState(false);
		mBackCar913Service.DelayUnLock();
	}

	public void backcar913stop(String howToStop) {
		Log.d(TAG, "" + howToStop + " ---> backcar913stop");

		mBackCarService.SetBackCarGamma(MsgType.COLOR_ARM2);
		ToModule913CameraOn_Off((byte) 0x0);
		if (mBackCarService.mIsReversing)
			RemoveBackCarView();
		mBackCarService.getBaseModule().removeChoiceView();
		if (null != mBackCarService.getActivity()) {
			FlyaudioBackCarActivity.setMustDestroy(true);
			mBackCarService.getActivity().finish();
			mBackCarService.setActivityEmpty();
			// Log.i(TAG, "removeActivity - BackCarActivity finish ");
		}
		
		
		exitStateReset();

	}

	public void Io913_startVideo() {

		mBackCarService.getBaseModule().Io913_startVideo();
	}

	public void BACKCAR_START_913() // D or R 914
	{

		if (isNeedSetLanguage()) {
			// Log.i(TAG, "It is need change Language");
			mBackCarService.getFlyBackCarMainView().InitBackCarView();
			tellmoduleViewisInit((byte) 0);
		}
		backcarstart913();
		tellmoduleStallDStatus(true);
		if (!mBackCar913Service.getBackCar913StallDState()) // not D but R
		{	
			setBackCarState(true);
			TellModuleStart();
		}else
			setBackCarState(false);
		
	}

	public void BACKCAR_STOP_913() {
		setsignal(false);
		mBackCarService.DealBackCarMessages(MsgType.MSG_REMOVE,
				MsgType.MSG_SIGNAL_READY, 0);
		mBackCarService.DealBackCarMessages(MsgType.MSG_REMOVE,
				MsgType.MSG_SETGAMMA, 0);
		mBackCarService.SetBackCarGamma(MsgType.COLOR_ARM2);
		mBackCarService.BACKCAR_STOP();
		setBackCarState(false);
	}

	public void ShowBackCarView913() // 913 run this
	{

		Log.i(TAG, "ShowBackCarView913");
		// BaseModulePreStart();
		RemoveRadarView();
		mBackCarService.getBackCarView().setImgFormat(
				mBackCarService.BACKCAR_914);

		Log.i(TAG, "backcarMode = " + mBackCarService.getBackCarMode()
				+ "startbyuser " + BackCar913Service.BackCar913StartByUser);
		mBackCarService.getBackCarView().setBackcarMode(
				mBackCarService.BACKCAR_914);
		mBackCarService.getFlyBackCarMainView().addLayoutView(
				BackCarTag.ADDBTNVIEW);
		mBackCarService.getFlyBackCarMainView().ShowBackCarView();
		tellmoduleRefAngle((byte) 0x0);

	}

	public void m913ToOther() // only 913 -->R (cvb or 913)
	{
		// JudgeManchineNum();//wty
		// RemoveBackCarView();
		mBackCarService.getFlyBackCarMainView().InitBackCarView();
		tellmoduleViewisInit((byte) 0);
		showNoticeView(true);
		ShowBackCarView();
		setBackCarState(true);
		mBackCarService.DealBackCarMessages(MsgType.MSG_SENGDELAY,
				MsgType.MSG_CHECK_SIGNAL, MsgType.SIGNALREADY_TIMEOUT);
		mBackCarService.DealBackCarMessages(MsgType.MSG_SENGDELAY,
				MsgType.MSG_STOPNOTICE, MsgType.STOPNOTICE_TIMEOUT);
	}

	public void backcar_to913() // R(cvb or camera )back to 913
	{
		setBackCarLock(false);
		RemoveBackCarView();
		if (getCurPageID() == PageID.PAGE_NOVIDEO_TIP) {
			Log.d(TAG, "backcar_to913"
					+ Integer.toHexString(getCurPageID()).toUpperCase());
			RemoveRadarView();
		}
		mBackCarService.getBackCarView().setBackCarView(false);
		mBackCarService.DealBackCarMessages(MsgType.MSG_REMOVE,
				MsgType.MSG_CHECK_SIGNAL, 0);
		mBackCarService.DealBackCarMessages(MsgType.MSG_REMOVE,
				MsgType.MSG_STOPNOTICE, 0);
		setsignal(false);
		setBackCarState(false);
		mBackCar913Service.BackTo913View();
	}

	public void needFlyBackCarMainView() {
		mBackCarService.getBackCarView().setImgFormat(
				mBackCarService.BACKCAR_914);
		if (!mBackCarService.mIsReversing)
			mBackCarService.getFlyBackCarMainView().ShowBackCarView();
	}

	public void showSignByUser(boolean bsignOn) {

		// setsignal(bsignOn);

		int ipageid = 0;
		// Log.i(TAG, "showsignview bsignOn "+ bsignOn);
		mBackCarService.getBaseModule().removeChoiceView();
		mBackCarService.showFloatApp(false, 0);
		if (bsignOn) {
			ipageid = FlyaudioProperties.GetBackCarpageid();
			setCurPageID(ipageid);
			mBackCarService.DealBackCarMessages(MsgType.MSG_REMOVE,
					MsgType.MSG_SETGAMMA, 0);
			mBackCarService.DealBackCarMessages(MsgType.MSG_SENGDELAY,
					MsgType.MSG_SETGAMMA, MsgType.SETGAMMA_TIMEOUT);

			if (!mBackCarService.mIsReversing)
				ShowBackCarView913();
		} else {
			ipageid = FlyaudioProperties.GetBackCarpageid();
			mBackCarService.getFlyUIMsgClient().getHandleServiceMsg()
					.JumpPagetest(ipageid);
		}

	}

	public void MakeAndSendMessageButton(int way, int cid, Bundle dataTemp) {
		Message tmpMsg = Message.obtain();
		tmpMsg.what = way;
		tmpMsg.arg1 = cid;
		tmpMsg.setData(dataTemp);
		if (MsgType.COMMON == way || MsgType.FLYOBJ == way
				|| MsgType.SETLIGHT == way) {
			// if(BaseModule.getBaseModule() != null)
			BaseModule.getBaseModule().sendMessageToModule(tmpMsg);
			// else
			// myMsgHandler.sendMessage(tmpMsg);
		} else
			myMsgHandler.sendMessage(tmpMsg);

	}

	public void showFlyButtonBg(int cid, byte level) {
	//	 Log.d(TAG, "cid  "+cid+"  level  "+level);
		ChangeUIObjBackground(cid, level);
	}

	public void set913SourceRect(int value) {
		BackCar913Service.Mode913.angle = value;
		Log.d(TAG, "Module to set913SourceRect ");
		mBackCarService.getBackCarView().set913SourceRect(value);
	}

	public void front913Video(String focusTag) {
		mBackCarService.getBaseModule().front913Video(focusTag);
	}

	public void rear913Video(String focusTag) {
		mBackCarService.getBaseModule().rear913Video(focusTag);
	}

	public void showLightLevel(int value) {
		if (value < 0 || value > 11) {
			value = 5;
		}
		int displayValue = value - 5;
		ToModule913CameraSetLight((byte) value);

		if (displayValue > 0)
			showText(BackCarTag.LIGHTTEXT, "+" + displayValue);
		else
			showText(BackCarTag.LIGHTTEXT, "" + displayValue);

	}

	public void setLightLevel(int value) {

		if (value < 0 || value > 11) {
			value = 5;
		}
		ToModule913CameraSetLight((byte) value);
	}

	public void reBackLight() {
		String s = SystemProperties.get("fly.hdmisv.lightlevel", "5");
		int value = Integer.parseInt(s);
		setLightLevel(value);
	}

	public void click913QuitBackcar() {
		Log.d(TAG,
				"---------click913QuitBackcar---------BackCar913StartByUser = "
						+ mBackCar913Service.BackCar913StartByUser);
		if (mBackCar913Service.BackCar913StartByUser == true) {
			backcar913stop("clickbackkey");
		} else {
			mBackCar913Service.BACKCAR_STOP_913();
		}
	}

	public void backKeyCode_Deal() {
		mBackCarService.DealBackCarMessages(MsgType.MSG_REMOVE,
				MsgType.MSG_CHECK_SIGNAL, 0);
		if (getBackCarState()) // bakcarstatus return
			return;

		if (mBackCar913Service.is913StartByUser()
				|| mBackCar913Service.getBackCar913StallDState()) {
			switch (BackCar913Service.Mode913.page) {
			case BackCarTag.VIDEOEPAGE:
				mBackCarService
						.MakeAndSendMessageButton(BackCarTag.VIDEOTOCHOICE);
				break;
			case BackCarTag.CHIOCEPAGE:
				Log.d(TAG, "backKeyCode_Deal");
				mBackCar913Service.keyCodeBackExit();
				break;
			}
		}

	}

	public void setChoiceFRMode() {
		final int ret = mBackCarService.get913Camera_fr_mode();
		mBackCarService.getBaseModule().initFr_mode(ret);
	}

	/*
	 * 
	 * public void showChoiceView() { setCurPageID(PageID.PAGE_913CHOICE);
	 * initRadarView(PageID.PAGE_913CHOICE);
	 * mBackCarService.getFlyRadarView().ShowRadarView();
	 * setRadarViewState(true); }
	 * 
	 * public void removeChoiceView() {
	 * 
	 * mBackCarService.DealBackCarMessages(MsgType.MSG_REMOVE,MsgType.
	 * MSG_CHECK_SIGNAL, 0);
	 * mBackCarService.getFlyRadarView().RemoveRadarView();
	 * setRadarViewState(false); setCurPageID(0); }
	 * 
	 * public void chioce913Page(String focus, int direct) { removeChoiceView();
	 * 
	 * setChoiceFRMode(); mBackCarService
	 * .MakeAndSendMessageWithBundle(MsgType.COMMON,BackCarTag.RMLIGHTVIEW,
	 * null); showChoiceView(); diret913 = direct;
	 * mBackCarService.getBackCarView().setBackCarView(false); //
	 * showFlyButtonBg(CHOICE_BNT, (byte)diret913);
	 * FlyBaseListener.focus(focus); ToModule913CameraSetLight((byte)0xff);
	 * //save status BackCar913Service.Mode913.page = BackCarTag.CHIOCEPAGE;
	 * BackCar913Service.Mode913.focusTag = focus;
	 * BackCar913Service.Mode913.chioceDire= diret913; }
	 * 
	 * void setAuxLineHeight(int ev_Y){
	 * mBackCarService.DealBackCarMessages(MsgType
	 * .MSG_REMOVE,MsgType.MSG_STOPNOTICE, 0);
	 * mBackCarService.getFlyBackCarMainView().setAuxLineHeight(ev_Y);
	 * mBackCarService
	 * .DealBackCarMessages(MsgType.MSG_SENGDELAY,MsgType.MSG_STOPNOTICE,
	 * MsgType.STOPNOTICE_TIMEOUT); } void showSetAuxBotton(){ if(!AuxLine_show)
	 * //没有辅助线显示就不显示辅助按钮 return; boolean needShow =
	 * mBackCarService.getFlyBackCarMainView()
	 * .isUIViewDisable(BackCarTag.Notice_STR);
	 * mBackCarService.DealBackCarMessages
	 * (MsgType.MSG_REMOVE,MsgType.MSG_STOPNOTICE, 0); showNoticeView(
	 * !needShow);
	 * mBackCarService.DealBackCarMessages(MsgType.MSG_SENGDELAY,MsgType
	 * .MSG_STOPNOTICE, MsgType.STOPNOTICE_TIMEOUT);
	 * 
	 * } public boolean AuxLine_show = true; public void m_ShowAuxButton(byte
	 * data) { if(data == 0) AuxLine_show = false; else if(data == 1)
	 * AuxLine_show = true; showAuxButton(AuxLine_show); }
	 * 
	 * public void showAuxButton(boolean show){ if(!AuxLine_show) show = false;
	 * 
	 * mBackCarService.getFlyBackCarMainView()
	 * .UIParentViewShow(FlyUtil.HexToTag(BackCarTag.AUXLINE_UP),show, false);
	 * mBackCarService.getFlyBackCarMainView()
	 * .UIParentViewShow(FlyUtil.HexToTag(BackCarTag.AUXLINE_DOWN),show, false);
	 * 
	 * }
	 */

	static final int FOCUSTYPE = 0;
	static final int SHOWTYPE = 1;

	public int diret913 = 0;

	Handler myMsgHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			int progress = 0;
			Bundle data = msg.getData();
			if ((int) msg.what == MsgType.OBJFOCUS) {
				Log.d(TAG, "setfocus " + String.format("%08x", msg.arg1));

				FlyBaseListener.focusInt((int) msg.arg1);
			} else if ((int) msg.what == MsgType.OBJVISIBLE) {
				// Log.d(TAG, "setvisible "+ String.format("%08x", msg.arg1));
				mBackCarService.getFlyBackCarMainView().DealMainView(SHOWTYPE,
						(int) msg.arg1, false);

			} else if ((int) msg.what == MsgType.OBJSETBG) {
				// Log.d(TAG, "setbglevel ");
				int pos = (int) msg.arg1 & 0xF;
				showFlyButtonBg((int) (msg.arg1 >> 4), (byte) pos);
			} else if ((int) msg.what == MsgType.COMSEEKBAR) {
				switch ((int) msg.arg1) {
				case BackCarTag.FLYSEEKBAR1:
					progress = data.getInt(BackCarTag.INTKEY);
					Log.d(TAG, "FLYSEEKBAR1 " + progress);
					BaseModule.getBaseModule().SetVdoBrightness(progress);

					break;
				case BackCarTag.FLYSEEKBAR2:
					progress = data.getInt(BackCarTag.INTKEY);
					Log.d(TAG, "FLYSEEKBAR2 " + progress);
					BaseModule.getBaseModule().SetVdoHue(progress);

					break;
				case BackCarTag.FLYSEEKBAR3:
					progress = data.getInt(BackCarTag.INTKEY);
					// progress -=100;
					Log.d(TAG, "FLYSEEKBAR3 " + progress);
					BaseModule.getBaseModule().SetVdoSaturation(progress);
					break;

				case BackCarTag.FLYSEEKBAR4:
					Log.d(TAG, "FLYSEEKBAR4 " + progress);
					progress = data.getInt(BackCarTag.INTKEY);
					BaseModule.getBaseModule().SetScrHueLevel(progress);
			//		BaseModule.getBaseModule().SetScrBrightnessLevel(progress);
					// BaseModule.getBaseModule().SetScrHueLevel(progress);
					break;

				case BackCarTag.FLYSEEKBAR5:
					progress = data.getInt(BackCarTag.INTKEY);
					Log.d(TAG, "FLYSEEKBAR5 " + progress);
					BaseModule.getBaseModule().SetScrContrastLevel(progress);

					break;

				case BackCarTag.FLYSEEKBAR6:
					progress = data.getInt(BackCarTag.INTKEY);
					Log.d(TAG, "FLYSEEKBAR6 " + progress);
					BaseModule.getBaseModule().SetScrSaturationLevel(progress);

					break;

				case BackCarTag.FLYSEEKBAR7:
					progress = data.getInt(BackCarTag.INTKEY);
					Log.d(TAG, "FLYSEEKBAR7" + progress);
					BaseModule.getBaseModule().SetScrHueLevel(progress);

					break;
				}
			}

		}
	};

	public void BaseModuleExit() {

		Message tmpMsg = Message.obtain();
		tmpMsg.what = MsgType.COMMON;
		tmpMsg.arg1 = BackCarTag.MODULEEXIT;
		BaseModule.getBaseModule().sendMessageToModule(tmpMsg);

	}

	public void BaseModuleStart() {

		BaseModule.getBaseModule().removeMessageToModule(MsgType.COMMON, 0);
		Message tmpMsg = Message.obtain();
		tmpMsg.what = MsgType.COMMON;
		tmpMsg.arg1 = BackCarTag.MODULEENTER;
		BaseModule.getBaseModule().sendMessageToModule(tmpMsg);

	}

	public void BaseModulePreStart() {

		Message tmpMsg = Message.obtain();
		tmpMsg.what = MsgType.COMMON;
		tmpMsg.arg1 = BackCarTag.MODULEPREENTER;
		BaseModule.getBaseModule().sendMessageToModule(tmpMsg);

	}

	public void getBaseModulePreStart() {
		// if(BaseModule.getBaseModule() != null){
		BaseModule.getBaseModule().BaseModulepreStart();

		// }
	}

	public void getBaseModulePreStop() {
		BaseModule.getBaseModule().BaseModulepreStop();

	}

	public void SendCommonMsgToBaseModule(int msg, Bundle mbd) {

		Message tmpMsg = Message.obtain();
		tmpMsg.what = MsgType.COMMON;
		tmpMsg.arg1 = msg;
		tmpMsg.setData(mbd);
		BaseModule.getBaseModule().sendMessageToModule(tmpMsg);

	}

	public void FromModuleMsgCtrAuxLine(byte data) {
		BaseModule.getBaseModule().FromModuleMsgCtrAuxLine(data);

	}

	public void FromModuleMsgCtrPhoneCall(byte data) {
		BaseModule.getBaseModule().FromModuleMsgCtrPhoneCall(data);

	}

	public void DealWithTrackAngle(int angle) {
		Bundle data = new Bundle();
		data.putInt(BackCarTag.INTKEY, angle);
		BaseModule.getBaseModule().DealWithTrackAngle(data);
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	public void SetTestBtnLayout(String tag, int evx, int evy) {
		mBackCarService.getFlyBackCarMainView().SetTestBtnLayout(tag, evx, evy);

	}

//	
//case BackCarTag.FLYSEEKBAR1:
//	progress = data.getInt(BackCarTag.INTKEY);
//
//	BaseModule.getBaseModule().SetVdoBrightness(progress);
//
//	break;
//case BackCarTag.FLYSEEKBAR2:
//	progress = data.getInt(BackCarTag.INTKEY);
//	Log.d(TAG, "vdohue " + progress);
//	BaseModule.getBaseModule().SetVdoHue(progress);
//
//	break;
//case BackCarTag.FLYSEEKBAR3:
//	progress = data.getInt(BackCarTag.INTKEY);
//	// progress -=100;
//	Log.d(TAG, "vdosta " + progress);
//	BaseModule.getBaseModule().SetVdoSaturation(progress);
//	break;
//
//case BackCarTag.FLYSEEKBAR4:
//	progress = data.getInt(BackCarTag.INTKEY);
//	BaseModule.getBaseModule().SetScrBrightnessLevel(progress);
//	// BaseModule.getBaseModule().SetScrHueLevel(progress);
//	break;
//
//case BackCarTag.FLYSEEKBAR5:
//	progress = data.getInt(BackCarTag.INTKEY);
//
//	BaseModule.getBaseModule().SetScrContrastLevel(progress);
//
//	break;
//
//case BackCarTag.FLYSEEKBAR6:
//	progress = data.getInt(BackCarTag.INTKEY);
//
//	BaseModule.getBaseModule().SetScrSaturationLevel(progress);
//
//	break;
//
//case BackCarTag.FLYSEEKBAR7:
//	progress = data.getInt(BackCarTag.INTKEY);
//
//	BaseModule.getBaseModule().SetScrHueLevel(progress);
//
//	break;
//}
}
