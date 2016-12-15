package com.flyaudio.xml;

import java.util.HashMap;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import java.util.ArrayList;

public class RadarChangeMapXMLParser extends DefaultHandler {

	protected HashMap<Integer, HashMap<String, Integer>> RadarChangeMap;

	public HashMap<Integer, HashMap<String, Integer>> getRadarChangeMap() {
		return RadarChangeMap;
	}

	HashMap<String, Integer> tmp = null;

	@Override
	public void startDocument() throws SAXException {
		// TODO Auto-generated method stub
		RadarChangeMap = new HashMap<Integer, HashMap<String, Integer>>();
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		// TODO Auto-generated method stub

		if (qName.equals("angle")) {
			String value = attributes.getValue("value");
			tmp = new HashMap<String, Integer>();
			RadarChangeMap.put(Integer.parseInt(value), tmp);
		}

		if (qName.equals("multiObj")) {
			String key = attributes.getValue("key");
			String max = attributes.getValue("max");
			tmp.put(key, Integer.parseInt(max));
		}

	}
}
