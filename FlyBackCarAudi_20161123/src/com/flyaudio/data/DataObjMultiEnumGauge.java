package com.flyaudio.data;

import android.util.Log;
import android.view.View;

import com.flyaudio.object.FlyMultiEnumGaugeObj;

public class DataObjMultiEnumGauge extends DataObjBase {
	protected int mDrawableIndex = -1;
	protected int m_Data = -1;
	int whichStatus = 0;

	public void storeProperty(byte[] data, int len) {
		
		super.storeProperty(data, len);
		switch (data[7]) {
		case 0x01:
			whichStatus = (int) (((int) (data[8] & 0xFF) << 24)
					| ((int) (data[9] & 0xFF) << 16)
					| ((int) (data[10] & 0xFF) << 8) | (data[11] & 0xFF));
			m_Data = whichStatus;
			// Log.i("BackCar","DataObjEnumGauge m_Data " + m_Data);
			break;

		case 0x02:
			whichStatus = (int) (((int) (data[8] & 0xFF) << 24)
					| ((int) (data[9] & 0xFF) << 16)
					| ((int) (data[10] & 0xFF) << 8) | (data[11] & 0xFF));
			mDrawableIndex = whichStatus;
			// Log.i("BackCar","DataObjEnumGauge m_Data " + m_Data);
			
		case 0x03:

			break;
		}
	}

	public void restoreProperty(View view) {
		//super.restoreProperty(view);
		
		if (!(view instanceof FlyMultiEnumGaugeObj))
			return;
		if (mDrawableIndex != -1) {
			((FlyMultiEnumGaugeObj) view)
					.setBackgroundDrawableIndex(mDrawableIndex);
		}
		if (m_Data != -1) {
			((FlyMultiEnumGaugeObj) view).setBackgroundIndex(m_Data);
		}

	}
}
