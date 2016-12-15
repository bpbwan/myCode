package com.flyaudio.backcar.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;

import android.util.Log;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.view.ViewGroup;
import android.view.WindowManager;

public class OverLayManager {

	private Context mContext;
	String TAG = "OverLayManager";
	private  HashMap<Integer, View> OverLayMap = new HashMap<Integer, View>();
	private WindowManager m_WM;
	
	public  OverLayManager(WindowManager  wm){
		this.m_WM = wm;

		}

	
	public  void PutPageMap(int overlay, View v){
		OverLayMap.put(overlay, v);
		Log.d("DDD", "AddPageView "+overlay);
	}

	public  void RemovePageMap(int page){
		
		OverLayMap.remove(page);
		Log.d("DDD", "RemodePageView "+page);

		}
	


	public  View GetPageView(int page){
		if(OverLayMap.containsKey(page))
			return OverLayMap.get(page);
		else
			return null;
		}

	
	public  boolean FindHasPageView(int page){
		if(OverLayMap.containsKey(page))
			return true;
		else 
			return false;
	}

	private boolean AddOverLayToWM(View view,  WindowManager.LayoutParams lp){
			if(m_WM != null && view != null){
				m_WM.addView(view, lp);
				return true;
				}
			return false;
		}
	
	private boolean RemoveOverLayFromWM(View view){
			if(view != null)
			m_WM.removeView(view);

			return true;
		}

	private void updateViewLayout(View view, ViewGroup.LayoutParams params){
			m_WM.updateViewLayout(view, params);

		}
	
	public void updateViewLayout(int overlay, ViewGroup.LayoutParams params){
			if(FindHasPageView(overlay))
				updateViewLayout(GetPageView(overlay), params);

		}
	
	public void AddOverLayToWM(View view,  WindowManager.LayoutParams lp, int overlay){
		if(!FindHasPageView(overlay)){
			
			if(AddOverLayToWM(view, lp))
				PutPageMap(overlay,view);
			}else 
			Log.i(TAG, "AddOverLayToWM  has the same overlayView overlayId "+overlay);
		}

	
	public void RemoveOverLayById(int overlay){
			if(FindHasPageView(overlay))
				RemoveOverLayFromWM(GetPageView(overlay));
			RemovePageMap(overlay);
		}
	

	public  void RemoveAllOverLayView(){
		Iterator iter = OverLayMap.entrySet().iterator();
		while (iter.hasNext()) {
			try{
		Map.Entry entry = (Map.Entry) iter.next();
		int key = (Integer)entry.getKey();
		
		View val = (View)entry.getValue();
		Log.d("DDD", "RemoveAllPageView "+key);

		m_WM.removeView(val);
				}catch(Exception e){

					}
		}
		OverLayMap.clear();
	}

	public View findViewWithTag(String controlID){
		View ret = null;
		Iterator iter = OverLayMap.entrySet().iterator();
		while (iter.hasNext()) {
			try{
		Map.Entry entry = (Map.Entry) iter.next();
		int key = (Integer)entry.getKey();
	
		View val = (View)entry.getValue();
		ret = val.findViewWithTag(controlID);
		if(ret != null)
			return ret;
				}catch(Exception e){

					}
		}
	
		return null;
	}
	
}

