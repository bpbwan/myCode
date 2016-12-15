package com.flyaudio.tool;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;

import android.util.Log;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import com.flyaudio.backcar.BackCarService;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;
import android.os.Message;
import java.io.IOException;
import android.os.Handler;
import android.view.View.OnClickListener;
import com.flyaudio.backcar.R;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.graphics.Bitmap;

import com.flyaudio.backcar913.BackCarTag;
import com.flyaudio.globaldefine.PageID;

public class TrackCap {
	BackCarService mBackCarService;
	static TrackCap mTrackCap;
	View TrackCapView;
	EditText ediend;
	EditText edistart;
	EditText edimax;
	Button bn2;
	Button bn3;
	Button bn4;
	Button bn5;

	static int startAngle = 0;
	static int endAngle = 0;
	static int maxangle = 0;
	static double s_maxAn = 0;
	static int track_s = 0;

	public static TrackCap getInstance(BackCarService Service) {
		if (mTrackCap == null) {
			mTrackCap = new TrackCap(Service);

		}
		return mTrackCap;
	}

	public TrackCap(BackCarService Service) {

		mBackCarService = Service;
		init();
	}

	void init() {
		LayoutInflater inflater = (LayoutInflater) mBackCarService
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		TrackCapView = inflater.inflate(R.layout.track_cap, null);

		Log.d("DDD", "TrackCapView " + TrackCapView);

		edistart = (EditText) TrackCapView.findViewWithTag("0010001");
		ediend = (EditText) TrackCapView.findViewWithTag("0010002");
		edimax = (EditText) TrackCapView.findViewWithTag("0010007");

		bn2 = (Button) TrackCapView.findViewWithTag("0010003");
		bn3 = (Button) TrackCapView.findViewWithTag("0010004");
		bn4 = (Button) TrackCapView.findViewWithTag("0010005");
		bn5 = (Button) TrackCapView.findViewWithTag("0010006");

		bn2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				String max = edimax.getText().toString();

				if (max != null && max.length() > 0)
					maxangle = Integer.parseInt(max);

				String str = edistart.getText().toString();

				if (str != null && str.length() > 0)
					startAngle = Integer.parseInt(str);
				String str2 = ediend.getText().toString();
				Log.i("DDD", "start " + str + "  end " + str2 + " max " + max);
				if (str2 != null && str2.length() > 0)
					endAngle = Integer.parseInt(str2);

				Message tmpMsg = Message.obtain();
				tmpMsg.what = 1;
				trackCapHandler.sendMessage(tmpMsg);
				new Thread(new TrakcCapRunnable()).start();

			}
		});

		bn3.setOnClickListener(new OnClickListener() {
			boolean isshow = true;

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!isshow)
					mBackCarService.getBackCarModule().ShowUiView(0x00070632,
							true);
				else
					mBackCarService.getBackCarModule().ShowUiView(0x00070632,
							false);
				isshow = !isshow;
			}
		});

		bn4.setOnClickListener(new OnClickListener() {
			boolean isshow = true;

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!isshow) {
					mBackCarService.getBackCarModule().ShowUiView(
							BackCarTag.bg913Front_150, true);
					mBackCarService.getBackCarModule().ShowUiView(
							BackCarTag.bg913Rear_150, true);
				} else {
					mBackCarService.getBackCarModule().ShowUiView(
							BackCarTag.bg913Front_150, false);
					mBackCarService.getBackCarModule().ShowUiView(
							BackCarTag.bg913Rear_150, false);

				}
				isshow = !isshow;
			}
		});

		bn5.setOnClickListener(new OnClickListener() {
			boolean isshow = true;

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!isshow)
					mBackCarService.getBackCarModule().ShowUiView(0x00071113,
							true);
				else
					mBackCarService.getBackCarModule().ShowUiView(0x00071113,
							false);
				isshow = !isshow;

			}
		});
	}

	static double tmpAngle = 0;
	static double xi = 1;

	public class TrakcCapRunnable implements Runnable {
		private boolean consumeStop = true;

		public void stop() {
			consumeStop = false;
		}

		@Override
		public void run() {
			if (startAngle >= endAngle)
				return;

			String path = "/sdcard/PNG/";
			String filename = "track";
			String saveName = filename;
			tmpAngle = startAngle;
			s_maxAn = (double) (90 - maxangle / 2);
			track_s = (int) (endAngle - startAngle) / 2;

			xi = (double) maxangle / (endAngle - startAngle);
			Log.d("DDD", "TrakcCapRunnable  " + xi);
			try {
				Runtime runtime = Runtime.getRuntime();
				runtime.exec("mkdir /sdcard/PNG");
			} catch (IOException e) {
				Log.e("Exception when doBack", e.toString());
			}
			// 50 /30 , 60 *30/50

			while (tmpAngle <= endAngle) {
				Message tmpMsg = Message.obtain();
				tmpMsg.what = 2;
				trackCapHandler.sendMessage(tmpMsg);
				try {
					Thread.sleep(100);
				} catch (Exception e) {
					// Log.e(TAG, "BackCar Exception e");
					e.printStackTrace();
				}

				double s = 0;
				if (tmpAngle <= 90)
					s = s_maxAn + (track_s - (90 - tmpAngle)) * xi;
				else
					s = 90 + (tmpAngle - 90) * xi;
				saveName = path + filename + "" + tmpAngle + ".png";
				tmpAngle++;

				try {
					Runtime runtime = Runtime.getRuntime();
					String exc = "screencap -p " + saveName;
					runtime.exec(exc);
					Log.d("DDD", "screencap -p " + saveName + "   " + s);
				} catch (IOException e) {
					Log.e("Exception when doBack", e.toString());
				}

				// cap(saveName);
				try {
					Thread.sleep(1500);
				} catch (Exception e) {
					// Log.e(TAG, "BackCar Exception e");
					e.printStackTrace();
				}

			}

			saveName = path + filename + "" + 90 + ".png";
			try {
				Runtime runtime = Runtime.getRuntime();
				String exc = "screencap -p " + saveName;
				runtime.exec(exc);
				Log.d("DDD", "screencap -p " + saveName);
			} catch (IOException e) {
				Log.e("Exception when doBack", e.toString());
			}

		}

	}

	Handler trackCapHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 1) {
				hide();
			} else if (msg.what == 2) {
				double s = 0.0f;
				if (tmpAngle <= 90)
					s = s_maxAn + (track_s - (90 - tmpAngle)) * xi;
				else
					s = 90 + (tmpAngle - 90) * xi;
				mBackCarService.getFlyBackCarMainView().flyBackCarView
						.update(s);
			}
		}
	};

	public void show() {

		mBackCarService.getFlyBackCarMainView().LayoutAddViewWithPageID(
				TrackCapView, PageID.PAGE_COMMON_TRACKCAP);

	}

	public void hide() {

		mBackCarService.getFlyBackCarMainView().LayoutRemoveViewWithPageID(
				PageID.PAGE_COMMON_TRACKCAP);

	}

	public void cap(String str) {
		String fname = str;

		View view = (View) (mBackCarService.getFlyBackCarMainView().mBackCarMainView)
				.getRootView();

		view.setDrawingCacheEnabled(true);

		view.buildDrawingCache();

		Bitmap bitmap = view.getDrawingCache();

		if (bitmap != null) {

			try {

				FileOutputStream out = new FileOutputStream(fname);

				bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);

			} catch (Exception e) {

				e.printStackTrace();

			}

		} else {

		}

	}

}
