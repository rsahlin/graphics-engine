package com.graphicsengine.spritemesh;

import com.nucleus.geometry.Mesh;
import com.nucleus.geometry.VertexBuffer;

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
    /**
     * Number of float data per vertex
     */
    final static int ATTRIBUTES_PER_VERTEX = 14;

    public UVSpriteProgram() {
        super();
        vertexShaderName = VERTEX_SHADER_NAME;
        fragmentShaderName = FRAGMENT_SHADER_NAME;
        // attributesPerVertex = ATTRIBUTES_PER_VERTEX;
        components = VertexBuffer.XYZ_COMPONENTS;
    }

    @Override
    public void setupUniforms(Mesh mesh) {
        createUniformStorage(mesh, shaderVariables);
        setScreenSize(mesh.getUniforms(), VARIABLES.uScreenSize);
    }

}
