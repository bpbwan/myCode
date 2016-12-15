/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.flyaudio.backcar.trackbitmap;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.flyaudio.backcar.modules.BaseModule;
import com.flyaudio.backcar.util.FlyUtil;
import com.flyaudio.tool.GLTrackViewToolHelper;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.Config;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.content.Context;
import android.os.SystemClock;

public class TrackGLRender implements Renderer {

	FloatBuffer vertices;
	FloatBuffer texture;
	ShortBuffer indices;
	int textureId;
	Context mcontext;
	static TrackBitmap mTrackBitmap;
	public static TrackGLRender mTrackGLRender;
	int screenW;
	int screenH;
	static int textureIds[] = new int[1];
	static GL10 m_GL;
	static int gtextureId = -1;
	boolean textrueGen = false;
	boolean everytimein = false;
	boolean flag = false;
	long time2 = 0;
	boolean DrawStop = false;
	boolean firstDraw = true;
	boolean mustDoit = false;
	Bitmap bitmap = null;
	Bitmap gbitmap = null;
	
	final int START_TIMEOUT = 1000;
	final int MSG_START = 1;
	
	public TrackGLRender(Context context, TrackBitmap trackBitmap) {
		mcontext = context;
		mTrackBitmap = trackBitmap;

		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * 2 * 4);
		byteBuffer.order(ByteOrder.nativeOrder());
		vertices = byteBuffer.asFloatBuffer();

		vertices.put(new float[] { -400f, -240f, 400f, -240f, -400f, 240f,
				400f, 240f });
		ByteBuffer indicesBuffer = ByteBuffer.allocateDirect(6 * 2);
		indicesBuffer.order(ByteOrder.nativeOrder());
		indices = indicesBuffer.asShortBuffer();
		indices.put(new short[] { 0, 1, 2, 1, 2, 3 });

		ByteBuffer textureBuffer = ByteBuffer.allocateDirect(4 * 2 * 4);
		textureBuffer.order(ByteOrder.nativeOrder());
		texture = textureBuffer.asFloatBuffer();
		texture.put(new float[] { 0, 1f, 1f, 1f, 0f, 0f, 1f, 0f });

		indices.position(0);
		vertices.position(0);
		texture.position(0);
		mTrackGLRender = this;

		gbitmap = Bitmap.createBitmap(FlyUtil.GET_SCREEN_W(), FlyUtil.GET_SCREEN_H(), Config.ARGB_4444);
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		Log.d("GLSurfaceViewTest", "surface created");
		if (!textrueGen) {
			textrueGen = true;
			gtextureId = -1;
			gl.glGenTextures(1, textureIds, 0);
		}
		gl.glClearColor(0, 0, 0, 0);
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		gl.glFlush();
		firstDraw = true;
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		Log.d("GLSurfaceViewTest", "surface changed: " + width + "x" + height);
		screenW = width;
		screenH = height;
		everytimein = false;
		gl.glClearColor(0, 0, 0, 0);

		gl.glViewport(0, 0, screenW, screenH);
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glEnable(GL10.GL_TEXTURE_2D);

