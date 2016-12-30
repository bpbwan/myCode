package com.flyaudio.backcar;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;

import android.content.pm.PackageManager.NameNotFoundException;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.util.Log;

import android.content.res.Configuration;
import java.util.Locale;

import com.flyaudio.backcar913.FlyBaseListener;

public class PluginProxyContext extends ContextWrapper {

	private Context context;
	private AssetManager mAssetManager = null;
	private Resources mResources = null;
	private LayoutInflater mLayoutInflater = null;
	private Theme mTheme = null;
	private String packageName = null;
	public String[] CarType = new String[10];
	public boolean LoadState = false;
	public boolean externCarType = false;
	// *****************璧勬簮ID绫诲瀷*******************
	public static final String LAYOUT = "layout";
	public static final String ID = "id";
	public static final String DRAWABLE = "drawable";
	public static final String STYLE = "style";
	public static final String STRING = "string";
	public static final String COLOR = "color";
	public static final String DIMEN = "dimen";
	public static final String RAW = "raw";
	public static final String BOOL = "bool";
	public static final String INTEGER = "integer";
	public static final String CARMODEL = "module";
	private static final String TAG = "BackCarProxy";

	public static int resCarType = 0;
	private Resources ExSkinResources = null;
	public static Context externContext = null;
	private static final String ExternSkinPagekanName = "com.flyaudio.backcar.skin";

	public static Context externModuleContext = null;
	private static final String PlugPagekageName = "com.flyaudio.object";

	//different_car_plug  
	public boolean  different_car_plug = false; 
	
	public PluginProxyContext(Context base) {
		super(base);
		this.context = base;
		proxyContext = this;
		try {
			externContext = base.createPackageContext(ExternSkinPagekanName,
					Context.CONTEXT_IGNORE_SECURITY);
			ExSkinResources = externContext.getResources();
			Log.i(TAG, "skinContext:" + externContext);
		} catch (NameNotFoundException e) {
			Log.i(TAG, "skinContext:" + e.toString());

		}
	}

	private static PluginProxyContext proxyContext;

	public static PluginProxyContext getInstance() {
		return proxyContext;
	}

	public void loadCarTypeRes(int cartype) {
		Log.i(TAG, "real cartype " + cartype);
		switch (cartype) {

		case 519: // 新增
		case 521:
		case 557:

		case 815:
		case 830:
		case 136:
			cartype = 136;
			FlyBaseListener.carTag = FlyBaseListener.CAR.G6Z;
			break;
		case 561: //chuanqi
			cartype = 561;
			FlyBaseListener.carTag = FlyBaseListener.CAR.G6Z;
			break;
		case 175:
			FlyBaseListener.carTag = FlyBaseListener.CAR.G6Z;
			break;
		case 215: // Gx551
		case 551:
		case 214:
			cartype = 214; //not change
			FlyBaseListener.carTag = FlyBaseListener.CAR.BENZ;
			break;
		case 802: // GX8802 CLA
		case 840:
			cartype = 840;
			FlyBaseListener.carTag = FlyBaseListener.CAR.BENZ;
			break;
		case 112: // AUdi
		case 113:
			FlyBaseListener.carTag = FlyBaseListener.CAR.AUDIQ3;
			break;
		case 114:
		case 216:
		case 556:
			FlyBaseListener.carTag = FlyBaseListener.CAR.AUDIA4;
			break;
		case 61:

			FlyBaseListener.carTag = FlyBaseListener.CAR.HYUNDAI;
			break;
		default:
			cartype = 100;
			FlyBaseListener.carTag = FlyBaseListener.CAR.G6Z;
			break;
		}

		resCarType = cartype;
		
		
		LoadState = true;
		externCarType = externCarType_loadResources(PlugPagekageName);

		if (externCarType) { // 外部倒车资源
			String CarModule = getString(CARMODEL, "");

			if (!CarModule.equals(""))
				FlyBaseListener.carTag = FlyBaseListener.CAR.valueOf(CarModule
						.toUpperCase(Locale.ENGLISH));

		} else {
			// 内部倒车资源
			String car = SearchCar(cartype);
			if (car == null)
				return;

			loadResources(car, false);
		}
		Log.d("DDD", "CarModule  ----------- " + FlyBaseListener.carTag
				+ "--------------");
		different_car_plug = getBool("different_car_plug", false);
		int tmpdata = getInteger("fake_carType", 0);
		if(tmpdata != 0)
			resCarType = tmpdata;
		
	}

