#version 450 core

layout (location = 0) in vec3 inPos;
layout (location = 1) in vec2 inUV;
layout (location = 2) in vec3 inNormal;

out vec3 vNormal;

uniform mat4 projMatrix;
uniform mat4 modelViewMatrix;

void main() {
    gl_Position = projMatrix * modelViewMatrix * vec4(inPos, 1);
    vNormal = inNormal;
}