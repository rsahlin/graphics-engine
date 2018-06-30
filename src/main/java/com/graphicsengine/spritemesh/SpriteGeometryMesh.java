package com.graphicsengine.spritemesh;

import java.io.IOException;

import com.nucleus.assets.AssetManager;
import com.nucleus.geometry.Mesh;
import com.nucleus.opengl.GLES20Wrapper;
import com.nucleus.opengl.GLESWrapper.GLES20;
import com.nucleus.opengl.GLException;
import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.renderer.Pass;
import com.nucleus.shader.GenericShaderProgram;
import com.nucleus.shader.ShaderProgram;
import com.nucleus.shader.ShaderProgram.Function;
import com.nucleus.shader.ShaderProgram.ProgramType;
import com.nucleus.texturing.Texture2D.Shading;

/**
 * SpriteMesh using a geometry shader
 * 
 *
 */
public class SpriteGeometryMesh extends SpriteMesh {

    static class GeometryMeshFunction extends Function {

        public GeometryMeshFunction(Pass pass, Shading shading, String category) {
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
            GeometryMeshFunction function = new GeometryMeshFunction(null, shading, "sprite");
            ShaderProgram program = new GenericShaderProgram(function, ProgramType.VERTEX_GEOMETRY_FRAGMENT);
            return AssetManager.getInstance().getProgram(gles, program);
        }

    }
}
