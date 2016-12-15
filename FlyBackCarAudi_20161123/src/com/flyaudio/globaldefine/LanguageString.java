package com.flyaudio.globaldefine;

//该类功能不完全
public class LanguageString
{
	public LanguageString()
	{
		// TODO Auto-generated constructor stub
	}
	
   public String GetStringByID(int ID)//通过ID获取字符串信息
   {
		String sText = null;
		String name=null;
		switch (ID) {
		//gps map
		case -84:
			name ="setup_gps_map_kld";
		break;

		default:
			break;
		}
		if(null!=name){
			//sText = SkinResource.getSkinStringByName(name, "string", FlyaudioApplication.mSkinContext);
			//Log.d("skin","skin string ="+sText);
			sText = name;
		}
		return sText;
	}
}
