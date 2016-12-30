package com.flyaudio.backcar913;

public class BackCarTag {

	// 8327

	public static final String SETLIGHTPAGE = "070d0020";
	public static final String PAGE_913A4SETTING = "070d0021";
	public static final String VIDEOPAGE = "070d0010";
	public static final String PAGE_CVB_LAYOUT = "070d0030";
	public static final String PAGE_CVB_SV_LAYOUT = "070d0031";
	
	public static final int TRACKCAPOPEN = 0x00100001;

	public static final int STALLENTER = 0x00100002;

	public static final int small_car = 0x00070120;
	public static final int bgusb_camera = 0x00070633;
	public static final int bgcvb = 0x00070632;
	public static final int bg913Rear_150 = 0x00070631;
	public static final int bg913Front_150 = 0x00070630;

	public static final int RadarDistance = 0x00070500;
	
	public static final int RADAR_FRONT_TRACK = 0x00070601;
	public static final int RADAR_REAR_TRACK = 0x00070602;

	public static final int car_bg = 0x00070632;

	public static final int SET150VIDEO_CID = 0x070d0003;
	public static final int SET180VIDEO_CID = 0x070d0005;

	public static final int SETLIGHT_CID = 0x70d0006;

	public static final int BIG_RADAR_UP1 = 0x00070021;
	public static final int BIG_RADAR_UP2 = 0x00070022;
	public static final int BIG_RADAR_UP3 = 0x00070024;
	public static final int BIG_RADAR_UP4 = 0x00070025;
	public static final int BIG_RADAR_DOWN1 = 0x00070027;
	public static final int BIG_RADAR_DOWN2 = 0x00070028;
	public static final int BIG_RADAR_DOWN3 = 0x0007002a;
	public static final int BIG_RADAR_DOWN4 = 0x0007002b;

	// benz

	public static final int CHOICE_BNT = 0x070e0015;
	public static final int CHOICE_BNT2 = 0x070e0017;

	public static final int CHOICEBACK_BNT = 0x070e0016;

	public static final int LIGHTTEXT = 0x070f0012;
	public static final int VIDEOTOCHOICE = 0x070d0001;
	public static final String VIDEOBOTTOMBTN = "070d0000";
	public static final String VIDEOTOP = "070d0002";

	public static final String CHOICEPAGE913 = "070e0010";
	public static final String RADARPAGE913 = "070f0000";
	public static final String RADARPAGECHILD = "070f0001";

	public static final int CAMERA913_FR_MODE = 0x070e0018;

	public static final int CAMERA913_F_MODE = 0x070e0019;

	public static final int CAMERA913_R_MODE = 0x070e001a;
	public static final int CAMERA913_F_R_MODE = 0x070e001b;
	public static final String FR_MODE_TEXT = "070e0020";
	public static final String F_MODE_TEXT = "070e0021";
	public static final String R_MODE_TEXT = "070e0022";
	public static final String F_R_MODE_TEXT = "070e0023";
	public static final String FR_TEXT_COLOR_C = "#FFFFFFFF";
	public static final String FR_TEXT_COLOR_U = "#FF545860";

	// AUDI A3
	public static final int Audi_SELECT_TEXT = 0x0701121a;

	public static final int Audi_SELECTBUTTON = 0x07011218;

	public static final int Audi_CHOICEBTN = 0x07011215;
	public static final int Audi_CHOICEBTN2 = 0x07011217;

	public static final int Audi_CHOICEBACKBUTTON = 0x07011216;

	public static final int Audi_F_MODE = 0x0701121b;

	public static final int Audi_R_MODE = 0x0701121c;

	public static final int Audi_FR_MODE = 0x0701121d;

	public static final int Audi_F_R_MODE = 0x0701121e;

	public static final String Audi_F_TEXT = "07011220";
	public static final String Audi_R_TEXT = "07011221";
	public static final String Audi_TEXT_COLOR_U = "#FF272727";
	public static final String Audi_TEXT_COLOR_C = "#FFFFFFFF";

