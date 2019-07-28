package com.graphicsengine.map;

import com.nucleus.geometry.AttributeUpdater.BufferIndex;
import com.nucleus.opengl.shader.GLShaderProgram;
import com.nucleus.opengl.shader.NamedVariableIndexer;
import com.nucleus.shader.GenericShaderProgram;
import com.nucleus.shader.ShaderVariable.VariableType;
import com.nucleus.texturing.TiledTexture2D;

/**
 * This class defines the mappings for the charset vertex and fragment shaders.
 * 
 * @author Richard Sahlin
 *
 */
public class PlayfieldProgram extends GenericShaderProgram {

    /**
     * Layout for the data needed by the playfield program
     * 
     *
     */
    public static class PlayfieldIndexer extends NamedVariableIndexer {

        protected final static Property[] PROPERTY = new Property[] { Property.VERTEX, Property.UV,
                Property.TRANSLATE, Property.EMISSIVE, Property.FRAME };
        protected final static int[] OFFSETS = new int[] { 0, 4, 0, 3, 7 };
        protected final static VariableType[] TYPES = new VariableType[] { VariableType.ATTRIBUTE,
                VariableType.ATTRIBUTE, VariableType.ATTRIBUTE, VariableType.ATTRIBUTE, VariableType.ATTRIBUTE };
        protected final static BufferIndex[] BUFFERINDEXES = new BufferIndex[] { BufferIndex.ATTRIBUTES_STATIC,
                BufferIndex.ATTRIBUTES_STATIC, BufferIndex.ATTRIBUTES, BufferIndex.ATTRIBUTES, BufferIndex.ATTRIBUTES };
        protected final static int[] SIZEPERVERTEX = new int[] { 8, 6 };

        private PlayfieldIndexer() {
            super();
            createArrays(PROPERTY, OFFSETS, TYPES, SIZEPERVERTEX, BUFFERINDEXES);
        }

    }

    public static final String CATEGORY = "charmap";

    protected TiledTexture2D texture;

    PlayfieldProgram(TiledTexture2D texture) {
        super(null, Shading.textured, CATEGORY, GLShaderProgram.ProgramType.VERTEX_FRAGMENT);
        setIndexer(new PlayfieldIndexer());
    }

    @Override
    public void updateUniformData() {
        setScreenSize(uniforms, getUniformByName("uScreenSize"));
        setTextureUniforms(texture, uniforms,
                getUniformByName("uTextureData"));
        // setEmissive(uniforms, getUniformByName("uAmbientLight"), globalLight.getAmbient());
    }

    @Override
    public void initUniformData() {
    }

}
