#version 430
#moj_import <minecraft:dynamictransforms.glsl>
#moj_import <minecraft:projection.glsl>

layout(std140, binding = 0) buffer InstanceTransform {
    mat4 transforms_ssbo[];
};

in vec3 Position;
in vec4 Color;

out vec4 vertexColor;

void main() {
    gl_Position = ProjMat * ModelViewMat * transforms_ssbo[gl_InstanceID] * vec4(Position, 1.0);

    vertexColor = Color;
}
