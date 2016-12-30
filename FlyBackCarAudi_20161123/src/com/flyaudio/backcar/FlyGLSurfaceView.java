package com.flyaudio.backcar;

import android.content.Context;
import android.content.res.Resources;
import android.opengl.GLSurfaceView;
import android.view.SurfaceHolder;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.util.Log;
import android.util.AttributeSet;

import com.flyaudio.backcar.trackbitmap.TrackBitmap.TrackBitmapCallbacks;
import com.flyaudio.backcar.trackbitmap.*;

import android.view.View;

public class FlyGLSurfaceView extends GLSurfaceView implements
		TrackBitmapCallbacks {

	final static int QUEUESIZE = 5;
	private String TAG = "BackCarGLSurfaceView";
	TrackBitmap mTrackBitmap = new TrackBitmap();
	PluginProxyContext mPluginProxyContext;
	BackCarTrack mTrack;
	boolean setMode = false;
	TrackGLRender mTrackGLRender;
	public FlyGLSurfaceView(Context context) {
		super(context);

		init(BackCarService.mBackCarService);
	}

	public FlyGLSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);

		init(BackCarService.mBackCarService);
	}

	private void init(Context context) {
		mTrack = BackCarTrack.getInstance();
		mTrackBitmap.setupCallback(this);
		setEGLConfigChooser(8, 8, 8, 8, 16, 0);

		
	
		// setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
		getHolder().setFormat(PixelFormat.TRANSLUCENT);
		setZOrderOnTop(true);
		setZOrderMediaOverlay(true);
		
		mTrackGLRender = new TrackGLRender(context, mTrackBitmap);
		setRenderer(mTrackGLRender);
		setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

	
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		// delet textture��
		if (TrackGLRender.mTrackGLRender != null)
			TrackGLRender.mTrackGLRender.destroyTexture();
	}

	public void setTrackWH(int trackw, int trackh) {
		mTrackBitmap.setTrackWH(trackw, trackh);
	}

	public void setup(PluginProxyContext m_pluginProxyContext, int w, int h) {
		this.mPluginProxyContext = m_pluginProxyContext;
		mTrackBitmap.setup(m_pluginProxyContext, w, h);
	}

	public void setTrailMode(int mode) {
		mTrackBitmap.setTrailMode(mode);
		
	}

	public boolean isSetMode() {
		return setMode;
	}

	public void sendParam(byte cmd, int len, float data) {
		mTrack.sendParam(cmd, len, data);
		if (cmd == 0x14)
			setMode = !setMode;
		Log.i(TAG, "sendParam command :" + cmd);

	}

	public void setDraw(boolean isdraw) {
			Log.d("GL", "setDraw "+isdraw);
		if (isdraw) {
			mTrackBitmap.startDrawing();
			if (TrackGLRender.mTrackGLRender != null)
				TrackGLRender.mTrackGLRender.Resume();
			setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
		} else {
			if (TrackGLRender.mTrackGLRender != null)
				TrackGLRender.mTrackGLRender.Pause();
			setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
			this.requestRender();
			mTrackBitmap.stopDrawing();
			
		}
	}

	public void bitmapQueueClear(){
		mTrackBitmap.bitmapQueueClear();
	}
	public void reRender() {
		this.requestRender();
	}

	public void update(int dist, int angle) {
		mTrackBitmap.update(angle);
	}

	public void update(double angle) {
		mTrackBitmap.update(angle);
	}

	public void needChangeParamJni(int trackw, int trackh) {
		mTrackBitmap.needChangeParamJni(trackw, trackh);
	}

	public void onPause() {
		super.onPause();
		mTrackBitmap.onPause();
	}

	public void onResume() {
		super.onResume();
		mTrackBitmap.onResume();
	}
	
	public void setFrameNullTime(int delay){
		mTrackGLRender.setFrameNullTime(delay);
	}
	
	public void requestSyncAngle(){
		mTrackBitmap.requestSyncAngle();
	}
	
	public Bitmap getCurrentBmp(){
		return mTrackBitmap.getCurrentBmp();
	}
	
}
