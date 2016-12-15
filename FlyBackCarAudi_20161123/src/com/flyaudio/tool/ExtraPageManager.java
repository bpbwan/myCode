package com.flyaudio.tool;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;

import android.util.Log;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

public class ExtraPageManager {

	private Context mContext;
	private  HashMap<Integer, View> PageViewMap = new HashMap<Integer, View>();

	

	
	public  void AddPageView(int page, View v){
		PageViewMap.put(page, v);
	}

	public  void RemodePageView(int page){
		PageViewMap.remove(page);

		}



	public  View GetPageView(int page){
		if(PageViewMap.containsKey(page))
			return PageViewMap.get(page);
		else
			return null;
		}

	
	public  boolean FindHasPageView(int page){
		if(PageViewMap.containsKey(page))
			return true;
		else 
			return false;

	}

	public  void RemoveAllPageView(FrameLayout MainLayout){
		Iterator iter = PageViewMap.entrySet().iterator();
		while (iter.hasNext()) {
		Map.Entry entry = (Map.Entry) iter.next();
		int key = (Integer)entry.getKey();
		Log.d("DDD", "RemoveAllPageView "+key);
		View val = (View)entry.getValue();
		RemodePageView(key);
		MainLayout.removeView(val);
		}
	}

}
