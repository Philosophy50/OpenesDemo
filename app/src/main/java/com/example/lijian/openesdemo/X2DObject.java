package com.example.lijian.openesdemo;

import android.opengl.GLES20;
import android.opengl.GLES30;
import android.os.SystemClock;
import android.util.Log;

import com.example.lijian.openesdemo.ESUtils.ESTransform;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by lijian on 2020/10/28.
 *
 */

public class X2DObject {
    private FloatBuffer mVertexBuffer;                 //顶点坐标数据缓冲
    private FloatBuffer mTexCoorBuffer;                //纹理坐标数据缓冲
    private ESTransform mMVPMatrix = new ESTransform();//矩阵变换
    private int mVertexPositionIndexId;     //顶点坐标位置引用id,[顶点着色器的入参]
    private int mTexCoorIndexId;            //顶点纹理坐标引用id,[顶点着色器的入参]
    private int mMVPMatrixIndexId ;         //顶点总变换矩阵引用id,[顶点着色器的入参]
    private int mTextureId;                 //纹理id
    private int mProgramId;                 //渲染程序Id
    private int apttId;                   //透明度句柄Id

    private boolean isInitShader = false;  //判断是否初始化过两个着色器



    private float x;                        //图片的  1/2  x长度
    private float y;                        //图片的  1/2  y长度
    private float x_offset;                 //起点中心的x坐标，即图片的x偏移量
    private float y_offset;                 //起点中心的y坐标，即图片的y偏移量
    private float x_destination = 0f;       //终点中心的x坐标
    private float y_destination = 0f;       //终点中心的y坐标
    private boolean isXPositive,isYPositive;//   图片向X、Y轴的移动方向
    
    
    private boolean isStay;                                  //图片结束移动是否保留
    private boolean isStartPictureMove = false;              //是否开启图片移动
    private boolean zBoolean = false;                        //是否Z轴移动
    private float zDistance = 20.0f;                        //z轴变化距离
    private float zVariation=-20f;                           /**当前z轴值*/
    private float zMax = 0.0f;                              //z轴的最高值
    private boolean isZoomUp = true;                         //图片放大or缩小
    private float minZoom = 0.1f,   maxZoom = 0.5f;          //Max、Min缩放倍数
    private float zoomMultiples = 0.1f;                      /**当前缩放值*/
    private boolean isAlphaUp = true;                        //透明度增加or减小
    private float   alphaMin = 0.1f,alphaMax = 1.1f;         //Max、Min透明度
    private boolean isLoop = false;                          //图片循环播放
    private boolean isNeedZoom = false,isNeedMove = true,isNeedRote = false,isNeedAlpha = false;
                                                            //是否开启缩放、移动、旋转、更改透明度
     private float timeUp = 4.5f*1.0f;                       /**这个就是 跑步的速度，修改*后面的值**/


    private boolean needRespond = false;
    private int respondEventNum = -1;                       //单例的响应项

    private long  mLastTime = 0;
    private float mAngle = 0.0f;        //旋转角度
    private float xVariation = 0.0f;   //x轴移动量
    private float yVariation = 0.0f;   //y轴移动量


    private boolean issmall = false;

    private final float moveRate = 500;           //调试用移动快慢的值

    ESTransform perspective = new ESTransform();//视角矩阵
    ESTransform modelview = new ESTransform(); //模型矩阵

    public void  setVariation(float param1,float param2){
        xVariation = param1;
        yVariation = param2;
    }
    /**
     *
     * @param x         图片的  1/2  x长度
     * @param y         图片的  1/2  y长度
     * @param x_offset  起点中心的x坐标，即图片的x偏移量
     * @param y_offset  起点中心的y坐标，即图片的y偏移量
     * @param picWidth  暂时不用，计划用来确定图片大小与屏幕的比例
     * @param picHeight 暂时不用，计划用来确定图片大小与屏幕的比例
     * @param texure    纹理id
     * @param programId 管线程序id
     */
    public X2DObject(float x , float y ,float x_offset,float y_offset,float picWidth,float picHeight,int texure,int programId){
        this.x = x;
        this.y = y;
        this.x_offset = x_offset;
        this.y_offset = y_offset;
        this.xVariation = x_offset;
        this.yVariation = y_offset;
        this.mTextureId = texure;
        this.mProgramId = programId;
        initVertex(picWidth,picHeight);
    }


    public  void setTexture(int param){
        mTextureId = param;
    }


