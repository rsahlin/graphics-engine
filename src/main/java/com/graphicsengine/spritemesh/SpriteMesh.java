package com.graphicsengine.spritemesh;

import static com.nucleus.geometry.VertexBuffer.QUAD_INDICES;

import com.nucleus.geometry.AttributeUpdater.Consumer;
import com.nucleus.geometry.ElementBuffer;
import com.nucleus.geometry.ElementBuffer.Mode;
import com.nucleus.geometry.ElementBuffer.Type;
import com.nucleus.geometry.ElementBuilder;
import com.nucleus.geometry.Material;
import com.nucleus.geometry.Mesh;
import com.nucleus.geometry.MeshBuilder;
import com.nucleus.geometry.VertexBuffer;
import com.nucleus.shader.ShaderProgram;
import com.nucleus.shader.VariableMapping;
import com.nucleus.texturing.Texture2D;
import com.nucleus.texturing.TextureType;
import com.nucleus.texturing.TiledTexture2D;
import com.nucleus.texturing.UVAtlas;
import com.nucleus.texturing.UVTexture2D;
import com.nucleus.texturing.Untextured;
import com.nucleus.texturing.Untextured.Shading;
import com.nucleus.vecmath.Rectangle;
import com.nucleus.vecmath.Transform;

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
     * Storage for 4 UV components
     */
    private transient float[] frames = new float[2 * 4];

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
     * Creates and builds the Mesh to be rendered, after this call all the quads in this mesh can be rendered
     * by fetching the mesh and rendering it.
     * Note that this class will be set as AttributeUpdater in the mesh in order for the sprites to be displayed
     * properly.
     * 
     * @param program
     * @param texture The texture to use for sprites, must be {@link TiledTexture2D} otherwise tiling will not work.
     * @param material The material for the mesh
     * @param count Number of sprites to support
     * @param Rectangle The rectangle defining the quad for each sprite
     */
    public void createMesh(ShaderProgram program, Texture2D texture, Material material, int count,
            Rectangle rectangle) {
        super.createMesh(program, texture, material);
        createBuffers(program, count);
        buildMesh(program, count, rectangle);
        setAttributeUpdater(this);
    }

    /**
     * Creates the buffers for the specified number of quads/sprites.
     * This method does not build the mesh, that has to be done by calling:
     * {@link #buildQuad(int, ShaderProgram, Rectangle)for each sprite/quad that shall be rendered.
     * or {@link #buildMesh(ShaderProgram, int, float[])}
     * 
     * @param program
     * @param texture
     * @param material
     * @param count
     */
    public void createMesh(ShaderProgram program, Texture2D texture, Material material, int count) {
        super.createMesh(program, texture, material);
        createBuffers(program, count);
        ElementBuilder.buildQuadBuffer(indices, indices.getCount() / QUAD_INDICES, 0);
        setAttributeUpdater(this);
    }

    /**
     * Creates the buffers, vertex and indexbuffers as needed. Attribute and uniform storage.
     * If texture is {@link TiledTexture2D} then vertice and index storage will be createde for 1 sprite.
     * 
     * @param program
     * @param spriteCount
     */
    private void createBuffers(ShaderProgram program, int spriteCount) {
        attributes = program.createAttributeBuffers(this, spriteCount * VertexBuffer.INDEXED_QUAD_VERTICES);
        indices = new ElementBuffer(Mode.TRIANGLES, spriteCount * QUAD_INDICES, Type.SHORT);
        program.setupUniforms(this);
    }

    /**
     * Builds a mesh with data that can be rendered using a tiled sprite renderer, this will draw a number of
     * sprites using one drawcall.
     * With this call all quads will have the same size
     * This call will build the quads using index buffer, texture UV will be set according to the texture reference.
     * Either as tiled or uvatlas
     * Vertex buffer will have storage for XYZ + UV.
     * Note that element data must have been created, and initialized, for spriteCount
     * 
     * @param program The shader program to use with the mesh
     * @param spriteCount Number of sprites to build, this is NOT the vertex count.
     * @param rectangle The rectangle defining each char, all chars will be the same
     */
    protected void buildMesh(ShaderProgram program, int spriteCount, Rectangle rectangle) {
        int vertexStride = program.getVertexStride();
        Texture2D texture = getTexture(Texture2D.TEXTURE_0);
        float[] quadPositions = buildQuadPosBuffer(texture, rectangle, vertexStride);
        MeshBuilder.buildQuadMeshIndexed(this, program, spriteCount, quadPositions);
    }

    float[] buildQuadPosBuffer(Texture2D texture, Rectangle rectangle, int vertexStride) {
        if (texture.textureType == TextureType.TiledTexture2D) {
            return MeshBuilder.createQuadPositionsUVIndexed(rectangle, vertexStride, 0,
                    (TiledTexture2D) texture);
        } else if (texture.textureType == TextureType.Untextured) {
            if (((Untextured) texture).getShading() == Shading.parametric) {
                return MeshBuilder.createQuadPositionsUVIndexed(rectangle, vertexStride, 0,
                        new float[] { -1, 1, 1, 1, 1, -1, -1, -1 });
            }
        }
        return MeshBuilder.createQuadPositionsIndexed(rectangle, vertexStride, 0);
    }

    /**
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
    public void buildQuad(int index, ShaderProgram program, Rectangle rectangle) {
        int vertexStride = program.getVertexStride();
        Texture2D texture = getTexture(Texture2D.TEXTURE_0);
        float[] quadPositions = buildQuadPosBuffer(texture, rectangle, vertexStride);
        MeshBuilder.buildQuads(this, program, 1, index, quadPositions);
    }

    @Override
    public void updateAttributeData() {
        if (attributeData == null) {
            throw new IllegalArgumentException(Consumer.BUFFER_NOT_BOUND);
        }
        VertexBuffer positions = getVerticeBuffer(BufferIndex.ATTRIBUTES);
        positions.setArray(attributeData, 0, 0, attributeData.length);
        positions.setDirty(true);
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
     * Sets attribute data for the specified sprite
     * 
     * @param index Index to the sprite to set attribute
     * @param mapping The variable to set
     * @param attribute
     */
    public void setAttribute4(int index, VariableMapping mapping, float[] attribute) {
        // TODO Precalculate ATTRIBUTES_PER_VERTEX * VERTICES_PER_SPRITE
        int offset = index * mapper.attributesPerVertex * ShaderProgram.VERTICES_PER_SPRITE;
        offset += mapping.getOffset();
        for (int i = 0; i < ShaderProgram.VERTICES_PER_SPRITE; i++) {
            throw new IllegalArgumentException("Not implemented");
            // offset += mapper.ATTRIBUTES_PER_VERTEX;
        }
    }

    /**
     * Sets the x, y and z of a quad/sprite in this mesh.
     * 
     * @param index Index of the quad/sprite to set position of, 0 and up
     * @param x
     * @param y
     * @param z
     */
    public void setPosition(int index, float x, float y, float z) {
        // TODO Precalculate ATTRIBUTES_PER_VERTEX * VERTICES_PER_SPRITE
        int offset = index * mapper.attributesPerVertex * ShaderProgram.VERTICES_PER_SPRITE;
        for (int i = 0; i < ShaderProgram.VERTICES_PER_SPRITE; i++) {
            attributeData[offset + mapper.translateOffset] = x;
            attributeData[offset + mapper.translateOffset + 1] = y;
            attributeData[offset + mapper.translateOffset + 2] = z;
            offset += mapper.attributesPerVertex;
        }
    }

    /**
     * Sets the quad at the specified index to the transform - currently only scale and translate supported.
     * 
     * @param index
     * @param transform
     */
    public void setTransform(int index, Transform transform) {
        // TODO Precalculate ATTRIBUTES_PER_VERTEX * VERTICES_PER_SPRITE
        int offset = index * mapper.attributesPerVertex * ShaderProgram.VERTICES_PER_SPRITE;
        float[] scale = transform.getScale();
        float[] pos = transform.getTranslate();
        for (int i = 0; i < ShaderProgram.VERTICES_PER_SPRITE; i++) {
            attributeData[offset + mapper.scaleOffset] = scale[0];
            attributeData[offset + mapper.scaleOffset + 1] = scale[1];
            attributeData[offset + mapper.scaleOffset + 2] = scale[2];
            attributeData[offset + mapper.translateOffset] = pos[0];
            attributeData[offset + mapper.translateOffset + 1] = pos[1];
            attributeData[offset + mapper.translateOffset + 2] = pos[2];
            offset += mapper.attributesPerVertex;
        }

    }

    /**
     * Sets the x, y and z scale of a quad/sprite in this mesh.
     * 
     * @param index Index of the quad/sprite to set scale for, 0 and up
     * @param x Scale in x axis, where 1 is normal size
     * @param y Scale in y axis, where 1 is normal size
     */
    public void setScale(int index, float x, float y) {
        // TODO Precalculate ATTRIBUTES_PER_VERTEX * VERTICES_PER_SPRITE
        int offset = index * mapper.attributesPerVertex * ShaderProgram.VERTICES_PER_SPRITE;
        for (int i = 0; i < ShaderProgram.VERTICES_PER_SPRITE; i++) {
            attributeData[offset + mapper.scaleOffset] = x;
            attributeData[offset + mapper.scaleOffset + 1] = y;
            offset += mapper.attributesPerVertex;
        }
    }

    /**
     * Sets the frame number for the quad/sprite mesh
     * 
     * @param index The index of the quad/sprite to set frame of, 0 and up
     * @param frame
     */
    public void setFrame(int index, int frame) {
        if (texture[Texture2D.TEXTURE_0].textureType == TextureType.TiledTexture2D) {
            // TODO Precalculate ATTRIBUTES_PER_VERTEX * VERTICES_PER_SPRITE
            int offset = index * mapper.attributesPerVertex * ShaderProgram.VERTICES_PER_SPRITE;
            for (int i = 0; i < ShaderProgram.VERTICES_PER_SPRITE; i++) {
                attributeData[offset + mapper.frameOffset] = frame;
                offset += mapper.attributesPerVertex;
            }
        } else if (texture[Texture2D.TEXTURE_0].textureType == TextureType.UVTexture2D) {
            setFrame(index, frame, ((UVTexture2D) texture[Texture2D.TEXTURE_0]).getUVAtlas());
        }
    }

    /**
     * Sets the ARGB color of the sprite
     * 
     * @param index The index of the sprite to set color to
     * @param rgba Array with at least 4 float values, index 0 is RED, 1 is GREEN, 2 is BLUE, 3 is ALPHA
     * @throws ArrayIndexOutOfBoundsException If program used does not support color parameter or if size of argb array
     * is < 4
     */
    public void setColor(int index, float[] rgba) {
        // TODO Precalculate ATTRIBUTES_PER_VERTEX * VERTICES_PER_SPRITE
        int offset = index * mapper.attributesPerVertex * ShaderProgram.VERTICES_PER_SPRITE;
        for (int i = 0; i < ShaderProgram.VERTICES_PER_SPRITE; i++) {
            attributeData[offset + mapper.colorOffset] = rgba[0];
            attributeData[offset + mapper.colorOffset + 1] = rgba[1];
            attributeData[offset + mapper.colorOffset + 2] = rgba[2];
            attributeData[offset + mapper.colorOffset + 3] = rgba[3];
            offset += mapper.attributesPerVertex;
        }
    }

    /**
     * Sets the frame when the mesh uses a UV texture
     * 
     * @param index The index of the quad/sprite to set frame of, 0 and up
     * @param frame
     * @param uvAtlas
     */
    private void setFrame(int index, int frame, UVAtlas uvAtlas) {
        // TODO Precalculate ATTRIBUTES_PER_VERTEX * VERTICES_PER_SPRITE
        int offset = index * mapper.attributesPerVertex * ShaderProgram.VERTICES_PER_SPRITE;
        int readIndex = 0;
        uvAtlas.getUVFrame(frame, frames, 0);
        for (int i = 0; i < ShaderProgram.VERTICES_PER_SPRITE; i++) {
            attributeData[offset + mapper.frameOffset] = frames[readIndex++];
            attributeData[offset + mapper.frameOffset + 1] = frames[readIndex++];
            offset += mapper.attributesPerVertex;
        }

    }

    /**
     * Sets the z axis rotation, in degrees, of this quad/sprite
     * 
     * @param index The index of the quad/sprite to rotate, 0 and up
     * @param rotation The z axis rotation, in degrees
     */
    public void setRotation(int index, float rotation) {
        int offset = index * mapper.attributesPerVertex * ShaderProgram.VERTICES_PER_SPRITE;
        for (int i = 0; i < ShaderProgram.VERTICES_PER_SPRITE; i++) {
            attributeData[offset + mapper.rotateOffset] = rotation;
            offset += mapper.attributesPerVertex;
        }
    }

}
