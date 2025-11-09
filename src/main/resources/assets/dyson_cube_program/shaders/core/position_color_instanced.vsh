#version 430
#moj_import <minecraft:dynamictransforms.glsl>
#moj_import <minecraft:projection.glsl>
#moj_import <dyson_cube_program:trans_color_ssbo.glsl>

in vec3 Position;
in vec4 Color;

out vec4 vertexColor;

void main() {
    gl_Position = ProjMat * ModelViewMat * transMatColor[gl_InstanceID].transforms_ssbo * vec4(Position, 1.0);

    vertexColor = Color * transMatColor[gl_InstanceID].color_ssbo;
}
