package com.graphicsengine.spritemesh;

import java.io.IOException;

import com.nucleus.Backend.DrawMode;
import com.nucleus.BackendException;
import com.nucleus.GraphicsPipeline;
import com.nucleus.geometry.AttributeBuffer;
import com.nucleus.geometry.Mesh;
import com.nucleus.geometry.shape.RectangleShapeBuilder;
import com.nucleus.opengl.geometry.GLMesh;
import com.nucleus.opengl.shader.GLShaderProgram;
import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.shader.Shader.Shading;
import com.nucleus.texturing.Texture2D;
import com.nucleus.texturing.TiledTexture2D;
import com.nucleus.texturing.UVTexture2D;
import com.nucleus.texturing.Untextured;

/**
 * A number of quads that will be rendered using the same Mesh, ie all quads in this class are rendered using
 * one draw call. This is done by batching the data for each quad.
 * Use the @link {@link TiledSpriteProgram} to render the mesh.
 * Main usecase is if OpenGLES version is prior to 3.2 and does not support geometry shaders.
 * This can also be used to render chars in a playfield.
 * This class only contains the drawable parts of the sprites - no logic is contained in this class.
 * 
 * @author Richard Sahlin
 *
 */
public class SpriteMesh extends GLMesh {

    /**
     * Builder for sprite meshes
     *
     */
    public static class Builder extends GLMesh.Builder<Mesh> {

        protected final static String INVALID_TYPE = "Invalid type: ";

        /**
         * 
         * @param renderer
         */
        public Builder(NucleusRenderer renderer) {
            super(renderer);
        }

        @Override
        public Mesh create() throws IOException, BackendException {
            setElementMode(DrawMode.TRIANGLES, objectCount * RectangleShapeBuilder.QUAD_VERTICES, 0,
                    objectCount * RectangleShapeBuilder.QUAD_ELEMENTS);
            return super.create();
        }

        @Override
        public GraphicsPipeline createPipeline() throws BackendException {
            // SpriteMesh is a special type of mesh that only works with specific shader program
            return renderer.getAssets().getGraphicsPipeline(renderer, createProgram(texture));
        }

        @Override
        public Mesh createInstance() {
            return new SpriteMesh();
        }

        /**
         * Creates the shader program to use with the specified texture.
         * 
         * @param texture {@link TiledTexture2D} or {@link UVTexture2D}
         * @return The shader program for the specified texture.
         */
        public GLShaderProgram createProgram(Texture2D texture) {
            switch (texture.textureType) {
                case TiledTexture2D:
                    return new TiledSpriteProgram((TiledTexture2D) texture, Shading.textured);
                case UVTexture2D:
                    return new UVSpriteProgram((UVTexture2D) texture);
                case Untextured:
                    return new TiledSpriteProgram(null, ((Untextured) texture).getShading());
                case Texture2D:
                    throw new IllegalArgumentException("Not supported");
                    // return new UVSpriteProgram();
                    // TODO - fix so that transformprogram loads the correct shader - 'transformvertex', currently
                    // loads texturedvertex. Use tiled or uv texture in the meantime.
                    // return new TransformProgram(null, Texture2D.Shading.textured, null);
                default:
                    throw new IllegalArgumentException(INVALID_TYPE + texture.textureType);
            }

        }

    }

    /**
     * Creates a new instance, mesh will NOT be created.
     */
    protected SpriteMesh() {
        super();
    }

    /**
     * Creates a new instance of the tiled sprite mesh based on the source.
     * This will NOT create the mesh and sprites it will only set the values from the source.
     * {@link #createMesh(TiledSpriteProgram, Texture2D, float[], float[])}
     * 
     * @param source
     */
    protected SpriteMesh(Mesh source) {
        super(source);
    }

    /**
     * Returns the tiled texture at the specified active texture index, for tiled sheets that only have 1 texture index
     * will always be 0
     * 
     * @param index Index to texture, starts at 0 and increases.
     * @return The tiled texture
     * @throws ArrayIndexOutOfBoundsException If index < 0 or > number of textures - 1
     */
    public TiledTexture2D getTiledTexture(int index) {
        return (TiledTexture2D) getTexture(index);
    }

    /**
     * Copies 4 attribute values from the source array to the specified offset in sprite.
     * This will update the mesh attributes, ie the data will be copied to 4 vertices.
     * 
     * @param sprite The sprite number
     * @param offset Offset to attribute to set
     * @param source The source array
     * @param sourceIndex Index into source where data is copied from.
     * @param sizePerVertex
     */
    public void setAttribute4(int sprite, int offset, float[] source, int sourceIndex, int sizePerVertex) {
        int index = sizePerVertex * 4 * sprite;
        AttributeBuffer attributeBuffer = getAttributeBuffer(BufferIndex.ATTRIBUTES.index);
        attributeBuffer.setArray(source, sourceIndex, index + offset, 4);
        index += sizePerVertex;
        attributeBuffer.setArray(source, sourceIndex, index + offset, 4);
        index += sizePerVertex;
        attributeBuffer.setArray(source, sourceIndex, index + offset, 4);
        index += sizePerVertex;
        attributeBuffer.setArray(source, sourceIndex, index + offset, 4);
        attributeBuffer.setDirty(true);
    }

    /**
     * Copies 3 attribute values from the source array to the specified offset in sprite.
     * This will update the mesh attributes, ie the data will be copied to 4 vertices.
     * 
     * @param sprite The sprite number
     * @param offset Offset to attribute to set
     * @param source The source array
     * @param sourceIndex Index into source where data is copied from.
     * @param sizePerVertex
     */
    public void setAttribute3(int sprite, int offset, float[] source, int sourceIndex, int sizePerVertex) {
        int index = sizePerVertex * 4 * sprite;
        AttributeBuffer attributeBuffer = getAttributeBuffer(BufferIndex.ATTRIBUTES.index);
        attributeBuffer.setArray(source, sourceIndex, index + offset, 3);
        index += sizePerVertex;
        attributeBuffer.setArray(source, sourceIndex, index + offset, 3);
        index += sizePerVertex;
        attributeBuffer.setArray(source, sourceIndex, index + offset, 3);
        index += sizePerVertex;
        attributeBuffer.setArray(source, sourceIndex, index + offset, 3);
        attributeBuffer.setDirty(true);
    }

    /**
     * Copies 2 attribute values from the source array to the specified offset in sprite.
     * This will update the mesh attributes, ie the data will be copied to 4 vertices.
     * 
     * @param sprite The sprite number
     * @param offset Offset to attribute to set
     * @param source The source array
     * @param sourceIndex Index into source where data is copied from.
     * @param sizePerVertex
     */
    public void setAttribute2(int sprite, int offset, float[] source, int sourceIndex, int sizePerVertex) {
        int index = sizePerVertex * 4 * sprite;
        AttributeBuffer attributeBuffer = getAttributeBuffer(BufferIndex.ATTRIBUTES.index);
        attributeBuffer.setArray(source, sourceIndex, index + offset, 2);
        index += sizePerVertex;
        attributeBuffer.setArray(source, sourceIndex, index + offset, 2);
        index += sizePerVertex;
        attributeBuffer.setArray(source, sourceIndex, index + offset, 2);
        index += sizePerVertex;
        attributeBuffer.setArray(source, sourceIndex, index + offset, 2);
        attributeBuffer.setDirty(true);
    }

}
