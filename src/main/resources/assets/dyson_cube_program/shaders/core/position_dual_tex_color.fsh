#version 400

layout(std140) uniform DynamicTransforms {
    mat4 ModelViewMat;
    vec4 ColorModulator;
    vec3 ModelOffset;
    mat4 TextureMat;
};

layout(std140) uniform CustomColorModulator{
    vec4 CCM;
};

layout(std140) uniform ScreenSize{
    vec2 size;
};

const vec2 UVCenter = vec2(0.5,0.5);

uniform sampler2D TexFront;
uniform sampler2D TexBack;
uniform sampler2D TexNoise;

in vec2 texCoordFront;
in vec4 vertexColor;

out vec4 fragColor;

void main() {
    float lod = textureQueryLod(TexFront, texCoordFront).x;
    float scale = max(115 - lod*2, 100);
    vec2 uv = (texCoordFront-UVCenter) * scale/100.0 + UVCenter;
    if(uv.x < 0.0 || uv.x > 1.0 || uv.y < 0.0 || uv.y > 1.0 || uv.x + uv.y < 0.5 || uv.x + uv.y > 1.5) discard;
    float a = 1.0;
    vec2 screenUV = gl_FragCoord.xy / size;
    if(lod > 5.0 && (uv.x < 0.05 || uv.x > 0.95 || uv.y < 0.05 || uv.y > 0.95 || uv.x + uv.y < 0.55 || uv.x + uv.y > 1.45)){
        float noise = texture(TexNoise, screenUV).r;
        a = (a + noise) / 2.5;
    }
    vec4 color = gl_FrontFacing ? (texture(TexFront, uv) * vertexColor) : texture(TexBack, uv) * 0.9 + vertexColor * CCM * 0.1;
    fragColor = color * ColorModulator;
    fragColor.a = a;
}