	// AUDI A4 A3 Q3
	public static final int A4_F_150VIDEO_CID = 0x07000020;
	public static final int A4_F_180VIDEO_CID = 0x07000021;
	public static final int A4_R_150VIDEO_CID = 0x07000022;
	public static final int A4_R_180VIDEO_CID = 0x07000023;
	public static final int A4_AUDI_SET_CID = 0x07000024;
	public static final int A4_AUDI_ClOSE_SYS_CID = 0x07000025;
	public static final int A4_AUDI_FRONT_SOUND_CID = 0x07000026;
	public static final int A4_AUDI_REAR_SOUND_CID = 0x07000027;
	public static final int A4_AUDI_ScreenSize_full = 0x07000028;
	public static final int A4_AUDI_ScreenSize_half = 0x07000029;

	public static final int A4_AUDI_FRONT_SOUND_Low = 0x0700002a;
	public static final int A4_AUDI_FRONT_SOUND_Mid = 0x0700002b;
	public static final int A4_AUDI_FRONT_SOUND_High = 0x0700002c;
	public static final int A4_AUDI_REAR_SOUND_Low = 0x0700002d;
	public static final int A4_AUDI_REAR_SOUND_Mid = 0x0700002e;
	public static final int A4_AUDI_REAR_SOUND_High = 0x0700002f;
	public static final int A4_AUDI_TITLE = 0x07000030;
	public static final int A4_AUDI_F_SOUND_VUALE = 0x07000031;
	public static final int A4_AUDI_R_SOUND_VUALE = 0x07000032;
	public static final int A4_AUDI_Float_Radar_Bg = 0x07000033;
	public static final int A4_ChangeBigOrSmall_radar = 0x07000035;

	public static final int DigInShowMainView = 0x07000036;
	public static final int SetHalfSurfaceView = 0x07000037;
	public static final int A4_AUDI_FLOAT_BG = 0x07000038;
	public static final int A4_AUDI_FLOAT_CAR = 0x07000039;
	public static final int A4_AUDI_FLOAT_CAR_FOCUS = 0x0700003a;
	public static final int A4_AUDI_OSD_SET_CID = 0x0700003b;
	public static final int A4_SMALL_CAR_BG = 0x0700003c;
	public static final int A4_AUDI_NOT_913_SET_CID = 0x0700003d;
	public static final int A4_AUDI_CVB_ScreenSize_full = 0x0700003e;
	public static final int A4_AUDI_CVB_CLOSE_SET = 0x0700003f;

	public static final int AUDI_FRONT_TONE_CID = 0x07000040;
	public static final int AUDI_REAR_TONE_CID = 0x07000041;

	public static final int AUDI_BACKTO_SETTING_CID = 0x07000042;
	// ��ת��������������
	public static final int AUDI_ROTATE_FRONT_SOUND_UI = 0x07000043;
	public static final int AUDI_ROTATE_REAR_SOUND_UI = 0x07000044;
	public static final int AUDI_ROTATE_FRONT_TONE_UI = 0x07000045;
	public static final int AUDI_ROTATE_REAR_TONE_UI = 0x07000046;

	public static final int AUDI_ROTATE_FRONT_SOUND_denote = 0x07000047;
	public static final int AUDI_ROTATE_FRONT_TONE_denote = 0x07000048;
	public static final int AUDI_ROTATE_REAR_SOUND_denote = 0x07000049;
	public static final int AUDI_ROTATE_REAR_TONE_denote = 0x0700004a;

	//Q3
	public static final int AUDI_BNT_EDGETRACK_CHANGE = 0x07021210;
	public static final int AUDI_BIG_RADAR_SHOW_ID = 0x07021211;
	public static final int AUDI_913_FRONT_ENUM_EDGETRACK_UI = 0x0700004b;
	public static final int AUDI_913_REAR_ENUM_EDGETRACK_UI= 0x0700004c;
	public static final int AUDI_CVB_REAR_ENUM_EDGETRACK_UI = 0x0700004d;
	public static final int AUDI_ENUM_EDGETRACK_UI = 0x0700004e;
	
