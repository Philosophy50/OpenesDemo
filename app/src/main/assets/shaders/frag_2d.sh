#version 300 es
precision mediump float;
in vec2 vTextureCoord;

uniform sampler2D sTexture;
uniform float aptt;


 out vec4 fragColor;
void main()                         
{
   vec4 mask = texture(sTexture, vTextureCoord);
    mask.a *= aptt;
   fragColor = mask ;
}              