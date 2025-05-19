#ifdef GL_ES
precision mediump float;
#endif

uniform vec2 u_resolution;
uniform vec2 u_playerPos;
uniform float u_radius;

void main() {
    vec2 fragCoord = gl_FragCoord.xy;
    float dist = distance(fragCoord, u_playerPos);

    if (dist < u_radius) {
        discard; // torna essa parte visível (sem escurecimento)
    } else {
        gl_FragColor = vec4(0.0, 0.0, 0.0, 0.85); // escurece o resto
    }
}
