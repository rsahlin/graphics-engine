package com.graphicsengine.spritemesh;

import com.nucleus.assets.AssetManager;
import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.renderer.Pass;
import com.nucleus.shader.ShaderProgram;
import com.nucleus.shader.ShadowPass1Program;
import com.nucleus.texturing.Texture2D;
import com.nucleus.texturing.Texture2D.Shading;

/**
 * This class defines the mapping for the UV sprite vertex and fragment shaders.
 * This program has support for a number of sprites with frames defined by UV coordinates for each sprite corner,
 * this means that the sprites can have different sizes.
 * 
 * @author Richard Sahlin
 *
 */
public class UVSpriteProgram extends TiledSpriteProgram {

    private static final String CATEGORY = "uvsprite";
    protected static final String VERTEX_SHADER_NAME = "assets/uvspritevertex.essl";

    public UVSpriteProgram() {
        super(Texture2D.Shading.textured);
    }
    
    @Override
    protected void setShaderSource() {
        super.setShaderSource();
        // Overwrite the vertex shader
        vertexShaderName = PROGRAM_DIRECTORY + CATEGORY + VERTEX_TYPE + SHADER_SOURCE_SUFFIX;
    }

    @Override
    public ShaderProgram getProgram(NucleusRenderer renderer, Pass pass, Shading shading) {
        switch (pass) {
            case UNDEFINED:
            case ALL:
            case MAIN:
                return this;
            case SHADOW1:
                return AssetManager.getInstance().getProgram(renderer, new ShadowPass1Program(this, shading, CATEGORY));
            case SHADOW2:
                return this;
                default:
            throw new IllegalArgumentException("Invalid pass " + pass);
        }
    }
    

}
