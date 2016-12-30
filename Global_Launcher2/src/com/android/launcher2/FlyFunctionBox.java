/*
 * Copyright (C) 2008 The Android Open Source Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.android.launcher2;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import com.android.flyaudioui.object.FlyTextButtonObj;
import com.android.launcher.R;

public class FlyFunctionBox extends FrameLayout implements OnClickListener,
        OnKeyListener {
    private float mZoom;
    private Launcher mLauncher;
    private Boolean state = false;

    public Boolean getState() {
		return state;
	}

	private String TAG = "FlyFunctionBox";
    // wen
    private FlyTextButtonObj mMediaBox;

    // private FlyFunctionBox mFlyMediaBox;

    public FlyFunctionBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub

        setVisibility(View.GONE);
    }

    public void setOnClick(FlyTextButtonObj MediaBox) {
        mMediaBox = MediaBox;
        mMediaBox.setOnClickListener(this);
        setOnKeyListener(this);
        // setFocusable(false);
    }

    public void onClick(View v) {
        Log.i(TAG, "-------mMediaBox-----onClick-------------"+state);
        if (!state) {
            showFLyMediaBox(true);
        } else {
            closeFlyMediaBox(true);
        }
    }
    public FlyFunctionBox(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs);
    }

    public void zoom(float zoom, boolean animate) {
        cancelLongPress();
        mZoom = zoom;
        if (isVisible()) {
            getParent().bringChildToFront(this);
            setVisibility(View.VISIBLE);
            if (animate) {
                startAnimation(AnimationUtils.loadAnimation(getContext(),
                        Launcher.flyLauncher.
                        getIdFromSkin(Launcher.skinContext, "fly_fade_in", "anim")));
            } else {
                onAnimationEnd();
            }
        } else {
            if (animate) {
                startAnimation(AnimationUtils.loadAnimation(getContext(),
                		Launcher.flyLauncher.
                        getIdFromSkin(Launcher.skinContext, "fly_fade_out", "anim")));
            } else {
                onAnimationEnd();
            }
        }
    }

    protected void onAnimationEnd() {
        if (!isVisible()) {
            setVisibility(View.GONE);
            mZoom = 0.0f;
        } else {
            mZoom = 1.0f;
        }
    }

    public boolean isVisible() {
        return mZoom > 0.001f;
    }
    
    

    public boolean onKey(View v, int keyCode, KeyEvent event) {
        Log.i(TAG, "------------KEYCODE_BACK-------------");
        if (!isVisible())
            return false;
        switch (keyCode) {
        case KeyEvent.KEYCODE_BACK:
            Log.i(TAG, "------------KEYCODE_BACK-------------");
            closeFlyMediaBox(true);
            break;
        default:
            return false;
        }
        return true;
    }

    // wen
    public boolean isFlyMediaBoxVisible() {
        // TODO Auto-generated method stub
        return isVisible();
    }

    // wen
    void showFLyMediaBox(boolean animated) {
        // TODO Auto-generated method stub
        zoom(1.0f, animated);
        // setFocusable(true);
        requestFocus();
        mMediaBox.setBoxTextColor(Color.WHITE);
        Log.i("DDDT", "change color to white");
        mMediaBox.getBackground().setLevel(1);
        state=true;
    }

    // wen
    void closeFlyMediaBox(boolean animated) {
        // TODO Auto-generated method stub
        if (isVisible()) {
            zoom(0.0f, animated);
            // setFocusable(false);
           
            mMediaBox.setBoxTextColor(Color.BLACK);
            Log.i("DDDT", "change color to black");
            mMediaBox.getBackground().setLevel(0); 
            state=false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        Log.i(TAG, "onTouchEvent-----");
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            Log.i(TAG, "onTouchEvent---ACTION_DOWN--");
            break;
        case MotionEvent.ACTION_UP:
            Log.i(TAG, "onTouchEvent---ACTION_UP--");
            closeFlyMediaBox(true);
            break;
        default:
            break;
        }
        return super.onTouchEvent(event);
    }

}

