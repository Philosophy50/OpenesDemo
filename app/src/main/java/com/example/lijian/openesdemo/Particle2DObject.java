package com.example.lijian.openesdemo;

import android.opengl.GLES20;
import android.opengl.GLES30;
import android.os.SystemClock;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;



public class Particle2DObject {

    private int mTextureId;
    private int mProgramId;
    // 统一变量的句柄引用
    private int mTimeLoc;
    private int mColorLoc;
    private int mRandomLoc;
    private int mSamplerLoc;

    private float mTime = 0.0f;

    private FloatBuffer mParticles;   //粒子缓存
    private final int NUM_PARTICLES = 200;  //显示的粒子数量
    private final int PARTICLE_SIZE = 10;//每个粒子有多少个数据
    private float [] mParticleData = new float[ NUM_PARTICLES * PARTICLE_SIZE ];
    //粒子数据中的各项属性句柄位置
    private final int ATTRIBUTE_LIFETIME_LOCATION      = 0;
    private final int ATTRIBUTE_STARTPOSITION_LOCATION = 1;
    private final int ATTRIBUTE_ENDPOSITION_LOCATION   = 2;
    private final int ATTRIBUTE_COLOR_LOCATION   = 3;
    private boolean isInitShader = false;  //判断是否初始化过两个着色器

    private long mLastTime = 0;
    float [] color = new float[4];

    public Particle2DObject(int texure,int programId){
        this.mTextureId = texure;
        this.mProgramId = programId;
        initVertex();
    }
    private void initVertex(){
        for ( int i = 0; i < ( NUM_PARTICLES * PARTICLE_SIZE ); i += PARTICLE_SIZE )
        {
            // Lifetime of particle
            mParticleData[i + 0] = ( ( float ) ( ( int ) ( Math.random() * 10000 ) % 10000 ) / 10000.0f );;
            // 结束 position of particle
            mParticleData[i + 1] = (Math.random()>0.5f?-(float) Math.random(): (float) Math.random())*2;;
            mParticleData[i + 2] = (float) Math.random()*2;
            mParticleData[i + 3] =0.0f;
            if(Math.abs(mParticleData[i + 2]) < Math.pow(Math.abs(mParticleData[i + 1] ),0.8)){
                mParticleData[i + 2] = (float) Math.pow(Math.abs(mParticleData[i + 1] ),0.5);
            }
            // 开始 position of particle
            mParticleData[i + 4] = 0.0f;//( ( float ) ( ( int ) ( Math.random() * 10000 ) % 10000 ) / 40000.0f ) - 0.125f;
            mParticleData[i + 5] = -0.6f;//( ( float ) ( ( int ) ( Math.random() * 10000 ) % 10000 ) / 40000.0f ) - 0.125f;
            mParticleData[i + 6] = 0.0f;//( ( float ) ( ( int ) ( Math.random() * 10000 ) % 10000 ) / 40000.0f ) - 0.125f;


            mParticleData[i+7] = (float) Math.random();
            mParticleData[i+8] = (float) Math.random();
            mParticleData[i+9] = (float) Math.random();

        }
        mParticles = ByteBuffer.allocateDirect ( mParticleData.length * 4 )
                .order ( ByteOrder.nativeOrder() ).asFloatBuffer();
        mParticles.put ( mParticleData ).position ( 0 );
    }
    private void initShader(){
        mTimeLoc = GLES30.glGetUniformLocation ( mProgramId, "u_time" );
        mColorLoc = GLES30.glGetUniformLocation ( mProgramId, "u_color" );
        mSamplerLoc = GLES30.glGetUniformLocation ( mProgramId, "s_texture" );
        mRandomLoc = GLES30.glGetUniformLocation ( mProgramId, "s_random" );
        isInitShader = true;//初始化着色器完毕
        mTime = 0.0f;
    }


    public void setStropDraw(boolean param){
        isStopDraw = param;
    }

    boolean isStopDraw = true;

