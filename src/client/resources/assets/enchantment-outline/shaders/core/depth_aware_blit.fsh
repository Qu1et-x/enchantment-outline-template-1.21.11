#version 330

uniform sampler2D HollowSampler;
uniform sampler2D RawMaskSampler;
uniform sampler2D MaskDepthSampler;
uniform sampler2D SceneDepthSampler;

out vec4 fragColor;

const int OUTLINE_RADIUS_PIXELS = 10;
const float DEPTH_EPSILON = 0.00001;
const float MIN_OUTPUT_ALPHA = 0.6;

bool eo_is_hollow_candidate(ivec2 pixel, out vec4 hollowColor) {
    hollowColor = texelFetch(HollowSampler, pixel, 0);
    return hollowColor.a > 0.0;
}

void eo_accumulate_outline_from_raw(ivec2 pixel, out float bestAlpha, out float bestDepth) {
    bestAlpha = 0.0;
    bestDepth = 1.0;

    for (int oy = -OUTLINE_RADIUS_PIXELS; oy <= OUTLINE_RADIUS_PIXELS; oy++) {
        for (int ox = -OUTLINE_RADIUS_PIXELS; ox <= OUTLINE_RADIUS_PIXELS; ox++) {
            if (ox == 0 && oy == 0) {
                continue;
            }

            ivec2 samplePixel = pixel + ivec2(ox, oy);
            vec4 sampleColor = texelFetch(RawMaskSampler, samplePixel, 0);
            if (sampleColor.a <= 0.0) {
                continue;
            }

            float sampleDepth = texelFetch(MaskDepthSampler, samplePixel, 0).r;
            bestDepth = min(bestDepth, sampleDepth);
            bestAlpha = max(bestAlpha, sampleColor.a);
        }
    }
}

bool eo_is_depth_visible(ivec2 pixel, float bestDepth) {
    float sceneDepth = texelFetch(SceneDepthSampler, pixel, 0).r;
    return bestDepth <= sceneDepth + DEPTH_EPSILON;
}

void main() {
    ivec2 pixel = ivec2(gl_FragCoord.xy);

    // Hollow mask already removed interior pixels; only keep preprocessed edge candidates.
    vec4 center;
    if (!eo_is_hollow_candidate(pixel, center)) {
        discard;
    }

    float bestAlpha;
    float bestDepth;
    eo_accumulate_outline_from_raw(pixel, bestAlpha, bestDepth);
    bestAlpha = max(bestAlpha, center.a);

    if (bestAlpha <= 0.0) {
        discard;
    }

    if (!eo_is_depth_visible(pixel, bestDepth)) {
        discard;
    }

    // Slightly boost alpha so thin outlines stay visible.
    fragColor = vec4(center.rgb, max(bestAlpha, MIN_OUTPUT_ALPHA));
}


