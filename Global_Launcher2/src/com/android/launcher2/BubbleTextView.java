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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.io.UnsupportedEncodingException;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Region.Op;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.SystemProperties;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.Paint;
import android.graphics.Color;
import android.content.res.TypedArray;


import com.android.launcher.R;

/**
 * TextView that draws a bubble behind the text. We cannot use a LineBackgroundSpan
 * because we want to make the bubble taller than the text and TextView's clip is
 * too aggressive.
 */
public class BubbleTextView extends TextView {
    static final float CORNER_RADIUS = 4.0f;
    static final float SHADOW_LARGE_RADIUS = 4.0f;
    static final float SHADOW_SMALL_RADIUS = 1.75f;
    static final float SHADOW_Y_OFFSET = 2.0f;
    static final int SHADOW_LARGE_COLOUR = 0xDD000000;
    static final int SHADOW_SMALL_COLOUR = 0xCC000000;
    static final float PADDING_H = 8.0f;
    static final float PADDING_V = 3.0f;
    static String DAY = "MODE_DAY";
    static String NIGHT = "MODE_NIGHT";

    private int mPrevAlpha = -1;
	private static int mPaintTextSize = -1;
	private static int mPaintTextLength = -1;
	
    private final HolographicOutlineHelper mOutlineHelper = new HolographicOutlineHelper();
    private final Canvas mTempCanvas = new Canvas();
    private final Rect mTempRect = new Rect();
    private boolean mDidInvalidateForPressedState;
    private Bitmap mPressedOrFocusedBackground;
    private int mFocusedOutlineColor;
    private int mFocusedGlowColor;
    private int mPressedOutlineColor;
    private int mPressedGlowColor;

    private boolean mBackgroundSizeChanged;
    private Drawable mBackground;

    private boolean mStayPressed;
    private CheckLongPressHelper mLongPressHelper;


	public  int Width = -1;
	public  int Height = -1;
    public BubbleTextView(Context context) {
        super(context);
        init();
    }

    public BubbleTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
		
		TypedArray a = context.obtainStyledAttributes(attrs,
					   R.styleable.PagedView, 0, 0);
			  
