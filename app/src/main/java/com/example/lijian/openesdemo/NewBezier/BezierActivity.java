package com.example.lijian.openesdemo.NewBezier;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.lijian.openesdemo.R;


public class BezierActivity extends AppCompatActivity {

    private GLSurfaceView mGLSurfaceView;
    private BezierRenderer mBezierRenderer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bezier);
        mBezierRenderer = new BezierRenderer(this);
        mGLSurfaceView = (GLSurfaceView) findViewById(R.id.glsurfaceview);
        mGLSurfaceView.setEGLContextClientVersion(3);
        mGLSurfaceView.setEGLConfigChooser(new MyConfigChooser());
        mGLSurfaceView.setRenderer(mBezierRenderer);
        mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }
}