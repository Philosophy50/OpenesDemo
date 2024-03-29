// The MIT License (MIT)
//
// Copyright (c) 2013 Dan Ginsburg, Budirijanto Purnomo
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.

//
// Book:      OpenGL(R) ES 3.0 Programming Guide, 2nd Edition
// Authors:   Dan Ginsburg, Budirijanto Purnomo, Dave Shreiner, Aaftab Munshi
// ISBN-10:   0-321-93388-5
// ISBN-13:   978-0-321-93388-1
// Publisher: Addison-Wesley Professional
// URLs:      http://www.opengles-book.com
//            http://my.safaribooksonline.com/book/animation-and-3d/9780133440133
//

// ESShader
//
//    Utility functions for loading GLSL ES 3.0 shaders and creating program objects.
//

package com.example.lijian.openesdemo.utils.ESUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES30;
import android.opengl.GLUtils;
import android.os.SystemClock;
import android.util.Log;

import com.example.lijian.openesdemo.BitmapList;

public class ESShader
{
   //
   ///
   /// \brief Read a shader source into a String
   /// \param context Application context
   /// \param fileName Name of shader file
   /// \return A String object containing shader source, otherwise null
   //
   private static String readShader ( Context context, String fileName )
   {
      String shaderSource = null;

      if ( fileName == null )
      {
         return shaderSource;
      }

      // Read the shader file from assets
      InputStream is = null;
      byte [] buffer;

      try
      {
         is =  context.getAssets().open ( fileName );

         // Create a buffer that has the same size as the InputStream
         buffer = new byte[is.available()];

         // Read the text file as a stream, into the buffer
         is.read ( buffer );

         ByteArrayOutputStream os = new ByteArrayOutputStream();

         // Write this buffer to the output stream
         os.write ( buffer );

         // Close input and output streams
         os.close();
         is.close();

         shaderSource = os.toString();
      }
      catch ( IOException ioe )
      {
         is = null;
      }

      if ( is == null )
      {
         return shaderSource;
      }

      return shaderSource;
   }

   //
   ///
   /// \brief Load a shader, check for compile errors, print error messages to
   /// output log
   /// \param type Type of shader (GL_VERTEX_SHADER or GL_FRAGMENT_SHADER)
   /// \param shaderSrc Shader source string
   /// \return A new shader object on success, 0 on failure
   //

   /**
    * 加载一个着色器，检查编译错误，将错误消息打印到输出日志
    * @param type  着色器类型（Vertex Or Fragment)
    * @param shaderSrc 着色器源码
    * @return 成功返回着色器对象，失败返回0
    */
   public static int loadShader ( int type, String shaderSrc )
   {
      int shader;
      int[] compiled = new int[1];

      // Create the shader object
      shader = GLES30.glCreateShader ( type );

      if ( shader == 0 )
      {
         return 0;
      }

      // Load the shader source
      GLES30.glShaderSource ( shader, shaderSrc );

      // Compile the shader
      GLES30.glCompileShader ( shader );

      // Check the compile status
      GLES30.glGetShaderiv ( shader, GLES30.GL_COMPILE_STATUS, compiled, 0 );

      Log.w("ESShader~","compiled[0]:"+compiled[0]);
      if ( compiled[0] == 0 )
      {
         Log.e ( "ESShader~", GLES30.glGetShaderInfoLog ( shader ) );
         GLES30.glDeleteShader ( shader );
         return 0;
      }

      return shader;
   }

   //
   ///
   /// \brief Load a vertex and fragment shader, create a program object, link
   ///    program.
   /// Errors output to log.
   /// \param vertShaderSrc Vertex shader source code
   /// \param fragShaderSrc Fragment shader source code
   /// \return A new program object linked with the vertex/fragment shader
   ///    pair, 0 on failure
   //

