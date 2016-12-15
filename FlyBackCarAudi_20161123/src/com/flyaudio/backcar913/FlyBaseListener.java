package com.flyaudio.backcar913;

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
import com.flyaudio.backcar.util.*;
import com.flyaudio.backcar.modules.BaseModule;

import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.Canvas;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import com.flyaudio.keyevent.*;

public class FlyBaseListener implements BaseKeyListener.KeyListenerCallback {
	private View mview;
	public ViewGroup iviewContainer;

	public String baseListenerName = null;
	private String TAG = "FlyBackCarListener";

	private View currentView = null;
	private View ClickView = null;
	private boolean needCheckView = false;

	private int row = -1;
	private int colum = -1;

	private static int curlightLevel = 0;
	private int currentPage = 0;
	private int buttonPage = 0;
	private static ArrayList<ArrayList<FlyButtonPage>> AllflyButtonArray = new ArrayList<ArrayList<FlyButtonPage>>();
	public ArrayList<FlyButtonPage> flyButtonArray = new ArrayList<FlyButtonPage>();
	private ArrayList<String> PageTag = new ArrayList<String>();

	private String mkeyStr = "10000";
	public static ArrayList<FlyBaseListener> gListener = new ArrayList<FlyBaseListener>();

	private static String oldFocus;
	static MyQueue myqueue = new MyQueue("913BtnQueue");
	public static int cur_fr_mode;

	public static enum CAR {
		DEFAULT, G6Z, BENZ, AUDIA3, AUDIA4, HYUNDAI,AUDIQ3,AUDIA6
	};

	public static CAR carTag = CAR.DEFAULT;

	static BaseKeyListener mBaseKeyListener;

	public FlyBaseListener(String listenName, ViewGroup mViewgroup, int row,
			int currenFocus, ArrayList<String> pagetag) {
		this.iviewContainer = mViewgroup;
		if (pagetag != null)
			PageTag = pagetag;
		baseListenerName = listenName;

		searchNameAndDel(baseListenerName);
		gListener.add(this);
		init();
	}


	
	public static boolean searchNameAndDel(String name) {
		if (gListener.size() <= 0)
			return false;
		for (FlyBaseListener sl : gListener)
			if (name.equals(sl.baseListenerName)) {
				Log.d("DDDD", "remove  "+name);
				AllflyButtonArray.remove(sl.flyButtonArray);
				gListener.remove(sl);
				return false;
			}
		return false;
	}

	private void init() {
		if (PageTag.size() != 0)
			mkeyStr = PageTag.get(0);
		CountView(iviewContainer, false, null);
		// for(FlyButtonPage flybuttonpage :flyButtonArray)
		// for(FlyButtonObj bntobj :flybuttonpage.buttonChild)
		// Log.d("DDD", " "+bntobj.getTag());
		AllflyButtonArray.add(flyButtonArray);
		// +++++++++++++++++++++++++++++//
		Log.d(TAG, "carTag " + carTag + " buttonPageNum " + buttonPage);
	}

