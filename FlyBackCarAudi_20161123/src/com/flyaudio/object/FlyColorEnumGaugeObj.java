package com.flyaudio.object;

import com.flyaudio.backcar.util.FlyUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;

public class FlyColorEnumGaugeObj   extends FlyEnumGaugeObj{

	private boolean changeDrawable = false;
	private int ChangeColor = 0x00FF0000;
	Drawable SaveDrawable  = null;
	public FlyColorEnumGaugeObj(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		SaveDrawable = getBackground();
	}
	
	public void setBackgroundIndex(int level) {
		if(SaveDrawable != null)
			setBackground(SaveDrawable);
		else 
			SaveDrawable = getBackground();
		try {
			getBackground().setLevel(level);
		} catch (Exception e) {
		}
		
		if(changeDrawable) {
			Bitmap bmp = trueColorDrawable(ChangeColor);
			if(bmp != null){
	//			Log.d("DDD", "setBackgroundIndex  "+mControlID+"     "+changeDrawable);
			Drawable dr =new BitmapDrawable(FlyUtil.getReSource(), bmp);
				this.setBackground(dr);
			}
			
		} 

			
	}
	
	public void ChangeDrawableColor(int data){
	
		if(data == 1)
			changeDrawable = true;
		else 
			changeDrawable = false;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub

		

			super.onDraw(canvas);
	}
	
}
