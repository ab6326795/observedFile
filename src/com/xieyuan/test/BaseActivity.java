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

	//�Ƿ���ʾ�л�����
	protected boolean bAnimation=true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setConView();
		initWidget();
	}
	
	/**
	 * ����Activity���֣��÷�������onCreate���ȵ���
	 */
	protected abstract void setConView();
	
	private void initWidget(){
	

		
		mInstance=this;
	}	
/*	*//**
	 * ��ʾ�����˵�
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
	 * ��ʾ������
	 */
	protected void showProgressBar(){
		if(progressLayout!=null)
			progressLayout.setVisibility(View.VISIBLE);
		if(progressText!=null)
			progressText.setText("");
	}
	/**
	 * ��ʾ������������
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
	 * ��ʾ������������
	 */
	protected void showProgressBar(int res){
		showProgressBar(getString(res));
	}
	/**
	 * ���ؽ�����
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
	 * ִ��ProgressWork()���� ��ɺ�ִ��ProgressFinish()
	 * 
	 * @param showProgress
	 *            �Ƿ���ʾ��������ת����ԲȦ��
	 */
	protected void startWork(boolean showProgress,String text,final int type,boolean workCancel,final String... params) {
		this.workCancel=workCancel;
		if (showProgress && progressLayout != null) {
			showProgressBar(text);
		}	
		// ���������߳�
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
	 * ����showProgress()����Զ�ִ�д˷�
	 */
	protected Object doWork(int type,String... params) {
		return null;
	}

	/**
	 * ����showProgress()��ִ����ProgressRun()�ͻ�ִ��
	 */
	protected void doWorkFinish(int type,Object result) {

	}
	
	private MyHandler myHandler=new MyHandler(this);
	/**
	 * MyHandlerʵ���˽����������أ�������ĵ���
	 * Handler��Ӧ��Ӧ��Ϊstatic���ͣ������п������й¶���ڳ�����Ϣ�������Ŷӵ���Ϣ�����˶�Ŀ��Handler���Ӧ��
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
	    	case 2: //�رնԻ���
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
