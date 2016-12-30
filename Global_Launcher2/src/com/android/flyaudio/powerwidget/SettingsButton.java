package com.android.flyaudio.powerwidget;

import com.android.launcher.R;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.app.ActivityManagerNative;

import java.util.ArrayList;
import java.util.List;

public class SettingsButton extends PowerButton {
  
    private static final List<Uri> OBSERVED_URIS = new ArrayList<Uri>();
    static {
        OBSERVED_URIS.add(Settings.Secure.getUriFor(Settings.Secure.LOCATION_PROVIDERS_ALLOWED));
    }

    public SettingsButton() { mType = BUTTON_SETTINGS; }

    @Override
    protected void updateState(Context context) {
    	setUI("settings_off","settings_button","power_button_text_color_d");
        	
            mState = STATE_DISABLED;
            
    }

    @Override
    protected void setupButton(View view) {
    	// TODO Auto-generated method stub
    	super.setupButton(view);
    	view.setId(7);
    }
    
    
    
    @Override
    protected void toggleState(Context context) {  
        Log.d("SettingsButton","toggleState");
     
     
        context.sendBroadcast(new Intent().setAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
        context.startActivity(new Intent(Settings.ACTION_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        Log.d(TAG," sendBroadcast close statubar");
    }

    @Override
    protected boolean handleLongClick(Context context) {
        context.sendBroadcast(new Intent().setAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
        context.startActivity(new Intent(Settings.ACTION_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        Log.d("SettingsButton","handleLongClick");
        return true;
    }
}
