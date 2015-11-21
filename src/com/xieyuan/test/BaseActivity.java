package com.xieyuan.test;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.xieyuan.app.MyApplication;
import com.xieyuan.constant.Keys;

public abstract class BaseActivity extends Activity{

	private Thread workThread;
	private Object workResult;

	private ViewGroup progressLayout;
	private TextView progressText;
	
	protected boolean workCancel=true;

	
	protected Activity mInstance;	

	//是否显示切换动画
	protected boolean bAnimation=true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setConView();
		initWidget();
	}
	
	/**
	 * 设置Activity布局，该方法将在onCreate首先调用
	 */
	protected abstract void setConView();
	
	private void initWidget(){
	

		
		mInstance=this;
	}	
/*	*//**
	 * 显示弹出菜单
	 * @param v
	 * @param resMenu
	 * @param listener
	 *//*
	protected void showPopupMenu(View v,int resMenu,PopupMenu.OnMenuItemClickListener listener) {
	    popupMenu = new PopupMenu(this, v);	    	    
	    if(listener!=null)
	    	popupMenu.setOnMenuItemClickListener(listener);
	    
	    MenuInflater inflater = popupMenu.getMenuInflater();
	    inflater.inflate(resMenu, popupMenu.getMenu());
	    popupMenu.show();
	}
*/
	protected boolean menuKeyOnClick(){
		return false;
	}
	
	/**
	 * 显示进度条
	 */
	protected void showProgressBar(){
		if(progressLayout!=null)
			progressLayout.setVisibility(View.VISIBLE);
		if(progressText!=null)
			progressText.setText("");
	}
	/**
	 * 显示进度条和文字
	 */
	protected void showProgressBar(String str){
		if(progressLayout!=null)
			progressLayout.setVisibility(View.VISIBLE);
		if(progressText!=null&&str!=null)
			progressText.setText(str);
	}
	
	protected boolean isShowProgressBar(){
		if(progressLayout!=null){
			return progressLayout.isShown();
		}
		return false;
	}
	
	/**
	 * 显示进度条和文字
	 */
	protected void showProgressBar(int res){
		showProgressBar(getString(res));
	}
	/**
	 * 隐藏进度条
	 */
	protected void dismissProgressBar(){
		if(progressLayout!=null)
			progressLayout.setVisibility(View.GONE);
		if(workThread!=null&&!workThread.interrupted()){
			workThread.interrupt();
		}
	}
	
	protected void showToast(String str){
		Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
	}
	
	protected void showToast(int res){
		Toast.makeText(this, res, Toast.LENGTH_SHORT).show();
	}
	

	/**
	 * 执行ProgressWork()任务 完成后执行ProgressFinish()
	 * 
	 * @param showProgress
	 *            是否显示进度条（转动的圆圈）
	 */
	protected void startWork(boolean showProgress,String text,final int type,boolean workCancel,final String... params) {
		this.workCancel=workCancel;
		if (showProgress && progressLayout != null) {
			showProgressBar(text);
		}	
		// 启动任务线程
		workThread=new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					workResult=doWork(type,params);
				} catch (Exception error) {
					error.printStackTrace();
				} finally {
					Bundle bundle=new Bundle();
					bundle.putInt(Keys.INTEGER_DATA, type);
					Message message=new Message();
					message.setData(bundle);
					message.what=2;
					
					myHandler.sendMessage(message);
					//myHandler.sendEmptyMessage(2);
				}

			}
		});
		workThread.start();
	}
	protected void startWork(boolean showProgress,String text){
		startWork(showProgress, text, 0,false);
	}
	protected void startWork(boolean showProgress){
		startWork(showProgress, null, 0,false);
	}
	protected void startWork(boolean showProgress,String text,final int type,final String... params){
		startWork(showProgress, text, type,false,params);
	}

	/**
	 * 调用showProgress()后会自动执行此方
	 */
	protected Object doWork(int type,String... params) {
		return null;
	}

	/**
	 * 调用showProgress()后执行完ProgressRun()就会执行
	 */
	protected void doWorkFinish(int type,Object result) {

	}
	
	private MyHandler myHandler=new MyHandler(this);
	/**
	 * MyHandler实现了进度条的隐藏，和任务的调用
	 * Handler类应该应该为static类型，否则有可能造成泄露。在程序消息队列中排队的消息保持了对目标Handler类的应用
	 * @author Administrator
	 *
	 */
	private static class MyHandler extends Handler{
		private BaseActivity baseActivity;
		
		public MyHandler(BaseActivity baseActivity) {
			this.baseActivity = baseActivity;
		}
		
	    public void handleMessage(Message msg) {
	    	switch(msg.what){
	    	case 1:
					
	    		break;
	    	case 2: //关闭对话框
	    		int type=msg.getData().getInt(Keys.INTEGER_DATA);
	    		baseActivity.dismissProgressBar();
	    		baseActivity.doWorkFinish(type,baseActivity.workResult);
	    		break;
	    	}
	    }
	}
	
	@Override
	public void finish() {		
		super.finish();

	}
	
	@Override
	public boolean onKeyUp(int keyCode,KeyEvent event){
		if(keyCode==KeyEvent.KEYCODE_BACK){
			if(isShowProgressBar()&&workCancel){
				dismissProgressBar();
				return true;
			}
		}else if(keyCode==KeyEvent.KEYCODE_MENU){
			if(menuKeyOnClick()){
				return true;
			}
			
		}
		return super.onKeyUp(keyCode, event);
	}
	
	
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
	}
}
