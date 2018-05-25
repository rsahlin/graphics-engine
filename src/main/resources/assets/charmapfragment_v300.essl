#version 300 es
 /**
 * Fragment shader for tiled sprite renderer
 */
precision mediump float;

uniform sampler2D uTexture;      //The texture sampler
in vec2 vTexCoord;
in vec4 vAmbientLight;

out vec4 fragColor;

void main()
{
    fragColor = texture2D(uTexture, vTexCoord) * vAmbientLight;
    
}
