package com.android.FileBrowser;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/** ճ���ļ� **/
public class PasteFile extends ListActivity {
	private TextView _filePath;
	private TextView file_process;
	private List<FileInfo> _files;
	private String _rootPath = FileUtil.getSDPath();
	private String _currentPath = _rootPath;
	private final String TAG = "PasteFile";
	private String _currentPasteFilePath = "";
	private String _action = "";
	private ProgressDialog progressDialog;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.file_paste);

		// ��ȡ��Intent���ݹ����Ĳ���
		Bundle bundle = getIntent().getExtras();
		_currentPasteFilePath = bundle.getString("CURRENTPASTEFILEPATH");
		_action = bundle.getString("ACTION");

		_filePath = (TextView) findViewById(R.id.file_path);

		file_process = (TextView) findViewById(R.id.file_process);
		// ���¼�
		((Button) findViewById(R.id.file_createdir))
				.setOnClickListener(fun_CreateDir);
		((Button) findViewById(R.id.paste)).setOnClickListener(fun_Paste);
		((Button) findViewById(R.id.cancel)).setOnClickListener(fun_Cancel);

		// Ĭ�ϻ�ȡ��Ŀ¼���ļ��б�
		viewFiles(_rootPath);
	}

	/** �б�����¼����� **/
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		FileInfo f = _files.get(position);

		if (f.IsDirectory) {
			viewFiles(f.Path);
		}
	}

	/** �ض��巵�ؼ��¼� **/
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// ����back����
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			File f = new File(_currentPath);
			String parentPath = f.getParent();
			if (parentPath != null) {
				viewFiles(parentPath);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/** ��ȡ��Ŀ¼�������ļ� **/
	private void viewFiles(String filePath) {
		ArrayList<FileInfo> tmp = FileActivityHelper.getFiles(PasteFile.this,
				filePath);
		if (tmp != null) {
			// ������
			if (_files != null) {
				_files.clear();
			}

			_files = tmp;
			// ���õ�ǰĿ¼
			_currentPath = filePath;
			_filePath.setText(filePath);
			// �����
			setListAdapter(new FileAdapter(this, _files));
		}
	}

	/** �����ļ��лص�ί�� **/
	private final Handler createDirHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0)
				viewFiles(_currentPath);
		}
	};

	private Button.OnClickListener fun_CreateDir = new Button.OnClickListener() {
		public void onClick(View v) {
			FileActivityHelper.createDir(PasteFile.this, _currentPath,
					createDirHandler);
		}
	};

	/**
	 * ��Handler������UI
	 */
	private final Handler progressHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// �ر�ProgressDialog
			progressDialog.dismiss();

			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putString("CURRENTPATH", _currentPath);
			intent.putExtras(bundle);
			setResult(Activity.RESULT_OK, intent);

			finish();
		}
	};

	private Button.OnClickListener fun_Paste = new Button.OnClickListener() {
		public void onClick(View v) {

			final File src = new File(_currentPasteFilePath);
			if (!src.exists()) {
				Toast.makeText(getApplicationContext(),
						R.string.file_notexists, Toast.LENGTH_SHORT).show();
				return;
			}
			String newPath = FileUtil.combinPath(_currentPath, src.getName());
			final File tar = new File(newPath);
			if (tar.exists()) {
				Toast.makeText(getApplicationContext(), R.string.file_exists,
						Toast.LENGTH_SHORT).show();
				return;
			}

//			progressDialog = ProgressDialog.show(PasteFile.this, "",
//					"Please wait...", true, false);

			MyliterAsyncTask mt = new MyliterAsyncTask(file_process, src, tar);
			
			mt.execute("6666");
//			new Thread() {
//				@Override
//				public void run() {
//					if ("MOVE".equals(_action)) { // �ƶ��ļ�
//						try {
//							FileUtil.moveFile(src, tar);
//						} catch (Exception ex) {
//							Log.e(TAG, getString(R.string.file_move_fail), ex);
//							Toast.makeText(getApplicationContext(),
//									ex.getMessage(), Toast.LENGTH_SHORT).show();
//						}
//					} else { // �����ļ�
//						try {
//							FileUtil.copyFile(src, tar);
//						} catch (Exception ex) {
//							Log.e(TAG, getString(R.string.file_copy_fail), ex);
//							Toast.makeText(getApplicationContext(),
//									ex.getMessage(), Toast.LENGTH_SHORT).show();
//						}
//					}
//
//					progressHandler.sendEmptyMessage(0);
//				}
//			}.start();
		}
	};

	private Button.OnClickListener fun_Cancel = new Button.OnClickListener() {
		public void onClick(View v) {
			setResult(Activity.RESULT_CANCELED);
			finish();
		}
	};

	class MyliterAsyncTask extends AsyncTask<String, Integer, String> implements FileUtil.RefListener {

		private TextView  processTv;
		private File src;
		private File tar;
		
		public MyliterAsyncTask(TextView  tv, File f1, File f2){
			this.processTv = tv;
			this.src = f1;
			this.tar = f2;
		}
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			processTv.setText("onPreExecute!");
		}
		
		@Override
		protected  String doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			FileUtil.setRefListener(this);
			
			if ("MOVE".equals(_action)) { // �ƶ��ļ�
			try {
				FileUtil.moveFile(src, tar);
			} catch (Exception ex) {
				Log.e(TAG, getString(R.string.file_move_fail), ex);
				Toast.makeText(getApplicationContext(),
						ex.getMessage(), Toast.LENGTH_SHORT).show();
			}
		} else { // �����ļ�
			try {
				FileUtil.copyFile(src, tar);
			} catch (Exception ex) {
				Log.e(TAG, getString(R.string.file_copy_fail), ex);
				Toast.makeText(getApplicationContext(),
						ex.getMessage(), Toast.LENGTH_SHORT).show();
			}
		}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			processTv.setText("AsyncTaskDone!");
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			processTv.setText(""+values[0]);
		}

		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
			super.onCancelled();
		}

		@Override
		public void publishProcess(int data) {
			// TODO Auto-generated method stub
			publishProgress(data);
		}



	}
}
