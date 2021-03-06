#version 300 es
 /**
 * Vertex shader for tiled sprite renderer
 * @author Richard Sahlin
 */
precision highp float;

//Put array declaration after name for GLSL compatibility
uniform mat4 uModelMatrix[3];
uniform vec2 uScreenSize[1]; //Screen width and height

in vec4 aVertex; //vertex position and quad index (0-3)
in vec3 aTranslate; //sprite x, sprite y, sprite z 
in vec3 aRotate; 
in vec3 aScale; 
in vec2 aFrameData;//texture u, texture v

out vec2 vTexCoord;
mat4 calculateTransformMatrix(vec3 rotate, vec3 scale, vec3 translate);

layout(packed) uniform UVData {
    vec4 uvData[1024];
};

void main() {
    vec4 pos =  vec4(aVertex.xyz, 1) * (calculateTransformMatrix(aRotate, aScale, aTranslate) * uModelMatrix[0] * uModelMatrix[1]);
    gl_Position = vec4(floor((uScreenSize[0] * vec2(pos)) + 0.5) / uScreenSize[0], pos.z, pos.w) * uModelMatrix[2];
    int frame = int(aFrameData.x);
    int index = int(aVertex.w);
    vec4 uv = uvData[frame];
    if (index == 0) {
        vTexCoord = vec2(uv.x, uv.y);
    } else if (index == 1) {
        vTexCoord = vec2(uv.x + uv.z, uv.y);
    } else if (index == 2) {
        vTexCoord = vec2(uv.x + uv.z, uv.y + uv.w);
    } else {
        vTexCoord = vec2(uv.x, uv.y + uv.w);
    }
}