   /**
    * 加载一个顶点和片段着色器，创建程序对象，连接程序。错误输出到日志
    * @param vertShaderSrc 顶点着色器源码
    * @param fragShaderSrc 片段着色器源码
    * @return 链接的新程序对象，失败时返回0
    */
   public static int loadProgram ( String vertShaderSrc, String fragShaderSrc )
   {
      int vertexShader;
      int fragmentShader;
      int programObject;
      int[] linked = new int[1];

      // Load the vertex/fragment shaders
      vertexShader = loadShader ( GLES30.GL_VERTEX_SHADER, vertShaderSrc );

      if ( vertexShader == 0 )
      {
         return 0;
      }

      fragmentShader = loadShader ( GLES30.GL_FRAGMENT_SHADER, fragShaderSrc );

      if ( fragmentShader == 0 )
      {
         GLES30.glDeleteShader ( vertexShader );
         return 0;
      }

      // Create the program object
      programObject = GLES30.glCreateProgram();

      if ( programObject == 0 )
      {
         return 0;
      }

      GLES30.glAttachShader ( programObject, vertexShader );
      GLES30.glAttachShader ( programObject, fragmentShader );

      // Link the program
      GLES30.glLinkProgram ( programObject );

      // Check the link status
      GLES30.glGetProgramiv ( programObject, GLES30.GL_LINK_STATUS, linked, 0 );

      if ( linked[0] == 0 )
      {
         Log.e ( "ESShader", "Error linking program:" );
         Log.e ( "ESShader", GLES30.glGetProgramInfoLog ( programObject ) );
         GLES30.glDeleteProgram ( programObject );
         return 0;
      }

      // Free up no longer needed shader resources
      GLES30.glDeleteShader ( vertexShader );
      GLES30.glDeleteShader ( fragmentShader );

      return programObject;
   }

   //
   ///
   /// \brief Load a vertex and fragment shader from "assets", create a program object, link
   ///    program.
   /// Errors output to log.
   /// \param vertShaderFileName Vertex shader source file name
   /// \param fragShaderFileName Fragment shader source file name
   /// \return A new program object linked with the vertex/fragment shader
   ///    pair, 0 on failure
   //
   /**
    * 加载一个顶点和片段着色器，创建程序对象，连接程序。错误输出到日志
    * @param context 上下文环境
    * @param vertexShaderFileName 顶点着色器源码文件名称
    * @param fragShaderFileName 片段着色器源码文件名称
    * @return 链接的新程序对象，失败时返回0
    */
   private static final String TAG = "ESShader";
   public static int loadProgramFromAsset ( Context context, String vertexShaderFileName, String fragShaderFileName )
   {
      boolean j=false;
      if(vertexShaderFileName.contains("vertex_")){
         j = true;
      }
      int vertexShader;
      int fragmentShader;
      int programObject;
      int[] linked = new int[1];

      String vertShaderSrc = null;
      String fragShaderSrc = null;

      // Read vertex shader from assets
      if(j){
         Log.w(TAG, "loadProgramFromAsset: readShaderV_start_time:"+SystemClock.elapsedRealtime());
      }
      vertShaderSrc = readShader ( context, vertexShaderFileName );
      if(j){
         Log.w(TAG, "loadProgramFromAsset: readShaderV_end_time:"+SystemClock.elapsedRealtime());
      }
      if ( vertShaderSrc == null )
      {
         return 0;
      }
      if(j){
         Log.w(TAG, "loadProgramFromAsset: readShaderF_start_time:"+SystemClock.elapsedRealtime());
      }
      // Read fragment shader from assets
      fragShaderSrc = readShader ( context, fragShaderFileName );
      if(j){
         Log.w(TAG, "loadProgramFromAsset: readShaderF_end_time:"+SystemClock.elapsedRealtime());
      }
      if ( fragShaderSrc == null )
      {
         return 0;
      }
      if(j){
         Log.w(TAG, "loadProgramFromAsset: loadShaderV_start_time:"+SystemClock.elapsedRealtime());
      }
      // Load the vertex shader
      vertexShader = loadShader ( GLES30.GL_VERTEX_SHADER, vertShaderSrc );
      if(j){
         Log.w(TAG, "loadProgramFromAsset: loadShaderV_end_time:"+SystemClock.elapsedRealtime());
      }
      if ( vertexShader == 0 )
      {
         return 0;
      }
      if(j){
         Log.w(TAG, "loadProgramFromAsset: loadShaderF_start_time::"+SystemClock.elapsedRealtime());
      }
      // Load the fragment shader
      fragmentShader = loadShader ( GLES30.GL_FRAGMENT_SHADER, fragShaderSrc );
      if(j){
         Log.w(TAG, "loadProgramFromAsset: loadShaderF_end_time:"+SystemClock.elapsedRealtime());
      }
      if ( fragmentShader == 0 )
      {
         GLES30.glDeleteShader ( vertexShader );
         return 0;
      }
      if(j){
         Log.w(TAG, "loadProgramFromAsset: glCreateProgramtime1:"+SystemClock.elapsedRealtime());
      }
      // Create the program object
      programObject = GLES30.glCreateProgram();
      if(j){
         Log.w(TAG, "loadProgramFromAsset: glCreateProgramtime2:"+SystemClock.elapsedRealtime());
      }
      if ( programObject == 0 )
      {
         return 0;
      }
      if(j){
         Log.w(TAG, "loadProgramFromAsset: glAttachShadertime1:"+SystemClock.elapsedRealtime());
      }
      GLES30.glAttachShader ( programObject, vertexShader );
      GLES30.glAttachShader ( programObject, fragmentShader );
      if(j){
         Log.w(TAG, "loadProgramFromAsset: glAttachShadertime2:"+SystemClock.elapsedRealtime());
      }
      // Link the program
      GLES30.glLinkProgram ( programObject );
      if(j){
         Log.w(TAG, "loadProgramFromAsset: time:"+SystemClock.elapsedRealtime());
      }
      // Check the link status
      GLES30.glGetProgramiv ( programObject, GLES30.GL_LINK_STATUS, linked, 0 );

      if ( linked[0] == 0 )
      {
         Log.e ( "ESShader", "Error linking program:" );
         Log.e ( "ESShader", GLES30.glGetProgramInfoLog ( programObject ) );
         GLES30.glDeleteProgram ( programObject );
         return 0;
      }

      // Free up no longer needed shader resources
      GLES30.glDeleteShader ( vertexShader );
      GLES30.glDeleteShader ( fragmentShader );

      return programObject;
   }

