#version 100
 /**
 * Vertex shader for charmap renderr
 * @author Richard Sahlin
 */

precision highp float;

//Put array declaration after name for GLSL compatibility
uniform mat4 uModelMatrix[3];
uniform vec3 uTextureData[1]; //tex width, tex height, frames per line
uniform vec2 uScreenSize[1]; //Width and  height of screen
uniform vec4 uAmbientLight[1];

attribute vec4 aVertex; //vertex position + index
attribute vec2 aTexCoord;
attribute vec3 aTranslate; //char x, char y, z (not used)
attribute vec2 aFrameData;//frame, flags
attribute vec4 aColor;

varying vec2 vTexCoord;
varying vec4 vAmbientLight;


void main() {
    int flags = int(aFrameData.y);
    vec4 pos =  vec4(aVertex.xyz + aTranslate.xyz, 1.0) * uModelMatrix[0] * uModelMatrix[1];
    gl_Position = vec4(floor((uScreenSize[0] * vec2(pos)) + 0.5) / uScreenSize[0], pos.z, 1.0) * uModelMatrix[2];
    vec2 flip = aTexCoord;
    if ((flags - 4) >= 0) {
        //FlipX
        flip.x = uTextureData[0].x - aTexCoord.x;
        flags -= 4;
    }
    if ((flags - 2) >= 0) {
        //FlipY
        flip.y = uTextureData[0].y - aTexCoord.y;
        flags -= 2;
    }
    vTexCoord = vec2(flip.x + 
                     mod(aFrameData.x, uTextureData[0].z) * uTextureData[0].x,
                     flip.y + floor(aFrameData.x / uTextureData[0].z) * uTextureData[0].y);
    vAmbientLight = uAmbientLight[0] * aColor;
}
