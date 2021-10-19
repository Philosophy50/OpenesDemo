#version 300 es
layout(location = 0) in float a_tData;//t 取值数组
uniform vec4 u_StartEndData;//起始点和终止点
uniform vec4 u_ControlData;//控制点
uniform mat4 u_MVPMatrix;
uniform float u_Offset;//y轴方向做一个动态偏移

vec2 fun2(in vec2 p0, in vec2 p1, in vec2 p2, in vec2 p3, in float t)
{
    vec2 q0 = mix(p0, p1, t);
    vec2 q1 = mix(p1, p2, t);
    vec2 q2 = mix(p2, p3, t);

    vec2 r0 = mix(q0, q1, t);
    vec2 r1 = mix(q1, q2, t);

    return mix(r0, r1, t);
}

void main() {

    vec4 pos;
    pos.w = 1.0;

    vec2 p0 = u_StartEndData.xy;
    vec2 p3 = u_StartEndData.zw;

    vec2 p1 = u_ControlData.xy;
    vec2 p2 = u_ControlData.zw;

    p0.y *= u_Offset;
    p1.y *= u_Offset;
    p2.y *= u_Offset;
    p3.y *= u_Offset;

    float t = a_tData;

    vec2 point = fun2(p0, p1, p2, p3, t);

    pos.xy = point;


    gl_PointSize = 20.0f;//设置点的大小
    gl_Position = u_MVPMatrix * pos;
}