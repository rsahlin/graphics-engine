#version 100
 /**
 * Vertex shader for tiled sprite renderer
 * @author Richard Sahlin
 */
 
 precision highp float;

//Put array declaration after name for GLSL compatibility
uniform mat4 uModelMatrix;
uniform mat4 uViewMatrix;
uniform mat4 uProjectionMatrix;

attribute vec4 aVertex; //vertex position
attribute vec2 aTexCoord;
attribute vec3 aTranslate; //sprite x, sprite y, sprite z
attribute vec3 aRotate;
attribute vec3 aScale;
attribute vec4 aColor;
attribute vec4 aFrameData;

varying vec4 color;
varying vec2 xy;
varying vec4 data;

mat4 calculateTransformMatrix(vec3 rotate, vec3 scale, vec3 translate);

void main() {
    vec4 pos =  vec4(aVertex.xyz, 1) * (calculateTransformMatrix(aRotate, aScale, aTranslate) * uModelMatrix * uViewMatrix);
    gl_Position = pos * uProjectionMatrix;
    color = aColor;
    xy = aTexCoord;
    data = aFrameData;
}
