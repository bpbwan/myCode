package com.flyaudio.object;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Canvas;

import com.flyaudio.backcar.MsgType;
import com.flyaudio.backcar.R;
import com.flyaudio.globaldefine.UIAction;
import com.flyaudio.keyevent.FlyKeyCode;
import com.flyaudio.backcar.BackCarService;
import com.flyaudio.backcar.util.FlyUtil;
import com.flyaudio.backcar913.FlyBaseListener;
import com.flyaudio.backcar913.BackCarTag;
import com.flyaudio.backcar.BackCarService;

import android.view.MotionEvent;
import android.view.KeyEvent;
import android.view.View.OnKeyListener;
import android.graphics.Rect;

public class FlyRotateObj extends FlyUIObj implements View.OnClickListener,
		OnKeyListener {
	final String TAG = "BackCarActivity";

	private float minRotateValue = 0f;
	private float maxRotateValue = 360f;
	private int RotateNum = 1;
	private float CurrentRotate = 0.0f;
	private float perRotate = 0.0f;

	private int centernX = 0;
	private int centernY = 0;
	private int curx = 0;
	private int cury = 0;
	
	public FlyRotateObj(Context context, AttributeSet attrs) {
		super(context, attrs);

		TypedArray a = getContext().obtainStyledAttributes(attrs,
				R.styleable.FlyRotateObj);
		minRotateValue = a.getFloat(R.styleable.FlyRotateObj_minRotateValue,
				minRotateValue);
		maxRotateValue = a.getFloat(R.styleable.FlyRotateObj_maxRotateValue,
				maxRotateValue);
		RotateNum = a.getInt(R.styleable.FlyRotateObj_RotateNum, RotateNum);
		CurrentRotate = a.getFloat(R.styleable.FlyRotateObj_currentRotate,
				CurrentRotate);
		perRotate = (float) (maxRotateValue - minRotateValue) / RotateNum;
		a.recycle();

		// Log.d("DDD",
		// "FlyRotateObj CurrentRotate "+CurrentRotate+" minRotateValue "+minRotateValue+
		// " maxRotateValue "+maxRotateValue+" perRotate "+perRotate);

		this.setOnClickListener(this);
		this.setOnKeyListener(this);
		
	}

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		RotateObj();
		if (m_bTouchable)
			this.requestFocus();
		int[] location = new int[2];
		getLocationInWindow(location);
		centernX =this.getLeft()+getMeasuredWidth()/2;
		centernY =this.getTop()+getMeasuredHeight()/2;


	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		Bundle data = new Bundle();
		data.putString(BackCarTag.STRKEY, "onClick");
		BackCarService.mBackCarService.MakeAndSendMessageWithBundle(
				MsgType.FLYOBJ, mControlID, data);
	}

	public boolean onKey(View v, int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub

		final int action = event.getAction();
		final boolean handleKeyEvent = (action != KeyEvent.ACTION_UP);
		if (!handleKeyEvent)
			return false;
		// Log.d("DDD", "FlyRotateObj onKey  --"+keyCode);
//		Log.d("DDD", "FlyRotateObj onKey  --" + " " + CurrentRotate + "  "
//				+ minRotateValue + " " + perRotate);
		boolean ret = false;
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			BackCarService.mBackCarService.MakeAndSendMessageWithBundle(
					MsgType.COMMON, BackCarTag.BACKKEYEVENT, null);
			break;
		case FlyKeyCode.FLYKEY_LEFT:
			if ((CurrentRotate - perRotate) >= minRotateValue)
				CurrentRotate = FlyUtil.SubBigDecimal(CurrentRotate, perRotate);
			RotateObj();
			MakeAndSendMessageToBaseModule();
			ret = true;
			break;

		case FlyKeyCode.FLYKEY_RIGHT:
			if ((CurrentRotate + perRotate) <= maxRotateValue) {
				CurrentRotate = FlyUtil.AddBigDecimal(CurrentRotate, perRotate);
			}
			RotateObj();
			MakeAndSendMessageToBaseModule();
			ret = true;
			break;
		case FlyKeyCode.FLYKEY_LEFT_DOWN:
			break;
		case FlyKeyCode.FLYKEY_RIGHT_DOWN:
			MakeAndSendKeyCodeToBaseModule(FlyKeyCode.FLYKEY_RIGHT_DOWN);
			break;
		case FlyKeyCode.FLYKEY_LEFT_UP:
			break;
		case FlyKeyCode.FLYKEY_RIGHT_UP:
			break;
		}
		return ret;

	}

	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		if (!m_bTouchable)
			return false;
		Bundle data = new Bundle();
		switch (event.getAction()) {
		case MotionEvent.ACTION_CANCEL:
			Log.d("DDD", "onTouchEvent ACTION_CANCEL");
			break;

		case MotionEvent.ACTION_DOWN:

			final int  curY =  (int) -(event.getRawY() - centernY);
			final int  curX =  (int) (event.getRawX() - centernX);
			float rot = (float) Math.atan((float)curY/curX);
			float Degrees = Math.abs((float) Math.toDegrees(rot));
			if(curY >= 0 &&curX >=0){ //1
				Degrees=(270-Degrees);
			}else if(curY >= 0 &&curX <=0){ //2
				Degrees=(90+Degrees);
			}else if(curY <= 0 &&curX <=0) { //3
				Degrees = 90 - Degrees;
			}else if(curY <= 0 &&curX >=0){ //4
				Degrees = 270+ Degrees;
			}
			
			if(Degrees <= minRotateValue){
				CurrentRotate = minRotateValue;
				RotateObj();
			}else if(Degrees >= maxRotateValue){
				CurrentRotate = maxRotateValue;
				RotateObj();
			}else {
				final int p = (int) ( FlyUtil.SubBigDecimal(Degrees, minRotateValue)/ perRotate+ 1);
				SetCurrentRotate(p);
			}

			MakeAndSendMessageToBaseModule();
//			Log.d("TTT","rot "+Degrees);
			break;
		case MotionEvent.ACTION_UP:
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		default:
			break;
		}
		return true;
	}
	
	void MakeAndSendMessageToBaseModule() {
		Bundle data = new Bundle();
//		Log.d("DDD", "FlyRotateObj onKey  --" + " " + CurrentRotate + "  "
//		+ minRotateValue + " " + perRotate);
		final int level = (int) ( FlyUtil.SubBigDecimal(CurrentRotate, minRotateValue)/ perRotate+ 1);
//		Log.d("DDD", "FlyRotateObj onKey  -2-" + " " + CurrentRotate + "  "
//		+ minRotateValue + " " + perRotate+"  "+level);
		data.putInt(BackCarTag.INTKEY, level);
		data.putString(BackCarTag.STRKEY, "onKey");
		BackCarService.mBackCarService.MakeAndSendMessageWithBundle(
				MsgType.FLYOBJ, mControlID, data);
	}

	void MakeAndSendKeyCodeToBaseModule(int keycode) {
		Bundle data = new Bundle();
		data.putInt(BackCarTag.INTKEY, keycode);
		data.putString(BackCarTag.STRKEY, "onFlyKey");
		BackCarService.mBackCarService.MakeAndSendMessageWithBundle(
				MsgType.FLYOBJ, mControlID, data);
	}

	public void SetCurrentRotate(int level) {
		if (level <= (RotateNum + 1)) {
			float data = FlyUtil
					.MultiBigDecimal((float) (level - 1), perRotate);
	
			CurrentRotate = FlyUtil.AddBigDecimal(minRotateValue, data);
			if (CurrentRotate < minRotateValue)
				CurrentRotate = minRotateValue;
//			Log.d("DDD", "FlyRotateObj onKey  -3-" + " " + CurrentRotate + "  "
//					+ minRotateValue + " " + perRotate);
			RotateObj();
		}
	}

	public void RotateObj() {
		// Log.d("DDD", "FlyRotateObj RotateObj "+CurrentRotate);
		if (minRotateValue <= CurrentRotate && CurrentRotate <= maxRotateValue)
			this.setRotation(CurrentRotate);
	}
}
