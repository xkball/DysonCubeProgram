#version 150

#moj_import <dyson_cube_program:noise.glsl>

const mat3 m = mat3( 0.00,  0.80,  0.60,
                    -0.80,  0.36, -0.48,
                    -0.60, -0.48,  0.64 );

const vec3[] dirs = vec3[4](
    vec3(1.0,1.0,1.0),
    vec3(-1.0,1.0,1.0),
    vec3(1.0,-1.0,1.0),
    vec3(1.0,1.0,-1.0)
);

uniform float ClientTime;
uniform vec3 Color;

in vec3 pos;
in float theta;

out vec4 fragColor;

vec3 gamma(vec3 color) {
    return pow(color, vec3(0.943));
}

void main() {
    float time = ClientTime/1000.0;
    vec2 nv = cell_noise((pos+time*dirs[0])*4.0);
    float nx = 1 - nv.x;
    vec3 p = pos*16.0;
    nx += gradient_noise_3t1(p-time*dirs[1]*16.0);
    p = m * p * 2.01;
    nx += 0.5 * gradient_noise_3t1(p+time*dirs[2]*16.0);
    p = m * p * 2.02;
    nx += 0.25 * gradient_noise_3t1(p+time*dirs[3]*16.0);
    p = m * p * 2.03;
    nx += 0.125 * gradient_noise_3t1(p+time*dirs[0]*16.0);
    //nx += 0.25 * cell_noise(p+time*dirs[3]*16.0).x;
    nx = clamp(nx+0.2,0.5,10);
    nv *= 2;
    nx *= max(1,nv.y);

    vec3 c = mix(gamma(Color * nx),Color,theta);
    fragColor = vec4(c,1.0);
    //fragColor = vec4(vec3(theta),1);
}
