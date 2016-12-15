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
import com.flyaudio.globaldefine.PageID;
import com.flyaudio.backcar.PluginProxyContext;
import com.flyaudio.globaldefine.ForeGround;
import com.flyaudio.backcar.util.*;
import com.flyaudio.backcar.modules.*;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import com.flyaudio.backcar913.BackCar913Service;

public class BenzKeyListener extends BaseKeyListener {
	static BenzKeyListener mAuDiA4KeyListener;

	public static BenzKeyListener getInstance(KeyListenerCallback cb) {
		if (mAuDiA4KeyListener == null) {
			mAuDiA4KeyListener = new BenzKeyListener(cb);

		}
		return mAuDiA4KeyListener;
	}

	public BenzKeyListener(KeyListenerCallback cb) {
		super(cb);
	}

	public void refrence(String viewtag) {
		callback.refrenceMethod(viewtag);

	}

	public void dealButtonPage(String viewtag, String parentTag, int bntpage) {
		currentPage = bntpage;

		if (parentTag.equals(BackCarTag.CHOICEPAGE913)) {
			choicePageSelect(viewtag, currentPage);
		} else if (parentTag.equals(BackCarTag.VIDEOPAGE))
			videoPageSelect(viewtag, currentPage);
		else if (parentTag.equals(BackCarTag.SETLIGHTPAGE))
			lightPageSelect(viewtag, currentPage);
	}

	void choicePage_Benz_select(String viewtag, int page) {
		if (viewtag.equals(FlyUtil.HexToTag(BackCarTag.CHOICEBACK_BNT))
				|| viewtag.equals(FlyUtil
						.HexToTag(BackCarTag.CAMERA913_FR_MODE))) // backButton
		{
			BenzModule.getInstance(BackCarService.mBackCarService)
					.Benz913choiceFR_U();

		} else if (viewtag.equals(FlyUtil.HexToTag(BackCarTag.CHOICE_BNT))) {

			Benz913choiceF_U();
		} else if (viewtag.equals(FlyUtil.HexToTag(BackCarTag.CHOICE_BNT2))) {
			Benz913choiceR_U();
		}
	}

	void choicePageSelect(String viewtag, int page) {
		choicePage_Benz_select(viewtag, page);
	}

	void lightPageSelect(String viewtag, int page) {

		BackCarService.mBackCarService.MakeAndSendMessageLight(Integer
				.parseInt(viewtag, 16));
	}

	void videoPageSelect(String viewtag, int page) {
		BackCarService.mBackCarService.MakeAndSendMessageWithBundle(
				MsgType.COMMON, BackCarTag.RMLIGHTVIEW, null);
	}

	void Benz913choiceF_U() {

		FlyBaseListener.setObjLevel(BackCarTag.CHOICECIRCLE, 2);
		FlyBaseListener.setObjLevel(FlyUtil.HexToTag(BackCarTag.CHOICE_BNT), 2);
		FlyBaseListener
				.setObjLevel(FlyUtil.HexToTag(BackCarTag.CHOICE_BNT2), 2);
		FlyBaseListener.setObjLevel(BackCarTag.CHOICEBG, 1);
		FlyBaseListener.setObjLevel(BackCarTag.CHOICECARBG, 1);

	}

