package com.graphicsengine.spritemesh;

import java.io.IOException;

import com.nucleus.Backend.DrawMode;
import com.nucleus.BackendException;
import com.nucleus.geometry.Mesh;
import com.nucleus.opengl.geometry.GLMesh;
import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.renderer.Pass;
import com.nucleus.shader.GenericShaderProgram;
import com.nucleus.shader.GraphicsShader;
import com.nucleus.shader.Shader.Categorizer;
import com.nucleus.shader.Shader.ProgramType;
import com.nucleus.shader.Shader.ShaderType;
import com.nucleus.shader.Shader.Shading;

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
        public GraphicsShader createProgram() throws BackendException {
            Shading shading = Shading.flat;
            GenericShaderProgram shader = new GenericShaderProgram();
            shader.init(null, null, shading, "sprite", ProgramType.VERTEX_FRAGMENT);
            return renderer.getAssets().getGraphicsPipeline(renderer, shader);
        }

    }
}
