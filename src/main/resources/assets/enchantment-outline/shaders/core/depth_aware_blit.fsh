#version 330

uniform sampler2D InSampler;
uniform sampler2D MaskDepthSampler;
uniform sampler2D SceneDepthSampler;

out vec4 fragColor;

void main() {
    ivec2 pixel = ivec2(gl_FragCoord.xy);

    vec4 color = texelFetch(InSampler, pixel, 0);
    if (color.a <= 0.0) {
        discard;
    }

    float maskDepth = texelFetch(MaskDepthSampler, pixel, 0).r;
    float mainDepth = texelFetch(SceneDepthSampler, pixel, 0).r;

    if (maskDepth > mainDepth + 0.00001) {
        discard;
    }

    fragColor = color;
}

