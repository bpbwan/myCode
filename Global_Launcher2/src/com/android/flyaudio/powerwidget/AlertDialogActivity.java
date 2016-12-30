package com.android.flyaudio.powerwidget;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.launcher2.Launcher;

public class AlertDialogActivity extends Activity {
    View view = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = Launcher.flyLauncher.getLayoutFromSkin(Launcher.skinContext,
                "reset");
        setContentView(view);
        Button button_Ok = (Button) view.findViewById(Launcher.flyLauncher
                .getIdFromSkin(Launcher.skinContext, "okLabel", "id"));
        Button button_Cancel = (Button) view.findViewById(Launcher.flyLauncher
                .getIdFromSkin(Launcher.skinContext, "cancelLabel", "id"));
        button_Ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Intent i = new Intent("com.android.flyaudioui.LOCALE_CHANGED");
                sendBroadcast(i);// 重启机器的广播
                Log.i("@@@@@@@", "AlertDialogActivity onClick sendBroadcast "
                        + "com.android.flyaudioui.LOCALE_CHANGED");
                finish();
            }
        });

        button_Cancel.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                finish();
            }

        });

    }
}