	void Benz913choiceR_U() {
		FlyBaseListener.setObjLevel(BackCarTag.CHOICECIRCLE, 1);

		FlyBaseListener.setObjLevel(FlyUtil.HexToTag(BackCarTag.CHOICE_BNT), 2);
		FlyBaseListener
				.setObjLevel(FlyUtil.HexToTag(BackCarTag.CHOICE_BNT2), 2);
		FlyBaseListener.setObjLevel(BackCarTag.CHOICEBG, 1);
		FlyBaseListener.setObjLevel(BackCarTag.CHOICECARBG, 0);

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

	public boolean KEYEVENT(View v, int keycode) {

		Log.d("DDD", "KEYEVENT " + keycode);
		return false;

	}

	void refrenceOnKey(FlyButtonObj v) {
		if (v.getTag() == null)
			return;
		if (v.getTag().equals(FlyUtil.HexToTag(BackCarTag.CHOICE_BNT))
				&& FlyBaseListener.cur_fr_mode == MsgType.FR_MODE) // choice key
		{
			FlyBaseListener.setObjLevel(
					FlyUtil.HexToTag(BackCarTag.CHOICE_BNT), 2);
			FlyBaseListener.setObjLevel(
					FlyUtil.HexToTag(BackCarTag.CHOICE_BNT2), 2);

			BackCar913Service.Mode913.chioceDire = 0;// v.getBackGroudLevel();
			FlyBaseListener.setObjLevel(BackCarTag.CHOICECARBG, 0);
			FlyBaseListener.setObjLevel(BackCarTag.CHOICECIRCLE, 1);
		} else if (v.getTag().equals(FlyUtil.HexToTag(BackCarTag.CHOICE_BNT2))
				&& FlyBaseListener.cur_fr_mode == MsgType.FR_MODE) // choice key
		{
			FlyBaseListener.setObjLevel(
					FlyUtil.HexToTag(BackCarTag.CHOICE_BNT), 2);
			FlyBaseListener.setObjLevel(
					FlyUtil.HexToTag(BackCarTag.CHOICE_BNT2), 2);

			BackCar913Service.Mode913.chioceDire = 1;// v.getBackGroudLevel();
			FlyBaseListener.setObjLevel(BackCarTag.CHOICECARBG, 1);
			FlyBaseListener.setObjLevel(BackCarTag.CHOICECIRCLE, 2);
		}
	}

	boolean choiceBtnSelect(View v) {
		if (v.getTag() != null) {
			if (v.getTag().equals(FlyUtil.HexToTag(BackCarTag.CHOICEBACK_BNT))) {
				switch (FlyBaseListener.cur_fr_mode) {
				case MsgType.FR_MODE: {
					int bglevel = BackCar913Service.Mode913.chioceDire;

					switch (bglevel) {
					case 0:
						FlyBaseListener.focus(FlyUtil
								.HexToTag(BackCarTag.CHOICE_BNT2));
						break;
					case 1:
						FlyBaseListener.focus(FlyUtil
								.HexToTag(BackCarTag.CHOICE_BNT));
						break;
					}
				}
					break;
				case MsgType.F_MODE:
					FlyBaseListener.focus(FlyUtil
							.HexToTag(BackCarTag.CHOICE_BNT));
					break;
				case MsgType.R_MODE:
					FlyBaseListener.focus(FlyUtil
							.HexToTag(BackCarTag.CHOICE_BNT2));
					break;
				case MsgType.F_ORIR_MODE:
					FlyBaseListener.focus(FlyUtil
							.HexToTag(BackCarTag.CHOICE_BNT));
				}

				View circle = FlyBaseListener
						.mfindViewWithTag(BackCarTag.CHOICECIRCLE);
				Animation scaleAnimation = new ScaleAnimation(0.1f, 1.0f, 0.1f,
						1f, Animation.RELATIVE_TO_SELF, 0.5f,
						Animation.RELATIVE_TO_SELF, 0.5f);
				scaleAnimation.setDuration(300);
				circle.startAnimation(scaleAnimation);
				return true;
			} else if (v.getTag().equals(
					FlyUtil.HexToTag(BackCarTag.CHOICE_BNT2))
					|| v.getTag().equals(
							FlyUtil.HexToTag(BackCarTag.CHOICE_BNT))) {
				BenzModule.getInstance(BackCarService.mBackCarService)
						.Benz913choiceFR_U();
			}
		}
		return false;
	}

	public OnKeyListener benz_KeyListener = new OnKeyListener() {
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			// TODO Auto-generated method stub

			final int action = event.getAction();
			final boolean handleKeyEvent = (action != KeyEvent.ACTION_UP);
			if (!handleKeyEvent)
				return false;
			switch (keyCode) {
			case FlyKeyCode.FLYKEY_LEFT:
				refrenceOnKey((FlyButtonObj) v);
				reFocus(v, View.FOCUS_LEFT);
				break;
			case KeyEvent.KEYCODE_DPAD_LEFT:

				refrenceOnKey((FlyButtonObj) v);
				break;
			case FlyKeyCode.FLYKEY_RIGHT:

				refrenceOnKey((FlyButtonObj) v);
				reFocus(v, View.FOCUS_RIGHT);
				break;
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				refrenceOnKey((FlyButtonObj) v);
				break;
			case KeyEvent.KEYCODE_DPAD_UP:
				if (choiceBtnSelect(v))
					return true;
				break;
			case KeyEvent.KEYCODE_DPAD_DOWN:
				if (findNextFocus(v, View.FOCUS_DOWN).equals(
						FlyUtil.HexToTag(BackCarTag.CHOICEBACK_BNT)))
					FlyBaseListener.setObjLevel(BackCarTag.CHOICECIRCLE, 0);
				break;
			case KeyEvent.KEYCODE_BACK:
				BackCarService.mBackCarService.MakeAndSendMessageWithBundle(
						MsgType.COMMON, BackCarTag.BACKKEYEVENT, null);
				if (true)
					return true;
				break;

			default:
				break;
			}
			BackCarService.mBackCarService.MakeAndSendMessageWithBundle(
					MsgType.COMMON, BackCarTag.ANYKEYEVENT, null);

			return false;
		}
	};

	public OnKeyListener benz_KeyListener2 = new OnKeyListener() {
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			// TODO Auto-generated method stub

			final int action = event.getAction();
			final boolean handleKeyEvent = (action != KeyEvent.ACTION_UP);
			if (!handleKeyEvent)
				return false;
			switch (keyCode) {
			case FlyKeyCode.FLYKEY_LEFT:
				reFocus(v, View.FOCUS_DOWN);
				break;
			case KeyEvent.KEYCODE_DPAD_LEFT:
				callback.refrenceKey(keyCode, (String) v.getTag(), dealkey);
				break;
			case FlyKeyCode.FLYKEY_RIGHT:
				reFocus(v, View.FOCUS_UP);
				break;
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				callback.refrenceKey(keyCode, (String) v.getTag(), dealkey);
				break;
			case KeyEvent.KEYCODE_BACK:
				BackCarService.mBackCarService.MakeAndSendMessageWithBundle(
						MsgType.COMMON, BackCarTag.BACKKEYEVENT, null);
				break;
			default:
				break;
			}
			BackCarService.mBackCarService.MakeAndSendMessageWithBundle(
					MsgType.COMMON, BackCarTag.ANYKEYEVENT, null);
			return false;
		}
	};

	DealKey dealkey = new DealKey();

	public class DealKey implements MethodCallbacks {

		public void methodCallback(String viewtag, String parentTag,
				int bntpage, int keyCode) {
			switch (keyCode) {

			case KeyEvent.KEYCODE_DPAD_RIGHT:
			case KeyEvent.KEYCODE_DPAD_LEFT:
				if (parentTag.equals(BackCarTag.SETLIGHTPAGE)) {
					BackCarService.mBackCarService
							.MakeAndSendMessageWithBundle(MsgType.COMMON,
									BackCarTag.RMLIGHTVIEW, null);
					FlyBaseListener.focus(FlyUtil
							.HexToTag(BackCarTag.SETLIGHT_CID));

				}
				break;
			}
		}
	}

}
