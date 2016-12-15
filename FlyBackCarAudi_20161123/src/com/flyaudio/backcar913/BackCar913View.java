package com.flyaudio.backcar913;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.graphics.ImageFormat;

import com.flyaudio.backcar.BackCarService;
import com.flyaudio.backcar.FlyBackCarMainView;
import com.flyaudio.backcar.MsgType;
import com.flyaudio.globaldefine.FlyaudioIntent;
import com.flyaudio.globaldefine.FlyaudioProperties;
import com.autochips.backcar.BackCar;
import android.app.Presentation; //for backoutvideo
import android.hardware.display.DisplayManager;
import android.view.Window;
import android.util.DisplayMetrics;
import java.io.DataInputStream;
import java.io.InputStream;

import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.os.Bundle;
import android.os.SystemProperties;
import com.autochips.dvr.DVR;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.MotionEvent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup.LayoutParams;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.widget.ImageView;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;

import com.flyaudio.backcar.FlyBackCarMainView;
import com.flyaudio.backcar.util.*;

public class BackCar913View {
	// public FlyBackCarMainView mFlyBackCarMainView = null;
	// public BackCar913View(FlyBackCarMainView mBackCarView){
	// mFlyBackCarMainView = mBackCarView;
	// }
	private static final String TAG = "BackCar913View";

	public final static int LAYOUT_913_NULL = 0;
	public final static int LAYOUT_913_CHOICE = 1;
	public final static int LAYOUT_913_VIDEO = 2;

	private final static int CAMERA_913_ANGLE_150 = 1;
	private final static int CAMERA_913_ANGLE_180 = 2;
	public static int g913CameraMode = 0;
	public static int FRONT_150_913 = 1;
	private static int FRONT_180_913 = 2;
	public static int REAR_150_913 = 3;
	private static int REAR_180_913 = 4;

	public BackCar913Service mBackCar913Service = null;

	public BackCar913View(BackCar913Service Service) {
		mBackCar913Service = Service;
	}

	public FlyBackCarMainView GetMainView() {
		return mBackCar913Service.mBackCarService.mFlyBackCarMainView;
	}

	public void changeFront(int fr, View v) {
		if (v == null)
			return;
		if (fr == 0)
			v.setBackgroundResource(btn_150[1]);
		else if (fr == 1)
			v.setBackgroundResource(btn_180[1]);

	}

	public void changeRear(int fr, View v) {
		if (v == null)
			return;
		if (fr == 0)
			v.setBackgroundResource(btn_150[0]);
		else if (fr == 1)
			v.setBackgroundResource(btn_180[0]);
	}

	public void change_fr_modeUi(View f, View r) {
		f.setVisibility(View.VISIBLE);
		r.setVisibility(View.VISIBLE);
		f.setBackgroundResource(fr_choice_ui[0]);
		r.setBackgroundResource(fr_choice_ui[1]);

	}

	public void change_f_modeUi(View f, View r) {
		f.setVisibility(View.VISIBLE);
		r.setVisibility(View.GONE);
		f.setBackgroundResource(fr_choice_ui[2]);
	}

	public void change_r_modeUi(View f, View r) {
		f.setVisibility(View.GONE);
		r.setVisibility(View.VISIBLE);
		r.setBackgroundResource(fr_choice_ui[3]);

	}

	public void change_audiA4_fr_modeui() {
		mBackCar913Service.set913FlyTextObj(BackCarTag.Audi_SELECT_TEXT,
				fr_mode_text[2]);

	}

	public void change_audiA4_f_modeui() {
		mBackCar913Service.set913FlyTextObj(BackCarTag.Audi_SELECT_TEXT,
				fr_mode_text[0]);

	}

	public void change_audiA4_r_modeui() {
		mBackCar913Service.set913FlyTextObj(BackCarTag.Audi_SELECT_TEXT,
				fr_mode_text[1]);

	}

	public void change_audiA4_f_r_modeui() {
		mBackCar913Service.set913FlyTextObj(BackCarTag.Audi_SELECT_TEXT,
				fr_mode_text[3]);

	}

