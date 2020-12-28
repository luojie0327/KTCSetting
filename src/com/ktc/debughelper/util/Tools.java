package com.ktc.debughelper.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.TextUtils;

/**
 * @author Arvin
 * @TODO 封装工具类
 * @Date 2019.1.23
 */
public class Tools {
	private static final String TAG = "Tools";

	/**
	 * 判断某一服务是否正在运行
	 * @param mContext
	 * @param serviceName
	 * @return boolean
	 */
	public static boolean isServiceWorking(Context mContext, String serviceName) {
        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningServiceInfo> mServiceList = am.getRunningServices(50);
        if (mServiceList.size() <= 0) {
        	LogUtil.i(TAG, serviceName + "   isNotServiceWorking");
            return false;
        }
        
        for (RunningServiceInfo mServiceInfo : mServiceList) {
            String mName = mServiceInfo.service.getClassName().toString();
            if (mName.contains(serviceName)) {
            	LogUtil.i(TAG, serviceName + "   isServiceWorking");
                return true ;
            }
        }
        LogUtil.i(TAG, serviceName + "   isNotServiceWorking");
        return false;
    }
	
	
	/**
	* 判断当前栈顶界面是否为某进程
	* TODO 判断当前是否为pkgName
	* @throws IOException 
	* @return boolean 
	*/
	public static boolean isTopCurRunningPackage(String[] nameList) {
		if (nameList != null && nameList.length > 0) {
			String result = getTopRunningPackage();
			for(String tmpPkg : nameList){
				LogUtil.i(TAG, "tmpPkg:  "+tmpPkg);
				if (result != null && result.contains(tmpPkg)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 获取当前前台进程的名称
	 * @return String
	 * 
	 */
	public static String getTopRunningPackage(){
		try{
			String cmd ="dumpsys activity activities";
			String filter ;
			if (android.os.Build.VERSION.SDK_INT >= 26) {
				filter = "mResumedActivity";
			} else {
				filter = "mFocusedActivity";
			}
			String result = command(cmd , filter);
			LogUtil.i(TAG, "result:  "+result);
			return result ;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	       
	       
	/**
	* 
	* TODO 运行shell命令过滤指定信息
	* @param cmd 
	* @param filter 
	* @return String
	* @throws IOException 
	*/
	private static String command(String cmd, String filter) {
		Process process = null ;
		InputStream stream = null; 
		InputStreamReader reader = null;
		BufferedReader bufferedReader = null;
		try {
			process = Runtime.getRuntime().exec(cmd);
			stream = process.getInputStream();
			if (stream != null) {
				reader = new InputStreamReader(stream);
				bufferedReader = new BufferedReader(reader, 1024);
				String line;
				while ((line = bufferedReader.readLine()) != null) {
					if (line.contains(filter)) {
						break;
					}
				}
				bufferedReader.close();
				reader.close();
				stream.close();
				process.destroy();
				return line;
			}
		} catch (Exception e) {
			e.printStackTrace() ;
		}finally{
			try {
				if(bufferedReader != null){
					bufferedReader.close();
				}
				if(reader != null ){
					reader.close() ;
				}
				if(stream != null){
					stream.close();
				}
				if(process != null){
					process.destroy();
				}
			} catch (Exception e) {
				e.printStackTrace() ;
			}
		}
		return null;
	}
	
	
	
	/**
	 * 判断自身进程的logcat任务进程是否存在
	 * @param allProcList
	 * @return boolean
	 */
	public static boolean hasMyUserLogcat(Context mContext) {

		List<String> orgProcessList = getAllProcess();
		List<ProcessInfo> allProcList = getProcessInfoList(orgProcessList);
		
		String packName = mContext.getPackageName();
		String myUser = getAppUser(packName, allProcList);
		
		for (ProcessInfo processInfo : allProcList) {
			if (!TextUtils.isEmpty(myUser) && myUser.equals(processInfo.user) && "logcat".equals(processInfo.name)) {
				LogUtil.i(TAG, "hasMyUserLogcat:  "+processInfo.toString());
				return true ;
			}
		}
		return false ;
	}

	/**
	 * 获取所有已安装应用
	 * 
	 * @param context
	 * @return List<ApplicationInfo>
	 */
	public static List<ApplicationInfo> getInstalledApplications(Context mContext) {
		PackageManager packageManager = mContext.getApplicationContext()
				.getPackageManager();
		return packageManager
				.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
	}
	
	
	/**
	 * 关闭由本程序开启的logcat进程：
	 * 根据用户名称杀死进程(如果是本程序进程开启的Logcat收集进程那么两者的USER一致)
	 * 如果不关闭会有多个进程读取logcat日志缓存信息写入日志文件
	 * 
	 * @param allProcList
	 * @return
	 */
	public static void killMyUserLogcat(Context mContext) {
		
		List<String> orgProcessList = getAllProcess();
		List<ProcessInfo> allProcList = getProcessInfoList(orgProcessList);
		
		String packName = mContext.getPackageName();
		String myUser = getAppUser(packName, allProcList);
		//只打印本App和logcat的进程信息，其他进程信息不打印
		for (ProcessInfo processInfo : allProcList) {
			if (myUser.equals(processInfo.user) || "logcat".equals( processInfo.name )) {
				//LogUtil.i(TAG, "killLogcatProcess_processInfo:  "+processInfo.toString());
			}
		}
		for (ProcessInfo processInfo : allProcList) {
			if (processInfo.name.toLowerCase().equals("logcat")
					&& processInfo.user.equals(myUser)) {
				android.os.Process.killProcess(Integer
						.parseInt(processInfo.pid));
				LogUtil.i(TAG, "killLogcatProcess_killProcess:  "+processInfo.toString());
			}
		}
	}

	/**
	 * 获取本程序的用户名称
	 * 
	 * @param packName
	 * @param allProcList
	 * @return
	 */
	private static String getAppUser(String packName, List<ProcessInfo> allProcList) {
		LogUtil.e(TAG, "getAppUser_packName:  "+packName);
		for (ProcessInfo processInfo : allProcList) {
			//LogUtil.e(TAG, "getAppUser_processInfo:  "+processInfo.name);
			if (processInfo.name.equals(packName)) {
				return processInfo.user;
			}
		}
		return null;
	}

	/**
	 * 根据ps命令得到的内容获取PID，User，name等信息
	 * 
	 * @param orgProcessList
	 * @return
	 */
	private static List<ProcessInfo> getProcessInfoList(List<String> orgProcessList) {
		List<ProcessInfo> procInfoList = new ArrayList<ProcessInfo>();
		for (int i = 1; i < orgProcessList.size(); i++) {
			String processInfo = orgProcessList.get(i);
			String[] proStr = processInfo.split(" ");
			// USER PID PPID VSIZE RSS WCHAN PC NAME
			// root 1 0 416 300 c00d4b28 0000cd5c S /init
			List<String> orgInfo = new ArrayList<String>();
			for (String str : proStr) {
				if (!"".equals(str)) {
					orgInfo.add(str);
				}
			}
			if (orgInfo.size() == 9) {
				ProcessInfo pInfo = new ProcessInfo();
				pInfo.user = orgInfo.get(0);
				pInfo.pid = orgInfo.get(1);
				pInfo.ppid = orgInfo.get(2);
				pInfo.name = orgInfo.get(8);
				procInfoList.add(pInfo);
				
				//LogUtil.e(TAG, "getProcessInfoList_pInfo:  "+pInfo.toString());
			}
		}
		return procInfoList;
	}

	/**
	 * 运行PS命令得到进程信息
	 * 
	 * @return
	 * 			USER PID PPID VSIZE RSS WCHAN PC NAME
	 * 			root 1 0 416 300 c00d4b28 0000cd5c S /init
	 */
	private static List<String> getAllProcess() {
		List<String> orgProcList = new ArrayList<String>();
		Process proc = null;
		try {
			proc = Runtime.getRuntime().exec("ps");
			StreamConsumer errorConsumer = new StreamConsumer(proc.getErrorStream());

			StreamConsumer outputConsumer = new StreamConsumer(proc
					.getInputStream(), orgProcList);

			errorConsumer.start();
			outputConsumer.start();
			if (proc.waitFor() != 0) {
				LogUtil.e(TAG, "getAllProcess proc.waitFor() != 0");
			}
		} catch (Exception e) {
			LogUtil.e(TAG, "getAllProcess failed： "+e.toString());
		} finally {
			try {
				proc.destroy();
			} catch (Exception e) {
				LogUtil.e(TAG, "getAllProcess failed： "+e.toString());
			}
		}
		
		LogUtil.e(TAG, "getAllProcess orgProcList： "+orgProcList.size());
		return orgProcList;
	}
	
	static class StreamConsumer extends Thread {
		InputStream is;
		List<String> list;

		StreamConsumer(InputStream is) {
			this.is = is;
		}

		StreamConsumer(InputStream is, List<String> list) {
			this.is = is;
			this.list = list;
		}

		public void run() {
			try {
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				String line = null;
				while ((line = br.readLine()) != null) {
					if (list != null) {
						list.add(line);
					}
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}
	
	static class ProcessInfo {
		public String user;
		public String pid;
		public String ppid;
		public String name;

		@Override
		public String toString() {
			String str = "user=" + user + " pid=" + pid + " ppid=" + ppid
					+ " name=" + name;
			return str;
		}
	}
    
}
