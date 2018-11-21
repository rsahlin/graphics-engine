#version 300 es
 /**
 * Vertex shader for shadow pass of untextured tiled sprite renderer
 * @author Richard Sahlin
 */
precision highp float;

//Put array declaration after name for GLSL compatibility
uniform mat4 uModelMatrix[4];

in vec3 aVertex; 
in vec4 aColor;
in vec4 aRect;

out vec4 vColor;
out vec4 vRect;
out vec4 vLightPos;

/**
 * Used for objects that uses only position, plus color, for instance lines, points or geometry shader objects
 */
void main() {
    vec4 pos =  vec4(aVertex.xyz, 1) * uModelMatrix[0] * uModelMatrix[1];
    gl_Position = pos * uModelMatrix[2];
    vColor = aColor;
    vRect = aRect;
    vLightPos = pos * uModelMatrix[3];
}
