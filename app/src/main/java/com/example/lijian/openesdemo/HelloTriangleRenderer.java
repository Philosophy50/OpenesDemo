package com.example.lijian.openesdemo;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

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

import static java.lang.Math.abs;
import static java.lang.Math.sin;

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
   private boolean isStartPictureMove = false;
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
   /*
    new float[]{
          0.5f,0.0f,
           0.0f,1.0f,
          1.0f,1.0f
   };
    */



   ///
   // Constructor
   //
   X2DObject x2DObject,x2DObject1,x2DObject2,x2DObject3,x2Dobject4,x2Dobject5,x2Dobject6;
   X2DObject mPopWindow,mPopWindowLight,mPopWinodwAtom;

   X2DObject mTreeLeft_1,mTreeRight_1,mTreeLeft_2,mTreeRight_2;

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

//      Thread jj = new Thread(){
//         @Override
//         public void run() {
//            super.run();
//            while(true){
//
//               try {
//                  iu++;
//                  maxAmplitude  = (float) abs(sin(iu * (3.1415926f / 0.6f)));
//                  Log.w("test_wl","Function:maxAmplitude = "+maxAmplitude);
//                  sleep(1000);
//               } catch (InterruptedException e) {
//                  e.printStackTrace();
//               }
//            }
//
//         }
//      };
//      jj.run();
   }
   int iu = 0;
   float maxAmplitude;
   ///
   // Create a shader object, load the shader source, and
   // compile the shader.
   //
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
   int textureR,textureB,textureG,textureP,textureY,programObject2;
   int textureCity,textureLight,textureAtom,textureTree;
   ///
   // Initialize the shader and program object
   //
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
      textureR = ESShader.loadTextureFromAsset(mContext,"textures/circle_red.png");
      textureB = ESShader.loadTextureFromAsset(mContext,"textures/circle_blue.png");
      textureG  = ESShader.loadTextureFromAsset(mContext,"textures/circle_green.png");
      textureP = ESShader.loadTextureFromAsset(mContext,"textures/circle_purple.png");
      textureY = ESShader.loadTextureFromAsset(mContext,"textures/circle_yellow.png");
      textureCity = ESShader.loadTextureFromAssetAlpha(mContext,"textures/a_city.png");
      textureLight = ESShader.loadTextureFromAssetAlpha(mContext,"textures/a_rotatinglight.png");
      textureAtom = ESShader.loadTextureFromAssetAlpha(mContext,"textures/a_atomization.png");
      textureTree = ESShader.loadTextureFromAssetAlpha(mContext,"textures/a_tree.png");

      mTreeLeft_1 = new X2DObject(2f,3f,          -3f,-2f,         200f,200f,textureTree,programObject2);
      mTreeLeft_1.setDestination(-16f,-11f,true);
      mTreeLeft_1.setisNeedZoom(true,true,false,false);
      mTreeLeft_1.setZoomValue(0.1f,1.3f);
      mTreeLeft_1.resetZoom(0.3f,true);
      mTreeLeft_1.setcircle(true);

      mTreeLeft_2 = new X2DObject(2f,3f,          -1.5f,-1f,         200f,200f,textureTree,programObject2);
      mTreeLeft_2.setDestination(-14.5f,-10f,true);
      mTreeLeft_2.setisNeedZoom(true,true,false,false);
      mTreeLeft_2.setZoomValue(0.1f,1.3f);
      mTreeLeft_2.resetZoom(0.3f,true);
      mTreeLeft_2.setcircle(true);



      x2DObject =   new X2DObject(10f,10f,   -2f,7f,   200f,200f,textureR,programObject2);
      x2DObject.setDestination(2f,5f,false);
      x2DObject.setRespondEvent(1);
      x2DObject1=  new X2DObject (4f,4f,     -2f,-4f,  200f,200f,textureB,programObject2);
      x2DObject1.setDestination(2f,5f,false);
      x2DObject1.setRespondEvent(1);
      x2DObject2 = new X2DObject(5f,5f,      2f,8f,         200f,200f,textureG,programObject2);
      x2DObject2.setDestination(2f,5f,false);
      x2DObject2.setRespondEvent(1);
      x2DObject3  = new X2DObject(5f,5f,     2f,0f,         200f,200f,textureLight,programObject2);
      x2DObject3.setRespondEvent(1);
      x2DObject3.setDestination(2f,5f,false);
      x2Dobject4  = new X2DObject(5f,5f      ,5f,5f,         200f,200f,textureY,programObject2);
      x2Dobject4.setDestination(2f,5f,false);
      x2Dobject4.setRespondEvent(1);
      x2Dobject5  = new X2DObject(5f,5f,     -5f,5f,         200f,200f,textureR,programObject2);
      x2Dobject5.setDestination(2f,5f,false);
      x2Dobject5.setRespondEvent(1);
      x2Dobject6  = new X2DObject(5f,5f,     -1f,-1f,         200f,200f,textureB,programObject2);
      x2Dobject6.setDestination(2f,5f,false);
      x2Dobject6.setRespondEvent(1);

      mPopWindow = new X2DObject(7f,7f,  0f,-2f ,    200f,200f, textureCity,programObject2);
      mPopWindow.setDestination(0f,-10,true);
      mPopWindow.setisNeedZoom(true,true,false,false);

      mPopWindowLight = new  X2DObject(8f,8f,  0f,0f ,    200f,200f, textureLight,programObject2);
      mPopWindowLight.setisNeedZoom(true,false,false,false);
      mPopWindowLight.manualStop(true);

      mPopWinodwAtom = new X2DObject(4f,4f,  0f,0f, 200f,200f, textureAtom,programObject2);
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
      mSecondMapTexId = loadTextureFromAsset ( "textures/lit.png");
      GLES30.glClearColor ( 1.0f, 1.0f, 1.0f, 0.0f );
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





      mTreeLeft_2.drawSelf();

      mTreeLeft_1.drawSelf();
      mPopWindowLight.drawSelf();
      mPopWindow.drawSelf();
      mPopWinodwAtom.drawSelf();

      x2DObject.drawSelf();
      x2DObject1.drawSelf();
      x2DObject2.drawSelf();
      x2DObject3.drawSelf();
      x2Dobject4.drawSelf();
      x2Dobject5.drawSelf();
      x2Dobject6.drawSelf();
      //GLES30.glDrawArrays ( GLES30.GL_TRIANGLE_STRIP, 0, 6 );


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

   private long mLastTime = 0;
   private float mAngle = 45.0f;
   private float zoomMultiples = 0.1f;
   private float xlocation = 0.0f;
   private float ylocation = 0.0f;

   private boolean isBig = true;
   private ESTransform mMVPMatrix = new ESTransform();
   // Uniform locations
   private int mMVPLoc;
   private int mBaseMapLoc;
   // Texture handle
   private int mBaseMapTexId;
   private int mSecondMapTexId;

   private int times = 0;
   private void update()
   {
      times ++;
      if ( mLastTime == 0 )
      {
         mLastTime = SystemClock.uptimeMillis();
      }

      long curTime = SystemClock.uptimeMillis();
      long elapsedTime = curTime - mLastTime;
      float deltaTime = elapsedTime / 1000.0f;
      mLastTime = curTime;

      ESTransform perspective = new ESTransform();
      ESTransform modelview = new ESTransform();
      float aspect;

      // Compute a rotation angle based on time to rotate the cube
      mAngle += ( deltaTime * 40.0f );

      if(isStartPictureMove) {
         if (zoomMultiples >= 1.0f && isBig) {
            isBig = false;
         } else if (zoomMultiples <= 0.1f && !isBig) {
            isBig = true;
         }
         if (isBig) {
            zoomMultiples += 0.01f;
         } else {
            zoomMultiples -= 0.01f;
         }
         xlocation += 0.01f;
         ylocation += 0.02f;
         if (xlocation >= 5.0f) {
            xlocation = 0.0f;
            ylocation = 0.0f;
         }
      }
      if ( mAngle >= 360.0f )
      {
         mAngle -= 360.0f;
      }


      // Compute the window aspect ratio
      aspect = ( float ) mWidth / ( float ) mHeight;

      // Generate a perspective matrix with a 60 degree FOV
      perspective.matrixLoadIdentity();
      perspective.perspective ( 60.0f, aspect, 1.0f, 20.0f );

      // Generate a model view matrix to rotate/translate the cube
      modelview.matrixLoadIdentity();
      // Translate away from the viewer
      modelview.translate ( xlocation, ylocation, -20.0f );
      // Rotate
      modelview.rotate ( mAngle, 0.0f, 0.0f, 1.0f );
      modelview.scale(zoomMultiples,zoomMultiples,zoomMultiples);

      // Compute the final MVP by multiplying the
      // modevleiw and perspective matrices together
      mMVPMatrix.matrixMultiply ( modelview.get(), perspective.get());

   }


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
            x2DObject.setisStartPictureMove(true);
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
            mTreeLeft_2.setisStartPictureMove(true);
            touchTime++;
            break;
         case 1:
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




            touchTime++;
            break;
         case 2:
            //mPopWindow.setTexture(textureCity);
            mPopWindow.setDestination(0f,0f,false);
            mPopWindow.setisNeedZoom(true,false,false,true);
            mPopWindow.setisBig(false);

            mPopWindow.setZoomValue(0.1f,0.625f);

            mPopWindow.setsmall(true);
            mPopWindow.resetZoom(0.5f,true);




            mPopWinodwAtom.manualStop(false);
            mPopWinodwAtom.setDestination(0f,0f,false);
            mPopWinodwAtom. resetZoom(0.5f,false);
            mPopWinodwAtom.setisNeedZoom(false,false,false,true);
            mPopWinodwAtom.setisBig(true);
            break;

      }

   }




}
