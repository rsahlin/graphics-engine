#version 300 es
 /**
 * Vertex shader for shadow pass of untextured tiled sprite renderer
 * @author Richard Sahlin
 */
precision highp float;

//Put array declaration after name for GLSL compatibility
uniform mat4 uModelMatrix[4];

in vec3 aVertex; //vertex position
in vec3 aTranslate; //sprite x, sprite y, sprite z
in vec3 aRotate;
in vec3 aScale;
in vec4 aColor;

out vec4 vLightPos;
out vec4 color;

mat4 calculateTransformMatrix(vec3 rotate, vec3 scale, vec3 translate);

void main() {
    vec4 pos =  vec4(aVertex.xyz, 1) * (calculateTransformMatrix(aRotate, aScale, aTranslate) * uModelMatrix[0] * uModelMatrix[1]);
    gl_Position = pos * uModelMatrix[2];
    vLightPos = pos * uModelMatrix[3];
    color = aColor;
}
