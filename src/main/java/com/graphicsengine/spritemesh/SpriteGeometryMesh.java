package com.graphicsengine.spritemesh;

import java.io.IOException;

import com.nucleus.Backend.DrawMode;
import com.nucleus.BackendException;
import com.nucleus.GraphicsPipeline;
import com.nucleus.geometry.Mesh;
import com.nucleus.opengl.GLPipeline;
import com.nucleus.opengl.geometry.GLMesh;
import com.nucleus.opengl.shader.GLShaderProgram;
import com.nucleus.opengl.shader.GLShaderProgram.Categorizer;
import com.nucleus.opengl.shader.GLShaderProgram.ProgramType;
import com.nucleus.opengl.shader.GLShaderProgram.ShaderType;
import com.nucleus.opengl.shader.GenericShaderProgram;
import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.renderer.Pass;

/**
 * SpriteMesh using a geometry shader
 * 
 *
 */
public class SpriteGeometryMesh extends SpriteMesh {

    static class GeometryCategorizer extends Categorizer {

        public GeometryCategorizer(Pass pass, GLShaderProgram.Shading shading, String category) {
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
        public Mesh create() throws IOException, BackendException {
            setArrayMode(DrawMode.POINTS, objectCount, 0);
            return super.create();
        }

        @Override
        public GraphicsPipeline createPipeline() {
            GLShaderProgram.Shading shading = GLShaderProgram.Shading.flat;
            GeometryCategorizer function = new GeometryCategorizer(null, shading, "sprite");
            GLShaderProgram shader = new GenericShaderProgram(function, ProgramType.VERTEX_GEOMETRY_FRAGMENT);
            return new GLPipeline(renderer, shader);
        }

    }
}
