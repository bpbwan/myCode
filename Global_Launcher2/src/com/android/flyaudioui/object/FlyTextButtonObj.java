package com.android.flyaudioui.object;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import cn.flyaudio.android.menubar.ButtonEntity;

import com.android.launcher.R;
import com.android.launcher.R.drawable;
import com.android.launcher2.Launcher;


public class FlyTextButtonObj extends FlyButtonObj implements OnLongClickListener{
       
    private int m_fontSize = 0;
    protected String m_text = null;
    private int m_fontColor = 0xFF585858;
    private int m_offsetx = 0;
    private int m_offsety = 0;
    private int m_fontstyle = 0;
    private int m_textAlign = 0; 
    private int btnTextLength = 0;
    
    private int upColor = 0x000000;
    private int downColor = 0x9D9D9D;
    private int mSwitchColor = 0;
    private boolean mTouchable = true;
    
    public boolean isHotseat=false;
    public ButtonEntity entity=null;
    
    
    
    public FlyTextButtonObj(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
//        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.FlyTextButtonObj);
//        upColor = a.getColor(R.styleable.FlyTextButtonObj_upColor, 0xFF000000);
//        downColor = a.getColor(R.styleable.FlyTextButtonObj_downColor, 0xFFFFFFFF);
//        
//        m_fontSize = a.getInteger(R.styleable.FlyTextButtonObj_m_FontSize, 0);
//        m_text = a.getString(R.styleable.FlyTextButtonObj_m_Text);
//        m_offsetx = a.getInteger(R.styleable.FlyTextButtonObj_m_Offsetx, 0);
//        m_offsety = a.getInteger(R.styleable.FlyTextButtonObj_m_Offsety, 0);
//        m_fontstyle = a.getInteger(R.styleable.FlyTextButtonObj_m_Fontstyle, 0);
//        m_textAlign = a.getInteger(R.styleable.FlyTextButtonObj_m_TextAlign, 0);
//        btnTextLength = a.getInteger(R.styleable.FlyTextButtonObj_btnTextLength, 0);
//        
//        a.recycle();
        
        upColor=Launcher.flyLauncher.getColorFromSkin(Launcher.skinContext, "menubar_button_upColor");
        downColor=Launcher.flyLauncher.getColorFromSkin(Launcher.skinContext, "menubar_button_downColor");
        m_fontSize=Launcher.flyLauncher.getIntegerFromSkin(Launcher.skinContext, "menubar_button_m_fontSize");
        m_offsetx=Launcher.flyLauncher.getIntegerFromSkin(Launcher.skinContext, "menubar_button_m_offsetx");
        m_offsety=Launcher.flyLauncher.getIntegerFromSkin(Launcher.skinContext, "menubar_button_m_offsety");
        m_fontstyle=Launcher.flyLauncher.getIntegerFromSkin(Launcher.skinContext, "menubar_button_m_fontstyle");
        m_textAlign=Launcher.flyLauncher.getIntegerFromSkin(Launcher.skinContext, "menubar_button_m_textAlign");
        btnTextLength=Launcher.flyLauncher.getIntegerFromSkin(Launcher.skinContext, "menubar_button_btnTextLength");
        
    }
    
    public void setM_offsetx(int param)
    {
    	m_offsetx=m_offsetx+param;
    	invalidate();
    }
    
    public FlyTextButtonObj(Context context,ButtonEntity entity)
    {
        super(context);
        // TODO Auto-generated constructor stub
        this.entity=entity;
        if(!entity.getM_Text().equals("")&&entity.getM_Text().subSequence(0, 1).equals("@"))
        {
            m_text=Launcher.flyLauncher.getStringFromSkin(Launcher.skinContext,
            		entity.getM_Text().substring(8));
            
        }
        else
        {
            m_text=entity.getM_Text();
        }
        upColor=Launcher.flyLauncher.getColorFromSkin(Launcher.skinContext, "box_button_upColor");
        downColor=Launcher.flyLauncher.getColorFromSkin(Launcher.skinContext, "box_button_downColor");
        m_fontSize=Launcher.flyLauncher.getIntegerFromSkin(Launcher.skinContext, "box_button_m_fontSize");
        m_offsetx=Launcher.flyLauncher.getIntegerFromSkin(Launcher.skinContext, "box_button_m_offsetx");
        m_offsety=Launcher.flyLauncher.getIntegerFromSkin(Launcher.skinContext, "box_button_m_offsety");
        m_fontstyle=Launcher.flyLauncher.getIntegerFromSkin(Launcher.skinContext, "box_button_m_fontstyle");
        m_textAlign=Launcher.flyLauncher.getIntegerFromSkin(Launcher.skinContext, "box_button_m_textAlign");
        btnTextLength=Launcher.flyLauncher.getIntegerFromSkin(Launcher.skinContext, "box_button_btnTextLength");
       
        
     
        this.setTag(entity.getTag());
        toControlID(entity.getTag());
//        Drawable  drawble=Launcher.flyLauncher.getDrawableFromSkin(Launcher.skinContext, 
//        		entity.getBackground().substring(10));
//        
        
        Drawable  drawble;
        try {
        	 drawble=Launcher.flyLauncher.getDrawableFromSkin(Launcher.skinContext, 
             		entity.getBackground().substring(10)+"_box");
		} catch (Exception e) {
			// TODO: handle exception
			 drawble=Launcher.flyLauncher.getDrawableFromSkin(Launcher.skinContext, 
	             		entity.getBackground().substring(10));
		}
       
        this.setBackgroundDrawable(drawble);
        
    }
    
