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

const vec2 UVCenter = vec2(0.5,0.5);

uniform sampler2D TexBack;

in vec2 texCoordFront;
in vec4 vertexColor;

out vec4 fragColor;

void main() {
    float lod = textureQueryLod(TexBack, texCoordFront).x;
    float scale = max(115 - lod*2, 100);
    vec2 uv = (texCoordFront-UVCenter) * scale/100.0 + UVCenter;
    if(uv.x < 0.0 || uv.x > 1.0 || uv.y < 0.0 || uv.y > 1.0 || uv.x + uv.y < 0.5 || uv.x + uv.y > 1.5) discard;
    if(gl_FrontFacing){
        fragColor = vec4(0.0);
    }
    else{
        fragColor = texture(TexBack, uv) * ColorModulator;
    }

}