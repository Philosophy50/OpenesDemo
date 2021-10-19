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

package com.example.lijian.openesdemo.ui;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.graphics.Point;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.example.lijian.openesdemo.ActionInstance;
import com.example.lijian.openesdemo.Animation2dRenderer;
import com.example.lijian.openesdemo.SEvice.MyService;

/**
 * Activity class for example program that detects OpenGL ES 3.0.
 **/
public class Animation2dActivity extends iActivity
{

   private final int CONTEXT_CLIENT_VERSION = 3;

   private GLSurfaceView mGLSurfaceView;
   private Animation2dRenderer mAnimation2dRenderer;
   private int leftPopWindow = 312 ;
   private int rightPopWindow = 712;
   private int topPopWindow = 200;
   private int bottomPopWindow = 400;


   private static final String TAG = "Animation2dActivity";


   @Override
   protected void onInitView(Bundle savedInstanceState) {
      init_timeout_set(2*1000,"Loading,Please waiting");


      Intent mm = new Intent(Animation2dActivity.this, MyService.class);
      startService(mm);
      Point outSize = new Point();
      getWindowManager().getDefaultDisplay().getRealSize(outSize);
      int x = outSize.x;
      int y = outSize.y;
      Toast.makeText(Animation2dActivity.this,"x:"+x+" y:"+y,Toast.LENGTH_SHORT).show();
      // mGLSurfaceView = ActionInstance.getInstance().getGLSurfaceView();
      //  mAnimation2dRenderer = ActionInstance.getInstance().getHelloTriangleRender();

      mGLSurfaceView = new GLSurfaceView ( this );
      mAnimation2dRenderer = new Animation2dRenderer(this);//TODO  slow


      if ( detectOpenGLES30() )
      {
         //设置OpenGL es版本号3.0
         mGLSurfaceView.setEGLContextClientVersion ( CONTEXT_CLIENT_VERSION );//3
         //设置渲染实现
         mGLSurfaceView.setRenderer ( mAnimation2dRenderer );
         //创建和调用requestRender()时才会刷新
         mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
      }
      else
      {
         Log.e ( "Animation2dActivity", "OpenGL ES 3.0 not supported on device.  Exiting..." );
         finish();
      }

      setContentView ( mGLSurfaceView );

   }

   @Override
   protected void onInitData() {


   }

   @Override
   protected void onInitCompleted() {
      mGLSurfaceView.setOnTouchListener(new View.OnTouchListener() {
         @Override
         public boolean onTouch(View v, MotionEvent event) {float y = event.getRawY();
            float x = event.getRawX();//获取触控点的坐标  ,相对于widget的左上角，getRawX是相对于屏幕的左上角

            Log.w("test_wl","MotionEvent_Down:x--"+x+" y--"+y+" Function:useless");
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
               case MotionEvent.ACTION_DOWN:
                  if( x > leftPopWindow && x < rightPopWindow && y>topPopWindow && y < bottomPopWindow){
                     mAnimation2dRenderer.setStartPictureMove(false);

                  }
                  break;
               case MotionEvent.ACTION_UP:
                  break;
               case MotionEvent.ACTION_MOVE:
                  break;
            }
            return false;
         }
      });
   }

   @Override
   protected void onCreate ( Bundle savedInstanceState )
   {
      super.onCreate ( savedInstanceState );
      Log.i(TAG, "onCreate: "+SystemClock.elapsedRealtime());


   }

   private boolean detectOpenGLES30()
   {
      ActivityManager am =
         ( ActivityManager ) getSystemService ( Context.ACTIVITY_SERVICE );
      ConfigurationInfo info = am.getDeviceConfigurationInfo();
      return ( info.reqGlEsVersion >= 0x30000 );
   }

   @Override
   protected void onResume()
   {
      // Ideally a game should implement onResume() and onPause()
      // to take appropriate action when the activity looses focus
      super.onResume();
      Log.i(TAG, "onResume: "+SystemClock.elapsedRealtime());
      //mGLSurfaceView.onResume();  //调用的话回到这个界面会导致从头开始，不调用可能资源不释放?
   }

   @Override
   public void onStart() {
      super.onStart();
      ActionInstance.getInstance().setContext(this);
      Log.i(TAG, "onStart: "+SystemClock.elapsedRealtime());
   }

   @Override
   protected void onPause()
   {
      // Ideally a game should implement onResume() and onPause()
      // to take appropriate action when the activity looses focus
      super.onPause();
     // mGLSurfaceView.onPause();//调用的话回到这个界面会导致从头开始，不调用可能资源不释放?
   }

   @Override
   public boolean onTouchEvent(MotionEvent event) {
      return super.onTouchEvent(event);
   }
}
