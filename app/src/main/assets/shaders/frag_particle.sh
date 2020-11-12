#version 300 es
precision mediump float;
uniform vec4 u_color;
uniform float s_random;
in float v_lifetime;
in vec3 v_colorPosition;

layout(location = 0) out vec4 fragColor;
uniform sampler2D s_texture;
void main()
{
    vec4 texColor;
    texColor = texture( s_texture, gl_PointCoord );

    texColor.r = texColor.r + v_colorPosition.r;
    texColor.r = texColor.g + v_colorPosition.g;
    texColor.r = texColor.b + v_colorPosition.b;
    fragColor = texColor;

//          fragColor = vec4( u_color ) * texColor;
    fragColor.a *= v_lifetime;
}