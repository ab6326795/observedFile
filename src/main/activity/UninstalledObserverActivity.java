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
 * @note 监听此应用是否被卸载，若被卸载则弹出卸载反馈
 * @note 由于API17加入多用户支持，原有命令在4.2及更高版本上执行时缺少userSerial参数，特此修改
 *
 */

public class UninstalledObserverActivity extends Activity
{
    /* 数据段begin */
    private static final String TAG = "UninstalledObserverActivity";
    
    // 监听进程pid
    private int mObserverProcessPid = -1;
    /* 数据段end */
    
    /* static */
    // 初始化监听进程
    private native int init(String userSerial);
    
    private native String decode();
    
    static
    {
        Log.d(TAG, "load lib --> uninstalled_observer");
        System.loadLibrary("Test");
    }
    /* static */
    
    /* 函数段begin */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.uninstalled_observer_layout);
        
        Toast.makeText(getApplicationContext(), decode(), Toast.LENGTH_LONG).show();
        // API level小于17，不需要获取userSerialNumber
     /*   if (Build.VERSION.SDK_INT < 17)
        {
            mObserverProcessPid = init(null);
        }
        // 否则，需要获取userSerialNumber
        else
        {
            mObserverProcessPid = init(getUserSerial());
        }*/
    }
    
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        
        // 示例代码，用于结束监听进程
//        if (mObserverProcessPid > 0)
//        {
//            android.os.Process.killProcess(mObserverProcessPid);
//        }
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
    /* 函数段end */
}