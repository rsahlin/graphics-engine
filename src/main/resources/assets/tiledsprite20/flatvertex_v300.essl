#version 300 es
 /**
 * Vertex shader for tiled sprite renderer
 * @author Richard Sahlin
 */

precision highp float;

//Put array declaration after name for GLSL compatibility
uniform mat4 uModelMatrix[3];

in vec4 aVertex; //vertex position
in vec3 aTranslate; //sprite x, sprite y, sprite z 
in vec3 aRotate;  
in vec3 aScale;
in vec4 aColor;

out vec4 color;

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
