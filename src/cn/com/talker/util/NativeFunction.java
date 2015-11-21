package cn.com.talker.util;

import android.util.Log;


public class NativeFunction {
	
	static{
		System.loadLibrary("talker_native");
	}

	
	/**
	 * 初始化
	 */
	public static native void init(String observerName,int interval);

	/**
	 * 卸载观察者
	 * @param packName 包名
	 * @param userSerial 
	 * @param url 卸载打开的URL
	 * @return pid
	 */
	public static native int startObserver(String packName,String userSerial,String component,String url);

	/**
	 * 销毁服务
	 * @param packName
	 * @return
	 */
	public static native boolean destroyObserver(String packName); 
	
	private static int id=1;
    public static boolean listener(){
    	Log.e("N_filelistener.h", "ID:"+(id++));
    	return true;
    }

}
