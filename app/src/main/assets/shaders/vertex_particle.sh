#version 300 es
uniform float u_time;
uniform vec3 u_centerPosition;
layout(location = 0) in float a_lifetime;
layout(location = 1) in vec3 a_startPosition;
layout(location = 2) in vec3 a_endPosition;

layout(location = 3) in vec3 a_colorPosition;
out vec3 v_colorPosition;

out float v_lifetime;
void main()
{
    if ( u_time <= a_lifetime )
    {
     gl_Position.xyz = a_startPosition;
     u_time *     a_endPosition;
     gl_Position.w = 1.0;
    }
    else{
        gl_Position = vec4( -1000, -1000, 0, 0 );
    }
    v_colorPosition = a_colorPosition;
    v_lifetime = 1.0 - ( u_time / a_lifetime );
    v_lifetime = clamp ( v_lifetime, 0.0, 1.0 );
    gl_PointSize = ( v_lifetime * v_lifetime ) * 10.0;
}