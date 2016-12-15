package com.flyaudio.object;

import com.flyaudio.backcar.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;

public class FlyTextButtonObj extends FlyButtonObj {

	public String mText = null;
	private int mOffsetX = 0;
	private int mOffsetY = 0;
	private int mFrontStyle = 0;
	private int mFrontSize = 15;
	private int mTextAlign = 0;
	private int upColor = 0x000000;
	private int downColor = 0x9D9D9D;
	private int mFrontColor = 0xFF585858;

	public boolean mTouchable = true;
	public int mSwitchColor = 0;
	private boolean isDoubleLine = false;
	private int btnTextLength = 0;

	// zwj
	private int mLines = 1;
	// 0浠ｈ〃鏂囧瓧瓒呭嚭閮ㄥ垎鐢�...缁撴潫锛�1浠ｈ〃澶氳鏄剧ず
	private int mDrawType = 0;

	public FlyTextButtonObj(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub

		TypedArray a = getContext().obtainStyledAttributes(attrs,
				R.styleable.FlyTextButtonObj);

		mText = a.getString(R.styleable.FlyTextButtonObj_m_Text);
		mOffsetX = a.getInteger(R.styleable.FlyTextButtonObj_m_Offsetx, 0);
		mOffsetY = a.getInteger(R.styleable.FlyTextButtonObj_m_Offsety, 0);
		mFrontStyle = a.getInteger(R.styleable.FlyTextButtonObj_m_Fontstyle, 0);
		mFrontSize = a.getInteger(R.styleable.FlyTextButtonObj_m_FontSize, 15);
		mTextAlign = a.getInteger(R.styleable.FlyTextButtonObj_m_TextAlign, 0);
		upColor = a.getInteger(R.styleable.FlyTextButtonObj_upColor, 0);
		downColor = a.getInteger(R.styleable.FlyTextButtonObj_downColor, 0);

		isDoubleLine = a.getBoolean(R.styleable.FlyTextButtonObj_isDoubleLine,
				false);

		btnTextLength = a.getInteger(
				R.styleable.FlyTextButtonObj_btnTextLength, 0);
		// zwj
		mLines = a.getInteger(R.styleable.FlyTextButtonObj_m_Lines, 1);
		mDrawType = a.getInteger(R.styleable.FlyTextButtonObj_m_DrawType, 0);
		a.recycle();
	}

	public void setmSwitchColor(int mSwitchColor) {
		this.mSwitchColor = mSwitchColor;
	}

	public void setmText(String mText) {
		this.mText = mText;
	}

	public void setmTouchable(boolean mTouchable) {
		this.mTouchable = mTouchable;
	}