	public void change_audiA4_fr_modeUi(View f, View r, boolean first) {
		int lid = mBackCar913Service.mBackCarService.proxyContext
				.getId("btn_150f");
		int rid = mBackCar913Service.mBackCarService.proxyContext
				.getId("btn_150r");
		View v = FlyBaseListener.mfindViewWithTag(FlyUtil
				.HexToTag(BackCarTag.A4_F_180VIDEO_CID));
		setTouchAble(v, BackCarTag.A4_F_180VIDEO_CID, true);
		v = FlyBaseListener.mfindViewWithTag(FlyUtil
				.HexToTag(BackCarTag.A4_R_150VIDEO_CID));
		setTouchAble(v, BackCarTag.A4_R_150VIDEO_CID, true);
		setNextFocusLeftRight(v, lid, -1);
		v = FlyBaseListener.mfindViewWithTag(FlyUtil
				.HexToTag(BackCarTag.A4_R_180VIDEO_CID));
		setTouchAble(v, BackCarTag.A4_R_180VIDEO_CID, true);

		v = FlyBaseListener.mfindViewWithTag(FlyUtil
				.HexToTag(BackCarTag.A4_F_150VIDEO_CID));
		setTouchAble(v, BackCarTag.A4_F_150VIDEO_CID, true);
		setNextFocusLeftRight(v, -1, rid);

		if (first) {
			FlyBaseListener.setObjLevel(
					FlyUtil.HexToTag(BackCarTag.A4_F_150VIDEO_CID), 0);
			FlyBaseListener.setObjLevel(
					FlyUtil.HexToTag(BackCarTag.A4_F_180VIDEO_CID), 0);
			FlyBaseListener.setObjLevel(
					FlyUtil.HexToTag(BackCarTag.A4_R_150VIDEO_CID), 0);
			FlyBaseListener.setObjLevel(
					FlyUtil.HexToTag(BackCarTag.A4_R_180VIDEO_CID), 0);
		}
		change_audiA4_fr_modeui();
	}

	public void change_audiA4_f_modeUi(View f, View r, boolean first) {
		View v = FlyBaseListener.mfindViewWithTag(FlyUtil
				.HexToTag(BackCarTag.A4_F_180VIDEO_CID));
		setTouchAble(v, BackCarTag.A4_F_180VIDEO_CID, true);
		v = FlyBaseListener.mfindViewWithTag(FlyUtil
				.HexToTag(BackCarTag.A4_R_150VIDEO_CID));
		setTouchAble(v, BackCarTag.A4_R_150VIDEO_CID, false);
		v = FlyBaseListener.mfindViewWithTag(FlyUtil
				.HexToTag(BackCarTag.A4_R_180VIDEO_CID));
		setTouchAble(v, BackCarTag.A4_R_180VIDEO_CID, false);

		v = FlyBaseListener.mfindViewWithTag(FlyUtil
				.HexToTag(BackCarTag.A4_F_150VIDEO_CID));
		setTouchAble(v, BackCarTag.A4_F_150VIDEO_CID, true);
		int rid = mBackCar913Service.mBackCarService.proxyContext
				.getId("btn_150f");
		setNextFocusLeftRight(v, -1, rid);

		if (first) {
			FlyBaseListener.setObjLevel(
					FlyUtil.HexToTag(BackCarTag.A4_F_150VIDEO_CID), 0);
			FlyBaseListener.setObjLevel(
					FlyUtil.HexToTag(BackCarTag.A4_F_180VIDEO_CID), 0);
			FlyBaseListener.setObjLevel(
					FlyUtil.HexToTag(BackCarTag.A4_R_150VIDEO_CID), 2);
			FlyBaseListener.setObjLevel(
					FlyUtil.HexToTag(BackCarTag.A4_R_180VIDEO_CID), 2);
		}
		change_audiA4_f_modeui();
	}

