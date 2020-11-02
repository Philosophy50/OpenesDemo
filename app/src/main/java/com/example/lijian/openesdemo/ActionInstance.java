package com.example.lijian.openesdemo;

/**
 * Created by lijian on 2020/11/2.
 */

public class ActionInstance {
    private static  ActionInstance getActionInstance= null;   ;

    public synchronized static ActionInstance getInstance() {
        if (getActionInstance == null)
            getActionInstance = new ActionInstance();
        return getActionInstance;
    }

    boolean type1 = false;


    public void setActionType(boolean typeNum){
        type1= typeNum;
    }



    public boolean getActionTyoe(int typeNum){

        boolean jeff;
        switch (typeNum){
            case 1:
                if(type1){
                    jeff = false;
                }
                return type1;
        }
        return false;
    }
}
