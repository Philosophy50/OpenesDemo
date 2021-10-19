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

// ESTransform
//
//    Utility class for handling transformations
//

package com.example.lijian.openesdemo.utils.ESUtils;

import java.lang.Math;

//模型--视图变换函数矩阵
public class ESTransform
{
//   public ESTransform()
//   {
//      mMatrixFloatBuffer = ByteBuffer.allocateDirect ( 16 * 4 )
//                           .order ( ByteOrder.nativeOrder() ).asFloatBuffer();
//   }
//   private FloatBuffer mMatrixFloatBuffer;
//   public FloatBuffer getAsFloatBuffer()
//   {
//      mMatrixFloatBuffer.put ( mMatrix ).position ( 0 );
//      return mMatrixFloatBuffer;
//   }
   /**
    * 将矩阵乘以比例缩放
    * @param sx x轴上的比例缩放因子
    * @param sy y轴上的比例缩放因子
    * @param sz z轴上的比例缩放因子
    */


   public void scale ( float sx, float sy, float sz )
   {
      mMatrix[0 * 4 + 0] *= sx;
      mMatrix[0 * 4 + 1] *= sx;
      mMatrix[0 * 4 + 2] *= sx;
      mMatrix[0 * 4 + 3] *= sx;

      mMatrix[1 * 4 + 0] *= sy;
      mMatrix[1 * 4 + 1] *= sy;
      mMatrix[1 * 4 + 2] *= sy;
      mMatrix[1 * 4 + 3] *= sy;

      mMatrix[2 * 4 + 0] *= sz;
      mMatrix[2 * 4 + 1] *= sz;
      mMatrix[2 * 4 + 2] *= sz;
      mMatrix[2 * 4 + 3] *= sz;
   }

   /**
    * 将矩阵乘以平移矩阵，返回平移后的新矩阵
    * @param tx x轴上的平移因子
    * @param ty y轴上的平移因子
    * @param tz z轴上的平移因子
    */
   public void translate ( float tx, float ty, float tz )
   {
      mMatrix[3 * 4 + 0] += ( mMatrix[0 * 4 + 0] * tx + mMatrix[1 * 4 + 0]
                              * ty + mMatrix[2 * 4 + 0] * tz );
      mMatrix[3 * 4 + 1] += ( mMatrix[0 * 4 + 1] * tx + mMatrix[1 * 4 + 1]
                              * ty + mMatrix[2 * 4 + 1] * tz );
      mMatrix[3 * 4 + 2] += ( mMatrix[0 * 4 + 2] * tx + mMatrix[1 * 4 + 2]
                              * ty + mMatrix[2 * 4 + 2] * tz );
      mMatrix[3 * 4 + 3] += ( mMatrix[0 * 4 + 3] * tx + mMatrix[1 * 4 + 3]
                              * ty + mMatrix[2 * 4 + 3] * tz );
   }

   /**
    * 将矩阵乘以旋转矩阵，返回新矩阵
    * @param angle 旋转角度，以度数表示
    * @param x 指定向量x的坐标
    * @param y 指定向量y的坐标
    * @param z 指定向量z的坐标
    */
   float[] rotMat = new float[16];

