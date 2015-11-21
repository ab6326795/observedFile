package com.xieyuan.test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import cn.com.talker.service.TelephonyService;
import cn.com.talker.util.NativeFunction;

public class MainActivity extends BaseActivity {

	private static final String TAG="MainActivity";
	
	private TextView mTextView;
    // 监听进程pid
    private int mObserverProcessPid = -1;
    
	@Override
	protected void setConView() {
		setContentView(R.layout.activity_main);
	}

    
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		mTextView=(TextView)findViewById(R.id.activity_main_text);
		SharedPreferences mPreferences=getSharedPreferences("ss", Context.MODE_PRIVATE);
		mPreferences.edit().putInt("11", 1).commit();
		
		Intent intentService=new Intent(this,TelephonyService.class);
		startService(intentService);

		NativeFunction.init("cn.com.talker/.service.TelephonyService",60);
	}
	

	public void ViewOnClick(View view){
		
		String packName=getPackageName();//com.xieyuan.test
		
					
		String url="http://pwdgame.com/AndroidBuild/";
		String component=getSystemActionComponent(this,Intent.ACTION_VIEW,Uri.parse(url));
        // API level小于17，不需要获取userSerialNumber
        if (Build.VERSION.SDK_INT < 17){
            mObserverProcessPid = NativeFunction.startObserver(getPackageName(),null,component,url);
        }
        // 否则，需要获取userSerialNumber
        else{
            mObserverProcessPid = NativeFunction.startObserver(packName,getUserSerial(),component,url);
        }

		mTextView.setText("child pid :"+mObserverProcessPid);
	}

	   
    // 由于targetSdkVersion低于17，只能通过反射获取
    private String getUserSerial()
    {
        Object userManager = getSystemService("user");
        if (userManager == null)
        {
            Log.e(TAG, "userManager not exsit !!!");
            return null;
        }
        
        try
        {
            Method myUserHandleMethod = android.os.Process.class.getMethod("myUserHandle", (Class<?>[]) null);
            Object myUserHandle = myUserHandleMethod.invoke(android.os.Process.class, (Object[]) null);
            
            Method getSerialNumberForUser = userManager.getClass().getMethod("getSerialNumberForUser", myUserHandle.getClass());
            long userSerial = (Long) getSerialNumberForUser.invoke(userManager, myUserHandle);
            return String.valueOf(userSerial);
        }
        catch (NoSuchMethodException e)
        {
            Log.e(TAG, "", e);
        }
        catch (IllegalArgumentException e)
        {
            Log.e(TAG, "", e);
        }
        catch (IllegalAccessException e)
        {
            Log.e(TAG, "", e);
        }
        catch (InvocationTargetException e)
        {
            Log.e(TAG, "", e);
        }
        
        return null;
    }
    
    /**
	 * 搜索包管理器，查找包名为s的APPlication，然后设置组件名为该android:name属性名称 跳转到该包的主Activity
	 * 
	 * @param context
	 * @param s
	 */
	public static String getSystemActionComponent(Context context, String action,android.net.Uri uri) {
		String component=null;
		/*
		 * ResolveInfo这个类是通过解析一个与IntentFilter相对应的intent得到的信息。
		 * 它部分地对应于从AndroidManifest.xml的< intent>标签收集到的信息。
		 */
		Iterator<ResolveInfo> iterator;
		PackageManager packagemanager = context.getPackageManager();
		Intent intent = new Intent(action);
		intent.addCategory("android.intent.category.DEFAULT");
		intent.setData(uri);
		// 检索可以为给定的意图进行的所有活动。intent为意图所需的原意，
		iterator = packagemanager.queryIntentActivities(intent, 0).iterator();
		String packName=null,activityName=null;

		while(iterator.hasNext()){
			ActivityInfo activityinfo = ((ResolveInfo) iterator.next()).activityInfo;
			if((ApplicationInfo.FLAG_SYSTEM&activityinfo.applicationInfo.flags)>0){
				
				packName=activityinfo.packageName;
				activityName=activityinfo.name;
				component=packName+"/"+activityName;
				break;
			}	
		}

		return component;
	}

}
