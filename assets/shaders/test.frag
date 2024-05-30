#define HIGHP

//shades of test
#define S2 vec3(100.0, 93.0, 49.0) / 100.0
#define S1 vec3(100.0, 60.0, 25.0) / 100.0
#define NSCALE 200.0 / 2.0

uniform vec2 u_campos;
uniform vec2 u_resolution;
uniform float u_time;

varying vec2 v_texCoords;

vec4 blendOver(vec4 a, vec4 b) {
    float newAlpha = mix(b.w, 1.0, a.w);
    vec3 newColor = mix(b.w * b.xyz, a.xyz, a.w);
    float divideFactor = (newAlpha > 0.001 ? (1.0 / newAlpha) : 1.0);
    return vec4(divideFactor * newColor, newAlpha);
}

void main(){
    vec2 c = v_texCoords.xy;
    vec2 coords = (c * u_resolution) + u_campos;

    float len = distance(coords,vec2(0.5,0.5));

    if(len < 0.1){
        gl_FragColor = vec4(0,0,0,1);
    }else if(len < 0.2){
        gl_FragColor = vec4((0.4-len)/0.6,0,0,1);
    }
}