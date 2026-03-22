#version 330

uniform sampler2D InSampler;

out vec4 fragColor;

#ifndef OUTLINE_RADIUS_PIXELS
#define OUTLINE_RADIUS_PIXELS 10
#endif

vec4 eo_pick_max_alpha_neighbor(ivec2 pixel) {
    vec4 bestColor = vec4(0.0);
    float bestAlpha = 0.0;

    for (int oy = -OUTLINE_RADIUS_PIXELS; oy <= OUTLINE_RADIUS_PIXELS; oy++) {
        for (int ox = -OUTLINE_RADIUS_PIXELS; ox <= OUTLINE_RADIUS_PIXELS; ox++) {
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

    return bestColor;
}

void main() {
    ivec2 pixel = ivec2(gl_FragCoord.xy);

    // Keep legacy behavior: interior is removed, only edge candidates survive.
    vec4 center = texelFetch(InSampler, pixel, 0);
    if (center.a > 0.0) {
        discard;
    }

    vec4 bestColor = eo_pick_max_alpha_neighbor(pixel);
    float bestAlpha = bestColor.a;

    if (bestAlpha <= 0.0) {
        discard;
    }

    fragColor = bestColor;
}

