package com.android.flyaudioui.object;

import android.util.Log;

public class Element {  
    private String id;  
    private String type;  
    private String value;  
    
    public String getId() {  
    	Log.d("oo", "-----------id--------------"+id);
        return id;  
    }  
    public void setId(String id) {  
    	Log.d("oo", "------------id-------------"+id);
        this.id = id;  
    }  
    public String getType() {  
    	Log.d("oo", "-----------Type--------------"+type);
        return type;  
    }  
    public void setType(String type) {  
    	Log.d("oo", "----------Type---------------"+type);
        this.type = type;  
    }  
    public String getValue() {  
    	Log.d("oo", "----------Value---------------"+value);
        return value;  
    }  
    public void setValue(String value) {  
    	Log.d("oo", "----------Value---------------"+value);
        this.value = value;  
    }  
}  