   public static int loadTextureFromAsset ( Context mContext,String fileName )
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

      bitmap.recycle();//释放图像资源 //TODO 0114
      return textureId[0];
   }

  static int[] pixBufferObj = new int[2];


   private static ByteBuffer bitmap2Buffer(Bitmap bm) {

      ByteBuffer byteBuffer = ByteBuffer.allocate(bm.getHeight() * bm.getWidth() * 4);
      bm.copyPixelsToBuffer(byteBuffer);
      byteBuffer.flip();
      return byteBuffer;
      /*
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
      return ByteBuffer.wrap(baos.toByteArray());
      */
   }

   public static int loadTextureFromXXX (String aa  )
   {
      int index = BitmapList.listFileName.indexOf(aa);
      Log.w("test_wl","****index******"+index+"************************" + SystemClock.elapsedRealtime()+" Function:ltfa");
      int[] textureId = new int[1];
      GLES30.glGenTextures ( 1, textureId, 0 );


      Bitmap bitmap = BitmapList.list.get(index);
      int size =bitmap.getWidth() * bitmap.getHeight() *4;
      GLES30.glGenBuffers( 2,pixBufferObj,0);
      GLES30.glBindBuffer(GLES30.GL_PIXEL_UNPACK_BUFFER, pixBufferObj[0]);
      GLES30.glBufferData(GLES30.GL_PIXEL_UNPACK_BUFFER,size,   null,GLES30.GL_STREAM_DRAW);
      GLES30.glBindBuffer(GLES30.GL_PIXEL_UNPACK_BUFFER,0);

      GLES30.glBindBuffer(GLES30.GL_PIXEL_PACK_BUFFER, pixBufferObj[1]);
      GLES30.glBufferData(GLES30.GL_PIXEL_PACK_BUFFER,size,   null,GLES30.GL_STREAM_DRAW);
      GLES30.glBindBuffer(GLES30.GL_PIXEL_PACK_BUFFER,0);




      GLES30.glBindBuffer(GLES30.GL_PIXEL_UNPACK_BUFFER, pixBufferObj[0]);
      ByteBuffer buffer = (ByteBuffer)GLES30.glMapBufferRange(
              GLES30.GL_PIXEL_UNPACK_BUFFER,
              0,
              size,
              GLES30.GL_MAP_WRITE_BIT
      ) ;
      bitmap.copyPixelsToBuffer(buffer);
      GLES30.glUnmapBuffer(GLES30.GL_PIXEL_UNPACK_BUFFER);

//      ByteBuffer bytebuffer = ((ByteBuffer) buf).order(ByteOrder.nativeOrder()); //TODO

     GLES30.glBindTexture ( GLES30.GL_TEXTURE_2D, textureId[0] );
        // GLUtils.texImage2D ( GLES30.GL_TEXTURE_2D, 0, bitmap, 0 ); //TODO


      /*
      GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D,0,GLES30.GL_RGBA,
             bitmap.getWidth(),bitmap.getHeight(),0,
              GLES30.GL_RGBA,
              GLES30.GL_UNSIGNED_BYTE,
              bitmap2Buffer(bitmap));//  bitmap2Buffer(bitmap)); //TODO

     */
      GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGBA,
              bitmap.getWidth(),bitmap.getHeight(),0,//screenWidth, screenHeight, 0,
              GLES30.GL_RGBA,
              GLES30. GL_UNSIGNED_BYTE,
              null);
      Log.w("test_wl","#13#  " + SystemClock.elapsedRealtime()+" Function:ltfa1");

     GLES30.glBindBuffer(GLES30.GL_PIXEL_UNPACK_BUFFER,0);


      GLES30.glTexParameteri ( GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR );
      GLES30.glTexParameteri ( GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR );
      GLES30.glTexParameteri ( GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE );
      GLES30.glTexParameteri ( GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE );

      GLES30.glBindTexture ( GLES30.GL_TEXTURE_2D, 0 );//TODO

       // BitmapList.list.get(0).recycle(); //TODO 0114
      //bitmap.recycle();//释放图像资源 //TODO 0114
      return textureId[0];
   }



   public static int loadTextureFromAssetAlpha ( Context mContext,String fileName )
   {

      Log.w("test_wl","**************************************" + SystemClock.elapsedRealtime()+" Function:ltfa");
      int[] textureId = new int[1];
      Bitmap bitmap ;
      InputStream is ;
      Log.w("test_wl","a"+ SystemClock.elapsedRealtime()+" Function:ltfa");
      try
      {
         is = mContext.getAssets().open ( fileName );
      }
      catch ( IOException ioe )
      {
         is = null;
      }
      Log.w("test_wl","b"+ SystemClock.elapsedRealtime()+" Function:ltfa");
      if ( is == null )
      {
         return 0;
      }

      BitmapFactory.Options options = new BitmapFactory.Options();
      options.inPremultiplied = false;
//      options.inSampleSize = 2;//采样率
//      options.inJustDecodeBounds = true;  用来轻量加载图片数据但不显示图片
      Log.w("test_wl","c"+ SystemClock.elapsedRealtime()+" Function:ltfa");
      bitmap = BitmapFactory.decodeStream ( is ,null,options);
      Log.w("test_wl","d"+ SystemClock.elapsedRealtime()+" Function:ltfa");
      GLES30.glGenTextures ( 1, textureId, 0 );
      Log.w("test_wl","e"+ SystemClock.elapsedRealtime()+" Function:ltfa");

      GLES30.glBindTexture ( GLES30.GL_TEXTURE_2D, textureId[0] );
      Log.w("test_wl","f"+ SystemClock.elapsedRealtime()+" Function:ltfa");

      GLUtils.texImage2D ( GLES30.GL_TEXTURE_2D, 0, bitmap, 0 );
      Log.w("test_wl","g"+ SystemClock.elapsedRealtime()+" Function:ltfa");

      GLES30.glTexParameteri ( GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR );
      GLES30.glTexParameteri ( GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR );
      GLES30.glTexParameteri ( GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE );
      GLES30.glTexParameteri ( GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE );
      Log.w("test_wl","h"+ SystemClock.elapsedRealtime()+" Function:ltfa");

      try {
         is.close();
      } catch (IOException e) {
         e.printStackTrace();
      }

      Log.w("test_wl","i"+ SystemClock.elapsedRealtime()+" Function:ltfa");

      bitmap.recycle();//释放图像资源 //TODO 0114

      Log.w("test_wl","##############################################" + SystemClock.elapsedRealtime()+" Function:ltfa");
      return textureId[0];
   }


   public static int loadTextureFromBitmap ( Context mContext,String aa )
   {

      int index = BitmapList.listFileName.indexOf(aa);
      Log.w("test_wl","****index******************************" + index+" Function:ltfa");
      int[] textureId = new int[1];
      Bitmap bitmap = BitmapList.list.get(index);


      GLES30.glGenTextures ( 1, textureId, 0 );
      GLES30.glBindTexture ( GLES30.GL_TEXTURE_2D, textureId[0] );

      GLUtils.texImage2D ( GLES30.GL_TEXTURE_2D, 0, bitmap, 0 );

      GLES30.glTexParameteri ( GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR );
      GLES30.glTexParameteri ( GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR );
      GLES30.glTexParameteri ( GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE );
      GLES30.glTexParameteri ( GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE );

   //bitmap.recycle();//释放图像资源 //TODO 0114
      return textureId[0];
   }
}
