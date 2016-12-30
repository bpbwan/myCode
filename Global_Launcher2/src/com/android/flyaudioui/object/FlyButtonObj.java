package com.android.flyaudioui.object;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import com.android.launcher.R;
import com.android.launcher2.Launcher;

public class FlyButtonObj extends FlyUIObj {

    protected boolean m_bActionUI = true;
    public boolean m_bTouchable = true;
    protected boolean m_bDownEvent = false;
    protected boolean m_bUpEvent = true;
    private String TAG = "FlyButtonObj";
    protected boolean m_bTimer = false;
    protected int m_timerDelay = 300;
    protected int m_ExTimerDelay = 0;
    protected int m_timerTimes = 0;

    protected int m_curTimerTimes = 0;// 用于定时器次数计数

    public boolean GetActionUI() {
        return m_bActionUI;
    }

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        public void run() {
            // TODO Auto-generated method stub
            if (m_timerTimes == 0)// 一直循环
            {
                handler.postDelayed(this, m_ExTimerDelay);
            } else {
                if (m_curTimerTimes > 0) {
                    handler.postDelayed(this, m_ExTimerDelay);
                    m_curTimerTimes--;
                } else {
                    return;
                }
            }
            byte[] paramBuf = new byte[1];
            paramBuf[0] = 0x00;
            MakeAndSendMessageToModule(mControlID,
                    UIAction.UIACTION_MOUSETIMER, paramBuf);
        }
    };

    public FlyButtonObj(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = getContext().obtainStyledAttributes(attrs,
                R.styleable.FlyButtonObj);
        m_bActionUI = a.getBoolean(R.styleable.FlyButtonObj_actionUI, true);
        m_bTouchable = a.getBoolean(R.styleable.FlyButtonObj_touchAble, true);
        m_bDownEvent = a.getBoolean(R.styleable.FlyButtonObj_downEvent, false);
        m_bUpEvent = a.getBoolean(R.styleable.FlyButtonObj_upEvent, true);

        m_bTimer = a.getBoolean(R.styleable.FlyButtonObj_bTimer, false);
        m_timerDelay = a.getInt(R.styleable.FlyButtonObj_btimerDelay, 300);
        m_ExTimerDelay = a.getInt(R.styleable.FlyButtonObj_bExTimeDelay,
                m_timerDelay);
        m_timerTimes = a.getInt(R.styleable.FlyButtonObj_btimerTimes, 0);

        a.recycle();
    }

    
    public FlyButtonObj(Context c)
    {
        super(c);
    }
    @Override
    public void setCommand(byte[] command) {
        // TODO Auto-generated method stub
        super.setCommand(command);
    }

    @Override
    public void setBooleanData(boolean data) {
        // TODO Auto-generated method stub
        super.setBooleanData(data);
    }

    @Override
    public void setIntegerData(int data) {
        // TODO Auto-generated method stub
        super.setIntegerData(data);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        super.onTouchEvent(event);
        if (m_bTouchable == false) {
            return true;
        }
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            if (m_bActionUI) {
                getBackground().setLevel(1);
            }
            if (m_bDownEvent) {
                byte[] paramBuf = new byte[1];
                paramBuf[0] = 0x00;
                MakeAndSendMessageToModule(mControlID,
                        UIAction.UIACTION_MOUSEDOWN, paramBuf);
                System.out
                        .println("botton----------------------------------down");
            }
            if (m_bTimer) {
                m_curTimerTimes = m_timerTimes;
                handler.postDelayed(runnable, m_timerDelay);
            }
            break;
        case MotionEvent.ACTION_UP:
            if (m_bActionUI) {
                getBackground().setLevel(0);
            }
            if (m_bUpEvent) {
                byte[] paramBuf = new byte[1];
                paramBuf[0] = 0x00;
                MakeAndSendMessageToModule(mControlID,
                        UIAction.UIACTION_MOUSEUP, paramBuf);
                System.out
                        .println("botton----------------------------------up");
            }
            if (m_bTimer) {
                handler.removeCallbacks(runnable);
            }
            break;
        default:
            break;
        }
        return true;
        // return super.onTouchEvent(event); //false
    }
}
