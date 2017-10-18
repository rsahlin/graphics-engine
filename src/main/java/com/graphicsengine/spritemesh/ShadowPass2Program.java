package com.graphicsengine.spritemesh;

import com.nucleus.geometry.Mesh;
import com.nucleus.opengl.GLES20Wrapper;
import com.nucleus.opengl.GLException;
import com.nucleus.shader.ShaderVariables;
import com.nucleus.shader.ShadowPass1Program;
import com.nucleus.texturing.Texture2D;
import com.nucleus.vecmath.Matrix;

public class ShadowPass2Program extends TiledSpriteProgram {

    /**
     * Name of this shader - TODO where should this be defined?
     */
    protected static final String VERTEX_NAME = "shadow2";
    protected static final String FRAGMENT_NAME = "shadow2";

    public ShadowPass2Program() {
        super(ShaderVariables.values());
    }

    @Override
    protected void setShaderSource(Texture2D.Shading shading) {
        vertexShaderName = PROGRAM_DIRECTORY + VERTEX_NAME + VERTEX + SHADER_SOURCE_SUFFIX;
        fragmentShaderName = PROGRAM_DIRECTORY + FRAGMENT_NAME + FRAGMENT + SHADER_SOURCE_SUFFIX;
    }

    @Override
    public void bindUniforms(GLES20Wrapper gles, float[] modelviewMatrix, float[] projectionMatrix, Mesh mesh)
            throws GLException {
        setScreenSize(mesh);
        setTextureUniforms(mesh.getTexture(Texture2D.TEXTURE_0));

        // Refresh the uniform matrix
        System.arraycopy(modelviewMatrix, 0, getUniforms(),
                shaderVariables[ShaderVariables.uMVMatrix.index].getOffset(),
                Matrix.MATRIX_ELEMENTS);
        System.arraycopy(projectionMatrix, 0, getUniforms(),
                shaderVariables[ShaderVariables.uProjectionMatrix.index].getOffset(),
                Matrix.MATRIX_ELEMENTS);
        System.arraycopy(ShadowPass1Program.getLightMatrix(), 0, getUniforms(),
                shaderVariables[ShaderVariables.uLightMatrix.index].getOffset(),
                Matrix.MATRIX_ELEMENTS);
        bindUniforms(gles, sourceUniforms, getUniforms());
    }

}
