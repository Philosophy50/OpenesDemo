package com.example.lijian.openesdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.lijian.openesdemo.utils.String2Bitmap.BitmapUtil;
import com.example.lijian.openesdemo.utils.String2Bitmap.StringBitmapParameter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lijian on 2020/12/7.
 */

public class BitmapList {
    public static List<Bitmap> list = new ArrayList<Bitmap>();
    static int[] pixBufferObj = new int[1];
    public static List<String> listFileName = new ArrayList<String>();
    private static Context mContext;
    public static ByteBuffer buffer;
    private static byte[] data;
    public static int init(Context context){
        mContext = context;
        listFileName.add("textures/b_numa0.png");
        listFileName.add("textures/b_numa1.png");
        listFileName.add("textures/b_numa2.png");
        listFileName.add("textures/b_numa3.png");
        listFileName.add("textures/b_numa4.png");
        listFileName.add("textures/b_numa5.png");
        listFileName.add ("textures/b_numa6.png");
        listFileName.add ("textures/b_numa7.png");
        listFileName.add ("textures/b_numa8.png");
        listFileName.add  ("textures/b_numa9.png");
        listFileName.add  ("textures/a_city.png");
        listFileName.add  ("textures/a_rotatinglight.png");
        listFileName.add  ("textures/a_atomization.png");
        listFileName.add  ("textures/a_tree.png");
        listFileName.add  ("textures/b_bg.png");
        listFileName.add  ("textures/b_progressbar.png");
        listFileName.add  ("textures/b_scorebar.png");
        listFileName.add  ("textures/b_bar.png");
        listFileName.add  ("textures/b_barbg.png");
        listFileName.add  ("textures/a_juice.png");
        listFileName.add  ("textures/a_reward_star.png");
        listFileName.add  ("textures/a_2lines.png");
        listFileName.add ("textures/b_star.png");
        listFileName.add ("textures/b_lm_sh_a.png");
        listFileName.add ("textPicture");
         for(int i = 0;i<(listFileName.size()-1) ;i++) {
             Bitmap bitmap ;
             InputStream is;
             try {
                 is = mContext.getAssets().open(listFileName.get(i));
             } catch (IOException ioe) {
                 is = null;
             }
             if (is == null) {
                 return 0;
             }
             BitmapFactory.Options options = new BitmapFactory.Options();
             options.inPremultiplied = false;
             bitmap = BitmapFactory.decodeStream(is, null, options);
             list.add(bitmap);



             try {
                 is.close();
             } catch (IOException e) {
                 e.printStackTrace();
             }
         }
        data = loadDataFromAssets("textures/a_tree.pkm");
        String aa = "如果速度是8,则1小时后获得2点能量";
        StringBitmapParameter sbp1 ;
        ArrayList<StringBitmapParameter> pp = new ArrayList<>();
        sbp1 = new StringBitmapParameter(aa);
        pp .add(sbp1);
        list.add(BitmapUtil.StringListtoBitmap(mContext,pp));
         return 1;
    }

    public static byte[] getbyte(){
        return data;
    }
    public static byte[] loadDataFromAssets(String fname)
    {
        byte[] data=null;
        InputStream in=null;
        try
        {
            in = mContext.getAssets().open(fname);
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

}