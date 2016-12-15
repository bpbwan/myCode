package com.flyaudio.keyevent;

import java.io.InputStream;

import android.os.SystemProperties;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.MotionEvent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View.OnKeyListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.util.Log;

import com.flyaudio.object.*;

import java.util.ArrayList;

import android.view.View.OnFocusChangeListener;

import com.flyaudio.backcar.BackCarService;
import com.flyaudio.backcar.MsgType;

import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.Canvas;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.flyaudio.backcar913.BackCarTag;
import com.flyaudio.backcar913.FlyBaseListener;
import com.flyaudio.keyevent.BaseKeyListener.KeyListenerCallback;
import com.flyaudio.backcar.modules.AuDiA4Module;
import com.flyaudio.backcar.modules.BaseModule;
import com.flyaudio.globaldefine.PageID;
import com.flyaudio.backcar.PluginProxyContext;
import com.flyaudio.globaldefine.ForeGround;
import com.flyaudio.backcar.util.*;

public class AuDiA4KeyListener extends BaseKeyListener {
	static AuDiA4KeyListener mAuDiA4KeyListener;

	public static AuDiA4KeyListener getInstance(KeyListenerCallback cb) {
		if (mAuDiA4KeyListener == null) {
			mAuDiA4KeyListener = new AuDiA4KeyListener(cb);

		}
		return mAuDiA4KeyListener;
	}

	public AuDiA4KeyListener(KeyListenerCallback cb) {
		super(cb);
	}

	public void refrence(String viewtag) {
		callback.refrenceMethod(viewtag);

	}

	public void dealButtonPage(String viewtag, String parentTag, int bntpage) {
		currentPage = bntpage;
		Log.d("DDD", "dealButtonPage  " + viewtag);
		if (parentTag.equals(BackCarTag.PAGE_913A4SETTING))
			A4SettingPageSelect(viewtag, currentPage);
		

	}

	void A4SettingPageSelect(String viewtag, int page) {
		/*	if (viewtag.equals(FlyUtil.HexToTag(BackCarTag.A4_AUDI_ClOSE_SYS_CID))
				|| viewtag.equals(FlyUtil
						.HexToTag(BackCarTag.A4_AUDI_ScreenSize_full))
				|| viewtag.equals(FlyUtil
						.HexToTag(BackCarTag.Audi_SELECTBUTTON))
				|| viewtag.equals(FlyUtil
						.HexToTag(BackCarTag.A4_AUDI_FRONT_SOUND_CID))
				|| viewtag.equals(FlyUtil
						.HexToTag(BackCarTag.A4_AUDI_REAR_SOUND_CID))) // backButton
		{

			AuDiA4Module.getInstance(null).hideDanKuangSetting();
		}
		*/
		// Log.d("DDD", "A4SettingPageSelect  "+viewtag);
		if (viewtag
				.equals(FlyUtil.HexToTag(BackCarTag.A4_AUDI_FRONT_SOUND_Low)))
			AuDiA4Module.getInstance(null).tellModuleFSoundLevel(1);
		else if (viewtag.equals(FlyUtil
				.HexToTag(BackCarTag.A4_AUDI_FRONT_SOUND_Mid)))
			AuDiA4Module.getInstance(null).tellModuleFSoundLevel(4);
		else if (viewtag.equals(FlyUtil
				.HexToTag(BackCarTag.A4_AUDI_FRONT_SOUND_High)))
			AuDiA4Module.getInstance(null).tellModuleFSoundLevel(9);
		else if (viewtag.equals(FlyUtil
				.HexToTag(BackCarTag.A4_AUDI_REAR_SOUND_Low)))
			AuDiA4Module.getInstance(null).tellModuleRSoundLevel(1);
		else if (viewtag.equals(FlyUtil
				.HexToTag(BackCarTag.A4_AUDI_REAR_SOUND_Mid)))
			AuDiA4Module.getInstance(null).tellModuleRSoundLevel(4);
		else if (viewtag.equals(FlyUtil
				.HexToTag(BackCarTag.A4_AUDI_REAR_SOUND_High)))
			AuDiA4Module.getInstance(null).tellModuleRSoundLevel(9);

	}