	private String SearchCar(int cartype) {
		AssetManager s = context.getResources().getAssets();
		String car = "" + cartype;
		Log.i(TAG, "cartype = " + car);
		try {
			CarType = s.list("");
			if (CarType == null)
				return null;
			int i = 0;
			int j = 0;
			for (; i < CarType.length; i++) {
				if (CarType[i] != null) {
					if (".apk".equals(CarType[i].substring(
							CarType[i].length() - 4, CarType[i].length()))) {

						if (car.equals(CarType[i].substring(0, 3))) {
							Log.i(TAG, CarType[i]);
							return CarType[i];
						}
					}
				} else
					break;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void loadResources(String resPluginName, boolean testModel) {
		try {
			File outFile = copy(resPluginName, testModel);
			AssetManager assetManager = AssetManager.class.newInstance();
			Method addAssetPath = assetManager.getClass().getMethod(
					"addAssetPath", String.class);
			addAssetPath.invoke(assetManager, outFile.getPath());
			Log.i(TAG, "path  " + outFile.getPath());
			mAssetManager = assetManager;
		} catch (Exception e) {
		//	e.printStackTrace();
		}
		Resources superRes = super.getResources();
		mResources = new Resources(mAssetManager, superRes.getDisplayMetrics(),
				superRes.getConfiguration());

		this.packageName = mResources.getResourcePackageName(R.string.app_name);// 鑾峰彇鎻掍欢鍖呭悕
		getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		initLocalLanguage();
	}

	public boolean externCarType_loadResources(String resPluginName) {
		try {
			Log.i(TAG, "externCarType_loadResources");
			externModuleContext = this.context.createPackageContext(
					resPluginName, Context.CONTEXT_IGNORE_SECURITY);

			mResources = externModuleContext.getResources();
			mAssetManager = externModuleContext.getAssets();

			this.packageName = resPluginName;// 鑾峰彇鎻掍欢鍖呭悕
			getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			initLocalLanguage();
		} catch (NameNotFoundException e) {
			Log.i(TAG, "externCarType_loadResources  null");
		//	e.printStackTrace();
			return false;
		}
		return true;
	}

	public int getIdentifier(String type, String name) {
		try {
			return mResources.getIdentifier(name, type, packageName);
		} catch (Exception e) {
			Log.d(TAG, " cant not find <type> " + type + "  <resname> " + name
					+ " id");
			e.printStackTrace();
		}
		return 0;
	}

	public int getRdrawable(String name) {

		return getIdentifier("drawable", name);
	}

	public int getrawid(String name) {
		return mResources.getIdentifier(name, RAW, packageName);
	}

	public int getId(String name) {
		return mResources.getIdentifier(name, ID, packageName);
	}

	public View getLayout(int currentpageid) {
		String name = "";
		Log.i(TAG, "currentpageid = " + currentpageid);
		isNeedSetLanguage();

		// currentpageid = 0x703;
		switch (currentpageid) {
		case 0x0703:
			name = "fly_backcar_radar_layout";
			break;
		case 0x0707:
			name = "fly_back_nosign_layout";
			break;
		case 0x0708:
			name = "fly_backcarvideo_radar_layout";
			if(different_car_plug)
				name = name+"_"+resCarType;
			break;

		case 0x0709:
			name = "lexus_es250_02_layout";
			break;
		case 0x070A:
			name = "lexus_nx300_01_layout";
			break;
		case 0x070B:
			name = "lexus_nx300_02_layout";
			break;
		case 0x070C:
			name = "lexus_is250_01_layout";
			break;

		case 0x070D:
			name = "fly_913_choice_layout";
			break;
		case 0x070E:
			name = "setlight";
			break;
		case 0x070F:
			name = "fly_913_choice_layout";
			break;
			
		case 0x0710:
			name = "audi_913_a4_set_layout";
			break;
		case 0x0711:
			name = "track_bg";
			if(different_car_plug)
				name = name+"_"+resCarType;
			break;
		case 0x0712:
			name = "black_page";
			break;
		case 0x0800:
			name = "fly_backcarvideo_913_layout";
			break;
		case 0x0801:
			name = "fly_floatradar_layout";
			break;
		case 0x0802:
			name = "a4_set_layout_800_480";
			break;
		case 0x0803:
			name = "fly_backcarvideo_cvb_layout";
			break;
		case 0x0804:
			name = "audi_cvb_a4_set_layout";
			break;
		case 0x0805:
			name = "a4_cvb_set_layout_800_480";
			break;

		case 0x806:
			name = "front_sound_level";
			break;
		case 0x807:
			name = "rear_sound_level";
			break;
		case 0x808:
			name = "front_tone_level";
			break;
		case 0x809:
			name = "rear_tone_level";
			break;
		case 0x080a:
			name = "fly_backcarvideo_cvbsv_layout";
			break;
		case	0x0810:
			name = "audi_cvbsv_a4_set_layout";
			break;
		default:
			name = "fly_backcar_radar_layout";
			break;
		}

		int Identi = 0;
		try {
			Identi = getIdentifier(LAYOUT, name);

		} catch (Exception e) {
			Identi = 0;
		}
		if (Identi != 0) {
			try {
				return mLayoutInflater.inflate(Identi, null);
			} catch (Exception e) {
				Log.d("DDD", "" + e);
			}
		}

		return null;
	}

	private String LocalLanguage;

	public void initLocalLanguage() {
		Locale locale = BackCarService.getInstance().getResources()
				.getConfiguration().locale;
		LocalLanguage = locale.getLanguage();
	}

	public boolean isNeedSetLanguage() {
		Locale locale = BackCarService.getInstance().getResources()
				.getConfiguration().locale;
		String language = locale.getLanguage();
		if (language == null)
			return false;
		if (language.matches(LocalLanguage))
			return false;
		LocalLanguage = language;
		Configuration config = getResources().getConfiguration();
		config.locale = locale;
		mResources.updateConfiguration(config, mResources.getDisplayMetrics());
		return true;
	}

	public String getString(String name) {
		return mResources.getString(getIdentifier(STRING, name));

	}

	public String getString(String name, String Default) {
		int id = getIdentifier(STRING, name);
		if (id != 0)
			return mResources.getString(id);
		return Default;
	}

	public int getColor(String name) {
		return mResources.getColor(getIdentifier(COLOR, name));
	}

	public Drawable getDrawable(String name) {
		return mResources.getDrawable(getIdentifier(DRAWABLE, name));
	}

	public int getStyle(String name) {
		return getIdentifier(STYLE, name);
	}

	public float getDimen(String name) {
		return mResources.getDimension(getIdentifier(DIMEN, name));
	}

	public Integer getInteger(String resourceName, int Default) {
		int id = getIdentifier(INTEGER, resourceName);
		if (id != 0)
			return mResources.getInteger(id);
		return Default;

	}

	public boolean getBool(String resourceName, boolean Default) {
		int id = getIdentifier(BOOL, resourceName);
		if (id != 0)
			return mResources.getBoolean(id);

		return Default;
	}

	@Override
	public Object getSystemService(String name) {
		if (LAYOUT_INFLATER_SERVICE.equals(name)) {
			if (mLayoutInflater == null) {
				try {
					Class<?> cls = Class
							.forName("com.android.internal.policy.PolicyManager");
					Method m = cls.getMethod("makeNewLayoutInflater",
							Context.class);
					// 浼犲叆褰撳墠PluginProxyContext绫诲疄渚嬶紝鍒涘缓涓?涓竷灞?鍔犺浇鍣?
					mLayoutInflater = (LayoutInflater) m.invoke(null, this);
				} catch (Throwable e) {
					e.printStackTrace();
				}
			} else {
				return mLayoutInflater;
			}
		}
		return super.getSystemService(name);
	}

	@Override
	public AssetManager getAssets() {
		return mAssetManager;
	}

	@Override
	public Resources getResources() {
		return mResources;
	}

	@Override
	public ClassLoader getClassLoader() {
		return context.getClassLoader();
	}

	@Override
	public Resources.Theme getTheme() {
		if (mTheme == null) {
			mTheme = mResources.newTheme();
			mTheme.applyStyle(android.R.style.Theme_Light, true);
		}
		return mTheme;
	}

	@Override
	public String getPackageName() {
		return packageName;
	}

	private File copy(String fileName, boolean testModel) {
		OutputStream os = null;
		InputStream is = null;
		File outFile = null;
		try {
			outFile = new File(context.getFilesDir(), fileName);

			if (outFile.exists()) {
				// return outFile;
			}
			is = context.getResources().getAssets().open(fileName);
			os = new BufferedOutputStream(new FileOutputStream(outFile), 4096);
			byte[] b = new byte[4096];
			int len = 0;
			while ((len = is.read(b)) != -1)
				os.write(b, 0, len);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null)
					is.close();
				if (os != null)
					os.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return outFile;
	}

	public Resources getExternSkinResources() {
		return ExSkinResources;
	}

	private int getgetResourcesFromSkin(Context skinPackageContext,
			String resourceType, String resourceName, String packgeName) {
		int id = 0;
		try {

			id = skinPackageContext.getResources().getIdentifier(resourceName,
					resourceType, packgeName);
		} catch (Exception e) {
			id = 0;
		}
		return id;
	}

	/**
	 * tang 鍙栧緱Raw涓殑xml
	 * 
	 * @param skinPackageContext
	 * @param resourceName
	 * @return
	 */
	public InputStream getXmlInRawFromSkin(Context skinPackageContext,
			String resourceName) {

		int id = getgetResourcesFromSkin(skinPackageContext, "raw",
				resourceName, ExternSkinPagekanName);
		if (id != 0)
			return skinPackageContext.getResources().openRawResource(id);
		else {
			id = getgetResourcesFromSkin(skinPackageContext, "raw",
					resourceName, PlugPagekageName);
			if (id != 0)
				return skinPackageContext.getResources().openRawResource(id);
		}
		return null;
	}

	/**
	 * 鍙栧緱bool鍊�
	 * 
	 * @param skinPackageContext
	 * @param resourceName
	 * @return
	 */
	public boolean getBoolFromSkin(Context skinPackageContext,
			String resourceName) {
		int id = getgetResourcesFromSkin(skinPackageContext, "bool",
				resourceName, ExternSkinPagekanName);
		if (id != 0)
			return skinPackageContext.getResources().getBoolean(id);
		return false;
	}

	/**
	 * tang 鍙栧緱鐨偆鍖呬腑鐨勫瓧绗︿覆
	 * 
	 * @param skinPackageContext
	 * @param resourceName
	 * @return
	 */
	public String getStringFromSkin(Context skinPackageContext,
			String resourceName) {
		int id = getgetResourcesFromSkin(skinPackageContext, "string",
				resourceName, ExternSkinPagekanName);
		if (id != 0)
			return skinPackageContext.getResources().getString(id);
		return null;
	}

	/**
	 * tang 鍙栧緱鐨偆鍖呬腑鐨勫瓧绗︿覆
	 * 
	 * @param skinPackageContext
	 * @param resourceName
	 * @return
	 */
	public String[] getArrayFromSkin(Context skinPackageContext,
			String resourceName) {
		int id = getgetResourcesFromSkin(skinPackageContext, "array",
				resourceName, ExternSkinPagekanName);
		if (id != 0)
			return skinPackageContext.getResources().getStringArray(id);
		return null;
	}

	/**
	 * tang 鍙栧緱鐨偆鍖呬腑鏁村瀷鍙傛暟
	 * 
	 * @param skinPackageContext
	 * @param resourceName
	 * @return
	 */
	public Integer getIntegerFromSkin(Context skinPackageContext,
			String resourceName, int Default) {
		int id = getgetResourcesFromSkin(skinPackageContext, "integer",
				resourceName, ExternSkinPagekanName);
		if (id != 0)
			return skinPackageContext.getResources().getInteger(id);
		else
			return Default;
	}

	/**
	 * tang 鍙栧緱鐨偆鍖呬腑Drawable璧勬簮
	 * 
	 * @param skinPackageContext
	 * @param resourceName
	 * @return
	 */
	public Drawable getDrawableFromSkin(Context skinPackageContext,
			String resourceName) {
		int id = getgetResourcesFromSkin(skinPackageContext, "drawable",
				resourceName, ExternSkinPagekanName);
		if (id != 0)
			return skinPackageContext.getResources().getDrawable(id);
		else
			return null;
	}

	/**
	 * tang 鍙栧緱鐨偆鍖呬腑color璧勬簮
	 * 
	 * @param skinPackageContext
	 * @param resourceName
	 * @return
	 */
	public int getColorFromSkin(Context skinPackageContext, String resourceName) {
		int id = getgetResourcesFromSkin(skinPackageContext, "color",
				resourceName, ExternSkinPagekanName);
		return skinPackageContext.getResources().getColor(id);
	}

	/**
	 * tang 鍙栧緱鐨偆鍖呬腑layout璧勬簮
	 * 
	 * @param skinPackageContext
	 * @param resourceName
	 * @return
	 */
	public View getLayoutFromSkin(Context skinPackageContext,
			String resourceName) {
		int viewId = getgetResourcesFromSkin(skinPackageContext, "layout",
				resourceName, ExternSkinPagekanName);
		LayoutInflater inflater = null;
		View view = null;
		if (viewId != 0) {
			inflater = LayoutInflater.from(skinPackageContext);
			view = inflater.inflate(skinPackageContext.getResources()
					.getLayout(viewId), null);
		}
		return view;
	}

	/**
	 * 鍙栧緱鐨偆鍖呬腑ID
	 * 
	 * @param skinPackageContext
	 * @param resourceName
	 * @return
	 */
	public int getIdFromSkin(Context skinPackageContext, String resourceName,
			String type) {
		int id = 0;
		id = getgetResourcesFromSkin(skinPackageContext, type, resourceName,
				ExternSkinPagekanName);
		return id;
	}

	public int getrawIdFromSkin(String name) {
		if (ExSkinResources != null)
			return ExSkinResources.getIdentifier(name, RAW,
					ExternSkinPagekanName);
		return 0;
	}

}
