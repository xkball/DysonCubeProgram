#version 150

#moj_import <dyson_cube_program:noise.glsl>

uniform float ClientTime;
uniform vec3 Color;

in vec2 uv;

out vec4 fragColor;

const mat2 m = mat2( 1.6,  1.2, -1.2,  1.6 );

void main() {
    float time = ClientTime/1000.0;
    float fade = 1 - smoothstep(0.7, 2.0, uv.x);
    vec2 p = (vec2(fade,uv.y) + vec2(time,0)) * 4;
    float n = wrapped_simplex_noise_2t1(p);
    //float n = 0.0;
    n += 1 - gaborNoise(p*2, 4.0, 2.0, 0.5, 0.2, 1);
    p = m * p * 2.01;
    n += 0.5 * wrapped_simplex_noise_2t1(p);
    p = m * p * 2.02;
    n += 0.25 * wrapped_simplex_noise_2t1(p);
    p = m * p * 2.03;
    n += 0.125 * wrapped_simplex_noise_2t1(p);
    //n += fade;
    float intensity = fade * (0.5 * n);
    vec3 c = Color * n;
    fragColor = vec4(c,intensity);
    //fragColor = vec4(vec3(theta),1);
}