	public void setBackground(int imgIndex) {
		mSwitchColor = imgIndex;
		getBackground().setLevel(imgIndex);
		if (imgIndex == 0) {
			setmSwitchColor(mSwitchColor);
			setTextColor(mFrontColor);

		} else if (imgIndex == 1) {
			setmSwitchColor(mSwitchColor);
			setTextColor(downColor);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		if (m_bTouchable == false)
			return true;

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (m_bActionUI) {
				mSwitchColor = 1;
				if (mTouchable)
					setTextColor(downColor);
			}
			break;
		case MotionEvent.ACTION_UP:
			if (m_bActionUI) {
				mSwitchColor = 0;
				if (mTouchable)
					setTextColor(upColor);
			}
			break;
		}

		return super.onTouchEvent(event);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		if (mText == null)
			return;
		super.onDraw(canvas);

		int width = this.getWidth();
		TextView mTextView = new TextView(getContext());
		Paint mPaint = mTextView.getPaint();

		switch (mFrontStyle) {
		case 0:
			mPaint.setTypeface(Typeface.DEFAULT);
			break;
		case 1:
			mPaint.setTypeface(Typeface.DEFAULT_BOLD);
			break;
		}

		switch (mTextAlign) {
		case 0:
			mPaint.setTextAlign(Paint.Align.LEFT);
			break;
		case 1:
			mPaint.setTextAlign(Paint.Align.RIGHT);
			break;
		case 2:
			mPaint.setTextAlign(Paint.Align.CENTER);
			break;
		}

		mPaint.setTextSize(mFrontSize);
		mPaint.setStyle(Style.FILL);
		mPaint.setAntiAlias(true);

		switchTextColor(mPaint, mSwitchColor);

		if (isDoubleLine) {
			// m_text = "number\nName";
			Log.d("wen", "text total width = " + width);
			String[] texts = mText.split("\n");
			if (mPaint.measureText(texts[0]) > (width / 1.9)) {
				String str0 = "";
				StringBuffer sb = new StringBuffer("");
				for (int i = 0; i < texts[0].length(); i++) {
					str0 += texts[0].charAt(i);
					if (mPaint.measureText(str0) < (width / 1.9))
						sb.append(texts[0].charAt(i));
				}
				texts[0] = sb.toString();
				Log.d("wen", "name:=" + texts[0]);
				str0 = null;
			}
			canvas.drawText(texts[0], mOffsetX, mOffsetY, mPaint);
			try {
				if (texts[1] != null) {
					if (mSwitchColor == 0)
						mPaint.setColor(0xFF02C5FD);
					// canvas.drawText(texts[1],mPaint.measureText(texts[0])+50,
					// 30, mPaint);
					if (mPaint.measureText(texts[1]) > (width / 2.5)) {
						String str1 = "";
						StringBuffer sb = new StringBuffer("");
						for (int i = 0; i < texts[1].length(); i++) {
							str1 += texts[1].charAt(i);
							if (mPaint.measureText(str1) < (width / 2.5))
								sb.append(texts[1].charAt(i));
						}
						texts[1] = sb.toString();
						Log.d("wen", "number:=" + texts[1]);
						str1 = null;
					}
					canvas.drawText(texts[1], (float) ((width / 1.7)),
							mOffsetY, mPaint);
					texts = null;
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				// TODO: handle exception
			}
		} else {
			if (mText.length() > btnTextLength) {
				float y = mPaint.getTextSize();
				String[] texts = mText.split(" ");
				canvas.drawText(texts[0], mOffsetX, mOffsetY - mFrontSize / 2,
						mPaint);
				try {
					String text = texts[1];
					if (text != null)
						canvas.drawText(text, mOffsetX, mOffsetY + y
								- mFrontSize / 2, mPaint);
				} catch (ArrayIndexOutOfBoundsException e) {
					// TODO: handle exception
				}
			} else {
				canvas.drawText(mText, mOffsetX, mOffsetY, mPaint);
			}
		}
	}

	private void switchTextColor(Paint mPaint, int which) {
		if (which == 0)
			mPaint.setColor(upColor);
		if (which == 1)
			mPaint.setColor(downColor);
		if (which == 2)
			mPaint.setColor(mFrontColor);
	}

	public void setTextColor(int color) {
		if (mTouchable)
			mFrontColor = 0xFF585858;
		else
			mFrontColor = color;
		invalidate();
	}

	private void drawText(Canvas canvas, int width, Paint mPaint,
			int requetLines) {
		int totallines = getTextLines(width, mPaint);
		Log.d("text", "totallines= " + totallines);
		if (totallines == 1) {
			Log.d("text", "draw Single text");
			canvas.drawText(mText, mOffsetX, mOffsetY, mPaint);

		} else if (totallines > 1) {
			Log.d("text", "draw Multi text");

			if (requetLines > totallines) { // 褰撹姹傜殑琛屾暟瓒呭嚭鎬昏鏁版椂锛岃姹傜粯鐢昏鏁版湭鎬昏鏁�
				requetLines = totallines;
			}

			String str = "";
			int end[] = new int[requetLines];
			int begin[] = new int[requetLines];
			int k = 0;
			int length = mText.length();
			Log.d("text", "length=" + length);
			for (int i = 0; i < length; i++) {
				str += mText.charAt(i);
				if (mPaint.measureText(str) >= width) {
					if (k < requetLines) {
						end[k] = i;
						if (k == 0)
							begin[k] = 0;
						else
							begin[k] = end[k - 1];
						k++;
						i--;
						str = "";
					}
				}
			}
			Log.d("text", "k=" + k);
			if (length - end[k - 1] >= 0) { // 褰撴渶鍚庝竴琛岀殑闀垮害涓嶈冻鐢诲竷瀹藉害鏃讹紝闇�灏嗗墿涓嬬殑瀛楃鍏ㄩ儴缁樼敾
				if (requetLines != 1) {
					Log.d("text", "requetLines != 1");
					begin[k] = end[k - 1];
					end[k] = length;
				}
			}
			for (int i = 0; i < requetLines; i++) {
				Log.d("text", "begin:" + begin[i] + " end:" + end[i]);
				if (i == 0) {
					canvas.drawText(mText.substring(begin[i], end[i]),
							mOffsetX, mOffsetY + mFrontSize * i, mPaint);
				} else if (i == 1) {
					canvas.drawText(mText.substring(begin[i], end[i]), 100,
							mOffsetY + mFrontSize * i, mPaint);
				}

			}
		}
	}

	private void drawText2(Canvas canvas, int width, Paint mPaint) {
		String str = "...";
		StringBuffer sb = new StringBuffer("");
		if (mPaint.measureText(mText) > width) {
			for (int i = 0; i < mText.length(); i++) {
				str += mText.charAt(i);
				if (mPaint.measureText(str) < width)
					sb.append(mText.charAt(i));
			}
			canvas.drawText(sb.toString() + "...", mOffsetX, mOffsetY, mPaint);
		} else {
			canvas.drawText(mText, mOffsetX, mOffsetY, mPaint);
		}
	}

	// 鑾峰彇瀛楃涓查渶瑕佺粯鐢荤殑鎬昏鏁�
	private int getTextLines(int width, Paint mPaint) {
		if (mPaint.measureText(mText) >= width) {
			String str = "";
			int count = 0;
			int position = 0;
			int length = mText.length();
			Log.d("text", "length=" + length);
			for (int i = 0; i < length; i++) {
				str += mText.charAt(i);
				if (mPaint.measureText(str) >= width) {
					Log.d("text", str);
					count++;
					position = i;
					Log.d("text", "position=" + position);
					i--;
					str = "";
				}
			}
			if (length - position > 0) {
				count++;
			}
			return count;
		} else {
			return 1;
		}
	}

}
