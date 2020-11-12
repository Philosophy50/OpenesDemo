package com.example.lijian.openesdemo;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.os.SystemClock;
import android.util.Log;

import com.example.lijian.openesdemo.ESUtils.ESShader;
import com.example.lijian.openesdemo.ESUtils.ESTransform;
import com.example.lijian.openesdemo.String2bitmap.BitmapUtil;
import com.example.lijian.openesdemo.String2bitmap.StringBitmapParameter;


public class HelloTriangleRenderer implements GLSurfaceView.Renderer
{
   // Member variables
   private Context mContext;
   private int mProgramObject;
   private int mWidth;
   private int mHeight;
   private FloatBuffer mVertices;//传递转换好的坐标数据
   private FloatBuffer mTexCoorBuffer;//顶点纹理坐标数据缓冲


   private int verticesIndex;
   private int textureIndex;

   private static String TAG = "HelloTriangleRenderer";
   private final float[] mVerticesData =
           { -4.0f, 4.0f, 0.0f,
                   -4.0f, -4.0f, 0.0f,
                   4.0f, -4.0f, 0.0f,
                   4.0f, -4.0f, 0.0f,
                   -4.0f, 4.0f, 0.0f,
                   4.0f,4.0f,0.0f
           };//矩形的四个角
           /*
           { 0.0f, 2.0f, 0.0f,
                   -1f, -1f, 0.0f,
                   1f, -1f, 0.0f };//三角形的三个角
         */


   private final float[] texCoor = new float[]{  //纹理坐标左上角是0 , 0
          0.0f,0.0f,
           0.0f,1.0f,
          1.0f,1.0f,
           1.0f,1.0f,
           0.0f,0.0f,
           1.0f,0.0f,
   };


   private X2DObject x2DObject,x2DObject1,x2DObject2,x2DObject3,x2Dobject4,x2Dobject5,x2Dobject6;
   private X2DObject mPopWindow,mPopWindowLight,mPopWinodwAtom,mPopReward;

   private X2DObject mTreeLeft_1,mTreeRight_1,mTreeLeft_2,mTreeLeft_3,mTreeLeft_4;
   private X2DObject mBackground,mProgressBar,mTextHint,mProgressNum2,mScoreBar;
   private X2DObject mTensNum,mOnesNum;
   private Load2DObject mProgress;
   private  Particle2DObject mParticle;

