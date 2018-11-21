 /**
 * Fragment shader for tiled sprite renderer
 * @author Richard Sahlin
 */
precision mediump float;

varying vec2 xy;
varying vec4 color;
varying vec4 data;

void main()
{
    float point = xy.x * xy.x + xy.y * xy.y;
	gl_FragColor = color * floor(min(data.x,point) / max(data.x,point) + data.y);
}