    public  void toControlID(String tag)
    {
        if(tag != null&&!tag.equals(""))
        {
            mControlID = Integer.parseInt(tag, 16);
            tag = null;
        }
    }
    
    public  void setM_text(String text) 
    {
        m_text = text;
        
    }
    
    
    public String getButtonName()
    {
    	return  m_text;
    }
    
    public int  getIconId()
    {
    	return Launcher.flyLauncher.getIdFromSkin(Launcher.skinContext, 
    			entity.getShortcutIcon().substring(10), "drawable");
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
    public static int getDrawableId(String packageName, String name) 
    {
           return getResourseIdByName(packageName, "drawable", name);
       }
    public static int getStringId(String packageName, String name) 
    {
           return getResourseIdByName(packageName, "string", name);
       }

    @Override
    public void setBooleanData(boolean data) {
        // TODO Auto-generated method stub
          if(data){
              getBackground().setLevel(1);
              mSwitchColor = 1;
              setTextColor(downColor);
          }else{
              getBackground().setLevel(0);
              mSwitchColor = 0;
              setTextColor(upColor);
          }
    }

    @Override
    public void setIntegerData(int data) {
        // TODO Auto-generated method stub
        if(data == 2)
            mTouchable = false;
        getBackground().setLevel(data);
        mSwitchColor = data;
    }

    @Override
    public void setStringData(String data) {
        // TODO Auto-generated method stub
        m_text = data;
        invalidate();
    }
      
    @Override
    public void setCommand(byte[] command)
    {
        // TODO Auto-generated method stub
        super.setCommand(command);
        switch (command[0])
        {
        case 0x30:
        case (byte)0xC0:
//          int StringID =(int) (command[1]&0xff<<24)|(command[2]&0xff<<16)|(command[3]&0xff<<8)|(command[4]&0xff);
//          LanguageString language= new LanguageString();
//          m_text = language.GetStringByID(StringID);
//          invalidate();           
            break;
        default:
            break;
        }
    }
    
    public boolean onLongClick(View v) {
        // TODO Auto-generated method stub
        byte []paramBuf =new byte[1];
        paramBuf[0] =0x00;
        Log.i("@@@@@", getButtonName()+": "+mControlID);
        MakeAndSendMessageToModule(mControlID, UIAction.UIACTION_MOUSETIMER, paramBuf);
        return true;
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        if(m_bTouchable == false){
            return true;
        }
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            if (m_bActionUI) {
                mSwitchColor = 1;
                if(mTouchable)
                    setTextColor(downColor);
            }
            break;
        case MotionEvent.ACTION_UP:
            if (m_bActionUI) {
                mSwitchColor = 0;
                if(mTouchable)
                    setTextColor(upColor);
            }
            
            break;
        default:
                break;
        }
//        return true;
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        if(m_text ==null)
            return;
        super.onDraw(canvas);
        Paint mPaint = new Paint();
        switch (m_fontstyle) {
            case 0:
                mPaint.setTypeface(Typeface.DEFAULT);
                break;
            case 1:
                mPaint.setTypeface(Typeface.DEFAULT_BOLD);
                break;
        }
        switch (m_textAlign) {
            case 0:
                mPaint.setTextAlign(Paint.Align.LEFT);
                break;
            case 1:
                mPaint.setTextAlign(Paint.Align.RIGHT);
                break;
            case 2:
                mPaint.setTextAlign(Paint.Align.CENTER);
                break;
        }
        mPaint.setTextSize(m_fontSize);
        mPaint.setStyle(Style.FILL); 
        mPaint.setAntiAlias(true);
        if(mSwitchColor == 0)
            mPaint.setColor(upColor);
        else if(mSwitchColor == 1)
            mPaint.setColor(downColor);
        else if(mSwitchColor == 2)
            mPaint.setColor(m_fontColor);
     
           if(m_text!=null&&!m_text.equals("")&&m_text.length() > btnTextLength){
               float y = mPaint.getTextSize();
               String[] texts = m_text.split(" ");
                canvas.drawText(texts[0] , m_offsetx, m_offsety - m_fontSize/2, mPaint);  
              if(texts[1] !=null)
                  canvas.drawText(texts[1] , m_offsetx, m_offsety+y - m_fontSize/2 , mPaint);  
           }else{
        		   canvas.drawText(m_text , m_offsetx, m_offsety, mPaint); 
        		
                
           }
    }
    
    private boolean isShowing=false;
    public void setBoxTextColor(int color)
    {
    	m_fontColor=color;
    	if(isShowing)
    	{
    		mSwitchColor = 0;
    		isShowing=false;
    	}
    	else
    	{
    		mSwitchColor = 1;
    		isShowing=true;
    	}
    	
    	Log.i("DDDT", "setBoxTextColor");
    	invalidate();
    }
    
    //===========================================================================
    private  void setTextColor(int color) {
        // TODO Auto-generated method stub
        if(mTouchable)
        {
        	   m_fontColor = 0xFF585858;
        	  
        }
        else
        {
        	m_fontColor = color;
           
        }
        invalidate();
    }
}
