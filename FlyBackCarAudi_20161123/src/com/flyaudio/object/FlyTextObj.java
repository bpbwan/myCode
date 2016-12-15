package com.flyaudio.object;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import com.flyaudio.backcar.R;

public class FlyTextObj extends FlyUIObj {
	final String TAG = "BackCarActivity";
	private int mFrontSize = 15;
	private int mFrontColor = 0xFFFFFF;
	private int mFrontStyle = 0;
	public String mText = null;
	private int mOffsetX = 0;
	private int mOffsetY = 0;
	private int mTextAlign = 0;
	// zwj
	private int mLines = 1;
	// 0浠ｈ〃鏂囧瓧瓒呭嚭閮ㄥ垎鐢�...缁撴潫锛�1浠ｈ〃澶氳鏄剧ず
	private int mDrawType = 0;

	public FlyTextObj(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.FlyTextObj);

		mFrontSize = a.getInteger(R.styleable.FlyTextObj_mFontSize, 15);
		mFrontColor = a.getInteger(R.styleable.FlyTextObj_mFontColor, 0xFFFFFF);
		mFrontStyle = a.getInteger(R.styleable.FlyTextObj_mFontstyle, 0);
		mText = a.getString(R.styleable.FlyTextObj_mText);
		mOffsetX = a.getInteger(R.styleable.FlyTextObj_mOffsetx, 0);
		mOffsetY = a.getInteger(R.styleable.FlyTextObj_mOffsety, 0);
		mTextAlign = a.getInteger(R.styleable.FlyTextObj_mTextAlign, 0);
		// zwj
		mLines = a.getInteger(R.styleable.FlyTextObj_mLines, 1);
		mDrawType = a.getInteger(R.styleable.FlyTextObj_mDrawType, 0);
		a.recycle();
	}

	public void setmText(String mText) {
		// Log.d("ana","--text--"+mText);

		this.mText = mText;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		if (mText == null) {
			return;
		}
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
		mPaint.setColor(mFrontColor);
		mPaint.setStyle(Style.FILL);
		mPaint.setAntiAlias(true);

		switch (mDrawType) {
		case 0:
			drawText2(canvas, width, mPaint);
			break;
		case 1:
			drawText(canvas, width, mPaint, mLines);
			break;
		}
	}

	// 鑾峰彇瀛楃涓查渶瑕佺粯鐢荤殑鎬昏鏁�
	private int getTextLines(int width, Paint mPaint) {
		if (mPaint.measureText(mText) >= width) {
			String str = "";
			int count = 0;
			int position = 0;
			int length = mText.length();
			// Log.d("text", "length=" + length);
			for (int i = 0; i < length; i++) {
				str += mText.charAt(i);
				if (mPaint.measureText(str) >= width) {
					Log.d("text", str);
					count++;
					position = i;
					// Log.d("text", "position=" + position);
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

	private void drawText(Canvas canvas, int width, Paint mPaint,
			int requetLines) {
		int totallines = getTextLines(width, mPaint);
		// Log.d("text", "totallines= " + totallines);
		if (totallines == 1) {
			// Log.d("text", "draw Single text");
			canvas.drawText(mText, mOffsetX, mOffsetY, mPaint);

		} else if (totallines > 1) {
			// Log.d("text", "draw Multi text");

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
			// Log.d("text", "k=" + k);
			if (length - end[k - 1] >= 0) { // 褰撴渶鍚庝竴琛岀殑闀垮害涓嶈冻鐢诲竷瀹藉害鏃讹紝闇�灏嗗墿涓嬬殑瀛楃鍏ㄩ儴缁樼敾
				if (requetLines != 1) {
					// Log.d("text", "requetLines != 1");
					begin[k] = end[k - 1];
					end[k] = length;
				}
			}
			for (int i = 0; i < requetLines; i++) {
				// Log.d("text", "begin:" + begin[i] + " end:" + end[i]);
				canvas.drawText(mText.substring(begin[i], end[i]), mOffsetX,
						mOffsetY + mFrontSize * i, mPaint);
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
}
