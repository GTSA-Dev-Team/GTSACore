#version 450 core

in vec4 fColor;
in vec3 fNormal;
in vec2 fUV;
in flat float fProgress;

uniform sampler2D tex0;

out vec4 FragColor;

void main() {
    FragColor = mix(texture(tex0, fUV) * fColor, fColor, fProgress);
}