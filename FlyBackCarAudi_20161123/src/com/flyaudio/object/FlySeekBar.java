package com.flyaudio.object;

import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Canvas;
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
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.view.View.OnTouchListener;
import android.view.View.OnClickListener;
import com.autochips.backcar.BackCar;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.os.Bundle;
import com.flyaudio.backcar.MsgType;

public class FlySeekBar extends SeekBar implements OnSeekBarChangeListener {
	protected int iData;
	protected String strData;
	protected boolean bData;
	protected int mControlID;
	private boolean m_bTouchable;
	protected boolean mSpecialEvent;

	public FlySeekBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		// mData = new HashMap<Integer, Object>();
		String tag = (String) getTag();
		if (tag != null) {
			mControlID = Integer.parseInt(tag, 16);
			tag = null;
		}
//		setEnabled(false);
		setOnSeekBarChangeListener(this);
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// TODO Auto-generated method stub
		Bundle data = new Bundle();
		data.putInt(BackCarTag.INTKEY, progress);
		BackCarService.mBackCarService.MakeAndSendMessageWithBundle(
				MsgType.COMSEEKBAR, mControlID, data);
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

		// Log.i(TAG,"start	"+String.valueOf(seekBar.getProgress()));
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		// Log.i(TAG,"stop"+String.valueOf(seekBar.getProgress()));
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

	protected void setDisplay(boolean mVisible) {
		if (mVisible)
			setVisibility(View.VISIBLE);
		else
			setVisibility(View.GONE);
	}

	public int getControlID() {
		return mControlID;
	}

	public void setControlID(int id) {

		this.mControlID = id;

	}

	public void setM_bProgress(int mProgress) {
		// Log.d("DDD", "setM_bProgress "+mProgress);

		if (mProgress == getProgress()) {
			Bundle data = new Bundle();
			data.putInt(BackCarTag.INTKEY, mProgress);
			BackCarService.mBackCarService.MakeAndSendMessageWithBundle(
					MsgType.COMSEEKBAR, mControlID, data);

		}
		setProgress(mProgress);
	}

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
			break;
		}
	}

}
