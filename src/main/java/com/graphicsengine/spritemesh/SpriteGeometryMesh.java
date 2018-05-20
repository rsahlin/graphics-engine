package com.graphicsengine.spritemesh;

import java.io.IOException;

import com.nucleus.assets.AssetManager;
import com.nucleus.geometry.Mesh;
import com.nucleus.opengl.GLException;
import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.shader.TransformProgram;
import com.nucleus.texturing.Texture2D.Shading;

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
    public static class Builder extends SpriteMesh.Builder {

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
            if (program == null) {
                program = AssetManager.getInstance().getProgram(renderer.getGLES(),
                        new TransformProgram(null, Shading.textured, TransformProgram.CATEGORY));
            }
            return super.create();
        }

    }
}
