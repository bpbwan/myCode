package com.android.flyaudioui.object;

public class FlyaudioCarName {

    // public static final String TIGUAN = "TIGUAN";//途观
    // public static final String MAGOTAN = "MAGOTAN";//迈腾
    // public static final String CAMRY = "CAMRY";//凯美瑞
    // public static final String SUBARU = "SUBARU";//斯巴鲁

    public static final String NORMAL = "0";// 正常
                                            // gps\dvd\radio\media\bt\box(set\btmusic\blcd\aux)
    // public static final int NORMAL_ALL = 1;//正常
    // gps\dvd\radio\media\bt\box(set\btmusic\blcd\aux\tpms\tv\ipod)

    public static final String AC = "1";// 空调gps\dvd\radio\media\bt\box(set\btmusic\blcd\aux\ac)

    public static final String CAR_INFO = "2";// 车辆信息
                                              // gps\dvd\radio\media\bt\box(set\btmusic\blcd\aux\carInfo)

    public static final String BACKCAR_ALL = "3";// 全景倒车
                                                 // gps\dvd\radio\media\bt\box(set\btmusic\blcd\aux\backcarAll)

    public static final String SYNC = "5";// sync

    // type_version
    public static final String HAVE_DVD_VERSION = "0";// 有功放的Audio

    public static final String NO_DVD_VERSION = "1";// 有功放的Audio

    //
    public static String naviIntent = "";

    public static String FlyCarName = null;

    public static String FlyTypeVersion = null;

}
