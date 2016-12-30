package com.android.FileBrowser;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/** Activity������ **/
public class FileActivityHelper {

	/** ��ȡһ���ļ����µ������ļ� **/
	public static ArrayList<FileInfo> getFiles(Activity activity, String path) {
		File f = null;
		File[] files = null;
		try { // ��ȡ�ļ�
			f = new File(path);
			files = f.listFiles();
			if (files == null) {
				Toast.makeText(activity,
						String.format(activity.getString(R.string.file_cannotopen), path),
						Toast.LENGTH_SHORT).show();
				return null;
			}
		} catch (Exception ex) {
			Toast.makeText(activity, ex.getMessage(), Toast.LENGTH_SHORT).show();
		}

		ArrayList<FileInfo> fileList = new ArrayList<FileInfo>();
		// ��ȡ�ļ��б�
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			FileInfo fileInfo = new FileInfo();
			fileInfo.Name = file.getName();
			fileInfo.IsDirectory = file.isDirectory();
			fileInfo.Path = file.getPath();
			fileInfo.Size = file.length();
			fileList.add(fileInfo);
		}

		// ����
		Collections.sort(fileList, new FileNumCountComparator());

		return fileList;
	}

	/** �������ļ��� **/
	public static void createDir(final Activity activity, final String path, final Handler hander) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		LayoutInflater inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.file_create, null);
		final EditText text = (EditText) layout.findViewById(R.id.file_name);
		builder.setView(layout);
		builder.setPositiveButton(R.string.ok, new OnClickListener() {
			public void onClick(DialogInterface dialoginterface, int i) {
				String newName = text.getText().toString().trim();
				if (newName.length() == 0) {
					Toast.makeText(activity, R.string.file_namecannotempty, Toast.LENGTH_SHORT)
							.show();
					return;
				}
				String fullFileName = FileUtil.combinPath(path, newName);
				File newFile = new File(fullFileName);
				if (newFile.exists()) {
					Toast.makeText(activity, R.string.file_exists, Toast.LENGTH_SHORT).show();
				} else {
					if (newFile.mkdir()) {
						hander.sendEmptyMessage(0); // �ɹ�
					} else {
						Toast.makeText(activity, R.string.file_create_fail, Toast.LENGTH_SHORT)
								.show();
					}
				}
			}
		}).setNegativeButton(R.string.cancel, null);
		AlertDialog alertDialog = builder.create();
		alertDialog.setTitle(R.string.mainmenu_createdir);
		alertDialog.show();
	}

	/** �������ļ� **/
	public static void renameFile(final Activity activity, final File f, final Handler hander) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		LayoutInflater inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.file_rename, null);
		final EditText text = (EditText) layout.findViewById(R.id.file_name);
		text.setText(f.getName());
		builder.setView(layout);
		builder.setPositiveButton(R.string.ok, new OnClickListener() {
			public void onClick(DialogInterface dialoginterface, int i) {
				String path = f.getParentFile().getPath();
				String newName = text.getText().toString().trim();
				if (newName.equalsIgnoreCase(f.getName())) {
					return;
				}
				if (newName.length() == 0) {
					Toast.makeText(activity, R.string.file_namecannotempty, Toast.LENGTH_SHORT)
							.show();
					return;
				}
				String fullFileName = FileUtil.combinPath(path, newName);

				File newFile = new File(fullFileName);
				if (newFile.exists()) {
					Toast.makeText(activity, R.string.file_exists, Toast.LENGTH_SHORT).show();
				} else {
					if (f.renameTo(newFile)) {
						hander.sendEmptyMessage(0); // �ɹ�
					} else {
						Toast.makeText(activity, R.string.file_rename_fail, Toast.LENGTH_SHORT)
								.show();
					}
				}
			}
		}).setNegativeButton(R.string.cancel, null);
		AlertDialog alertDialog = builder.create();
		alertDialog.setTitle(R.string.file_rename);
		alertDialog.show();
	}

	/** �鿴�ļ����� **/
	public static void viewFileInfo(Activity activity, File f) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		LayoutInflater inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.file_info, null);
		FileInfo info = FileUtil.getFileInfo(f);

		((TextView) layout.findViewById(R.id.file_name)).setText(f.getName());
		((TextView) layout.findViewById(R.id.file_lastmodified)).setText(new Date(f.lastModified())
				.toLocaleString());
		((TextView) layout.findViewById(R.id.file_size))
				.setText(FileUtil.formetFileSize(info.Size));
		if (f.isDirectory()) {
			((TextView) layout.findViewById(R.id.file_contents)).setText("Folder "
					+ info.FolderCount + ", File " + info.FileCount);
		}

		builder.setView(layout);
		builder.setPositiveButton(R.string.ok, new OnClickListener() {
			public void onClick(DialogInterface dialoginterface, int i) {
				dialoginterface.cancel();
			}
		});
		AlertDialog alertDialog = builder.create();
		alertDialog.setTitle(R.string.file_info);
		alertDialog.show();
	}
}
