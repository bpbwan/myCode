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

import com.autochips.backcar.BackCar;
import com.autochips.dvr.DVR;

import android.util.AttributeSet;
import android.util.Log;

import com.autochips.inputsource.InputSourceClient;
import com.autochips.inputsource.InputSource;
import com.autochips.inputsource.DIGIN;
import com.flyaudio.backcarcamera.*;
import com.flyaudio.backcar.util.FlyUtil;
import com.flyaudio.backcar913.*;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.SystemProperties;
import android.widget.FrameLayout;



public class BackCarView extends SurfaceView {
   private final static int BACKCAR_NONE = 0;
   private final static int BACKCAR_USB = 1;
   private final static int BACKCAR_914 = 2;  //or 913
   private final static int BACKCAR_CVB = 3;
   private final static int BACKCAR_CVB_SV = 5;
   
   private final static int Point_Origin_X = 1280;
   private final static int Point_Origin_Y = 720;
   public  int Point_913_180_X = 1152;//1152  
   public  int Point_913_180_Y = 648;//648    
   public  int Point_913_150_X = 960;//1280 - 960
   public  int Point_913_150_Y = 540;//720  - 540
   
   
   public int x_offerset_150 = 0;
   public int y_offerset_150 = 0;
   public int x_offerset_180 = 0;
   public int y_offerset_180 = 0;
   public final int CAMEARE_W = 640;
   public final int CAMEARE_H = 480;

	public final int DIGIN_VDO_W = 1280;
	public final int DIGIN_VDO_H = 720;

	public final int CVB_VDO_W = 720;
	public final int CVB_VDO_H = 480;


	static  int H_MIRROR = 0x20;
	static  int NONE_MIRROR = 0x00;

    private String TAG = "BackCarView";
    private int    mScreenW = 1024;
    private int    mScreenH = 600;
    public boolean mIsRear =false;
    //private int mDistance = -1;
   private Paint  Pt=null;
    boolean mflag = true;
   private DVR mDvr = null;
   public int backcarmode = BACKCAR_CVB;
   public  InputSourceClient mAvinV = null;
   private final static int VIDEO_WIDTH = 800;
   private final static int VIDEO_HEIGHT = 480;

    private boolean usbCamera = false;
    private final static int RECTLEFT =0;
    private final static int RECTTOP = 2;


    private final static int PALWIDTH = 720;
    private final static int PALHEIGHT = 576;
    private int mWidth = PALWIDTH;
    private int mHeight = PALHEIGHT;//default PAL
    public static int oldWidth = 0;
    public static int oldheight = 0;
    private BackCarService mBackCarService = null;
    public  int gBackCarView_913Mode = 0;
    public int FRONT913=1;
    public int REAR913=2;
    public int rearOrFront=0;
	ImgFormat img = ImgFormat.NONE;
	private int layout_Width = -1;
	private int layout_Height = -1;
	private int bHeight= 0;
	private int aHeight= 0;
	boolean bflag =false;
	protected String leftTemp = null;
	boolean flag =false;
	private int oldDigMode = -1;
	private int topLayoutHeight = 0;
	private int bottomLayoutHeight = 0;
	public void setLayoutSize(int w, int h) {
		this.layout_Width = w;
		this.layout_Height = h;
		 Log.d(TAG, " layout_Width "+layout_Width+" layout_Height "+layout_Height);
		}
	 enum ImgFormat
		{
			YV12,
			RGB565,
			NONE
		}

     /* 
     */
	boolean hasStopDigin =false;
	public void  setmFlag(boolean m)
     	{

			mflag = m;

     	}
	public int retCurBackCarMode()
     	{
		 	return backcarmode;
     	}
	 
