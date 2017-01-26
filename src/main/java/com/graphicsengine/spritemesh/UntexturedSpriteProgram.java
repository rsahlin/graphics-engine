package com.graphicsengine.spritemesh;

import com.nucleus.geometry.Mesh;
import com.nucleus.geometry.VertexBuffer;

/**
 * This class defines the mapping for the untextured sprite vertex and fragment shaders.
 * It shares most of the functionallity with {@linkplain TiledSpriteProgram}
 * Uses the same mapping as the TiledSpriteProgram but frame and UV are not used.
 * 
 * @author Richard Sahlin
 *
 */
public class UntexturedSpriteProgram extends TiledSpriteProgram {

    protected final static String VERTEX_SHADER_NAME = "assets/untexturedspritevertex.essl";
    protected final static String FRAGMENT_SHADER_NAME = "assets/untexturedspritefragment.essl";

    public UntexturedSpriteProgram() {
        super();
        vertexShaderName = VERTEX_SHADER_NAME;
        fragmentShaderName = FRAGMENT_SHADER_NAME;
        attributesPerVertex = ATTRIBUTES_PER_VERTEX;
        components = VertexBuffer.XYZ_COMPONENTS;
    }

    @Override
    public void setupUniforms(Mesh mesh) {
        createUniformStorage(mesh, shaderVariables);
    }
}
