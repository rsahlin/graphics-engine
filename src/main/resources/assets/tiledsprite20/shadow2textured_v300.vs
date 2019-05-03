#version 300 es
 /**
 * Vertex shader for shadow pass of tiled sprite renderer
 * @author Richard Sahlin
 */
precision highp float;

//Put array declaration after name for GLSL compatibility
uniform mat4 uModelMatrix[4];
uniform vec3 uTextureData[1]; //tex width, tex height, frames per line
uniform vec2 uScreenSize[1]; //Width and height of screen

in vec4 aVertex; //vertex position
in vec2 aTexCoord;
in vec3 aTranslate; //sprite x, sprite y, sprite z
in vec3 aRotate;
in vec3 aScale;
in vec2 aFrameData;//frame

out vec2 vTexCoord;
out vec4 vLightPos;

mat4 calculateTransformMatrix(vec3 rotate, vec3 scale, vec3 translate);

void main() {
    vec4 pos =  vec4(aVertex.xyz, 1) * (calculateTransformMatrix(aRotate, aScale, aTranslate) * uModelMatrix[0] * uModelMatrix[1]);
    gl_Position = vec4(floor((uScreenSize[0] * vec2(pos) + 0.5)) / uScreenSize, pos.z, pos.w) * uModelMatrix[2];
    vLightPos = pos * uModelMatrix[3];
    float y = floor(aFrameData.x / uTextureData[0].z);
    vTexCoord = vec2(aTexCoord.x + 
                     mod(aFrameData.x, uTextureData[0].z) * uTextureData[0].x,
                     aTexCoord.y + y * uTextureData[0].y);
}
