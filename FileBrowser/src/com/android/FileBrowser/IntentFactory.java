package com.android.FileBrowser;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.Media;
import android.util.Log;

public class IntentFactory {

	public static void IntentTest(Activity activity, String type, Bundle bundle) {

		Intent mIntent = null;
		if (type.equals("goolgle_search")) {
			mIntent = CreateGoolgleSearch(bundle);
		} else if (type.equals("home")) {
			mIntent = CreateHome(bundle);
		} else if (type.equals("web")) {
			mIntent = CreateWeb(bundle);
		} else if (type.equals("call")) {
			mIntent = CreateCall(bundle);
		} else if (type.equals("msg")) {
			mIntent = CreateMsg(bundle);
		} else if (type.equals("map")) {
			mIntent = CreateMap(bundle);
		} else if (type.equals("music")) {
			mIntent = CreateMusic(bundle);
		} else if (type.equals("capture")) {
			mIntent = CreateCamera(bundle);
		}
		else if (type.equals("imagecrop")) {
			mIntent = CreateImageCrop(bundle);
		}
		else if (type.equals("imagecrop2")) {
			mIntent = CreateImageCrop2(bundle);
		}
		else if (type.equals("wireless")) {
			mIntent = CreateWireless(bundle);
		}
		else if (type.equals("wifisetting")) {
			mIntent = CreateWifisetting(bundle);
		}
		
		else if (type.equals("bluetooth")) {
			mIntent = CreateBlueTooth(bundle);
		}
		else if (type.equals("local_setting")) {
			mIntent = CreateLocalSetting(bundle);
		}
		else if (type.equals("record")) {
			mIntent = CreateRecord(bundle);
		}

		Log.d("AAAA", "IntentTest " + type);
		if (mIntent != null) {
			// activity.startActivity(mIntent);
			activity.startActivityForResult(mIntent, 0);
		}
	}

	private static Intent CreateGoolgleSearch(Bundle bundle) {

		return null;
	}

	private static Intent CreateHome(Bundle bundle) {
		Intent mIntent = new Intent();
		mIntent.setAction(Intent.ACTION_MAIN);
		mIntent.addCategory(Intent.CATEGORY_HOME);
		return mIntent;
	}

	private static Intent CreateWeb(Bundle bundle) {
		Intent mIntent = new Intent();
		mIntent.setAction(Intent.ACTION_VIEW);
		String uri = bundle.getString("web");
		mIntent.setData(Uri.parse(uri));
		return mIntent;
	}

	private static Intent CreateCall(Bundle bundle) {
		Intent mIntent = new Intent();
		mIntent.setAction(Intent.ACTION_DIAL);
		String uri = bundle.getString("call");
		mIntent.setData(Uri.parse(uri));
		return mIntent;
	}

	private static Intent CreateMsg(Bundle bundle) {
		Intent mIntent = new Intent();
		mIntent.setAction(Intent.ACTION_SENDTO);
		String uri = bundle.getString("msg");
		mIntent.setData(Uri.parse(uri));
		mIntent.putExtra("sms_body", "hello!");
		return mIntent;
	}

	private static Intent CreateMap(Bundle bundle) {

		// Uri uri = Uri.parse("geo:39.9,116.3");
		Intent mIntent = new Intent();
		mIntent.setAction(Intent.ACTION_VIEW);
		String uri = bundle.getString("map");
		mIntent.setData(Uri.parse(uri));
		return mIntent;
	}

	private static Intent CreateMusic(Bundle bundle) {

		Intent mIntent = new Intent();
		mIntent.setAction(Intent.ACTION_VIEW);
		String uri = bundle.getString("music");
		String type = bundle.getString("type");
		mIntent.setDataAndType(Uri.parse(uri), type);
		return mIntent;
	}

	private static Intent CreateCamera(Bundle bundle) {

		Intent mIntent = new Intent();
		mIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

		return mIntent;
	}

	private static Intent CreateImageCrop(Bundle bundle) {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		intent.putExtra("crop", "true"); // 开启剪切
		intent.putExtra("aspectX", 1); // 剪切的宽高比为1：2
		intent.putExtra("aspectY", 2);
		intent.putExtra("outputX", 20); // 保存图片的宽和高
		intent.putExtra("outputY", 40);
		intent.putExtra("output", Uri.fromFile(new File("/mnt/sdcard/PNG"))); // 保存路径
		intent.putExtra("outputFormat", "JPEG");// 返回格式
		return intent;
	}
	
	private static Intent CreateImageCrop2(Bundle bundle) {
		Intent intent = new Intent("com.android.camera.action.CROP"); 
		intent.setClassName("com.android.camera", "com.android.camera.CropImage"); 
		intent.setData(Uri.fromFile(new File("/mnt/sdcard/PNG"))); 
		intent.putExtra("outputX", 1); // 剪切的宽高比为1：2
		intent.putExtra("outputY", 2);
		intent.putExtra("aspectX", 20); // 保存图片的宽和高
		intent.putExtra("aspectY", 40);
		intent.putExtra("scale", true);
		intent.putExtra("noFaceDetection", true); 
		intent.putExtra("output", Uri.parse("file:///mnt/sdcard/temp")); 
		return intent;
	}
	
	
	private static Intent CreateWireless(Bundle bundle) {

		Intent mIntent = new Intent();
		mIntent.setAction(android.provider.Settings.ACTION_WIRELESS_SETTINGS);

		return mIntent;
	}
	
	
	private static Intent CreateWifisetting(Bundle bundle) {

		Intent mIntent = new Intent();
		mIntent.setAction(android.provider.Settings.ACTION_WIFI_SETTINGS);

		return mIntent;
	}
	
	
	private static Intent CreateBlueTooth(Bundle bundle) {

		Intent mIntent = new Intent();
		mIntent.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);

		return mIntent;
	}
	
	private static Intent CreateLocalSetting(Bundle bundle) {

		Intent mIntent = new Intent();
		mIntent.setAction(android.provider.Settings.ACTION_LOCALE_SETTINGS);

		return mIntent;
	}
	
	private static Intent CreateRecord(Bundle bundle) {
	//打开录音机
	Intent mIntent = new Intent(Media.RECORD_SOUND_ACTION); 
	return mIntent;
	}
}
