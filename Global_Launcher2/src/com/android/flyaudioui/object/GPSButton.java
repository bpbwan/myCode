package com.android.flyaudioui.object;


import com.android.launcher2.Launcher;
import android.content.Context;
import android.content.Intent;
import android.os.SystemProperties;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;

public class GPSButton extends Button implements OnClickListener {
	private Launcher mLauncer;		//	fly_jing_test
//	private final String FLY = "fly_jing_test";		//	fly_jing_test
//	private final String tjing = "-*- GPSButton.java -*- ";	//	fly_jing_test
	
    public GPSButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        setOnClickListener(this);
    }

    public void onClick(View view) {
        // TODO Auto-generated method stub
      
      Intent localIntent = new Intent("android.intent.action.NaviOne");
      localIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      SystemProperties.set("tcc.fly.gpskey", "system");
      try {
    	  Launcher.flyLauncher.startActivity(localIntent);
      } catch (Exception e) {
        // TODO: handle exception
    	  Toast toast = Toast.makeText(Launcher.flyLauncher,
				    "There are no this app!!!", Toast.LENGTH_SHORT);
				   toast.setGravity(Gravity.CENTER, 0, 0);
				   toast.show();
//         System.out.println("no this app----------");
//         SystemProperties.set("tcc.fly.gpskey", "module");
      }
       
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // TODO Auto-generated method stub
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                getBackground().setLevel(1);
                break;
            case MotionEvent.ACTION_UP:
                getBackground().setLevel(0);
                break;
            default:
                break;
        }
        return super.onTouchEvent(ev);
    }
}
