package com.flyaudio.xml;

import java.util.HashMap;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.content.Context;
import android.util.Log;
import com.flyaudio.data.BcData;

public class TrackParamXMLParaser extends DefaultHandler {

	String TAG = "BackCarTrackXML";
	private int mm = -1;
	private int carType;
	public final int BcDataNum = 4;
	public final int BcDataMaxIndex = BcDataNum;
	BcData bcdata[] = new BcData[4];

	public TrackParamXMLParaser(int cartype) {
		carType = cartype;
	}

	public void getBcData(BcData[] bc) {
		for (int i = 0; i < BcDataMaxIndex; i++)
			bc[i] = bcdata[i];
	}

	@Override
	public void startDocument() throws SAXException {
		// TODO Auto-generated method stub
		mm = -1;
		Log.d(TAG, "startDocument");
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (qName.equals(TrackItems.ENDTAG)) {
			// if(bcdata[mm].carID !=carType)
			// mm--;
			// else
			Log.d(TAG, "find " + carType + " track");

		}
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		// TODO Auto-generated method stub
		String tagName = attributes.getValue("name");
		if (qName.equals(TrackItems.ENDTAG)) {
			bcdata[++mm] = new BcData();
		}

		Log.d(TAG, " " + attributes.getValue("value"));
		if (mm < 0 || mm > BcDataMaxIndex || bcdata[mm] == null)
			return;

		if (qName.equals("int")) {
			final int Ivalue = Integer.parseInt(attributes.getValue("value"));
			if (tagName.equals(TrackItems.ID)) {

				bcdata[mm].carID = Ivalue;
			} else if (tagName.equals(TrackItems.ATSAFE)) {
				bcdata[mm].atsafe = Ivalue;

			} else if (tagName.equals(TrackItems.DOT)) {
				bcdata[mm].dotNum = Ivalue;

			} else if (tagName.equals(TrackItems.HIGH)) {
				bcdata[mm].high = Ivalue;

			} else if (tagName.equals(TrackItems.VISION)) {
				bcdata[mm].Vision = Ivalue;

			} else if (tagName.equals(TrackItems.MINY)) {
				bcdata[mm].mMinY = Ivalue;

			} else if (tagName.equals(TrackItems.MAXY)) {
				bcdata[mm].mMaxY = Ivalue;

			} else if (tagName.equals(TrackItems.NUMF)) {
				bcdata[mm].mNumF = Ivalue;
			} else if (tagName.equals(TrackItems.MidLineY1)) {
				bcdata[mm].midLineY1 = Ivalue;
			} else if (tagName.equals(TrackItems.MidLineY1)) {
				bcdata[mm].midLineY1 = Ivalue;
			} else if (tagName.equals(TrackItems.MidLineY2)) {
				bcdata[mm].midLineY2 = Ivalue;
			} else if (tagName.equals(TrackItems.MidLineY3)) {
				bcdata[mm].midLineY3 = Ivalue;
			} else if (tagName.equals(TrackItems.TRACKMODE)) {
				bcdata[mm].TrackMode = Ivalue;
			}

		} else if (qName.equals("float")) {
			final float Fvalue = Float.parseFloat(attributes.getValue("value"));
			if (tagName.equals(TrackItems.LPU)) {
				bcdata[mm].LPu = Fvalue;

			} else if (tagName.equals(TrackItems.RPU)) {
				bcdata[mm].RPu = Fvalue;

			}
		} else if (qName.equals("double")) {
			final double Dvalue = Double.parseDouble(attributes
					.getValue("value"));
			if (tagName.equals(TrackItems.PU)) {
				bcdata[mm].Pu = Dvalue;

			} else if (tagName.equals(TrackItems.PV)) {
				bcdata[mm].Pv = Dvalue;

			} else if (tagName.equals(TrackItems.LC)) {
				bcdata[mm].Lcar = Dvalue;

			} else if (tagName.equals(TrackItems.WC)) {
				bcdata[mm].Wcar = Dvalue;

			} else if (tagName.equals(TrackItems.X_A)) {
				bcdata[mm].x_angle = Dvalue;

			} else if (tagName.equals(TrackItems.Y_A)) {
				bcdata[mm].y_angle = Dvalue;

			} else if (tagName.equals(TrackItems.SMAX)) {
				bcdata[mm].Smax = Dvalue;

			} else if (tagName.equals(TrackItems.LCL)) {
				bcdata[mm].LcL = Dvalue;

			} else if (tagName.equals(TrackItems.LCR)) {
				bcdata[mm].LcR = Dvalue;

			}
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
