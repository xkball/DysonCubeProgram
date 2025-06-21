#version 150

in vec3 Position;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform vec3 RenderDir;


out vec3 pos;
out float theta;
void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
    theta = pow(1 - dot(normalize(Position),normalize(-RenderDir)),4);
    pos = Position;
}
