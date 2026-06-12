#version 450 core

layout (location = 0) in vec3 vPos;
layout (location = 1) in vec2 vUV;
layout (location = 2) in vec3 vNormal;

out vec3 fNormal;
out vec2 fUV;

uniform mat4 projMatrix;
uniform mat4 modelViewMatrix;

void main() {
    gl_Position = projMatrix * modelViewMatrix * vec4(vPos, 1);
    fNormal = vNormal;
    fUV = vUV;
}