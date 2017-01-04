package com.android.MyWallPaper;

import java.io.File;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;

import android.R;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.animation.AccelerateDecelerateInterpolator;

public class MyWallPaper extends WallpaperService {

	private final int FaceSize = 6;
	private final int MinSpeed = 30;
	private final float Touch_XOffset = 0;
	private boolean Rigth = true;
	private Bitmap wallpaper_escape1 = null;
	private Bitmap bmp = null;
	private int DrawSpeed = MinSpeed;
	private String PackageName ="com.android.FileBrowser";
	private static String uri_src= null;
	Handler myHandler = new Handler();

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		

		MyWallPaperReceiver myReceiver = new MyWallPaperReceiver();
		IntentFilter  myfilter = new IntentFilter();
		myfilter.addAction("com.android.intent.action.SETWALLPAPER");
		registerReceiver(myReceiver, myfilter);
		

	}

	@Override
	public Engine onCreateEngine() {
		// TODO Auto-generated method stub
		return new MyEngine();
	}

	class MyWallPaperReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context arg0, Intent intent) {
			// TODO Auto-generated method stub
			Log.d("AAAA", "onReceive   ");
			if(intent.getAction().equals("com.android.intent.action.SETWALLPAPER")){
				 uri_src = intent.getStringExtra("data");
			
//				File f = new File(uri_src);
				bmp = BitmapFactory.decodeFile(uri_src);

//				
			Log.d("AAAA", "onReceive     "+uri_src);
			}
			
		}
		
	}
	
	
	
	class MyEngine extends Engine {

		float Offset_X = 0.0f;
		float Offset_Y = 0.0f;
		float Offset_Z = 0.0f;

		float Touch_x = 0;

		@Override
		public void onDestroy() {
			Log.d("AAAA", "onDestroy");
			// TODO Auto-generated method stub
			super.onDestroy();
		}

		private final Runnable mDrawFrame = new Runnable() {
			public void run() {
				DrawAFrame();
			}
		};

		@Override
		public void onCreate(SurfaceHolder surfaceHolder) {
			Log.d("AAAA", "onCreate");
			// TODO Auto-generated method stub
			super.onCreate(surfaceHolder);
			
			int id = getResources().getIdentifier("bg_cling", "drawable",
					PackageName);
			if (id != 0) {
//				BitmapDrawable db = (BitmapDrawable) getResources()
//						.getDrawable(id);
			 bmp = BitmapFactory.decodeResource(getResources(), id);
			}
			id = getResources().getIdentifier("wallpaper_escape1", "drawable",
					PackageName);
			
			if(id != 0)
				wallpaper_escape1= BitmapFactory.decodeResource(getResources(), id);
			
			if(uri_src!=null){
				bmp = BitmapFactory.decodeFile(uri_src);
				if(bmp.getWidth() > 200 || bmp.getHeight() >200){
					
				}
			}
		}

		@Override
		public void onTouchEvent(MotionEvent event) {
			// TODO Auto-generated method stub
			super.onTouchEvent(event);
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				Touch_x = event.getX();
				break;
			case MotionEvent.ACTION_MOVE:
				// Log.d("AAA", "  "+event.getX());
				if ((event.getX() - Touch_x) > 0) {
					Rigth = true;
				} else {
					Rigth = false;
				}
				// Log.d("AAAA", "  "+(event.getX() - Touch_x));
				Touch_x = event.getX();
				DrawSpeed = 150;
				break;
			case MotionEvent.ACTION_UP:
				myHandler.postDelayed(DrawSpeedRunnable, 10);
				break;

			}
		}

		private final Runnable DrawSpeedRunnable = new Runnable() {
			public void run() {
				DrawSpeed--;
				if (DrawSpeed < MinSpeed)
					DrawSpeed = MinSpeed;
				else
					myHandler.postDelayed(DrawSpeedRunnable, 10);
			}

		};

		@Override
		public void onSurfaceCreated(SurfaceHolder holder) {
			Log.d("AAAA", "onSurfaceCreated");
			// TODO Auto-generated method stub
			super.onSurfaceCreated(holder);
		}

		@Override
		public void onSurfaceChanged(SurfaceHolder holder, int format,
				int width, int height) {
			Log.d("AAAA", "onSurfaceChanged");

			// TODO Auto-generated method stub
			super.onSurfaceChanged(holder, format, width, height);
			myHandler.postDelayed(mDrawFrame, 0);
		}

		@SuppressLint("NewApi")
		private void DrawAFrame() {
			if (Rigth) {
				Offset_X += 1.0f;
				Offset_Y += 1.0f;
				Offset_Z += 1.0f;
			} else {
				Offset_X -= 1.0f;
				Offset_Y -= 1.0f;
				Offset_Z -= 1.0f;
			}


				try {
					Canvas can = getSurfaceHolder().lockCanvas();

					if (can != null) {
						Paint paint = new Paint();
						paint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
						can.drawPaint(paint);
						
						DrawCanvas(can, bmp, Offset_X % 360);
						can.restore();
						getSurfaceHolder().unlockCanvasAndPost(can);
					}
				} catch (Exception e) {

				}

				myHandler.postDelayed(mDrawFrame, 1000 / DrawSpeed);
			}

		

	}

	void DrawCanvas(Canvas can, Bitmap bmp, float degrees) {
		if(bmp == null)
			return;
		
		android.graphics.Camera mCamera = new android.graphics.Camera();
		Matrix matrix = new Matrix();

		boolean degeFlag = false;
		if (degrees < 0)
			degeFlag = true;

		float perDegrees = 360 / FaceSize;

		float[] tmp_degrees = new float[FaceSize];
		float[] Save_degrees = new float[FaceSize];
		TmpDegrees[] tmp = new TmpDegrees[FaceSize];

		for (int i = 0; i < FaceSize; i++) {
			tmp_degrees[i] = degrees;
			tmp[i] = new TmpDegrees();
			tmp[i].i = i;
			tmp[i].use = false;
			if (!degeFlag) {
				tmp[i].data = Math.abs(degrees % 360 - 180);
				Save_degrees[i] = tmp[i].data;
				degrees += perDegrees;
			}else {
				tmp[i].data = Math.abs(Math.abs(degrees % 360) - 180);
				Save_degrees[i] = tmp[i].data;
				degrees -= perDegrees;
			}
		}
		Arrays.sort(Save_degrees);
		int j, m = -1;
		for (int i = 0; i < FaceSize; i++) {
			for (j = 0; j < FaceSize; j++) {
				if (Save_degrees[i] == tmp[j].data && tmp[j].use == false) {
					m = j;
					tmp[j].use = true;
					break;
				}
			}
			Save_degrees[i] = tmp_degrees[tmp[m].i];
		}

		can.drawBitmap(wallpaper_escape1, matrix, null);
		
		for (int i = FaceSize - 1; i >= 0; i--) {
			mCamera.save();
			matrix.reset();
			mCamera.rotateY(Save_degrees[i]);
			// Log.d("AAAA", "Save_degrees[i]  "+i+"  "+Save_degrees[i]);
			mCamera.translate(480f, -130f, 300f);
			mCamera.getMatrix(matrix);
			mCamera.restore();

			matrix.preTranslate(-480f, -80);
			matrix.postTranslate(480f, 80);
			can.drawBitmap(bmp, matrix, null);

		}
	}

	public static void insertSort(int[] numbers) {
		int size = numbers.length, temp, j;
		for (int i = 1; i < size; i++) {
			temp = numbers[i];
			for (j = i; j > 0 && temp < numbers[j - 1]; j--)
				numbers[j] = numbers[j - 1];
			numbers[j] = temp;
		}
	}

	class TmpDegrees {
		int i;
		float data;
		boolean use;
	}
}
