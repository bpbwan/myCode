package com.flyaudio.object;

import com.flyaudio.backcar.R;
import com.flyaudio.globaldefine.UIAction;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.graphics.drawable.Drawable;
import com.flyaudio.backcar.modules.BaseModule;
import com.flyaudio.backcar.util.FlyUtil;
import android.graphics.Canvas;

public class FlyMultiEnumGaugeObj extends FlyEnumGaugeObj {
	// TPMS
	final String TAG = "BackCarActivity";
	final int DrawableSum = 4;
	protected Drawable mDrawableThums[] = new Drawable[4];
	protected int m_CurrentIndex = 0;

	private byte[] lock = new byte[0];
	public FlyMultiEnumGaugeObj(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.FlyMultiEnumGaugeObj);
		mDrawableThums[0] = a
				.getDrawable(R.styleable.FlyMultiEnumGaugeObj_drawableThumb0);
		mDrawableThums[1] = a
				.getDrawable(R.styleable.FlyMultiEnumGaugeObj_drawableThumb1);
		mDrawableThums[2] = a
				.getDrawable(R.styleable.FlyMultiEnumGaugeObj_drawableThumb2);
		mDrawableThums[3] = a
				.getDrawable(R.styleable.FlyMultiEnumGaugeObj_drawableThumb3);
		a.recycle();

	}


	
	public void setBackgroundIndex(int level) {
	

		
		final int index = BaseModule.getBaseModule().GetChangeMultiRadarUi(mControlID, level);
		
		setBackgroundDrawableIndex(index);

		super.setBackgroundIndex(level);
	}

	public void setBackgroundDrawableIndex(int index) {
			if(m_CurrentIndex == index)
				return ;
			if (index < 0 || index >= DrawableSum)
				return ;
			
			m_CurrentIndex = index;
			if (mDrawableThums[m_CurrentIndex] == null)
				return ;
			setBackground(mDrawableThums[m_CurrentIndex]);
			m_Data = -1;

	}

}
