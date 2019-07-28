package com.graphicsengine.spritemesh;

import java.nio.FloatBuffer;

import com.nucleus.geometry.AttributeUpdater.BufferIndex;
import com.nucleus.opengl.shader.GLShaderProgram;
import com.nucleus.opengl.shader.NamedShaderVariable;
import com.nucleus.opengl.shader.NamedVariableIndexer;
import com.nucleus.renderer.NucleusRenderer.Renderers;
import com.nucleus.renderer.Pass;
import com.nucleus.shader.GenericShaderProgram;
import com.nucleus.shader.ShaderVariable.VariableType;
import com.nucleus.texturing.TextureType;
import com.nucleus.texturing.TiledTexture2D;

/**
 * This class defines the mappings for the tile sprite vertex and fragment shaders.
 * This program has support for rotated sprites in Z axis, the sprite position and frame index can be set for each
 * sprite.
 * It is used by the {@link SpriteMesh}
 * 
 */
public class TiledSpriteProgram extends GenericShaderProgram {

    /**
     * Layout for the data needed by the tiled sprite program
     * 
     *
     */
    public static class TiledSpriteIndexer extends NamedVariableIndexer {

        protected final static Property[] PROPERTY = new Property[] { Property.VERTEX, Property.UV,
                Property.TRANSLATE, Property.ROTATE, Property.SCALE, Property.ALBEDO, Property.FRAME };
        protected final static int[] OFFSETS = new int[] { 0, 4, 0, 3, 6, 9, 12 };
        protected final static VariableType[] TYPES = new VariableType[] { VariableType.ATTRIBUTE,
                VariableType.ATTRIBUTE,
                VariableType.ATTRIBUTE, VariableType.ATTRIBUTE, VariableType.ATTRIBUTE, VariableType.ATTRIBUTE,
                VariableType.ATTRIBUTE };
        protected final static BufferIndex[] BUFFERINDEXES = new BufferIndex[] { BufferIndex.ATTRIBUTES_STATIC,
                BufferIndex.ATTRIBUTES_STATIC, BufferIndex.ATTRIBUTES, BufferIndex.ATTRIBUTES, BufferIndex.ATTRIBUTES,
                BufferIndex.ATTRIBUTES, BufferIndex.ATTRIBUTES };
        protected final static int[] SIZEPERVERTEX = new int[] { 13, 6 };

        private TiledSpriteIndexer() {
            super();
            createArrays(PROPERTY, OFFSETS, TYPES, SIZEPERVERTEX, BUFFERINDEXES);
        }

    }

    public static final String COMMON_VERTEX_SHADER = "common";

    protected TiledTexture2D texture;

    /**
     * This uses gles 20 - deprecated in favour of geometry shader
     */
    protected static final String CATEGORY = "tiledsprite20";

    /**
     * Constructor for TiledSpriteProgram
     * 
     * @param texture
     * @param shading
     */
    TiledSpriteProgram(TiledTexture2D texture, Shading shading) {
        super(new SharedfragmentCategorizer(null, shading, CATEGORY), GLShaderProgram.ProgramType.VERTEX_FRAGMENT);
        if (texture == null && shading == Shading.textured) {
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
    TiledSpriteProgram(Pass pass, Shading shading, String category) {
        super(new SharedfragmentCategorizer(pass, shading, category), GLShaderProgram.ProgramType.VERTEX_FRAGMENT);
        setIndexer(new TiledSpriteIndexer());
    }

    @Override
    protected String[] getLibName(Renderers version, ShaderType type) {
        switch (type) {
            case VERTEX:
                return new String[] { COMMON_VERTEX_SHADER };
            default:
                return null;
        }
    }

    @Override
    public void updateUniformData() {
        setScreenSize(uniforms, getUniformByName("uScreenSize"));
        setTextureUniforms(uniforms, texture);
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
            NamedShaderVariable texUniform = getUniformByName("uTextureData");
            // If null it could be because loaded program does not match with texture usage
            if (texUniform != null) {
                setTextureUniforms(texture, uniforms, texUniform);
            } else {
                if (function.getShading() == null || function.getShading() == Shading.flat) {
                    throw new IllegalArgumentException(
                            "Texture type " + texture.getTextureType() + ", does not match shading "
                                    + getFunction().getShading()
                                    + " for program:\n" + toString());
                }
            }
        }
    }

    @Override
    public void initUniformData() {
    }

}
