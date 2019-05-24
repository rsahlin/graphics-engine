package com.graphicsengine.spritemesh;

import java.io.IOException;

import com.nucleus.assets.AssetManager;
import com.nucleus.geometry.Mesh;
import com.nucleus.opengl.GLException;
import com.nucleus.opengl.geometry.GLMesh;
import com.nucleus.opengl.shader.GenericShaderProgram;
import com.nucleus.opengl.shader.ShaderProgram;
import com.nucleus.opengl.shader.ShaderProgram.Categorizer;
import com.nucleus.opengl.shader.ShaderProgram.ProgramType;
import com.nucleus.opengl.shader.ShaderProgram.ShaderType;
import com.nucleus.renderer.Backend.DrawMode;
import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.renderer.Pass;

/**
 * SpriteMesh using a geometry shader
 * 
 *
 */
public class SpriteGeometryMesh extends SpriteMesh {

    static class GeometryCategorizer extends Categorizer {

        public GeometryCategorizer(Pass pass, ShaderProgram.Shading shading, String category) {
            super(pass, shading, category);
        }

        @Override
        public String getPath(ShaderType type) {
            switch (type) {
                case FRAGMENT:
                    return "";
                default:
                    return super.getPath(type);
            }
        }

    }

    protected final static String INVALID_TYPE = "Invalid type: ";

    /**
     * Builder for sprite meshes
     *
     */
    public static class Builder extends GLMesh.Builder<Mesh> {

        public Builder(NucleusRenderer renderer) {
            super(renderer);
        }

        @Override
        public Mesh createInstance() {
            return new SpriteGeometryMesh();
        }

        @Override
        public Mesh create() throws IOException, GLException {
            setArrayMode(DrawMode.POINTS, objectCount, 0);
            return super.create();
        }

        @Override
        public ShaderProgram createProgram() {
            ShaderProgram.Shading shading = ShaderProgram.Shading.flat;
            GeometryCategorizer function = new GeometryCategorizer(null, shading, "sprite");
            ShaderProgram program = new GenericShaderProgram(function, ProgramType.VERTEX_GEOMETRY_FRAGMENT);
            return AssetManager.getInstance().getProgram(renderer.getGLES(), program);
        }

    }
}
