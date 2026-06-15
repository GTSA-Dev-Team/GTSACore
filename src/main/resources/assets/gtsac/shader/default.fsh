#version 450 core

in vec3 fNormal;
in vec2 fUV;

uniform sampler2D tex0;
uniform sampler2D tex1;
uniform vec2 packedLight;

out vec4 FragColor;

void main()
{
    vec3 N = normalize(fNormal);

    // Hardcoded light direction (from above + front)
    vec3 lightDir = normalize(vec3(-0.3, -1.0, -0.2));

    // Simple ambient + diffuse
    float diff = max(dot(N, -lightDir), 0.0);

    float ambient = 0.25;

    vec3 baseColor = vec3(1.0, 1.0, 1.0); // white model tint

    vec3 color = baseColor * (ambient + diff * 0.75);

    FragColor = texture(tex0, fUV) * texture(tex1, packedLight); // * vec4(color, 1.0);
}