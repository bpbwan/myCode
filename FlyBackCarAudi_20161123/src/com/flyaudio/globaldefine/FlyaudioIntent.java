package com.flyaudio.globaldefine;

import java.util.HashMap;

import android.os.SystemProperties;
import android.util.Log;
import com.flyaudio.backcar.R;

public class FlyaudioIntent {

	public static final String TAG = "BackCarFlyaudioIntent";
	/*
	 * 
	 * public static int currentBackRadarView = 0; public static int
	 * currentNoSignTip = 0; public static int currentRadarPage = 0;
	 * 
	 * //public static int currentReverseType = 0;
	 * 
	 * 
	 * 
	 * static {
	 * 
	 * int cartype =0; String ManchineNum =
	 * SystemProperties.get("fly.version.mcu"); char c[]
	 * =ManchineNum.toCharArray(); cartype = (c[3] - '0')*100 + (c[4] - '0')*10
	 * + (c[5] - '0');
	 * 
	 * switch(cartype) { case 165: case 815: case 136: currentBackRadarView = 1;
	 * // currentNoSignTip = 0; // currentRadarPage = 0; break;
	 * 
	 * case 175: currentBackRadarView = 0; currentNoSignTip = 1;
	 * currentRadarPage = 1; break; default: // currentBackRadarView = 0; //
	 * currentNoSignTip = 0; // currentRadarPage = 0; break; }
	 * 
	 * }
	 * 
	 * // public static int[] Reverselayouts = new int[] { //
	 * R.layout.lexus_es250_02_layout, // R.layout.lexus_nx300_01_layout, //
	 * R.layout.lexus_nx300_02_layout, // R.layout.lexus_is250_01_layout};
	 * 
	 * // radar public static int[] BackRadarViewlayouts = new int[] {
	 * R.layout.fly_backcar_radar_layout, R.layout.golf_backcar_radar_layout};
	 * 
	 * public static int[] BackVideoRadarViewlayouts = new int[] {
	 * R.layout.fly_backcarvideo_radar_layout,
	 * R.layout.golf_backcarvideo_radar_layout};
	 * 
	 * //BackCarNosign public static int[] Back_nosign_layout = new int[] {
	 * R.layout.fly_back_nosign_layout, R.layout.lexus_back_nosign_layout };
	 */
	public static HashMap<Integer, Integer> PageMap = new HashMap<Integer, Integer>();

	static {
		// Log.i("JumpPage", "currentReverseType + !" + currentReverseType);
		PageMap.put(0x0703, 1);
		PageMap.put(0x0707, 2);
		PageMap.put(0x0708, 3);
		PageMap.put(0x0709, 4);
		PageMap.put(0x070A, 5);
		PageMap.put(0x070B, 6);
		PageMap.put(0x070C, 7);
	}

	public static String GetExtraPage(int pageid) {
		String retStr = null;
		switch (pageid) {
		case 0x0705:
			retStr = "705";
			break;
		case 0x0706:
			retStr = "706";
			break;
		}
		return retStr;
	}
}
