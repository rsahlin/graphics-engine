package com.graphicsengine.map;

import com.nucleus.geometry.Mesh;
import com.nucleus.opengl.GLES20Wrapper;
import com.nucleus.renderer.Pass;
import com.nucleus.shader.CommonShaderVariables;
import com.nucleus.shader.ShaderProgram;
import com.nucleus.texturing.Texture2D;
import com.nucleus.texturing.Texture2D.Shading;
import com.nucleus.texturing.TextureType;
import com.nucleus.texturing.TiledTexture2D;

/**
 * This class defines the mappings for the charset vertex and fragment shaders.
 * 
 * @author Richard Sahlin
 *
 */
public class PlayfieldProgram extends ShaderProgram {

    public static final String CATEGORY = "charmap";
    private final static String INVALID_TEXTURE_TYPE = "Invalid texture type: ";

    PlayfieldProgram() {
        super(null, null, CATEGORY, CommonShaderVariables.values(), Shaders.VERTEX_FRAGMENT);
    }

    @Override
    public int getVariableCount() {
        return CommonShaderVariables.values().length;
    }

    @Override
    public void setUniformData(float[] destinationUniform, Mesh mesh) {
        setScreenSize(uniforms, shaderVariables[CommonShaderVariables.uScreenSize.index]);
        Texture2D texture = mesh.getTexture(Texture2D.TEXTURE_0);
        if (texture.getTextureType() == TextureType.TiledTexture2D) {
            setTextureUniforms((TiledTexture2D) texture, uniforms,
                    shaderVariables[CommonShaderVariables.uTextureData.index]);
        } else {
            System.err.println(INVALID_TEXTURE_TYPE + texture);
        }
        setAmbient(uniforms, shaderVariables[CommonShaderVariables.uAmbientLight.index], globalLight.getAmbient());
    }

    @Override
    public ShaderProgram getProgram(GLES20Wrapper gles, Pass pass, Shading shading) {
        switch (pass) {
            case UNDEFINED:
            case ALL:
            case MAIN:
                return this;
            default:
                throw new IllegalArgumentException("Invalid pass " + pass);
        }
    }

    @Override
    public void initBuffers(Mesh mesh) {
        // TODO Auto-generated method stub

    }
}
