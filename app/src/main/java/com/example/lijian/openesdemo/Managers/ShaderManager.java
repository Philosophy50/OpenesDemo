package com.example.lijian.openesdemo.Managers;

import android.content.Context;

import com.example.lijian.openesdemo.ESUtils.ESShader;
import com.example.lijian.openesdemo.X2DObject;

/**
 * Created by lijian on 2020/10/28.
 */

public class ShaderManager {

    int programObject2;
    int texture2,texture3;
    public void ShaderManager(Context mContext){
        programObject2 =  ESShader.loadProgramFromAsset(mContext,"shaders/vertex_2d.sh", "shaders/frag_2d.sh");
        texture2 = ESShader.loadTextureFromAsset(mContext,"textures/basemap.png");
        texture3 = ESShader.loadTextureFromAsset(mContext,"textures/lit.png");
        X2DObject x2DObject,x2DObject1;
        x2DObject =   new X2DObject(200f,200f,0f,0f,200f,200f,texture2,programObject2);
        x2DObject1=  new X2DObject(200f,200f,0f,0f,200f,200f,texture3,programObject2);

   }

}
