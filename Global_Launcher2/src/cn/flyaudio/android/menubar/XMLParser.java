package cn.flyaudio.android.menubar;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.android.launcher2.Launcher;
import java.lang.Integer;

import android.util.Log;


public class XMLParser extends DefaultHandler
{
	
	private ArrayList<ButtonEntity> dataList;
	private ButtonEntity ButtonEntity;
	//private StringBuffer buffer=new StringBuffer();
	
	public ArrayList<ButtonEntity> getData()
	{
		return dataList;
	}
	@Override
	public void startDocument() throws SAXException 
	{
		// TODO Auto-generated method stub
		dataList = new ArrayList<ButtonEntity>();
	}
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException 
			{
		// TODO Auto-generated method stub
		if(qName.equals(MenuBarConstent.SHORTCUTS))
		{
			ButtonEntity = new ButtonEntity();
			
			ButtonEntity.setTag(attributes.getValue(MenuBarConstent.MENUBAR_TAG));
			ButtonEntity.setBackground(attributes.getValue(MenuBarConstent.MENUBAR_BACKGROUND));
			ButtonEntity.setIsMenu(attributes.getValue(MenuBarConstent.MENUBAR_ISMENU));
			ButtonEntity.setM_Text(attributes.getValue(MenuBarConstent.MENUBAR_M_TEXT));
			
			
			ButtonEntity.setShortcutIcon(attributes.getValue(MenuBarConstent.MENUBAR_SHORTCUTICON));
			ButtonEntity.setPackageName(attributes.getValue(MenuBarConstent.MENUBAR_PACKAGENAME));
			ButtonEntity.setClassName(attributes.getValue(MenuBarConstent.MENUBAR_CLASSNAME));
			
			ButtonEntity.setScreen(attributes.getValue(MenuBarConstent.MENUBAR_SCREEN));
			ButtonEntity.setX(attributes.getValue(MenuBarConstent.MENUBAR_X));
			ButtonEntity.setY(attributes.getValue(MenuBarConstent.MENUBAR_Y));
	
			dataList.add(ButtonEntity);
			//Log.i("msg", boxButtonEntity.getM_Text()+"---"+boxButtonEntity.getLayout_marginBottom());
		}
		else if(qName.equals(MenuBarConstent.SHORTCUTSCOUNT))
		{
			Launcher.menuBarButtonCount=Integer.valueOf(attributes.getValue(MenuBarConstent.BUTTON_COUNT));
		}
		super.startElement(uri, localName, qName, attributes);
	}
}
