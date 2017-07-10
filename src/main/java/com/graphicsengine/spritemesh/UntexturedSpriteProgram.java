package com.graphicsengine.spritemesh;

import com.nucleus.geometry.Mesh;
import com.nucleus.texturing.Untextured;

/**
 * This class defines the mapping for the untextured sprite vertex and fragment shaders.
 * It shares most of the functionality with {@linkplain TiledSpriteProgram}
 * Uses the same mapping as the TiledSpriteProgram but frame and UV are not used.
 * 
 * @author Richard Sahlin
 *
 */
public class UntexturedSpriteProgram extends TiledSpriteProgram {
    /**
     * Creates a new untextured sprite program for the specified untextured shading.
     * This is used for parametric untextured sprites.
     * 
     * @param shading
     */
    public UntexturedSpriteProgram(Untextured.Shading shading) {
        super();
        vertexShaderName = PROGRAM_DIRECTORY + shading.name() + SPRITE + VERTEX + SHADER_SOURCE_SUFFIX;
        fragmentShaderName = PROGRAM_DIRECTORY + shading.name() + FRAGMENT + SHADER_SOURCE_SUFFIX;
    }

    @Override
    public void setupUniforms(Mesh mesh) {
        // Must override since we shall not store screensize in uniform
        createUniformStorage(mesh, shaderVariables);
    }
}