		m_GL = gl;
		gl.glFlush();

	}

	@Override
	public void onDrawFrame(GL10 gl) { // notes: ˫�л�framebuf ������֡���ڲ�ͬframebuf
		
		if (DrawStop || firstDraw || mustDoit) {
			gl.glClearColor(0, 0, 0, 0);
			gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
			gl.glFlush();
			firstDraw = false;
	
		} else {
			DrawContinue(gl);

		}
	}

	void DrawContinue(GL10 gl) {
		final int mtextureId = loadTexture(null, gl);
		

		
		if (mtextureId != -1)
			gl.glClearColor(0, 0, 0, 0);
		gl.glMatrixMode(GL10.GL_MODELVIEW);

		gl.glLoadIdentity();
		gl.glOrthof(-400, 400, -240, 240, -1, 1);

		// gl.glEnable(GL10.GL_TEXTURE_2D);
		// ������ID

		if (mtextureId != -1) {
			gtextureId = mtextureId;
			DrawFrame(gl, true, mtextureId);

		} else if (gtextureId == textureIds[0]) {

			if (!everytimein) {
				everytimein = true;
				if (gbitmap != null) {
					gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIds[0]);
					GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, gbitmap, 0);
					gl.glTexParameterf(GL10.GL_TEXTURE_2D,
							GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
					gl.glTexParameterf(GL10.GL_TEXTURE_2D,
							GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_NEAREST);
					gl.glBindTexture(GL10.GL_TEXTURE_2D, 0);

				}
			}
			DrawFrame(gl, true, textureIds[0]);
		} else if (gtextureId == -1) {
		}

		gl.glFlush();
		// if(mtextureId != -1){
		// showFrameCount();
		// }

	}

	void DrawFrame(GL10 gl, boolean hastexttrueId, int texttureid) {
		if (hastexttrueId)
			gl.glBindTexture(GL10.GL_TEXTURE_2D, texttureid);

		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

		gl.glVertexPointer(2, GL10.GL_FLOAT, 0, vertices);

		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texture);
		gl.glDrawElements(GL10.GL_TRIANGLE_STRIP, 6, GL10.GL_UNSIGNED_SHORT,
				indices);

		// gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		// gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

	}

	public void destroyTexture() {
		Log.d("DDD", "destroyTexture ");
		// m_GL.glDeleteTextures(1,textureIds, 0);
		// textrueGen = false;
	}



	public int loadTexture(String fileName, GL10 gl) {

		if (mTrackBitmap != null)
			bitmap = mTrackBitmap.comsumeBitmap();

		if (bitmap != null) {
		//	gbitmap = bitmap;
			gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIds[0]);
			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
					GL10.GL_NEAREST);
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
					GL10.GL_NEAREST);
			gl.glBindTexture(GL10.GL_TEXTURE_2D, 0);

			return textureIds[0];
		} else
			return -1;
	}

	public int loadTextureFromContext(String fileName, GL10 gl) {

		try {
			Bitmap mbitmap = BitmapFactory.decodeStream(mcontext.getAssets()
					.open(fileName));

			if (mbitmap != null) {
				// gl.glGenTextures(1, textureIds, 0);
				final int textureId = textureIds[0];

				gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId);
				GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, mbitmap, 0);
				gl.glTexParameterf(GL10.GL_TEXTURE_2D,
						GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
				gl.glTexParameterf(GL10.GL_TEXTURE_2D,
						GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_NEAREST);
				gl.glBindTexture(GL10.GL_TEXTURE_2D, 0);

				return textureId;
			} else
				return -1;
		} catch (IOException e) {
			Log.d("TexturedRectangleTest",
					"couldn't load asset 'bobrgb888.png'!");
			throw new RuntimeException("couldn't load asset '" + fileName + "'");
		}
	}

	int count = 20;

	void showFrameCount() {
		if (count == 20) {
			long time = SystemClock.uptimeMillis();
			long mtime = (time - time2) / 20;
			Log.d("DDD", "GLRender	: " + mtime + " ms /fps");

			time2 = SystemClock.uptimeMillis();
			count = 0;
		}
		count++;
	}

	public void Pause() {
		DrawStop = true;
		Log.d("GL", "DrawStop  true");
	}

	public void Resume() {
		Log.d("GL", "DrawStop  false");
		DrawStop = false;
	}
	
	public void setFrameNullTime(int delay){
		GLHandler.removeMessages(MSG_START);

		mustDoit = true;
		Log.d("GL", "setFrameNullTime  "+mustDoit);
		GLHandler.sendEmptyMessageDelayed(MSG_START, START_TIMEOUT);
	}
	
	Handler GLHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
		//	flyBackCarView.update(0, (int) msg.arg1);
			switch((int)msg.what){
			
			case MSG_START:
				mTrackBitmap.ClearBitmapLeftOne();
			
				mustDoit = false;
				Log.d("GL", "MSG_START  "+mustDoit);
				break;
			}
		}
	};
	/*
	 * public class ComsumeBitmap implements Runnable { private boolean
	 * consumeStop = false; public void stop(){ consumeStop = false; } public
	 * boolean isRunning(){ return consumeStop; }
	 * 
	 * @Override public void run() { consumeStop = true; while(consumeStop){
	 * textureId = loadTexture("bg_150r.png",mgl); if(textureId != -1)
	 * TrackBitmap.mCallback.reRender(); } }
	 * 
	 * }
	 */
}
