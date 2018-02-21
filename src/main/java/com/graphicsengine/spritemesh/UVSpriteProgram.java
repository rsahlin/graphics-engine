package com.graphicsengine.spritemesh;

import com.nucleus.assets.AssetManager;
import com.nucleus.opengl.GLES20Wrapper;
import com.nucleus.renderer.Pass;
import com.nucleus.shader.ShaderProgram;
import com.nucleus.shader.ShadowPass1Program;
import com.nucleus.texturing.Texture2D;
import com.nucleus.texturing.Texture2D.Shading;
import com.nucleus.texturing.UVTexture2D;

/**
 * This class defines the mapping for the UV sprite vertex and fragment shaders.
 * This program has support for a number of sprites with frames defined by UV coordinates for each sprite corner,
 * this means that the sprites can have different sizes.
 * 
 * @author Richard Sahlin
 *
 */
public class UVSpriteProgram extends TiledSpriteProgram {

    protected static final String CATEGORY = "uvsprite";
    protected static final String VERTEX_SHADER_NAME = "assets/uvspritevertex.essl";
    protected UVTexture2D uvTexture;

    public UVSpriteProgram() {
        super(null, Texture2D.Shading.textured, CATEGORY);
    }

    @Override
    protected void setTextureUniforms(float[] uniforms, Texture2D texture) {
        if (uvTexture == null) {
            uvTexture = (UVTexture2D) texture;
        }

        if (uvTexture.getUVAtlasBuffer().isDirty()) {

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
                return this;
            default:
                throw new IllegalArgumentException("Invalid pass " + pass);
        }
    }

}
