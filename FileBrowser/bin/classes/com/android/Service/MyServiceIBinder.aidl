package com.android.Service;

import com.android.Service.IMsg;
import android.os.Messenger;


interface MyServiceIBinder {
	    int getPid();

	    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
	            double aDouble, String aString);

		void setMessage(in Messenger msg);
	}