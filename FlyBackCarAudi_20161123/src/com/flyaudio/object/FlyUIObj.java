package com.flyaudio.object;

import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.view.MotionEvent;

import com.flyaudio.backcar.BackCarService;
import com.flyaudio.backcar.R;

import android.widget.FrameLayout;
import android.view.ViewGroup.LayoutParams;
import android.os.SystemProperties;

import com.flyaudio.backcar913.BackCarTag;

import android.os.Bundle;

public class FlyUIObj extends ImageView {
	protected int iData;
	protected String strData;
	protected boolean bData;
	protected int mControlID;
	public boolean m_bTouchable;
	protected boolean mSpecialEvent;
	public boolean mIsLongPressed = false;

	// protected HashMap<Integer, Object> mData;

	public FlyUIObj(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		// mData = new HashMap<Integer, Object>();
		String tag = (String) getTag();
		if (tag != null) {
			mControlID = Integer.parseInt(tag, 16);
			tag = null;
		}
		TypedArray a = getContext().obtainStyledAttributes(attrs,
				R.styleable.FlyUIObj);
		m_bTouchable = a.getBoolean(R.styleable.FlyButtonObj_touchAble, true);
		mSpecialEvent = a.getBoolean(R.styleable.FlyUIObj_SpecialEvent, false);
		// Log.d("HandleUIObjSpecialMsg","mSpecialEvent ="+mSpecialEvent);
		a.recycle();
	}

	public void MakeAndSendMessageToModule(int controlID, byte controlType,
			byte[] param) {

		Log.d("send", "FlyUIObj MakeAndSendMessageToModule controlID="
				+ controlID);
		try {
			if (!mSpecialEvent) {
				if (null != Integer.toString(controlID))
					// FlyaudioApplication.mFlyaudioApplication.MakeAndSendMessage(controlID,
					// controlType, param);
					BackCarService.mBackCarService.MakeAndSendMessage(
							controlID, controlType, param);
			} else {
				byte[] data = new byte[3];
				data[0] = (byte) 0xE1;
				data[1] = (byte) 0x20;
				data[2] = (byte) 0x09;
				if (null != Integer.toString(controlID))
					BackCarService.mBackCarService.MakeAndSendMessage(
							controlID, controlType, data);
				// HandleUIObjSpecialMsg.HandlerMsg(getContext(),this.mControlID);
			}

			// FlyaudioUIActivity.SendMessageToService(controlID, controlType,
			// param);
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	public void setBackGroud(int imgIndex) {
		if (imgIndex < 0)
			return;
		try {
			getBackground().setLevel(imgIndex);
		} catch (Exception e) {
		}
		// invalidate();
	}

	// main method
	public void handleControlUIMessage(byte[] data, int len) {
		switch (data[7]) {
		case 0: {
			if (data[8] == 0)
				setBooleanData(false);
			else
				setBooleanData(true);
		}
			break;
		case 1: {
			int whichStatus = (int) (((int) (data[8] & 0xFF) << 24)
					| ((int) (data[9] & 0xFF) << 16)
					| ((int) (data[10] & 0xFF) << 8) | (data[11] & 0xFF));
			setIntegerData(whichStatus);
		}
			break;
		case 2: {
			int mParamLen = len - 9;
			String str = "";
			if (mParamLen >= 0 && mParamLen < 255) {
				char[] mParam = new char[mParamLen];
				for (int i = 0; i < mParamLen;) {
					mParam[i] = (char) (((data[9 + i] & 0xff) << 8) | (data[8 + i]) & 0xff);
					str = str + Character.toString(mParam[i]);
					i = i + 2;
				}
				setStringData(str);
			}
		}
			break;
		case (byte) 0xFF: {
			int commandLen = len - 9;
			// Log.d("fly", "commandLen--------- : "+commandLen);
			if (commandLen >= 0 && commandLen < 255) {
				byte[] command = new byte[commandLen];
				for (int i = 0; i < commandLen; i++) {
					command[i] = data[8 + i];
				}
				setCommand(command);
			}
		}
			break;
		}
	}

	// public HashMap<Integer, Object> getData(){
	// return this.mData;
	// }

	// 接口
	public void setBooleanData(boolean data) {
		bData = data;
	}

	public void setIntegerData(int data) {
		iData = data;
	}

	public void setStringData(String data) {
		strData = data;
	}

	public void setCommand(byte[] command) {
		// Log.d("fly", "commandLen------------------------------ : 5555");

		switch (command[0]) {
		case 0x00: {
			if (command[1] == 0)
				setDisplay(false);
			else
				setDisplay(true);
		}
			break;
		case 0x0F:

			setBackGroud((int) command[1]);
			break;
		}
	}

	protected void setDisplay(boolean mVisible) {
		if (mVisible)
			setVisibility(View.VISIBLE);
		else
			setVisibility(View.GONE);
	}

	public int getControlID() {
		return mControlID;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
	}

	public interface FlyUIObjListener {
		void MakeAndSendMessageToModule(int controlID, byte controlType,
				byte[] param);
	}

	// public void setmData(HashMap<Integer, Object> data) {
	// this.mData = data;
	// }

	public void setControlID(int id) {

		this.mControlID = id;

	}

	private float tmpY = 0;
	private float tmpX = 0;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub

		if (!m_bTouchable)
			return false;
		Bundle data = new Bundle();
		switch (event.getAction()) {
		case MotionEvent.ACTION_CANCEL:
			Log.d("DDD", "onTouchEvent ACTION_CANCEL");
			break;

		case MotionEvent.ACTION_DOWN:

			data.putString(BackCarTag.STRKEY, BackCarTag.TOUCH_DOWN);
			BackCarService.mBackCarService.MakeAndSendMessageWithBundle(0,
					mControlID, data);

			tmpY = event.getRawY();
			tmpX = event.getRawX();

			break;
		case MotionEvent.ACTION_UP:
			data.putString(BackCarTag.STRKEY, BackCarTag.TOUCH_UP);
			BackCarService.mBackCarService.MakeAndSendMessageWithBundle(0,
					mControlID, data);
			break;

		case MotionEvent.ACTION_MOVE:
			int event_xy[] = new int[2];
			event_xy[1] = (int) (event.getRawY() - tmpY);
			event_xy[0] = (int) (event.getRawX() - tmpX);

			data.putString(BackCarTag.STRKEY, BackCarTag.TOUCH_MOVE);
			data.putIntArray(BackCarTag.INTKEY, event_xy);
			BackCarService.mBackCarService.MakeAndSendMessageWithBundle(0,
					mControlID, data);
			tmpX = event.getRawX();
			tmpY = event.getRawY();
			break;
		default:
			break;
		}
		return true;
	}

	static boolean isLongPressed(float lastX, float lastY, float thisX,
			float thisY, long lastDownTime, long thisEventTime,
			long longPressTime) {
		float offsetX = Math.abs(thisX - lastX);
		float offsetY = Math.abs(thisY - lastY);
		long intervalTime = thisEventTime - lastDownTime;
		if (offsetX <= 10 && offsetY <= 10 && intervalTime >= longPressTime) {
			return true;
		}
		return false;
	}

	protected Bitmap mDrawableToBitamp(Drawable drawable) {
		int w = drawable.getIntrinsicWidth();
		int h = drawable.getIntrinsicHeight();
		if(w >0 && h >0){
		Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
				: Bitmap.Config.RGB_565;
		Bitmap bitmap = Bitmap.createBitmap(w, h, config);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, w, h);
		drawable.draw(canvas);
		return bitmap;
		}
		else 
			return null;
	}
	
}
