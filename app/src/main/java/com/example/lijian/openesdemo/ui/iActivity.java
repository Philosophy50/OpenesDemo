/**
 * 
 */
package com.example.lijian.openesdemo.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

import com.example.lijian.openesdemo.Base.iLog;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author tj
 *
 */
public abstract class iActivity extends Activity{
	public final String TAG="iActivity";
	private Context context;

	private  LoadAnimation loadAnimation=null;
	//just load view from layout, every view that can be clicked or selected should be set disable by default
	protected abstract void onInitView(Bundle savedInstanceState);
	//load data or create object
	protected abstract void onInitData();
	//every view be set to enable after all objects have be created
	protected abstract void onInitCompleted();
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context=this;
		iLog.w(TAG+" onCreate");

		loadAnimation=new LoadAnimation(iActivity.this);
				
		onInitView(savedInstanceState);
		
		loadAnimation.start();
	}
	
	@Override
    public void onStart(){        
        super.onStart();
		iLog.w(TAG+" onStart");
        if(!uiThread.isAlive())
            uiThread.start();
        //ProgressDialog.show(context, "ͬ��", "����ͬ����");
    }
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		iLog.w(TAG+" onDestroy");
		uiThread.interrupt();
	}
	
	protected void init_timeout_set(long timeout,String info){
		if( loadAnimation!=null ){
			loadAnimation.timeout_set(timeout, info);
		}
	}
	
	private UIThread  uiThread = new UIThread(){
		@Override
		protected int onPreExecute() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		protected int doInBackground() {
			// TODO Auto-generated method stub
			try {
				onInitData();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				iLog.e(TAG+" MyTask doInBackground exceptione="+e.getMessage());
			}
			return 0;
		}

		@Override
		protected int onPostExecute() {
			// TODO Auto-generated method stub
			try {
				onInitCompleted();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				iLog.e(TAG+" MyTask onPostExecute exceptione="+e.getMessage());
			}
        	
        	if( loadAnimation!=null ){
				loadAnimation.stop();
			}
			return 0;
		}
    	
    };
    
    
    private class LoadAnimation{
    	private static final int what_show=1;
    	private static final int what_dismiss=2;
    	
    	private boolean benable=false;
		private Timer timer = new Timer();
		private long   timeout_begin=0;
		private long   timeout_peroid=0;//ms
		private String info="LoadAnimation";
		private ProgressDialog load_wait;
		
		private TimerTask loadtask = new TimerTask() {
			@Override
			public void run() {
				onLoadCheckTimer();
			}
		};
		
		public LoadAnimation(Activity instance){
			load_wait=new ProgressDialog(instance);
			load_wait.setOnDismissListener(new OnDismissListener(){ 
				@Override
				public void onDismiss(DialogInterface dialog) {
					// TODO Auto-generated method stub
					start();
				} 
	        });
			timer = new Timer();
			timer.schedule(loadtask, 0,100);
		}
		
		protected void timeout_set(long timeout,String info){
			this.timeout_peroid=timeout;
			this.info=info;
		}
		
		private void enable(){
			benable=true;
		}
		
		private void disable(){
			benable=false;
		}
		
		private void time_begin(){
			timeout_begin=SystemClock.elapsedRealtime();
		}
		
		public void start(){
			if( timeout_peroid==0 ){
				iLog.w(TAG+" LoadAnimation.start() timeout_peroid=0");
				return;
			}
			time_begin();
			enable();
		}
		
		public void stop(){
			disable();
			timer.cancel();
			mHandler.obtainMessage(what_dismiss).sendToTarget();
			iLog.d(TAG+" LoadAnimation.stop ");
		}
		
		private void onLoadCheckTimer(){
			iLog.d(TAG+" onLoadCheckTimer ");
			if( benable==false ){
				return;
			}
			long ct=SystemClock.elapsedRealtime();
			iLog.d(TAG+" onLoadCheckTimer over  ct = "+ct+"  timeout_begin = "+timeout_begin +"  timeout_peroid = " +timeout_peroid);
			if( (ct-timeout_begin)>timeout_peroid ){
				iLog.d(TAG+" onLoadCheckTimer over  in");
				load_wait.setMessage(this.info); 
				disable();
				mHandler.obtainMessage(what_show).sendToTarget();
			}
		}
		
		private Handler mHandler = new Handler(){
	        @Override
	        public void handleMessage(Message msg) {
	            switch (msg.what) {
	            case what_show:
	            	load_wait.show();
	                break;
	            case what_dismiss:
	            	load_wait.dismiss();
	                break;
	            default:
	                break;
	            }
	        };
	    };
	}
}