    public void modifyOffset(float x_newOffset,float y_newOffset){
        x_offset = x_newOffset;
        y_offset = y_newOffset;
    }
    /**
     * 设置终点坐标
     * @param x_destination  x轴终点坐标
     * @param y_destination  y轴终点坐标
     */
    public void setDestination(float x_destination,float y_destination,boolean isStay){
        this.x_destination = x_destination;
        this.y_destination = y_destination;
        this.isStay = isStay;
        isXPositive = (this.x_destination - x_offset )>0 ;
        isYPositive = (this.y_destination - y_offset )>0;
    }

    /**
     * 初始化顶点数据，放入Buffer
     * @param picWidth  暂时不用
     * @param picHeight 暂时不用
     */
    private void initVertex(float picWidth,float picHeight){
        picWidth = picWidth*2/1024;
        picHeight = picHeight*2/600;

        float vertex[] = new float[]
        {       -x,  y, 0.0f,
                -x, -y, 0.0f,
                 x, -y, 0.0f,
                 x, -y, 0.0f,
                -x,  y, 0.0f,
                 x,  y, 0.0f
        };

        mVertexBuffer = ByteBuffer.allocateDirect ( vertex.length * 4 )  //开辟对应容量的缓冲空间
                .order ( ByteOrder.nativeOrder() )                     //设置字节顺序为本地操作系统顺序
                .asFloatBuffer()      ;                                 //设为浮点型缓冲
        mVertexBuffer.put ( vertex ).position ( 0 );

        float[] texCoor = new float[]{
                0, 0,
                0, 1,
                1,1,
                1, 1,
                0, 0,
                1, 0
        };//初始化纹理坐标数据
        mTexCoorBuffer = ByteBuffer.allocateDirect(texCoor.length*4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mTexCoorBuffer.put(texCoor).position(0);


        float aspect;
        // Compute the window aspect ratio
        aspect = ( float ) 1024 / ( float ) 600;  //屏幕比例,测试用3266,测试写死
        // Generate a perspective matrix with a 60 degree FOV
        perspective.matrixLoadIdentity();
        perspective.perspective ( 60.0f, aspect, 1.0f, 20.0f );
    }

    

    /**
     * 初始化着色器，获得句柄
     */
    private void initShader() {//初始化着色器

        mVertexPositionIndexId = GLES30.glGetAttribLocation(mProgramId, "vPosition");//获取VertexShader里的顶点位置
        mTexCoorIndexId = GLES30.glGetAttribLocation(mProgramId, "vTexCoor");//获取VertexShader里的纹理位置
        mMVPMatrixIndexId = GLES30.glGetUniformLocation(mProgramId, "uMVPMatrix");//获取VertexShader里的uniform变换矩阵
        apttId = GLES30.glGetUniformLocation(mProgramId,"aptt");
        isInitShader = true;//初始化着色器完毕
    }
    private boolean isStop=false;
    private float  changeAlpha = 1.0f;

    public void manualStop(boolean param){
        isStop = param;
    }

    public boolean getStopState(){
        return isStop;
    }
    public boolean getNeedRespond(){return needRespond;}
    boolean daoweile = true;
    public void drawSelf(){

        if(isStop){
            if(needRespond) {
                daoweile = true;
                setVariation(x_offset, y_offset);
                isStop = false;   //转一下停止绘图的判断
            }
            return;
        }
        if( needRespond   ){
            if( (!ActionInstance.getInstance().getActionTyoe(respondEventNum)) ) {
                if (daoweile)
                    return;
            }
        }
        daoweile = false;
        if(!isInitShader)
            initShader();
        update();

        GLES30.glEnable(GLES30.GL_BLEND);//打开混合
        GLES30.glBlendFunc(GLES30.GL_SRC_ALPHA,GLES30.GL_ONE_MINUS_SRC_ALPHA);


        GLES30.glUseProgram(mProgramId);
        GLES30.glVertexAttribPointer(mVertexPositionIndexId,3,GLES30.GL_FLOAT,false,0,mVertexBuffer);//声明顶点位置画笔
        GLES30.glVertexAttribPointer(mTexCoorIndexId,2,GLES30.GL_FLOAT,false,0,mTexCoorBuffer);      //声明纹理位置画笔

        GLES30.glEnableVertexAttribArray(mVertexPositionIndexId);//启用顶点位置数组
        GLES30.glEnableVertexAttribArray(mTexCoorIndexId);      //启用纹理位置数组
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);              //启用纹理编号0
        GLES30.glBindTexture(GLES20.GL_TEXTURE_2D,mTextureId);   //绑定纹理图片

        GLES30.glUniform1f(apttId,changeAlpha);
        GLES30.glUniformMatrix4fv ( mMVPMatrixIndexId, 1, false, mMVPMatrix.get(),0);//getAsFloatBuffer() );
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, 6);//6 画顶点数量

