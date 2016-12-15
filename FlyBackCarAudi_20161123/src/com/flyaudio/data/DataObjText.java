package com.flyaudio.data;

import android.util.Log;
import android.view.View;

import com.flyaudio.globaldefine.LanguageString;
import com.flyaudio.object.FlyTextObj;

public class DataObjText extends DataObjBase {
	private String mText = null;

	@Override
	public void storeProperty(byte[] data, int len) {
		super.storeProperty(data, len);
		switch (data[7]) {
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
				this.mText = str;
				Log.i("BackCar", "DataObjText 2 mText " + str);
			}
		}
			break;
		case (byte) 0xFF: {
			// Log.d("vv","FFFFFFFF data[8]:"+data[8]);
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
					// Log.d("vv","mText ="+mText+"  StringID:"+StringID);
					// Log.i("BackCar","DataObjText FF mText " + this.mText);
				}
			}
		}
			break;

		case 3: {
			int mParamLen = len - 8;
			String str = "";
			int im = 0;
			byte[] mt = new byte[mParamLen];
			for (im = 0; im < mParamLen; im++)
				mt[im] = data[8 + im];

			this.mText = new String(mt);
			;
			Log.i("BackCar", "DataObjText 3 mText " + str + " len " + mParamLen
					+ "  byte " + data[8]);
		}
			break;

		}
	}

	@Override
	public void restoreProperty(View view) {
		super.restoreProperty(view);
		if (!(view instanceof FlyTextObj))
			return;
		if (mText != null)
			((FlyTextObj) view).setmText(this.mText);

		((FlyTextObj) view).invalidate();
	}

}
