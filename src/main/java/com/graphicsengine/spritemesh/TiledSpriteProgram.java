package com.graphicsengine.spritemesh;

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
 * This class defines the mappings for the tile sprite vertex and fragment shaders.
 * This program has support for rotated sprites in Z axis, the sprite position and frame index can be set for each
 * sprite.
 * It is used by the {@link SpriteMeshNode}
 * 
 * @author Richard Sahlin
 *
 */
public class TiledSpriteProgram extends ShaderProgram {

    private final static String INVALID_TEXTURE_TYPE = "Invalid texture type: ";
    /**
     * Offset into uniform variable data where texture UV are.
     */
    private final static int UNIFORM_TEX_OFFSET = 0;

    /**
     * Number of float data per vertex
     */
    final static int ATTRIBUTES_PER_VERTEX = 16;
    /**
     * Number of floats for each tiled sprite in the attribute data.
     */
    private final static int ATTRIBUTES_PER_SPRITE = ATTRIBUTES_PER_VERTEX * VERTICES_PER_SPRITE;

    /**
     * Index into aTranslate for x position
     */
    final static int ATTRIBUTE_SPRITE_TRANSLATE_INDEX = 0;
    /**
     * Index into aRotate for rotation
     */
    final static int ATTRIBUTE_SPRITE_ROTATION_INDEX = 4;
    /**
     * Index into aScale for scale
     */
    final static int ATTRIBUTE_SPRITE_SCALE_INDEX = 8;
    /**
     * Index into aFrameData texture coordinates and frame - this is used to calculate texture coordinate with frame.
     */
    final static int ATTRIBUTE_SPRITE_FRAMEDATA = 14;
    /**
     * Index into attribute to get direct acces to uv coordinates
     */
    final static int ATTRIBUTE_SPRITE_UV = 12;

    public enum VARIABLES implements VariableMapping {
        uMVMatrix(0, 0, ShaderVariable.VariableType.UNIFORM, null),
        uProjectionMatrix(1, 16, ShaderVariable.VariableType.UNIFORM, null),
        uScreenSize(2, 32, ShaderVariable.VariableType.UNIFORM, null),
        uSpriteData(3, 34, ShaderVariable.VariableType.UNIFORM, null),
        aPosition(4, 0, ShaderVariable.VariableType.ATTRIBUTE, BufferIndex.VERTICES),
        aTranslate(5, 0, ShaderVariable.VariableType.ATTRIBUTE, BufferIndex.ATTRIBUTES),
        aRotate(6, 4, ShaderVariable.VariableType.ATTRIBUTE, BufferIndex.ATTRIBUTES),
        aScale(7, 8, ShaderVariable.VariableType.ATTRIBUTE, BufferIndex.ATTRIBUTES),
        aFrameData(8, 12, ShaderVariable.VariableType.ATTRIBUTE, BufferIndex.ATTRIBUTES);

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

    protected final static String VERTEX_SHADER_NAME = "assets/tiledspritevertex.essl";
    protected final static String FRAGMENT_SHADER_NAME = "assets/tiledspritefragment.essl";

    TiledSpriteProgram() {
        super(VARIABLES.values());
        vertexShaderName = VERTEX_SHADER_NAME;
        fragmentShaderName = FRAGMENT_SHADER_NAME;
        attributesPerVertex = ATTRIBUTES_PER_VERTEX;
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
        // Refresh the uniform matrix
        System.arraycopy(modelviewMatrix, 0, mesh.getUniforms(), VARIABLES.uMVMatrix.offset, Matrix.MATRIX_ELEMENTS);
        System.arraycopy(projectionMatrix, 0, mesh.getUniforms(), VARIABLES.uProjectionMatrix.offset,
                Matrix.MATRIX_ELEMENTS);
        bindUniforms(gles, uniforms, mesh.getUniforms());
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
    public void setupUniforms(Mesh mesh) {
        createUniformStorage(mesh, shaderVariables);
        float[] uniforms = mesh.getUniforms();
        setScreenSize(uniforms);
        Texture2D texture = mesh.getTexture(Texture2D.TEXTURE_0);
        if (texture instanceof TiledTexture2D) {
            setTextureUniforms((TiledTexture2D) texture, uniforms, shaderVariables[VARIABLES.uSpriteData.index],
                    UNIFORM_TEX_OFFSET);
        } else {
            System.err.println(INVALID_TEXTURE_TYPE + texture);
        }
    }

    /**
     * Sets the screensize to uniform storage
     * 
     * @param uniforms
     */
    protected void setScreenSize(float[] uniforms) {
        int screenSizeOffset = shaderVariables[VARIABLES.uScreenSize.index].getOffset();
        uniforms[screenSizeOffset++] = Window.getInstance().getWidth();
        uniforms[screenSizeOffset++] = Window.getInstance().getHeight();

    }

    @Override
    public int getAttributeOffset(int vertex) {
        return vertex * ATTRIBUTES_PER_VERTEX;
    }

    @Override
    public int getPropertyOffset(Property property) {
        switch (property) {
        case TRANSLATE:
            return ATTRIBUTE_SPRITE_TRANSLATE_INDEX;
        case ROTATE:
            return ATTRIBUTE_SPRITE_ROTATION_INDEX;
        case SCALE:
            return ATTRIBUTE_SPRITE_SCALE_INDEX;
        case FRAME:
            return ATTRIBUTE_SPRITE_FRAMEDATA;
        case UV:
            return ATTRIBUTE_SPRITE_UV;
        default:
            return -1;
        }
    }

}
