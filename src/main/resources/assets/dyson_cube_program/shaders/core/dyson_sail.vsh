#version 430
#moj_import <minecraft:dynamictransforms.glsl>
#moj_import <minecraft:projection.glsl>
#moj_import <dyson_cube_program:trans_color_ssbo.glsl>

layout(std140) uniform ScreenSize{
    vec2 screenSize;
};

in vec3 Position;
in vec4 Color;

out vec4 vertexColor;
out float strength;

void main() {
    gl_Position = ProjMat * ModelViewMat * transMatColor[gl_InstanceID].transforms_ssbo * vec4(Position, 1.0);
    vertexColor = transMatColor[gl_InstanceID].color_ssbo;
    gl_Position.x += (2.0/screenSize.x) * gl_Position.w * Color.r * Color.a;
    gl_Position.y += (2.0/screenSize.y) * gl_Position.w * Color.g * Color.a;
    strength = Color.a * gl_Position.z + (1 - Color.a);
}
