package com.graphicsengine.spritemesh;

import java.io.IOException;

import com.nucleus.assets.AssetManager;
import com.nucleus.geometry.Mesh;
import com.nucleus.opengl.GLES20Wrapper;
import com.nucleus.opengl.GLESWrapper.GLES20;
import com.nucleus.opengl.GLException;
import com.nucleus.renderer.Pass;
import com.nucleus.shader.GenericShaderProgram;
import com.nucleus.shader.ShaderProgram;
import com.nucleus.shader.ShaderProgram.Categorizer;
import com.nucleus.shader.ShaderProgram.ProgramType;
import com.nucleus.texturing.Texture2D.Shading;

/**
 * SpriteMesh using a geometry shader
 * 
 *
 */
public class SpriteGeometryMesh extends SpriteMesh {

    static class GeometryCategorizer extends Categorizer {

        public GeometryCategorizer(Pass pass, Shading shading, String category) {
            super(pass, shading, category);
        }

        @Override
        public String getPath(int shaderType) {
            switch (shaderType) {
                case GLES20.GL_FRAGMENT_SHADER:
                    return "";
                default:
                    return super.getPath(shaderType);
            }
        }

    }

    protected final static String INVALID_TYPE = "Invalid type: ";

    /**
     * Builder for sprite meshes
     *
     */
    public static class Builder extends Mesh.Builder<Mesh> {

        public Builder(GLES20Wrapper gles) {
            super(gles);
        }

        @Override
        public Mesh createInstance() {
            return new SpriteGeometryMesh();
        }

        @Override
        public Mesh create() throws IOException, GLException {
            setArrayMode(Mode.POINTS, objectCount, 0);
            return super.create();
        }

        @Override
        public ShaderProgram createProgram() {
            Shading shading = Shading.flat;
            GeometryCategorizer function = new GeometryCategorizer(null, shading, "sprite");
            ShaderProgram program = new GenericShaderProgram(function, ProgramType.VERTEX_GEOMETRY_FRAGMENT);
            return AssetManager.getInstance().getProgram(gles, program);
        }

    }
}
