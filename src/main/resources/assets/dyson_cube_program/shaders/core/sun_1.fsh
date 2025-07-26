#version 150

#moj_import <dyson_cube_program:noise.glsl>
#moj_import <minecraft:dynamictransforms.glsl>
#moj_import <minecraft:projection.glsl>

const mat3 m = mat3( 0.00,  0.80,  0.60,
                    -0.80,  0.36, -0.48,
                    -0.60, -0.48,  0.64 );

const vec3[] dirs = vec3[4](
    vec3(1.0,1.0,1.0),
    vec3(-1.0,1.0,1.0),
    vec3(1.0,-1.0,1.0),
    vec3(1.0,1.0,-1.0)
);

layout(std140) uniform SunUniform{
    vec3 RenderDir;
    float ClientTime;
    vec3 Color;
};

in vec3 pos;

out vec4 fragColor;

void main() {
    float time = ClientTime/1000.0;

    float nx = 1;
    vec3 p = pos*16.0;
    nx += gradient_noise_3t1(p-time*dirs[0]*16.0);
    p = m * p * 2.01;
    nx += 0.5 * gradient_noise_3t1(p+time*dirs[1]*16.0);
    p = m * p * 2.02;
    nx += 0.25 * gradient_noise_3t1(p+time*dirs[2]*16.0);
    nx = clamp(nx+0.2,0.5,10);

    vec3 c = Color * nx ;
    fragColor = vec4(c,0.6);
    //fragColor = vec4(vec3(theta),1);
}
