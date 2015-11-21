package com.xieyuan.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;

/**
 * �̳���Application��ʵ����APP�������ڹ���
 * 
 */
public class MyApplication extends Application {
	public static String TAG = "MyApplication";
		
	// �������ʵ�ֵ���ģʽ
	private static MyApplication sInstance = null;

	public static Context mContext;
	// Activity����
	private static ArrayList<Activity> activityList = null;

	public MyApplication() {		
		
	}

	public static synchronized MyApplication getInstance() {
		if (sInstance == null) {
			sInstance = new MyApplication();			
		}
		return sInstance;
	}

	/**
	 * �������ʼ��������Universal-Image-Loader
	 */
	public void onCreate() {
		super.onCreate();
		
		mContext=getApplicationContext();
		activityList = new ArrayList<Activity>();
		
	}

	public static Context getAppContext(){
		if(mContext==null)
			Log.e("ERROR", "MyApplication Context is null ��");
		return mContext;
	}
	
	/**
	 * �������
	 */
	public void clearActivity() {
		activityList.clear();
	}
    
	public void addActivity(Activity activity){
		activityList.add(activity);
	}
	public void removeActivity(Activity activity){
		activityList.remove(activity);
	}
	
	public void removeAllActivity(Class<?>...ignore){
		Iterator<Activity> iterator=activityList.iterator();
		while(iterator.hasNext()){
			Activity temp=iterator.next();
			Class<?> tem=temp.getClass();
			boolean ignoreActivity=false;
			if(ignore!=null)
			  for(int i=0;i<ignore.length;i++){
				  Class<? extends Activity> class1=(Class<? extends Activity>) ignore[i];
				  if(temp.getClass()==class1){
					  ignoreActivity=true;
					  break;
				  }
			  }
			if(ignoreActivity)
				continue;
			if(temp!=null&&!temp.isFinishing())
			   temp.finish();
		}
	}
	
	/**
	 * �����˳�
	 */
	public void exit(){
		
		removeAllActivity();
		//System.exit(0);
	}
}
