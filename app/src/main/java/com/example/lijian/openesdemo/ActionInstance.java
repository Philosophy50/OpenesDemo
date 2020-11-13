package com.example.lijian.openesdemo;

/**
 * Created by lijian on 2020/11/2.
 */

public class ActionInstance {
    private static  ActionInstance getActionInstance= null;   ;


    private final int TYPE_PARTICLE_AFTER_REWARD = 2;
    private final int TYPE_NEED_RESPOND = 1;
    public synchronized static ActionInstance getInstance() {
        if (getActionInstance == null)
            getActionInstance = new ActionInstance();
        return getActionInstance;
    }

    boolean type1 = false;

    int typeNum = 0;

    boolean[] type = {false,false};
    public void setActionType(int param,boolean typeNum){
        type[param-1]= typeNum;
        this.typeNum = param;
    }



    public boolean getActionTyoe(int typeNum){

        boolean jeff;
        switch (typeNum){
            case TYPE_NEED_RESPOND:
                if(type1){
                    jeff = false;
                }
                return type[0];

            case TYPE_PARTICLE_AFTER_REWARD:
                return type[1];
        }
        return false;
    }
}