	public void change_audiA4_r_modeUi(View f, View r, boolean first) {
		View v = FlyBaseListener.mfindViewWithTag(FlyUtil
				.HexToTag(BackCarTag.A4_F_150VIDEO_CID));
		setTouchAble(v, BackCarTag.A4_F_150VIDEO_CID, false);
		v = FlyBaseListener.mfindViewWithTag(FlyUtil
				.HexToTag(BackCarTag.A4_F_180VIDEO_CID));
		setTouchAble(v, BackCarTag.A4_F_180VIDEO_CID, false);
		v = FlyBaseListener.mfindViewWithTag(FlyUtil
				.HexToTag(BackCarTag.A4_R_150VIDEO_CID));
		setTouchAble(v, BackCarTag.A4_R_150VIDEO_CID, true);
		int lid = mBackCar913Service.mBackCarService.proxyContext
				.getId("btn_150r");

		setNextFocusLeftRight(v, lid, -1);
		
		v = FlyBaseListener.mfindViewWithTag(FlyUtil
				.HexToTag(BackCarTag.A4_R_180VIDEO_CID));
		setTouchAble(v, BackCarTag.A4_R_180VIDEO_CID, true);


		setNextFocusLeftRight(v, lid, -1);
		if (first) {
			FlyBaseListener.setObjLevel(
					FlyUtil.HexToTag(BackCarTag.A4_F_150VIDEO_CID), 2);
			FlyBaseListener.setObjLevel(
					FlyUtil.HexToTag(BackCarTag.A4_F_180VIDEO_CID), 2);
			FlyBaseListener.setObjLevel(
					FlyUtil.HexToTag(BackCarTag.A4_R_150VIDEO_CID), 0);
			FlyBaseListener.setObjLevel(
					FlyUtil.HexToTag(BackCarTag.A4_R_180VIDEO_CID), 0);
		}
		change_audiA4_r_modeui();
	}

	public void change_audiA4_f_r_modeUi(View f, View r, boolean first) {
		View v = FlyBaseListener.mfindViewWithTag(FlyUtil
				.HexToTag(BackCarTag.A4_F_180VIDEO_CID));
		setTouchAble(v, BackCarTag.A4_F_180VIDEO_CID, true);
		v = FlyBaseListener.mfindViewWithTag(FlyUtil
				.HexToTag(BackCarTag.A4_R_150VIDEO_CID));
		setTouchAble(v, BackCarTag.A4_R_150VIDEO_CID, false);
		v = FlyBaseListener.mfindViewWithTag(FlyUtil
				.HexToTag(BackCarTag.A4_R_180VIDEO_CID));
		setTouchAble(v, BackCarTag.A4_R_180VIDEO_CID, false);

		v = FlyBaseListener.mfindViewWithTag(FlyUtil
				.HexToTag(BackCarTag.A4_F_150VIDEO_CID));
		setTouchAble(v, BackCarTag.A4_F_150VIDEO_CID, true);
		int rid = mBackCar913Service.mBackCarService.proxyContext
				.getId("btn_150f");
		setNextFocusLeftRight(v, -1, rid);

		if (first) {
			FlyBaseListener.setObjLevel(
					FlyUtil.HexToTag(BackCarTag.A4_F_150VIDEO_CID), 0);
			FlyBaseListener.setObjLevel(
					FlyUtil.HexToTag(BackCarTag.A4_F_180VIDEO_CID), 0);
			FlyBaseListener.setObjLevel(
					FlyUtil.HexToTag(BackCarTag.A4_R_150VIDEO_CID), 2);
			FlyBaseListener.setObjLevel(
					FlyUtil.HexToTag(BackCarTag.A4_R_180VIDEO_CID), 2);
		}
		change_audiA4_f_r_modeui();
	}

	public void change_audiA4_sound_levelUI(int cid, int vuale) {
		if (vuale < 3)
			mBackCar913Service
					.set913FlyTextObj(cid, A4_sound_level_text[vuale]);
	}

	public void change_audiA4_title_levelUI(int cid, int vuale) {
		if (vuale < 4)
			mBackCar913Service.set913FlyTextObj(cid, A4_title_text[vuale]);
	}

