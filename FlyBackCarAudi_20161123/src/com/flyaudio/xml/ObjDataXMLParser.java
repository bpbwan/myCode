package com.flyaudio.xml;

import java.util.HashMap;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ObjDataXMLParser extends DefaultHandler {
	private HashMap<String, Integer> objDataMap;

	public HashMap<String, Integer> getData() {
		return objDataMap;
	}

	@Override
	public void startDocument() throws SAXException {
		// TODO Auto-generated method stub
		objDataMap = new HashMap<String, Integer>();
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		// TODO Auto-generated method stub

		if (qName.equals("tag")) {
			String controlId = attributes.getValue("id");
			String type = attributes.getValue("type");
			objDataMap.put(controlId, Integer.parseInt(type));
		}
	}
}
