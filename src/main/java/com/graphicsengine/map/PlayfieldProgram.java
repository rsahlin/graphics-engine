package com.graphicsengine.map;

import com.nucleus.geometry.Mesh;
import com.nucleus.opengl.GLES20Wrapper;
import com.nucleus.opengl.GLException;
import com.nucleus.opengl.GLUtils;
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

    /**
     * Index into uniform char data where the texture fraction s (width) is
     */
    protected final static int UNIFORM_TEX_FRACTION_S_INDEX = 0;
    /**
     * Index into uniform charmap data where the texture fraction t (height) is
     */
    protected final static int UNIFORM_TEX_FRACTION_T_INDEX = 1;

    /**
     * Index into uniform charmap data where 1 / texture fraction w - this is used to calculate y pos from frame index
     */
    protected final static int UNIFORM_TEX_ONEBY_S_INDEX = 2;

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

    public enum VARIABLES {
        uMVPMatrix(0, ShaderVariable.VariableType.UNIFORM),
        uCharsetData(1, ShaderVariable.VariableType.UNIFORM),
        aPosition(2, ShaderVariable.VariableType.ATTRIBUTE),
        aCharset(3, ShaderVariable.VariableType.ATTRIBUTE),
        aCharset2(4, ShaderVariable.VariableType.ATTRIBUTE);

        public final int index;
        private final VariableType type;

        private VARIABLES(int index, VariableType type) {
            this.index = index;
            this.type = type;
        }

    }

    private final static String VERTEX_SHADER_NAME = "assets/charmapvertex.essl";
    private final static String FRAGMENT_SHADER_NAME = "assets/charmapfragment.essl";

    PlayfieldProgram() {
        super();
        attributesPerVertex = ATTRIBUTES_PER_VERTEX;
        vertexShaderName = VERTEX_SHADER_NAME;
        fragmentShaderName = FRAGMENT_SHADER_NAME;
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
        ShaderVariable v = getShaderVariable(VARIABLES.uMVPMatrix.index);
        System.arraycopy(modelviewMatrix, 0, mesh.getUniformMatrices(), 0, modelviewMatrix.length);
        gles.glUniformMatrix4fv(getShaderVariable(VARIABLES.uMVPMatrix.index).getLocation(), v.getSize(), false,
                mesh.getUniformMatrices(), 0);
        GLUtils.handleError(gles, "glUniformMatrix4fv ");
        v = getShaderVariable(VARIABLES.uCharsetData.index);
        if (v != null) {
            setVectorUniform(gles, v, mesh.getUniformVectors(), 0);
        }
        GLUtils.handleError(gles, "glUniform4fv ");
    }

    @Override
    public void setupUniforms(Mesh mesh) {
        createUniformStorage(mesh, shaderVariables);
        float[] uniformVectors = mesh.getUniformVectors();
        Texture2D texture = mesh.getTexture(Texture2D.TEXTURE_0);
        if (texture instanceof TiledTexture2D) {
            setTextureUniforms((TiledTexture2D) texture, uniformVectors, UNIFORM_TEX_FRACTION_S_INDEX);
        } else {
            uniformVectors[UNIFORM_TEX_FRACTION_S_INDEX] = 1f;
            uniformVectors[UNIFORM_TEX_FRACTION_T_INDEX] = 1f;
            uniformVectors[UNIFORM_TEX_ONEBY_S_INDEX] = 1f;
        }
    }

    @Override
    public void createProgram(GLES20Wrapper gles) {
        super.createProgram(gles);
        positionAttributes = new ShaderVariable[] { getShaderVariable(VARIABLES.aPosition.index) };
        positionOffsets = new int[] { 0 };
        genericAttributes = new ShaderVariable[] { getShaderVariable(VARIABLES.aCharset.index),
                getShaderVariable(VARIABLES.aCharset2.index) };
        genericOffsets = new int[] { ATTRIBUTE_1_OFFSET, ATTRIBUTE_2_OFFSET };
    }

}
