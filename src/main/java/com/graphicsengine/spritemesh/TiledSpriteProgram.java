package com.graphicsengine.spritemesh;

import com.nucleus.geometry.Mesh;
import com.nucleus.geometry.Mesh.BufferIndex;
import com.nucleus.opengl.GLES20Wrapper;
import com.nucleus.opengl.GLException;
import com.nucleus.shader.ShaderProgram;
import com.nucleus.shader.ShaderVariable;
import com.nucleus.shader.ShaderVariable.VariableType;
import com.nucleus.shader.VariableMapping;
import com.nucleus.texturing.Texture2D;
import com.nucleus.texturing.TiledTexture2D;

/**
 * This class defines the mappings for the tile sprite vertex and fragment shaders.
 * This program has support for rotated sprites in Z axis, the sprite position and frame index can be set for each
 * sprite.
 * It is used by the {@link SpriteMeshController}
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
    protected final static int ATTRIBUTES_PER_VERTEX = 8;
    /**
     * Number of floats for each tiled sprite in the attribute data.
     */
    public final static int ATTRIBUTES_PER_SPRITE = ATTRIBUTES_PER_VERTEX * VERTICES_PER_SPRITE;

    /**
     * Index into aTileSprite for x position
     */
    protected final static int ATTRIBUTE_SPRITE_X_INDEX = 0;
    /**
     * Index into aTileSprite for y position
     */
    protected final static int ATTRIBUTE_SPRITE_Y_INDEX = 1;
    /**
     * Index into aTileSprite2 z position
     */
    protected final static int ATTRIBUTE_SPRITE_Z_INDEX = 2;
    /**
     * Index into aTileSprite frame number, this is the sprite frame number to use.
     */
    protected final static int ATTRIBUTE_SPRITE_FRAME_INDEX = 3;
    /**
     * Index into aTileSprite texture u coordinate - this is used to calculate texture coordinate with frame.
     */
    protected final static int ATTRIBUTE_SPRITE_U_INDEX = 4;
    /**
     * Index into aTileSprite texture v coordinate - this is used to calculate texture coordinate with frame.
     */
    protected final static int ATTRIBUTE_SPRITE_V_INDEX = 5;
    /**
     * Index into aTileSprite z axis rotation
     */
    protected final static int ATTRIBUTE_SPRITE_ROTATION_INDEX = 6;
    /**
     * Index into aTileSprite for scale
     */
    protected final static int ATTRIBUTE_SPRITE_SCALE_INDEX = 7;

    public enum VARIABLES implements VariableMapping {
        uMVPMatrix(0, 0, ShaderVariable.VariableType.UNIFORM, null),
        uSpriteData(1, 16, ShaderVariable.VariableType.UNIFORM, null),
        aPosition(2, 0, ShaderVariable.VariableType.ATTRIBUTE, BufferIndex.VERTICES),
        aTileSprite(3, 0, ShaderVariable.VariableType.ATTRIBUTE, BufferIndex.ATTRIBUTES),
        aTileSprite2(4, 4, ShaderVariable.VariableType.ATTRIBUTE, BufferIndex.ATTRIBUTES);

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

    private final static String VERTEX_SHADER_NAME = "assets/tiledspritevertex.essl";
    private final static String FRAGMENT_SHADER_NAME = "assets/tiledspritefragment.essl";

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
    public void bindUniforms(GLES20Wrapper gles, float[] modelviewMatrix, Mesh mesh) throws GLException {
        // Refresh the uniform matrix
        System.arraycopy(modelviewMatrix, 0, mesh.getUniforms(), VARIABLES.uMVPMatrix.offset, modelviewMatrix.length);
        bindUniforms(gles, uniforms, mesh.getUniforms());
    }

    @Override
    public void setupUniforms(Mesh mesh) {
        createUniformStorage(mesh, shaderVariables);
        float[] uniforms = mesh.getUniforms();
        Texture2D texture = mesh.getTexture(Texture2D.TEXTURE_0);
        if (texture instanceof TiledTexture2D) {
            setTextureUniforms((TiledTexture2D) texture, uniforms, VARIABLES.uSpriteData, UNIFORM_TEX_OFFSET);
        } else {
            System.err.println(INVALID_TEXTURE_TYPE + texture);
        }
    }

    @Override
    public void createProgram(GLES20Wrapper gles) {
        super.createProgram(gles);
    }

}
