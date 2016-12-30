package com.flyaudio.backcar;

public class MsgType {
	// 类型
	public static final int MSG_SENG = 0;
	public static final int MSG_SENGDELAY = 1;
	public static final int MSG_REMOVE = 2;
	// 消息
	public static final int MSG_BACKCAR_START = 0;
	public static final int MSG_BACKCAR_STOP = 1;
	public static final int MSG_BACKCAR_RUNNING = 2;
	public static final int MSG_BACKCAR_STARTACTIVITY = 3;
	public static final int MSG_BACKCAR_STOPACTIVITY = 4;
	public static final int MSG_SIGNAL_READY = 5;
	public static final int MSG_SIGNAL_LOST = 6;
	public static final int MSG_CHECK_SIGNAL = 7;
	public static final int MSG_MONITOR = 8;
	public static final int MSG_STOPNOTICE = 9;
	public static final int MSG_SYSTEMEXIT = 10;
	public static final int MSG_SETGAMMA = 11;
	public static final int MSG_SHOWACTIVITY = 12;
	public static final int MSG_BACKCAR_CHOICE = 13;
	public static final int MSG_BACKCAR_EXIT = 14;
	public static final int MSG_BACKTO913 = 15;
	public static final int MSG_913TOOTHER = 16;
	public static final int MSG_BACKCAR_START_913 = 17;

	public static final int MSG_BACKCAR_RETRY = 18;
	public static final int MSG_BACKCAR_RETRYPREVIEW = 19;
	public static final int MSG_BACKCAR_RETRYEXIT = 20;
	public static final int MSG_DELAYSTOP = 21;
	public static final int BLACKPAGE = 22;
	public static final int MSG_913START_CVB = 23;
	public static final int MSG_TIME_COUNT = 24;
	public static final int MSG_ONSTOP_DELAY = 25;
	
	// 时间
	public static final int MSG_ONSTOP_DELAY_TIMEOUT = 600;
	public static final int STARTACTIVITY_TIMEOUT = 300;
	public static final int STOPACTIVITY_TIMEOUT = 1500;
	public static final int STOPNOTICE_TIMEOUT = 3000;
	public static final int SIGNAL_LOSE_TIMEOUT = 1800;
	public static final int RESUME_ACTIVITY_TIMEOUT = 200;
	public static final int SIGNALREADY_TIMEOUT = 2000;
	public static final int SYSTEMEXIT_TIMEOUT = 3500;
	public static final int SETGAMMA_TIMEOUT = 200;
	public static final int CAMRETRY_TIMEOUT = 2000;
	public static final int PREVIEW_TIMEOUT = 400;
	public static final int RETRYEXIT_TIMEOUT = 2000;
	public static final int DIGINCHECK_TIMEOUT = 1000;
	public static final int MSG_DELAYSTOP_TIMEOUT = 800;

	public static final int MSG_BACKCARACTIVITY_RESUME = 0;
	public static final int MSG_BACKCARACTIVITY_STOP = 1;
	public static final int MSG_BACKCARACTIVITY_DESTROY = 2;

	public static final int COLOR_ARM2 = 0;
	public static final int COLOR_BackCar = 1;

	// 913 Transfer
	public static final int TRANSFER_BACKCAR_913_START = 15;
	public static final int TRANSFER_BACKCAR_913_STOP = 16;
	public static final int TRANSFER_STALLD_913_START = 17;
	public static final int TRANSFER_STALLD_913_STOP = 18;
	public static final int TRANSFER_USER_913_ENTER = 19;
	public static final int TRANSFER_STALLD_913_EXIT = 20;

	public static final int MSG_BACKCAR_913_STOP = 21;
	public static final int MSG_BACKCAR_913_STALLD_START = 22;
	public static final int MSG_BACKCAR_913_STALLD_AUTO_STOP = 23;
	public static final int MSG_BACKCAR_913_ENTER_REAR_VIDEO = 24;
	public static final int MSG_BACKCAR_913_ENTER_FRONT_VIDEO = 25;
	public static final int MSG_BACKCAR_913_CHECKDEVICE = 26;
	public static final int MSG_BACKCAR_913_U_START = 27;
	public static final int STALLD_STOP = 28;


