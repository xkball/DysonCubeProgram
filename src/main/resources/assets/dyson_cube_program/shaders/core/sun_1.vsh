#version 150

#moj_import <minecraft:dynamictransforms.glsl>
#moj_import <minecraft:projection.glsl>

in vec3 Position;

out vec3 pos;

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
    pos = Position;
}
