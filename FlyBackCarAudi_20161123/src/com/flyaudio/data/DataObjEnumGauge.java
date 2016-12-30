package com.flyaudio.data;

import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.flyaudio.backcar.MsgType;
import com.flyaudio.object.FlyEnumGaugeObj;

public class DataObjEnumGauge extends DataObjBase {
	protected int m_Data = -1;
	protected int m_Color = -1;
	public void storeProperty(byte[] data, int len) {
		super.storeProperty(data, len);
		switch (data[7]) {
		case 1:
			int whichStatus = (int) (((int) (data[8] & 0xFF) << 24)
					| ((int) (data[9] & 0xFF) << 16)
					| ((int) (data[10] & 0xFF) << 8) | (data[11] & 0xFF));
			m_Data = whichStatus;
			// Log.i("BackCar","DataObjEnumGauge m_Data " + m_Data);
			break;
		case 0x02:
			whichStatus = (int) (((int) (data[8] & 0xFF) << 24)
					| ((int) (data[9] & 0xFF) << 16)
					| ((int) (data[10] & 0xFF) << 8) | (data[11] & 0xFF));
			m_Color = whichStatus;
		}
	}

	public void restoreProperty(final View view) {
//		if(!Updating){
//			Updating = true;
//			new Handler().postDelayed(new Runnable() {
//				
//				@Override
//				public void run() {
//					// TODO Auto-generated method stub
//					DelayUpdateUi(view);
//					if (!(view instanceof FlyEnumGaugeObj))
//						return;
//					if(m_Color != -1 )
//						((FlyEnumGaugeObj) view).ChangeDrawableColor(m_Color);
//					
//					if (m_Data != -1) {
//						((FlyEnumGaugeObj) view).setBackgroundIndex(m_Data);
//					}
//					Updating = false;
//				}
//			}, MsgType.UI_UPDATE_TIME);
//	}
		
		
		super.restoreProperty(view);
		if (!(view instanceof FlyEnumGaugeObj))
			return;
		if(m_Color != -1 )
			((FlyEnumGaugeObj) view).ChangeDrawableColor(m_Color);
		
		if (m_Data != -1) {
			((FlyEnumGaugeObj) view).setBackgroundIndex(m_Data);
		}
	}
}
