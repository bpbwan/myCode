package com.flyaudio.backcar.trackbitmap;

import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.LinkedList;
import java.util.ArrayList;

import com.flyaudio.backcar.modules.BaseModule;

import android.util.Log;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;

public class TrackBitmapQueue {

	int QUEUESIZE = 5;
	Queue<BitmapLock> queue = null;
	private String tagname = null;
	ArrayList<BitmapLock> arrayBitmap = null;
	private int ArrarSize = 0;
	static int curPoi = -1;
	String TAG = "TrackBitmapQueue";

	private byte[] lock = new byte[0];

	/*
	 * baipeibin
	 * 
	 * @ ---> --- 1 --> drawBitmap =====> ==== 1 ---> showbitmap . --- 2 ==== 2
	 * . --- 3 ==== 3 . --- 4 ==== 4 v --- 5 ==== 5
	 */

	public TrackBitmapQueue(String tag) {
		tagname = tag;
		queue = new LinkedList<BitmapLock>();
	}

	public void setQueueSize(int size) {

		QUEUESIZE = size;

	}

	public Bitmap QueueRef(String str) {

		synchronized (lock) {
			Bitmap ret = null;
			if(BaseModule.getBaseModule().DEBUG_L2)
				Log.d("GL", "QueueRef ");
			if (queue.size() != 0) {
				try{
				BitmapLock block = queue.poll();
				if (!block.isLock())
					ret = block.bp;

	
				TrackBitmap.notifyComsumeSync(queue.size());
				}catch(NoSuchElementException e){
					queue.clear();
					Log.d("GL", "QueueRef  "+queue.size() +" "+e);
				}
			}
			return ret;

		}
	}

	public void OfferQueue(BitmapLock data) {
		// Log.d(TAG, "data "+data);

		if (queue.size() >= QUEUESIZE)
			QueueRef("offerqueue");
		
		if(queue.size() > QUEUESIZE)
			queue.clear();
		
		queue.offer(data);

		// Log.d("DDD", "OfferQueue size "+queue.size());
	}
	public void Clear(int size){
		try{
		for(int i =0 ; i<size; i++)
			QueueRef("offerqueue");
			}catch(NoSuchElementException e){
					
				}
	}
	
	public int size(){
		return queue.size();
	}
	
	public void Clear(){
		Clear(queue.size());
	}
	
	public void LeftOne(){
		if(queue.size()>1)
		Clear(queue.size()-1);
	}
	
	public void fillBitmapLoopPool(Bitmap[] bitmaps) {
		arrayBitmap = new ArrayList<BitmapLock>();

		for (int i = 0; i < bitmaps.length; i++) {
			BitmapLock bit = new BitmapLock(bitmaps[i]);
			arrayBitmap.add(bit);

		}
		ArrarSize = arrayBitmap.size();
	}

	public BitmapLock getLoopBitmap() {
		curPoi++;
		if (curPoi >= ArrarSize)
			curPoi = 0;

		return arrayBitmap.get(curPoi);
	}
	

	
	public class BitmapLock {
		public BitmapLock(Bitmap bitmap) {
			bp = bitmap;
			isbusy = false;
		}

		Bitmap bp;
		boolean isbusy;

		public void setLock() {
			isbusy = true;
		}

		public void unLock() {
			isbusy = false;
		}

		public boolean isLock() {
			return isbusy;
		}
	}
	


}
