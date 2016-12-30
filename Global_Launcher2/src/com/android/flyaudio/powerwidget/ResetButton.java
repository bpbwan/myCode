package com.android.flyaudio.powerwidget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import com.android.launcher.R;
import com.android.launcher2.Launcher;

public class ResetButton extends PowerButton {

    private static final List<Uri> OBSERVED_URIS = new ArrayList<Uri>();
    public static Context mcontext = null;

    public static void SetContext(Context context) {
        mcontext = context;
        Log.d(TAG, "...resetbutton get context ....");

    }

    static {
        OBSERVED_URIS.add(Settings.Secure.getUriFor(Settings.Secure.LOCATION_PROVIDERS_ALLOWED));
    }

    public ResetButton() {
        mType = BUTTON_GPS;
    }

    @Override
    protected void updateState(Context context) {
    	setUI("reset_off","reset_button","power_button_text_color_d");
        
        mState = STATE_DISABLED;
        // }
    }

    
    @Override
    protected void setupButton(View view) {
    	// TODO Auto-generated method stub
    	super.setupButton(view);
    	view.setId(6);
    }
    
    @Override
    protected void toggleState(Context context) {
        context.sendBroadcast(new Intent().setAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
        
        showAlertDialog(context);
        // final Context mContext = context;
        //
        
 /*       WindowManager mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        layoutParams.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;

        AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);
        builder.setTitle(R.string.reset_title);
        builder.setPositiveButton(R.string.okLabel,
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)

                    {
                        // Intent i = new
                        // Intent("com.android.flyaudioui.LOCALE_CHANGED");
                        // mcontext.sendBroadcast(i);//重启机器的广播

                    }
                });

        builder.setNegativeButton(R.string.cancelLabel,
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                    }
                });
        // builder.create().show();
        AlertDialog alert = builder.create();
        // alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);//
        // alert.show();

        // mWindowManager.addView(alert, layoutParams);
*/
    }

    private void showAlertDialog(Context context) {
    	
        Intent dialog = new Intent(Launcher.flyLauncher, AlertDialogActivity.class);
        dialog.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Launcher.flyLauncher.startActivity(dialog);
    }

    @Override
    protected boolean handleLongClick(Context context) {
        context.sendBroadcast(new Intent().setAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
        showAlertDialog(context);
        return true;
    }

    /*
     * @Override protected List<Uri> getObservedUris() { return OBSERVED_URIS; }
     * private boolean getGpsState(Context context) { ContentResolver resolver =
     * context.getContentResolver(); return
     * Settings.Secure.isLocationProviderEnabled(resolver,
     * LocationManager.GPS_PROVIDER); }
     */
}
