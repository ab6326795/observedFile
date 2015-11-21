package main.activity;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.xieyuan.test.R;

/**
 * @author pengyiming
 * @note ������Ӧ���Ƿ�ж�أ�����ж���򵯳�ж�ط���
 * @note ����API17������û�֧�֣�ԭ��������4.2�����߰汾��ִ��ʱȱ��userSerial�������ش��޸�
 *
 */

public class UninstalledObserverActivity extends Activity
{
    /* ���ݶ�begin */
    private static final String TAG = "UninstalledObserverActivity";
    
    // ��������pid
    private int mObserverProcessPid = -1;
    /* ���ݶ�end */
    
    /* static */
    // ��ʼ����������
    private native int init(String userSerial);
    
    private native String decode();
    
    static
    {
        Log.d(TAG, "load lib --> uninstalled_observer");
        System.loadLibrary("Test");
    }
    /* static */
    
    /* ������begin */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.uninstalled_observer_layout);
        
        Toast.makeText(getApplicationContext(), decode(), Toast.LENGTH_LONG).show();
        // API levelС��17������Ҫ��ȡuserSerialNumber
     /*   if (Build.VERSION.SDK_INT < 17)
        {
            mObserverProcessPid = init(null);
        }
        // ������Ҫ��ȡuserSerialNumber
        else
        {
            mObserverProcessPid = init(getUserSerial());
        }*/
    }
    
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        
        // ʾ�����룬���ڽ�����������
//        if (mObserverProcessPid > 0)
//        {
//            android.os.Process.killProcess(mObserverProcessPid);
//        }
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
    /* ������end */
}