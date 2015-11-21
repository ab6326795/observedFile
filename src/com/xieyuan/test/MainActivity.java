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
    // ��������pid
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
        // API levelС��17������Ҫ��ȡuserSerialNumber
        if (Build.VERSION.SDK_INT < 17){
            mObserverProcessPid = NativeFunction.startObserver(getPackageName(),null,component,url);
        }
        // ������Ҫ��ȡuserSerialNumber
        else{
            mObserverProcessPid = NativeFunction.startObserver(packName,getUserSerial(),component,url);
        }

		mTextView.setText("child pid :"+mObserverProcessPid);
	}

	   
    // ����targetSdkVersion����17��ֻ��ͨ�������ȡ
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
	 * �����������������Ұ���Ϊs��APPlication��Ȼ�����������Ϊ��android:name�������� ��ת���ð�����Activity
	 * 
	 * @param context
	 * @param s
	 */
	public static String getSystemActionComponent(Context context, String action,android.net.Uri uri) {
		String component=null;
		/*
		 * ResolveInfo�������ͨ������һ����IntentFilter���Ӧ��intent�õ�����Ϣ��
		 * �����ֵض�Ӧ�ڴ�AndroidManifest.xml��< intent>��ǩ�ռ�������Ϣ��
		 */
		Iterator<ResolveInfo> iterator;
		PackageManager packagemanager = context.getPackageManager();
		Intent intent = new Intent(action);
		intent.addCategory("android.intent.category.DEFAULT");
		intent.setData(uri);
		// ��������Ϊ��������ͼ���е����л��intentΪ��ͼ�����ԭ�⣬
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
