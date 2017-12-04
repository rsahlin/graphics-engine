package com.graphicsengine.spritemesh;

import com.nucleus.assets.AssetManager;
import com.nucleus.common.Constants;
import com.nucleus.geometry.Mesh;
import com.nucleus.io.ExternalReference;
import com.nucleus.opengl.GLES20Wrapper;
import com.nucleus.opengl.GLESWrapper.GLES20;
import com.nucleus.opengl.GLException;
import com.nucleus.opengl.GLUtils;
import com.nucleus.renderer.Pass;
import com.nucleus.shader.ShaderVariables;
import com.nucleus.shader.VariableMapping;
import com.nucleus.texturing.Texture2D;
import com.nucleus.texturing.TextureFactory;
import com.nucleus.texturing.TextureParameter;
import com.nucleus.texturing.TextureType;
import com.nucleus.vecmath.Matrix;

/**
 * Shader for second shadow pass
 * This should combine the sprite program with shadow render
 *
 */
public class ShadowPass2Program extends TiledSpriteProgram {

    Texture2D shadow;

    /**
     * Name of this shader - TODO where should this be defined?
     */
    protected static final String VERTEX_NAME = "shadow2";
    protected static final String FRAGMENT_NAME = "shadow2";

    public ShadowPass2Program(Pass pass, String category, Texture2D.Shading shading) {
        super(pass, shading, category);
        shadow = TextureFactory.createTexture(TextureType.Texture2D);
        ExternalReference ref = new ExternalReference(ExternalReference.ID_LOOKUP + "DEPTHshadow");
        shadow.setExternalReference(ref);
        shadow.set(new TextureParameter(TextureParameter.DEFAULT_TEXTURE_PARAMETERS));
    }

    @Override
    public void setUniformData(float[] uniforms, Mesh mesh) {
        setScreenSize(uniforms, shaderVariables[ShaderVariables.uScreenSize.index]);
        setTextureUniforms(uniforms, mesh.getTexture(Texture2D.TEXTURE_0));
    }

    @Override
    public void setUniformMatrices(float[] uniforms, float[][] matrices, Mesh mesh) {
        // Refresh the uniform matrix using light matrix
        System.arraycopy(matrices[0], 0, getUniforms(),
                shaderVariables[ShaderVariables.uMVMatrix.index].getOffset(),
                Matrix.MATRIX_ELEMENTS);
        System.arraycopy(matrices[1], 0, getUniforms(),
                shaderVariables[ShaderVariables.uProjectionMatrix.index].getOffset(),
                Matrix.MATRIX_ELEMENTS);
        System.arraycopy(matrices[2], 0, getUniforms(),
                shaderVariables[ShaderVariables.uLightMatrix.index].getOffset(),
                Matrix.MATRIX_ELEMENTS);
    }

    @Override
    public void setUniforms(GLES20Wrapper gles, float[] uniforms, VariableMapping[] uniformMapping) throws GLException {
        int textureID = shadow.getName();
        if (textureID == Constants.NO_VALUE) {
            AssetManager.getInstance().getIdReference(shadow);
            textureID = shadow.getName();
            gles.glBindTexture(GLES20.GL_TEXTURE_2D, textureID);
            gles.uploadTexParameters(shadow.getTexParams());
        } else {
            gles.glBindTexture(GLES20.GL_TEXTURE_2D, textureID);
            GLUtils.handleError(gles, "glBindTexture()");
        }
        super.setUniforms(gles, uniforms, uniformMapping);
    }

}
