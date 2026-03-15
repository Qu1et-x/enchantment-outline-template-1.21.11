#version 330

uniform sampler2D InSampler;
uniform sampler2D MaskDepthSampler;
uniform sampler2D MainDepthSampler;

out vec4 fragColor;

void main() {
    ivec2 pixel = ivec2(gl_FragCoord.xy);

    vec4 color = texelFetch(InSampler, pixel, 0);
    if (color.a <= 0.0) {
        discard;
    }

    float maskDepth = texelFetch(MaskDepthSampler, pixel, 0).r;
    float mainDepth = texelFetch(MainDepthSampler, pixel, 0).r;

    // Keep only fragments that are not hidden behind final scene depth.
    if (maskDepth > mainDepth + 0.00001) {
        discard;
    }

    fragColor = color;
}

