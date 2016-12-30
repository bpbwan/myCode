package com.android.flyaudio.powerwidget;

import android.app.ActivityManagerNative;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.launcher.R;
import com.android.launcher.R.string;
import com.android.launcher2.Launcher;

import java.util.ArrayList;
import java.util.List;

public abstract class PowerButton implements OnTouchListener {
    public static final String TAG = "PowerButton";

    public static final int STATE_ENABLED = 1;
    public static final int STATE_DISABLED = 2;
    public static final int STATE_TURNING_ON = 3;
    public static final int STATE_TURNING_OFF = 4;
    public static final int STATE_INTERMEDIATE = 5;
    public static final int STATE_UNKNOWN = 6;

    public static final String BUTTON_WIFI = "toggleWifi";
    public static final String BUTTON_GPS = "toggleGPS";
    public static final String BUTTON_BLUETOOTH = "toggleBluetooth";
    public static final String BUTTON_BRIGHTNESS = "toggleBrightness";
    public static final String BUTTON_SOUND = "toggleSound";
    public static final String BUTTON_SYNC = "toggleSync";
    public static final String BUTTON_WIFIAP = "toggleWifiAp";
    public static final String BUTTON_SCREENTIMEOUT = "toggleScreenTimeout";
    public static final String BUTTON_MOBILEDATA = "toggleMobileData";
    public static final String BUTTON_LOCKSCREEN = "toggleLockScreen";
    public static final String BUTTON_NETWORKMODE = "toggleNetworkMode";
    public static final String BUTTON_AUTOROTATE = "toggleAutoRotate";
    public static final String BUTTON_AIRPLANE = "toggleAirplane";
    public static final String BUTTON_FLASHLIGHT = "toggleFlashlight";
    public static final String BUTTON_SLEEP = "toggleSleepMode";
    public static final String BUTTON_MEDIA_PLAY_PAUSE = "toggleMediaPlayPause";
    public static final String BUTTON_MEDIA_PREVIOUS = "toggleMediaPrevious";
    public static final String BUTTON_MEDIA_NEXT = "toggleMediaNext";
    public static final String BUTTON_LTE = "toggleLte";
    public static final String BUTTON_WIMAX = "toggleWimax";
    public static final String BUTTON_UNKNOWN = "unknown";
    private static final String SEPARATOR = "OV=I=XseparatorX=I=VO";
    
    public static final String BUTTON_MEMORY = "toogme";
    public static final String BUTTON_RESET = "toogReset";
    public static final String BUTTON_SETTINGS = "toogSettings";
    private static final Mode MASK_MODE = Mode.SCREEN;

    protected int mIcon;
    protected int mIconTag;
//    protected int mTextColor;
    protected int mText;
    protected int mState;
    protected View mView;
    protected int color;
    protected String mType = BUTTON_UNKNOWN;

    protected ImageView mIconView;
    
    protected TextView mIconViewText;

    private View.OnClickListener mExternalClickListener;
    private View.OnLongClickListener mExternalLongClickListener;

    protected boolean mHapticFeedback;
    protected Vibrator mVibrator;
    private long[] mClickPattern;
    private long[] mLongClickPattern;

    // we use this to ensure we update our views on the UI thread
    private Handler mViewUpdateHandler = new Handler() {
        @Override
		public void handleMessage(Message msg) {
			if (mIconView != null) {
				mIconView.setImageResource(mIcon);
				mIconView.setVisibility(View.VISIBLE);
				Log.d(TAG, "mViewUpdateHandler");
			}
			// mIconTagView.setImageResource(mIconTag);
			if (mText != 0) {//这里有resourcenotfound的错误，应该是资源名字和皮肤包里的不匹配造成的，找找看
				Log.i("@@", "mText:"+Integer.toHexString(mText));
				try {
					mIconViewText.setText(mText);
				} catch (Exception e) {
					// TODO: handle exception
					
					//mIconViewText.setText(mContext.getResources().getText(mText));
				}
				
				mIconViewText.setTextColor(color);
			}
		}
    };

    protected abstract void updateState(Context context);
    protected abstract void toggleState(Context context);
    protected abstract boolean handleLongClick(Context context);
    private Context mContext;
    protected void update(Context context) {
    	mContext=context;
    	updateState(context);
        updateView();
    }

    public String[] parseStoredValue(CharSequence val) {
        if (TextUtils.isEmpty(val)) {
          return null;
        } else {
          return val.toString().split(SEPARATOR);
        }
    }


    protected void onReceive(Context context, Intent intent) {
//    	Log.i("DDD3", "onReceive updateView");
//    	updateState(context);
//    	 updateView();
        }

    protected void onChangeUri(ContentResolver resolver, Uri uri) {
        // do nothing as a standard, override this if the button needs to respond
        // to a changed setting
    }

    /* package */ void setHapticFeedback(boolean enabled,
            long[] clickPattern, long[] longClickPattern) {
        mHapticFeedback = enabled;
        mClickPattern = clickPattern;
        mLongClickPattern = longClickPattern;
    }

    protected IntentFilter getBroadcastIntentFilter() {
        return new IntentFilter();
    }

    protected List<Uri> getObservedUris() {
        return new ArrayList<Uri>(); 
    }

