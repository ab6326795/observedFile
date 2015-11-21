package com.xieyuan.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;

/**
 * 继承于Application，实现了APP生命周期管理
 * 
 */
public class MyApplication extends Application {
	public static String TAG = "MyApplication";
		
	// 本类对象，实现单例模式
	private static MyApplication sInstance = null;

	public static Context mContext;
	// Activity集合
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
	 * 在这里初始化配置了Universal-Image-Loader
	 */
	public void onCreate() {
		super.onCreate();
		
		mContext=getApplicationContext();
		activityList = new ArrayList<Activity>();
		
	}

	public static Context getAppContext(){
		if(mContext==null)
			Log.e("ERROR", "MyApplication Context is null ！");
		return mContext;
	}
	
	/**
	 * 清除集合
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
	 * 程序退出
	 */
	public void exit(){
		
		removeAllActivity();
		//System.exit(0);
	}
}
