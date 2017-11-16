package com.graphicsengine.spritemesh;

import com.nucleus.assets.AssetManager;
import com.nucleus.geometry.Mesh;
import com.nucleus.opengl.GLES20Wrapper;
import com.nucleus.opengl.GLException;
import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.renderer.Pass;
import com.nucleus.shader.ShaderProgram;
import com.nucleus.shader.ShaderVariables;
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

    public static final String SPRITE = "sprite";
    /**
     * Offset into uniform variable data where texture UV are.
     */
    private final static int UNIFORM_TEX_OFFSET = 0;

    TiledSpriteProgram(Texture2D.Shading shading) {
        super(null, shading, null, ShaderVariables.values());
    }
    
    protected TiledSpriteProgram(Pass pass, Texture2D.Shading shading, String category) {
        super(pass, shading, category, ShaderVariables.values());
    }

    @Override
    protected void setShaderSource() {
        vertexShaderName = PROGRAM_DIRECTORY + sourceName.getShading().name() + SPRITE + VERTEX_TYPE
                + SHADER_SOURCE_SUFFIX;
        fragmentShaderName = PROGRAM_DIRECTORY + sourceName.getShading().name() + SPRITE + FRAGMENT_TYPE
                + SHADER_SOURCE_SUFFIX;
    }
    
    @Override
    public void bindUniforms(GLES20Wrapper gles, float[][] matrices, Mesh mesh)
            throws GLException {
        super.bindUniforms(gles, matrices, mesh);
        setScreenSize(mesh);
        setTextureUniforms(mesh.getTexture(Texture2D.TEXTURE_0));
        setUniforms(gles, sourceUniforms);
    }

    protected void setTextureUniforms(Texture2D texture) {
        if (texture.getTextureType() == TextureType.TiledTexture2D) {
            setTextureUniforms((TiledTexture2D) texture, uniforms, shaderVariables[ShaderVariables.uTextureData.index],
                    UNIFORM_TEX_OFFSET);
        }
    }

    /**
     * Sets the screensize in the uniforms
     */
    protected void setScreenSize(Mesh mesh) {
        setScreenSize(getUniforms(), shaderVariables[ShaderVariables.uScreenSize.index]);
    }

    @Override
    public ShaderProgram getProgram(NucleusRenderer renderer, Pass pass, Shading shading) {
        switch (pass) {
            case UNDEFINED:
            case ALL:
            case MAIN:
                return this;
            case SHADOW1:
                return AssetManager.getInstance().getProgram(renderer, new ShadowPass1Program(shading, null));
            case SHADOW2:
                return AssetManager.getInstance().getProgram(renderer, new ShadowPass2Program(pass, null, shading));
                default:
            throw new IllegalArgumentException("Invalid pass " + pass);
        }
    }

}
