package com.flyaudio.data;

import com.flyaudio.object.FlyUIObj;

import android.util.Log;
import android.view.View;

public class DataObjBase {
	protected int visible = -1;

	public void storeProperty(byte[] data, int len) {
		switch (data[7]) {
		case (byte) 0xFF: {
			int commandLen = len - 9;
			if (commandLen >= 0 && commandLen < 255) {
				byte[] command = new byte[commandLen];
				for (int i = 0; i < commandLen; i++) {
					command[i] = data[8 + i];
				}

				if (0x0 == command[0])
					visible = command[1];
				// Log.i("BackCar","DataObjBase visible " + visible);
			}
		}
			break;
		}
	}

	public void restoreProperty(View v) {

		if (!(v instanceof FlyUIObj))
			return;
		if (visible != -1) {
			// Log.i("BackCarMM","restoreProperty DataObjBase visible " +
			// visible);
			if (visible == 1) {
				((FlyUIObj) v).setVisibility(View.VISIBLE);
			} else if (visible == 0) {
				((FlyUIObj) v).setVisibility(View.GONE);
			}
		}
	}

	public static boolean intToBoolean(int i) {
		return i == 0 ? false : true;
	}
}
