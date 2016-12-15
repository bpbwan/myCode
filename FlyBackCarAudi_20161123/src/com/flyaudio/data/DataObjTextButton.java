package com.flyaudio.data;

import android.util.Log;
import android.view.View;

import com.flyaudio.globaldefine.LanguageString;
import com.flyaudio.object.FlyTextButtonObj;

public class DataObjTextButton extends DataObjButton {

	private String mText = null;
	private int myTouchable = -1;
	private int level = -1; // backgroud

	@Override
	public void storeProperty(byte[] data, int len) {
		super.storeProperty(data, len);
		// TODO Auto-generated method stub
		switch (data[7]) {
		case 0: {
			level = data[8];
			// Log.d("BackCar", "DataObjTextButton level " + level);
		}
			break;
		case 1: {
			int param = (int) (((int) (data[8] & 0xFF) << 24)
					| ((int) (data[9] & 0xFF) << 16)
					| ((int) (data[10] & 0xFF) << 8) | (data[11] & 0xFF));
			level = param;
			if (param == 2)
				myTouchable = 0;
			else
				myTouchable = 1;
			// Log.d("BackCar", "DataObjTextButton myTouchable " + myTouchable);
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
				// Log.d("BackCar", "DataObjTextButton str " + str);
				mText = str;
			}
		}
			break;
		case (byte) 0xFF:
			// Log.d("dd","FFFFFFFF data[8]:"+data[8]);
			int commandLen = len - 9;
			if (commandLen >= 0 && commandLen < 255) {
				byte[] command = new byte[commandLen];
				for (int i = 0; i < commandLen; i++) {
					command[i] = data[8 + i];
				}
				if (command[0] == -64) {
					int StringID = (command[1] << 24) | (command[2] << 16)
							| (command[3] << 8) | (command[4]);
					LanguageString language = new LanguageString();
					this.mText = language.GetStringByID(StringID);
					// Log.d("BackCar","DataObjTextButton mText ="+mText+"  StringID:"+StringID);
				}
			}
			break;
		}
		// Log.d("TextButton", "level:" + level);
	}

	@Override
	public void restoreProperty(View v) {
		super.restoreProperty(v);

		if (!(v instanceof FlyTextButtonObj)) {
			return;
		}
		// TODO Auto-generated method stub
		if (level != -1) {
			((FlyTextButtonObj) v).setBackground(level);
		}
		if (mText != null)
			((FlyTextButtonObj) v).setmText(mText);

		if (myTouchable != -1)
			((FlyTextButtonObj) v).setmTouchable(intToBoolean(myTouchable));
		((FlyTextButtonObj) v).invalidate();

	}

}
