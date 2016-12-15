package com.flyaudio.xml;

import java.util.HashMap;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import java.util.ArrayList;

public class PanoramaCarXMLParser extends DefaultHandler {

	private ArrayList<Integer> PanoramaCartype = new ArrayList<Integer>();
	private ArrayList<String> PanoramaCartype2 = new ArrayList<String>();

	public ArrayList<Integer> getIntCarType() {
		return PanoramaCartype;
	}

	public ArrayList<String> getStringCarType() {
		return PanoramaCartype2;
	}

	@Override
	public void startDocument() throws SAXException {
		// TODO Auto-generated method stub
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		// TODO Auto-generated method stub
		String type = attributes.getValue("cartype");

		if (qName.equals("CarTnt")) {
			PanoramaCartype.add(Integer.parseInt(type));
		}
		if (qName.equals("CarString")) {
			PanoramaCartype2.add(type);
		}
	}
}