	public void setLightPageDealKey(int keyCode, String viewtag) {
		switch (keyCode) {

		case KeyEvent.KEYCODE_DPAD_RIGHT:
		case KeyEvent.KEYCODE_DPAD_LEFT:
			BackCarService.mBackCarService.MakeAndSendMessageWithBundle(
					MsgType.COMMON, BackCarTag.RMLIGHTVIEW, null);
			FlyBaseListener.focus(FlyUtil.HexToTag(BackCarTag.SETLIGHT_CID));
			break;

		}

	}

	boolean commopageKeyEvent(View v, int keycode) {
		if(BaseModule.getBaseModule().DEBUG)
		Log.d("DDD", "commopageKeyEvent " + keycode);
		boolean ret = false;
		switch (keycode) {
		case FlyKeyCode.FLYKEY_LEFT:
			reFocus(v, View.FOCUS_LEFT);
			ret = true;

			break;
		case KeyEvent.KEYCODE_DPAD_LEFT:

			AuDiA4Module.getInstance(null).CarLeftKeyDeal();
			ret = true;
			break;
		case FlyKeyCode.FLYKEY_RIGHT:

			reFocus(v, View.FOCUS_RIGHT);
			ret = true;

			break;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			BackCarService.mBackCarService.MakeAndSendMessageWithBundle(
					MsgType.FLYOBJ, BackCarTag.A4_AUDI_SET_CID, null);
			ret = true;
			break;
		case KeyEvent.KEYCODE_DPAD_UP: // 19
			if (!BackCarService.mBackCarService.getReversing())
				BackCarService.mBackCarService.getFlyFloatRadarView()
						.LayoutRemoveViewWithPageID(PageID.PAGE_913A4SETTING);
			break;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			ret = true;
			break;
		case KeyEvent.KEYCODE_BACK:
			BackCarService.mBackCarService.MakeAndSendMessageWithBundle(
					MsgType.COMMON, BackCarTag.BACKKEYEVENT, null);

			break;

		default:
			break;
		}
		return ret;
	}

	boolean DVRPageKeyEvent(View v, int keycode) {
		if(BaseModule.getBaseModule().DEBUG)
		Log.d("DDD", "DVRPageKeyEvent " + keycode);
		boolean ret = false;
		switch (keycode) {
		case FlyKeyCode.FLYKEY_LEFT:
			reFocus(v, View.FOCUS_LEFT);
			break;
		case KeyEvent.KEYCODE_DPAD_LEFT: // 21
			BackCarService.mBackCarService.getFlyFloatRadarView()
					.LayoutRemoveViewWithPageID(PageID.PAGE_913A4SETTING);
			ret = true;
			break;
		case FlyKeyCode.FLYKEY_RIGHT:

			reFocus(v, View.FOCUS_RIGHT);
			break;
		case KeyEvent.KEYCODE_DPAD_RIGHT: // 22
			ret = true;
			break;
		case KeyEvent.KEYCODE_DPAD_UP: // 19

			break;
		case KeyEvent.KEYCODE_DPAD_DOWN: // 20
			BackCarService.mBackCarService.MakeAndSendMessageWithBundle(
					MsgType.FLYOBJ, BackCarTag.A4_AUDI_SET_CID, null);
			ret = true;
			break;
		default:
			break;
		}

		return ret;

	}

	boolean NaviPageKeyEvent(View v, int keycode) {
		if(BaseModule.getBaseModule().DEBUG)
	Log.d("DDD", "NaviPageKeyEvent4 " + keycode);
		boolean ret = false;
		switch (keycode) {
		case FlyKeyCode.FLYKEY_LEFT:
			reFocus(v, View.FOCUS_LEFT);
			break;
		case KeyEvent.KEYCODE_DPAD_LEFT: // 21

			break;
		case FlyKeyCode.FLYKEY_RIGHT:

			reFocus(v, View.FOCUS_RIGHT);
			break;
		case KeyEvent.KEYCODE_DPAD_RIGHT: // 22

			break;
		case KeyEvent.KEYCODE_DPAD_UP: // 19
			BackCarService.mBackCarService.getFlyFloatRadarView()
					.LayoutRemoveViewWithPageID(PageID.PAGE_913A4SETTING);
			break;
		case KeyEvent.KEYCODE_DPAD_DOWN: // 20
			BackCarService.mBackCarService.MakeAndSendMessageWithBundle(
					MsgType.FLYOBJ, BackCarTag.A4_AUDI_SET_CID, null);
			ret = true;
			break;
		case KeyEvent.KEYCODE_BACK:
			BackCarService.mBackCarService.MakeAndSendMessageWithBundle(
					MsgType.COMMON, BackCarTag.BACKKEYEVENT, null);
			break;

		default:
			break;
		}

		return ret;

	}

