#version 300 es
precision mediump float;
uniform mat4 uMVPMatrix;
in vec3 vPosition;
in vec2 vTexCoor;
out vec2 vTextureCoord;


void main()     
{                            		
   gl_Position = uMVPMatrix * vec4(vPosition,1);
   vTextureCoord = vTexCoor;
}                      