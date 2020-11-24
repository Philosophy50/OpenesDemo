package com.example.lijian.openesdemo.Bezier;

import android.content.Context;
import android.opengl.GLES30;
import android.util.Log;

import com.example.lijian.openesdemo.ESUtils.ESShader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import static android.opengl.GLES20.*;

public class BezierLine {
    private final Context mContext;
    /**开始结束、控制点、t数组、Y轴偏移量*/
    private float[] mStartEndPoints;
    private float[] mControlPoints;
    private float[] mDataPoints;
    private float mAmps = 1.0f;
    /***四个句柄***/
    private int mProgram;
    private int mStartEndHandle;
    private int mControlHandle;
    private int mDataHandle;
    private int mAmpsHandle;
    /**缓存空间*/
    private FloatBuffer mBuffer;



    final int[] fboId = new int[1];
    private final int[] textureId = new int[1];
    //    private ScreenTexture mScreenTexture;

    private int mTextureId;

    private float[] mMMatrix;

    public BezierLine(Context context) {
        mContext = context;
        mProgram = ESShader.loadProgramFromAsset(mContext,"shaders/vertex_bezier.sh","shaders/frag_bezier.sh");
        //获取句柄位置
        initShader();
        //起始点和终止点
        mStartEndPoints = new float[]{
                -1, 0,
                1, 0,
        };
        //控制点
        mControlPoints = new float[]{
                0, 0.5f,
                1, 0,
        };
        //t取值数组
        mDataPoints = getTData();
        //申请空间
        mBuffer = ByteBuffer.allocateDirect ( mDataPoints.length * 4 )
                .order ( ByteOrder.nativeOrder() ).asFloatBuffer();
        mBuffer.put ( mDataPoints ).position ( 0 );

/*
        final int buffers[] = new int[1];
        GLES20.glGenBuffers(1, buffers, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, mBuffer.capacity() * Const.BYTES_PER_FLOAT,
                mBuffer, GLES20.GL_STATIC_DRAW);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        mBufferId = buffers[0];

        mBuffer = null;

        mMMatrix = new float[16];
*/
    }

    /**
     * 初始化着色器，获得句柄
     */
    private void initShader() {//初始化着色器

        mStartEndHandle = glGetUniformLocation(mProgram, "u_StartEndData");
        mControlHandle = glGetUniformLocation(mProgram, "u_ControlData");
        mAmpsHandle = glGetUniformLocation(mProgram, "u_Offset");
        mDataHandle = glGetAttribLocation(mProgram, "a_tData");
    }
    /**获取T数组*/
    @SuppressWarnings("UnnecessaryLocalVariable")
    private float[] getTData() {
        //  1---2
        //  | /
        //  3
        float[] tData = new float[Const.POINTS_PER_TRIANGLE * Const.T_DATA_SIZE * Const.NUM_POINTS];

        float step = 1f / (float) tData.length * 2f;

        for (int i = 0; i < tData.length; i += Const.POINTS_PER_TRIANGLE) {
            float t = (float) i / (float) tData.length;
            float t1 = (float) (i + 1) / (float) tData.length;
            float t2 = (float) (i + 2) / (float) tData.length;

            tData[i] = t;
            tData[i + 1] = t1;
            tData[i + 2] = t2;

        }

        return tData;
    }

    public void setAmp(float param){
        mAmps = param;
    }

    public void drawSelf(){
        Log.w("test_ww","***");
        GLES30.glEnable(GLES30.GL_BLEND);//打开混合
        GLES30.glBlendFunc(GLES30.GL_SRC_ALPHA,GLES30.GL_ONE_MINUS_SRC_ALPHA);


        GLES30.glUseProgram(mProgram);

        GLES30.glVertexAttribPointer(mDataHandle  ,1, GLES30.GL_FLOAT, false, 0, Const.BYTES_PER_FLOAT * Const.T_DATA_SIZE);
        GLES30.glEnableVertexAttribArray(mDataHandle);


        GLES30. glUniform4f(mStartEndHandle,
                mStartEndPoints[0],
                mStartEndPoints[1],
                mStartEndPoints[2],
                mStartEndPoints[3]);
        GLES30.  glUniform4f(mControlHandle,
                mControlPoints[0],
                mControlPoints[1],
                mControlPoints[2],
                mControlPoints[3]);
        GLES30. glUniform1f(mAmpsHandle, mAmps);


        GLES30.glDrawArrays ( GLES30.GL_POINTS, 0, Const.NUM_POINTS * Const.POINTS_PER_TRIANGLE );
        GLES30.glDisable(GLES30.GL_BLEND);
    }






}