   public void rotate ( float angle, float x, float y, float z )
   {
      float sinAngle, cosAngle;
      float mag = ( float ) Math.sqrt ( ( double ) ( x * x + y * y + z * z ) );

      sinAngle = ( float ) Math.sin ( ( double ) ( angle * Math.PI / 180.0 ) );
      cosAngle = ( float ) Math.cos ( ( double ) ( angle * Math.PI / 180.0 ) );

      if ( mag > 0.0f )
      {
         float xx, yy, zz, xy, yz, zx, xs, ys, zs;
         float oneMinusCos;

         x /= mag;
         y /= mag;
         z /= mag;

         xx = x * x;
         yy = y * y;
         zz = z * z;
         xy = x * y;
         yz = y * z;
         zx = z * x;
         xs = x * sinAngle;
         ys = y * sinAngle;
         zs = z * sinAngle;
         oneMinusCos = 1.0f - cosAngle;

         rotMat[0 * 4 + 0] = ( oneMinusCos * xx ) + cosAngle;
         rotMat[0 * 4 + 1] = ( oneMinusCos * xy ) - zs;
         rotMat[0 * 4 + 2] = ( oneMinusCos * zx ) + ys;
         rotMat[0 * 4 + 3] = 0.0F;

         rotMat[1 * 4 + 0] = ( oneMinusCos * xy ) + zs;
         rotMat[1 * 4 + 1] = ( oneMinusCos * yy ) + cosAngle;
         rotMat[1 * 4 + 2] = ( oneMinusCos * yz ) - xs;
         rotMat[1 * 4 + 3] = 0.0F;

         rotMat[2 * 4 + 0] = ( oneMinusCos * zx ) - ys;
         rotMat[2 * 4 + 1] = ( oneMinusCos * yz ) + xs;
         rotMat[2 * 4 + 2] = ( oneMinusCos * zz ) + cosAngle;
         rotMat[2 * 4 + 3] = 0.0F;

         rotMat[3 * 4 + 0] = 0.0F;
         rotMat[3 * 4 + 1] = 0.0F;
         rotMat[3 * 4 + 2] = 0.0F;
         rotMat[3 * 4 + 3] = 1.0F;

         matrixMultiply ( rotMat, mMatrix );
      }
   }
   /**
    * 将矩阵乘以透视投影矩阵，返回新矩阵
    * @param left :指定左裁剪平面坐标
    * @param right :指定右裁剪平面坐标
    * @param bottom :指定下裁剪平面坐标
    * @param top:指定上裁剪平面坐标
    * @param nearZ:指定近深度裁剪平面坐标(必须为正)
    * @param farZ:指定远深度裁剪平面坐标(必须为正)
    */
   public void frustum ( float left, float right, float bottom, float top,
                         float nearZ, float farZ )
   {
      float deltaX = right - left;
      float deltaY = top - bottom;
      float deltaZ = farZ - nearZ;
      float[] frust = new float[16];

      if ( ( nearZ <= 0.0f ) || ( farZ <= 0.0f ) || ( deltaX <= 0.0f )
            || ( deltaY <= 0.0f ) || ( deltaZ <= 0.0f ) )
      {
         return;
      }

      frust[0 * 4 + 0] = 2.0f * nearZ / deltaX;
      frust[0 * 4 + 1] = frust[0 * 4 + 2] = frust[0 * 4 + 3] = 0.0f;

      frust[1 * 4 + 1] = 2.0f * nearZ / deltaY;
      frust[1 * 4 + 0] = frust[1 * 4 + 2] = frust[1 * 4 + 3] = 0.0f;

      frust[2 * 4 + 0] = ( right + left ) / deltaX;
      frust[2 * 4 + 1] = ( top + bottom ) / deltaY;
      frust[2 * 4 + 2] = - ( nearZ + farZ ) / deltaZ;
      frust[2 * 4 + 3] = -1.0f;

      frust[3 * 4 + 2] = -2.0f * nearZ * farZ / deltaZ;
      frust[3 * 4 + 0] = frust[3 * 4 + 1] = frust[3 * 4 + 3] = 0.0f;

      matrixMultiply ( frust, mMatrix );
   }

   /**
    * 将矩阵乘以透视投影矩阵，并返回新矩阵.相比frustum方法更简单
    * @param fovy 指定以度数表示的视野,0~180
    * @param aspect 渲染窗口的纵横比(宽度/高度)
    * @param nearZ 指定到近深度裁剪平面的距离，必须为正数
    * @param farZ  指定到远深度裁剪平面的距离，必须为正数
    */
   public void perspective ( float fovy, float aspect, float nearZ, float farZ )
   {
      float frustumW, frustumH;

      frustumH = ( float ) Math.tan ( fovy / 360.0 * Math.PI ) * nearZ;
      frustumW = frustumH * aspect;

      frustum ( -frustumW, frustumW, -frustumH, frustumH, nearZ, farZ );
   }

