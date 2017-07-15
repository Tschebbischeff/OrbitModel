#version 150 core

in vec4 vertexColor;
in vec3 worldNormal;

out vec4 fragColor;

uniform bool mode;

void main() {
    if (mode) {
        fragColor = vec4(vertexColor.xyz, vertexColor.w * 0.1);
    } else {
        fragColor = vertexColor;
    }
}