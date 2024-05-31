uniform vec2 u_campos;
uniform vec2 u_resolution;
uniform float u_time;

uniform int u_firenum;
uniform vec4 u_fires[MAX_NUM];
uniform float u_rotations[MAX_NUM];
uniform vec4 u_colors[MAX_NUM];

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
    vec4 color = vec4(0);

    for (int i = 0; i < u_firenum; i++){
        vec4 fire = u_fires[i];
        float len = distance(coords, fire.rg);
        if (len < fire.b){
            color = blendOver(texture2D(u_texture, c).rgba, u_colors[i] * (fire.b - len / 1.5) / fire.b);
            if ((len / fire.b) > 0.9){
                float ro;
                if (coords.x - fire.r == 0){
                    ro = 90;
                } else {
                    ro = atan((coords.y - fire.g)/(coords.x - fire.r));
                }
                color = blendOver(texture2D(u_texture,
                c + vec2(cos(ro), sin(ro)) * len/ 25),
                u_colors[i] * (fire.b - len / 1.5) / fire.b);
            }
        }
    }

    gl_FragColor = color;
}