package com.flyaudio.backcar.util;

import java.math.BigDecimal;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.os.Messenger;
import android.util.Log;

import com.flyaudio.backcar913.*;
import com.flyaudio.backcar.*;
import com.flyaudio.backcar913.BackCar913Service;
import com.flyaudio.globaldefine.FlyaudioIntent;
import com.flyaudio.globaldefine.FlyaudioProperties;
import com.flyaudio.globaldefine.PageID;
import com.flyaudio.globaldefine.UIAction;
import com.flyaudio.msg.HandleServiceMsg;

import android.os.Message;
import android.os.Handler;
import android.os.Messenger;
import android.os.Bundle;
import android.os.SystemProperties;

import com.flyaudio.backcar.animator.*;

import android.view.Gravity;
import android.view.View;

import com.xdroid.animation.anim.TelescopicAnimation.TelescopicTargetCallback;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.widget.FrameLayout;
import android.os.SystemProperties;

public class FlyUtil {
	static int SCREEN_W = 0;
	static int SCREEN_H = 0;
	public static int FRONTVIEW = 1;
	public static int BACKFLAG = 2;
	public static int CVBBACKFLAG = 3;
	public static int USBBACKFLAG = 4;
	
	public static void InitScreenSize(final int width, final int high) {
		SCREEN_W = width;
		SCREEN_H = high;
	}

	public static int GET_SCREEN_W() {

		return SCREEN_W;
	}

	public static int GET_SCREEN_H() {
		return SCREEN_H;

	}

	public static void ChangeViewSize(final View v, int width, int height) {
		if (v != null) {
			android.view.ViewGroup.LayoutParams lp = v.getLayoutParams();
			if (width > 0)
				lp.width = width;
			if (height > 0)
				lp.height = height;
			v.setLayoutParams(lp);
		}
	}

	public static void ChangeViewshow(final View v, boolean flag) {
		if (v != null) {
			if (flag)
				v.setVisibility(View.VISIBLE);
			else
				v.setVisibility(View.GONE);
		}
	}

	public static void ChangeViewPisition(final View v, int right, int top) {
		if (v != null) {
			FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) v
					.getLayoutParams();
			if (right >= 0)
				lp.rightMargin = right;
			if (top >= 0)
				lp.topMargin = top;
			v.setLayoutParams(lp);
		}
	}

	public static void ChangeViewPisitionAdd(final View v, int right, int top) {
		if (v != null) {
			FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) v
					.getLayoutParams();
			if (right >= 0)
				lp.rightMargin += right;
			if (top >= 0)
				lp.topMargin += top;
			v.setLayoutParams(lp);
		}
	}
	
	public static void ChangeViewLayout(final View v, int width, int height, int gravity, int x){
		FrameLayout.LayoutParams s = (android.widget.FrameLayout.LayoutParams) 
				v.getLayoutParams();
		
		if(gravity == Gravity.LEFT)
		s.leftMargin = x;
		else 
			s.rightMargin = x;
		s.gravity = gravity;
		v.setLayoutParams(s);

		}
	
	public static String HexToTag(int data) {

		String ret = String.format("%08x", data);
		return ret;
	}
	
	public static Resources  getReSource(){
		return PluginProxyContext.getInstance().getResources();
	}

//	public static void ChangeViewVisable(View v, boolean needReversal ){
//		if(v != null){
//			if(needReversal){
//				if(v.getVisibility() == View.VISIBLE)
//					v.setVisibility(View.GONE);
//				else {
//					v.setVisibility(View.VISIBLE);
//					}
//			}
//		}
//	}
	
	public static float AddBigDecimal(float m1, float m2){
		
		
		BigDecimal s1  = new BigDecimal(String.valueOf(m1));
		BigDecimal s2  = new BigDecimal(String.valueOf(m2));
		
		return s1.add(s2).floatValue();
	}
	
	public static float SubBigDecimal(float m1, float m2){
		
		
		BigDecimal s1  = new BigDecimal(String.valueOf(m1));
		BigDecimal s2  = new BigDecimal(String.valueOf(m2));
		
		return s1.subtract(s2).floatValue();
	}
	
	public static float MultiBigDecimal(float m1, float m2){
		BigDecimal s1  = new BigDecimal(String.valueOf(m1));
		BigDecimal s2  = new BigDecimal(String.valueOf(m2));
		
		return s1.multiply(s2).floatValue();
	}
	
	public static float DivBigDecimal(float m1, float m2){
		BigDecimal s1  = new BigDecimal(String.valueOf(m1));
		BigDecimal s2  = new BigDecimal(String.valueOf(m2));
		
		return s1.divide(s2).floatValue();
	}
	
}
