package com.flyaudio.backcar;


import android.content.Context;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.Paint.Align;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.Rect;
import android.graphics.ImageFormat;



import android.util.AttributeSet;
import android.util.Log;

import com.autochips.inputsource.InputSourceClient;
import com.autochips.inputsource.InputSource;
import com.autochips.inputsource.DIGIN;

import com.flyaudio.backcar913.*;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.SystemProperties;
import android.widget.FrameLayout;



public class BackCarView extends SurfaceView {
   
	public final int DIGIN_VDO_W = 1280;
	public final int DIGIN_VDO_H = 720;
	String TAG = "BackCarView";
	private BackCarView  bv = null;
	
    private void init() {

        getHolder().addCallback(mSHCallback);
//        getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//        
//		setZOrderOnTop(true);
//		setZOrderMediaOverlay(true);
		getHolder().setFormat(ImageFormat.YV12);
		getHolder().setFixedSize(DIGIN_VDO_W, DIGIN_VDO_H);
		bv = this;
    }

    public BackCarView(Context context) {
        super(context);

        init();
    }

   public void  setRearMirror(int m){}
    
   public void SetAVINSignalType(int m1, int m2){}

	
     private SurfaceHolder.Callback mSHCallback = new SurfaceHolder.Callback() {
        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
            Log.i(TAG, "mSHCallback - surfaceChanged do nothing, w = " + w + ", h = " + h);
   
    	}

        public void surfaceCreated(SurfaceHolder holder) {
            Log.i(TAG, "mSHCallback - surfaceCreated");
	  				DigIn.getInstance().DigIn_init(holder, bv);
					DigIn.Stop();
					DigIn.setDisplay(getHolder());
					DigIn.Play();

        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            Log.w(TAG, "mSHCallback - surfaceDestroyed");
            DigIn.getInstance().DigIn_deinit();
        }
    };
}
