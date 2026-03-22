#version 330

uniform sampler2D JfaInSampler;

out vec4 fragColor;

#ifndef JFA_STEP
#define JFA_STEP 1
#endif

float dist2_to_seed(vec2 pixelCenter, vec4 candidate, ivec2 size) {
    if (candidate.a <= 0.0 || candidate.w <= 0.0) {
        return 3.402823e38;
    }

    vec2 seedPx = candidate.xy * vec2(size);
    vec2 delta = seedPx - pixelCenter;
    return dot(delta, delta);
}

void main() {
    ivec2 pixel = ivec2(gl_FragCoord.xy);
    ivec2 size = textureSize(JfaInSampler, 0);

    vec2 pixelCenter = vec2(pixel) + vec2(0.5);

    vec4 best = texelFetch(JfaInSampler, pixel, 0);
    float bestDist2 = dist2_to_seed(pixelCenter, best, size);

    for (int oy = -1; oy <= 1; oy++) {
        for (int ox = -1; ox <= 1; ox++) {
            ivec2 samplePixel = pixel + ivec2(ox, oy) * JFA_STEP;
            samplePixel = clamp(samplePixel, ivec2(0), size - ivec2(1));
            vec4 candidate = texelFetch(JfaInSampler, samplePixel, 0);
            float d2 = dist2_to_seed(pixelCenter, candidate, size);
            if (d2 < bestDist2) {
                bestDist2 = d2;
                best = candidate;
            }
        }
    }

    fragColor = best;
}

