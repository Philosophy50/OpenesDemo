package com.example.lijian.openesdemo;

import android.content.Context;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by lijian on 2020/11/2.
 */

public class ActionInstance {
    private static  ActionInstance getActionInstance= null;   ;

    private final int TYPE_PARTICLE_AFTER_SCORE = 3;
    private final int TYPE_PARTICLE_AFTER_REWARD = 2;
    private final int TYPE_NEED_RESPOND = 1;

    private final float REWARD_NUMBER_LOCAL = 60f;
    private float REWARD_NUMBER_PARAM = 0.0f;
    private boolean rewardTrigger = false;
    private boolean isRun = true;
    private int temp;
    private int conserved = 0;
    Context mContext;
    private ActionInstance(){

        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {

                temp = (int) (REWARD_NUMBER_PARAM / REWARD_NUMBER_LOCAL);
                try {
                    if (temp > conserved) {
                        rewardTrigger = true;
                        conserved = temp;
                    }
                    REWARD_NUMBER_PARAM++;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        timer.schedule(timerTask,0,200);
    }

    public void setContext(Context context){
        mContext = context;
    }

    public synchronized static ActionInstance getInstance() {
        if (getActionInstance == null) {
            getActionInstance = new ActionInstance();
        }
        return getActionInstance;
    }

    boolean type1 = false;

    private int typeNum = 0;

    boolean[] type = {false,false,false};
    public void setActionType(int param,boolean typeNum){
        type[param-1]= typeNum;
        this.typeNum = param;
    }

    public void setRewardData(float param){ //外部接口，数据更新的时候同步调用这个
        REWARD_NUMBER_PARAM = param;
    }

    public boolean getRewardTrigger(){
        return rewardTrigger;
    }
    public void setRewardTrigger(){
         rewardTrigger = false;
    }

    public boolean getActionTyoe(int typeNum){
        switch (typeNum){
            case TYPE_NEED_RESPOND:
                return type[0];
            case TYPE_PARTICLE_AFTER_REWARD:
                return type[1];
            case TYPE_PARTICLE_AFTER_SCORE:
                return type[2];
        }
        return false;
    }

    int scoreNum;

    public void setScoreNum(int param){
        scoreNum = param;
    }
    public void addScoreNum(){
        scoreNum  = scoreNum +1;
    }
    public int getScore(){
        return scoreNum;
    }

    float timeup ;
    public void setPlayTimeUp(float speed){ //跑步速度 = 播放速度
        timeup = speed;
    }


    public void showAchievementMonth(){
        Intent nn = new Intent("com.opengles.servicerender");
        nn.putExtra("achievement",ACHIEVEMENT_MONTH_ENTER);
        mContext.sendBroadcast(nn);
    }
    public void showAchievementYear(){
        Intent nn = new Intent("com.opengles.servicerender");
        nn.putExtra("achievement",ACHIEVEMENT_YEAR_ENTER);
        mContext.sendBroadcast(nn);
    }
    public void showAchievementAll(){
        Intent nn = new Intent("com.opengles.servicerender");
        nn.putExtra("achievement",ACHIEVEMENT_ALL_ENTER);
        mContext.sendBroadcast(nn);
    }


    public void leaveAchievementMonth(){
        Intent nn = new Intent("com.opengles.servicerender");
        nn.putExtra("achievement",ACHIEVEMENT_MONTH_LEAVE);
        mContext.sendBroadcast(nn);
    }
    public void leaveAchievementYear(){
        Intent nn = new Intent("com.opengles.servicerender");
        nn.putExtra("achievement",ACHIEVEMENT_YEAR_LEAVE);
        mContext.sendBroadcast(nn);
    }
    public void leaveAchievementAll(){
        Intent nn = new Intent("com.opengles.servicerender");
        nn.putExtra("achievement",ACHIEVEMENT_ALL_LEAVE);
        mContext.sendBroadcast(nn);
    }
    public static int ACHIEVEMENT_ENTER = 0;
    public static int ACHIEVEMENT_RESET = 1;
    public static int ACHIEVEMENT_MONTH_ENTER = 2;
    public static int ACHIEVEMENT_YEAR_ENTER = 3;
    public static int ACHIEVEMENT_ALL_ENTER = 4;
    public static int ACHIEVEMENT_MONTH_LEAVE = 5;
    public static int ACHIEVEMENT_YEAR_LEAVE = 6;
    public static int ACHIEVEMENT_ALL_LEAVE = 7;
    public void resetAchievement(){
        Intent nn = new Intent("com.opengles.servicerender");
        nn.putExtra("achievement",ACHIEVEMENT_RESET);
        mContext.sendBroadcast(nn);
    }

    public void enterAchievement(){
        Intent nn1 = new Intent("com.opengles.servicerender");
        nn1.putExtra("achievement",ACHIEVEMENT_ENTER);
        mContext.sendBroadcast(nn1);
    }


    private GLSurfaceView mGLSurfaceView;
    private int leftPopWindow = 312 ;
    private int rightPopWindow = 712;
    private int topPopWindow = 200;
    private int bottomPopWindow = 400;
    private Animation2dRenderer mAnimation2dRenderer ;

    public Animation2dRenderer getHelloTriangleRender(){
        Log.w("test_wl",">--<getHelloTriangleRender");
        if(mAnimation2dRenderer == null)
          mAnimation2dRenderer = new Animation2dRenderer(mContext);
        return mAnimation2dRenderer;
    }

    public GLSurfaceView getGLSurfaceView(){
        Log.w("test_wl",">--<getGLSurfaceView");
        if(mGLSurfaceView == null) {
            mGLSurfaceView = new GLSurfaceView(mContext);
            mGLSurfaceView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    float x = event.getRawX();//获取触控点的坐标  ,相对于widget的左上角，getRawX是相对于屏幕的左上角
                    float y = event.getRawY();
                    Log.w("test_wl", "MotionEvent_Down:x--" + x + " y--" + y + " Function:useless");
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_DOWN:
                            if (x > leftPopWindow && x < rightPopWindow && y > topPopWindow && y < bottomPopWindow) {
                                mAnimation2dRenderer.setStartPictureMove(false);
                            }
                            break;
                        case MotionEvent.ACTION_UP:
                            break;
                        case MotionEvent.ACTION_MOVE:
                            break;
                    }
                    return false;
                }
            });
        }
        return mGLSurfaceView;
    }

    int barPercent = 0;
    public void setBarPercent(int paramBarPercent){
        if (paramBarPercent>0 && paramBarPercent<101)
            barPercent = paramBarPercent;
    }
    public int getBarPercent(){
        return barPercent;
    }

    private int openglesState = OPENGLES_STATE_START;
    private static int OPENGLES_STATE_PAUSE = 0;
    private static int OPENGLES_STATE_START = 1;
    private static int OPENGLES_STATE_OVER  = 2;
    public void startOpengles(){
        openglesState = OPENGLES_STATE_START;
    }
    public void pauseOpengles(){
        openglesState = OPENGLES_STATE_PAUSE;
    }
    public void stopOpengles(){
        openglesState = OPENGLES_STATE_OVER;
    }
    public int getOpenglesState(){
        return openglesState;
    }
}
