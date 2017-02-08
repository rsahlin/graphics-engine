package com.graphicsengine.spritemesh;

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
import com.nucleus.texturing.TiledTexture2D;
import com.nucleus.vecmath.Matrix;

/**
 * This class defines the mappings for the tile sprite vertex and fragment shaders.
 * This program has support for rotated sprites in Z axis, the sprite position and frame index can be set for each
 * sprite.
 * It is used by the {@link SpriteMesh}
 * 
 * @author Richard Sahlin
 *
 */
public class TiledSpriteProgram extends ShaderProgram {

    public static final String SPRITE = "sprite";
    private final static String INVALID_TEXTURE_TYPE = "Invalid texture type: ";
    /**
     * Offset into uniform variable data where texture UV are.
     */
    private final static int UNIFORM_TEX_OFFSET = 0;

    /**
     * The shader names used, the variable names used in shader sources MUST be defined here.
     */
    public enum VARIABLES implements VariableMapping {
        uMVMatrix(0, ShaderVariable.VariableType.UNIFORM, null),
        uProjectionMatrix(1, ShaderVariable.VariableType.UNIFORM, null),
        uScreenSize(2, ShaderVariable.VariableType.UNIFORM, null),
        uSpriteData(3, ShaderVariable.VariableType.UNIFORM, null),
        aPosition(4, ShaderVariable.VariableType.ATTRIBUTE, BufferIndex.VERTICES),
        aUV(5, ShaderVariable.VariableType.ATTRIBUTE, BufferIndex.VERTICES),
        aTranslate(6, ShaderVariable.VariableType.ATTRIBUTE, BufferIndex.ATTRIBUTES),
        aRotate(7, ShaderVariable.VariableType.ATTRIBUTE, BufferIndex.ATTRIBUTES),
        /**
         * Scale in z has no meaning, use only x and y
         */
        aScale(8, ShaderVariable.VariableType.ATTRIBUTE, BufferIndex.ATTRIBUTES),
        aColor(9, ShaderVariable.VariableType.ATTRIBUTE, BufferIndex.ATTRIBUTES),
        aFrameData(10, ShaderVariable.VariableType.ATTRIBUTE, BufferIndex.ATTRIBUTES);
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

    protected final static String VERTEX_SHADER_NAME = "assets/tiledspritevertex.essl";
    protected final static String FRAGMENT_SHADER_NAME = "assets/tiledspritefragment.essl";

    TiledSpriteProgram() {
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
        // Refresh the uniform matrix
        // TODO prefetch the offsets for the shader variables and store in array.
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
        setScreenSize(mesh);
        Texture2D texture = mesh.getTexture(Texture2D.TEXTURE_0);
        if (texture instanceof TiledTexture2D) {
            setTextureUniforms((TiledTexture2D) texture, uniforms, shaderVariables[VARIABLES.uSpriteData.index],
                    UNIFORM_TEX_OFFSET);
        } else {
            System.err.println(INVALID_TEXTURE_TYPE + texture);
        }
    }

    /**
     * Sets the screensize in the uniforms
     */
    protected void setScreenSize(Mesh mesh) {
        setScreenSize(mesh.getUniforms(), shaderVariables[VARIABLES.uScreenSize.index]);
    }

    @Override
    public int getPropertyOffset(Property property) {
        ShaderVariable v = null;
        switch (property) {
        case TRANSLATE:
            v = shaderVariables[TiledSpriteProgram.VARIABLES.aPosition.index];
            break;
        case ROTATE:
            v = shaderVariables[TiledSpriteProgram.VARIABLES.aRotate.index];
            break;
        case SCALE:
            v = shaderVariables[TiledSpriteProgram.VARIABLES.aScale.index];
            break;
        case FRAME:
            v = shaderVariables[TiledSpriteProgram.VARIABLES.aFrameData.index];
            break;
        case COLOR:
            v = shaderVariables[TiledSpriteProgram.VARIABLES.aColor.index];
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
