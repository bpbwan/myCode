package com.android.flyaudio.powerwidget;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.util.Log;

import com.android.internal.telephony.TelephonyIntents;
import com.android.launcher.R;

public class MobileDataButton extends PowerButton {

    public static final String ACTION_MODIFY_NETWORK_MODE = "com.android.internal.telephony.MODIFY_NETWORK_MODE";
    public static final String EXTRA_NETWORK_MODE = "networkMode";

    public MobileDataButton() { mType = BUTTON_MOBILEDATA; }

    @Override
    protected void updateState(Context context) {
        Log.d("zengyuke","updateState MobileDataButton ");
        Log.d("zengyuke","MobileDataButton getDataState(context)= "+ getDataState(context));
        
        if (getDataState(context)) {
        	setUI("stat_data_on","mobiledata_button","power_button_text_color");     	
            mState = STATE_ENABLED;          
        } else {
        	setUI("stat_data_off","mobiledata_button","power_button_text_color_d");    
            //mIconTag = R.drawable.teb_off;
            mState = STATE_DISABLED;
        }
    }

    @Override
    protected void toggleState(Context context) {
        boolean enabled = getDataState(context);
        Log.d("zengyuke"," Before toggleState   enabled = getDataState(context) = "+  getDataState(context));

        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        
        if (enabled) {
            cm.setMobileDataEnabled(false);
        } else {
            cm.setMobileDataEnabled(true);
        }
        Log.d("zengyuke","  After toggleState getDataState(context) = "+ getDataState(context));
        
    }

    @Override
    protected boolean handleLongClick(Context context) {
        context.sendBroadcast(new Intent().setAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
//        Intent intent = new Intent("android.settings.DATA_ROAMING_SETTINGS");
//        context.startActivity(intent);
        Intent intent = new Intent();
        intent.setClassName("com.android.phone", "com.android.phone.MSimMobileNetworkSubSettings");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent); 
        
        return true;
    }

    @Override
    protected IntentFilter getBroadcastIntentFilter() {
        Log.d(TAG,"MobileDataButton getBroadcastIntentFilter");
        IntentFilter filter = new IntentFilter();
        filter.addAction(TelephonyIntents.ACTION_ANY_DATA_CONNECTION_STATE_CHANGED);
        return filter;
    }

    private boolean getDataState(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
            .getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getMobileDataEnabled();
    }
}
