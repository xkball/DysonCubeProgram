
struct TransMatColor{
    mat4 transforms_ssbo;
    vec4 color_ssbo;
};

layout(std140) buffer InstanceTransformColor {
    TransMatColor transMatColor[];
};