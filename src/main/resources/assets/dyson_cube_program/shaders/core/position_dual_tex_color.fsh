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

const float[] scale = float[11](1.3/1.0, 1.2/1.0, 1.1/1.0, 1.05/1.0, 1.03/1.0, 1.01/1.0, 1.0, 1.0, 1.0 ,1.0, 1.0);
const vec2 UVCenter = vec2(0.5,0.5);

uniform sampler2D TexFront;
uniform sampler2D TexBack;
uniform sampler2D TexNoise;

in vec2 texCoordFront;
in vec4 vertexColor;

//Fun fact: 如果没有成功绑定多个写的color attchment, 会都写到一个上
layout (location = 0) out vec4 fragColor;
layout (location = 1) out vec4 bloomColor;

void main() {
    float lod = textureQueryLod(TexFront, texCoordFront).x;
    vec2 uv = (texCoordFront-UVCenter) * scale[int(lod)] + UVCenter;
    if(uv.x < 0.0 || uv.x > 1.0 || uv.y < 0.0 || uv.y > 1.0 || uv.x + uv.y < 0.5 || uv.x + uv.y > 1.5) discard;
    float a = 1.0;
    vec2 screenUV = gl_FragCoord.xy / vec2(1280.0,720.0);
    if(lod > 5.0 && (uv.x < 0.05 || uv.x > 0.95 || uv.y < 0.05 || uv.y > 0.95 || uv.x + uv.y < 0.55 || uv.x + uv.y > 1.45)){
        float noise = texture(TexNoise, screenUV).r;
        a = (a + noise) / 2.5;
    }
    vec4 color = gl_FrontFacing ? (texture(TexFront, uv) * vertexColor) : texture(TexBack, uv) * 0.9 + vertexColor * CCM * 0.1;
    fragColor = color * ColorModulator;
    fragColor.a = a;
    //wip
    bloomColor = vec4(0.0,0.0,0.0,1.0);
}