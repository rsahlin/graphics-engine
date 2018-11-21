#version 300 es
 /**
 */

precision highp float;

//Put array declaration after name for GLSL compatibility
uniform mat4 uModelMatrix[3];

in vec3 aVertex; 
in vec4 aColor;
in vec4 aRect;

out vec4 vColor;
out vec4 vRect;

/**
 * Used for objects that uses only position, plus color, for instance lines, points or geometry shader objects
 */
void main() {
    gl_Position = (vec4(aVertex, 1.0) * uModelMatrix[0] * uModelMatrix[1]) * uModelMatrix[2];
    vColor = aColor;
    vRect = aRect;
}
