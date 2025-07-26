#version 150

in vec3 Position;

#moj_import <minecraft:dynamictransforms.glsl>
#moj_import <minecraft:projection.glsl>

layout(std140) uniform SunUniform{
    vec3 RenderDir;
    float ClientTime;
    vec3 Color;
};

out vec3 pos;
out float theta;
void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
    theta = pow(1 - dot(normalize(Position),normalize(-RenderDir)),4);
    pos = Position;
}
