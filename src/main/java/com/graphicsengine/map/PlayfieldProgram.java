package com.graphicsengine.map;

import com.nucleus.geometry.AttributeUpdater.Consumer;
import com.nucleus.geometry.AttributeUpdater.Property;
import com.nucleus.geometry.Mesh;
import com.nucleus.geometry.Mesh.BufferIndex;
import com.nucleus.geometry.VertexBuffer;
import com.nucleus.opengl.GLES20Wrapper;
import com.nucleus.opengl.GLException;
import com.nucleus.renderer.Window;
import com.nucleus.shader.ShaderProgram;
import com.nucleus.shader.ShaderVariable;
import com.nucleus.shader.ShaderVariable.VariableType;
import com.nucleus.shader.VariableMapping;
import com.nucleus.texturing.Texture2D;
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
     * Number of floats for each char in the attribute data.
     */
    private final static int ATTRIBUTES_PER_CHAR = PlayfieldProgram.ATTRIBUTES_PER_VERTEX
            * VERTICES_PER_SPRITE;

    /**
     * Index into aCharset for translate position
     */
    private final static int ATTRIBUTE_CHARMAP_TRANSLATE_INDEX = 0;
    /**
     * Index into aCharset texture uv coordinate - this is used to calculate texture coordinate with frame.
     */
    private final static int ATTRIBUTE_CHARMAP_UV_INDEX = 2;
    /**
     * The char frame number
     */
    private final static int ATTRIBUTE_CHARMAP_FRAME_INDEX = 4;

    public enum VARIABLES implements VariableMapping {
        uMVMatrix(0, 0, ShaderVariable.VariableType.UNIFORM, null),
        uProjectionMatrix(1, 16, ShaderVariable.VariableType.UNIFORM, null),
        uCharsetData(2, 32, ShaderVariable.VariableType.UNIFORM, null),
        uScreenSize(3, 35, ShaderVariable.VariableType.UNIFORM, null),
        aPosition(4, 0, ShaderVariable.VariableType.ATTRIBUTE, BufferIndex.VERTICES),
        aCharset(5, 0, ShaderVariable.VariableType.ATTRIBUTE, BufferIndex.ATTRIBUTES),
        aCharset2(6, 4, ShaderVariable.VariableType.ATTRIBUTE, BufferIndex.ATTRIBUTES);

        private final int index;
        private final VariableType type;
        private final int offset;
        private final BufferIndex bufferIndex;

        /**
         * @param index Index of the shader variable
         * @param offset Offset into data array where the variable data source is
         * @param type Type of variable
         * @param bufferIndex Index of buffer in mesh that holds the variable data
         */
        private VARIABLES(int index, int offset, VariableType type, BufferIndex bufferIndex) {
            this.index = index;
            this.type = type;
            this.offset = offset;
            this.bufferIndex = bufferIndex;
        }

        @Override
        public int getIndex() {
            return index;
        }

        @Override
        public int getOffset() {
            return offset;
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
        attributesPerVertex = ATTRIBUTES_PER_VERTEX;
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
        System.arraycopy(modelviewMatrix, 0, mesh.getUniforms(), VARIABLES.uMVMatrix.offset, Matrix.MATRIX_ELEMENTS);
        System.arraycopy(projectionMatrix, 0, mesh.getUniforms(), VARIABLES.uProjectionMatrix.offset,
                Matrix.MATRIX_ELEMENTS);
        bindUniforms(gles, uniforms, mesh.getUniforms());
    }

    @Override
    public void setupUniforms(Mesh mesh) {
        createUniformStorage(mesh, shaderVariables);
        int screenSizeOffset = shaderVariables[VARIABLES.uScreenSize.index].getOffset();
        float[] uniforms = mesh.getUniforms();
//      uniforms[screenSizeOffset++] = Window.getInstance().getWidth();
//      uniforms[screenSizeOffset++] = Window.getInstance().getHeight();
        uniforms[screenSizeOffset++] = Window.getInstance().getWidth();
        uniforms[screenSizeOffset++] = Window.getInstance().getHeight();
        Texture2D texture = mesh.getTexture(Texture2D.TEXTURE_0);
        if (texture instanceof TiledTexture2D) {
            setTextureUniforms((TiledTexture2D) texture, uniforms,
                    shaderVariables[VARIABLES.uCharsetData.index],
                    UNIFORM_TEX_OFFSET);
        } else {
            System.err.println(INVALID_TEXTURE_TYPE + texture);
        }
    }

    @Override
    public void createProgram(GLES20Wrapper gles) {
        super.createProgram(gles);
    }

    @Override
    public int getAttributeOffset(int vertex) {
        return vertex * ATTRIBUTES_PER_VERTEX;
    }

    @Override
    public VertexBuffer createAttributeBuffer(int verticeCount, Mesh mesh) {
        VertexBuffer buffer = super.createAttributeBuffer(verticeCount, mesh);
        if (mesh instanceof Consumer) {
            ((Consumer) mesh).bindAttributeBuffer(buffer);
        }
        return buffer;
    }

    @Override
    public int getPropertyOffset(Property property) {
        switch (property) {
        case TRANSLATE:
            return ATTRIBUTE_CHARMAP_TRANSLATE_INDEX;
        case FRAME:
            return ATTRIBUTE_CHARMAP_FRAME_INDEX;
        case UV:
            return ATTRIBUTE_CHARMAP_UV_INDEX;
        default:
            return -1;
        }
    }
}
