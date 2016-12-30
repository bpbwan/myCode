package cn.flyaudio.android.menubar;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ShortcutXMLParser extends DefaultHandler
{
	
	private List<ShortcutEntity> list ;
	private ShortcutEntity entity ;
	
    @Override
    public void startDocument() throws SAXException {
    	// TODO Auto-generated method stub
    	list = new ArrayList<ShortcutEntity>();
    }
    
    public List<ShortcutEntity> getData()
	{
		return list;
	}
    
    @Override
    public void startElement(String uri, String localName, String qName,
    		Attributes attributes) throws SAXException {
    	// TODO Auto-generated method stub
    	if(qName.equals("shortcut"))
		{
    		entity = new ShortcutEntity();
			
    		entity.setShortcutIcon(attributes.getValue("shortcuticon"));
    		entity.setPackageName(attributes.getValue("packagename"));
    		entity.setClassName(attributes.getValue("classname"));
    		entity.setShortcutname(attributes.getValue("shortcutname"));
//    		entity.setScreen(attributes.getValue("screen"));
//    		entity.setX(attributes.getValue("x"));
//    		entity.setY(attributes.getValue("y"));
	
    		list.add(entity);
			//Log.i("msg", boxButtonEntity.getM_Text()+"---"+boxButtonEntity.getLayout_marginBottom());
		}

    	super.startElement(uri, localName, qName, attributes);
    }

}
