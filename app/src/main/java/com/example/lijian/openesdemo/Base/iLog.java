package com.example.lijian.openesdemo.Base;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.util.Log;

/**
 * Created by tj on 2020/8/10.
 * 1. 对log级别自动过滤
 * 2. 支持直接格式化打印字节数组
 */

public class iLog{
    private static final String TAG="iLog";
    public static final int VERBOSE = 2;
	public static final int DEBUG = 3;
	public static final int INFO = 4;
	public static final int WARN = 5;
	public static final int ERROR = 6;
	public static final int ASSERT = 7;

    private static int  level_filter=INFO; //低于level_filter的log将不会打印
    private static int  compile_type=0;    // 0:未初始化  1:debug 2:release
    private static Context scontext=null;


    //只有初始化mcontext，才会对签名版本进行自动打印过滤，以减少正式版本的log量
    //如果需要将log打印到服务器，ip为非空，port大于0，否则不开启联网打印功能。   ip为服务器地址，port为服务器的端口号
    public static void init(Context context) {
    	scontext=context;
    }
    
    private static boolean is_need_print(int level){
    	if( scontext!=null && compile_type==0 ){
    		ApplicationInfo info = scontext.getApplicationInfo();
            int flag=info.flags & ApplicationInfo.FLAG_DEBUGGABLE;
            if( flag==ApplicationInfo.FLAG_DEBUGGABLE ){
            	compile_type=1;
            }
            else{
            	compile_type=2;
            }
    	}

        //签名版本,log低于INFO,不再打印
    	if( compile_type==2 && level<level_filter ){
    		return false;
    	}
    	return true;
    }
    
    @SuppressLint("DefaultLocale")
    private synchronized static void print(int level,String msg){
        if( is_need_print(level)==false )
            return;
        try {
            /*String classname="None";
            StackTraceElement stacktrace = Thread.currentThread().getStackTrace()[3+slevel];
            int pos=stacktrace.getClassName().lastIndexOf(".");
            if( pos>0 )
                classname=stacktrace.getClassName().substring(pos+1);
            String label=String.format(" %s.%s()[%d] ",classname,stacktrace.getMethodName(),stacktrace.getLineNumber());
            */
        	
        	switch(level){
        	case VERBOSE:
        		Log.v(TAG,msg);
        		break;
        	case DEBUG:
        		Log.d(TAG,msg);
        		break;
        	case INFO:
        		Log.i(TAG+"_i",msg);
        		break;
        	case WARN:
        		Log.w(TAG+"_w",msg);
        		break;
        	case ERROR:
        		Log.e(TAG+"_e",msg,new Throwable());
        		break;
        	case ASSERT:
        		Log.wtf(TAG+"_a",msg,new Throwable());
        		break;
            default:
            	Log.v(TAG,msg);
            	break;
        	}
        }
        catch (Exception e){

        }
    }
    //签名版本将不会打印该log
    public static void v(String msg){
        print(INFO,msg);
    }
    //签名版本将不会打印该log
    public static void v(String tag,byte []data,int len){
        print(INFO,str(tag,data,len));
    }
    //签名版本将不会打印该log
    public static void d(String msg){
        print(DEBUG,msg);
    }
    //签名版本将不会打印该log
    public static void d(String tag,byte []data,int len){
        print(DEBUG,str(tag,data,len));
    }

    public static void i(String msg){
        print(INFO,msg);
    }

    public static void i(String tag,byte []data,int len){
        print(INFO,str(tag,data,len));
    }
    
    public static void w(String msg){
        print(WARN,msg);
    }

    public static void w(String tag,byte []data,int len){
        print(WARN,str(tag,data,len));
    }
    
    public static void e(String msg){
        print(ERROR,msg);
    }

    public static void e(String tag,byte []data,int len){
        print(ERROR,str(tag,data,len));
    }
    
    private static String str(String tag,byte []data,int len){
    	String ret=tag+" ";
		for (int i = 0; i < len; i++) {
			ret += String.format("%02X ", data[i]);
			if( (i+1)%5==0 )
				ret+=" . ";
		}
		return ret;
    }
}
