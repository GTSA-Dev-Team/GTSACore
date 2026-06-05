#version 450 core

in vec3 vNormal;

out vec4 FragColor;

void main()
{
    vec3 N = normalize(vNormal);

    // Hardcoded light direction (from above + front)
    vec3 lightDir = normalize(vec3(-0.3, -1.0, -0.2));

    // Simple ambient + diffuse
    float diff = max(dot(N, -lightDir), 0.0);

    float ambient = 0.25;

    vec3 baseColor = vec3(1.0, 1.0, 1.0); // white model tint

    vec3 color = baseColor * (ambient + diff * 0.75);

    FragColor = vec4(color, 1.0);
}