   /**
    * 乘以正交投影矩阵，并返回新矩阵
    * @param left 指定左裁剪平面坐标
    * @param right 指定右裁剪平面坐标
    * @param bottom 指定下裁剪平面坐标
    * @param top 指定上裁剪平面坐标
    * @param nearZ 指定近深度裁剪平面距离，可以为正为负
    * @param farZ 指定远裁剪平面坐标，可以为正为负
    */
   public void ortho ( float left, float right, float bottom, float top,
                       float nearZ, float farZ )
   {
      float deltaX = right - left;
      float deltaY = top - bottom;
      float deltaZ = farZ - nearZ;
      float[] orthoMat = makeIdentityMatrix();

      if ( ( deltaX == 0.0f ) || ( deltaY == 0.0f ) || ( deltaZ == 0.0f ) )
      {
         return;
      }

      orthoMat[0 * 4 + 0] = 2.0f / deltaX;
      orthoMat[3 * 4 + 0] = - ( right + left ) / deltaX;
      orthoMat[1 * 4 + 1] = 2.0f / deltaY;
      orthoMat[3 * 4 + 1] = - ( top + bottom ) / deltaY;
      orthoMat[2 * 4 + 2] = -2.0f / deltaZ;
      orthoMat[3 * 4 + 2] = - ( nearZ + farZ ) / deltaZ;

      matrixMultiply ( orthoMat, mMatrix );
   }

   /**
    * 将srcA和srcB矩阵相乘，返回结果.及其重要，基本最后都要用上
    * @param srcA 输入矩阵A
    * @param srcB 输入矩阵B
    */
   float[] tmp = new float[16];
   public void matrixMultiply ( float[] srcA, float[] srcB )
   {
      int i;

      for ( i = 0; i < 4; i++ )
      {
         tmp[i * 4 + 0] = ( srcA[i * 4 + 0] * srcB[0 * 4 + 0] )
                          + ( srcA[i * 4 + 1] * srcB[1 * 4 + 0] )
                          + ( srcA[i * 4 + 2] * srcB[2 * 4 + 0] )
                          + ( srcA[i * 4 + 3] * srcB[3 * 4 + 0] );

         tmp[i * 4 + 1] = ( srcA[i * 4 + 0] * srcB[0 * 4 + 1] )
                          + ( srcA[i * 4 + 1] * srcB[1 * 4 + 1] )
                          + ( srcA[i * 4 + 2] * srcB[2 * 4 + 1] )
                          + ( srcA[i * 4 + 3] * srcB[3 * 4 + 1] );

         tmp[i * 4 + 2] = ( srcA[i * 4 + 0] * srcB[0 * 4 + 2] )
                          + ( srcA[i * 4 + 1] * srcB[1 * 4 + 2] )
                          + ( srcA[i * 4 + 2] * srcB[2 * 4 + 2] )
                          + ( srcA[i * 4 + 3] * srcB[3 * 4 + 2] );

         tmp[i * 4 + 3] = ( srcA[i * 4 + 0] * srcB[0 * 4 + 3] )
                          + ( srcA[i * 4 + 1] * srcB[1 * 4 + 3] )
                          + ( srcA[i * 4 + 2] * srcB[2 * 4 + 3] )
                          + ( srcA[i * 4 + 3] * srcB[3 * 4 + 3] );
      }

      mMatrix = tmp;
   }

   /**
    * 返回矩阵的内存指针
    */
   public void matrixLoadIdentity()
   {
      for ( int i = 0; i < 16; i++ )
      {
         mMatrix[i] = 0.0f;
      }

      mMatrix[0 * 4 + 0] = 1.0f;
      mMatrix[1 * 4 + 1] = 1.0f;
      mMatrix[2 * 4 + 2] = 1.0f;
      mMatrix[3 * 4 + 3] = 1.0f;
   }


   private float[] makeIdentityMatrix()
   {
      float[] result = new float[16];

      for ( int i = 0; i < 16; i++ )
      {
         result[i] = 0.0f;
      }

      result[0 * 4 + 0] = 1.0f;
      result[1 * 4 + 1] = 1.0f;
      result[2 * 4 + 2] = 1.0f;
      result[3 * 4 + 3] = 1.0f;

      return result;
   }


   public float[] get()
   {
      return mMatrix;
   }

   private float[] mMatrix = new float[16];


}