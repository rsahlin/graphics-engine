package com.graphicsengine.map;

import com.nucleus.geometry.Mesh;
import com.nucleus.opengl.GLES20Wrapper;
import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.renderer.Pass;
import com.nucleus.shader.ShaderProgram;
import com.nucleus.shader.ShaderVariable;
import com.nucleus.shader.ShaderVariables;
import com.nucleus.shader.VariableMapping;
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

    /**
     * Offset into uniform variable data where texture UV are.
     */
    private final static int UNIFORM_TEX_OFFSET = 0;

    PlayfieldProgram() {
        super(null, null, CATEGORY, ShaderVariables.values(), Shaders.VERTEX_FRAGMENT);
    }

    @Override
    public VariableMapping getVariableMapping(ShaderVariable variable) {
        return ShaderVariables.valueOf(getVariableName(variable));
    }

    @Override
    public int getVariableCount() {
        return ShaderVariables.values().length;
    }

    @Override
    public void setUniformData(float[] uniforms, Mesh mesh) {
        setScreenSize(uniforms, shaderVariables[ShaderVariables.uScreenSize.index]);
        Texture2D texture = mesh.getTexture(Texture2D.TEXTURE_0);
        if (texture.getTextureType() == TextureType.TiledTexture2D) {
            setTextureUniforms((TiledTexture2D) texture, uniforms,
                    shaderVariables[ShaderVariables.uTextureData.index],
                    UNIFORM_TEX_OFFSET);
        } else {
            System.err.println(INVALID_TEXTURE_TYPE + texture);
        }
        setAmbient(getUniforms(), shaderVariables[ShaderVariables.uAmbientLight.index], globalLight.getAmbient());
    }

    @Override
    public void createProgram(GLES20Wrapper gles) {
        super.createProgram(gles);
    }

    @Override
    public ShaderProgram getProgram(NucleusRenderer renderer, Pass pass, Shading shading) {
        switch (pass) {
            case UNDEFINED:
            case ALL:
            case MAIN:
                return this;
            default:
                throw new IllegalArgumentException("Invalid pass " + pass);
        }
    }
}
