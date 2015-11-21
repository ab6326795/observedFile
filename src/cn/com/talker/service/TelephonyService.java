package cn.com.talker.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class TelephonyService extends Service{
	private final static String TAG="TelephonyService";
	
	private Context mContext;
	  
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	
    @Override
    public void onCreate() {
    	super.onCreate();
        
    	mContext=getApplicationContext();
    
        
    }

    @Override
	public void onDestroy(){

    	startService(new Intent(getApplicationContext(),TelephonyService.class));
    	
    	super.onDestroy();


    }
}
