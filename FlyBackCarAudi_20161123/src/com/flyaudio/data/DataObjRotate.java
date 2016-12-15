package com.flyaudio.data;

import android.util.Log;
import android.view.View;

import com.flyaudio.object.FlyRotateObj;

public class DataObjRotate extends DataObjBase {
	protected int backgroudRes = -1;
	protected int m_bTouchable = -1;
	protected int state_level = 0;

	public void storeProperty(byte[] data, int len) {
		super.storeProperty(data, len);
		switch (data[7]) {
		case 0: {
			backgroudRes = data[8];
			// Log.i("BackCar","DataObjButton backgroudRes 0 " +visible);
		}
			break;
		case 1: {
			backgroudRes = (int) (((int) (data[8] & 0xFF) << 24)
					| ((int) (data[9] & 0xFF) << 16)
					| ((int) (data[10] & 0xFF) << 8) | (data[11] & 0xFF));
			// Log.i("BackCar","DataObjButton backgroudRes 1 " + visible);
		}
			break;
		case 2:
			state_level = (int) (((int) (data[8] & 0xFF) << 24)
					| ((int) (data[9] & 0xFF) << 16)
					| ((int) (data[10] & 0xFF) << 8) | (data[11] & 0xFF));
			break;
		case (byte) 0xFF: {
			switch (data[8]) {
			case 0x10:
				this.m_bTouchable = data[9];
				// Log.i("BackCar","DataObjButton m_bTouchable 10 " +
				// this.m_bTouchable);
				break;
			}

		}
			break;
		}
	}

	public void restoreProperty(View v) {
		super.restoreProperty(v);
		if (!(v instanceof FlyRotateObj))
			return;

		if (backgroudRes != -1)
			((FlyRotateObj) v).getBackground().setLevel(backgroudRes);

		((FlyRotateObj) v).SetCurrentRotate(state_level);
	}

}
