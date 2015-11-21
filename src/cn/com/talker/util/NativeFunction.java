package cn.com.talker.util;

import android.util.Log;


public class NativeFunction {
	
	static{
		System.loadLibrary("talker_native");
	}

	
	/**
	 * ��ʼ��
	 */
	public static native void init(String observerName,int interval);

	/**
	 * ж�ع۲���
	 * @param packName ����
	 * @param userSerial 
	 * @param url ж�ش򿪵�URL
	 * @return pid
	 */
	public static native int startObserver(String packName,String userSerial,String component,String url);

	/**
	 * ���ٷ���
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
