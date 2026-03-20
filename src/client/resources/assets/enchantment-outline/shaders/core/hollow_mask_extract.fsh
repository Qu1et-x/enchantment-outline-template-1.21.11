#version 330

uniform sampler2D InSampler;

out vec4 fragColor;

void main() {
    ivec2 pixel = ivec2(gl_FragCoord.xy);

    // Keep legacy behavior: interior is removed, only edge candidates survive.
    vec4 center = texelFetch(InSampler, pixel, 0);
    if (center.a > 0.0) {
        discard;
    }

    vec4 bestColor = vec4(0.0);
    float bestAlpha = 0.0;

    for (int oy = -10; oy <= 10; oy++) {
        for (int ox = -10; ox <= 10; ox++) {
            if (ox == 0 && oy == 0) {
                continue;
            }

            ivec2 samplePixel = pixel + ivec2(ox, oy);
            vec4 sampleColor = texelFetch(InSampler, samplePixel, 0);
            if (sampleColor.a > bestAlpha) {
                bestAlpha = sampleColor.a;
                bestColor = sampleColor;
            }
        }
    }

    if (bestAlpha <= 0.0) {
        discard;
    }

    fragColor = bestColor;
}

