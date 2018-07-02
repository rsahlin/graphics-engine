package com.graphicsengine.spritemesh;

import com.nucleus.geometry.Mesh;
import com.nucleus.opengl.GLES20Wrapper;
import com.nucleus.opengl.GLESWrapper.GLES20;
import com.nucleus.opengl.GLESWrapper.Renderers;
import com.nucleus.opengl.GLException;
import com.nucleus.renderer.Pass;
import com.nucleus.shader.QuadExpanderShader;
import com.nucleus.shader.ShaderProgram;
import com.nucleus.shader.ShaderSource;
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

    static class SpriteCategorizer extends Categorizer {

        public SpriteCategorizer(Pass pass, Shading shading, String category) {
            super(pass, shading, category);
        }

        @Override
        public String getShaderSourceName(int shaderType) {
            switch (shaderType) {
                case GLES20.GL_FRAGMENT_SHADER:
                    // Fragment shaders are shared - skip category path
                    return getPassString() + getShadingString();
            }
            return (getPath(shaderType) + getPassString() + getShadingString());
        }

    }

    /**
     * This uses gles 20 - deprecated in favour of geometry shader
     */
    protected static final String CATEGORY = "tiledsprite20";

    protected QuadExpanderShader expanderShader;

    TiledSpriteProgram(Texture2D.Shading shading) {
        super(new SpriteCategorizer(null, shading, CATEGORY), ProgramType.VERTEX_FRAGMENT);
        setIndexer(new TiledSpriteIndexer());
    }

    protected TiledSpriteProgram(Pass pass, Texture2D.Shading shading, String category) {
        super(new SpriteCategorizer(pass, shading, category), ProgramType.VERTEX_FRAGMENT);
        setIndexer(new TiledSpriteIndexer());
    }

    @Override
    public void createProgram(GLES20Wrapper gles) throws GLException {
        if (GLES20Wrapper.getInfo().getRenderVersion().major >= 3
                && GLES20Wrapper.getInfo().getRenderVersion().minor >= 1) {
            // expanderShader = (QuadExpanderShader) AssetManager.getInstance().getProgram(gles,
            // new QuadExpanderShader());
        }
        super.createProgram(gles);

    }

    @Override
    protected String getSourceNameVersion(Renderers version, int type) {
        if (version.major >= 3) {
            return ShaderSource.V300;
        }
        return super.getSourceNameVersion(version, type);
    }

    @Override
    public void updateUniformData(float[] destinationUniform, Mesh mesh) {
        setScreenSize(destinationUniform, getUniformByName("uScreenSize"));
        setTextureUniforms(destinationUniform, mesh.getTexture(Texture2D.TEXTURE_0));
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