    public void drawSelf(){
        if(!ActionInstance.getInstance().getActionTyoe(2)) {
            return;
        }

        if(!isInitShader)
            initShader();
        update();

        GLES30.glEnable(GLES30.GL_BLEND);//打开混合
        GLES30.glBlendFunc(GLES30.GL_SRC_ALPHA,GLES30.GL_ONE_MINUS_SRC_ALPHA);


        GLES30.glUseProgram(mProgramId);
        for ( int i = 0; i < ( NUM_PARTICLES * PARTICLE_SIZE ); i += PARTICLE_SIZE )
        {

            mParticleData[i+7] = (float) Math.random();
            mParticleData[i+8] = (float) Math.random();
            mParticleData[i+9] = (float) Math.random();

        }

        mParticles = ByteBuffer.allocateDirect ( mParticleData.length * 4 )
                .order ( ByteOrder.nativeOrder() ).asFloatBuffer();
        mParticles.put ( mParticleData ).position ( 0 );

        mParticles.position ( 0 );
        GLES30.glVertexAttribPointer ( ATTRIBUTE_LIFETIME_LOCATION, 1, GLES30.GL_FLOAT,
                false, PARTICLE_SIZE * ( 4 ),
                mParticles );
        //[1]
        mParticles.position ( 1 );
        GLES30.glVertexAttribPointer ( ATTRIBUTE_ENDPOSITION_LOCATION, 3, GLES30.GL_FLOAT,
                false, PARTICLE_SIZE * ( 4 ),
                mParticles );
        //[4]
        mParticles.position ( 4 );
        GLES30.glVertexAttribPointer ( ATTRIBUTE_STARTPOSITION_LOCATION, 3, GLES30.GL_FLOAT,
                false, PARTICLE_SIZE * ( 4 ),
                mParticles );


        mParticles.position(7);
        GLES30.glVertexAttribPointer ( ATTRIBUTE_COLOR_LOCATION, 3, GLES30.GL_FLOAT,
                false, PARTICLE_SIZE * ( 4 ),
                mParticles );


        GLES30.glEnableVertexAttribArray ( ATTRIBUTE_LIFETIME_LOCATION );
        GLES30.glEnableVertexAttribArray ( ATTRIBUTE_ENDPOSITION_LOCATION );
        GLES30.glEnableVertexAttribArray ( ATTRIBUTE_STARTPOSITION_LOCATION );
        GLES30.glEnableVertexAttribArray ( ATTRIBUTE_COLOR_LOCATION );


        // Bind the texture
        GLES30.glActiveTexture ( GLES30.GL_TEXTURE0 );
        GLES30.glBindTexture ( GLES30.GL_TEXTURE_2D, mTextureId );

        // Set the sampler texture unit to 0 v
        GLES30.glUniform1i ( mSamplerLoc, 0 );

        GLES30.glDrawArrays ( GLES30.GL_POINTS, 0, NUM_PARTICLES );

    }

    public void setmLastTime(){
        mLastTime = 0;
    }

    private void update()
    {
        if ( mLastTime == 0 )
        {
            mLastTime = SystemClock.uptimeMillis();
        }

        long curTime = SystemClock.uptimeMillis();
        long elapsedTime = curTime - mLastTime;
        float deltaTime = elapsedTime / 1000.0f;
        mLastTime = curTime;

        mTime += deltaTime;

        GLES30.glUseProgram ( mProgramId );

        if ( mTime >= 1.0f )
        {
            //float [] centerPos = new float[3];

            mTime = 0.0f;

            ActionInstance.getInstance().setActionType(2,false);

            color[0] = (float) 1.0f;//Math.random();// 1.0f;//( ( float ) ( ( int ) ( Math.random() * 1000 ) % 10000 ) / 20000.0f ) + 0.5f;
            color[1] =(float) 0.0f;//Math.random();//  1.0f;//( ( float ) ( ( int ) ( Math.random() * 1000 ) % 10000 ) / 20000.0f ) + 0.5f;
            color[2] =(float) 0.0f;//Math.random();//  0.0f;//( ( float ) ( ( int ) ( Math.random() * 1000 ) % 10000 ) / 20000.0f ) + 0.5f;
            color[3] =  1.0f;

            GLES30.glUniform4f ( mColorLoc, color[0], color[1], color[2], color[3] );
        }

        GLES30.glUniform1f( mRandomLoc, (float) Math.random());
        GLES30.glUniform1f ( mTimeLoc, mTime );
    }
}