    public void setImgFormat(int mode)
     {

		Log.d(TAG, "setImgFormat    ======================"+mode);
		switch(mode)
			{
			case BACKCAR_USB:	getHolder().setFixedSize(CAMEARE_W, CAMEARE_H);
								break;
			case BACKCAR_914: 
							img = ImgFormat.YV12;
							getHolder().setFormat(ImageFormat.YV12);
							getHolder().setFixedSize(DIGIN_VDO_W, DIGIN_VDO_H);
							mWidth = DIGIN_VDO_W;
							mHeight = DIGIN_VDO_H;
				break;
			case BACKCAR_CVB_SV:
			case BACKCAR_CVB:	img = ImgFormat.RGB565;
							getHolder().setFixedSize(720, 480);
					oldheight = FlyUtil.GET_SCREEN_H();
					break;
			}
		
		final android.view.ViewGroup.LayoutParams lp = this.getLayoutParams();
		
		if(oldheight != 0)
			lp.height = oldheight;
		
			if(oldWidth  != 0){
				lp.width = oldWidth;
			}
			this.setLayoutParams(lp);
			
			Log.d("DDD", "setImgFormat   "+oldWidth+" oldheight "+oldheight);
     }
	
   public void setRearMirror(int m)
	{
		rearOrFront = m;
	}
   
    public void getBackCarServiceInstance(BackCarService service){
        mBackCarService = service;
		Point_913_150_X = mBackCarService.proxyContext.getInteger("video150_x", Point_913_150_X);
		Point_913_150_Y = mBackCarService.proxyContext.getInteger("video150_y", Point_913_150_Y);
		Point_913_180_X = mBackCarService.proxyContext.getInteger("video180_x", Point_913_180_X);
		Point_913_180_Y = mBackCarService.proxyContext.getInteger("video180_y", Point_913_180_Y);
		x_offerset_150 = mBackCarService.proxyContext.getInteger("x_offerset_150", x_offerset_150);
		y_offerset_150 = mBackCarService.proxyContext.getInteger("y_offerset_150", y_offerset_150);
		x_offerset_180 = mBackCarService.proxyContext.getInteger("x_offerset_180", x_offerset_180);
		y_offerset_180 = mBackCarService.proxyContext.getInteger("y_offerset_180", y_offerset_180);
		Log.d(TAG,"150x " + Point_913_150_X + " 150y " + Point_913_150_Y + 
			" 180x = " + Point_913_180_X + " 180y " + Point_913_180_Y);
      }
	
    private void init() {
		bflag = false;

        getHolder().addCallback(mSHCallback);
        getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
     
    }
	
    public void setDvr(DVR dvr){
        mDvr = dvr;
    }
	
  public void setBackcarMode(int flag){
		  backcarmode = flag;
	  }
  
 public void SetAVINSignalType(int x, int y)
	  {
	 Log.d(TAG,"SetAVINSignalType x = " + x + " y= " + y + " mWidth = " + mWidth + " mHeight = " + mHeight);
		  if (mWidth == x && mHeight == y) {
			  Log.i(TAG,"SetAVINSignalType : resolution have not been changed!");
			  } else {
			  mWidth = x;
			  mHeight = y;
		//	  holder.setFixedSize(mWidth, mHeight);
				getHolder().setFixedSize(mWidth,mHeight);
				 Log.i(TAG,"resolution have been changed!");
			   }
	  }

 
  public void setInputClient(InputSourceClient avin)
  {
	  mAvinV = (DIGIN)avin;
  }

    private Bitmap getResource(int id, int alpha) {
        Drawable drawBitmap = mContext.getResources().getDrawable(id);
        Bitmap bmp = DrawableTobitmap(drawBitmap);
     
        return bmp;
    }

	public Bitmap DrawableTobitmap(Drawable m_Drawable) {
		// TODO Auto-generated method stub
		if (m_Drawable != null)
			return ((BitmapDrawable) m_Drawable).getBitmap();

		Log.i(TAG, "m_Drawable is NULL !");
		
		return null;
	}    
    
    public BackCarView(Context context) {
        super(context);

        init();
    }

