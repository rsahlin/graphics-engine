package com.graphicsengine.spritemesh;

import com.nucleus.assets.AssetManager;
import com.nucleus.geometry.Mesh;
import com.nucleus.opengl.GLESWrapper.GLES20;
import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.renderer.Pass;
import com.nucleus.shader.ShaderProgram;
import com.nucleus.shader.CommonShaderVariables;
import com.nucleus.shader.ShadowPass1Program;
import com.nucleus.texturing.Texture2D;
import com.nucleus.texturing.Texture2D.Shading;
import com.nucleus.texturing.TextureType;
import com.nucleus.texturing.TiledTexture2D;

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

    protected static final String CATEGORY = "sprite";
    /**
     * Offset into uniform variable data where texture UV are.
     */
    private final static int UNIFORM_TEX_OFFSET = 0;

    TiledSpriteProgram(Texture2D.Shading shading) {
        super(null, shading, CATEGORY, CommonShaderVariables.values(), Shaders.VERTEX_FRAGMENT);
    }

    @Override
    protected String getShaderSource(int type) {
        if (function.getPass() != null || type == GLES20.GL_VERTEX_SHADER) {
            return super.getShaderSource(type);
        }
        // Hardcoded fragment shader used by subclass as well
        return PROGRAM_DIRECTORY + function.getShadingString() + CATEGORY + FRAGMENT_TYPE + SHADER_SOURCE_SUFFIX;
    }

    protected TiledSpriteProgram(Pass pass, Texture2D.Shading shading, String category) {
        super(pass, shading, category, CommonShaderVariables.values(), Shaders.VERTEX_FRAGMENT);
    }

    @Override
    public void setUniformData(float[] uniforms, Mesh mesh) {
        setScreenSize(uniforms, shaderVariables[CommonShaderVariables.uScreenSize.index]);
        setTextureUniforms(uniforms, mesh.getTexture(Texture2D.TEXTURE_0));
    }

    protected void setTextureUniforms(float[] uniforms, Texture2D texture) {
        if (texture.getTextureType() == TextureType.TiledTexture2D) {
            setTextureUniforms((TiledTexture2D) texture, uniforms, shaderVariables[CommonShaderVariables.uTextureData.index],
                    UNIFORM_TEX_OFFSET);
        }
    }

    @Override
    public ShaderProgram getProgram(NucleusRenderer renderer, Pass pass, Shading shading) {
        switch (pass) {
            case UNDEFINED:
            case ALL:
            case MAIN:
                return this;
            case SHADOW1:
                return AssetManager.getInstance().getProgram(renderer, new ShadowPass1Program(this, shading, CATEGORY));
            case SHADOW2:
                return AssetManager.getInstance().getProgram(renderer, new ShadowPass2Program(pass, null, shading));
            default:
                throw new IllegalArgumentException("Invalid pass " + pass);
        }
    }

}