	public boolean KEYEVENT(View v, int keycode) {
		boolean ret = false;

		if (getCurrentForeground().equals(ForeGround.BackCarPage))
			ret = commopageKeyEvent(v, keycode);
		else if (getCurrentForeground().equals(ForeGround.FlyDVRPage))
			ret = DVRPageKeyEvent(v, keycode);
		else
			ret = NaviPageKeyEvent(v, keycode);

		return ret;
	}

	public OnKeyListener CVBPage = new OnKeyListener() {
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			// TODO Auto-generated method stub

			final int action = event.getAction();
			final boolean handleKeyEvent = (action != KeyEvent.ACTION_UP);
			if (!handleKeyEvent)
				return false;
			boolean ret = false;
			Log.d("DDD", "CVBPage4  " + keyCode);
			switch (keyCode) {
			case FlyKeyCode.FLYKEY_LEFT:
				reFocus(v, View.FOCUS_LEFT);
				break;
			case KeyEvent.KEYCODE_DPAD_LEFT:
				BackCarService.mBackCarService.getFlyBackCarMainView()
						.LayoutRemoveViewWithPageID(PageID.PAGE_913A4SETTING);
				FlyBaseListener.focus(FlyUtil
						.HexToTag(BackCarTag.A4_AUDI_NOT_913_SET_CID));

				ret = true;
				break;
			case FlyKeyCode.FLYKEY_RIGHT:

				reFocus(v, View.FOCUS_RIGHT);
				break;
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				BackCarService.mBackCarService.MakeAndSendMessageWithBundle(
						MsgType.FLYOBJ, BackCarTag.A4_AUDI_NOT_913_SET_CID,
						null);
				break;
			case KeyEvent.KEYCODE_DPAD_UP: // 19

				break;
			case KeyEvent.KEYCODE_DPAD_DOWN:
				break;
			case KeyEvent.KEYCODE_BACK:

				break;

			default:
				break;
			}
			return ret;
		}
	};

	boolean DvrPageKeyevent(int keyCode) {
		switch (keyCode) {
		case 21:
			BackCarService.mBackCarService.getFlyFloatRadarView()
					.LayoutRemoveViewWithPageID(PageID.PAGE_913A4SETTING);
			break;
		case 22:

			BackCarService.mBackCarService.MakeAndSendMessageWithBundle(
					MsgType.FLYOBJ, BackCarTag.A4_AUDI_SET_CID, null);
			break;

		case 66:

			break;

		default:
			BackCarService.mBackCarService.MakeAndSendMessageWithBundle(
					MsgType.FLYOBJ, BackCarTag.A4_AUDI_ClOSE_SYS_CID, null);

		}
		return true;

	}

	public OnKeyListener pageRadar = new OnKeyListener() {
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			// TODO Auto-generated method stub

			final int action = event.getAction();
			final boolean handleKeyEvent = (action != KeyEvent.ACTION_UP);
			if (!handleKeyEvent)
				return false;
			boolean ret = false;
			if (!BackCarService.mBackCarService.getReversing()) {
				if(BaseModule.getBaseModule().DEBUG)
				Log.d("DDD", "pageRadarKEYEVENT4 " + keyCode);

				if (getCurrentForeground().equals(ForeGround.FlyDVRPage))
					ret = DvrPageKeyevent(keyCode);
				else {
					switch (keyCode) {
					case 19:
						BackCarService.mBackCarService.getFlyFloatRadarView()
								.LayoutRemoveViewWithPageID(
										PageID.PAGE_913A4SETTING);
						break;
					case 20:

						BackCarService.mBackCarService
								.MakeAndSendMessageWithBundle(MsgType.FLYOBJ,
										BackCarTag.A4_AUDI_SET_CID, null);
						break;

					case 66:

						break;

					default:
						BackCarService.mBackCarService
								.MakeAndSendMessageWithBundle(MsgType.FLYOBJ,
										BackCarTag.A4_AUDI_ClOSE_SYS_CID, null);

					}
				}

			}
			return ret;
		}
	};

}
