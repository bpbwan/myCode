package com.android.flyaudio.powerwidget;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Debug;
import android.util.Log;



public class RamInfo {
	private Context context;
	ActivityManager am;

	public RamInfo(Context context) {
		super();
		this.context = context;
	}

	/**
	 * 得到设备的所有RAM
	 * @return 返回所有内存大小，单位：kb
	 */
	public int getAllMemory() {
		String filePath = "/proc/meminfo";
		int ram = 0;
		FileReader fr = null;
		BufferedReader localBufferedReader = null;
		try {
			fr = new FileReader(filePath);
			localBufferedReader = new BufferedReader(fr, 8192);
			String line = localBufferedReader.readLine();
			int a = line.length() - 3;
			int b = line.indexOf(' ');
			String str = line.substring(b, a);
			while (str.substring(0, 1).equals(" ")) {
				str = str.substring(1, str.length());
			}
			ram = Integer.parseInt(str);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fr.close();
				localBufferedReader.close();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}

		return ram;
	}

	/**
	 * 得到设备的可用RAM
	 * @return 返回所有内存大小，单位：byte
	 */
	public long getAvailMemory() {
	    am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
		am.getMemoryInfo(mi);
		return mi.availMem / 1024;
	}

	/**
	 * 得到设备的已用RAM
	 * @return 返回所有内存大小，单位：byte
	 */
	public long getUesdMemory() {
		return getAllMemory() - getAvailMemory();
	}
	
	/**
	 * 获取设备的RAM信息
	 * @return String数组，数组第0个元素为 总RAM，第1个元素为 可用RAM，单位M或G
	 */
	public String[] getRamInfo() {
		String[] ramInfo = new String[3];

		if (getAllMemory() < 1024 * 1024)
			ramInfo[0] = String.valueOf(Math.round(getAllMemory() / 1024.0)) + "M";
		else
			ramInfo[0] = String.valueOf(Math.round(getAllMemory() / 1024.0 / 1024)) + "G";

		if (getAvailMemory() < 1024 * 1024)
			ramInfo[1] = String.valueOf(Math.round(getAvailMemory() / 1024.0)) + "M";
		else
			ramInfo[1] = String.valueOf(Math.round(getAvailMemory() / 1024.0 / 1024)) + "G";
		
		if (getUesdMemory() < 1024 * 1024)
			ramInfo[2] = String.valueOf(Math.round(getUesdMemory() / 1024.0)) + "M";
		else
			ramInfo[2] = String.valueOf(Math.round(getUesdMemory() / 1024.0 / 1024)) + "G";

		return ramInfo;
	}
	
	/**
	 * 获取ram已用的百分比
	 * @return 百分比，不带小数
	 */
	public String getUsedPercent() {
		return String.valueOf(Math.round(100 * (getUesdMemory() / (float)getAllMemory())));
	}
	
	
	
    public int getRunningTasksSize(){
        //return am.getRunningTasks(100).size();
        Log.d("zengyuke","getRunningTasks= "+am.getRunningTasks(Integer.MAX_VALUE).size());
        Log.d("zengyuke","getRunningAppProcesses= "+am.getRunningAppProcesses().size());
     return am.getRunningAppProcesses().size();
}
    public int getRunningAppSize(){
        ActivityManager mAManager;
        mAManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);       
        
        List<Process> mProcessList = getProcessInfoList(mAManager);
        
     return mProcessList.size();
}
    
    
    public List<Process> getProcessInfoList(ActivityManager am){
        List<Process> listProcess = new ArrayList<Process>();
        List<ActivityManager.RunningAppProcessInfo> appProList = am.getRunningAppProcesses();
            
        PackageManager pm = context.getPackageManager(); 
        
        for(ActivityManager.RunningAppProcessInfo pi : appProList){
            if (pm.getLaunchIntentForPackage(pi.processName) != null && pi.pid != android.os.Process.myPid()) { 
                try{
                    Process process = new Process();
                    ApplicationInfo ai = pm.getApplicationInfo(pi.processName,PackageManager.GET_META_DATA);
                    process.setPid(pi.pid);
                    process.setName(ai.loadLabel(pm).toString());
                    process.setProcess(pi.processName);
                    process.setIcon(ai.loadIcon(pm));
                    Debug.MemoryInfo[] memoryInfo = am.getProcessMemoryInfo(new int[] {process.getPid()});  
                    process.setRam(memoryInfo[0].dalvikPss+memoryInfo[0].nativePss+memoryInfo[0].otherPss);
                    listProcess.add(process);
                } catch (NameNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } 
            }
        }
        
        return listProcess;
    }
	
    
    
    
}