    protected void setupButton(View view) {
        mView = view;
        if (mView != null) {
            mView.setTag(mType);
            mView.setOnClickListener(mClickListener);
            mView.setOnLongClickListener(mLongClickListener);
            mView.setOnTouchListener(this);
            mIconView = (ImageView) mView.findViewById(
            		Launcher.flyLauncher.getIdFromSkin(Launcher.skinContext,"power_widget_button_image","id"));
           // mIconTagView = (ImageView) mView.findViewById(R.id.power_widget_button_tag_image);
            mIconViewText = (TextView) mView.findViewById(
            		Launcher.flyLauncher.getIdFromSkin(Launcher.skinContext,"power_widget_button_text","id"));
            mVibrator = (Vibrator) mView.getContext().getSystemService(Context.VIBRATOR_SERVICE);
        } else {
            mIconView = null;
        }
    }

    protected void updateView() {
        mViewUpdateHandler.sendEmptyMessage(0);
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            if (mHapticFeedback && mClickPattern != null) {
                if (mClickPattern.length == 1) {
                    // One-shot vibration
                    mVibrator.vibrate(mClickPattern[0]);
                } else {
                    // Pattern vibration
                    mVibrator.vibrate(mClickPattern, -1);
                }
            }
            Log.d("zengyuke","toggleState = "+mType);
            toggleState(v.getContext());
            Log.d("zengyuke","update");
            update(v.getContext());

            if (mExternalClickListener != null) {
                mExternalClickListener.onClick(v);
            }
        }
    };

    private View.OnLongClickListener mLongClickListener = new View.OnLongClickListener() {
        public boolean onLongClick(View v) {
            boolean result = handleLongClick(v.getContext());

            if (result && mHapticFeedback && mLongClickPattern != null) {
                mVibrator.vibrate(mLongClickPattern, -1);
            }

            try {
                ActivityManagerNative.getDefault().dismissKeyguardOnNextActivity();
            } catch (RemoteException e) {
            }

            if (result && mExternalLongClickListener != null) {
                mExternalLongClickListener.onLongClick(v);
            }
            return result;
        }
    };

    void setExternalClickListener(View.OnClickListener listener) {
        mExternalClickListener = listener;
    }

    void setExternalLongClickListener(View.OnLongClickListener listener) {
        mExternalLongClickListener = listener;
    }

    protected SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences("PowerButton-" + mType, Context.MODE_PRIVATE);
    }
    
    protected void setUI(String icon,String text,String colors)
    {
    	if(icon!=null)
    		mIcon = Launcher.flyLauncher.getIdFromSkin(Launcher.skinContext,icon, "drawable");
    	if(text !=null)
    		mText = Launcher.flyLauncher.getIdFromSkin(Launcher.skinContext,text, "string");//Launcher.flyLauncher.getIdFromSkin(Launcher.skinContext,text, "string")
    	if(colors!=null)
    		color=Launcher.flyLauncher.getColorFromSkin(Launcher.skinContext,colors);
    }
    
    
    public boolean onTouch(View view, MotionEvent event) {
		// TODO Auto-generated method stub
    	
    	ImageView imag=(ImageView)view.
    			findViewById(
    					Launcher.flyLauncher.getIdFromSkin(Launcher.skinContext,"power_widget_button_image", "id"));
    	TextView text=(TextView)view.
    			findViewById(
    					Launcher.flyLauncher.getIdFromSkin(Launcher.skinContext,"power_widget_button_text", "id"));
		if (view.getId() == 6) {
			if(event.getAction()==MotionEvent.ACTION_DOWN)
			{
				imag.setImageDrawable(
						Launcher.flyLauncher.getDrawableFromSkin(Launcher.skinContext, "reset_on"));
				text.setTextColor(
						Launcher.flyLauncher.getColorFromSkin(Launcher.skinContext, "power_button_text_color"));
				
			}
			else if(event.getAction()==MotionEvent.ACTION_UP)
			{				

				imag.setImageDrawable(
						Launcher.flyLauncher.getDrawableFromSkin(Launcher.skinContext, "reset_off"));
				text.setTextColor(
						Launcher.flyLauncher.getColorFromSkin(Launcher.skinContext, "power_button_text_color_d"));
			}
		} else if (view.getId() == 7) {
			if(event.getAction()==MotionEvent.ACTION_DOWN)
			{	
				view.setBackgroundDrawable(
						Launcher.flyLauncher.getDrawableFromSkin(Launcher.skinContext, "right_bg"));
				imag.setImageDrawable(
						Launcher.flyLauncher.getDrawableFromSkin(Launcher.skinContext, "settings_on"));
				text.setTextColor(
						Launcher.flyLauncher.getColorFromSkin(Launcher.skinContext, "power_button_text_color"));
			}
			else if(event.getAction()==MotionEvent.ACTION_UP)
			{			

				imag.setImageDrawable(
						Launcher.flyLauncher.getDrawableFromSkin(Launcher.skinContext, "settings_off"));
				text.setTextColor(
						Launcher.flyLauncher.getColorFromSkin(Launcher.skinContext, "power_button_text_color_d"));
			}
		}
		else if(view.getId() == 1)
		{

			view.setBackgroundDrawable(
					Launcher.flyLauncher.getDrawableFromSkin(Launcher.skinContext, "left_bg"));
		}
		else
		{

			view.setBackgroundDrawable(
					Launcher.flyLauncher.getDrawableFromSkin(Launcher.skinContext, "mid_bg"));
		}
		return false;
	}
}
