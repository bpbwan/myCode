package com.android.FileBrowser;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import com.android.FileBrowser.FileAdapter.ViewHolder;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.UserHandle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.view.KeyEvent;

public class Main extends ListActivity {
	private TextView _filePath;
	private List<FileInfo> _files;
	private String _rootPath = FileUtil.getSDPath();
	private String _currentPath = _rootPath;
	private final String TAG = "Main";
	private final int MENU_RENAME = Menu.FIRST;
	private final int MENU_COPY = Menu.FIRST + 3;
	private final int MENU_MOVE = Menu.FIRST + 4;
	private final int MENU_DELETE = Menu.FIRST + 5;
	private final int MENU_INFO = Menu.FIRST + 6;
	private final int MENU_SETWALLPAPER = Menu.FIRST + 7;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		_filePath = (TextView) findViewById(R.id.file_path);

		// ��ȡ��ǰĿ¼���ļ��б�
		viewFiles(_currentPath);

		// �󶨳����¼�
		// getListView().setOnItemLongClickListener(_onItemLongClickListener);

		// ע�������Ĳ˵�
		registerForContextMenu(getListView());
	}

	/** �����Ĳ˵� **/
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		AdapterView.AdapterContextMenuInfo info = null;

		try {
			info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		} catch (ClassCastException e) {
			Log.e(TAG, "bad menuInfo", e);
			return;
		}

		FileInfo f = _files.get(info.position);
		menu.setHeaderTitle(f.Name);
		menu.add(0, MENU_RENAME, 1, getString(R.string.file_rename));
		menu.add(0, MENU_COPY, 2, getString(R.string.file_copy));
		menu.add(0, MENU_MOVE, 3, getString(R.string.file_move));
		menu.add(0, MENU_DELETE, 4, getString(R.string.file_delete));
		menu.add(0, MENU_INFO, 5, getString(R.string.file_info));
		menu.add(0, MENU_SETWALLPAPER, 6, getString(R.string.set_wallpaper));
	}

	/** �����Ĳ˵��¼����� **/
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		FileInfo fileInfo = _files.get(info.position);
		File f = new File(fileInfo.Path);
		switch (item.getItemId()) {
		case MENU_RENAME:
			FileActivityHelper.renameFile(Main.this, f, renameFileHandler);
			return true;
		case MENU_COPY:
			pasteFile(f.getPath(), "COPY");
			return true;
		case MENU_MOVE:
			pasteFile(f.getPath(), "MOVE");
			return true;
		case MENU_DELETE:
			FileUtil.deleteFile(f);
			viewFiles(_currentPath);
			return true;
		case MENU_INFO:
			FileActivityHelper.viewFileInfo(Main.this, f);
			return true;
		case MENU_SETWALLPAPER:
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	/** �б�����¼����� **/
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		FileInfo f = _files.get(position);
		ViewHolder h = (ViewHolder)v.getTag();
		if(h != null)
		Log.d(TAG, "onListItemClick  "+v.getTag()+" "+h.name.getText());
		
		if (f.IsDirectory) {
			viewFiles(f.Path);
		} else {
			
			//IntentFactory.IntentTest(Main.this, "goolgle_search", null);
			
			
//			IntentFactory.IntentTest(Main.this, "home", null);
			
			Bundle msg = new Bundle();
//			msg.putString("web", "http://www.baidu.com");
//			IntentFactory.IntentTest(Main.this, "web", msg);
			

//			msg.putString("call", "tel:10086");
//			IntentFactory.IntentTest(Main.this, "call", msg);
			
//			IntentFactory.IntentTest(Main.this, "capture", msg);
			
//		    IntentFactory.IntentTest(Main.this, "imagecrop", msg);
			
//			IntentFactory.IntentTest(Main.this, "wireless", msg);
			
	//		IntentFactory.IntentTest(Main.this, "wifisetting", msg);
			
	//		IntentFactory.IntentTest(Main.this, "bluetooth", msg);
			
	//		IntentFactory.IntentTest(Main.this, "local_setting", msg);
			
	//		IntentFactory.IntentTest(Main.this, "record", msg);
			
//			setWallPaper(f.Path);
//			Main.this.finish();
			
		switch((int)id){
		case 1:
			msg.putString("data", "com.android.Service.MYSERVICE");
			IntentFactory.IntentTest(Main.this, "service", msg);
			break;
		case 2:
			msg.putString("data", "com.android.Service.SECONDSERVICE");
			IntentFactory.IntentTest(Main.this, "service", msg);
			break;
		}
			//openFile(f.Path);
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
			} else {
				exit();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/** ��ȡ��PasteFile���ݹ�����·�� **/
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (Activity.RESULT_OK == resultCode) {
			Bundle bundle = data.getExtras();
			if (bundle != null && bundle.containsKey("CURRENTPATH")) {
				viewFiles(bundle.getString("CURRENTPATH"));
			}
		}
		Log.d("AAAA", "onActivityResult resultCode  "+resultCode+"    requestCode "+requestCode);
	}

	/** �����˵� **/
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = this.getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	/** �˵��¼� **/
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.mainmenu_home:
			viewFiles(_rootPath);
			break;
		case R.id.mainmenu_refresh:
			viewFiles(_currentPath);
			break;
		case R.id.mainmenu_createdir:
			FileActivityHelper.createDir(Main.this, _currentPath, createDirHandler);
			break;
		case R.id.mainmenu_exit:
			exit();
			break;
		default:
			break;
		}
		return true;
	}

	/** ��ȡ��Ŀ¼�������ļ� **/
	private void viewFiles(String filePath) {
		ArrayList<FileInfo> tmp = FileActivityHelper.getFiles(Main.this, filePath);
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

	/** �����¼����� **/
	/**
	 * private OnItemLongClickListener _onItemLongClickListener = new
	 * OnItemLongClickListener() {
	 * 
	 * @Override public boolean onItemLongClick(AdapterView<?> parent, View
	 *           view, int position, long id) { Log.e(TAG, "position:" +
	 *           position); return true; } };
	 **/

	/** ���ļ� **/
	private void openFile(String path) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);

		File f = new File(path);
		Log.d(TAG, " "+path+"  "+f.getName());
		String type = FileUtil.getMIMEType(f.getName());
		intent.setDataAndType(Uri.fromFile(f), type);

		startActivity(intent);
	}

	
	private void setWallPaper(String path) {
		File f = new File(path);
		String type = FileUtil.getMIMEType(f.getName());
		Log.d(TAG, "type  "+type);
	if(type.equals("image/*")){
		Intent intent = new Intent();
		Log.d(TAG, "setWallPaper");

		intent.putExtra("data",path);
		intent.setAction("com.android.intent.action.SETWALLPAPER");
		Main.this.sendBroadcast(intent);
	}
	}
	
	/** ������ص�ί�� **/
	private final Handler renameFileHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0)
				viewFiles(_currentPath);
		}
	};

	/** �����ļ��лص�ί�� **/
	private final Handler createDirHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0)
				viewFiles(_currentPath);
		}
	};

	/** ճ���ļ� **/
	private void pasteFile(String path, String action) {
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putString("CURRENTPASTEFILEPATH", path);
		bundle.putString("ACTION", action);
		intent.putExtras(bundle);
		intent.setClass(Main.this, PasteFile.class);
		// ��һ��Activity���ȴ���
		startActivityForResult(intent, 0);
	}

	/** �˳����� **/
	private void exit() {

		new AlertDialog.Builder(Main.this).setMessage(R.string.confirm_exit).setCancelable(false)
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Main.this.finish();
						android.os.Process.killProcess(android.os.Process.myPid());
						System.exit(0);
					}
				}).setNegativeButton("No", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				}).show();
	}
	
	

}
