package com.graphicsengine.spritemesh;

import java.nio.FloatBuffer;

import com.nucleus.BackendException;
import com.nucleus.opengl.GLES20Wrapper;
import com.nucleus.opengl.shader.QuadExpanderShader;
import com.nucleus.opengl.shader.GLShaderProgram;
import com.nucleus.opengl.shader.ShaderSource;
import com.nucleus.opengl.shader.ShaderVariable;
import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.renderer.NucleusRenderer.Renderers;
import com.nucleus.renderer.Pass;
import com.nucleus.texturing.TextureType;
import com.nucleus.texturing.TiledTexture2D;

/**
 * This class defines the mappings for the tile sprite vertex and fragment shaders.
 * This program has support for rotated sprites in Z axis, the sprite position and frame index can be set for each
 * sprite.
 * It is used by the {@link SpriteMesh}
 * 
 * @author Richard Sahlin
 *
 */
public class TiledSpriteProgram extends GLShaderProgram {

    public static final String COMMON_VERTEX_SHADER = "common";

    protected TiledTexture2D texture;

    /**
     * This uses gles 20 - deprecated in favour of geometry shader
     */
    protected static final String CATEGORY = "tiledsprite20";

    protected QuadExpanderShader expanderShader;

    /**
     * Constructor for TiledSpriteProgram
     * 
     * @param texture
     * @param shading
     */
    TiledSpriteProgram(TiledTexture2D texture, GLShaderProgram.Shading shading) {
        super(new SharedfragmentCategorizer(null, shading, CATEGORY), GLShaderProgram.ProgramType.VERTEX_FRAGMENT);
        if (texture == null && shading == GLShaderProgram.Shading.textured) {
            throw new IllegalArgumentException("Texture may not be null for shading: " + shading);
        }
        this.texture = texture;
        setIndexer(new TiledSpriteIndexer());
    }

    /**
     * Internal constructor to be used by subclass - DO NOT USE to create instance of TiledSpriteProgram
     * 
     * @param pass
     * @param shading
     * @param category
     */
    TiledSpriteProgram(Pass pass, GLShaderProgram.Shading shading, String category) {
        super(new SharedfragmentCategorizer(pass, shading, category), GLShaderProgram.ProgramType.VERTEX_FRAGMENT);
        setIndexer(new TiledSpriteIndexer());
    }

    @Override
    protected String[] getCommonShaderName(ShaderType type) {
        switch (type) {
            case VERTEX:
                return new String[] { PROGRAM_DIRECTORY + COMMON_VERTEX_SHADER };
            default:
                return null;
        }
    }

    @Override
    public void createProgram(NucleusRenderer renderer) throws BackendException {
        if (GLES20Wrapper.getInfo().getRenderVersion().major >= 3
                && GLES20Wrapper.getInfo().getRenderVersion().minor >= 1) {
            // expanderShader = (QuadExpanderShader) AssetManager.getInstance().getProgram(gles,
            // new QuadExpanderShader());
        }
        super.createProgram(renderer);

    }

    @Override
    protected String getSourceNameVersion(Renderers version, int type) {
        if (version.major >= 3) {
            return ShaderSource.V300;
        }
        return super.getSourceNameVersion(version, type);
    }

    @Override
    public void updateUniformData(FloatBuffer destinationUniform) {
        setScreenSize(destinationUniform, getUniformByName("uScreenSize"));
        setTextureUniforms(destinationUniform, texture);
    }

    /**
     * Sets the data related to texture uniforms in the uniform float storage
     * 
     * @param uniforms
     * @param texture
     */
    protected void setTextureUniforms(FloatBuffer uniforms, TiledTexture2D texture) {
        if (texture != null && texture.getTextureType() == TextureType.TiledTexture2D) {
            // TODO - where should the uniform name be defined?
            ShaderVariable texUniform = getUniformByName("uTextureData");
            // If null it could be because loaded program does not match with texture usage
            if (texUniform != null) {
                setTextureUniforms(texture, uniforms, texUniform);
            } else {
                if (function.getShading() == null || function.getShading() == GLShaderProgram.Shading.flat) {
                    throw new IllegalArgumentException(
                            "Texture type " + texture.getTextureType() + ", does not match shading " + getShading()
                                    + " for program:\n" + toString());
                }
            }
        }
    }

    /**
     * 
     * Returns the expander shader to be used with this program.
     * 
     * @return
     */
    public QuadExpanderShader getExpanderShader() {
        return expanderShader;
    }

    @Override
    public void initUniformData(FloatBuffer destinationUniforms) {
    }

}
