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
import com.nucleus.shader.ShaderSource;
import com.nucleus.shader.ShadowPass1Program;
import com.nucleus.texturing.Texture2D;
import com.nucleus.texturing.Texture2D.Shading;

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
            return ShaderSource.V300;
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
