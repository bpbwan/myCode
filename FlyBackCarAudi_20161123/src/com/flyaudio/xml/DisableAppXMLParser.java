package com.flyaudio.xml;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

public class DisableAppXMLParser extends DefaultHandler {

	private ArrayList<String> packageNameList;
	private ArrayList<String> receiverList;

	public ArrayList<String> getPackageNameList() {
		return packageNameList;
	}

	public ArrayList<String> getReceiverList() {
		return receiverList;
	}

	@Override
	public void startDocument() throws SAXException {
		// TODO Auto-generated method stub
		Log.d("da", "startDocument");
		packageNameList = new ArrayList<String>();
		receiverList = new ArrayList<String>();
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		// TODO Auto-generated method stub
		if (qName.equals("item")) {
			String packagename = attributes.getValue("packageName");
			String receiver = attributes.getValue("receiver");
			packageNameList.add(packagename);
			receiverList.add(receiver);
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		// TODO Auto-generated method stub
	}

	@Override
	public void endDocument() throws SAXException {
		// TODO Auto-generated method stub
	}

}
