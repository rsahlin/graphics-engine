#version 100
 /**
 * Vertex shader for tiled sprite renderer
 * @author Richard Sahlin
 */

precision highp float;

//Put array declaration after name for GLSL compatibility
uniform mat4 uModelMatrix[3];

attribute vec4 aVertex; //vertex position
attribute vec3 aTranslate; //sprite x, sprite y, sprite z 
attribute vec3 aRotate;  
attribute vec3 aScale;
attribute vec4 aColor;

varying vec4 color;

/**
 * From commonvertex.essl
 */
mat4 calculateTransformMatrix(vec3 rotate, vec3 scale, vec3 translate);

/**
 * Used for objects that are transformed, scaled and rotated - with the translate, rotation and scale being defined from attribute data
 */
void main() {
    vec4 pos =  vec4(aVertex.xyz, 1) * (calculateTransformMatrix(aRotate, aScale, aTranslate) * uModelMatrix[0] * uModelMatrix[1]);
    gl_Position = pos * uModelMatrix[2];
    color = aColor;
}
