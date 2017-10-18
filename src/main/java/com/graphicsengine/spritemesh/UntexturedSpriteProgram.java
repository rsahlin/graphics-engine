package com.graphicsengine.spritemesh;

import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.renderer.Pass;
import com.nucleus.shader.ShaderProgram;
import com.nucleus.texturing.Texture2D.Shading;
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
    public ShaderProgram getProgram(NucleusRenderer renderer, Pass pass, Shading shading) {
        switch (pass) {
            case SHADOW2:
                // return AssetManager.getInstance().getProgram(renderer, new ShadowPass2Program(shading));
                default:
                return super.getProgram(renderer, pass, shading);
        }
    }
    
    
    
}
