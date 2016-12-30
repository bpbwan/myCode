package cn.flyaudio.android.menubar;

public class ShortcutEntity
{
	private String packageName="";
	private String className="";
	private String shortcutIcon="";
//	private String screen="";
	private String shortcutname="";

//	private String x="";
//	private String y="";
	
	public int getShortcutIconId()
	{
		return getDrawableId(shortcutIcon.substring(10));
	}
	public int getShortcutnameId() 
	{	
		return getStringId(shortcutname.substring(8));
	}
	public void setShortcutname(String shortcutname) {
		this.shortcutname = shortcutname;
	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getShortcutIcon() {
		return shortcutIcon;
	}
	public void setShortcutIcon(String shortcutIcon) {
		this.shortcutIcon = shortcutIcon;
	}

	
	
	 private static int getResourseIdByName(String packageName, String className, String name) 
	    {
	           int id = 0;
	           try {
	               Class desireClass = Class.forName(packageName + ".R$" + className);
	               if (desireClass != null)
	                   id = desireClass.getField(name).getInt(desireClass);
	           } catch (ClassNotFoundException e) {
	               e.printStackTrace();
	           } catch (IllegalArgumentException e) {
	               e.printStackTrace();
	           } catch (SecurityException e) {
	               e.printStackTrace();
	           } catch (IllegalAccessException e) {
	               e.printStackTrace();
	           } catch (NoSuchFieldException e) {
	               e.printStackTrace();
	           }
	           return id;
	       }
	    public static int getDrawableId( String name) 
	    {
	           return getResourseIdByName("com.android.launcher", "drawable", name);
	       }
	    public static int getStringId( String name) 
	    {
	           return getResourseIdByName("com.android.launcher", "string", name);
	       }

}
