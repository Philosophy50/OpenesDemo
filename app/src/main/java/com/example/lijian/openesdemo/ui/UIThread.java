/**
 * 
 */
package com.example.lijian.openesdemo.ui;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.example.lijian.openesdemo.Base.iLog;

/**
 * @author tj
 *
 */
public abstract class UIThread extends Thread{
	public final String TAG="UIThread";
	protected static final int what_run_prepare=1;
	protected static final int what_run_completed=2;
	
	protected abstract int onPreExecute();
	protected abstract int doInBackground();
	protected abstract int onPostExecute();
	
	Runnable runnable=new Runnable(){
		public void run() {
			try {
				doInBackground();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				iLog.e(TAG+" onStart UIThread exceptione="+e.getMessage());
			}
	}					
};
	
	Handler mHandler= new Handler(){
        @Override
        public void handleMessage(Message msg) {
        	iLog.d(TAG+" onStart handleMessage msg.what="+msg.what);
            switch (msg.what){
            	case UIThread.what_run_prepare:
            		onPreExecute();
            		break;
                case UIThread.what_run_completed:
                	onPostExecute();
                    break;
                default:
                    break;
            }
        }
    };
	
	public UIThread() {
		super();
    }
	
	@Override
    public void run() {
        try {
        	Looper.prepare();
    		if( runnable==null ){
    			iLog.w(TAG+" run() runnable is null");
    		}
    		
    		if( mHandler==null ){
    			iLog.w(TAG+" run() mHandler is null");
    		}
    		
    		mHandler.obtainMessage(what_run_prepare).sendToTarget();
    		
        	runnable.run();
        }
        catch(Exception e){
        	iLog.e(TAG+" run() "+e.getMessage());
        }
        finally{
	        mHandler.obtainMessage(what_run_completed).sendToTarget();
	        Looper.loop();
        }
	}
	
}
