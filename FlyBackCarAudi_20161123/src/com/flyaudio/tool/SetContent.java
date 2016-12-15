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

public class SetContent{
	BackCarService mBackCarService;
	static SetContent mSetContent;
 
 
	public static SetContent getInstance(BackCarService Service){
			if(mSetContent==null){
				mSetContent = new SetContent(Service);

				}
			return mSetContent;
		}

	public SetContent(BackCarService Service){

		mBackCarService = Service;
		init();
	}
	
	void init(){
	
		}
	
	public void show(){
		LayoutInflater inflater = (LayoutInflater) mBackCarService.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	
		final View setting_content = inflater.inflate(
					R.layout.setting_content, null);
		mBackCarService.getFlyBackCarMainView().LayoutAddViewWithPageID(setting_content, 
			PageID.PAGE_COMMON_SETTING);


		}

	public void hide(){
		mBackCarService.getFlyBackCarMainView().LayoutRemoveViewWithPageID(PageID.PAGE_COMMON_SETTING);

		}
	 
	 Handler trackCapHandler = new Handler(){
				  public void handleMessage(android.os.Message msg) {		  
					  if (msg.what == 1) {	  
					 
						  }
					  }
	};



}

