package com.android.launcher2.util;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

public class FavoritesXMLParser extends DefaultHandler {

    private static final String TAG = "FlyFavorites";
    private boolean DEBUG = false;

    private ArrayList<FavoritesEntity> dataList;
    private FavoritesEntity mFavoritesEntity;

    public ArrayList<FavoritesEntity> getData() {
        return dataList;
    }

    @Override
    public void startDocument() throws SAXException {
        // TODO Auto-generated method stub
        dataList = new ArrayList<FavoritesEntity>();
    }

    @Override
    public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException {
        // TODO Auto-generated method stub
        if (!qName.equals(FavoritesConstant.TAG_FAVORITES)) {
            mFavoritesEntity = new FavoritesEntity();

            mFavoritesEntity.setType(qName);

            mFavoritesEntity.setPackageName(attributes
                    .getValue(FavoritesConstant.PACKAGE_NAME));
            mFavoritesEntity.setClassName(attributes
                    .getValue(FavoritesConstant.CLASS_NAME));
            mFavoritesEntity.setMove(getString(
                    attributes.getValue(FavoritesConstant.MOVE), "1"));
            mFavoritesEntity.setScreen(attributes
                    .getValue(FavoritesConstant.SCREEN));
            mFavoritesEntity.setX(attributes.getValue(FavoritesConstant.X));
            mFavoritesEntity.setY(attributes.getValue(FavoritesConstant.Y));
            mFavoritesEntity.setSpanX(attributes
                    .getValue(FavoritesConstant.SPAN_X));
            mFavoritesEntity.setSpanY(attributes
                    .getValue(FavoritesConstant.SPAN_Y));
            mFavoritesEntity.setIcon(getString(
                    attributes.getValue(FavoritesConstant.ICON), "0"));
            mFavoritesEntity.setTitle(getString(
                    attributes.getValue(FavoritesConstant.TITLE), "0"));
            mFavoritesEntity.setUri(attributes.getValue(FavoritesConstant.URI));
            mFavoritesEntity.setDelete(attributes
                    .getValue(FavoritesConstant.DELETE));
            if (DEBUG) {
                Log.d(TAG, mFavoritesEntity.toString());
                Log.d(TAG, "****************************");
            }

            dataList.add(mFavoritesEntity);
        }
    }

    private String getString(String value, String strDefault) {
        return (("").equals(value) || value == null) ? strDefault : value;
    }
}
