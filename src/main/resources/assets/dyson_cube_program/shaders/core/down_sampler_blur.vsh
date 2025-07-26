#version 150

#moj_import <minecraft:projection.glsl>

layout(std140) uniform DownSampler{
    float Factor;
    int Radius;
    vec2 BlurDir;
    vec2 OutSize;
};

in vec4 Position;
out vec2 texCoord;

void main() {
    vec4 outPos = ProjMat * vec4(Position.xy, 0.0, 1.0);
    gl_Position = vec4(outPos.xy, 0.2, 1.0);
    texCoord = Position.xy/(OutSize * Factor);
}