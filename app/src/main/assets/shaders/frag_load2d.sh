#version 300 es
precision mediump float;
in vec2 vTextureCoord;
in float vxPosition;
uniform float xPosition;
uniform float x2Position;
uniform sampler2D sTexture;
uniform float aptt;
 out vec4 fragColor;
void main()
{
    float ff=xPosition;
   if(vxPosition>= x2Position && vxPosition<ff){
       fragColor = vec4(1.0,0.0,0.0,1.0);
   }else{
    vec4 mask = texture(sTexture, vTextureCoord);
          mask.a *= aptt;
          fragColor = mask ;

   }
}              