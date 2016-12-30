package com.android.FileBrowser;

import java.util.Comparator;

public class FileNumCountComparator implements Comparator<FileInfo>{

	@Override
	public int compare(FileInfo fo1, FileInfo fo2) {
		// TODO Auto-generated method stub
		

		return fo1.Name.length() - fo2.Name.length();
	}

}
