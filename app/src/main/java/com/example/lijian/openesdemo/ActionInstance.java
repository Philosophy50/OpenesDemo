package com.example.lijian.openesdemo;

import android.app.Notification;
import android.os.SystemClock;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by lijian on 2020/11/2.
 */

public class ActionInstance {
    private static  ActionInstance getActionInstance= null;   ;


    private final int TYPE_PARTICLE_AFTER_REWARD = 2;
    private final int TYPE_NEED_RESPOND = 1;

    private final float REWARD_NUMBER_LOCAL = 60f;
    private float REWARD_NUMBER_PARAM = 0.0f;
    private boolean rewardTrigger = false;
    private boolean isRun = true;
    private int temp;
    private int conserved = 0;

    ActionInstance(){
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


    public synchronized static ActionInstance getInstance() {
        if (getActionInstance == null) {
            getActionInstance = new ActionInstance();
        }
        return getActionInstance;
    }

    boolean type1 = false;

    int typeNum = 0;

    boolean[] type = {false,false};
    public void setActionType(int param,boolean typeNum){

        type[param-1]= typeNum;
        Log.w("test_wl",this+"  typeNum:"+typeNum + "  "+type[0]+"/"+type[1]);

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
        }
        return false;
    }
}
