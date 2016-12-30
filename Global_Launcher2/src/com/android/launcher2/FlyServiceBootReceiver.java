package com.android.launcher2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class FlyServiceBootReceiver extends BroadcastReceiver {

    private static final String tag = "launcher";

    @Override
    public void onReceive(Context arg0, Intent arg1) {
        // TODO Auto-generated method stub
        Log.d("apl", "launcher BootReceiver message");
    }
}

