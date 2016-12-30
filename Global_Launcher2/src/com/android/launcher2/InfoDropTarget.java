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

import android.content.ComponentName;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.android.launcher.R;

public class InfoDropTarget extends ButtonDropTarget {

    private ColorStateList mOriginalTextColor;
    private TransitionDrawable mDrawable;
    private TransitionDrawable mDrawable0;
    private TransitionDrawable mDrawable1;

    private int mHoverColor = 0xFF0000FF;

    public InfoDropTarget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InfoDropTarget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mOriginalTextColor = getTextColors();

        // Get the hover color
        Resources r = getResources();
        mHoverColor = r.getColor(R.color.info_target_hover_tint);

		
		mHoverPaint.setColorFilter(new PorterDuffColorFilter(mHoverColor,
						PorterDuff.Mode.SRC_ATOP));
		
		//		  mDrawable0 = (TransitionDrawable) Launcher.flyLauncher
		//				  .getDrawableFromSkin(Launcher.skinContext,
		//						  "info_target_selector");
		  
				
		//		  mDrawable1 = (TransitionDrawable) Launcher.flyLauncher
		//				  .getDrawableFromSkin(Launcher.skinContext,
		//						  "info_target_selector2");
			 
				
		if (LauncherModel.NIGHT) {
					mDrawable0 = (TransitionDrawable) Launcher.flyLauncher
							.getDrawableFromSkin(Launcher.skinContext,
									"info_target_selector_night");
					mDrawable1 = (TransitionDrawable) Launcher.flyLauncher
							.getDrawableFromSkin(Launcher.skinContext,
									"info_target_selector2_night");
		} else {
					mDrawable0 = (TransitionDrawable) Launcher.flyLauncher
							.getDrawableFromSkin(Launcher.skinContext,
									"info_target_selector");
					mDrawable1 = (TransitionDrawable) Launcher.flyLauncher
							.getDrawableFromSkin(Launcher.skinContext,
									"info_target_selector2");
		}
				
				
		this.setCompoundDrawablesWithIntrinsicBounds(mDrawable, null, null,
						null);
				mDrawable = (TransitionDrawable) getCompoundDrawables()[0];
				if (null != mDrawable0 && mDrawable1!=null) {
					mDrawable0.setCrossFadeEnabled(true);
					mDrawable1.setCrossFadeEnabled(true);
		}
				
		//mDrawable = (TransitionDrawable) getCurrentDrawable();
		//mDrawable.setCrossFadeEnabled(true);

        // Remove the text in the Phone UI in landscape
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (!LauncherApplication.isScreenLarge()) {
                setText("");
            }
        }
    }

    private boolean isAllAppsApplication(DragSource source, Object info) {
        return (source instanceof AppsCustomizePagedView)
                && (info instanceof ApplicationInfo);
    }

    @Override
    public boolean acceptDrop(DragObject d) {
        // acceptDrop is called just before onDrop. We do the work here, rather than
        // in onDrop, because it allows us to reject the drop (by returning false)
        // so that the object being dragged isn't removed from the drag source.
        ComponentName componentName = null;
        if (d.dragInfo instanceof ApplicationInfo) {
            componentName = ((ApplicationInfo) d.dragInfo).componentName;
        } else if (d.dragInfo instanceof ShortcutInfo) {
            componentName = ((ShortcutInfo) d.dragInfo).intent.getComponent();
        } else if (d.dragInfo instanceof PendingAddItemInfo) {
            componentName = ((PendingAddItemInfo) d.dragInfo).componentName;
        }
        if (componentName != null) {
            mLauncher.startApplicationDetailsActivity(componentName);
        }

        // There is no post-drop animation, so clean up the DragView now
        d.deferDragViewCleanupPostAnimation = false;
        return false;
    }

    @Override
    public void onDragStart(DragSource source, Object info, int dragAction) {
        boolean isVisible = true;
        // If we are dragging a widget or shortcut, hide the info target
        if (!isAllAppsApplication(source, info)) {
            isVisible = false;
        }
        
        
        if (LauncherModel.NIGHT) {
            mDrawable0 = (TransitionDrawable) Launcher.flyLauncher
                    .getDrawableFromSkin(Launcher.skinContext,
                            "info_target_selector_night");
            mDrawable1 = (TransitionDrawable) Launcher.flyLauncher
                    .getDrawableFromSkin(Launcher.skinContext,
                            "info_target_selector2_night");
        } else {
            mDrawable0 = (TransitionDrawable) Launcher.flyLauncher
                    .getDrawableFromSkin(Launcher.skinContext,
                            "info_target_selector");
            mDrawable1 = (TransitionDrawable) Launcher.flyLauncher
                    .getDrawableFromSkin(Launcher.skinContext,
                            "info_target_selector2");
        }
        
        if (info instanceof ApplicationInfo) {
            ApplicationInfo appInfo = (ApplicationInfo) info;
            if ((appInfo.flags & ApplicationInfo.DOWNLOADED_FLAG) != 0) {
                setCompoundDrawablesRelativeWithIntrinsicBounds(mDrawable0, null, null, null);
                mDrawable= (TransitionDrawable) getCompoundDrawables()[0];
            } else {
                setCompoundDrawablesRelativeWithIntrinsicBounds(mDrawable1, null, null, null);
                mDrawable= (TransitionDrawable) getCompoundDrawables()[0];
            }
        }else{
            setCompoundDrawablesRelativeWithIntrinsicBounds(mDrawable0, null, null, null);
            mDrawable= (TransitionDrawable) getCompoundDrawables()[0];
        }
        mActive = isVisible;
        mDrawable.resetTransition();
        setTextColor(mOriginalTextColor);
        ((ViewGroup) getParent()).setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onDragEnd() {
        super.onDragEnd();
        mActive = false;
    }

    public void onDragEnter(DragObject d) {
        super.onDragEnter(d);

        mDrawable.startTransition(mTransitionDuration);
        setTextColor(mHoverColor);
    }

    public void onDragExit(DragObject d) {
        super.onDragExit(d);

        if (!d.dragComplete) {
            mDrawable.resetTransition();
            setTextColor(mOriginalTextColor);
        }
    }
}
