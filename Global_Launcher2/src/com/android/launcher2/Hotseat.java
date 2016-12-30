/*
 * Copyright (C) 2011 The Android Open Source Project
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

import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;

import com.android.launcher.R;

public class Hotseat extends FrameLayout {
    @SuppressWarnings("unused")
    private static final String TAG = "Hotseat";

    private Launcher mLauncher;
    private CellLayout mContent;
    public static ImageView mAllAppsButton;
	private static final int sAllAppsButtonRank = 0; // In the middle of the

    private int mCellCountX;
    private int mCellCountY;
    private int mAllAppsButtonRank;
    private LayoutInflater inflater;

    private boolean mTransposeLayoutWithOrientation;
    private boolean mIsLandscape;

    public Hotseat(Context context) {
        this(context, null);
		 mAllAppsButton = (ImageView) findViewById(R.id.allapp);
    }

    public Hotseat(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
		 mAllAppsButton = (ImageView) findViewById(R.id.allapp);
    }

    public Hotseat(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.Hotseat, defStyle, 0);
        Resources r = context.getResources();
        mCellCountX = a.getInt(R.styleable.Hotseat_cellCountX, -1);
        mCellCountY = a.getInt(R.styleable.Hotseat_cellCountY, -1);
        mAllAppsButtonRank = r.getInteger(R.integer.hotseat_all_apps_index);
        mTransposeLayoutWithOrientation = 
                r.getBoolean(R.bool.hotseat_transpose_layout_with_orientation);
        mIsLandscape = context.getResources().getConfiguration().orientation ==
            Configuration.ORIENTATION_LANDSCAPE;
		 mAllAppsButton = (ImageView) findViewById(R.id.allapp);
    }

    public void setup(Launcher launcher) {
        mLauncher = launcher;
        setOnKeyListener(new HotseatIconKeyEventListener());
    }

    CellLayout getLayout() {
        return mContent;
    }
  
    private boolean hasVerticalHotseat() {
        return (mIsLandscape && mTransposeLayoutWithOrientation);
    }

    /* Get the orientation invariant order of the item in the hotseat for persistence. */
    int getOrderInHotseat(int x, int y) {
        return hasVerticalHotseat() ? (mContent.getCountY() - y - 1) : x;
    }
    /* Get the orientation specific coordinates given an invariant order in the hotseat. */
    int getCellXFromOrder(int rank) {
        return hasVerticalHotseat() ? 0 : rank;
    }
    int getCellYFromOrder(int rank) {
        return hasVerticalHotseat() ? (mContent.getCountY() - (rank + 1)) : 0;
    }
    public static boolean isAllAppsButtonRank(int rank) {
        return rank == sAllAppsButtonRank;
     }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (mCellCountX < 0) mCellCountX = LauncherModel.getCellCountX();
        if (mCellCountY < 0) mCellCountY = LauncherModel.getCellCountY();
        mContent = (CellLayout) findViewById(R.id.layout);
        mContent.setGridSize(mCellCountX, mCellCountY);
        mContent.setIsHotseat(true);

        resetLayout();
    }

    void resetLayout() {
        Log.d("DDD", "resetLayout");
        mContent.removeAllViewsInLayout();
        // Add the Apps button
        Context context = getContext();
        inflater = LayoutInflater.from(context);

        // allAppsButton = (BubbleTextView)
        // inflater.inflate(R.layout.application,
        // mContent, false);// 设定HOTSEAT图标?
        // allAppsButton.setTextGone();
        Drawable drawable = Launcher.flyLauncher.getDrawableFromSkin(
                Launcher.skinContext, "all_apps_button_icon");

        // allAppsButton.setCompoundDrawablesWithIntrinsicBounds(null, drawable,
        // null, null);
        if (mLauncher != null) {
            if ((mLauncher.isWorkspace)) {
                Log.d("DDD", "isWorkspace");
                if (LauncherModel.NIGHT) {
                    drawable = Launcher.flyLauncher.getDrawableFromSkin(
                            Launcher.skinContext, "all_apps_button_icon_night");
                } else {
                    drawable = Launcher.flyLauncher.getDrawableFromSkin(
                            Launcher.skinContext, "all_apps_button_icon");
                }

            } else {
                Log.d("DDD", "not isWorkspace");
                if (LauncherModel.NIGHT) {
                    drawable = Launcher.flyLauncher.getDrawableFromSkin(
                            Launcher.skinContext, "home_button_night");
                } else {
                    drawable = Launcher.flyLauncher.getDrawableFromSkin(
                            Launcher.skinContext, "home_button");
                }
                Hotseat.mAllAppsButton.setBackgroundDrawable(drawable);
            }
        } else {
            if (LauncherModel.NIGHT) {
                drawable = Launcher.flyLauncher.getDrawableFromSkin(
                        Launcher.skinContext, "all_apps_button_icon_night");
            } else {
                drawable = Launcher.flyLauncher.getDrawableFromSkin(
                        Launcher.skinContext, "all_apps_button_icon");
            }
        }
        mAllAppsButton = (ImageView) findViewById(R.id.allapp);
        mAllAppsButton.setBackgroundDrawable(drawable);
        // allAppsButton.setCompoundDrawablesWithIntrinsicBounds(null, context
        // .getResources().getDrawable(R.drawable.all_apps_button_icon),
        // null, null);

        // allAppsButton.setText(context.getString(R.string.all_apps_button_label));
        // allAppsButton.setContentDescription(context
        // .getString(R.string.all_apps_button_label));

        mAllAppsButton.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (mLauncher != null
                        && (event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN) {
                    mLauncher.onTouchDownAllAppsButton(v);
                } else {
                    if (mLauncher == null
                            && (event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN) {
                        // try {
                        // Runtime runtime = Runtime.getRuntime();
                        // runtime.exec("input keyevent "
                        // + KeyEvent.KEYCODE_BACK);
                        // } catch (Exception e) {
                        // // TODO: handle exception
                        // }
                        // Intent intent = new Intent(Intent.ACTION_MAIN);
                        //
                        // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// 注意
                        //
                        // intent.addCategory(Intent.CATEGORY_HOME);
                        //
                        // startActivity(intent);
                        //
                        // mWorkspace.setBackgroundColor(Color.RED);
                        //
                        // mWorkspace.setVisibility(GONE);
                        //
                        // Log.d("launcher", "----->" + mWorkspace);
                    }
                }
                return false;
            }
        });

        mAllAppsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(android.view.View v) {
                final boolean allAppsVisible = mLauncher.isAllAppsVisible();
                if (allAppsVisible) {
                    Launcher.flyLauncher.showhome();
                } else {
                    if (mLauncher != null) {
                        mLauncher.onClickAllAppsButton(v);
                    }
                }
                mLauncher.getSearchBar().setVisibility(View.VISIBLE);
            }
        });

        // Note: We do this to ensure that the hotseat is always laid out in the
        // orientation of
        // the hotseat in order regardless of which orientation they were added
        int x = getCellXFromOrder(sAllAppsButtonRank);
        int y = getCellYFromOrder(sAllAppsButtonRank);
        Log.d("DDD", "resetLayout --- " + " x = " + x + "   y  =" + y);
        // mContent.addViewToCellLayout(allAppsButton, -1, 0,
        // new CellLayout.LayoutParams(x, y, 1, 1), true);
    }
}
