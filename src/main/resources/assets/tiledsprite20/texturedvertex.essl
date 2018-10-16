#version 100
 /**
 * Vertex shader for tiled sprite renderer
 * @author Richard Sahlin
 */

precision highp float;

//Put array declaration after name for GLSL compatibility
uniform mat4 uModelMatrix[3];
uniform vec3 uTextureData[1]; //tex width, tex height, frames per line
uniform vec2 uScreenSize[1]; //Width and height of screen

attribute vec4 aVertex; //vertex position
attribute vec2 aTexCoord;
attribute vec3 aTranslate; //sprite x, sprite y, sprite z
attribute vec3 aRotate;
attribute vec3 aScale;
attribute vec2 aFrameData;//frame

varying vec2 vTexCoord;

mat4 calculateTransformMatrix(vec3 rotate, vec3 scale, vec3 translate);

void main() {
    vec4 pos =  vec4(aVertex.xyz, 1) * (calculateTransformMatrix(aRotate, aScale, aTranslate) * uModelMatrix[0] * uModelMatrix[1]);
    gl_Position = vec4(floor((uScreenSize[0] * vec2(pos) + 0.5)) / uScreenSize[0], pos.z, 1) * uModelMatrix[2];
    float y = floor(aFrameData.x / uTextureData[0].z);
    vTexCoord = vec2(aTexCoord.x + 
                     mod(aFrameData.x, uTextureData[0].z) * uTextureData[0].x,
                     aTexCoord.y + y * uTextureData[0].y);
                     
}
