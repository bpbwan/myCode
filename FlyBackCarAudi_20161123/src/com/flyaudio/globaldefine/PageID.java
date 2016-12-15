package com.flyaudio.globaldefine;

import com.flyaudio.backcar913.BackCarTag;

public class PageID {
	// MODULE_ID_BACK 0x07
	public static final int PAGE_BACK_NOVIDEO = 0x0700;
	public static final int PAGE_REMOVE = 0x0702;
	public static final int PAGE_NOVIDEO_RADAR = 0x0703;
	public static final int PAGE_VDO_OSD_UI = 0x0705;
	public static final int PAGE_NOVIDEO_TIP = 0x0707;
	public static final int PAGE_BACK_VIDEO = 0x0708;
	public static final int PAGE_913CHOICE = 0x070F;
	public static final int PAGE_913VIDEO_UI = 0x0800;
	public static final int PAGE_913SETLIGHT = 0x070E;
	public static final int PAGE_913A4SETTING = 0x0710;
	public static final int PAGE_TRACK_BG = 0x0711;
	
	public static final int PAGE_es250_02_VIDEO = 0x0709;
	public static final int PAGE_nx300_01_VIDEO = 0x070A;
	public static final int PAGE_nx300_02_VIDEO = 0x070B;
	public static final int PAGE_is250_01_VIDEO = 0x070C;

	public static final int PAGE_913FLOATRADAR = 0x0801;
	public static final int PAGE_913A4SETTING_Half = 0x0802; // 800*480 ����״̬��������
	public static final int PAGE_CVB_LAYOUT = 0x0803;

	public static final int PAGE_CVBA4SETTING_LAYOUT = 0x0804;
	public static final int PAGE_CVBA4SETTING_Half = 0x0805; // 800*480
																// ����״̬��CVB������

	public static final int PAGE_FRONT_SOUND_SET = 0x0806;
	public static final int PAGE_REAR_SOUND_SET = 0x0807;
	public static final int PAGE_FRONT_TONE_SET = 0x0808;
	public static final int PAGE_REAR_TONE_SET = 0x0809;
	public static final int PAGE_CVB_SV_LAYOUT = 0x080a;
	public static final int PAGE_CVBSV_SETTING_LAYOUT = 0x0810;
	
	public static final int PAGE_COMMON_SETTING = 0x0900;
	public static final int PAGE_COMMON_TRACKTOOL = 0x0901;
	public static final int PAGE_COMMON_TRACKCAP = 0x0902;

	public static String GetPageTag(int layoutpageid) {
		String pagetag = "";
		switch (layoutpageid) {
		case 0x070E:
			pagetag = BackCarTag.SETLIGHTPAGE;
			break;
		case 0x0804:
		case 0x0710:
			pagetag = BackCarTag.PAGE_913A4SETTING;
			break;
		case 0x0803:
		case 0x080a:
			pagetag = BackCarTag.PAGE_CVB_LAYOUT;
			break;

		}
		return pagetag;
	}

	
	public static final String PAGE_VDO_OSD_UI_TAG = "0705";
}
