package com.graphicsengine.spritemesh;

import com.nucleus.assets.AssetManager;
import com.nucleus.common.Constants;
import com.nucleus.geometry.Mesh;
import com.nucleus.io.ExternalReference;
import com.nucleus.opengl.GLES20Wrapper;
import com.nucleus.opengl.GLESWrapper.GLES20;
import com.nucleus.opengl.GLException;
import com.nucleus.opengl.GLUtils;
import com.nucleus.shader.ShaderVariables;
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

    public ShadowPass2Program(Texture2D.Shading shading) {
        super(shading, ShaderVariables.values());
        shadow = TextureFactory.createTexture(TextureType.Texture2D);
        ExternalReference ref = new ExternalReference(ExternalReference.ID_LOOKUP + "DEPTHshadow");
        shadow.setExternalReference(ref);
        shadow.set(new TextureParameter(TextureParameter.DEFAULT_TEXTURE_PARAMETERS));
    }

    @Override
    protected void setShaderSource(Texture2D.Shading shading) {
        vertexShaderName = PROGRAM_DIRECTORY + VERTEX_NAME + shading.name() + VERTEX + SHADER_SOURCE_SUFFIX;
        fragmentShaderName = PROGRAM_DIRECTORY + FRAGMENT_NAME + shading.name() + FRAGMENT + SHADER_SOURCE_SUFFIX;
    }

    @Override
    public void bindUniforms(GLES20Wrapper gles, float[][] matrices, Mesh mesh)
            throws GLException {
        setScreenSize(mesh);
        setTextureUniforms(mesh.getTexture(Texture2D.TEXTURE_0));
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
        setUniforms(gles, sourceUniforms);

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

    }

}
