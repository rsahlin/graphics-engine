package com.graphicsengine.spritemesh;

import java.io.IOException;

import com.graphicsengine.component.SpriteAttributeComponent;
import com.nucleus.assets.AssetManager;
import com.nucleus.geometry.Mesh;
import com.nucleus.geometry.shape.RectangleShapeBuilder;
import com.nucleus.opengl.GLException;
import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.shader.ShaderProgram;
import com.nucleus.texturing.Texture2D;
import com.nucleus.texturing.TiledTexture2D;
import com.nucleus.texturing.UVTexture2D;
import com.nucleus.texturing.Untextured;

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
    public static class Builder extends Mesh.Builder<SpriteGeometryMesh> {
        /**
         * Internal constructor - avoid using directly if the mesh should belong to a specific node type.
         * Use
         * {@link SpriteAttributeComponent#createMeshBuilder(NucleusRenderer, com.nucleus.scene.ComponentNode, int, com.nucleus.vecmath.Rectangle)}
         * instead
         * 
         * @param renderer
         */
        Builder(NucleusRenderer renderer) {
            super(renderer);
        }

        public static Builder createBuilder(NucleusRenderer renderer) {
            return new Builder(renderer);
        }

        @Override
        public Mesh create() throws IOException, GLException {
            setElementMode(Mode.TRIANGLES, objectCount * RectangleShapeBuilder.QUAD_VERTICES,
                    objectCount * RectangleShapeBuilder.QUAD_ELEMENTS);
            if (material.getProgram() == null) {
                ShaderProgram program = createProgram(texture);
                program = AssetManager.getInstance().getProgram(renderer.getGLES(), program);
                material.setProgram(program);
            }
            return super.create();
        }

        @Override
        protected Mesh createMesh() {
            return new SpriteGeometryMesh();
        }

        /**
         * Creates the shader program to use with the specified texture.
         * 
         * @param texture {@link TiledTexture2D} or {@link UVTexture2D}
         * @return The shader program for the specified texture.
         */
        public ShaderProgram createProgram(Texture2D texture) {
            switch (texture.textureType) {
                case TiledTexture2D:
                    return new TiledSpriteProgram(Texture2D.Shading.textured);
                case UVTexture2D:
                    return new UVSpriteProgram();
                case Untextured:
                    return new TiledSpriteProgram(((Untextured) texture).getShading());
                case Texture2D:
                    // TODO - fix so that transformprogram loads the correct shader - 'transformvertex', currently
                    // loads texturedvertex. Use tiled or uv texture in the meantime.
                    // return new TransformProgram(null, Texture2D.Shading.textured, null);
                default:
                    throw new IllegalArgumentException(INVALID_TYPE + texture.textureType);
            }

        }

    }
}
