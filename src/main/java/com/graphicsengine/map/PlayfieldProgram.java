package com.graphicsengine.map;

import com.nucleus.SimpleLogger;
import com.nucleus.geometry.AttributeUpdater.Property;
import com.nucleus.geometry.Mesh;
import com.nucleus.geometry.Mesh.BufferIndex;
import com.nucleus.opengl.GLES20Wrapper;
import com.nucleus.opengl.GLException;
import com.nucleus.shader.ShaderProgram;
import com.nucleus.shader.ShaderVariable;
import com.nucleus.shader.ShaderVariable.VariableType;
import com.nucleus.shader.VariableMapping;
import com.nucleus.texturing.Texture2D;
import com.nucleus.texturing.TextureType;
import com.nucleus.texturing.TiledTexture2D;
import com.nucleus.vecmath.Matrix;

/**
 * This class defines the mappings for the charset vertex and fragment shaders.
 * 
 * @author Richard Sahlin
 *
 */
public class PlayfieldProgram extends ShaderProgram {

    private final static String INVALID_TEXTURE_TYPE = "Invalid texture type: ";

    /**
     * Offset into uniform variable data where texture UV are.
     */
    private final static int UNIFORM_TEX_OFFSET = 0;

    /**
     * Number of float data per vertex
     */
    private final static int ATTRIBUTES_PER_VERTEX = 8;

    /**
     * The shader names used, the variable names used in shader sources MUST be defined here.
     */
    public enum VARIABLES implements VariableMapping {
        uMVMatrix(0, ShaderVariable.VariableType.UNIFORM, null),
        uProjectionMatrix(1, ShaderVariable.VariableType.UNIFORM, null),
        uCharsetData(2, ShaderVariable.VariableType.UNIFORM, null),
        uScreenSize(3, ShaderVariable.VariableType.UNIFORM, null),
        uAmbient(4, ShaderVariable.VariableType.UNIFORM, null),
        uDiffuse(5, ShaderVariable.VariableType.UNIFORM, null),
        aPosition(6, ShaderVariable.VariableType.ATTRIBUTE, BufferIndex.VERTICES),
        aUV(7, ShaderVariable.VariableType.ATTRIBUTE, BufferIndex.VERTICES),
        aCharset(8, ShaderVariable.VariableType.ATTRIBUTE, BufferIndex.ATTRIBUTES),
        aCharset2(9, ShaderVariable.VariableType.ATTRIBUTE, BufferIndex.ATTRIBUTES);

        private final int index;
        private final VariableType type;
        private final BufferIndex bufferIndex;

        /**
         * @param index Index of the shader variable
         * @param type Type of variable
         * @param bufferIndex Index of buffer in mesh that holds the variable data
         */
        private VARIABLES(int index, VariableType type, BufferIndex bufferIndex) {
            this.index = index;
            this.type = type;
            this.bufferIndex = bufferIndex;
        }

        @Override
        public int getIndex() {
            return index;
        }

        @Override
        public VariableType getType() {
            return type;
        }

        @Override
        public BufferIndex getBufferIndex() {
            return bufferIndex;
        }

    }

    private final static String VERTEX_SHADER_NAME = "assets/charmapvertex.essl";
    private final static String FRAGMENT_SHADER_NAME = "assets/charmapfragment.essl";

    PlayfieldProgram() {
        super(VARIABLES.values());
        vertexShaderName = VERTEX_SHADER_NAME;
        fragmentShaderName = FRAGMENT_SHADER_NAME;
    }

    @Override
    public VariableMapping getVariableMapping(ShaderVariable variable) {
        return VARIABLES.valueOf(getVariableName(variable));
    }

    @Override
    public int getVariableCount() {
        return VARIABLES.values().length;
    }

    @Override
    public void bindUniforms(GLES20Wrapper gles, float[] modelviewMatrix, float[] projectionMatrix, Mesh mesh)
            throws GLException {
        // Refresh the matrix
        System.arraycopy(modelviewMatrix, 0, mesh.getUniforms(), shaderVariables[VARIABLES.uMVMatrix.index].getOffset(),
                Matrix.MATRIX_ELEMENTS);
        System.arraycopy(projectionMatrix, 0, mesh.getUniforms(),
                shaderVariables[VARIABLES.uProjectionMatrix.index].getOffset(),
                Matrix.MATRIX_ELEMENTS);
        bindUniforms(gles, uniforms, mesh.getUniforms());
    }

    @Override
    public void setupUniforms(Mesh mesh) {
        createUniformStorage(mesh, shaderVariables);
        float[] uniforms = mesh.getUniforms();
        setScreenSize(uniforms, shaderVariables[VARIABLES.uScreenSize.index]);
        Texture2D texture = mesh.getTexture(Texture2D.TEXTURE_0);
        if (texture.getTextureType() == TextureType.TiledTexture2D) {
            setTextureUniforms((TiledTexture2D) texture, uniforms,
                    shaderVariables[VARIABLES.uCharsetData.index],
                    UNIFORM_TEX_OFFSET);
        } else {
            System.err.println(INVALID_TEXTURE_TYPE + texture);
        }
        setAmbient(uniforms, shaderVariables[VARIABLES.uAmbient.index], mesh.getMaterial());

    }

    @Override
    public void createProgram(GLES20Wrapper gles) {
        super.createProgram(gles);
    }

    @Override
    public int getPropertyOffset(Property property) {
        ShaderVariable v = null;
        switch (property) {
        case TRANSLATE:
            v = shaderVariables[VARIABLES.aCharset.index];
            break;
        case FRAME:
            v = shaderVariables[VARIABLES.aCharset2.index];
            break;
        default:
        }
        if (v != null) {
            return v.getOffset();
        } else {
            SimpleLogger.d(getClass(), "No ShaderVariable for " + property);
        }
        return -1;
    }
}
