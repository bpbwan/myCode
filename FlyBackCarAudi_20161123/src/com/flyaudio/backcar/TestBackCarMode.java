package com.flyaudio.backcar;

import java.lang.String;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import com.flyaudio.backcar913.*;
import com.flyaudio.backcarcamera.*;
import java.util.Arrays;
import android.os.Message;
import android.os.Handler;
import android.os.Messenger;
import android.util.Log;
import android.os.SystemProperties;
import java.util.Random;
import android.content.Intent;

public class TestBackCarMode {

	private BackCarService mBackCarService = null;
	private BackCar913Service mBackCar913Service = null;

	private static String TESTMODE_1 = "flyaudio.intent.action.backcar.test1"; // cvb
																				// in
																				// and
																				// out
	private static String TESTMODE_2 = "flyaudio.intent.action.backcar.test2"; // 913
																				// io
																				// in
																				// or
																				// stall
																				// D
																				// in
																				// and
																				// out
	private static String TESTMODE_3 = "flyaudio.intent.action.backcar.test3"; // 913D
	private static String TESTMODE_4 = "flyaudio.intent.action.backcar.test4"; // random
																				// cvb
																				// or
																				// 913
	private static String EXITTEST = "flyaudio.intent.action.backcar.testout"; // exit

	private String TAG = "TestBackCar";
	private String curStatus = null;
	private int curStatusI = 0;
	private ModeType md = null;

	static final int SET150VIDEO_CID = 0x070d0003;
	static final int SET180VIDEO_CID = 0x070d0005;
	static final int SETLIGHT_CID = 0x70d0006;
	static final int SETLIGHT_VIEWTAG = 0x070d0020;
	static final int VIDEOTOCHOICE = 0x070d0001;

	static final int CHOICE_BNT = 0x070e0015;
	static final int CHOICEBACK_BNT = 0x070e0016;

	public boolean random = false;

	enum ModeType {
		CVB, m913, m913D, USER913, Exit
	}

	TestBackCarMode(BackCarService service) {
		this.mBackCarService = service;
		this.mBackCar913Service = service.mBackCar913Service;
		initTestBackCar();
	}

	void initTestBackCar() {

		md = ModeType.CVB;
		IntentFilter filter = new IntentFilter();
		filter.addAction(TESTMODE_1);
		filter.addAction(TESTMODE_2);
		filter.addAction(TESTMODE_3);
		filter.addAction(TESTMODE_4);
		filter.addAction(EXITTEST);
		mBackCarService.registerReceiver(mTestBackCarListener, filter);
	}

	public void DealBackCarMessages(int DealMessages, int iMsgType, int time) {
		if (DealMessages == MsgType.MSG_SENG) {
			myMsgHandler.sendEmptyMessage(iMsgType);
		}
		if (DealMessages == MsgType.MSG_SENGDELAY) {
			myMsgHandler.sendEmptyMessageDelayed(iMsgType, time);
		}
		if (DealMessages == MsgType.MSG_REMOVE) {
			myMsgHandler.removeMessages(iMsgType);
		}
	}

