package com.flyaudio.data;

import android.util.Log;
import android.view.View;

import com.flyaudio.object.FlySeekBar;

public class DataObjSeekBar extends DataObjBase {
	protected int backgroudRes = -1;
	protected int m_bTouchable = -1;
	protected int m_Progress = -1;

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
		case (byte) 0xFF: {
			switch (data[8]) {
			case 0x10:
				this.m_bTouchable = data[9];
				// Log.i("BackCar","DataObjButton m_bTouchable 10 " +
				// this.m_bTouchable);
				break;
			case 0x11:
				this.m_Progress = (int) (((int) (data[9] & 0xFF) << 24)
						| ((int) (data[10] & 0xFF) << 16)
						| ((int) (data[11] & 0xFF) << 8) | (data[12] & 0xFF));

			}
		}
			break;
		}
	}

	public void restoreProperty(View v) {

		super.restoreProperty(v);
		if (!(v instanceof FlySeekBar))
			return;

		if (backgroudRes != -1)
			((FlySeekBar) v).getBackground().setLevel(backgroudRes);

		// if (this.m_bTouchable != -1)
		// ((FlySeekBar) v).setM_bTouchable(intToBoolean(m_bTouchable));
		if (this.m_Progress != -1)
			((FlySeekBar) v).setM_bProgress(this.m_Progress);
	}

}
