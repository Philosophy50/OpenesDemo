package com.example.lijian.openesdemo.Bezier;

import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.lijian.openesdemo.R;

public class BezierActivity extends AppCompatActivity {
    GLSurfaceView glview ;
    BezierRenderer brender;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bezier);
        glview = (GLSurfaceView) findViewById(R.id.glsurfaceview);
        brender = new BezierRenderer(this);
        glview.setEGLContextClientVersion ( 3 );//3
        glview.setRenderer ( brender );
        glview.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }
}
