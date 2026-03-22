#version 330

uniform sampler2D HollowSampler;
uniform sampler2D RawMaskSampler;
uniform sampler2D MaskDepthSampler;
uniform sampler2D SceneDepthSampler;

#ifdef JFA_EDGE
uniform sampler2D JfaFieldSampler;
#endif

out vec4 fragColor;

#ifndef OUTLINE_RADIUS
#define OUTLINE_RADIUS 10
#endif

#ifndef ALPHA_THRESHOLD
#define ALPHA_THRESHOLD 0.001
#endif

#ifndef DEPTH_EPSILON
#define DEPTH_EPSILON 0.00001
#endif

#ifndef OUTLINE_COLOR_R
#define OUTLINE_COLOR_R 1.0
#endif

#ifndef OUTLINE_COLOR_G
#define OUTLINE_COLOR_G 1.0
#endif

#ifndef OUTLINE_COLOR_B
#define OUTLINE_COLOR_B 1.0
#endif

#ifndef OUTLINE_COLOR_MIX
#define OUTLINE_COLOR_MIX 0.0
#endif

const float MIN_OUTPUT_ALPHA = 0.6;

bool eo_compute_edge(ivec2 pixel, out vec3 edgeColor, out float edgeAlpha) {
    vec4 center = texelFetch(HollowSampler, pixel, 0);
    if (center.a <= ALPHA_THRESHOLD) {
        return false;
    }

    edgeColor = center.rgb;
    edgeAlpha = center.a;

#ifdef LEGACY_EDGE
    for (int oy = -OUTLINE_RADIUS; oy <= OUTLINE_RADIUS; oy++) {
        for (int ox = -OUTLINE_RADIUS; ox <= OUTLINE_RADIUS; ox++) {
            if (ox == 0 && oy == 0) {
                continue;
            }

            ivec2 samplePixel = pixel + ivec2(ox, oy);
            vec4 sampleColor = texelFetch(RawMaskSampler, samplePixel, 0);
            if (sampleColor.a > 0.0) {
                edgeAlpha = max(edgeAlpha, sampleColor.a);
            }
        }
    }
    return edgeAlpha > 0.0;
#endif

#ifdef JFA_EDGE
    ivec2 size = textureSize(JfaFieldSampler, 0);
    vec4 seedInfo = texelFetch(JfaFieldSampler, pixel, 0);
    if (seedInfo.w <= 0.0 || seedInfo.a <= 0.0) {
        return false;
    }

    vec2 pixelCenter = vec2(pixel) + vec2(0.5);
    vec2 seedPx = seedInfo.xy * vec2(size);
    float distPx = length(seedPx - pixelCenter);
    if (distPx > float(OUTLINE_RADIUS)) {
        return false;
    }

    return true;
#endif

    return false;
}

bool eo_depth_visible(ivec2 pixel, inout float alphaOut) {
    float bestDepth = 1.0;

    for (int oy = -OUTLINE_RADIUS; oy <= OUTLINE_RADIUS; oy++) {
        for (int ox = -OUTLINE_RADIUS; ox <= OUTLINE_RADIUS; ox++) {
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
            alphaOut = max(alphaOut, sampleColor.a);
        }
    }

    if (alphaOut <= 0.0) {
        return false;
    }

    float sceneDepth = texelFetch(SceneDepthSampler, pixel, 0).r;
    return bestDepth <= sceneDepth + DEPTH_EPSILON;
}

void main() {
    ivec2 pixel = ivec2(gl_FragCoord.xy);

    vec3 edgeColor;
    float edgeAlpha;
    if (!eo_compute_edge(pixel, edgeColor, edgeAlpha)) {
        discard;
    }

    if (!eo_depth_visible(pixel, edgeAlpha)) {
        discard;
    }

    float colorMix = clamp(OUTLINE_COLOR_MIX, 0.0, 1.0);
    vec3 customColor = vec3(OUTLINE_COLOR_R, OUTLINE_COLOR_G, OUTLINE_COLOR_B);
    vec3 finalColor = mix(edgeColor, customColor, colorMix);

    float finalAlpha = clamp(max(edgeAlpha, MIN_OUTPUT_ALPHA), 0.0, 1.0);

    fragColor = vec4(finalColor, finalAlpha);
}

