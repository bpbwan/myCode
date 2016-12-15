package com.flyaudio.keyevent;

import java.io.InputStream;

import android.os.SystemProperties;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.MotionEvent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View.OnKeyListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.util.Log;

import com.flyaudio.object.*;

import java.util.ArrayList;

import android.view.View.OnFocusChangeListener;

import com.flyaudio.backcar.BackCarService;
import com.flyaudio.backcar.MsgType;

import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.Canvas;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.flyaudio.backcar913.BackCarTag;
import com.flyaudio.backcar913.FlyBaseListener;
import com.flyaudio.keyevent.BaseKeyListener.KeyListenerCallback;
import com.flyaudio.backcar.modules.AuDiQ3Module;
import com.flyaudio.globaldefine.PageID;
import com.flyaudio.backcar.PluginProxyContext;
import com.flyaudio.globaldefine.ForeGround;
import com.flyaudio.backcar.util.*;

public class AuDiQ3KeyListener extends AuDiA3KeyListener {
	static AuDiQ3KeyListener mAuDiQ3KeyListener;

	public static AuDiQ3KeyListener getInstance(KeyListenerCallback cb) {
		if (mAuDiQ3KeyListener == null) {
			mAuDiQ3KeyListener = new AuDiQ3KeyListener(cb);

		}
		return mAuDiQ3KeyListener;
	}

	public AuDiQ3KeyListener(KeyListenerCallback cb) {
		super(cb);
	}
	
	
}
