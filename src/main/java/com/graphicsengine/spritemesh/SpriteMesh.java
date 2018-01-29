package com.graphicsengine.spritemesh;

import java.io.IOException;

import com.nucleus.assets.AssetManager;
import com.nucleus.geometry.AttributeBuffer;
import com.nucleus.geometry.Mesh;
import com.nucleus.geometry.MeshBuilder;
import com.nucleus.geometry.RectangleShapeBuilder;
import com.nucleus.opengl.GLException;
import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.shader.ShaderProgram;
import com.nucleus.texturing.Texture2D;
import com.nucleus.texturing.TiledTexture2D;
import com.nucleus.texturing.UVTexture2D;
import com.nucleus.texturing.Untextured;
import com.nucleus.vecmath.Rectangle;

/**
 * A number of quads that will be rendered using the same Mesh, ie all quads in this class are rendered using
 * one draw call.
 * Use the @link {@link TiledSpriteProgram} to render the mesh.
 * This can also be used to render chars in a playfield.
 * This class only contains the drawable parts of the sprites - no logic is contained in this class.
 * 
 * @author Richard Sahlin
 *
 */
public class SpriteMesh extends Mesh {

    /**
     * Contains attribute data for all sprites - this is a copy of the attribute buffer, since this will double-buffer
     * the attribute buffer there is no need for syncronization while writing into this buffer.
     * This data must be mapped into the mesh attribute buffer for changes to take place.
     */
    // protected transient FloatBuffer attributeData;

    public static class Builder extends Mesh.Builder<SpriteMesh> {

        private final static String INVALID_TYPE = "Invalid type: ";

        private int spriteCount;

        /**
         * Creates a new SpriteMesh builder
         * 
         * @param renderer
         * @throws IllegalArgumentException If renderer is null
         */
        public Builder(NucleusRenderer renderer) {
            super(renderer);
        }

        /**
         * Sets the number of sprites (quads) that the mesh shall support
         * 
         * @param spriteCount Number of sprites (quads) to support
         * @return
         */
        public Builder setSpriteCount(int spriteCount) {
            this.spriteCount = spriteCount;
            setElementMode(Mode.TRIANGLES, spriteCount * RectangleShapeBuilder.QUAD_VERTICES,
                    spriteCount * RectangleShapeBuilder.QUAD_ELEMENTS);
            return this;
        }

        @Override
        public Mesh create() throws IOException, GLException {
            if (material.getProgram() == null) {
                ShaderProgram program = createProgram(texture);
                program = AssetManager.getInstance().getProgram(renderer, program);
                material.setProgram(program);
            }
            return super.create();
            /**
             * SpriteMesh mesh = new SpriteMesh();
             * mesh.createMesh(texture, material, vertexCount, indiceCount, mode);
             * if (Configuration.getInstance().isUseVBO()) {
             * BufferObjectsFactory.getInstance().createVBOs(renderer, mesh);
             * }
             */
        }

        @Override
        protected Mesh createMesh() {
            return new SpriteMesh();
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
     * This method should be moved to RectangleShapeBuilder
     * Builds one quad at the specified index, use this call to create the quads to be drawn individually.
     * Before using this call the indexed buffer (indices) must be built in the mesh, ie this method will only
     * set the vertex positions and UV for this quad
     * This will setup the quad according to the specified size and anchor. Texture UV will be built based
     * on the texture type.
     * 
     * @param index
     * @param program
     * @param rectangle The rectangle defining the sprite
     */
    @Deprecated
    public void buildQuad(int index, ShaderProgram program, Rectangle rectangle) {
        int vertexStride = program.getVertexStride();
        float[] quadPositions = new float[vertexStride * 4];
        Texture2D texture = getTexture(Texture2D.TEXTURE_0);
        RectangleShapeBuilder.createQuadArray(rectangle, texture, vertexStride, 0, quadPositions);
        MeshBuilder.buildQuads(this, program, 1, index, quadPositions);
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
     */
    public void setAttribute4(int sprite, int offset, float[] source, int sourceIndex) {
        int index = mapper.attributesPerVertex * 4 * sprite;
        AttributeBuffer attributeBuffer = getVerticeBuffer(BufferIndex.ATTRIBUTES.index);
        attributeBuffer.setArray(source, sourceIndex, index + offset, 4);
        index += mapper.attributesPerVertex;
        attributeBuffer.setArray(source, sourceIndex, index + offset, 4);
        index += mapper.attributesPerVertex;
        attributeBuffer.setArray(source, sourceIndex, index + offset, 4);
        index += mapper.attributesPerVertex;
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
     */
    public void setAttribute3(int sprite, int offset, float[] source, int sourceIndex) {
        int index = mapper.attributesPerVertex * 4 * sprite;
        AttributeBuffer attributeBuffer = getVerticeBuffer(BufferIndex.ATTRIBUTES.index);
        attributeBuffer.setArray(source, sourceIndex, index + offset, 3);
        index += mapper.attributesPerVertex;
        attributeBuffer.setArray(source, sourceIndex, index + offset, 3);
        index += mapper.attributesPerVertex;
        attributeBuffer.setArray(source, sourceIndex, index + offset, 3);
        index += mapper.attributesPerVertex;
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
     */
    public void setAttribute2(int sprite, int offset, float[] source, int sourceIndex) {
        int index = mapper.attributesPerVertex * 4 * sprite;
        AttributeBuffer attributeBuffer = getVerticeBuffer(BufferIndex.ATTRIBUTES.index);
        attributeBuffer.setArray(source, sourceIndex, index + offset, 2);
        index += mapper.attributesPerVertex;
        attributeBuffer.setArray(source, sourceIndex, index + offset, 2);
        index += mapper.attributesPerVertex;
        attributeBuffer.setArray(source, sourceIndex, index + offset, 2);
        index += mapper.attributesPerVertex;
        attributeBuffer.setArray(source, sourceIndex, index + offset, 2);
        attributeBuffer.setDirty(true);
    }

}
