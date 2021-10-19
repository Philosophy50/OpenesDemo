package com.example.lijian.openesdemo.utils.ESUtils;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import android.content.res.Resources;
import android.opengl.GLES30;
import android.os.SystemClock;
import android.util.Log;

import com.example.lijian.openesdemo.BitmapList;

public class BnETC2Util 
{
	public static final int PKM_HEADER_SIZE=16;
	public static final int PKM_HEADER_WIDTH_OFFSET=12;
	public static final int PKM_HEADER_HEIGHT_OFFSET=14;
	
	public static byte[] loadDataFromAssets(String fname, Resources r)
	{
		byte[] data=null;
		InputStream in=null;
		try
		{
			in = r.getAssets().open(fname);
	    	int ch=0;
		    ByteArrayOutputStream baos = new ByteArrayOutputStream();
		    while((ch=in.read())!=-1)
		    {
		      	baos.write(ch);
		    }      
		    data=baos.toByteArray();
		    baos.close();
		    in.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}		
		return data;
	}

	private static final String TAG = "BnETC2Util";

	public static int initTextureETC2(String pkmName,Resources r) //textureId
	{
		byte[] data= BitmapList.getbyte();//loadDataFromAssets(pkmName,r);

		int[] pixBufferObj = new int[1];
		GLES30.glGenBuffers( 1,pixBufferObj,0);
		GLES30.glBindBuffer(GLES30.GL_PIXEL_UNPACK_BUFFER, pixBufferObj[0]);
		GLES30.glBufferData(GLES30.GL_PIXEL_UNPACK_BUFFER,data.length,   null,GLES30.GL_STREAM_DRAW);
		GLES30.glBindBuffer(GLES30.GL_PIXEL_UNPACK_BUFFER,0);


		GLES30.glBindBuffer(GLES30.GL_PIXEL_UNPACK_BUFFER, pixBufferObj[0]);
		ByteBuffer buffer = (ByteBuffer)GLES30.glMapBufferRange(
				GLES30.GL_PIXEL_UNPACK_BUFFER,
				0,
				data.length,
				GLES30.GL_MAP_WRITE_BIT
		) ;
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.put(data).position(PKM_HEADER_SIZE);
		ByteBuffer header = ByteBuffer.allocateDirect(PKM_HEADER_SIZE).order(ByteOrder.BIG_ENDIAN);
		header.put(data, 0, PKM_HEADER_SIZE).position(0);
		int width = header.getShort(PKM_HEADER_WIDTH_OFFSET);
		int height = header.getShort(PKM_HEADER_HEIGHT_OFFSET);
		GLES30.glUnmapBuffer(GLES30.GL_PIXEL_UNPACK_BUFFER);

	//	GLES30.glBufferSubData(GLES30.GL_PIXEL_UNPACK_BUFFER, 0, data.length*8, buffer);

		//生成纹理ID
		int[] textures = new int[1];
		GLES30.glGenTextures
		(
				1,          //产生的纹理id的数量
				textures,   //纹理id的数组
				0           //偏移量
		);    
		int textureId=textures[0];    
		GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId);
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER,GLES30.GL_NEAREST);
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_MAG_FILTER,GLES30.GL_LINEAR);
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S,GLES30.GL_CLAMP_TO_EDGE);
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T,GLES30.GL_CLAMP_TO_EDGE);


		Log.w(TAG,"picture information width = "+width+" height = "+height);




		GLES30.glCompressedTexImage2D
        (
    		GLES30.GL_TEXTURE_2D, 
    		0, 
    		GLES30.GL_COMPRESSED_RGBA8_ETC2_EAC, 
    		width, 
    		height, 
    		0,
    		data.length - PKM_HEADER_SIZE,
    		null
        );

		GLES30.glBindBuffer(GLES30.GL_PIXEL_UNPACK_BUFFER,0);

		return textureId;
	}
}
