/**
 * 
 */
package com.android.FileBrowser;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author zhuch
 * 
 */
public class FileAdapter extends BaseAdapter {

	private LayoutInflater _inflater;
	private List<FileInfo> _files;
	String TAG = "FileAdapter";
	public FileAdapter(Context context, List<FileInfo> files) {
		_files = files;
		_inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		Log.d(TAG, "getCount  "+ _files.size());
		// TODO Auto-generated method stub
		return _files.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		Log.d(TAG, "getItem  "+ position);
		return _files.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		Log.d(TAG, "getItemId  "+ position);
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		Log.d(TAG, "getView  "+ position+"  "+convertView);
		if (convertView == null) { // convertView �����ã������Ϊnull��ִ�г�ʼ������
			// ����xml�ļ�ΪView
			convertView = _inflater.inflate(R.layout.file_item, null);
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.file_name);
			holder.icon = (ImageView) convertView.findViewById(R.id.file_icon);
			holder.Position = (TextView)convertView.findViewById(R.id.file_position);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// ����View��Ϣ
		FileInfo f = _files.get(position);
		holder.name.setText(f.Name);
		holder.icon.setImageResource(f.getIconResourceId());
		holder.Position.setText(""+holder.name.length());
		return convertView;
	}

	/* class ViewHolder */
	public class ViewHolder {
		TextView name;
		ImageView icon;
		TextView Position;
	}
}
