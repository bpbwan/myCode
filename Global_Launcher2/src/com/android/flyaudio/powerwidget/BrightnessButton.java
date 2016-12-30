
package com.android.flyaudio.powerwidget;

//import com.android.server.power.PowerManagerService;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.android.launcher.R;
import com.android.launcher2.Launcher;

public class BrightnessButton extends PowerButton {

    private static final String TAG = "BrightnessButton";

   
    private static final int AUTO_BACKLIGHT = -1; // 关屏

    private static final int LOW_BACKLIGHT = 0;// 暗淡
    private static final int MID_BACKLIGHT = 1;// 中等亮度
    private static final int HIGH_BACKLIGHT = 2;// 高亮

    private static final int[] BACKLIGHTS = new int[] {
            AUTO_BACKLIGHT, LOW_BACKLIGHT, MID_BACKLIGHT, HIGH_BACKLIGHT, 2
    };

    // 屏幕的背光亮度　，在0到255之间
    private static final Uri BRIGHTNESS_URI = Settings.System
            .getUriFor(Settings.System.SCREEN_BRIGHTNESS);

    // 控制是否启用自动亮度模式。
    private static final Uri BRIGHTNESS_MODE_URI = Settings.System
            .getUriFor(Settings.System.SCREEN_BRIGHTNESS_MODE);

    private static final List<Uri> OBSERVED_URIS = new ArrayList<Uri>();

    static {
        OBSERVED_URIS.add(BRIGHTNESS_URI);
        OBSERVED_URIS.add(BRIGHTNESS_MODE_URI);
    }

    private boolean mAutoBrightnessSupported = false;

    private boolean mAutoBrightness = false;

    private int mCurrentBrightness;

    private int mCurrentBacklightIndex = 0;

    private int[] mBacklightValues = new int[] {
            0, 1, 2, 3
    };

    public BrightnessButton() {
        mType = BUTTON_BRIGHTNESS; // 切换亮度
    }
	Context mContext=null;
    @Override
    protected void setupButton(View view) {
        super.setupButton(view);
        if (mView != null) {
            mContext = mView.getContext();
            mAutoBrightnessSupported = mContext.getResources().getBoolean(
                    com.android.internal.R.bool.config_automatic_brightness_available);
            // updateSettings(context.getContentResolver());
            mView.setId(1);
        }
    }
    @Override
    protected void updateState(Context context) {
        Log.d(TAG, "1  mCurrentBrightness = " + mCurrentBrightness + " brightness = " + brightness);
        SharedPreferences state = context.getSharedPreferences("brightness_state", 0);
        mCurrentBrightness = state.getInt("currentstate", 3);
        Log.d(TAG, "2  mCurrentBrightness = " + mCurrentBrightness + " brightness = " + brightness);
        // int i=intent.getIntExtra("value", 0);
        updateBrightnessUI(mCurrentBrightness);
        updateView();
    }

    
    
    // 切换状态，控制屏幕亮度
    int brightness = 0;

    @Override
    protected void toggleState(Context context) {
        PowerManager power = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        ContentResolver resolver = context.getContentResolver();
        Intent i = new Intent("cn.flyaudio.systemui.changebrightness");
        mCurrentBacklightIndex++;
        // Log.d("DDD","mCurrentBacklightIndex = " + mCurrentBacklightIndex +
        // " mCrIndex = 0 " + (mCurrentBacklightIndex > mBacklightValues.length
        // - 1));
        if (mCurrentBacklightIndex > mBacklightValues.length - 1) {
            mCurrentBacklightIndex = 0;
        }
        int backlightIndex = mBacklightValues[mCurrentBacklightIndex];
        brightness = BACKLIGHTS[backlightIndex];
        Log.d(TAG, " mCurrentBacklightIndex = " + mCurrentBacklightIndex + "   = " + brightness);
        if (brightness == AUTO_BACKLIGHT) {
            // Settings.System.putInt(resolver,
            // Settings.System.SCREEN_BRIGHTNESS_MODE, //自动　－－改为关屏
            // Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
            i.putExtra("value", 0);
        } else {
            if (mAutoBrightnessSupported) {
                Settings.System.putInt(resolver, Settings.System.SCREEN_BRIGHTNESS_MODE,
                        Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
            }
            if (power != null) {
                // power.setBacklightBrightness(brightness);

                if (brightness == HIGH_BACKLIGHT) {
                    i.putExtra("value", 3);
                    Log.d("DDD", "HIGH_BACKLIGHT........");
                } else if (brightness == MID_BACKLIGHT) {
                    i.putExtra("value", 2);
                    Log.d("DDD", "MID_BACKLIGHT.....");
                } else if (brightness == LOW_BACKLIGHT) {
                    i.putExtra("value", 1);
                    Log.d("DDD", "LOW_BACKLIGHT.....");
                }
            }
        }
        Settings.System.putInt(resolver, Settings.System.SCREEN_BRIGHTNESS, brightness);

        context.sendBroadcast(i);

        int br = Settings.System.getInt(resolver,
                Settings.System.SCREEN_BRIGHTNESS, -1);
        Log.d(TAG, " sendBroadcast to changebrightenss...." + brightness
                + "get system bright  br = " + br);
    }

    // 长按进入setting的显示设置页
    @Override
    protected boolean handleLongClick(Context context) {
     
        return true;
    }

    @Override
    protected List<Uri> getObservedUris() {
        return OBSERVED_URIS;
    }

   

    
    
    
    @Override
    protected void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        int i = intent.getIntExtra("value", 3);
        // updateState(context);
        mCurrentBrightness = i;
        Log.d(TAG, "  onReceive  i=intent.getIntExtra == " + intent.getIntExtra("value", 3));
        updateBrightnessUI(mCurrentBrightness);
        SharedPreferences.Editor edit = context.getSharedPreferences("brightness_state", 0).edit();
        edit.putInt("currentstate", mCurrentBrightness);
        edit.commit();
        updateView();
        
    }


@Override
    public boolean onTouch(View view, MotionEvent arg1) {
		// TODO Auto-generated method stub
    	view.setId(1);
		return super.onTouch(view, arg1);
	}

    @Override
    protected IntentFilter getBroadcastIntentFilter() {
        // TODO Auto-generated method stub
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.android.systemui.updatebrightnessstate");     
        filter.addAction("cn.flyaudio.systemui.changebrightness");  
        return filter;
    }
    
   
   private void updateBrightnessUI(int i){
       switch (i) 
       {
           case 0:
        	   setUI("stat_brightness_low","night_bright_button","power_button_text_color");
               mCurrentBacklightIndex = 0;
               mState = STATE_ENABLED;
               break;
           case 1:
        	   setUI("stat_brightness_low","night_bright_button","power_button_text_color");
               mState = STATE_ENABLED;
               mCurrentBacklightIndex = 1;
               break;
           case 2:
        	   setUI("stat_brightness_mid","mid_bright_button","power_button_text_color");
               mCurrentBacklightIndex = 2;
               mState = STATE_INTERMEDIATE;
               break;
           case 3:
        	   setUI("stat_brightness_hight","bright_on_button","power_button_text_color");
               mCurrentBacklightIndex = 3;
               mState = STATE_INTERMEDIATE;
               break;
       }
       
       
       
   }

}