	// bigradar 0 small radar 1 change
	public void ChangeBigAndSmallRadarTextUI(int cid, int vaule) {
		if (vaule < 2)
			mBackCar913Service
					.set913FlyTextObj(cid, A4_changeRadar_text[vaule]);

	}

	public void setTouchAble(View v, int cid, boolean flag) {
		if (v != null) {
			v.setFocusable(flag);
			v.setFocusableInTouchMode(flag);
			mBackCar913Service.mBackCarModule.SetFlyButtonObjTouchMode(cid,
					flag);
		}
	}

	public void setNextFocusLeftRight(View v, int lid, int rid) {
		if (v == null)
			return;
		if (lid > 0)
			v.setNextFocusLeftId(lid);
		if (rid > 0)
			v.setNextFocusRightId(rid);
	}

	private int btn_150[] = new int[2];
	private int btn_180[] = new int[2];
	private int fr_choice_ui[] = new int[4]; // (fr_mode use 0 | 1) (only f_mode
												// or f_r_mode use 2) ( only
												// r_mode 3)
	private String fr_mode_text[] = new String[4]; // 0 f , 1 r , 2 fr ,3 f_r
	private String A4_sound_level_text[] = new String[3];
	private String A4_title_text[] = new String[4];
	private String A4_changeRadar_text[] = new String[2];

	public void init913CarView2() {
		btn_150[0] = mBackCar913Service.mBackCarService.proxyContext
				.getRdrawable("rear_150_button");
		btn_150[1] = mBackCar913Service.mBackCarService.proxyContext
				.getRdrawable("front_150_button");
		btn_180[0] = mBackCar913Service.mBackCarService.proxyContext
				.getRdrawable("rear_180_button");
		btn_180[1] = mBackCar913Service.mBackCarService.proxyContext
				.getRdrawable("front_180_button");

		fr_choice_ui[0] = mBackCar913Service.mBackCarService.proxyContext
				.getRdrawable("choice_f_btn");
		fr_choice_ui[1] = mBackCar913Service.mBackCarService.proxyContext
				.getRdrawable("choice_r_btn");
		fr_choice_ui[2] = mBackCar913Service.mBackCarService.proxyContext
				.getRdrawable("f_mode_choice");
		fr_choice_ui[3] = mBackCar913Service.mBackCarService.proxyContext
				.getRdrawable("r_mode_choice");

		fr_mode_text[0] = mBackCar913Service.mBackCarService.proxyContext
				.getString("fr_mode_f", "");
		fr_mode_text[1] = mBackCar913Service.mBackCarService.proxyContext
				.getString("fr_mode_r", "");
		fr_mode_text[2] = mBackCar913Service.mBackCarService.proxyContext
				.getString("fr_mode_fr", "");
		fr_mode_text[3] = mBackCar913Service.mBackCarService.proxyContext
				.getString("fr_mode_f_r", "");

		A4_sound_level_text[0] = mBackCar913Service.mBackCarService.proxyContext
				.getString("low", "");
		A4_sound_level_text[1] = mBackCar913Service.mBackCarService.proxyContext
				.getString("mid", "");
		A4_sound_level_text[2] = mBackCar913Service.mBackCarService.proxyContext
				.getString("high", "");

		A4_title_text[0] = mBackCar913Service.mBackCarService.proxyContext
				.getString("title180f", "");
		A4_title_text[1] = mBackCar913Service.mBackCarService.proxyContext
				.getString("title150f", "");
		A4_title_text[2] = mBackCar913Service.mBackCarService.proxyContext
				.getString("title150r", "");
		A4_title_text[3] = mBackCar913Service.mBackCarService.proxyContext
				.getString("title180r", "");

		A4_changeRadar_text[0] = mBackCar913Service.mBackCarService.proxyContext
				.getString("fullscreen", "");

		A4_changeRadar_text[1] = mBackCar913Service.mBackCarService.proxyContext
				.getString("halfscreen", "");
	}
}
