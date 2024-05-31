uniform vec2 u_campos;
uniform vec2 u_resolution;
uniform float u_time;

uniform vec4[num] fires;
uniform float[num] rotations;
uniform vec4[num] colors;

uniform sampler2D u_texture;

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
    vec4 color = texture2D(u_texture, c);

    for (float i = 0.0;i<num;i++){
        float len = distance(coords, fires[i].rg);
        if (len < fires[i].b){
            color = blendOver(u_texture, colors[i] * (fires[i].b - len) / fires[i].b);
        }
    }

    gl_FragColor = color;
}