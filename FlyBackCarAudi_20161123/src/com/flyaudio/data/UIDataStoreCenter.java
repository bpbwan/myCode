package com.flyaudio.data;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.content.res.Resources;

import com.flyaudio.backcar.R;
import com.flyaudio.backcar.modules.BaseModule;
//import com.flyaudio.fui.SkinResource;
import com.flyaudio.globaldefine.DataObjType;
import com.flyaudio.xml.ObjDataXMLParser;
import com.flyaudio.xml.XMLTool;

public class UIDataStoreCenter {
	protected ConcurrentHashMap<Integer, DataObjBase> mMapIdValue = new ConcurrentHashMap<Integer, DataObjBase>();
	private HashMap<String, Integer> objDataMap = new HashMap<String, Integer>();
	private Context mContext;
	private Resources res;
	private int ID;

	public UIDataStoreCenter(Context context, Resources ress, int id) {
		mContext = context;
		res = ress;
		ID = id;
		loadObjDataFromXML();

	}

	public void loadObjDataFromXML() {
		// int idRes = SkinResource.getSkinResourceId("objdata", "raw",
		// FlyaudioApplication.mSkinContext);
		// Log.d("skin","loadObjDataFromXML objdata id="+idRes);
		// InputStream inStream =
		// FlyaudioApplication.mSkinContext.getResources().openRawResource(idRes);
		InputStream inStream = null;

		if (res != null && ID != 0)
			inStream = res.openRawResource(ID);
		else {
			inStream = mContext.getResources().openRawResource(R.raw.objdata);
			Log.i("BBB", "res == null");
		}
		ObjDataXMLParser parser = new ObjDataXMLParser();
		objDataMap = ((ObjDataXMLParser) XMLTool.parse(inStream, parser))
				.getData();
		// Log.d("dataxml","###objDataMap:"+objDataMap.toString());
		// Log.d("dataxml","###objDataMap.size():"+objDataMap.size());
		initDataObjMap();
	}

	private void initDataObjMap() {
		Iterator<Map.Entry<String, Integer>> iterators = objDataMap.entrySet()
				.iterator();
		DataObjBase DataObj = null;
		while (iterators.hasNext()) {
			Entry<String, Integer> entry = iterators.next();
			DataObj = getDataObjByType(entry.getValue());
			int controlID = Integer.parseInt(entry.getKey(), 16);
			if (DataObj != null) {
				mMapIdValue.put(controlID, DataObj);
			} else {
				DataObj = new DataObjBase();
				mMapIdValue.put(controlID, DataObj);
			}
		}
	}

	public DataObjBase getDataObjByType(int objType) {
		DataObjBase DataObj = null;
		switch (objType) {
		case DataObjType.TYPE_BUTTON:
			DataObj = new DataObjButton();
			break;
		/*
		 * case DataObjType.TYPE_CHECKBOX_BUTTON: DataObj = new
		 * DataObjCheckBoxButton(); break;
		 */
		case DataObjType.TYPE_ENUMGAUGE_OBJ:
			DataObj = new DataObjEnumGauge();
			break;
		/*
		 * case DataObjType.TYPE_GAUGE_OBJ: DataObj = new DataObjGauge(); break;
		 * case DataObjType.TYPE_MULTI_BUTTON_OBJ: DataObj = new
		 * DataObjMultiButton(); break; case DataObjType.TYPE_MULTI_GAUGE_OBJ:
		 * DataObj = new DataObjMultiGauge(); break;
		 */
		case DataObjType.TYPE_STATIC_BUTTON_OBJ:
			break;
		case DataObjType.TYPE_TEXT_BUTTON_OBJ:
			DataObj = new DataObjTextButton();
			break;
		/*
		 * case DataObjType.TYPE_TEXT_GAUGE_OBJ: DataObj = new
		 * DataObjTextGauge(); break;
		 */
		case DataObjType.TYPE_TEXT_OBJ:
			DataObj = new DataObjText();
			break;
		case DataObjType.TYPE_UI_OBJ:
			DataObj = new DataObjBase();
			break;
		case DataObjType.TYPE_SEEKBAR_OBJ:
			DataObj = new DataObjSeekBar();
			break;
		case DataObjType.TYPE_ROTATEUI_OBJ:
			DataObj = new DataObjRotate();
			break;
		case DataObjType.TYPE_MULTIENUMGAUGE_OBJ:
			DataObj = new DataObjMultiEnumGauge();
			break;
		}
		return DataObj;
	}

	public void handleControlUIMessage(byte[] data, int len) {
		int controlID = controlID(data);
		DataObjBase DataObj = mMapIdValue.get(controlID);
		if (DataObj != null) {
			DataObj.storeProperty(data, len);
			// Log.d("BackCar","handleControlUIMessage controlID:"+
			// String.format("%08x", controlID));
		} else {
			// Log.d("BackCardataxml","handleControlUIMessage can't find id:"+controlID);
			if(BaseModule.getBaseModule().DEBUG)
			Log.d("BackCar",
					"handleControlUIMessage can't find id:"
							+ String.format("%08x", controlID));
		}
	}

	public static String IntegerToString(int i) {
		return String.format("%08x", i);
	}

	public int controlID(byte[] data) {
		int FlyUIControlID = (int) ((data[3] & 0xFF) << 24)
				| (int) ((data[4] & 0xFF) << 16)
				| (int) ((data[5] & 0xFF) << 8) | (data[6]) & 0xFF;

		return FlyUIControlID;
	}

	public void SetFlyUIObjProperty(View view) {

		if (view == null) {
			return;
		}

		String controlID = (String) view.getTag();
		if (controlID == null)
			return;

		int controlId = Integer.parseInt(controlID, 16);
		if (!mMapIdValue.containsKey(controlId)) {
			return;
		}

		DataObjBase dataObj = mMapIdValue.get(controlId);
		if (dataObj != null) {

			dataObj.restoreProperty(view);
			// Log.d("DDD","SetFlyUIObjProperty controlID:"+controlID);
		} else {
			Log.d("BackCardataxml", "Can't find FlyDataObj by id:" + controlId);
		}
	}
}