   public HelloTriangleRenderer ( Context context )
   {
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

   private int LoadShader ( int type, String shaderSrc )
   {
      int shader;
      int[] compiled = new int[1];
      // Create the shader object
      shader = GLES30.glCreateShader ( type );
      if ( shader == 0 )
      {
         return 0;
      }
      // Load the shader source 将源码连接到对应着色器对象
      GLES30.glShaderSource ( shader, shaderSrc );
      // Compile the shader  编译着色器对象
      GLES30.glCompileShader ( shader );
      // Check the compile status 检查编译结果
      GLES30.glGetShaderiv ( shader, GLES30.GL_COMPILE_STATUS, compiled, 0 );
      if ( compiled[0] == 0 )
      {
         Log.e ( TAG, GLES30.glGetShaderInfoLog ( shader ) );
         GLES30.glDeleteShader ( shader );
         return 0;
      }
      return shader;
   }
   private  int textureR,textureB,textureG,textureP,textureY,programObject2,programLoadingObject,programParticle;
   private  int textureOnes,textureTens;//个位贴图，百位贴图
   private  int textureCity,textureLight,textureAtom,textureTree,textureBackgound,textureBar,textureProgress;
   private int textureJuice;
   private int texturebitmap,textureParticle,textureScoreBar;

   private  int textureNum0,textureNum1,textureNum2,textureNum3,textureNum4,textureNum5;
   private int textureNum6,textureNum7,textureNum8,textureNum9;
   ///
   // Initialize the shader and program object
   //
   StringBitmapParameter sbp1 ;

   public void onSurfaceCreated ( GL10 glUnused, EGLConfig config )
   {

      String vShaderStr =
         "#version 300 es 			  \n"
         +   "in vec4 vPosition;           \n"          //输入向量
         +  "in vec2 a_texCoord;            \n"          //纹理向量
                 +"out vec2 v_texCoord;   \n"
                 +  "uniform mat4 u_mvpMatrix;      \n"
         + "void main()                  \n"
         + "{                            \n"
         + " gl_Position = u_mvpMatrix * vPosition;  \n"
         + " v_texCoord = a_texCoord;  \n"             //纹理向量赋值输出
         + "}                            \n";

      String fShaderStr =
         "#version 300 es		 			          	\n"
         + "precision mediump float;					  	\n"  //fragment Shader里必须指明Float精度      //声明着色器版本es3.0
                 +"in vec2  v_texCoord;                        \n"     //片元着色器纹理输入向量
                 + "out vec4 fragColor;	 			 		  	\n"      //输出向量
                 +" uniform sampler2D s_baseMap;                 \n"   //2D纹理贴图变量
         + "void main()                                  \n"
         + "{                                            \n"
         + "  vec4 baseColor ;                            \n"
         + "  baseColor = texture( s_baseMap, v_texCoord );                \n"
         + "  fragColor = baseColor;	\n"//rgba  vec4 ( 1.0, 0.0, 0.0, 1.0 );
         + "}                                            \n";
      //创建顶点着色器对象和片元着色器对象
      int vertexShader;
      int fragmentShader;
      int[] linked = new int[1];
      // Load the vertex/fragment shaders 在函数中将着色语言源码加载到顶点着色器和片元着色器
      vertexShader = LoadShader ( GLES30.GL_VERTEX_SHADER, vShaderStr );
      fragmentShader = LoadShader ( GLES30.GL_FRAGMENT_SHADER, fShaderStr );
      // Create the program object创建程序对象
      int programObject;
      /*********TODO***********/


      programObject2 =  ESShader.loadProgramFromAsset(mContext,"shaders/vertex_2d.sh", "shaders/frag_2d.sh");
      programLoadingObject = ESShader.loadProgramFromAsset(mContext,"shaders/vertex_load2d.sh","shaders/frag_load2d.sh");
      programParticle = ESShader.loadProgramFromAsset(mContext,"shaders/vertex_particle.sh","shaders/frag_particle.sh");
      textureR   = ESShader.loadTextureFromAsset(mContext,"textures/b_star.png");//circle_red.png");
      textureB   = ESShader.loadTextureFromAsset(mContext,"textures/b_star.png");//ccircle_blue.png");
      textureG   = ESShader.loadTextureFromAsset(mContext,"textures/b_star.png");//ccircle_green.png");
      textureP   = ESShader.loadTextureFromAsset(mContext,"textures/b_star.png");//ccircle_purple.png");
      textureY   = ESShader.loadTextureFromAsset(mContext,"textures/b_star.png");//ccircle_yellow.png");
      textureCity  = ESShader.loadTextureFromAssetAlpha(mContext,"textures/a_city.png");
      textureLight = ESShader.loadTextureFromAssetAlpha(mContext,"textures/a_rotatinglight.png");
      textureAtom  = ESShader.loadTextureFromAssetAlpha(mContext,"textures/a_atomization.png");
      textureTree  = ESShader.loadTextureFromAssetAlpha(mContext,"textures/a_tree.png");
      textureBackgound = ESShader.loadTextureFromAssetAlpha(mContext,"textures/b_bg.png");
      textureBar = ESShader.loadTextureFromAssetAlpha(mContext,"textures/b_progressbar.png");
      textureScoreBar = ESShader.loadTextureFromAssetAlpha(mContext,"textures/b_scorebar.png");
      textureProgress = ESShader.loadTextureFromAssetAlpha(mContext,"textures/b_bar.png");
      textureJuice = ESShader.loadTextureFromAssetAlpha(mContext,"textures/a_city.png");

      textureNum0 = ESShader.loadTextureFromAssetAlpha(mContext,"textures/b_numa0.png");
      textureNum1 = ESShader.loadTextureFromAssetAlpha(mContext,"textures/b_numa1.png");
      textureNum2 = ESShader.loadTextureFromAssetAlpha(mContext,"textures/b_numa2.png");
      textureNum3 = ESShader.loadTextureFromAssetAlpha(mContext,"textures/b_numa3.png");
      textureNum4 = ESShader.loadTextureFromAssetAlpha(mContext,"textures/b_numa4.png");
      textureNum5 = ESShader.loadTextureFromAssetAlpha(mContext,"textures/b_numa5.png");
      textureNum6 = ESShader.loadTextureFromAssetAlpha(mContext,"textures/b_numa6.png");
      textureNum7 = ESShader.loadTextureFromAssetAlpha(mContext,"textures/b_numa7.png");
      textureNum8 = ESShader.loadTextureFromAssetAlpha(mContext,"textures/b_numa8.png");
      textureNum9 = ESShader.loadTextureFromAssetAlpha(mContext,"textures/b_numa9.png");


      textureParticle = ESShader.loadTextureFromAsset(mContext,"textures/smoke.png");



      ArrayList<StringBitmapParameter> pp = new ArrayList<>();
      sbp1 = new StringBitmapParameter("如果速度是8,则1小时后获得2点能量");
      pp .add(sbp1);
      texturebitmap = ESShader.loadTextureFromBitmap(mContext,BitmapUtil.StringListtoBitmap(mContext,pp));


      mBackground = new X2DObject(256f,140f,0f,0f,200f,200f,textureBackgound,programObject2);
      mBackground.setisNeedZoom(false,false,false,false);
      mBackground. setzSSSS();

      mProgressBar = new X2DObject(56f,10f,  -4f,10f, 200f,200f,textureBar,programObject2);
      mProgressBar.setisNeedZoom(false,false,false,false);
      mProgressBar. setzSSSS();

      mScoreBar = new X2DObject(33.6f,10f,9.0f,10f,200f,200f,textureScoreBar,programObject2);
      mScoreBar.setisNeedZoom(false,false,false,false);
      mScoreBar. setzSSSS();

      mTensNum = new  X2DObject(2.0f,3.0f,  11.5f,10f,200f,200f,textureNum0,programObject2);
      mTensNum.setisNeedZoom(false,false,false,false);
      mTensNum. setzSSSS();

      mOnesNum = new   X2DObject(2.0f,3.0f,  12f,10f,200f,200f,textureNum0,programObject2);
      mOnesNum.setisNeedZoom(false,false,false,false);
      mOnesNum. setzSSSS();

      mTextHint = new X2DObject(56f,4f,  -7f,8f, 200f,200f,texturebitmap,programObject2);
      mTextHint.setisNeedZoom(false,false,false,false);
      mTextHint. setzSSSS();
//12
      mProgress = new Load2DObject(32f,2f,  -4f,10f, 200f,200f,textureProgress,programLoadingObject);
      mProgress.setisNeedZoom(false,false,false,false);
      mProgress. setzSSSS();
      mProgress.setParamA();

      mParticle = new Particle2DObject(textureParticle,programParticle);



      mTreeLeft_1 = new X2DObject(2f,3f,          -4f,-0.5f,         200f,200f,textureTree,programObject2);
      mTreeLeft_1.setDestination(-14.5f,-4.5f,true);
      mTreeLeft_1.setisNeedZoom(true,true,false,false);
      mTreeLeft_1.setZoomValue(0.1f,2.0f);
      mTreeLeft_1.resetZoom(0.1f,true);
      mTreeLeft_1.setLoop(true);
      mTreeLeft_1.setZscale(20.0f,4.5f);

      mTreeLeft_2 = new X2DObject(2f,3f,          -4f,-0.5f,         200f,200f,textureTree,programObject2);
      mTreeLeft_2.setDestination(-14.5f,-4.5f,true);
      mTreeLeft_2.setisNeedZoom(true,true,false,false);
      mTreeLeft_2.setZoomValue(0.1f,2.0f);
      mTreeLeft_2.resetZoom(0.1f,true);
      mTreeLeft_2.setLoop(true);
      mTreeLeft_2.setZscale(20.0f,4.5f);

      mTreeLeft_3 = new X2DObject(2f,3f,          -4f,-0.5f,         200f,200f,textureTree,programObject2);
      mTreeLeft_3.setDestination(-14.5f,-4.5f,true);
      mTreeLeft_3.setisNeedZoom(true,true,false,false);
      mTreeLeft_3.setZoomValue(0.1f,2.0f);
      mTreeLeft_3.resetZoom(0.1f,true);
      mTreeLeft_3.setLoop(true);
      mTreeLeft_3.setZscale(20.0f,4.5f);

      mTreeLeft_4 = new X2DObject(2f,3f,          -4f,-0.5f,         200f,200f,textureTree,programObject2);
      mTreeLeft_4.setDestination(-14.5f,-4.5f,true);
      mTreeLeft_4.setisNeedZoom(true,true,false,false);
      mTreeLeft_4.setZoomValue(0.1f,2.0f);
      mTreeLeft_4.resetZoom(0.1f,true);
      mTreeLeft_4.setLoop(true);
      mTreeLeft_4.setZscale(20.0f,4.5f);



      mTreeRight_1 = new X2DObject(2f,3f,          5f,-0.5f,         200f,200f,textureTree,programObject2);
      mTreeRight_1.setDestination(15.5f,-4.5f,true);
      mTreeRight_1.setisNeedZoom(true,true,false,false);
      mTreeRight_1.setZoomValue(0.1f,2.0f);
      mTreeRight_1.resetZoom(0.1f,true);
      mTreeRight_1.setLoop(true);
      mTreeRight_1.setZscale(20.0f,4.5f);


      x2DObject =   new X2DObject(10f,10f,   -2f,7f,   200f,200f,textureR,programObject2);
      x2DObject.setDestination(7f,10f,false);
      x2DObject.setTimeUp(9f);
      x2DObject.setRespondEvent(1);
      x2DObject1=  new X2DObject (7f,7f,     -2f,-4f,  200f,200f,textureB,programObject2);
      x2DObject1.setDestination(7f,10f,false);
      x2DObject1.setRespondEvent(1);
      x2DObject1.setTimeUp(9f);
      x2DObject2 = new X2DObject(8f,8f,      2f,8f,         200f,200f,textureG,programObject2);
      x2DObject2.setDestination(7f,10f,false);
      x2DObject2.setRespondEvent(1);
      x2DObject2.setTimeUp(9f);

      x2DObject3  = new X2DObject(8f,8f,     2f,0f,         200f,200f,textureLight,programObject2);
      x2DObject3.setRespondEvent(1);
      x2DObject3.setDestination(7f,10f,false);
      x2DObject3.setTimeUp(9f);

      x2Dobject4  = new X2DObject(8f,8f    ,5f,5f,         200f,200f,textureY,programObject2);
      x2Dobject4.setDestination(7f,10f,false);
      x2Dobject4.setRespondEvent(1);
      x2Dobject4.setTimeUp(9f);

      x2Dobject5  = new X2DObject(8f,8f ,   -5f,5f,         200f,200f,textureR,programObject2);
      x2Dobject5.setDestination(7f,10f,false);
      x2Dobject5.setRespondEvent(1);
      x2Dobject5.setTimeUp(9f);

      x2Dobject6  = new X2DObject(8f,8f,     -1f,-1f,         200f,200f,textureB,programObject2);
      x2Dobject6.setDestination(7f,10f,false);
      x2Dobject6.setRespondEvent(1);
      x2Dobject6.setTimeUp(9f);

      mPopReward = new X2DObject(7f,7f,  0f,-2f ,    200f,200f, textureCity,programObject2);
      mPopReward.setDestination(0f,-7f,true);
      mPopReward.setZoomValue(0.1f,0.5f);
      mPopReward.setisNeedZoom(true,true,false,false);
      mPopReward.setZscale(15.0f,1.0f,-19.0f);
      mPopReward.setisStartPictureMove(true);
      mPopReward.setRewardEnd();







      mPopWindow = new X2DObject(7f,7f,  0f,-2f ,    200f,200f, textureCity,programObject2);
      mPopWindow.setDestination(0f,-7f,true);
      mPopWindow.setZoomValue(0.1f,0.5f);
      mPopWindow.setisNeedZoom(true,true,false,false);
      mPopWindow.setZscale(15.0f,1.0f,-19.0f);


      mPopWindowLight = new  X2DObject(8f,8f,  0f,0f ,    200f,200f, textureLight,programObject2);
      mPopWindowLight.setisNeedZoom(true,false,false,false);
      mPopWindowLight.manualStop(true);

      mPopWinodwAtom = new X2DObject(12f,12f,  0f,0f, 200f,200f, textureAtom,programObject2);
      mPopWinodwAtom.setisNeedZoom(true,false,false,false);
      mPopWinodwAtom.manualStop(true);

      //      mPopWinodwAtom = new  X2DObject(7f,7f,  0f,0f ,    200f,200f, textureB,textureAtom);

      /*********TODO***********/



      programObject = GLES30.glCreateProgram();

      if ( programObject == 0 )
      {
         return;
      }
      //将着色器对象连接到程序对象
      GLES30.glAttachShader ( programObject, vertexShader );
      GLES30.glAttachShader ( programObject, fragmentShader );


      // Bind vPosition to attribute 0 将着色语言中的变量与 程序对象的index绑定
      /*
      GLES30.glBindAttribLocation ( programObject, 0, "vPosition" );
      GLES30.glBindAttribLocation ( programObject, 1, "a_texCoord" );
*/
      // Link the program 链接程序对象
      GLES30.glLinkProgram ( programObject );

      // Check the link status
      GLES30.glGetProgramiv ( programObject, GLES30.GL_LINK_STATUS, linked, 0 );

      if ( linked[0] == 0 )
      {
         Log.e ( TAG, "Error linking program:" );
         Log.e ( TAG, GLES30.glGetProgramInfoLog ( programObject ) );
         GLES30.glDeleteProgram ( programObject );
         return;
      }

      // Store the program object
      mProgramObject = programObject;
      //获取程序中的句柄
      mBaseMapLoc = GLES30.glGetUniformLocation ( mProgramObject, "s_baseMap" );
      mMVPLoc = GLES30.glGetUniformLocation ( mProgramObject, "u_mvpMatrix" );

      //获取程序中顶点位置属性引用
      verticesIndex = GLES30.glGetAttribLocation(mProgramObject, "vPosition");
      //获取程序中顶点纹理坐标属性引用
      textureIndex= GLES30.glGetAttribLocation(mProgramObject, "a_texCoord");

      mBaseMapTexId = loadTextureFromAsset ( "textures/basemap.png" );
      GLES30.glClearColor ( 1.0f, 1.0f, 1.0f, 0.0f );

      mBackground.setisStartPictureMove(true);
      mProgressBar.setisStartPictureMove(true);
      mTextHint.setisStartPictureMove(true);
      mScoreBar.setisStartPictureMove(true);
      mProgress.setisStartPictureMove(true);
      mTensNum.setisStartPictureMove(true);
        mOnesNum.setisStartPictureMove(true);
   }

   // /
   // Draw a triangle using the shader pair created in onSurfaceCreated()
   //
   public void onDrawFrame ( GL10 glUnused )
   {
      //update();
      // Set the viewport 视口定义了所有渲染操作最终显示的2D矩形  原点坐标0,0 ,宽,高
      GLES30.glViewport ( 0, 0, mWidth, mHeight );

      // Clear the color buffer 清除颜色缓冲区
      GLES30.glClear ( GLES30.GL_COLOR_BUFFER_BIT );


      // Use the program object
      GLES30.glUseProgram ( mProgramObject );

      // Load the vertex data 将加载三角形数组坐标数据的Buffer传入管线，但在这之前，需要按照顶点的次序依次将属性传入缓冲，即mVertices的赋值过程
      GLES30.glVertexAttribPointer ( verticesIndex, 3, GLES30.GL_FLOAT, false, 0, mVertices );
                                    /* 3代表每个顶点的组件数量（即xyz），false表示不转规格化，0表示相邻顶点的数据间隔*/
      GLES30.glVertexAttribPointer( textureIndex, 2,GLES30.GL_FLOAT,false,0, mTexCoorBuffer );

      //通过顶点位置属性启用顶点位置数据
      GLES30.glEnableVertexAttribArray ( verticesIndex );
      GLES30.glEnableVertexAttribArray ( textureIndex );

      // Bind the base map
      GLES30.glActiveTexture ( GLES30.GL_TEXTURE1 );
      GLES30.glBindTexture ( GLES30.GL_TEXTURE_2D, mBaseMapTexId );
      // Set the base map sampler to texture unit to 0
      GLES30.glUniform1i ( mBaseMapLoc, 0 );



              // Load the MVP matrix
      GLES30.glUniformMatrix4fv ( mMVPLoc, 1, false,
              mMVPMatrix.getAsFloatBuffer() );
      //通知绘制图元



   //越在前，越在下层
      mBackground.drawSelf();
      mProgressBar.drawSelf();
      mScoreBar.drawSelf();
      mTensNum.drawSelf();
      mOnesNum.drawSelf();
      mTextHint.drawSelf();
      mProgress.drawSelf();
      mTreeLeft_4.drawSelf();
      mTreeLeft_3.drawSelf();
      mTreeLeft_2.drawSelf();
      mTreeLeft_1.drawSelf();
      mTreeRight_1.drawSelf();


      mPopWindowLight.drawSelf();
      mPopWindow.drawSelf();
      mPopWinodwAtom.drawSelf();

      mPopReward.drawSelf();

      x2DObject.drawSelf();
      x2DObject1.drawSelf();
      x2DObject2.drawSelf();
      x2DObject3.drawSelf();
      x2Dobject4.drawSelf();
      x2Dobject5.drawSelf();
      x2Dobject6.drawSelf();
      //GLES30.glDrawArrays ( GLES30.GL_TRIANGLE_STRIP, 0, 6 );

      mParticle.drawSelf();
   }

   // /
   // Handle surface changes
   //
   public void onSurfaceChanged ( GL10 glUnused, int width, int height )
   {
      Log.w("test_wl","ffh:"+width+"/"+height);
      mWidth = width;
      mHeight = height;
   }

   private ESTransform mMVPMatrix = new ESTransform();
   // Uniform locations
   private int mMVPLoc;
   private int mBaseMapLoc;
   // Texture handle
   private int mBaseMapTexId;



   private int loadTextureFromAsset ( String fileName )
   {
      int[] textureId = new int[1];
      Bitmap bitmap = null;
      InputStream is = null;

      try
      {
         is = mContext.getAssets().open ( fileName );
      }
      catch ( IOException ioe )
      {
         is = null;
      }

      if ( is == null )
      {
         return 0;
      }

      bitmap = BitmapFactory.decodeStream ( is );

      GLES30.glGenTextures ( 1, textureId, 0 );
      GLES30.glBindTexture ( GLES30.GL_TEXTURE_2D, textureId[0] );

      GLUtils.texImage2D ( GLES30.GL_TEXTURE_2D, 0, bitmap, 0 );

      GLES30.glTexParameteri ( GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR );
      GLES30.glTexParameteri ( GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR );
      GLES30.glTexParameteri ( GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE );
      GLES30.glTexParameteri ( GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE );

      bitmap.recycle();//释放图像资源
      return textureId[0];
   }

   private int touchTime = 0;
   public void setStartPictureMove(boolean param){
      switch (touchTime){
         case 0 :
          //  x2DObject.setisStartPictureMove(true);
            x2DObject1.setisStartPictureMove(true);
            x2DObject2.setisStartPictureMove(true);
            x2DObject3.setisStartPictureMove(true);

            x2Dobject4.setisStartPictureMove(true);
            x2Dobject5.setisStartPictureMove(true);
            x2Dobject6.setisStartPictureMove(true);
            mPopWindow.setisStartPictureMove(true);
            mPopWindowLight.setisStartPictureMove(true);
            mPopWinodwAtom.setisStartPictureMove(true);

            mTreeLeft_1.setisStartPictureMove(true);
            mTreeRight_1.setisStartPictureMove(true);
            touchTime++;
            setNumber(25);
            break;
         case 1:

            mTreeLeft_2.setisStartPictureMove(true);
            mPopWindow . setzSSSS();
            mPopWindow.modifyOffset(0f,0f);
            mPopWindow.setDestination(0f,0f,true);
            mPopWindow.resetZoom(0.1f,true);
            mPopWindow.setZoomValue(0.1f,0.5f);
            mPopWindowLight.manualStop(false);
            mPopWindowLight.modifyOffset(0f,0f);
            mPopWindowLight.setDestination(0f,0f,true);
            mPopWindowLight.setisNeedZoom(true,false,true,false);
            mPopWindowLight.resetZoom(0.1f,true);
            mPopWindowLight.setZoomValue(0.1f,0.7f);
            mTreeLeft_2.setTimeUp(18f);
            mTreeLeft_1.setTimeUp(18f);
            mTreeRight_1.setTimeUp(18f);

            mProgress.setpercent(25);
            touchTime++;
            setNumber(36);
            break;
         case 2:
            mTreeLeft_3.setisStartPictureMove(true);
            mPopWindow.setDestination(0f,0f,false);
            mPopWindow.setisNeedZoom(true,false,false,true);
            mPopWindow.setAlphaUp(false);
            mPopWindow.setZoomValue(0.1f,0.625f);
            mPopWindow.setsmall(true);
            mPopWindow.resetZoom(0.5f,true);
            mPopWindow.setAlphaValue(0.1f,1.1f);



            mPopWinodwAtom.manualStop(false);
            mPopWinodwAtom.setDestination(0f,0f,false);
            mPopWinodwAtom. resetZoom(0.5f,false);
            mPopWinodwAtom.setisNeedZoom(true,false,false,true);
            mPopWinodwAtom.setAlphaUp(true);
            mPopWinodwAtom.setAlphaValue(0.1f,0.8f);


            mProgress.setpercent(75);
            mTreeLeft_4.setTimeUp(4f);
            mTreeLeft_3.setTimeUp(4f);
            mTreeLeft_2.setTimeUp(4f);
            mTreeLeft_1.setTimeUp(4f);
            mTreeRight_1.setTimeUp(4f);
            setNumber(42);
            break;
         default:
            mTreeLeft_4.setisStartPictureMove(true);
            setNumber(tempnum++);
            mProgress.setpercent(75);
            break;

      }

   }

   int tempnum = 36;

   private void setNumber(int param){
      if(param<0 || param>99){
         return;
      }
      switch ( param/10){
         case 0:
            mTensNum.setTexture(textureNum0);
            break;
         case 1:
            mTensNum.setTexture(textureNum1);
            break;
         case 2:
            mTensNum.setTexture(textureNum2);
            break;
         case 3:
            mTensNum.setTexture(textureNum3);
            break;
         case 4:
            mTensNum.setTexture(textureNum4);
            break;
         case 5:
            mTensNum.setTexture(textureNum5);
            break;
         case 6:
            mTensNum.setTexture(textureNum6);
            break;
         case 7:
            mTensNum.setTexture(textureNum7);
            break;
         case 8:
            mTensNum.setTexture(textureNum8);
            break;
         case 9:
            mTensNum.setTexture(textureNum9);
            break;
      }

      switch ( param%10){
         case 0:
            mOnesNum.setTexture(textureNum0);
            break;
         case 1:
            mOnesNum.setTexture(textureNum1);
            break;
         case 2:
            mOnesNum.setTexture(textureNum2);
            break;
         case 3:
            mOnesNum.setTexture(textureNum3);
            break;
         case 4:
            mOnesNum.setTexture(textureNum4);
            break;
         case 5:
            mOnesNum.setTexture(textureNum5);
            break;
         case 6:
            mOnesNum.setTexture(textureNum6);
            break;
         case 7:
            mOnesNum.setTexture(textureNum7);
            break;
         case 8:
            mOnesNum.setTexture(textureNum8);
            break;
         case 9:
            mOnesNum.setTexture(textureNum9);
            break;
      }

   }


}
