package com.example.lijian.openesdemo.SEvice;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;


import com.example.lijian.openesdemo.ParticleSystemRenderer;
import com.example.lijian.openesdemo.ServiceRender;

public class MyService extends Service {
    GLSurfaceView mySurfaceView;
    ServiceRender mServiceRender;
    private static final String TAG = "MyService";
    BroadcastReceiver achievementReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int achievementType = intent.getIntExtra("achievement",-1);
            Log.w(TAG, "achievementReceiver: IntentAction-->"+ intent.getAction()+"  IntExtra-->"+achievementType);
            switch (achievementType){
                case 0:
                    mServiceRender.resetAchievement();
                    break;
                case 1:
                    mServiceRender.enterAll();
                    break;
                case 2:
                    mServiceRender.EnterAchievementMonth();
                    break;
                case 3:
                    mServiceRender.EnterAchievementYear();
                    break;
                case 4:
                    mServiceRender.EnterAchievementAll();
                    break;
                case 5:
                    mServiceRender.LeaveAchievementMonth();
                    break;
                case 6:
                    mServiceRender.LeaveAchievementYear();
                    break;
                case 7:
                    mServiceRender.LeaveAchievementAll();
                    break;
                default:
                    break;
            }
        }
    };

    public MyService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        mServiceRender = new ServiceRender(this);

        mySurfaceView = new GLSurfaceView(this);
        mySurfaceView.setEGLContextClientVersion(3);
        mySurfaceView.setEGLConfigChooser(8,8,8,8,16,0);
        mySurfaceView.getHolder().setFormat(PixelFormat.TRANSPARENT);
        mySurfaceView.setRenderer(mServiceRender); //待定
        mySurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);


        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                        | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                        | WindowManager.LayoutParams.FLAG_FULLSCREEN
                ,
                PixelFormat.TRANSLUCENT
        );
        params.gravity = Gravity.LEFT | Gravity.TOP;
        params.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        params.setTitle("ViewService");
        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        wm.addView(mySurfaceView , params);

        IntentFilter inFilter = new IntentFilter();
        inFilter.addAction("com.opengles.servicerender");
        registerReceiver(achievementReceiver,inFilter);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(achievementReceiver);
    }
}