	// benz
	public static final String CHOICEBG = "070e0011";
	public static final String CHOICECARBG = "070e0012";
	public static final String CHOICECIRCLE = "070e0013";

	public static final int VIDEOPAGE913 = 0x070d0010;

	public static final int CLICKED = 1;
	public static final int NOCLICK = 0;
	public static final int VIDEOEPAGE = 0;
	public static final int CHIOCEPAGE = 1;

	public static final int STARTBYUSER = 0x00008000;
	public static final int BACKTO913 = 0x00008001;
	public static final int BACKKEYEVENT = 0x00008002;
	public static final int RMLIGHTVIEW = 0x00008003;
	public static final int ADDLIGHTVIEW = 0x00008004;
	public static final int HOMEEYEVENT = 0x00008005;
	public static final int ANYKEYEVENT = 0x00008006;
	public static final int MODULEEXIT = 0x00008007;
	public static final int MODULEENTER = 0x00008008;
	public static final int F_VOICE_LEVEL = 0x00008009;
	public static final int R_VOICE_LEVEL = 0x0000800a;
	public static final int MODULEPREENTER = 0x0000800b;

	public static final int BLACKPAGE = 0x0000800c;

	public static final int DOWNLINE_NOTICE = 0x070d0000;
	
	// keycode
	public static final int ADDBTNVIEW = 1;
	public static final int RMBTNVIEW = 2;

	public static final int FLYKEY_RIGHT = 112;
	public static final int FLYKEY_LEFT = 124;

	public static final String SUBLINE = "persist.backcar.subline.height";
	public static final String AuxLine = "00070530";
	public static final int AUXLINE = 0x00070530;
	public static final String TRACKBG = "00070531";

	public static final int UITEST913_150 = 0x00070700;
	public static final int UITEST913_180 = 0x00070701;

	public static final int AUXLINE_UP = 0x00070703;
	public static final int AUXLINE_DOWN = 0x00070704;

	public static final String Notice_STR = "00070113";
	public static final String Setting_STR = "0701003b";
	
	public static final int SETBRIGHT_LEFT = 0x00070711;
	public static final int SETBRIGHT_RIGHT = 0x00070712;
	public static final int SETCONTRAST_LEFT = 0x00070713;
	public static final int SETCONTRAST_RIGHT = 0x00070714;
	public static final int SETHUE_LEFT = 0x00070715;
	public static final int SETHUE_RIGHT = 0x00070716;
	public static final int SETSTATURA_LEFT = 0x00070717;
	public static final int SETSTATURA_RIGHT = 0x00070718;

	public static final int FLYSEEKBAR1 = 0x00070721;
	public static final int FLYSEEKBAR2 = 0x00070722;
	public static final int FLYSEEKBAR3 = 0x00070723;
	public static final int FLYSEEKBAR4 = 0x00070724;
	public static final int FLYSEEKBAR5 = 0x00070725;
	public static final int FLYSEEKBAR6 = 0x00070726;
	public static final int FLYSEEKBAR7 = 0x00070727;

	//setting back
	public static final int TOUCH_BG = 0x00070720;

	public static final int SETTINGBACK = 0x00070710;
	public static final String SETTINGBACK_TAG = "00070710";

	public static final String INTKEY = "flyInt";
	public static final String STRKEY = "flyString";
	public static final String BOOLKEY = "flyboolean";
	public static final String TOUCH_DOWN = "touch_down";
	public static final String TOUCH_UP = "touch_up";
	public static final String TOUCH_MOVE = "touch_move";

	public static final String UI_FILLET_TAG = "00070540";
	public static final String UI_WIDE_TAG = "00070550";
	public static final String UI_RECT_TAG = "00070560";
	public static final String FLYBACKCARVIEW = "00070800";

}
