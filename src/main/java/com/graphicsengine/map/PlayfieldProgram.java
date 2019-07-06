package com.graphicsengine.map;

import java.nio.FloatBuffer;

import com.graphicsengine.spritemesh.TiledSpriteIndexer;
import com.nucleus.opengl.shader.GLShaderProgram;
import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.renderer.Pass;
import com.nucleus.shader.Shader.Shading;
import com.nucleus.texturing.TiledTexture2D;

/**
 * This class defines the mappings for the charset vertex and fragment shaders.
 * 
 * @author Richard Sahlin
 *
 */
public class PlayfieldProgram extends GLShaderProgram {

    public static final String CATEGORY = "charmap";

    protected TiledTexture2D texture;

    PlayfieldProgram(TiledTexture2D texture) {
        // super(null, null, CATEGORY, CommonShaderVariables.values(), ProgramType.VERTEX_FRAGMENT);
        super(null, null, CATEGORY, GLShaderProgram.ProgramType.VERTEX_FRAGMENT);
        setIndexer(new TiledSpriteIndexer());
    }

    @Override
    public void updateUniformData(FloatBuffer destinationUniform) {
        setScreenSize(uniforms, getUniformByName("uScreenSize"));
        setTextureUniforms(texture, uniforms,
                getUniformByName("uTextureData"));
        // setEmissive(uniforms, getUniformByName("uAmbientLight"), globalLight.getAmbient());
    }

    @Override
    public GLShaderProgram getProgram(NucleusRenderer renderer, Pass pass, Shading shading) {
        switch (pass) {
            case UNDEFINED:
            case ALL:
            case MAIN:
                return this;
            default:
                throw new IllegalArgumentException("Invalid pass " + pass);
        }
    }

    @Override
    public void initUniformData(FloatBuffer destinationUniforms) {
    }

}
