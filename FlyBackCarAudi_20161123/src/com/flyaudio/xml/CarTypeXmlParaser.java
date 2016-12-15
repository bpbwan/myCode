package com.flyaudio.xml;

import java.util.HashMap;
import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.content.Context;
import android.util.Log;

public class CarTypeXmlParaser extends DefaultHandler {

	String TAG = "BackCarTrackXML";
	private int mm = -1;
	private int carType;

	ArrayList<CarTypeEntity> g_Alist = null;
	CarTypeEntity common_Res;
	CarTypeEntity tmpEntity;

	public CarTypeXmlParaser(ArrayList<CarTypeEntity> mlist,
			CarTypeEntity mComCartypeRes) {
		g_Alist = mlist;
		common_Res = mComCartypeRes;
	}

	@Override
	public void startDocument() throws SAXException {
		// TODO Auto-generated method stub

		Log.d(TAG, "startDocument");
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		// TODO Auto-generated method stub
		String tagName = attributes.getValue("name");
		if (qName.equals(CarTypeEntity.COMCARTYPE)) {
			common_Res.setCarTypeRes(attributes.getValue("value"));
		}
		if (qName.equals(CarTypeEntity.CARTYPE)) {
			CarTypeEntity entity = new CarTypeEntity();
			g_Alist.add(entity);
			tmpEntity = entity;
		} else if (qName.equals(CarTypeEntity.CARTYPERES)) {
			tmpEntity.setCarTypeRes(attributes.getValue("value"));

		} else if (qName.equals(CarTypeEntity.CARTYPE_ENTITY)) {

		}
	}

	@Override
	public void endDocument() throws SAXException {
		// TODO Auto-generated method stub

	}

	private int HexToInteger(String strId) {
		if (!("").equals(strId) && strId != null) {
			return Integer.parseInt(strId.substring(2), 16);
		}
		return -1;
	}

}
