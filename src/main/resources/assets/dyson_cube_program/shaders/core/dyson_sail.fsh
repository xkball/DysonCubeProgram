#version 330

#moj_import <minecraft:dynamictransforms.glsl>

in vec4 vertexColor;
in float strength;
out vec4 fragColor;

void main() {
    vec4 color = vertexColor;
    if (color.a == 0.0) {
        discard;
    }
    fragColor = color * ColorModulator;
    fragColor.a *= strength;
}
