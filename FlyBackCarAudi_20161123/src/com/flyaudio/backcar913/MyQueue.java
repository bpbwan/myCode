package com.flyaudio.backcar913;

import java.util.Queue;
import java.util.LinkedList;
import android.util.Log;

public class MyQueue {
	int QUEUESIZE = 2;
	Queue<String> queue = null;
	private String tagname = null;

	String TAG = "BackCarQueue";

	public MyQueue(String tag) {
		tagname = tag;
		queue = new LinkedList<String>();
	}

	public void setQueueSize(int size) {

		QUEUESIZE = size;

	}

	public String QueueRef() {
		String ret = null;
		synchronized (this) {

			if (queue.size() != 0)
				ret = queue.poll();
		}
		return ret;
	}

	public void OfferQueue(String data) {
		// Log.d(TAG, "data "+data);
		if (queue.size() >= QUEUESIZE)
			QueueRef();
		queue.offer(data);
	}
}