        GLES30.glDisable(GLES30.GL_BLEND);

    }


    public void setisNeedZoom(boolean isZoom,boolean isMove,boolean isRote,boolean isAlpha){
        isNeedZoom = isZoom;
        isNeedMove = isMove;
        isNeedRote = isRote;
        isNeedAlpha = isAlpha;
    }


    public void resetZoom(float paramfZoom,boolean isZoomUp){
        zoomMultiples = paramfZoom;
        this.isZoomUp = isZoomUp;
    }

    public void setZoomValue (float minZoom, float maxZoom){
         this.minZoom = minZoom;
        this.maxZoom = maxZoom;
    }

    public void setAlphaUp(boolean param){
        changeAlpha = ((isAlphaUp = param  )? alphaMin:alphaMax);
    }

    public void setAlphaValue(float minParam ,float maxParam){
        alphaMin = minParam;
        alphaMax = maxParam;
    }



    public void setsmall(boolean param){
        issmall = param;
    }

    public void setLoop(boolean param){
        isLoop = param;
    }

    private void update()   //计算矩阵
    {
        if ( mLastTime == 0 )
        {
            mLastTime = SystemClock.uptimeMillis();
        }

        long curTime = SystemClock.uptimeMillis();
        long elapsedTime = curTime - mLastTime;
        float deltaTime = (float)elapsedTime / 1000.0f;
        mLastTime = curTime;



        // Compute a rotation angle based on time to rotate the cube
        if(isStartPictureMove) {


            if(isNeedAlpha) {

                 if (isAlphaUp  ) {
                    if(changeAlpha<alphaMax) {
                        changeAlpha += 0.01f;
                    }
                 } else {
                     if(changeAlpha>alphaMin )
                         changeAlpha -= 0.01f;
                 }
            }
            if(needDelayMove){  //延迟移动
                if(delayRecordTime == 0){
                    delayRecordTime = SystemClock.elapsedRealtime();
                }
                if(  SystemClock.elapsedRealtime() - delayRecordTime > delayMoveTime){
                    isNeedMove = true;
                }else{
                    isNeedMove = false;
                }
            }
            if(isNeedMove) {
                xVariation +=    (x_destination - x_offset)/(moveRate)  *timeUp;
                yVariation +=   (y_destination-y_offset)/(moveRate)   *timeUp;
                if(zBoolean && zVariation<=zMax) {
                      zVariation += (zDistance / ((moveRate) * 1.78)) * timeUp;
                }

                if(isXPositive && xVariation >= x_destination ) {//TODO 精度
                    if(isYPositive && yVariation >= y_destination){
                        //  isStop = true;
                        if(isStay) {
                            xVariation = x_destination;
                            yVariation = y_destination;
                        }else{
                            if(needScore){
                                ActionInstance.getInstance().setActionType(1,false);
                                ActionInstance.getInstance().addScoreNum();
                            }
                            if(showLandMark){
                                ActionInstance.getInstance().setActionType(3,true);
                            }
                            isStop = true;
                        }

                        if(isLoop){
                            xVariation = x_offset;  //循环
                            yVariation = y_offset;
                            zoomMultiples = minZoom;
                             zVariation = -20.0f;

                        }
                    }else if(!isYPositive && yVariation <= y_destination){
                        if(isStay) {
                            xVariation = x_destination;
                            yVariation = y_destination;
                        }else{
                            if(needScore){
                                ActionInstance.getInstance().setActionType(1,false);
                                ActionInstance.getInstance().addScoreNum();

                              }
                              if(showLandMark){
                                ActionInstance.getInstance().setActionType(3,true);
                            }
                            isStop = true;

                        }

                        if(isLoop){
                            xVariation = x_offset;//循环
                             yVariation = y_offset;
                            zoomMultiples = minZoom;
                                zVariation = -20.0f;

                        }
                    }
                }else if(!isXPositive && xVariation <= x_destination){ //TODO 精度
                    if(isYPositive && yVariation >= y_destination){
                        if(isStay) {
                            xVariation = x_destination;
                            yVariation = y_destination;
                        }else{
                            if(needScore){
                                ActionInstance.getInstance().setActionType(1,false);

                                ActionInstance.getInstance().addScoreNum();
                            }
                            if(showLandMark){
                                ActionInstance.getInstance().setActionType(3,true);
                            }
                            isStop = true;

                        }

                        if(isLoop){
                            xVariation = x_offset;//循环
                            yVariation = y_offset;
                            zoomMultiples = minZoom;
                            zVariation = -20.0f;
                        }
                        // xVariation = x_offset;
                        //yVariation = y_offset;
                    }else if(!isYPositive && yVariation <= y_destination){

                        if(isStay) {
                            xVariation = x_destination;
                            yVariation = y_destination;

                            if(rewardend){
                                resetZoom(0.3f,false);
                                minZoom = 0.25f;
                                rewardend = false;
                                rewardtime ++;
                            }

                        }else{
                            if(needScore){
                                ActionInstance.getInstance().setActionType(1,false);

                                ActionInstance.getInstance().addScoreNum();
                            }
                            if(showLandMark){
                                ActionInstance.getInstance().setActionType(3,true);
                            }
                            isStop = true;

                        }

                        if(isLoop){
                            xVariation = x_offset;//循环
                            yVariation = y_offset;
                            zoomMultiples = minZoom;
                                zVariation = -20.0f;

                        }
                        //xVariation = x_offset;
                        //yVariation = y_offset;
                    }
                }

            }

            if(isNeedZoom){
                if(isZoomUp){
                    if (zoomMultiples <= maxZoom) {
                        zoomMultiples += (0.005f) *timeUp;
                        if(issmall && (zoomMultiples>=maxZoom)){
                            isZoomUp = false;
                        }
                    }

                    if(rewardtime>0){
                        resetZoom(0.3f,false);
                        minZoom = 0.25f;
                        rewardtime++;
                    }
                }else{
                    if (zoomMultiples >= minZoom) {
                        zoomMultiples -= 0.01f;
                    }else{

                        if(rewardtime >0 && rewardtime <4){
                            resetZoom(0.25f,true);
                            maxZoom = 0.3f;
                        }else if(rewardtime>= 4) {
                            minZoom = 0.001f;
                            rewardtime = -1;
                        }
                        else if (rewardtime == -1) {
                            ActionInstance.getInstance().setActionType(2, true);
                            rewardtime = 0;
                            isStop = true;
                        }else{
                            isStop = true;
                            ActionInstance.getInstance().setActionType(1,true);
                        }
                        /*
                        else{
                            if(rewardtime == -1) {
                                ActionInstance.getInstance().setActionType(2, true);
                                rewardtime = 0;

                            }
                            ActionInstance.getInstance().setActionType(1,true);
                            isStop = true;
                            //isStop = true;
                            Log.w("test_wl","issstop 5");


                        }
                        */
                    }
                }
            }

        }else{
            return;
        }

        if(isNeedRote) {
            mAngle += (deltaTime * 40.0f);
            if (mAngle >= 360.0f) {
                mAngle -= 360.0f;
            }
        }


        // Generate a model view matrix to rotate/translate the cube
        modelview.matrixLoadIdentity();
        // Translate away from the viewer
        modelview.translate(xVariation, yVariation, zVariation);//-20.0f);

        // Rotate 暂时关闭
        if(isNeedRote) {
            modelview.rotate(mAngle, 0.0f, 0.0f, 1.0f);
        }


        modelview.scale(zoomMultiples,zoomMultiples,0.3f);//0.3f);//zoomMultiples);

        // Compute the final MVP by multiplying the
        // modevleiw and perspective matrices together
        mMVPMatrix.matrixMultiply ( modelview.get(), perspective.get());
    }

    public void setisStartPictureMove(boolean param){
        isStartPictureMove = param;
    }

    boolean pramA = false;
    public void setParamA(){
        pramA = true;
    }

    public void setRespondEvent(int param){
        needRespond = true;
        respondEventNum = param;
    }


    public void setZscale(float zDis,float timeup){
        zDistance = zDis;
        zBoolean = true;
        timeUp = timeup;
    }

    public void setTimeUp(float param){
        timeUp = param;
    }

    public void setZscale(float zDis,float timeup,float Zmax){
        zDistance = zDis;
        zBoolean = true;
        timeUp = timeup;
        zMax = Zmax;
    }
    public void setzSSSS(){
        zVariation = -20f;
    }

    private boolean rewardend = false;
    private int rewardtime = 0;

    public void setRewardEnd(){
        rewardend = true;
    }

    private boolean needDelayMove = false;
    private int delayMoveTime = 0;
    private long delayRecordTime = 0;

    public void setDeylayMove(int seconds){
        needDelayMove = true;
        delayMoveTime = seconds * 100;
    }

    private boolean needScore = false;

    public  void setIsNeedScore (){
        needScore = true;
    }


    private boolean showLandMark = false;

    public void showLandMark(){
        showLandMark = true;
    }
}


