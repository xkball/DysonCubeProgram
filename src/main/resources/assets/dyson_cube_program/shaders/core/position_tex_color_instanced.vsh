#version 430

#moj_import <minecraft:dynamictransforms.glsl>
#moj_import <minecraft:projection.glsl>

struct TransMatColor{
    mat4 transforms_ssbo;
    vec4 color_ssbo;
};

layout(std140) buffer InstanceTransformColor {
    TransMatColor transMatColor[];
};

in vec3 Position;
in vec2 UV0;
in vec4 Color;

out vec2 texCoord0;
out vec4 vertexColor;

void main() {
    gl_Position = ProjMat * ModelViewMat * transMatColor[gl_InstanceID].transforms_ssbo * vec4(Position, 1.0);

    texCoord0 = UV0;
    vertexColor = Color * transMatColor[gl_InstanceID].color_ssbo;
}
