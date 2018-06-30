package com.graphicsengine.spritemesh;

import java.io.IOException;

import com.nucleus.assets.AssetManager;
import com.nucleus.geometry.Mesh;
import com.nucleus.opengl.GLES20Wrapper;
import com.nucleus.opengl.GLException;
import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.shader.GenericShaderProgram;
import com.nucleus.shader.ShaderProgram;
import com.nucleus.shader.ShaderProgram.ProgramType;
import com.nucleus.texturing.Texture2D;
import com.nucleus.texturing.Texture2D.Shading;
import com.nucleus.texturing.TextureType;

/**
 * SpriteMesh using a geometry shader
 * 
 *
 */
public class SpriteGeometryMesh extends SpriteMesh {

    protected final static String INVALID_TYPE = "Invalid type: ";

    /**
     * Builder for sprite meshes
     *
     */
    public static class Builder extends Mesh.Builder<Mesh> {

        public Builder(NucleusRenderer renderer) {
            super(renderer);
        }

        @Override
        protected Mesh createMesh() {
            return new SpriteGeometryMesh();
        }

        @Override
        public Mesh create() throws IOException, GLException {
            setArrayMode(Mode.POINTS, objectCount, 0);
            return super.create();
        }

        @Override
        public ShaderProgram createProgram(GLES20Wrapper gles) {
            Shading shading = Shading.flat;
            Texture2D texture = getTexture();
            if (texture != null && texture.getTextureType() != TextureType.Untextured) {

            } else {

            }
            ShaderProgram program = new GenericShaderProgram(new String[] { "pointsprite", "flat", "flatsprite" },
                    null,
                    shading, null,
                    ProgramType.VERTEX_GEOMETRY_FRAGMENT);
            return AssetManager.getInstance().getProgram(gles, program);
        }

    }
}
