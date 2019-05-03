#version 100
 /**
 * Fragment shader for tiled sprite renderer
 * @author Richard Sahlin
 */
precision mediump float;

uniform sampler2D uTexture;      //The texture sampler
varying vec2 vTexCoord;
varying vec4 vAmbientLight;

void main()
{
	gl_FragColor = texture2D(uTexture, vTexCoord) * vAmbientLight;
	
}
