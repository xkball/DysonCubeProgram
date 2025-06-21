#version 150

vec3 hash3(vec3 p) {
    p = vec3(dot(p,vec3(127.1,311.7, 74.7)),
    dot(p,vec3(269.5,183.3,246.1)),
    dot(p,vec3(113.5,271.9,124.6)));

    return -1.0 + 2.0*fract(sin(p)*43758.5453123);
}

vec2 hash2(vec2 p) {
    p = vec2( dot(p,vec2(127.1,311.7)), dot(p,vec2(269.5,183.3)) );
    return -1.0 + 2.0*fract(sin(p)*43758.5453123);
}

float gaussian(float x, float sigma) {
    return exp(-x * x / (2.0 * sigma * sigma));
}

float simplex_noise_2t1(vec2 p) {
    const float K1 = 0.366025404; // (sqrt(3)-1)/2;
    const float K2 = 0.211324865; // (3-sqrt(3))/6;

    vec2  i = floor( p + (p.x+p.y)*K1 );
    vec2  a = p - i + (i.x+i.y)*K2;
    float m = step(a.y,a.x);
    vec2  o = vec2(m,1.0-m);
    vec2  b = a - o + K2;
    vec2  c = a - 1.0 + 2.0*K2;
    vec3  h = max( 0.5-vec3(dot(a,a), dot(b,b), dot(c,c) ), 0.0 );
    vec3  n = h*h*h*h*vec3( dot(a,hash2(i+0.0)), dot(b,hash2(i+o)), dot(c,hash2(i+1.0)));
    return dot( n, vec3(70.0) );
}

float gradient_noise_3t1(vec3 p) {
    vec3 i = floor( p );
    vec3 f = fract( p );

    vec3 u = f*f*(3.0-2.0*f);

    return mix( mix( mix( dot( hash3( i + vec3(0.0,0.0,0.0) ), f - vec3(0.0,0.0,0.0) ),
                          dot( hash3( i + vec3(1.0,0.0,0.0) ), f - vec3(1.0,0.0,0.0) ), u.x),
                     mix( dot( hash3( i + vec3(0.0,1.0,0.0) ), f - vec3(0.0,1.0,0.0) ),
                          dot( hash3( i + vec3(1.0,1.0,0.0) ), f - vec3(1.0,1.0,0.0) ), u.x), u.y),
                mix( mix( dot( hash3( i + vec3(0.0,0.0,1.0) ), f - vec3(0.0,0.0,1.0) ),
                          dot( hash3( i + vec3(1.0,0.0,1.0) ), f - vec3(1.0,0.0,1.0) ), u.x),
                     mix( dot( hash3( i + vec3(0.0,1.0,1.0) ), f - vec3(0.0,1.0,1.0) ),
                          dot( hash3( i + vec3(1.0,1.0,1.0) ), f - vec3(1.0,1.0,1.0) ), u.x), u.y), u.z );
}

vec2 simplex_noise_3t2(vec3 p) {
    return vec2(simplex_noise_2t1(p.xy),simplex_noise_2t1(p.yz));
}

vec3 simplex_noise_3t3(vec3 p) {
    return vec3(simplex_noise_2t1(p.yx+p.z),simplex_noise_2t1(p.zy+p.x),simplex_noise_2t1(p.xz+p.y));
}

vec2 cell_noise(vec3 p ) {
    vec3 theCell = floor(p);
    float dis1 = 114514.;
    float dis2 = 114514.;
    for(int x = -1; x < 2; ++x){
        for(int y = -1; y < 2; ++y){
            for(int z = -1; z < 2; ++z){
                vec3 cell = theCell + vec3(x,y,z) + vec3(0.1,0.2,-0.1);
                //vec2 cell_ = vec2(cell.x *23. + cell.y * 41.,iTime/2.);
                //vec3 cell_ = vec3(noise2d(cell),iTime/2.);
                vec3 pos = cell + simplex_noise_3t3(cell);
                float dis = distance(pos,p);
                if(dis < dis1){
                    dis2 = dis1;
                    dis1 = dis;
                }
                else{
                    dis2 = min(dis2,dis);
                }
            }
        }
    }
    float delta = dis2 - dis1;
    //return delta > 0.1 ? 1 : dis1;
    return vec2(clamp(delta,0,1),dis1);
}

float gaborKernel(vec2 x, float frequency, float sigma, float phi, vec2 direction) {
    float dotProd = dot(x, direction);
    return gaussian(dotProd, sigma) * cos(2.0 * 3.1415926 * frequency * dotProd + phi);
}

float gaborNoise(vec2 uv, float impulseDensity, float frequency, float sigma, float baseAngle, float spread) {
    float sum = 0.0;
    float weight = 0.0;

    for (int i = -2; i <= 2; i++) {
        for (int j = -2; j <= 2; j++) {
            vec2 cell = floor(uv) + vec2(i, j);
            vec2 rand = hash2(cell);
            vec2 pos = cell + rand;

            vec2 offset = uv - pos;

            float theta = baseAngle + (hash2(cell + 2.0).x - 0.5) * spread;
            vec2 dir = vec2(cos(theta), sin(theta));
            float phi = hash2(cell + 3.0).x * 6.2831;

            float k = gaborKernel(offset, frequency, sigma, phi, dir);

            sum += k;
            weight += 1.0;
        }
    }

    return sum / weight;
}

float wrapped_simplex_noise_2t1(vec2 p){
    return simplex_noise_2t1(vec2(simplex_noise_2t1(p + vec2(0.114)),simplex_noise_2t1(p + vec2(0.514))));
}