	void CountView(ViewGroup v, boolean hasTaget, FlyButtonPage pareant) {
		boolean hasFlyButton = false;
		boolean isSetLight = false;
		int cn = v.getChildCount();

		// Log.d(TAG, "tag = "+v.getTag()+ "   cn  = "+cn);

		FlyButtonPage tmpbutton = new FlyButtonPage();
		for (int im = 0; im < cn; im++) {
			View vm = v.getChildAt(im);
			if (vm instanceof FlyButtonObj) {
				hasFlyButton = true;
				row++;
				/*
				if (carTag == CAR.BENZ) {
					if (mkeyStr.equals(BackCarTag.SETLIGHTPAGE))
						vm.setOnKeyListener(BenzKeyListener.getInstance(this).benz_KeyListener2);
					else
						vm.setOnKeyListener(BenzKeyListener.getInstance(this).benz_KeyListener);
					vm.setOnFocusChangeListener(BenzKeyListener
							.getInstance(this));

					mBaseKeyListener = (BaseKeyListener) BenzKeyListener
							.getInstance(this);
				}

				else if (carTag == CAR.AUDIA4) {
					if (mkeyStr.equals(BackCarTag.RADARPAGE913))
						vm.setOnKeyListener(AuDiA4KeyListener.getInstance(this).pageRadar);
					else if (mkeyStr.equals(BackCarTag.PAGE_CVB_LAYOUT)) {
						vm.setOnKeyListener(AuDiA4KeyListener.getInstance(this).CVBPage);
					} else
						vm.setOnKeyListener(AuDiA4KeyListener.getInstance(this));
					vm.setOnFocusChangeListener(AuDiA4KeyListener
							.getInstance(this));
					mBaseKeyListener = (BaseKeyListener) AuDiA4KeyListener
							.getInstance(this);
				} else if (carTag == CAR.AUDIA3) {
					if (mkeyStr.equals(BackCarTag.RADARPAGE913))
						vm.setOnKeyListener(AuDiA3KeyListener.getInstance(this).pageRadar);
					else if (mkeyStr.equals(BackCarTag.PAGE_CVB_LAYOUT)) {
						vm.setOnKeyListener(AuDiA3KeyListener.getInstance(this).CVBPage);
					} else
						vm.setOnKeyListener(AuDiA3KeyListener.getInstance(this));
					vm.setOnFocusChangeListener(AuDiA3KeyListener
							.getInstance(this));
					mBaseKeyListener = (BaseKeyListener) AuDiA3KeyListener
							.getInstance(this);
				}
				*/
				mBaseKeyListener = BaseModule.getBaseModule().SetOnKeyListener(mkeyStr, vm, this);
				tmpbutton.buttonChild.add((FlyButtonObj) vm);

			} else if (vm instanceof ViewGroup) {
				CountView((ViewGroup) vm, hasTaget, pareant);
			}
		}
		if (hasFlyButton) {
			// String s = PageTag.size() >= (buttonPage+1) ?
			// PageTag.get(buttonPage) : "1000";
			// String s = PageTag.size() >= (buttonPage+1) ?
			// PageTag.get(buttonPage) : "1000";
			// Log.d("DDD", "add page "+tmpbutton.buttonChild.size());
			tmpbutton.init(mkeyStr, ++buttonPage);
			flyButtonArray.add(tmpbutton);
		}
	}

	public void focus() {

	}

	public static void oldBtnFocus() {
		focus(myqueue.QueueRef());
	}

	public static void saveLightLevel(int lightLevel) {
		curlightLevel = lightLevel;

	}

	public static void setCur_fr_mode(int fr_mode) {
		cur_fr_mode = fr_mode;
	}

	public static int getCur_fr_mode() {
		return cur_fr_mode;
	}

	public static int getoldLightLevel() {
		return curlightLevel;
	}

	public void focusLight(String Tag) {
		if (Tag == null)
			return;
		curlightLevel = Integer.parseInt(Tag, 16);
		focus(Tag);
	}

	public static void focusInt(int Tag) {
		String controlID = String.format("%08x", Tag);
		focus(controlID);
	}
	

  
  
	public static void focus(String Tag) {
		// Log.d("FLYBackCar", "focus  "+Tag);
		if (Tag == null)
			return;

		for (FlyBaseListener sl : gListener) {
			View v = (View) sl.iviewContainer.findViewWithTag(Tag);
			if (v != null) {
				v.requestFocus();
				myqueue.OfferQueue(Tag);
				break;
			}

		}
	}

	public static void setObjLevel(String tag, int pos) {

		int msg2 = (int) Integer.parseInt(tag, 16); // carbg
		msg2 = msg2 << 4 | pos;
		BackCarService.mBackCarService.MakeAndSendMessageBgChange(msg2);

	}

	public static View mfindViewWithTag(String tag) {

		for (FlyBaseListener sl : gListener) {
			View v = (View) sl.iviewContainer.findViewWithTag(tag);
			if (v != null)
				return v;
		}
		return null;
	}

