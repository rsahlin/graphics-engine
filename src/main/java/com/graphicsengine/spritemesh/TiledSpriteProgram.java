package com.graphicsengine.spritemesh;

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
     * Index into uniform sprite data data where the texture fraction s (width) is
     */
    private final static int UNIFORM_TEX_FRACTION_S_INDEX = 0;
    /**
     * Index into uniform sprite data data where the texture fraction t (height) is
     */
    protected final static int UNIFORM_TEX_FRACTION_T_INDEX = 1;

    /**
     * Number of float data per vertex
     */
    protected final static int ATTRIBUTES_PER_VERTEX = 8;
    /**
     * Number of floats for each tiled sprite in the attribute data.
     */
    public final static int ATTRIBUTES_PER_SPRITE = ATTRIBUTES_PER_VERTEX * VERTICES_PER_SPRITE;

    protected final static int ATTRIBUTE_1_OFFSET = 0;
    protected final static int ATTRIBUTE_2_OFFSET = 4;

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
        uMVPMatrix(0, 0, ShaderVariable.VariableType.UNIFORM),
        uSpriteData(1, 16, ShaderVariable.VariableType.UNIFORM),
        aPosition(2, 0, ShaderVariable.VariableType.ATTRIBUTE),
        aTileSprite(3, 0, ShaderVariable.VariableType.ATTRIBUTE),
        aTileSprite2(4, 4, ShaderVariable.VariableType.ATTRIBUTE);

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

    private final static String VERTEX_SHADER_NAME = "assets/tiledspritevertex.essl";
    private final static String FRAGMENT_SHADER_NAME = "assets/tiledspritefragment.essl";

    TiledSpriteProgram() {
        super();
        vertexShaderName = VERTEX_SHADER_NAME;
        fragmentShaderName = FRAGMENT_SHADER_NAME;
        attributesPerVertex = ATTRIBUTES_PER_VERTEX;
        uniforms = new VariableMapping[] { VARIABLES.uMVPMatrix, VARIABLES.uSpriteData };
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
            setTextureUniforms((TiledTexture2D) texture, uniforms, VARIABLES.uSpriteData, UNIFORM_TEX_FRACTION_S_INDEX);
        } else {
            System.err.println(INVALID_TEXTURE_TYPE + texture);
        }
    }

    @Override
    public void createProgram(GLES20Wrapper gles) {
        super.createProgram(gles);
        positionAttributes = new ShaderVariable[] { getShaderVariable(VARIABLES.aPosition) };
        positionOffsets = new int[] { 0 };
        genericAttributes = new ShaderVariable[] { getShaderVariable(VARIABLES.aTileSprite),
                getShaderVariable(VARIABLES.aTileSprite2) };
        genericOffsets = new int[] { ATTRIBUTE_1_OFFSET, ATTRIBUTE_2_OFFSET };
    }

}