	public static final int STALLD_STOP_TIMEOUT = 1000;
	public static final int MSG_BACKCAR_913_STOP_TIMEOUT = 500;
	public static final int MSG_BACKCAR_913_STALLD_START_TIMEOUT = 0;
	public static final int MSG_BACKCAR_913_STALLD_AUTO_STOP_TIMEOUT = 15000;
	public static final int MSG_BACKCAR_913_U_START_TIMEOUT = 0;
	
	public static final int MSG_SETHALFSURFACE_TIMEOUT = 0;
	public static final int MSG_SETFULLSURFACE_TIMEOUT = 100;
	public static final int MSG_REMOVE_FLOATRADAR_TIMEOUT = 8000;

	public static final int FR_MODE = 0;
	public static final int F_MODE = 1;
	public static final int R_MODE = 2;
	public static final int F_ORIR_MODE = 3;

	// USB Camera
	public static final byte MSG_RETRYOPEN = 0x00;
	public static final byte MSG_HASOPEN = 0x01;
	public static final byte MSG_RETRYPREVIEW = 0x02;
	public static final byte MSG_RETRYEXIT = 0x03;
	public static final byte MSG_REMOVEEXIT = 0x04;
	public static final byte MSG_OPEN = 0x05;
	public static final byte MSG_STARTFAIL = 0x06;

	// TestBackCarMode
	public static final byte MSG_START_CVB = 1;
	public static final byte MSG_START_913 = 2;
	public static final byte MSG_START_CAM = 3;
	public static final byte MSG_STOP = 4;
	public static final byte MSG_USER_START = 5;
	public static final byte MSG_STADLL_D_IN = 6;
	public static final byte MSG_STADLL_D_OUT = 7;

	// backcarmodule
	public static final int FLYOBJ = 0;
	public static final int OBJFOCUS = 1;
	public static final int OBJVISIBLE = 2;
	public static final int OBJSETBG = 3;
	public static final int SETLIGHT = 4;
	public static final int COMMON = 5;

	public static final int SETSURFACE = 6;
	public static final int REMOVE_FLOATRADAR = 7;
	public static final int GEY_FLYPAGE = 8;
	public static final int COMSEEKBAR = 9;

	public static final int RESIZESURFACE = 10;
	public static final int SHOW_TRACKBG = 11;
	public static final int SHOW_TRACK = 12;
	public static final int SETDRAW_TRACK = 13;
	public static final int TRACKBLACKPAGE = 14;
	public static final int TRACK_REBACK = 15;
	public static final int FRONT150VDO = 16;
	public static final int REAR150VDO = 17;
	
	
	//g6 module 
	public static final int	REQUEST_SYNCANGLE = 18;
	public static final int	REQUEST_SYNCANGLE_PERSECOND = 19;
	
	public static final int	REQUEST_SYNCANGLE_TIMEOUT = 1000;
	
	// BaseModule
	public static final int BLACKPAGE_REMOVE = 0;
	public static final int STOP_SETTING_COLOR = 1;
	public static final int SHOW_SETTING_COLOR = 2;
	public static final int FLOATRADAR_SHOW_DELAY = 3;
	public static final int FLOATRADAR_HIDE_DELAY = 4;
	
	public static final int FLOATRADAR_HIDE_DELAY_TIMEOUT = 300;
	public static final int FLOATRADAR_SHOW_DELAY_TIMEOUT =300;
	public static final int BLACKPAGE_REMOVE_TIMEOUT = 500;
	public static final int MSG_GEY_FLYPAGE_TIMEOUT = 80;
	public static final int SyncUser_TIMEOUT =400;
	public static final int RESIZESURFACE_TIMEOUT = 100;
	public static final int SHOW_SETTING_TIMEOUT = 3000;
	public static final int SHOW_TRACKBG_TIMEOUT = 80;
	public static final int CVBSHOW_TRACK_TIMEOUT = 0;
	public static final int SHOW_TRACK_TIMEOUT = 1600;
	public static final int SHOW_CVBTRACK_TIMEOUT = 100;
	public static final int SETDRAW_TRACK_TIMEOUT = 0;
	public static final int BLACKPAGE_REMOVE_TIMEOUT2 = 0;
	public static final int TRACKBLACKPAGE_REMOVE_TIMEOUT = 2500;
	public static final int TRACK_REBACK_TIMEOUT = 500;
	
	
	public static final int UI_UPDATE_TIME = 100;
	
}