	public static void findViewAndShowOrNot(String tag, boolean show) {
		final View v = mfindViewWithTag(tag);
		if (v != null) {
			if (show)
				v.setVisibility(View.VISIBLE);
			else
				v.setVisibility(View.GONE);
		}
	}

	public void findTagFocus(String tag) {

	}

	public static void UIParentViewShow(String tag, boolean show,
			boolean needReversal) {
		final View v = mfindViewWithTag(tag);
		try {
			final ViewGroup vp = (ViewGroup) v.getParent();
			if (needReversal) {
				int state_visl = vp.getVisibility();
				if (state_visl == View.VISIBLE)
					show = false;
				else
					show = true;
			}
			if (show)
				vp.setVisibility(View.VISIBLE);
			else
				vp.setVisibility(View.GONE);
		} catch (Exception e) {
			// Log.d(TAG, "UIParentViewShow "+e);
		}
	}

	public static int getObjBgLevel(String tag) {
		View v = mfindViewWithTag(tag);
		if (v != null)
			return v.getBackground().getLevel();
		else {
			Log.d("FLYBackCar", "getObjBgLevel  " + tag + "  null");
			return 0;
		}
	}

	public void refrenceMethod(String viewtag) {
		if (viewtag == null)
			return;

		BackCar913Service.Mode913.focusTag = viewtag;
		for (ArrayList<FlyButtonPage> mflyButtonArray : AllflyButtonArray)
			for (FlyButtonPage flybuttonpage : mflyButtonArray)
				for (FlyButtonObj bntobj : flybuttonpage.buttonChild) {
					if (bntobj.getTag() != null)
						if (bntobj.getTag().equals(viewtag)) {
							mBaseKeyListener.dealButtonPage(viewtag,
									flybuttonpage.getTag(),
									flybuttonpage.buttonPageId);
							return;
						}
				}
	}

	public void refrenceKey(int keyCode, String viewtag,
			BaseKeyListener.MethodCallbacks callback) {
		if (viewtag == null)
			return;
		for (ArrayList<FlyButtonPage> mflyButtonArray : AllflyButtonArray)
			for (FlyButtonPage flybuttonpage : flyButtonArray)
				for (FlyButtonObj bntobj : flybuttonpage.buttonChild) {
					if (bntobj.getTag().equals(viewtag)) {
						callback.methodCallback(viewtag,
								flybuttonpage.getTag(),
								flybuttonpage.buttonPageId, keyCode);
						return;
					}
				}
	}

	class FlyButtonPage {
		public ArrayList<ArrayList<FlyButtonObj>> buttonGroup = new ArrayList<ArrayList<FlyButtonObj>>();
		public ArrayList<FlyButtonObj> buttonChild = new ArrayList<FlyButtonObj>();

		// public ArrayList<FlyButtonPage> buttonpage = new
		// ArrayList<FlyButtonPage>();

		private int groupSize;
		private int childSize;
		private String Tag = null;
		public int buttonPageId;

		private int childPageSize;

		void init(String tag, int page) {
			Tag = tag;
			buttonPageId = page;
			initCount();
		}

		String getTag() {
			return Tag;
		}

		int getGroupSize() {
			return groupSize;
		}

		int getChildSize() {
			return childSize;
		}

		void initCount() {
			groupSize = buttonGroup.size();
			childSize = buttonChild.size();

		}
	}

	public static boolean DeaelBtnObjTouch(FlyButtonObj v, int x, int y) {
		final int cid = v.getControlID();
		if (cid == BackCarTag.CHOICE_BNT || cid == BackCarTag.CHOICE_BNT2) { // choice btn 914
			Bitmap bmp = drawableToBitamp(v.getBackground());

			if (bmp.getPixel(x, y) == 0)
				return false;
			v.getBackground().setLevel(2);

		}
		return true;
	}

	private static Bitmap drawableToBitamp(Drawable drawable) {
		int w = drawable.getIntrinsicWidth();
		int h = drawable.getIntrinsicHeight();
		Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
				: Bitmap.Config.RGB_565;
		Bitmap bitmap = Bitmap.createBitmap(w, h, config);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, w, h);
		drawable.draw(canvas);
		return bitmap;
	}
}
