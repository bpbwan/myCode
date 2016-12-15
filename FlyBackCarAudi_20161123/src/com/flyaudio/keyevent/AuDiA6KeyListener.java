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
import com.flyaudio.backcar.modules.AuDiA6Module;
import com.flyaudio.globaldefine.PageID;
import com.flyaudio.backcar.PluginProxyContext;
import com.flyaudio.globaldefine.ForeGround;
import com.flyaudio.backcar.util.*;

public class AuDiA6KeyListener extends AuDiA3KeyListener {
	static AuDiA6KeyListener mAuDiA3KeyListener;

	public static AuDiA6KeyListener getInstance(KeyListenerCallback cb) {
		if (mAuDiA3KeyListener == null) {
			mAuDiA3KeyListener = new AuDiA6KeyListener(cb);

		}
		return mAuDiA3KeyListener;
	}

	public AuDiA6KeyListener(KeyListenerCallback cb) {
		super(cb);
	}


}
