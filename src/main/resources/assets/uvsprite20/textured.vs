#version 100
 /**
 * Vertex shader for tiled sprite renderer
 * @author Richard Sahlin
 */
precision highp float;

//Put array declaration after name for GLSL compatibility
uniform mat4 uModelMatrix[3];
uniform vec2 uScreenSize[1]; //Screen width and height

attribute vec4 aVertex; //vertex position and quad index (0-3)
attribute vec3 aTranslate; //sprite x, sprite y, sprite z 
attribute vec3 aRotate;  
attribute vec3 aScale;
attribute vec2 aFrameData;//texture u, texture v

varying vec2 vTexCoord;
mat4 calculateTransformMatrix(vec3 rotate, vec3 scale, vec3 translate);

void main() {
    vec4 pos =  vec4(aVertex.xyz, 1) * (calculateTransformMatrix(aRotate, aScale, aTranslate) * uModelMatrix[0] * uModelMatrix[1]);
    gl_Position = vec4(floor((uScreenSize[0] * vec2(pos)) + 0.5) / uScreenSize[0], pos.z, pos.w) * uModelMatrix[2];
    vTexCoord = vec2(aFrameData.x, aFrameData.y);
}
