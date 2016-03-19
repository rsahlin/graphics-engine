package com.graphicsengine.spritemesh;

import com.nucleus.data.Anchor;
import com.nucleus.geometry.AttributeUpdater.Consumer;
import com.nucleus.geometry.Mesh;
import com.nucleus.geometry.MeshBuilder;
import com.nucleus.geometry.VertexBuffer;
import com.nucleus.shader.ShaderProgram;
import com.nucleus.texturing.Texture2D;
import com.nucleus.texturing.TiledTexture2D;

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
public class SpriteMesh extends Mesh implements Consumer {

    /**
     * Contains attribute data for all sprites - this is the array that sprites will write into.
     * This data must be mapped into the mesh for changes to take place.
     */
    protected transient float[] attributeData;

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
     * Creates the Mesh to be rendered, after this call the all the sprites in this controller can be rendered
     * by fetching the mesh and rendering it.
     * Note that this class will be set as AttributeUpdater in the mesh in order for the sprites to be displayed
     * properly.
     * 
     * @param program
     * @param texture The texture to use for sprites, must be {@link TiledTexture2D} otherwise tiling will not work.
     * @param count Number of sprites to support
     * @param size Width and height of quads, all quads will have same size.
     * @param anchor Anchor for quads.
     */
    public void createMesh(ShaderProgram program, Texture2D texture, int count, float[] size, Anchor anchor) {
        super.createMesh(program, texture);
        buildMesh(program, count, size, anchor);
        setAttributeUpdater(this);
    }

    /**
     * Builds a mesh with data that can be rendered using a tiled sprite renderer, this will draw a number of
     * sprites using one drawcall.
     * Vertex buffer will have storage for XYZ + UV.
     * 
     * @param program The shader program to use with the mesh
     * @param spriteCount Number of sprites to build, this is NOT the vertex count.
     * @param size Width and height of each sprite
     * @param anchor Anchor values for sprites
     */
    private void buildMesh(ShaderProgram program, int spriteCount, float[] size, Anchor anchor) {
        int vertexStride = program.getVertexStride();
        float[] quadPositions = MeshBuilder.buildQuadPositionsIndexed(size, anchor, vertexStride);
        MeshBuilder.buildQuadMeshIndexed(this, program, spriteCount, quadPositions);
    }

    @Override
    public void setAttributeData() {
        if (attributeData == null) {
            throw new IllegalArgumentException(Consumer.BUFFER_NOT_BOUND);
        }
        VertexBuffer positions = getVerticeBuffer(BufferIndex.ATTRIBUTES);
        positions.setArray(attributeData, 0, 0, attributeData.length);
    }

    @Override
    public float[] getAttributeData() {
        if (attributeData == null) {
            throw new IllegalArgumentException(Consumer.BUFFER_NOT_BOUND);
        }
        return attributeData;
    }

    @Override
    public void destroy() {
        super.destroy();
        attributeData = null;
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

    @Override
    public void bindAttributeBuffer(VertexBuffer buffer) {
        attributeData = new float[buffer.getBuffer().capacity()];
    }

    /**
     * Sets the x, y position and frame of this element
     * 
     * @param x
     * @param y
     * @param z
     */
    public void setPosition(float x, float y, float z) {
        int index = 0;
        for (int i = 0; i < ShaderProgram.VERTICES_PER_SPRITE; i++) {
            attributeData[index + mapper.TRANSLATE_INDEX] = x;
            attributeData[index + mapper.TRANSLATE_INDEX + 1] = y;
            attributeData[index + mapper.TRANSLATE_INDEX + 2] = z;
            index += mapper.ATTRIBUTES_PER_VERTEX;
        }
    }

    public void setScale(float x, float y, float z) {
        int index = 0;
        for (int i = 0; i < ShaderProgram.VERTICES_PER_SPRITE; i++) {
            attributeData[index + mapper.SCALE_INDEX] = x;
            attributeData[index + mapper.SCALE_INDEX + 1] = y;
            attributeData[index + mapper.SCALE_INDEX + 2] = z;
            index += mapper.ATTRIBUTES_PER_VERTEX;
        }

    }

}
