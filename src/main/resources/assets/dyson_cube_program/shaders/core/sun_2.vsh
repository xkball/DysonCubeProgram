#version 150

in vec3 Position;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;

out vec2 uv;

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
    uv = vec2(length(Position.xy),atan(Position.y,Position.x));
}
