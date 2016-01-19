package com.graphicsengine.map;

import com.nucleus.geometry.Mesh;
import com.nucleus.opengl.GLES20Wrapper;
import com.nucleus.opengl.GLException;
import com.nucleus.shader.VariableMapping;
import com.nucleus.shader.ShaderProgram;
import com.nucleus.shader.ShaderVariable;
import com.nucleus.shader.ShaderVariable.VariableType;
import com.nucleus.texturing.Texture2D;
import com.nucleus.texturing.TiledTexture2D;

/**
 * This class defines the mappings for the charset vertex and fragment shaders.
 * 
 * @author Richard Sahlin
 *
 */
public class PlayfieldProgram extends ShaderProgram {

    private final static String INVALID_TEXTURE_TYPE = "Invalid texture type: ";

    /**
     * Index into uniform char data where the texture fraction s (width) is
     */
    protected final static int UNIFORM_TEX_FRACTION_S_INDEX = 0;
    /**
     * Index into uniform charmap data where the texture fraction t (height) is
     */
    protected final static int UNIFORM_TEX_FRACTION_T_INDEX = 1;

    /**
     * Number of float data per vertex
     */
    protected final static int ATTRIBUTES_PER_VERTEX = 8;
    /**
     * Number of floats for each char in the attribute data.
     */
    protected final static int ATTRIBUTES_PER_CHAR = PlayfieldProgram.ATTRIBUTES_PER_VERTEX
            * VERTICES_PER_SPRITE;

    protected final static int ATTRIBUTE_1_OFFSET = 0;
    protected final static int ATTRIBUTE_2_OFFSET = 4;

    /**
     * Index into aCharset for x position
     */
    protected final static int ATTRIBUTE_CHARMAP_X_INDEX = 0;
    /**
     * Index into aCharset for y position
     */
    protected final static int ATTRIBUTE_CHARMAP_Y_INDEX = 1;
    /**
     * Index into aCharset texture u coordinate - this is used to calculate texture coordinate with frame.
     */
    protected final static int ATTRIBUTE_CHARMAP_U_INDEX = 2;
    /**
     * Index into aCharset texture v coordinate - this is used to calculate texture coordinate with frame.
     */
    protected final static int ATTRIBUTE_CHARMAP_V_INDEX = 3;
    /**
     * Index into aCharset frame number, this is the charmap frame number to use.
     */
    protected final static int ATTRIBUTE_CHARMAP_FRAME_INDEX = 4;
    /**
     * Index into aCharset flags, controls x and y axis flip.
     */
    protected final static int ATTRIBUTE_CHARMAP_FLAGS_INDEX = 5;

    public enum VARIABLES implements VariableMapping {
        uMVPMatrix(0, 0, ShaderVariable.VariableType.UNIFORM),
        uCharsetData(1, 16, ShaderVariable.VariableType.UNIFORM),
        aPosition(2, 0, ShaderVariable.VariableType.ATTRIBUTE),
        aCharset(3, 0, ShaderVariable.VariableType.ATTRIBUTE),
        aCharset2(4, 4, ShaderVariable.VariableType.ATTRIBUTE);

        public final int index;
        public final int offset;
        private final VariableType type;

        private VARIABLES(int index, int offset, VariableType type) {
            this.index = index;
            this.offset = offset;
            this.type = type;
        }

        @Override
        public int getIndex() {
            return index;
        }

        @Override
        public int getOffset() {
            return offset;
        }

    }

    private final static String VERTEX_SHADER_NAME = "assets/charmapvertex.essl";
    private final static String FRAGMENT_SHADER_NAME = "assets/charmapfragment.essl";

    PlayfieldProgram() {
        super();
        attributesPerVertex = ATTRIBUTES_PER_VERTEX;
        vertexShaderName = VERTEX_SHADER_NAME;
        fragmentShaderName = FRAGMENT_SHADER_NAME;
        uniforms = new VariableMapping[] { VARIABLES.uMVPMatrix, VARIABLES.uCharsetData };
    }

    @Override
    public int getVariableIndex(ShaderVariable variable) {
        return VARIABLES.valueOf(getVariableName(variable)).index;
    }

    @Override
    public int getVariableCount() {
        return VARIABLES.values().length;
    }

    @Override
    public void bindUniforms(GLES20Wrapper gles, float[] modelviewMatrix, Mesh mesh) throws GLException {
        // Refresh the matrix
        System.arraycopy(modelviewMatrix, 0, mesh.getUniforms(), VARIABLES.uMVPMatrix.offset, modelviewMatrix.length);
        bindUniforms(gles, uniforms, mesh.getUniforms());
    }

    @Override
    public void setupUniforms(Mesh mesh) {
        createUniformStorage(mesh, shaderVariables);
        Texture2D texture = mesh.getTexture(Texture2D.TEXTURE_0);
        if (texture instanceof TiledTexture2D) {
            setTextureUniforms((TiledTexture2D) texture, mesh.getUniforms(), VARIABLES.uCharsetData,
                    UNIFORM_TEX_FRACTION_S_INDEX);
        } else {
            System.err.println(INVALID_TEXTURE_TYPE + texture);
        }
    }

    @Override
    public void createProgram(GLES20Wrapper gles) {
        super.createProgram(gles);
        positionAttributes = new ShaderVariable[] { getShaderVariable(VARIABLES.aPosition) };
        positionOffsets = new int[] { 0 };
        genericAttributes = new ShaderVariable[] { getShaderVariable(VARIABLES.aCharset),
                getShaderVariable(VARIABLES.aCharset2) };
        genericOffsets = new int[] { ATTRIBUTE_1_OFFSET, ATTRIBUTE_2_OFFSET };
    }

}
