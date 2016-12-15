package com.flyaudio.keyevent;

import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.MotionEvent;
import android.view.KeyEvent;
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
import com.flyaudio.backcar913.BackCarTag;
import android.os.SystemProperties;

public abstract class BaseKeyListener implements OnFocusChangeListener,
		OnKeyListener {

	public View currentView;
	public KeyListenerCallback callback;
	public int currentPage = 0;
	private long currentKeyEventTime = 0;
	private long lastKeyEventTime = 0;
	private String TAG = "BackCarBaseKey";
	public abstract void refrence(String viewtag);

	public abstract void dealButtonPage(String viewtag, String parentTag,
			int bntpage);

	public abstract boolean KEYEVENT(View v, int keycode);

	public BaseKeyListener(KeyListenerCallback cb) {
		callback = cb;
	}

	protected void reFocus(View v, int dir) {
		View vm = v.focusSearch(dir);
		if (vm != null)
			vm.requestFocus();

	}

	protected String findNextFocus(View v, int dir) {
		final View vm = v.focusSearch(dir);
		if (vm != null)
			return (String) vm.getTag();
		return "notfound";
	}

	@Override
	public void onFocusChange(View view, boolean b) {

		if (b) {
			String mTag = (String) view.getTag();
			// currentView = view;

			refrence(mTag);
		}
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub

		final int action = event.getAction();
		final boolean handleKeyEvent = (action != KeyEvent.ACTION_UP);
		if (!handleKeyEvent)
			return false;
		if (KEYEVENT(v, keyCode))
			return true;

		BackCarService.mBackCarService.MakeAndSendMessageWithBundle(
				MsgType.COMMON, BackCarTag.ANYKEYEVENT, null);
		return false;
	}

	public interface KeyListenerCallback {
		public void refrenceMethod(String viewtag);

		public void refrenceKey(int keyCode, String viewtag,
				MethodCallbacks callback);
	}

	public interface MethodCallbacks {
		public void methodCallback(String viewtag, String parentTag,
				int bntpage, int keyCode);
	}

	public String getCurrentForeground() {

		String pageForeground = SystemProperties.get("fly.topapp.packagename");
		if (pageForeground == null)
			pageForeground = "";
		return pageForeground;
	}
	
	public boolean keyEventFilter(int keycode, int timeout){
		
		
		if(FlyKeyCode.FLYKEY_LEFT == keycode || keycode == FlyKeyCode.FLYKEY_RIGHT){
				currentKeyEventTime = System.currentTimeMillis();	
			if((lastKeyEventTime+timeout)>currentKeyEventTime){
				if(lastKeyEventTime == 0)
					lastKeyEventTime = currentKeyEventTime;
				return true;
			}
			else 
				lastKeyEventTime = currentKeyEventTime;

			return false;
		}
		else 
			return false;
	
	}
}
