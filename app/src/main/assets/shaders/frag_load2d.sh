#version 300 es
precision mediump float;
in vec2 vTextureCoord;
in float vxPosition;
uniform float xPosition;
uniform sampler2D sTexture;
uniform float aptt;
 out vec4 fragColor;
void main()
{
    float ff=xPosition;
   if(vxPosition>= 0.0 && vxPosition<ff){//(ff/625.0)){  //
       fragColor = vec4(0.411,0.878,0.807,0.5);
   }else{
  vec4 mask = texture(sTexture, vTextureCoord);
          mask.a *= aptt;
          fragColor = mask ;

   }
}              