package com.graphicsengine.spritemesh;

import com.nucleus.assets.AssetManager;
import com.nucleus.geometry.Mesh;
import com.nucleus.opengl.GLES20Wrapper;
import com.nucleus.opengl.GLESWrapper.GLES20;
import com.nucleus.opengl.GLESWrapper.Renderers;
import com.nucleus.opengl.GLException;
import com.nucleus.renderer.Pass;
import com.nucleus.shader.CommonShaderVariables;
import com.nucleus.shader.QuadExpanderShader;
import com.nucleus.shader.ShaderProgram;
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

    protected QuadExpanderShader expanderShader;

    /**
     * Offset into uniform variable data where texture UV are.
     */
    private final static int UNIFORM_TEX_OFFSET = 0;

    TiledSpriteProgram(Texture2D.Shading shading) {
        super(null, shading, CATEGORY, CommonShaderVariables.values(), Shaders.VERTEX_FRAGMENT);
    }

    @Override
    public void createProgram(GLES20Wrapper gles) throws GLException {
        super.createProgram(gles);
        if (gles.getInfo().getRenderVersion().major > 2) {
            expanderShader = (QuadExpanderShader) AssetManager.getInstance().getProgram(gles, new QuadExpanderShader());
        }
    }

    protected TiledSpriteProgram(Pass pass, Texture2D.Shading shading, String category) {
        super(pass, shading, category, CommonShaderVariables.values(), Shaders.VERTEX_FRAGMENT);
    }

    @Override
    protected String getSourceNameVersion(Renderers version, int type) {
        if (version.major >= 3) {
            return "_v300";
        }
        return super.getSourceNameVersion(version, type);
    }

    @Override
    protected Function getFunction(int type) {
        switch (type) {
            case GLES20.GL_VERTEX_SHADER:
                return function;
            case GLES20.GL_FRAGMENT_SHADER:
                // For sprite fragment shader ignore the category
                return new Function(function.getPass(), function.getShading(), null);
            default:
                throw new IllegalArgumentException("Not valid for type " + type);
        }
    }

    @Override
    public void setUniformData(float[] destinationUniform, Mesh mesh) {
        setScreenSize(destinationUniform, shaderVariables[CommonShaderVariables.uScreenSize.index]);
        setTextureUniforms(destinationUniform, mesh.getTexture(Texture2D.TEXTURE_0));
    }

    /**
     * Sets the data related to texture uniforms in the uniform float storage
     * 
     * @param uniforms
     * @param texture
     */
    protected void setTextureUniforms(float[] uniforms, Texture2D texture) {
        if (texture.getTextureType() == TextureType.TiledTexture2D) {
            setTextureUniforms((TiledTexture2D) texture, uniforms,
                    shaderVariables[CommonShaderVariables.uTextureData.index],
                    UNIFORM_TEX_OFFSET);
        }
    }

    @Override
    public ShaderProgram getProgram(GLES20Wrapper gles, Pass pass, Shading shading) {
        switch (pass) {
            case UNDEFINED:
            case ALL:
            case MAIN:
                return this;
            case SHADOW1:
                return AssetManager.getInstance().getProgram(gles, new ShadowPass1Program(this, shading, CATEGORY));
            case SHADOW2:
                return AssetManager.getInstance().getProgram(gles, new ShadowPass2Program(pass, null, shading));
            default:
                throw new IllegalArgumentException("Invalid pass " + pass);
        }
    }

    /**
     * Returns the expander shader to be used with this program.
     * 
     * @return
     */
    public QuadExpanderShader getExpanderShader() {
        return expanderShader;
    }

    @Override
    public void initBuffers(Mesh mesh) {
        // TODO Auto-generated method stub

    }

}
