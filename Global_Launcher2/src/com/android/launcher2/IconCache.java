/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.launcher2;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import java.util.HashMap;
import android.os.SystemProperties;
import java.io.InputStream;
import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;
import android.util.Log;
import android.util.DisplayMetrics;
import android.graphics.BitmapFactory;

/**
 * Cache of application icons. Icons can be made from any thread.
 */
public class IconCache {
    @SuppressWarnings("unused")
    private static final String TAG = "Launcher.IconCache";
    private static String screenDPI = null;
    private static HashMap<String, String> APP_ICON_WHITE_LIST ; 
    
    static {
        APP_ICON_WHITE_LIST = new HashMap<String, String>();
        APP_ICON_WHITE_LIST.put("com.autochips.dvdplayer.DvdHomeActivity", "com.autochips.dvdplayer");
        APP_ICON_WHITE_LIST.put("com.autochips.HDMI.HDMIActivity", "com.autochips.HDMI");
        APP_ICON_WHITE_LIST.put("com.autochips.btsuite.MainBluetoothActivity", "com.autochips.btsuite");
        APP_ICON_WHITE_LIST.put("com.autochips.AVInput.AVInputActivity", "com.autochips.AVInput");
        APP_ICON_WHITE_LIST.put("com.autochips.videoplayer2.ThumbnailActivity", "com.autochips.videoplayer2");
        APP_ICON_WHITE_LIST.put("com.autochips.music2.TrackListActivity", "com.autochips.music2");
        APP_ICON_WHITE_LIST.put("com.autochips.picturebrowser.MainActivity", "com.autochips.picturebrowser");
        APP_ICON_WHITE_LIST.put("com.ATCSetting.mainui.MainActivity", "com.ATCSetting.mainui");
        APP_ICON_WHITE_LIST.put("com.android.voicedialer.VoiceDialerActivity", "com.android.voicedialer");
        APP_ICON_WHITE_LIST.put("com.android.browser.BrowserActivity", "com.android.browser");
        APP_ICON_WHITE_LIST.put("com.android.calendar.AllInOneActivity", "com.android.calendar");
    }

    private static final int INITIAL_ICON_CACHE_CAPACITY = 50;

    private static class CacheEntry {
        public Bitmap icon;
        public String title;
    }

    private final Bitmap mDefaultIcon;
    private final LauncherApplication mContext;
    private final PackageManager mPackageManager;
    private final HashMap<ComponentName, CacheEntry> mCache = new HashMap<ComponentName, CacheEntry>(
            INITIAL_ICON_CACHE_CAPACITY);
    private int mIconDpi;

    public IconCache(LauncherApplication context) {
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);

        mContext = context;
        mPackageManager = context.getPackageManager();
        mIconDpi = activityManager.getLauncherLargeIconDensity();

