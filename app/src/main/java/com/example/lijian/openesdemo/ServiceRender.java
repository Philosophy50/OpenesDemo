package com.example.lijian.openesdemo;

import android.content.Context;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.example.lijian.openesdemo.utils.ESUtils.ESShader;

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


    private final float[] mVerticesAchiementMonth =
            {   9.8f, -14f,     //只换Y轴坐标        一开始的隐藏坐标   0,1
                //2 3 4 5
                9.8f, -6f,       //正常的显示位置
                25f,-6f,         //正常的结束位置
                //6 7 8 9
                9.8f,0f,        //被全年最佳纪录破了
                25f,0f,         //被全年最佳纪录破了
                //10 11 12 13
                9.8f,5f,        //被历史最佳纪录破了
                25f,5f,         //被历史最佳纪录破了
            };//
    private final float[] mVerticesAchiementYear =
            {       8.4f, -19f,
                    8.4f,-6f,   //正常位置
                    25f,-6f,    //正常结束位置

                    8.4f,0f,     //被历史最佳纪录破了
                    25f,0f,     //被历史最佳纪录破了
            };//
    private final float[] mVerticesAchiementAll =
            {       7.3f, -25f,
                    7.3f,-6f,
                    25f,-6f,
            };//

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

    private  int programObject2,programBezier; //TODO
    private int textureAchievementBG,textureAchievementMonth,textureAchievementYear,textureAchievementAll;
    private X2DObject mAchievementBGm,mAchievementBGy,mAchievementBGa,mAchievementMonth,mAchievementYear,mAchievementAll;
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        programObject2 =  ESShader.loadProgramFromAsset(mContext,"shaders/vertex_2d.sh", "shaders/frag_2d.sh");
        programBezier = ESShader.loadProgramFromAsset(mContext,"shaders/vertex_bezier.sh","shaders/frag_bezier.sh");//TODO
        textureAchievementBG   = ESShader.loadTextureFromAsset(mContext,"textures/c_bg.png");
        textureAchievementMonth = ESShader.loadTextureFromAsset(mContext,"textures/c_month1.png");
        textureAchievementYear  = ESShader.loadTextureFromAsset(mContext,"textures/c_year1.png");
        textureAchievementAll   = ESShader.loadTextureFromAsset(mContext,"textures/c_all1.png");
        //三个不同大小的底图片
        mAchievementBGm     = new X2DObject(7f , 5f ,  mVerticesAchiementMonth[0],mVerticesAchiementMonth[1], 200f,200f,textureAchievementBG,programObject2);
        mAchievementBGm.setisNeedZoom(false,true,false,false);
        mAchievementBGm.setDestination(mVerticesAchiementMonth[0],mVerticesAchiementMonth[1],true);
        mAchievementBGm.resetZoom(0.6f,false);
        mAchievementBGm.resetZVariation();

        mAchievementBGy     = new X2DObject(7f , 5f ,   mVerticesAchiementYear[0],mVerticesAchiementYear[1], 200f,200f,textureAchievementBG,programObject2);
        mAchievementBGy.setisNeedZoom(false,true,false,false);
        mAchievementBGy.setDestination(mVerticesAchiementYear[0],mVerticesAchiementYear[1],true);
        mAchievementBGy.resetZoom(0.8f,false);
        mAchievementBGy.resetZVariation();

        mAchievementBGa     = new X2DObject(7f , 5f ,  mVerticesAchiementAll[0],mVerticesAchiementAll[1], 200f,200f,textureAchievementBG,programObject2);
        mAchievementBGa.setisNeedZoom(false,true,false,false);
        mAchievementBGa.setDestination(mVerticesAchiementAll[0],mVerticesAchiementAll[1],true);
        mAchievementBGa.resetZoom(1f,false);
        mAchievementBGa.resetZVariation();

        //三个不同的历史记录图片
        mAchievementMonth   = new X2DObject(7f,5f,  mVerticesAchiementMonth[0],mVerticesAchiementMonth[1], 200f,200f,textureAchievementMonth,programObject2);
        mAchievementMonth.setisNeedZoom(false,true,false,false);
        mAchievementMonth.setDestination(mVerticesAchiementMonth[0],mVerticesAchiementMonth[1],true);
        mAchievementMonth.resetZoom(0.6f,false);
        mAchievementMonth.resetZVariation();
        mAchievementYear    = new X2DObject(7f,5f,  mVerticesAchiementYear[0],mVerticesAchiementYear[1], 200f,200f,textureAchievementYear,programObject2);
        mAchievementYear.setisNeedZoom(false,true,false,false);
        mAchievementYear.setDestination(mVerticesAchiementYear[0],mVerticesAchiementYear[1],true);
        mAchievementYear.resetZoom(0.8f,false);
        mAchievementYear.resetZVariation();
        mAchievementAll     = new X2DObject(7f,5f, mVerticesAchiementAll[0],mVerticesAchiementAll[1],  200f,200f,textureAchievementAll,programObject2);
        mAchievementAll.setisNeedZoom(false,true,false,false);
        mAchievementAll.setDestination(mVerticesAchiementAll[0],mVerticesAchiementAll[1],true);
        mAchievementAll.resetZoom(1f,false);
        mAchievementAll.resetZVariation();


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
                Log.w("test_wl","to-> sendreset");
             ActionInstance.getInstance().showAchievementYear();//resetAchievement();
              //  moveAway();
            }
        };
        timer.schedule(timerTask,10*1000,60*1000);

        Timer timer1 = new Timer();
        TimerTask ntk1 = new TimerTask() {
            @Override
            public void run() {
                Log.w("test_wl","to-> sendenter");
                ActionInstance.getInstance().leaveAchievementYear();//enterAchievement();
                //  moveAway();
            }
        };
        timer1.schedule(ntk1,20*1000,60*1000);
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
        GLES30.glClear ( GLES30.GL_COLOR_BUFFER_BIT );
        GLES30.glViewport ( 0, 0, mWidth, mHeight );
        if(ActionInstance.getInstance().getOpenglesState() == 1 ) {
            mAchievementBGm.drawSelf();
            mAchievementBGy.drawSelf();
            mAchievementBGa.drawSelf();

            mAchievementMonth.drawSelf();
            mAchievementYear.drawSelf();
            mAchievementAll.drawSelf();
        }



    }

    public void LeaveAchievementMonth(){
        mAchievementBGm.modifyOffset(mVerticesAchiementMonth[2],mVerticesAchiementMonth[3]);
        mAchievementBGm.setDestination(mVerticesAchiementMonth[4],mVerticesAchiementMonth[5],true);
        mAchievementMonth.modifyOffset(mVerticesAchiementMonth[2],mVerticesAchiementMonth[3]);
        mAchievementMonth.setDestination(mVerticesAchiementMonth[4],mVerticesAchiementMonth[5],true);
    }
    public void LeaveAchievementYear(){
        mAchievementBGm.modifyOffset(mVerticesAchiementMonth[6],mVerticesAchiementMonth[7]);
        mAchievementBGm.setDestination(mVerticesAchiementMonth[8],mVerticesAchiementMonth[9],true);
        mAchievementMonth.modifyOffset(mVerticesAchiementMonth[6],mVerticesAchiementMonth[7]);
        mAchievementMonth.setDestination(mVerticesAchiementMonth[8],mVerticesAchiementMonth[9],true);

        mAchievementBGy.modifyOffset(mVerticesAchiementYear[2],mVerticesAchiementYear[3]);
        mAchievementBGy.setDestination(mVerticesAchiementYear[4],mVerticesAchiementYear[5],true);
        mAchievementYear.modifyOffset(mVerticesAchiementYear[2],mVerticesAchiementYear[3]);
        mAchievementYear.setDestination(mVerticesAchiementYear[4],mVerticesAchiementYear[5],true);
    }
    public void LeaveAchievementAll(){
        mAchievementBGm.modifyOffset(mVerticesAchiementMonth[10],mVerticesAchiementMonth[11]);
        mAchievementBGm.setDestination(mVerticesAchiementMonth[12],mVerticesAchiementMonth[13],true);
        mAchievementMonth.modifyOffset(mVerticesAchiementMonth[10],mVerticesAchiementMonth[11]);
        mAchievementMonth.setDestination(mVerticesAchiementMonth[12],mVerticesAchiementMonth[13],true);

        mAchievementBGy.modifyOffset(mVerticesAchiementYear[6],mVerticesAchiementYear[7]);
        mAchievementBGy.setDestination(mVerticesAchiementYear[8],mVerticesAchiementYear[9],true);
        mAchievementYear.modifyOffset(mVerticesAchiementYear[6],mVerticesAchiementYear[7]);
        mAchievementYear.setDestination(mVerticesAchiementYear[8],mVerticesAchiementYear[9],true);


        mAchievementBGa.modifyOffset(mVerticesAchiementAll[2],mVerticesAchiementAll[3]);
        mAchievementBGa.setDestination(mVerticesAchiementAll[4],mVerticesAchiementAll[5],true);
        mAchievementAll.modifyOffset(mVerticesAchiementAll[2],mVerticesAchiementAll[3]);
        mAchievementAll.setDestination(mVerticesAchiementAll[4],mVerticesAchiementAll[5],true);
    }

    public void EnterAchievementMonth(){
        mAchievementBGm.setVariation(mVerticesAchiementMonth[0],mVerticesAchiementMonth[1]);
        mAchievementBGm.modifyOffset(mVerticesAchiementMonth[0],mVerticesAchiementMonth[1]);
        mAchievementBGm.setDestination(mVerticesAchiementMonth[2],mVerticesAchiementMonth[3],true);

        mAchievementMonth.setVariation(mVerticesAchiementMonth[0],mVerticesAchiementMonth[1]);
        mAchievementMonth.modifyOffset(mVerticesAchiementMonth[0],mVerticesAchiementMonth[1]);
        mAchievementMonth.setDestination(mVerticesAchiementMonth[2],mVerticesAchiementMonth[3],true);

    }
    public void EnterAchievementYear(){
        mAchievementBGm.setDestination(mVerticesAchiementMonth[6],mVerticesAchiementMonth[7],true);
        mAchievementMonth.setDestination(mVerticesAchiementMonth[6],mVerticesAchiementMonth[7],true);

        mAchievementBGy.setVariation(mVerticesAchiementYear[0],mVerticesAchiementYear[1]);
        mAchievementBGy.modifyOffset(mVerticesAchiementYear[0],mVerticesAchiementYear[1]);
        mAchievementBGy.setDestination(mVerticesAchiementYear[2],mVerticesAchiementYear[3],true);

        mAchievementYear.setVariation(mVerticesAchiementYear[0],mVerticesAchiementYear[1]);
        mAchievementYear.modifyOffset(mVerticesAchiementYear[0],mVerticesAchiementYear[1]);
        mAchievementYear.setDestination(mVerticesAchiementYear[2],mVerticesAchiementYear[3],true);
    }
    public void EnterAchievementAll(){
        mAchievementBGm.setDestination(mVerticesAchiementMonth[10],mVerticesAchiementMonth[11],true);
        mAchievementMonth.setDestination(mVerticesAchiementMonth[10],mVerticesAchiementMonth[11],true);
        mAchievementBGy.setDestination(mVerticesAchiementYear[6],mVerticesAchiementYear[7],true);
        mAchievementYear.setDestination(mVerticesAchiementYear[6],mVerticesAchiementYear[7],true);

        mAchievementBGa.setVariation(mVerticesAchiementAll[0],mVerticesAchiementAll[1]);
        mAchievementBGa.modifyOffset(mVerticesAchiementAll[0],mVerticesAchiementAll[1]);
        mAchievementBGa.setDestination(mVerticesAchiementAll[2],mVerticesAchiementAll[3],true);


        mAchievementAll.setVariation(mVerticesAchiementAll[0],mVerticesAchiementAll[1]);
        mAchievementAll.modifyOffset(mVerticesAchiementAll[0],mVerticesAchiementAll[1]);
        mAchievementAll.setDestination(mVerticesAchiementAll[2],mVerticesAchiementAll[3],true);
    }
    public void resetAchievement(){   //重置成就显示
        Log.i("test_wl", "to->resetAchievement: ");
        LeaveAchievementMonth();
        LeaveAchievementYear();
        LeaveAchievementAll();

    }

    public void enterAll(){
        Log.i("test_wl", "to->enterAll: ");
        EnterAchievementMonth();
        EnterAchievementYear();
        EnterAchievementAll();
    }
}
