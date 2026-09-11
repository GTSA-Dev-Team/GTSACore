#version 450 core

layout (location = 0) in vec3 vPos;
layout (location = 1) in vec4 vColor;

out vec4 fColor;

uniform mat4 localMatrix;
uniform mat4 projMatrix;

void main() {
    gl_Position = projMatrix * localMatrix * vec4(vPos, 1);
    fColor = vColor;
}