        // need to set mIconDpi before getting default icon
        mDefaultIcon = makeDefaultIcon();
    }

    public Drawable getFullResDefaultActivityIcon() {
        return getFullResIcon(Resources.getSystem(),
                android.R.mipmap.sym_def_app_icon);
    }

    public Drawable getFullResIcon(Resources resources, int iconId) {
        Drawable d;
        try {
            d = resources.getDrawableForDensity(iconId, mIconDpi);
        } catch (Resources.NotFoundException e) {
            d = null;
        }

        return (d != null) ? d : getFullResDefaultActivityIcon();
    }

    public Drawable getFullResIcon(String packageName, int iconId) {
        Resources resources;
        try {
            resources = mPackageManager.getResourcesForApplication(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            resources = null;
        }
        if (resources != null) {
            if (iconId != 0) {
                return getFullResIcon(resources, iconId);
            }
        }
        return getFullResDefaultActivityIcon();
    }

    public Drawable getFullResIcon(ResolveInfo info) {
        return getFullResIcon(info.activityInfo);
    }

    public Drawable getFullResIcon(ActivityInfo info) {

        Resources resources;
        try {
            resources = mPackageManager
                    .getResourcesForApplication(info.applicationInfo);
        } catch (PackageManager.NameNotFoundException e) {
            resources = null;
        }
        if (resources != null) {
            int iconId = info.getIconResource();
            if (iconId != 0) {
                return getFullResIcon(resources, iconId);
            }
        }
        return getFullResDefaultActivityIcon();
    }

    private Bitmap makeDefaultIcon() {
        Drawable d = getFullResDefaultActivityIcon();
        Bitmap b = Bitmap.createBitmap(Math.max(d.getIntrinsicWidth(), 1),
                Math.max(d.getIntrinsicHeight(), 1), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        d.setBounds(0, 0, b.getWidth(), b.getHeight());
        d.draw(c);
        c.setBitmap(null);
        return b;
    }

    /**
     * Remove any records for the supplied ComponentName.
     */
    public void remove(ComponentName componentName) {
        synchronized (mCache) {
            mCache.remove(componentName);
        }
    }

    /**
     * Empty out the cache.
     */
    public void flush() {
        synchronized (mCache) {
            mCache.clear();
        }
    }

    /**
     * Fill in "application" with the icon and label for "info."
     */
    public void getTitleAndIcon(ApplicationInfo application, ResolveInfo info,
            HashMap<Object, CharSequence> labelCache) {
        synchronized (mCache) {
            CacheEntry entry = cacheLocked(application.componentName, info,
                    labelCache);

            application.title = entry.title;
            application.iconBitmap = entry.icon;
        }
    }

    public Bitmap getIcon(Intent intent) {
        synchronized (mCache) {
            final ResolveInfo resolveInfo = mPackageManager.resolveActivity(
                    intent, 0);
            ComponentName component = intent.getComponent();

            if (resolveInfo == null || component == null) {
                return mDefaultIcon;
            }

            CacheEntry entry = cacheLocked(component, resolveInfo, null);
            return entry.icon;
        }
    }

    public Bitmap getIcon(ComponentName component, ResolveInfo resolveInfo,
            HashMap<Object, CharSequence> labelCache) {
        synchronized (mCache) {
            if (resolveInfo == null || component == null) {
                return null;
            }

            CacheEntry entry = cacheLocked(component, resolveInfo, labelCache);
            return entry.icon;
        }
    }

    public boolean isDefaultIcon(Bitmap icon) {
        return mDefaultIcon == icon;
    }

    private CacheEntry cacheLocked(ComponentName componentName,
            ResolveInfo info, HashMap<Object, CharSequence> labelCache) {
        CacheEntry entry = mCache.get(componentName);
        if (entry == null) {
            entry = new CacheEntry();

            mCache.put(componentName, entry);

            ComponentName key = LauncherModel
                    .getComponentNameFromResolveInfo(info);
            if (labelCache != null && labelCache.containsKey(key)) {
                entry.title = labelCache.get(key).toString();
            } else {
                entry.title = info.loadLabel(mPackageManager).toString();
                if (labelCache != null) {
                    labelCache.put(key, entry.title);
                }
            }
            if (entry.title == null) {
                entry.title = info.activityInfo.name;
            }
            /*try {
                int appFlags = mPackageManager.getApplicationInfo(
                        componentName.getPackageName(), 0).flags;
                if ((appFlags & android.content.pm.ApplicationInfo.FLAG_SYSTEM) == 0) {
                    entry.icon = Utilities.create3rdIconBitmap(
                            getFullResIcon(info), mContext);
                    return entry;
                }
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }*/
          /*    Launcher.FLYMODE  
            if (!APP_ICON_WHITE_LIST.containsKey(componentName.getClassName())) {
                entry.icon = Utilities.create3rdIconBitmap(
                        getFullResIcon(info), mContext);
                return entry;
            }
            entry.icon = Utilities.createIconBitmap(getFullResIcon(info),
                    mContext);
			*/
			
            // try first the theme's menu icon. then fallback to default.
            String usedTheme = SystemProperties.get("persist.sys.qrd_theme.current", "default");			

			if (!usedTheme.equals("default")) {
				Log.d("DDD", "usedTheme : "+usedTheme+" not default");
				try {
					String themeBasePath;
					if (!usedTheme.startsWith("0x"))
									themeBasePath = "/system/qrd_theme/";
								else
									themeBasePath = "/data/qrd_theme/";
								
					ZipFile themeSrc = new ZipFile(themeBasePath + usedTheme + ".zip");
					InputStream stream = null;
					String filePathInTheme = "Launcher/" + screenDPI + "/" + componentName.getClassName() + ".png";
					android.util.Log.e("filePathInThemeinLauncher", filePathInTheme); 	
					ZipEntry zipentry = themeSrc.getEntry(filePathInTheme);
					if (zipentry != null) {
						stream = themeSrc.getInputStream(zipentry); 
						BitmapFactory.Options opts = new BitmapFactory.Options();
						opts.inDensity = DisplayMetrics.DENSITY_DEFAULT;
						Drawable dr = Drawable.createFromResourceStream(null,null,stream,null,opts);
						//Drawable dr = Drawable.createFromStream(stream, null);
						if (dr != null)
							entry.icon = Utilities.createIconBitmap(
		                    dr, mContext);
						stream.close();
					}	
					themeSrc.close();
				} catch (Exception e) {
					android.util.Log.e("filePathInThemeinLauncher exception", "" + e);	
				}				
			}
			
            //fallback
            if (entry.icon == null)
            	entry.icon = Utilities.createIconBitmap(
                    getFullResIcon(info), mContext);
        }
        return entry;
    }

    public HashMap<ComponentName, Bitmap> getAllIcons() {
        synchronized (mCache) {
            HashMap<ComponentName, Bitmap> set = new HashMap<ComponentName, Bitmap>();
            for (ComponentName cn : mCache.keySet()) {
                final CacheEntry e = mCache.get(cn);
                set.put(cn, e.icon);
            }
            return set;
        }
    }
}