		mPaintTextLength = a.getInt(com.android.internal.R.styleable.TextView_maxEms, 5);
		mPaintTextLength *=2;
        init();
    }

    public BubbleTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
		
		TypedArray a = context.obtainStyledAttributes(attrs,
					   R.styleable.PagedView, defStyle, 0);
			  
		mPaintTextLength = a.getInt(com.android.internal.R.styleable.TextView_maxEms, 5);
		mPaintTextLength *=2;

        init();
    }
	


    private void init() {
        mLongPressHelper = new CheckLongPressHelper(this);
        mBackground = getBackground();

        final Resources res = getContext().getResources();
        mFocusedOutlineColor = mFocusedGlowColor = mPressedOutlineColor = mPressedGlowColor =
            res.getColor(android.R.color.holo_blue_light);



        setShadowLayer(SHADOW_LARGE_RADIUS, 0.0f, SHADOW_Y_OFFSET, SHADOW_LARGE_COLOUR);
    }
	
	public static Bitmap drawableToBitmap(Drawable drawable) {

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
    public static Bitmap drawableToBitmap(Drawable drawable , Bitmap bit, String text, int textColor) {

      //  if (drawable instanceof BitmapDrawable) {
       //     return ((BitmapDrawable) drawable).getBitmap();
       // }
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
		final int textpadd = canvas.getHeight()/5-5;
		
		text = bSubstring(text, mPaintTextLength, canvas.getWidth()-10);  // remember getbytes("unicode") has 2 byte head
		//	Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);  
		

		if(bit != null) {
			int left = (canvas.getWidth()-bit.getWidth())/2;
			int right = left+bit.getWidth();
			int top = (canvas.getHeight()-bit.getHeight())/ 2-20;
			int bottom = top+bit.getHeight();
			Rect  srcRect = new Rect(0, 0, bit.getWidth(), bit.getHeight());
        	Rect  dstRect = new Rect(left, top,right,bottom);// bmp2?????????
       	 	canvas.drawBitmap(bit, srcRect, dstRect, null);
			}
		if(text == null)
			return bitmap;
		
		Paint paint = new Paint();
		paint.setTextAlign(Paint.Align.CENTER);
		paint.setColor(textColor);
		paint.setTextSize(mPaintTextSize);
		paint.setStyle(Style.FILL);
		paint.setAntiAlias(true);
		
		canvas.drawText(text, canvas.getWidth()/2, 
		canvas.getHeight()-textpadd, paint);
        return bitmap;
    }
	
	public static Bitmap createShortcutIcon(Bitmap flyOtherBigBitMap,
			   Drawable appIcon) {
	
		   int with = flyOtherBigBitMap.getWidth();
		   int height = flyOtherBigBitMap.getHeight();
		   int ShortcutIcon_X=Launcher.flyLauncher.getIntegerFromSkin(
				   Launcher.skinContext, "ShortcutIcon_X");
		   int ShortcutIcon_Y=Launcher.flyLauncher.getIntegerFromSkin(
				   Launcher.skinContext, "ShortcutIcon_Y");
		   
		   
		   Log.d("AAAAA", "   with: ");
		   Bitmap newb = Bitmap.createBitmap(with, height, Config.ARGB_8888);
		   Canvas cv = new Canvas(newb);
		   cv.drawBitmap(flyOtherBigBitMap, 0, 0, null);
		   cv.drawBitmap(drawableToBitmap(appIcon), ShortcutIcon_X, ShortcutIcon_Y, null);
	
		   cv.save(Canvas.ALL_SAVE_FLAG);// 保存
		   cv.restore();// 存储
		   // Log.d("AAAAA"," createShortcutIcon setLayerType: " +
		   // cv.isHardwareAccelerated() ) ;
		   return newb;
	
	   }

	public static Bitmap createDragBitmap(Bitmap bitmap, String text){
        int icon_textColor = Color.BLACK;
        Drawable flyOtherBigBitMap = null;
        flyOtherBigBitMap = Launcher.flyLauncher.getDrawableFromSkin(
                Launcher.flyLauncher.skinContext, "default_app_icon");
        Log.d("launcher", "LauncherModel:" + LauncherModel.NIGHT);
        if (LauncherModel.NIGHT) {
            flyOtherBigBitMap = Launcher.flyLauncher.getDrawableFromSkin(
                    Launcher.flyLauncher.skinContext, "default_app_icon_night");
            icon_textColor = (Launcher.flyLauncher.getColorFromSkin(
                    Launcher.skinContext, "bubbletextview_text_night"));
        } else {
            flyOtherBigBitMap = Launcher.flyLauncher.getDrawableFromSkin(
                    Launcher.flyLauncher.skinContext, "default_app_icon");
            icon_textColor = (Launcher.flyLauncher.getColorFromSkin(
                    Launcher.skinContext, "bubbletextview_text"));

        }
		return drawableToBitmap(flyOtherBigBitMap, bitmap, 
			text, icon_textColor);

		}

    public void applyFromShortcutInfo(ShortcutInfo info, IconCache iconCache) {
        Bitmap  bit = info.getIcon(iconCache);
        int icon_textColor = Color.BLACK;
        Drawable flyOtherBigBitMap = null;
        flyOtherBigBitMap = Launcher.flyLauncher.getDrawableFromSkin(
                Launcher.flyLauncher.skinContext, "default_app_icon");
        Log.d("launcher", "LauncherModel:" + LauncherModel.NIGHT);
        if (LauncherModel.NIGHT) {
            flyOtherBigBitMap = Launcher.flyLauncher.getDrawableFromSkin(
                    Launcher.flyLauncher.skinContext, "default_app_icon_night");
            icon_textColor = (Launcher.flyLauncher.getColorFromSkin(
                    Launcher.skinContext, "bubbletextview_text_night"));
        } else {
            flyOtherBigBitMap = Launcher.flyLauncher.getDrawableFromSkin(
                    Launcher.flyLauncher.skinContext, "default_app_icon");
            icon_textColor = (Launcher.flyLauncher.getColorFromSkin(
                    Launcher.skinContext, "bubbletextview_text"));

        }
		mPaintTextSize = (int)this.getTextSize();
	//	mPaintTextLength = this.

       // setCompoundDrawablesWithIntrinsicBounds(null,
		//			new FastBitmapDrawable(b),
		//			null, null);
//		this.appIcon.setImageBitmap(createShortcutIcon(
 //                       drawableToBitmap(flyOtherBigBitMap),
 //                       bitmapToDrawable(appIcon)));
		setCompoundDrawablesWithIntrinsicBounds(null,
								new FastBitmapDrawable(drawableToBitmap(flyOtherBigBitMap, bit, 
								info.title.toString(), icon_textColor)),
					null, null);
        
		 if ("com.flyaudio.shortcutmanager".equals(info.getPackageName())) {
            String[] appClassNameArray = Launcher.flyLauncher.getArrayFromSkin(
                    Launcher.flyLauncher.skinContext, "fly_app_class_name");
            if (appClassNameArray == null)
                appClassNameArray = getResources().getStringArray(
                        R.array.fly_app_class_name);
            String className = null;
            if (info.getPackageName() != null) {
                String icon_name = info.getClassName().replace(".", "_");
                for (int i = 0; i < appClassNameArray.length; i++) {
                    className = appClassNameArray[i];
                    if (icon_name.toLowerCase().equals(className.toLowerCase())) {
                        Log.d("AAAAA", "1111==========1=============className "
                                + className.toLowerCase());
                        try {
                            if (LauncherModel.NIGHT) {
                                flyOtherBigBitMap = Launcher.flyLauncher
                                        .getDrawableFromSkin(
                                                Launcher.flyLauncher.skinContext,
                                                (className.toLowerCase() + "_night"));
                            } else {
                                flyOtherBigBitMap = Launcher.flyLauncher
                                        .getDrawableFromSkin(
                                                Launcher.flyLauncher.skinContext,
                                                className.toLowerCase());
                            }
							setCompoundDrawablesWithIntrinsicBounds(null,
						 new FastBitmapDrawable(drawableToBitmap(flyOtherBigBitMap, null, 
						 info.title.toString(), icon_textColor)),
					 	null, null);

                        } catch (Exception e) {
                            // TODO: handle exception
                            Log.d("AAAAA",
                                    "we can't find bitmap , so use default bimap");
			 		setCompoundDrawablesWithIntrinsicBounds(null,
						 new FastBitmapDrawable(drawableToBitmap(flyOtherBigBitMap, bit, 
						 info.title.toString(), icon_textColor)),
					 	null, null);
                            break;
                        }
       //                 this.appIcon
        //                        .setImageBitmap(drawableToBitmap(flyOtherBigBitMap));
                        break;
                    }
                }
            }
        } else {
            String[] appPageNameArray = Launcher.flyLauncher.getArrayFromSkin(
                    Launcher.flyLauncher.skinContext, "third_app_package_name");
            if (appPageNameArray == null) {
                appPageNameArray = getResources().getStringArray(
                        R.array.third_app_package_name);
                Log.d("DDD", "  we can't find skin resource");
            }
            String pageName = null;
            if (info.getPackageName() != null) {
                String icon_name = info.getPackageName().replace(".", "_");
                Log.d("AAAAA", "===== icon_name: " + icon_name.toLowerCase()
                        + " pageName: " + info.getPackageName());
                for (int i = 0; i < appPageNameArray.length; i++) {
                    pageName = appPageNameArray[i];
                    Log.d("bub", "  pageName: " + pageName);
                    if (icon_name.toLowerCase().equals(pageName.toLowerCase())) {
                        Log.d("AAAAA", "1111============2===========pageName "
                                + pageName.toLowerCase());
                        try {
                            // flyOtherBigBitMap = Launcher.flyLauncher
                            // .getDrawableFromSkin(
                            // Launcher.flyLauncher.skinContext,
                            // pageName.toLowerCase());

                            if (LauncherModel.NIGHT) {
                                flyOtherBigBitMap = Launcher.flyLauncher
                                        .getDrawableFromSkin(
                                                Launcher.flyLauncher.skinContext,
                                                (pageName.toLowerCase() + "_night"));
                            } else {
                                flyOtherBigBitMap = Launcher.flyLauncher
                                        .getDrawableFromSkin(
                                                Launcher.flyLauncher.skinContext,
                                                pageName.toLowerCase());
                            }

						 setCompoundDrawablesWithIntrinsicBounds(null,
						 new FastBitmapDrawable(drawableToBitmap(flyOtherBigBitMap, bit, 
						 info.title.toString(), icon_textColor)),
					 null, null);
                        } catch (Exception e) {
                            // TODO: handle exception
                            Log.d("AAAAA",
                                    "we can't find bitmap , so use default bimap");
					setCompoundDrawablesWithIntrinsicBounds(null,
									new FastBitmapDrawable(drawableToBitmap(flyOtherBigBitMap, bit, 
									info.title.toString(), icon_textColor)),
								null, null);

                            break;
                        }
				setCompoundDrawablesWithIntrinsicBounds(null,
									new FastBitmapDrawable(drawableToBitmap(flyOtherBigBitMap, bit, 
									info.title.toString(), icon_textColor)),
								null, null);
                        break;
                    }
                }
            }
        }
        	


	  Width = flyOtherBigBitMap.getIntrinsicWidth();
	  Height = flyOtherBigBitMap.getIntrinsicHeight();


      //  this.appName.setText(info.title);
      //  this.appName.setTextColor(icon_textColor);


       // Log.d("DDD", " Bubbletext " + info.title);
		//setTextColor(icon_textColor);
		//setText(info.title);
        setTag(info);
    }

    @Override
    protected boolean setFrame(int left, int top, int right, int bottom) {
        if (getLeft() != left || getRight() != right || getTop() != top || getBottom() != bottom) {
            mBackgroundSizeChanged = true;
        }
        return super.setFrame(left, top, right, bottom);
    }

    @Override
    protected boolean verifyDrawable(Drawable who) {
        return who == mBackground || super.verifyDrawable(who);
    }

    @Override
    public void setTag(Object tag) {
        if (tag != null) {
            LauncherModel.checkItemInfo((ItemInfo) tag);
        }
        super.setTag(tag);
    }

    @Override
    protected void drawableStateChanged() {
        if (isPressed()) {
            // In this case, we have already created the pressed outline on ACTION_DOWN,
            // so we just need to do an invalidate to trigger draw
            if (!mDidInvalidateForPressedState) {
                setCellLayoutPressedOrFocusedIcon();
            }
        } else {
            // Otherwise, either clear the pressed/focused background, or create a background
            // for the focused state
            final boolean backgroundEmptyBefore = mPressedOrFocusedBackground == null;
            if (!mStayPressed) {
                mPressedOrFocusedBackground = null;
            }
            if (isFocused()) {
                if (getLayout() == null) {
                    // In some cases, we get focus before we have been layed out. Set the
                    // background to null so that it will get created when the view is drawn.
                    mPressedOrFocusedBackground = null;
                } else {
                    mPressedOrFocusedBackground = createGlowingOutline(
                            mTempCanvas, mFocusedGlowColor, mFocusedOutlineColor);
                }
                mStayPressed = false;
                setCellLayoutPressedOrFocusedIcon();
            }
            final boolean backgroundEmptyNow = mPressedOrFocusedBackground == null;
            if (!backgroundEmptyBefore && backgroundEmptyNow) {
                setCellLayoutPressedOrFocusedIcon();
            }
        }

        Drawable d = mBackground;
        if (d != null && d.isStateful()) {
            d.setState(getDrawableState());
        }
        super.drawableStateChanged();
    }

    /**
     * Draw this BubbleTextView into the given Canvas.
     *
     * @param destCanvas the canvas to draw on
     * @param padding the horizontal and vertical padding to use when drawing
     */
    private void drawWithPadding(Canvas destCanvas, int padding) {
        final Rect clipRect = mTempRect;
        getDrawingRect(clipRect);

        // adjust the clip rect so that we don't include the text label
        clipRect.bottom =
            getExtendedPaddingTop() - (int) BubbleTextView.PADDING_V + getLayout().getLineTop(0);

        // Draw the View into the bitmap.
        // The translate of scrollX and scrollY is necessary when drawing TextViews, because
        // they set scrollX and scrollY to large values to achieve centered text
        destCanvas.save();
        destCanvas.scale(getScaleX(), getScaleY(),
                (getWidth() + padding) / 2, (getHeight() + padding) / 2);
        destCanvas.translate(-getScrollX() + padding / 2, -getScrollY() + padding / 2);
        destCanvas.clipRect(clipRect, Op.REPLACE);
        draw(destCanvas);
        destCanvas.restore();
    }

    /**
     * Returns a new bitmap to be used as the object outline, e.g. to visualize the drop location.
     * Responsibility for the bitmap is transferred to the caller.
     */
    private Bitmap createGlowingOutline(Canvas canvas, int outlineColor, int glowColor) {
        final int padding = HolographicOutlineHelper.MAX_OUTER_BLUR_RADIUS;
        final Bitmap b = Bitmap.createBitmap(
                getWidth() + padding, getHeight() + padding, Bitmap.Config.ARGB_8888);

        canvas.setBitmap(b);
        drawWithPadding(canvas, padding);
        mOutlineHelper.applyExtraThickExpensiveOutlineWithBlur(b, canvas, glowColor, outlineColor);
        canvas.setBitmap(null);

        return b;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Call the superclass onTouchEvent first, because sometimes it changes the state to
        // isPressed() on an ACTION_UP
        boolean result = super.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // So that the pressed outline is visible immediately when isPressed() is true,
                // we pre-create it on ACTION_DOWN (it takes a small but perceptible amount of time
                // to create it)
                if (mPressedOrFocusedBackground == null) {
                    mPressedOrFocusedBackground = createGlowingOutline(
                            mTempCanvas, mPressedGlowColor, mPressedOutlineColor);
                }
		//add  by flyaudio bpb 
		setStayPressed(true);

                // Invalidate so the pressed state is visible, or set a flag so we know that we
                // have to call invalidate as soon as the state is "pressed"
                if (isPressed()) {
                    mDidInvalidateForPressedState = true;
                    setCellLayoutPressedOrFocusedIcon();
                } else {
                    mDidInvalidateForPressedState = false;
                }

                mLongPressHelper.postCheckForLongPress();
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                // If we've touched down and up on an item, and it's still not "pressed", then
                // destroy the pressed outline
                if (!isPressed()) {
                    mPressedOrFocusedBackground = null;
                }

                mLongPressHelper.cancelLongPress();
		//add by flyaudio
		setStayPressed(false);
                break;
        }
        return result;
    }

    void setStayPressed(boolean stayPressed) {
        mStayPressed = stayPressed;
        if (!stayPressed) {
            mPressedOrFocusedBackground = null;
        }
        setCellLayoutPressedOrFocusedIcon();
    }

    void setCellLayoutPressedOrFocusedIcon() {
        if (getParent() instanceof ShortcutAndWidgetContainer) {
            ShortcutAndWidgetContainer parent = (ShortcutAndWidgetContainer) getParent();
            if (parent != null) {
                CellLayout layout = (CellLayout) parent.getParent();
                layout.setPressedOrFocusedIcon((mPressedOrFocusedBackground != null) ? this : null);
            }
        }
    }

    void clearPressedOrFocusedBackground() {
        mPressedOrFocusedBackground = null;
        setCellLayoutPressedOrFocusedIcon();
    }

    Bitmap getPressedOrFocusedBackground() {
        return mPressedOrFocusedBackground;
    }

    int getPressedOrFocusedBackgroundPadding() {
        return HolographicOutlineHelper.MAX_OUTER_BLUR_RADIUS / 2;
    }

    @Override
    public void draw(Canvas canvas) {
        final Drawable background = mBackground;
        if (background != null) {
            final int scrollX = getScrollX();
            final int scrollY = getScrollY();

            if (mBackgroundSizeChanged) {
                background.setBounds(0, 0,  getRight() - getLeft(), getBottom() - getTop());
                mBackgroundSizeChanged = false;
            }

            if ((scrollX | scrollY) == 0) {
                background.draw(canvas);
            } else {
                canvas.translate(scrollX, scrollY);
                background.draw(canvas);
                canvas.translate(-scrollX, -scrollY);
            }
        }

        // If text is transparent, don't draw any shadow
        if (getCurrentTextColor() == getResources().getColor(android.R.color.transparent)) {
            getPaint().clearShadowLayer();
            super.draw(canvas);
            return;
        }

        // We enhance the shadow by drawing the shadow twice
        getPaint().setShadowLayer(SHADOW_LARGE_RADIUS, 0.0f, SHADOW_Y_OFFSET, SHADOW_LARGE_COLOUR);
        super.draw(canvas);
        canvas.save(Canvas.CLIP_SAVE_FLAG);
        canvas.clipRect(getScrollX(), getScrollY() + getExtendedPaddingTop(),
                getScrollX() + getWidth(),
                getScrollY() + getHeight(), Region.Op.INTERSECT);
        getPaint().setShadowLayer(SHADOW_SMALL_RADIUS, 0.0f, 0.0f, SHADOW_SMALL_COLOUR);
        super.draw(canvas);
        canvas.restore();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mBackground != null) mBackground.setCallback(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mBackground != null) mBackground.setCallback(null);
    }

    @Override
    protected boolean onSetAlpha(int alpha) {
        if (mPrevAlpha != alpha) {
            mPrevAlpha = alpha;
            super.onSetAlpha(alpha);
        }
        return true;
    }

    @Override
    public void cancelLongPress() {
        super.cancelLongPress();

        mLongPressHelper.cancelLongPress();
    }

	public static String bSubstring(String s, int length, int mTextWidth) 
    {
		try {
        byte[] bytes = s.getBytes("Unicode");

		int xnum = 0;
		int ni = 0;
		boolean enough = false;
        int n = 0; // 表示当前的字节数
        int i = 2; // 要截取的字节数，从第3个字节开始
    /*    for (; i < bytes.length; i++)
        {
            // 奇数位置，如3、5、7等，为UCS2编码中两个字节的第二个字节
            if (i % 2 == 1)
            {

                 // 在UCS2第二个字节时n加1
                 if (bytes[i] != 0) {
				 	xnum+=2;

                 	}
				 else xnum++;

				 if(xnum > length && !enough) {
				 	i--;
				 	enough = true;		// ���� lenth������
					n = xnum;
					ni = i;
				}
				
            }
        }

		if(enough) {
			Log.d("DDD", "xnum "+xnum+"  "+i);

			if(xnum <= length+4) 
				ni = i;
		}else
			ni = i;

		

		
        // 如果i为奇数时，处理成偶数
        if (ni % 2 == 1)

        {
            // 该UCS2字符是汉字时，去掉这个截一半的汉字
            if (bytes[ni - 1] != 0)
                ni = ni - 1;
            // 该UCS2字符是字母或数字，则保留该字符
           else
                ni = ni + 1;
        }
		String retStr = new String(bytes, 0, ni, "Unicode"); //Unicode

		if ( n > length && xnum >(length+4))
			retStr = retStr+"...";

	*/
		final Paint paint = new Paint();
		paint.setTextAlign(Paint.Align.CENTER);
		paint.setTextSize(mPaintTextSize);
		ni = bytes.length;
		String retStr = new String(bytes, 0, ni, "Unicode"); //Unicode
		float textWidth = paint.measureText(retStr);

		if(textWidth < mTextWidth)
			return retStr;


		while(textWidth >=mTextWidth) {  //�޶��������С��ͼ���
				ni-=2;
				retStr = new String(bytes, 0, ni, "Unicode"); //Unicode
				retStr = retStr+"...";
				textWidth = paint.measureText(retStr);
			}

		
        return retStr;
			}catch (Exception e ){
			Log.d("DDD", " bSubstring "+e);
				}
		return s;
    }
}
