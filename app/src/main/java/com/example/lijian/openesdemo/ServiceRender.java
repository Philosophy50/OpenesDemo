package com.example.lijian.openesdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;

import com.example.lijian.openesdemo.ESUtils.ESShader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by lijian on 2020/11/20.
 */

public class ServiceRender implements GLSurfaceView.Renderer {
    private Context mContext;
    private FloatBuffer mVertices;//传递转换好的坐标数据
    private FloatBuffer mTexCoorBuffer;//顶点纹理坐标数据缓冲
    private final float[] mVerticesData =
            { -4.0f, 4.0f, 0.0f,
                    -4.0f, -4.0f, 0.0f,
                    4.0f, -4.0f, 0.0f,
                    4.0f, -4.0f, 0.0f,
                    -4.0f, 4.0f, 0.0f,
                    4.0f,4.0f,0.0f
            };//矩形的四个角



    private final float[] texCoor = new float[]{  //纹理坐标左上角是0 , 0
            0.0f,0.0f,
            0.0f,1.0f,
            1.0f,1.0f,
            1.0f,1.0f,
            0.0f,0.0f,
            1.0f,0.0f,
    };
    public ServiceRender(Context context){
        mContext = context;

        mVertices = ByteBuffer.allocateDirect ( mVerticesData.length * 4 )  //开辟对应容量的缓冲空间
                .order ( ByteOrder.nativeOrder() )                     //设置字节顺序为本地操作系统顺序
                .asFloatBuffer();                                       //设为浮点型缓冲
        mVertices.put ( mVerticesData ).position ( 0 );                     //将数组中的顶点数据送入缓冲，设置缓冲起始位置为0




        mTexCoorBuffer = ByteBuffer.allocateDirect(texCoor.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mTexCoorBuffer.put(texCoor).position(0);


    }

    private  int programObject2;
    private int textureAchievementBG,textureAchievementMonth,textureAchievementYear,textureAchievementAll;
    private X2DObject mAchievementBGm,mAchievementBGy,mAchievementBGa,mAchievementMonth,mAchievementYear,mAchievementAll;
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        programObject2 =  ESShader.loadProgramFromAsset(mContext,"shaders/vertex_2d.sh", "shaders/frag_2d.sh");
        textureAchievementBG   = ESShader.loadTextureFromAsset(mContext,"textures/c_bg.png");
        textureAchievementMonth = ESShader.loadTextureFromAsset(mContext,"textures/c_month1.png");
        textureAchievementYear  = ESShader.loadTextureFromAsset(mContext,"textures/c_year1.png");
        textureAchievementAll   = ESShader.loadTextureFromAsset(mContext,"textures/c_all1.png");
        //三个不同大小的底图片
        mAchievementBGm     = new X2DObject(7f , 5f ,  9.8f,-14f, 200f,200f,textureAchievementBG,programObject2);
        mAchievementBGm.setisNeedZoom(false,true,false,false);
        mAchievementBGm.setDestination(9.8f,5f,true);
        mAchievementBGm.resetZoom(0.6f,false);
        mAchievementBGm.setzSSSS();

        mAchievementBGy     = new X2DObject(7f , 5f ,   8.4f,-14f, 200f,200f,textureAchievementBG,programObject2);
        mAchievementBGy.setisNeedZoom(false,true,false,false);
        mAchievementBGy.setDestination(8.4f,0f,true);
        mAchievementBGy.resetZoom(0.8f,false);
        mAchievementBGy.setzSSSS();
        mAchievementBGa     = new X2DObject(7f , 5f ,  7f,-14f, 200f,200f,textureAchievementBG,programObject2);
        mAchievementBGa.setisNeedZoom(false,true,false,false);
        mAchievementBGa.setDestination(7f,-6f,true);
        mAchievementBGa.resetZoom(1f,false);
        mAchievementBGa.setzSSSS();

        //三个不同的历史记录图片
        mAchievementMonth   = new X2DObject(7f,5f,  9.8f,-14f, 200f,200f,textureAchievementMonth,programObject2);
        mAchievementMonth.setisNeedZoom(false,true,false,false);
        mAchievementMonth.setDestination(9.8f,5f,true);
        mAchievementMonth.resetZoom(0.6f,false);
        mAchievementMonth.setzSSSS();
        mAchievementYear    = new X2DObject(7f,5f,  8.4f,-14f, 200f,200f,textureAchievementYear,programObject2);
        mAchievementYear.setisNeedZoom(false,true,false,false);
        mAchievementYear.setDestination(8.4f,0f,true);
        mAchievementYear.resetZoom(0.8f,false);
        mAchievementYear.setzSSSS();
        mAchievementAll     = new X2DObject(7f,5f,  7.3f,-14f,  200f,200f,textureAchievementAll,programObject2);
        mAchievementAll.setisNeedZoom(false,true,false,false);
        mAchievementAll.setDestination(7.3f,-6f,true);
        mAchievementAll.resetZoom(1f,false);
        mAchievementAll.setzSSSS();


        //设置可以移动
        mAchievementBGm.setisStartPictureMove(true);
        mAchievementBGy.setisStartPictureMove(true);
        mAchievementBGa.setisStartPictureMove(true);

        mAchievementMonth.setisStartPictureMove(true);
        mAchievementYear.setisStartPictureMove(true);
        mAchievementAll.setisStartPictureMove(true);

        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
             ActionInstance.getInstance().resetAchievement();
              //  moveAway();
            }
        };
        timer.schedule(timerTask,10*1000,10*1000);
    }

    private void moveAway(){

        mAchievementBGm.modifyOffset(9.8f,5f);
        mAchievementBGy.modifyOffset(8.4f,0f);
        mAchievementBGa.modifyOffset(7f,-6f);
        mAchievementMonth.modifyOffset(9.8f,5f);
        mAchievementYear.modifyOffset(8.4f,0f);
        mAchievementAll.modifyOffset(7.3f,-6f);


        mAchievementBGm.setDestination(25f,5f,true);
        mAchievementBGy.setDestination(25f,0f,true);
        mAchievementBGa.setDestination(25f,-6f,true);
        mAchievementMonth.setDestination(25f,5f,true);
        mAchievementYear.setDestination(25f,0f,true);
        mAchievementAll.setDestination(25f,-6f,true);
    }
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mWidth = width;
        mHeight = height;
    }
    private int mWidth;
    private int mHeight;
    @Override
    public void onDrawFrame(GL10 gl) {
        mAchievementBGm.drawSelf();
        mAchievementBGy.drawSelf();
        mAchievementBGa.drawSelf();

        mAchievementMonth.drawSelf();
        mAchievementYear.drawSelf();
        mAchievementAll.drawSelf();



    }

    public void resetAchievement(){

        mAchievementBGm.modifyOffset(9.8f,-14f);
        mAchievementBGy.modifyOffset(8.4f,-14f);
        mAchievementBGa.modifyOffset(7f,-14f);
        mAchievementMonth.modifyOffset(9.8f,-14f);
        mAchievementYear.modifyOffset(8.4f,-14f);
        mAchievementAll.modifyOffset(7.3f,-14f);

    }
}
