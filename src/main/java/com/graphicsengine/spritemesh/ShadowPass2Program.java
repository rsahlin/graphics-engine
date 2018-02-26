package com.graphicsengine.spritemesh;

import com.nucleus.assets.AssetManager;
import com.nucleus.common.Constants;
import com.nucleus.geometry.Mesh;
import com.nucleus.io.ExternalReference;
import com.nucleus.opengl.GLES20Wrapper;
import com.nucleus.opengl.GLESWrapper.GLES20;
import com.nucleus.opengl.GLESWrapper.GLES30;
import com.nucleus.opengl.GLException;
import com.nucleus.renderer.Pass;
import com.nucleus.shader.CommonShaderVariables;
import com.nucleus.texturing.ParameterData;
import com.nucleus.texturing.Texture2D;
import com.nucleus.texturing.TextureFactory;
import com.nucleus.texturing.TextureParameter;
import com.nucleus.texturing.TextureParameter.Name;
import com.nucleus.texturing.TextureParameter.Param;
import com.nucleus.texturing.TextureParameter.Parameter;
import com.nucleus.texturing.TextureParameter.Target;
import com.nucleus.texturing.TextureType;
import com.nucleus.texturing.TextureUtils;
import com.nucleus.vecmath.Matrix;

/**
 * Shader for second shadow pass
 * This should combine the sprite program with shadow render
 *
 */
public class ShadowPass2Program extends TiledSpriteProgram {

    Texture2D shadow;

    public ShadowPass2Program(Pass pass, String category, Texture2D.Shading shading) {
        super(pass, shading, category);
        // This defines the texture parameters for the shadow pass.
        shadow = TextureFactory.createTexture(TextureType.Texture2D);
        ExternalReference ref = new ExternalReference(ExternalReference.ID_LOOKUP + "DEPTHshadow");
        shadow.setExternalReference(ref);
        shadow.set(new TextureParameter(
                new Parameter[] { Parameter.LINEAR, Parameter.LINEAR, Parameter.CLAMP, Parameter.CLAMP }));
        ParameterData[] extra = new ParameterData[] {
                new ParameterData(Target.TEXTURE_2D, Name.TEXTURE_COMPARE_MODE, Param.COMPARE_REF_TO_TEXTURE),
                new ParameterData(Target.TEXTURE_2D, Name.TEXTURE_COMPARE_FUNC, Param.LESS) };
    }

    @Override
    public void setUniformMatrices(float[] uniforms, float[][] matrices, Mesh mesh) {
        // Refresh the uniform matrix using light matrix
        System.arraycopy(matrices[0], 0, getUniforms(),
                shaderVariables[CommonShaderVariables.uMVMatrix.index].getOffset(),
                Matrix.MATRIX_ELEMENTS);
        System.arraycopy(matrices[1], 0, getUniforms(),
                shaderVariables[CommonShaderVariables.uProjectionMatrix.index].getOffset(),
                Matrix.MATRIX_ELEMENTS);
        System.arraycopy(matrices[2], 0, getUniforms(),
                shaderVariables[CommonShaderVariables.uLightMatrix.index].getOffset(),
                Matrix.MATRIX_ELEMENTS);
    }

    @Override
    public void prepareTextures(GLES20Wrapper gles, Mesh mesh) throws GLException {
        int textureID = shadow.getName();
        if (textureID == Constants.NO_VALUE) {
            AssetManager.getInstance().getIdReference(shadow);
            textureID = shadow.getName();
        }
        int unit = samplers[shaderVariables[CommonShaderVariables.uShadowTexture.index].getOffset()];
        TextureUtils.prepareTexture(gles, shadow, unit);
        gles.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES30.GL_TEXTURE_COMPARE_MODE, GLES30.GL_COMPARE_REF_TO_TEXTURE);
        gles.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES30.GL_TEXTURE_COMPARE_FUNC, GLES20.GL_LESS);
        Texture2D texture = mesh.getTexture(Texture2D.TEXTURE_0);
        if (texture != null && texture.textureType != TextureType.Untextured) {
            TextureUtils.prepareTexture(gles, texture,
                    samplers[shaderVariables[CommonShaderVariables.uTexture.index].getOffset()]);
        }
    }

}
