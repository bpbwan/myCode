package com.flyaudio.tool;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;

import android.util.Log;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import com.flyaudio.backcar.BackCarService;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;
import android.os.Message;
import java.io.IOException;
import android.os.Handler;
import android.view.View.OnClickListener;
import com.flyaudio.backcar.R;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.graphics.Bitmap;

import com.flyaudio.backcar913.BackCarTag;
import com.flyaudio.globaldefine.PageID;
import com.flyaudio.object.FlyTrackView;
import com.flyaudio.backcar.util.FlyUtil;

public class BaseToolHelper {
	BackCarService mBackCarService;
	static BaseToolHelper mBaseToolHelper;
	
	protected LayoutInflater inflater;
	View mainView;
	public static BaseToolHelper getInstance(BackCarService Service) {
		if (mBaseToolHelper == null) {
			mBaseToolHelper = new BaseToolHelper(Service);

		}
		return mBaseToolHelper;
	}
	private int CurrentID = 0;
	public BaseToolHelper(BackCarService Service) {

		mBackCarService = Service;
		init();
	}

	protected void init() {
	inflater = (LayoutInflater) mBackCarService
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}

	public void showView(View child, int ID){
		mBackCarService.getFlyBackCarMainView().LayoutAddViewWithPageID(
						child, ID);
		CurrentID = ID;

		}
	
	public void show() {
		
	}

	public void hide() {
		mBackCarService.getFlyBackCarMainView().LayoutRemoveViewWithPageID(
				CurrentID);
	}

	Handler trackCapHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 1) {

			}
		}
	};

}

