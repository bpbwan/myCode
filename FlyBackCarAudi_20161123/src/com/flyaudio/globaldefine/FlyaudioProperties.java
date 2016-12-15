package com.flyaudio.globaldefine;

import android.os.SystemProperties;
import android.util.Log;

public class FlyaudioProperties {
	public static int currentBackRadarView = 0;
	public static boolean currentneed = true;
	public static int cartype = 0;
	public static int currentBackVideoRadarOsdView = 0;
	public static int currentBackVideoRadarpageid = 0;
	public static int curentvideopageid = 0x708;

	public FlyaudioProperties() {
		// TODO Auto-generated constructor stub
	}

	/* 鏍规嵁鎵�鑾峰彇鐨勫睘鎬у�煎姩鎬佸姞杞介〉 */
	static {

		String ManchineNum = SystemProperties.get("fly.version.mcu");
		char c[] = ManchineNum.toCharArray();
		if (!ManchineNum.equals(""))
			cartype = (c[3] - '0') * 100 + (c[4] - '0') * 10 + (c[5] - '0');
		switch (cartype) {

		case 175:
			currentneed = false;
		default:
			currentneed = true;
			break;
		}

	}

	public static boolean needchangpage() {
		return cartype == 175;

	}

	public static int GetBackCarpageid() {
		String Radar = SystemProperties.get("fly.backcar.radar");
		if (currentneed) {
			if (("").equals(Radar)) {
				currentBackRadarView = 0;
				currentBackVideoRadarpageid = PageID.PAGE_NOVIDEO_TIP;
			} else if (Radar.equals("0")) {
				currentBackRadarView = 0;
				currentBackVideoRadarpageid = PageID.PAGE_NOVIDEO_TIP;
			} else if (Radar.equals("1")) {
				currentBackRadarView = 1;
				currentBackVideoRadarpageid = PageID.PAGE_NOVIDEO_RADAR;
			}
		} else {
			currentBackRadarView = 0;
			currentBackVideoRadarpageid = PageID.PAGE_NOVIDEO_TIP;
		}
		// Log.d("BackCar", "Radar: " + currentBackVideoRadarpageid);
		return currentBackVideoRadarpageid;
	}

	public static int GetBackCarMainpageid() {
		if (cartype == 175) {
			int currentcartype = 0;
			int currentcamera = 0;
			String CARTYPE = SystemProperties.get("fly.set.cartype");
			String CAMERATYPE = SystemProperties.get("fly.set.cameratype");
			if (("").equals(CARTYPE)) {
				currentcartype = 0;
			} else if (CARTYPE.equals("ES")) {
				currentcartype = 0;
			} else if (CARTYPE.equals("NX")) {
				currentcartype = 1;
			} else if (CARTYPE.equals("IS")) {
				currentcartype = 2;
			}

			if (("").equals(CAMERATYPE)) {
				currentcamera = 0;
			} else if (CAMERATYPE.equals("0")) {
				currentcamera = 0;
			} else if (CAMERATYPE.equals("1")) {
				currentcamera = 1;
			}

			if (currentcartype == 0)
				curentvideopageid = PageID.PAGE_es250_02_VIDEO;
			else if (currentcartype == 1 && currentcamera == 0)
				curentvideopageid = PageID.PAGE_nx300_01_VIDEO;
			else if (currentcartype == 1 && currentcamera == 1)
				curentvideopageid = PageID.PAGE_nx300_02_VIDEO;
			else if (currentcartype == 2 && currentcamera == 0)
				curentvideopageid = PageID.PAGE_is250_01_VIDEO;
			else if (currentcartype == 2 && currentcamera == 1)
				curentvideopageid = PageID.PAGE_is250_01_VIDEO;
		}
		Log.i("JumpPage", "currentReverseType + !" + curentvideopageid);
		return curentvideopageid;
	}

}
