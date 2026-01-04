#version 330

layout(std140) uniform DynamicTransforms {
    mat4 ModelViewMat;
    vec4 ColorModulator;
    vec3 ModelOffset;
    mat4 TextureMat;
};

layout(std140) uniform DualTexOffset{
    vec2 Offset;
};

layout(std140) uniform CustomColorModulator{
    vec4 CCM;
};

uniform sampler2D Sampler0;

in vec2 texCoordFront;
in vec4 vertexColor;

out vec4 fragColor;

void main() {
    vec2 uv = gl_FrontFacing ? texCoordFront : texCoordFront + Offset;
    vec4 color = gl_FrontFacing ? (texture(Sampler0, uv) * vertexColor) : texture(Sampler0, uv) * 0.9 + vertexColor * CCM * 0.1;

    if (color.a == 0.0) {
        discard;
    }

    fragColor = color * ColorModulator;
}