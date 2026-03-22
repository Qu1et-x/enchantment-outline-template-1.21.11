#version 330

uniform sampler2D RawMaskSampler;

out vec4 fragColor;

void main() {
    ivec2 pixel = ivec2(gl_FragCoord.xy);
    ivec2 size = textureSize(RawMaskSampler, 0);
    vec4 mask = texelFetch(RawMaskSampler, pixel, 0);

    if (mask.a <= 0.0) {
        fragColor = vec4(0.0);
        return;
    }

    vec2 uv = (vec2(pixel) + vec2(0.5)) / vec2(size);
    fragColor = vec4(uv, mask.a, 1.0);
}

