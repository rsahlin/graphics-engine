package com.graphicsengine.spritemesh;

import com.nucleus.geometry.Mesh;

/**
 * This class defines the mapping for the UV sprite vertex and fragment shaders.
 * This program has support for a number of sprites with frames defined by UV coordinates for each sprite corner,
 * this means that the sprites can have different sizes.
 * 
 * @author Richard Sahlin
 *
 */
public class UVSpriteProgram extends TiledSpriteProgram {

    protected final static String VERTEX_SHADER_NAME = "assets/uvspritevertex.essl";

    public UVSpriteProgram() {
        super();
        vertexShaderName = VERTEX_SHADER_NAME;
        fragmentShaderName = FRAGMENT_SHADER_NAME;
        attributesPerVertex = ATTRIBUTES_PER_VERTEX;
    }

    @Override
    public void setupUniforms(Mesh mesh) {
        createUniformStorage(mesh, shaderVariables);
    }

}
