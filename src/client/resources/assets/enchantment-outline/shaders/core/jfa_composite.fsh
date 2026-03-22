#version 330

uniform sampler2D HollowSampler;
uniform sampler2D JfaFieldSampler;
uniform sampler2D MaskDepthSampler;
uniform sampler2D SceneDepthSampler;

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

void main() {
    ivec2 pixel = ivec2(gl_FragCoord.xy);
    ivec2 size = textureSize(JfaFieldSampler, 0);

    vec4 center = texelFetch(HollowSampler, pixel, 0);
    if (center.a <= ALPHA_THRESHOLD) {
        discard;
    }

    vec4 seedInfo = texelFetch(JfaFieldSampler, pixel, 0);
    if (seedInfo.w <= 0.0 || seedInfo.a <= 0.0) {
        discard;
    }

    vec2 pixelCenter = vec2(pixel) + vec2(0.5);
    vec2 seedPx = seedInfo.xy * vec2(size);
    float distPx = length(seedPx - pixelCenter);
    if (distPx > float(OUTLINE_RADIUS)) {
        discard;
    }

    float maskDepth = texture(MaskDepthSampler, seedInfo.xy).r;
    float sceneDepth = texelFetch(SceneDepthSampler, pixel, 0).r;
    if (maskDepth > sceneDepth + DEPTH_EPSILON) {
        discard;
    }

    fragColor = vec4(center.rgb, max(center.a, 0.6));
}



