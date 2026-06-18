#version 450 core

layout (location = 0) in vec3 vPos;
layout (location = 1) in vec4 vColor;
layout (location = 2) in vec3 vNormal;
layout (location = 3) in vec2 vUV;
layout (location = 4) in float progress;

out vec4 fColor;
out vec3 fNormal;
out vec2 fUV;
out flat float fProgress;

uniform mat4 projMatrix;

void main() {
    gl_Position = projMatrix * vec4(vPos, 1);
    fColor = vColor;
    fNormal = vNormal;
    fUV = vUV;
    fProgress = progress;
}