	void testcvbmode() {
		SystemProperties.set("persist.backcar.mode", "3");
		curStatus = " === >cvb";
		Log.d(TAG, "" + curStatus);
		md = ModeType.CVB;
		Thread testcvbmode = new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while (md == ModeType.CVB) {
					DealBackCarMessages(MsgType.MSG_SENG,
							MsgType.MSG_START_CVB, 0);
					try {
						Thread.sleep(3000);
					} catch (Exception e) {
						e.printStackTrace();
					}
					DealBackCarMessages(MsgType.MSG_SENG, MsgType.MSG_STOP, 0);
					try {
						Thread.sleep(3000);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		};
		testcvbmode.start();
	}

	void test913mode() {
		SystemProperties.set("persist.backcar.mode", "2");
		curStatus = " === >913";
		Log.d(TAG, "" + curStatus);
		md = ModeType.m913;
		Thread test913mode = new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while (md == ModeType.m913) {
					DealBackCarMessages(MsgType.MSG_SENG,
							MsgType.MSG_START_CVB, 0);
					try {
						Thread.sleep(3000);
					} catch (Exception e) {
						e.printStackTrace();
					}
					DealBackCarMessages(MsgType.MSG_SENG, MsgType.MSG_STOP, 0);
					try {
						Thread.sleep(3000);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		};
		test913mode.start();
	}

	void test913StallDmode() {
		SystemProperties.set("persist.backcar.mode", "2");
		curStatus = " === >913D";
		Log.d(TAG, "" + curStatus);
		md = ModeType.m913D;
		Thread test913mode = new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while (md == ModeType.m913D) {
					DealBackCarMessages(MsgType.MSG_SENG,
							MsgType.MSG_STADLL_D_IN, 0);
					try {
						Thread.sleep(3000);
					} catch (Exception e) {
						e.printStackTrace();
					}
					DealBackCarMessages(MsgType.MSG_SENG,
							MsgType.MSG_STADLL_D_OUT, 0);
					try {
						Thread.sleep(3000);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		};
		test913mode.start();
	}

	void randomTest() {
		random = true;
		Thread randomTest = new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Random mRandom = new Random();
				int ranNumber = 0;
				while (random) {
					ranNumber = mRandom.nextInt(4);
					switch (ranNumber) {
					case 0:
						SystemProperties.set("persist.backcar.mode", "3");
						Log.d(TAG, "=========>" + curStatus
								+ "--------->cvb<========");
						curStatus = "cvb";
						DealBackCarMessages(MsgType.MSG_SENG,
								MsgType.MSG_START_CVB, 0);
						try {
							Thread.sleep(4000);
						} catch (Exception e) {
							e.printStackTrace();
						}
						DealBackCarMessages(MsgType.MSG_SENG, MsgType.MSG_STOP,
								0);
						Log.d(TAG, "=========>cvb out<========");
						try {
							Thread.sleep(1500);
						} catch (Exception e) {
							e.printStackTrace();
						}

						break;
					case 1:
						SystemProperties.set("persist.backcar.mode", "2");
						Log.d(TAG, "=========>" + curStatus
								+ "-------->913R<========");
						curStatus = "913R";
						DealBackCarMessages(MsgType.MSG_SENG,
								MsgType.MSG_START_CVB, 0);
						try {
							Thread.sleep(1500);
						} catch (Exception e) {
							e.printStackTrace();
						}

						ranNumber = mRandom.nextInt(3);
						switch (ranNumber) {
						case 0:
							break;
						case 1:
							mBackCarService
									.MakeAndSendMessageButton(SET150VIDEO_CID);

							break;
						case 2:
							mBackCarService
									.MakeAndSendMessageButton(SET180VIDEO_CID);
							break;
						}
						try {
							Thread.sleep(1500);
						} catch (Exception e) {
							e.printStackTrace();
						}
						DealBackCarMessages(MsgType.MSG_SENG, MsgType.MSG_STOP,
								0);
						Log.d(TAG, "=========>913R out<========");
						try {
							Thread.sleep(1500);
						} catch (Exception e) {
							e.printStackTrace();
						}
						break;
					case 2:
						Log.d(TAG, "=========>" + curStatus
								+ "---------->913D<========");
						curStatus = "913D";
						DealBackCarMessages(MsgType.MSG_SENG,
								MsgType.MSG_STADLL_D_IN, 0);
						try {
							Thread.sleep(1500);
						} catch (Exception e) {
							e.printStackTrace();
						}

						ranNumber = mRandom.nextInt(3);
						switch (ranNumber) {
						case 0:
							break;
						case 1:
							mBackCarService
									.MakeAndSendMessageButton(SET150VIDEO_CID);

							break;
						case 2:
							mBackCarService
									.MakeAndSendMessageButton(SET180VIDEO_CID);
							break;
						}
						try {
							Thread.sleep(2000);
						} catch (Exception e) {
							e.printStackTrace();
						}
						DealBackCarMessages(MsgType.MSG_SENG,
								MsgType.MSG_STADLL_D_OUT, 0);
						Log.d(TAG, "=========>913D out<========");
						try {
							Thread.sleep(2000);
						} catch (Exception e) {
							e.printStackTrace();
						}

						break;

					case 3:
						Log.d(TAG, "=========>" + curStatus
								+ "-------->913 StartUser<========");
						curStatus = "913 StartUser";
						if (mBackCar913Service.is913StartByUser()) {
							if (BackCar913Service.Mode913.page == 0) // video
								mBackCarService
										.MakeAndSendMessageButton(VIDEOTOCHOICE);
							mBackCarService
									.MakeAndSendMessageButton(CHOICEBACK_BNT);
							try {
								Thread.sleep(2000);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}

						sendStartByUser();
						try {
							Thread.sleep(2000);
						} catch (Exception e) {
							e.printStackTrace();
						}

						ranNumber = mRandom.nextInt(2);
						if (ranNumber == 1) {
							// ranNumber = mRandom.nextInt(1);
							mBackCarService
									.MakeAndSendMessageButton(CHOICE_BNT);

							try {
								Thread.sleep(2000);
							} catch (Exception e) {
								e.printStackTrace();
							}
							ranNumber = mRandom.nextInt(3);
							switch (ranNumber) {
							case 0:
								break;
							case 1:
								mBackCarService
										.MakeAndSendMessageButton(SET150VIDEO_CID);

								break;
							case 2:
								mBackCarService
										.MakeAndSendMessageButton(SET180VIDEO_CID);
								break;
							}
						}
						try {
							Thread.sleep(2000);
						} catch (Exception e) {
							e.printStackTrace();
						}
						break;
					}
				}
			}
		};
		randomTest.start();

	}

	BroadcastReceiver mTestBackCarListener = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			final String action = intent.getAction();
			if (null == action) {
				Log.i(TAG, "mTestBackCarListener - action is null");
				return;
			}
			if (TESTMODE_1.equals(action)) {
				testcvbmode();
			} else if (TESTMODE_2.equals(action)) {
				test913mode();
			} else if (TESTMODE_3.equals(action)) {

			} else if (TESTMODE_4.equals(action)) {
				random = false;
				md = ModeType.Exit;
				Log.i(TAG, "mTestBackCarListener -TESTMODE_4");
				randomTest();
			} else if (EXITTEST.equals(action)) {
				Log.i(TAG, "mTestBackCarListener -EXITTEST");
				random = false;
				md = ModeType.Exit;
			}

		}
	};

	private void sendStallDIn(int m) {
		Intent mintent = new Intent();
		mintent.setAction("android.intent.backcar.frontRearCamera.tf");
		mBackCarService.sendBroadcast(mintent);
	}

	private void sendStallDOut(int m) {
		Intent mintent = new Intent();
		mintent.setAction("android.intent.backcar.frontRearCamera.t");
		mBackCarService.sendBroadcast(mintent);
	}

	private void sendStartByUser() {
		Intent mintent = new Intent();
		mintent.setAction("android.intent.backcar.frontRearCamera");
		mBackCarService.sendBroadcast(mintent);
	}

	Handler myMsgHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MsgType.MSG_START_CVB:
				mBackCarService.IO_START();

				break;
			case MsgType.MSG_START_913:
				mBackCarService.IO_START();

				break;
			case MsgType.MSG_START_CAM:

				break;

			case MsgType.MSG_STOP:
				mBackCarService.IO_STOP();
				break;

			case MsgType.MSG_USER_START:
				sendStartByUser();
				break;

			case MsgType.MSG_STADLL_D_IN:
				sendStallDIn(0);
				break;

			case MsgType.MSG_STADLL_D_OUT:
				sendStallDOut(0);
				break;

			}
		}
	};

}