    public BackCarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        init();
    }

    public BackCarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        
        init();
    }
	
   public void setBackCarView(boolean flag)
   	{
		if(flag)
			this.setVisibility(View.VISIBLE);
		else this.setVisibility(View.INVISIBLE);
   	}
   
    public void update(int dist) {
    }
	
   
    protected void onDraw(Canvas canvas) {

    }
	
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		//Log.i(TAG, "BackCarView - onTouchEvent");
		super.onTouchEvent(event);
		return true;
	}


boolean test = true;
	public void SetHodlerFixSize(int w, int h){
		synchronized(this){
			if(test)
			getHolder().setFixedSize(DIGIN_VDO_W, DIGIN_VDO_H-1);
			else 
				getHolder().setFixedSize(DIGIN_VDO_W, DIGIN_VDO_H);
			test = !test;
			//invalidate();
			}
		//this.setVisibility(View.GONE);
		Log.d("DDD", "w "+w);

		
		}
	
	

	
	public void reSizeSurfaceView(int width, int height){
		//final android.view.ViewGroup.LayoutParams lp = this.getLayoutParams();
		FrameLayout.LayoutParams lp =(FrameLayout.LayoutParams)this.getLayoutParams();
		Log.d("DDD", "reSizeSurfaceView "+width+" "+height+"lp old w "+lp.width+" lp old h "+lp.height);
			if(width>0){
				lp.width = width;
				oldWidth = width;
			}
			if(height>0){
				lp.height = height;
				oldheight = height;
			}
			
			this.setLayoutParams(lp);

			switch(backcarmode)
			{
			case BACKCAR_NONE: break;
			case BACKCAR_USB: 	 
							break;
			case BACKCAR_914: 	getHolder().setFixedSize(DIGIN_VDO_W, DIGIN_VDO_H-1);
							break;
			case BACKCAR_CVB_SV:
			case BACKCAR_CVB: 	getHolder().setFixedSize(CVB_VDO_W, CVB_VDO_H-1);
							break;
			default:
					break;
			}
			
		}

	

	void fixWindow(){
		FrameLayout.LayoutParams lp =(FrameLayout.LayoutParams)this.getLayoutParams();
		 
				final View botLayout= mBackCarService.getFlyBackCarMainView()
					.findChildViewWithTag(BackCarTag.VIDEOBOTTOMBTN);
		
				final View topLayout = mBackCarService.getFlyBackCarMainView()
					.findChildViewWithTag(BackCarTag.VIDEOTOP);//FlyBaseListener.mfindViewWithTag(BackCarTag.VIDEOTOP);
				
				if(botLayout != null){
					Log.d("DDD", "botLayout  measure "+botLayout.getHeight());
					 if(botLayout.getHeight() == 0){
					 
					 	botLayout.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					 	}else 
					 		bottomLayoutHeight = botLayout.getHeight()-2;
					    
						lp.height = layout_Height - bottomLayoutHeight; 		
					}
				Log.d("DDD", "lp old w "+lp.width+" lp old h "+lp.height);
				 if(topLayout != null){
				 	//���м��ʻ�ȡ��getHeight �� 0 ������measure ûЧ�� bug(�̶��߶Ƚ��)
					 Log.d("DDD", "topLayout  measure "+topLayout.getHeight());
							 if(topLayout.getHeight() == 0) {
							 	
							 	//topLayout.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
							 	if(backcarmode == BACKCAR_914)
								 	topLayoutHeight = mBackCarService.DigInVdoTopMargin;
									else topLayoutHeight = topLayout.getHeight();
							 	}
							 	else 
									topLayoutHeight = topLayout.getHeight();

								
								lp.height = lp.height - topLayoutHeight;
								lp.topMargin = topLayoutHeight;						
				}
		
				 //����÷����Ļ�����Ƶȫ����
					 if(layout_Width>0 && !mBackCarService.needHalfScreen)
						lp.width = layout_Width;
					 
					 if(lp.height>0){
							oldheight = lp.height;
						}	 
			
				Log.d("DDD", "fixWindow  w "+lp.width+" h "+lp.height);
				this.setLayoutParams(lp);


		}

	
	void setDigInVideoScale(){
		// android.view.ViewGroup.LayoutParams lp = this.getLayoutParams();
		fixWindow();
		getHolder().setFixedSize(DIGIN_VDO_W, DIGIN_VDO_H);
		mBackCarService.showFloatApp(false, 0);// yingcan���

	}
	

	void setCVBVideoScale(){
		if( !mBackCarService.hasDigInVideo)
			return;
		android.view.ViewGroup.LayoutParams lp = this.getLayoutParams();
		if(layout_Height >0 && !mBackCarService.needHalfScreen)
		lp.height = layout_Height;
		if(layout_Width>0 && !mBackCarService.needHalfScreen)
			 	lp.width = layout_Width;
		this.setLayoutParams(lp);
		Log.d("DDD", "setCVBVideoScale");
		fixWindow();
		
		getHolder().setFixedSize(CVB_VDO_W, CVB_VDO_H);
		
		mBackCarService.showFloatApp(false, 0);// yingcan���
	}
	
	public void setSourceRect()
	{

		if(mAvinV!=null)
		{		Log.d(TAG,"setSourceRect mode"+rearOrFront);
			if(rearOrFront==REAR913)
			mAvinV.setMirror(H_MIRROR);
			else if(rearOrFront==FRONT913)
			mAvinV.setMirror(NONE_MIRROR);
				
			int x_150 = (Point_Origin_X - Point_913_150_X)/2;
			int y_150 = (Point_Origin_Y - Point_913_150_Y)/2;
			mAvinV.setSourceRect(InputSource.DEST_TYPE_FRONT, x_150+x_offerset_150, y_150+y_offerset_150
					, mWidth-x_150*2+x_offerset_150, mHeight-y_150*2+y_offerset_150);	
		
		}
	}
	
	public void setflag(boolean b)
		{	
			flag = b;
		}

	public void set913SourceRect(int mode){
		Log.d(TAG,"set913SourceRect mode = " + mode + " gBackCarView_913Mode = " + gBackCarView_913Mode+"rearOrFront= "+rearOrFront);
		gBackCarView_913Mode = mode;
		if(mAvinV == null){
			Log.d(TAG," set913SourceRect mAvinV == null");
			return;
		}
		
		if(oldDigMode != mode)
			oldDigMode = mode;
		else {	
				return;
			}
		Log.i("DDD", "gBackCarView_913Mode  ="+gBackCarView_913Mode);
		if(mode !=0){
		if(rearOrFront==REAR913)
			mAvinV.setMirror(H_MIRROR);
		else if(rearOrFront==FRONT913)
			mAvinV.setMirror(NONE_MIRROR);
		}
		
		//if(!flag) return;
		switch(mode){//gBackCarView_913Mode
		case 1: //180
			
			DigIn.Stop();
			gBackCarView_913Mode = 1;

			DigIn.setDisplay(getHolder());
			DigIn.Play();
			int x_180 = (Point_Origin_X - Point_913_180_X)/2;
			int y_180 = (Point_Origin_Y - Point_913_180_Y)/2;
		

			toastStr("180  video "+Point_913_180_X+"*"+Point_913_180_Y);
			mAvinV.setSourceRect(InputSource.DEST_TYPE_FRONT, x_180+x_offerset_180, y_180+y_offerset_180
					, mWidth-x_180*2+x_offerset_180, mHeight-y_180*2+y_offerset_180);

			break;
		case 2://150
			DigIn.Stop();
			gBackCarView_913Mode = 2;
			
			DigIn.setDisplay(getHolder());
			DigIn.Play();
			int x_150 = (Point_Origin_X - Point_913_150_X)/2;
			int y_150 = (Point_Origin_Y - Point_913_150_Y)/2;
			toastStr("150  video "+Point_913_150_X+"*"+Point_913_150_Y);

			mAvinV.setSourceRect(InputSource.DEST_TYPE_FRONT, x_150+x_offerset_150, y_150+y_offerset_150
					, mWidth-x_150*2+x_offerset_150, mHeight-y_150*2+y_offerset_150);	


			break;
		}

	}

	void toastStr(String str)
		{
			mBackCarService.getBackCarModule().showText(0x00070702,str);
		}

	public void DestroySurfaceView(){
		Log.d(TAG,"DestroySurfaceView");
		//getHolder().setFixedSize(0, 0);
		this.destroyDrawingCache();
		this.setVisibility(View.GONE);
	}



	public void  deinitDigIn()
		{
			BackCar913Service.getInstance().DigIn_deinit();
		}

     private SurfaceHolder.Callback mSHCallback = new SurfaceHolder.Callback() {
        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
            Log.i(TAG, "mSHCallback - surfaceChanged do nothing, w = " + w + ", h = " + h);
		switch(backcarmode)
		{
		case BACKCAR_NONE: break;
		case BACKCAR_USB: BackCarUsbCamera.getUsbCm().StartCameraPreView(holder);
						break;
		case BACKCAR_914: //holder.setFixedSize(mWidth, mHeight);
				setDigInVideoScale();
				
				break;
		case BACKCAR_CVB_SV:
		case BACKCAR_CVB: 
			setCVBVideoScale();
	/*
					if(mflag){
							try{
								Canvas canvas = holder.lockCanvas();
								if(canvas!=null)
								holder.unlockCanvasAndPost(canvas);	
								Log.i(TAG, "lockcavans");
							}
						catch (Exception  e)
							{
							Log.i(TAG, "Exception "+e);

								}
								}
		*/
							break;
		default: break;
		}
    	}

        public void surfaceCreated(SurfaceHolder holder) {
            Log.i(TAG, "mSHCallback - surfaceCreated");
	  Log.i(TAG, "BackCarMode is [ "+backcarmode+" ]");
            Surface surface = holder.getSurface();
            if (null != surface) {
		switch(backcarmode)
			{
			case BACKCAR_NONE: break;
			case BACKCAR_USB: 
			//				BackCarUsbCamera.getUsbCm().OpenCamera(holder);
			//				usbCamera = true;
							holder.setFixedSize(CAMEARE_W, CAMEARE_H);
					//		BackCarUsbCamera.getUsbCm().StartCameraPreView(holder);
							break;
			case BACKCAR_914:  
				if(img != ImgFormat.YV12)
				{	
					img = ImgFormat.YV12;
					holder.setFormat(ImageFormat.YV12);
				}
				else {
					holder.setFixedSize(DIGIN_VDO_W, DIGIN_VDO_H);
					BackCar913Service.getInstance().DigIn_init(holder);
					setSourceRect();
					}
					break;
			case BACKCAR_CVB_SV:
			case BACKCAR_CVB: 
				//if(img != ImgFormat.RGB565)
					//{
						img = ImgFormat.RGB565;
					//	holder.setFormat(ImageFormat.RGB_565);
					//}
					//else
					BackCar.setVideoSurface(surface); 
								break;
			default: break;
			}
            }
			
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            Log.w(TAG, "mSHCallback - surfaceDestroyed");
			
	switch(backcarmode)
	{
		case BACKCAR_NONE: break;
		case BACKCAR_USB: 	 
		//					BackCarUsbCamera.getUsbCm().CloseCamera();
							getHolder().getSurface().release();
							break;
		case BACKCAR_914: 	//hasStopDigin = false;
							BackCar913Service.getInstance().DigIn_deinit();
							oldDigMode = -1;
							mAvinV = null;
							break;
		case BACKCAR_CVB_SV:
		case BACKCAR_CVB: BackCar.setVideoSurface(null);
							break;
		default:
			break;
	}
	mBackCarService.syncFloatExitVideo();

        }
    };
}
