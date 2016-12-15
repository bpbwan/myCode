package com.flyaudio.tool;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;

import android.util.Log;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

public class PageViewManager {

	private Context mContext;
	private static HashMap<Integer, View> PageViewMap = new HashMap<Integer, View>();
	

	
	public static void AddPageView(int page, View v){
		PageViewMap.put(page, v);
		Log.d("DDD", "AddPageView "+page);
	}

	public static void RemodePageView(int page){
		
		PageViewMap.remove(page);
		Log.d("DDD", "RemodePageView "+page);

		}



	public static View GetPageView(int page){
		if(PageViewMap.containsKey(page))
			return PageViewMap.get(page);
		else
			return null;
		}

	
	public static boolean FindHasPageView(int page){
		if(PageViewMap.containsKey(page))
			return true;
		else 
			return false;

	}

	public static void RemoveAllPageView(FrameLayout MainLayout){
		Iterator iter = PageViewMap.entrySet().iterator();
		while (iter.hasNext()) {
			try{
		Map.Entry entry = (Map.Entry) iter.next();
		int key = (Integer)entry.getKey();
		
		View val = (View)entry.getValue();
		Log.d("DDD", "RemoveAllPageView "+key);

		MainLayout.removeView(val);
				}catch(Exception e){

					}
		}
		PageViewMap.clear();
	}

}

