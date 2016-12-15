package com.flyaudio.object;

import com.flyaudio.backcar.R;
import com.flyaudio.globaldefine.UIAction;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class FlyEnumGaugeObj extends FlyUIObj {
	// TPMS
	final String TAG = "BackCarActivity";
	protected int m_Data = 0;

	protected int direct = 0;
	protected boolean m_bTouchable = true;
	protected int m_Size = 2;
	
	private float x = 0, y = 0;

	public FlyEnumGaugeObj(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.FlyEnumGaugeObj);
		m_bTouchable = a.getBoolean(
				R.styleable.FlyEnumGaugeObj_FlyEnumGauge_touchAble, true);
		m_Size = a.getInt(R.styleable.FlyEnumGaugeObj_FlyEnumGauge_size, 0);
		direct = a.getInt(R.styleable.FlyEnumGaugeObj_FlyEnumGauge_direct, 0);

		a.recycle();
	}

	public void ChangeDrawableColor(int data){
		
	}
	
	public void setBackgroundIndex(int level) {

		if (level < 0)
			return;
		if (m_Size > 0 && level > m_Size)
			return;
		if(m_Data != level){
		m_Data = level;
		

		setBackGroud(m_Data);

		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		Log.i(TAG, "FlyEnumGaugeObj - onTouchEvent");
		super.onTouchEvent(event);

		int tempPos = 0;
		x = event.getX();
		y = event.getY();

		if (m_bTouchable == false ) {
			return true;
		}
		if(m_Size == 0)
			return true;

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN: {
			tempPos = GetPos(direct, x, y);
			if (tempPos == m_Data)
				return true;

			m_Data = tempPos;
			getBackground().setLevel(m_Data);
			invalidate();
			break;
		}

		case MotionEvent.ACTION_MOVE: {
			tempPos = GetPos(direct, x, y);
			if (tempPos == m_Data)
				return true;

			m_Data = tempPos;
			getBackground().setLevel(m_Data);
			invalidate();
			break;
		}

		case MotionEvent.ACTION_UP: {
			tempPos = GetPos(direct, x, y);
			if (tempPos != m_Data) {
				m_Data = tempPos;
				getBackground().setLevel(m_Data);
				invalidate();
			}

			byte[] paramBuf = new byte[4];
			paramBuf[0] = (byte) ((tempPos >> 24) & 0xff);
			paramBuf[1] = (byte) ((tempPos >> 16) & 0xff);
			paramBuf[2] = (byte) ((tempPos >> 8) & 0xff);
			paramBuf[3] = (byte) (tempPos & 0xff);
			MakeAndSendMessageToModule(mControlID, UIAction.UIACTION_MOUSEUP,
					paramBuf);
			break;
		}
		default:
			break;
		}
		return true;
	}

	// =============================================================================

	private int GetPos(int m_direct, float x, float y) {
		// TODO Auto-generated method stub
		int GaugeSize = 0;
		

		
		int tempPos = 0;
		switch (m_direct) {
		case 0: // 涓�->涓�
			GaugeSize = this.getHeight();
			if (y < 0)
				y = 0;
			if (y > GaugeSize)
				y = GaugeSize;
			tempPos = (int) y / (int) (GaugeSize / m_Size);
			if (tempPos > m_Size)
				tempPos = m_Size;
			// Log.d("Kevin",
			// "direct=0,height:"+GaugeSize+" y:"+y+" m_Size:"+m_Size+"pos:"+tempPos);

			break;
		case 1: // 涓�->涓�
			GaugeSize = this.getHeight();
			if (y < 0)
				y = 0;
			if (y > GaugeSize)
				y = GaugeSize;
			tempPos = m_Size - (int) y / (int) (GaugeSize / m_Size);
			if (tempPos > m_Size)
				tempPos = m_Size;
			if (tempPos < 0)
				tempPos = 0;
			// Log.d("Kevin",
			// " direct=1,height:"+GaugeSize+" y:"+y+" m_Size:"+m_Size+"pos:"+tempPos);
			break;
		case 2:// 宸� -> 鍙�
			GaugeSize = this.getWidth();
			if (x < 0)
				x = 0;
			if (x > GaugeSize)
				x = GaugeSize;
			tempPos = (int) x / (GaugeSize / m_Size);
			if (tempPos > m_Size)
				tempPos = m_Size;
			break;
		case 3:// 鍙� -> 宸�
			GaugeSize = this.getWidth();
			if (x < 0)
				x = 0;
			if (x > GaugeSize)
				x = GaugeSize;
			tempPos = m_Size - (int) x / (GaugeSize / m_Size);
			if (tempPos > m_Size)
				tempPos = m_Size;
			if (tempPos < 0)
				tempPos = 0;
			break;

		default:
			break;
		}
		return tempPos;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		// getBackground().setLevel(m_Data);
	}
	
	
protected Bitmap trueColorDrawable( int Color){
	Bitmap bitmap  = mDrawableToBitamp(getBackground());
	if(bitmap == null)
		return null;
	
	for(int x = 0; x < bitmap.getWidth(); x++)
		for(int y= 0; y<bitmap.getHeight(); y++){
			int color = bitmap.getPixel(x, y);
		if(  color != 0 ){
			int aphla = color & 0xFF000000;
			   color = aphla | Color;
				bitmap.setPixel(x, y, color);
			}
		}
	return bitmap;
	}

protected Bitmap trueColorDrawable(Drawable de,  int Color){
	Bitmap bitmap  = mDrawableToBitamp(de);
	if(bitmap == null)
		return null;
	
	for(int x = 0; x < bitmap.getWidth(); x++)
		for(int y= 0; y<bitmap.getHeight(); y++){
		if( bitmap.getPixel(x, y) != 0 ){
				bitmap.setPixel(x, y, Color);
			}
		}
	return bitmap;
	}
}
