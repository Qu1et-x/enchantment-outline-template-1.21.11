#version 330

uniform sampler2D InSampler;
uniform sampler2D MaskDepthSampler;
uniform sampler2D SceneDepthSampler;

out vec4 fragColor;

void main() {
    ivec2 pixel = ivec2(gl_FragCoord.xy);

    // Draw outline only outside the original mask interior.
    vec4 center = texelFetch(InSampler, pixel, 0);
    if (center.a > 0.0) {
        discard;
    }

    vec4 bestColor = vec4(0.0);
    float bestAlpha = 0.0;
    float bestDepth = 1.0;

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

            if (sampleColor.a > 0.0) {
                float sampleDepth = texelFetch(MaskDepthSampler, samplePixel, 0).r;
                bestDepth = min(bestDepth, sampleDepth);
            }
        }
    }

    if (bestAlpha <= 0.0) {
        discard;
    }

    float mainDepth = texelFetch(SceneDepthSampler, pixel, 0).r;

    if (bestDepth > mainDepth + 0.00001) {
        discard;
    }

    // Slightly boost alpha so thin outlines stay visible.
    fragColor = vec4(bestColor.rgb, max(bestColor.a, 0.6));
}

