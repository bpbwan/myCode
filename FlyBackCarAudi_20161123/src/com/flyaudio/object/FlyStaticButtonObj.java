package com.flyaudio.object;

import com.flyaudio.globaldefine.UIAction;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class FlyStaticButtonObj extends FlyButtonObj {

	public FlyStaticButtonObj(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		if (m_bTouchable == false) {
			return true;
		}

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (m_bDownEvent) {
				byte[] paramBuf = new byte[1];
				paramBuf[0] = 0x00;
				MakeAndSendMessageToModule(mControlID,
						UIAction.UIACTION_MOUSEDOWN, paramBuf);
			}
			break;
		case MotionEvent.ACTION_UP:
			if (m_bUpEvent) {
				byte[] paramBuf = new byte[1];
				paramBuf[0] = 0x00;
				MakeAndSendMessageToModule(mControlID,
						UIAction.UIACTION_MOUSEUP, paramBuf);
			}
			break;
		}
		// return super.onTouchEvent(event);
		return true;
	}

}
