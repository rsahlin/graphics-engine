package com.graphicsengine.spritemesh;

import static com.nucleus.geometry.VertexBuffer.QUAD_INDICES;

import java.io.IOException;

import com.graphicsengine.scene.QuadParentNode;
import com.nucleus.assets.AssetManager;
import com.nucleus.geometry.AttributeUpdater.Consumer;
import com.nucleus.geometry.ElementBuilder;
import com.nucleus.geometry.Material;
import com.nucleus.geometry.Mesh;
import com.nucleus.geometry.MeshBuilder;
import com.nucleus.geometry.VertexBuffer;
import com.nucleus.io.ExternalReference;
import com.nucleus.renderer.BufferObjectsFactory;
import com.nucleus.renderer.Configuration;
import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.shader.ShaderProgram;
import com.nucleus.shader.ShaderVariable;
import com.nucleus.shader.VariableMapping;
import com.nucleus.texturing.Texture2D;
import com.nucleus.texturing.TextureType;
import com.nucleus.texturing.TiledTexture2D;
import com.nucleus.texturing.UVAtlas;
import com.nucleus.texturing.UVTexture2D;
import com.nucleus.texturing.Untextured;
import com.nucleus.vecmath.AxisAngle;
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

    public static class Builder {

        private final static String INVALID_TYPE = "Invalid type: ";

        private NucleusRenderer renderer;
        private ExternalReference textureRef;
        private Material material;
        private int count;
        private Rectangle spriteRect;
        /**
         * Creates a new SpriteMesh builder
         * 
         * @param renderer
         * @throws IllegalArgumentException If renderer is null
         */
        public Builder(NucleusRenderer renderer) {
            if (renderer == null) {
                throw new IllegalArgumentException("Renderer may not be null");
            }
            this.renderer = renderer;
        }

        private void validate() {
            if (textureRef == null || material == null || count <= 0 || spriteRect == null) {
                throw new IllegalArgumentException("Missing arguments to create mesh:" + textureRef + ", " + material
                        + ", " + count + ", " + spriteRect);
            }
        }

        /**
         * Sets the texture reference
         * 
         * @param textureRef
         * @return
         */
        public Builder setTextureRef(ExternalReference textureRef) {
            this.textureRef = textureRef;
            return this;
        }

        /**
         * Sets the material for the mesh
         * 
         * @param material
         * @return
         */
        public Builder setMaterial(Material material) {
            this.material = material;
            return this;
        }

        /**
         * Sets the number of sprites (quads) that the mesh shall support
         * 
         * @param spriteCount Number of sprites (quads) to support
         * @return
         */
        public Builder setCount(int spriteCount) {
            this.count = spriteCount;
            return this;
        }

        /**
         * Sets the rectangle defining each of the sprites
         * 
         * @param rectangle
         * @return
         */
        public Builder setRectangle(Rectangle rectangle) {
            this.spriteRect = rectangle;
            return this;
        }

        /**
         * This will create an old school sprite mesh, where each sprite has a frame, the sprite can be rotated in x
         * axis
         * and positioned in x and y.
         * 
         * @return The created mesh using the parameters set in this builder
         * @throws IllegalArgumentException If the needed arguments has not been set.
         */
        public SpriteMesh create() throws IOException {
            validate();
            Texture2D texture = AssetManager.getInstance().getTexture(renderer, textureRef);
            ShaderProgram program = createProgram(texture);
            SpriteMesh mesh = new SpriteMesh();
            renderer.createProgram(program);
            mesh.createMesh(program, texture, material, count, spriteRect);
            if (Configuration.getInstance().isUseVBO()) {
                BufferObjectsFactory.getInstance().createVBOs(renderer, mesh);
            }
            return mesh;
        }

        /**
         * This will create an old school sprite mesh, where each sprite has a frame, the sprite can be rotated in x
         * axis and positioned in x and y.
         * Arguments are taken from the parent node.
         * 
         * @param parent The parent node where arguments are read from
         * @return The created sprite mesh
         */
        public SpriteMesh create(QuadParentNode parent) throws IOException {
            Texture2D texture = AssetManager.getInstance().getTexture(renderer, parent.getTextureRef());
            ShaderProgram program = createProgram(texture);
            SpriteMesh mesh = new SpriteMesh();
            renderer.createProgram(program);
            mesh.createMesh(program, texture, parent.getMaterial(), parent.getMaxQuads());
            if (Configuration.getInstance().isUseVBO()) {
                BufferObjectsFactory.getInstance().createVBOs(renderer, mesh);
            }
            return mesh;
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
                return new TiledSpriteProgram();
            case UVTexture2D:
                return new UVSpriteProgram();
            case Untextured:
                return new UntexturedSpriteProgram(((Untextured) texture).getShading());
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
        super.createMesh(program, texture, material, count * VertexBuffer.INDEXED_QUAD_VERTICES, count * QUAD_INDICES);
        setMode(Mode.TRIANGLES);
        buildMesh(program, count, rectangle, 0);
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
        super.createMesh(program, texture, material, count * VertexBuffer.INDEXED_QUAD_VERTICES, count * QUAD_INDICES);
        setMode(Mode.TRIANGLES);
        ElementBuilder.buildQuadBuffer(indices, indices.getCount() / QUAD_INDICES, 0);
        setAttributeUpdater(this);
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
     * @param z
     */
    protected void buildMesh(ShaderProgram program, int spriteCount, Rectangle rectangle, float z) {
        int vertexStride = program.getVertexStride();
        Texture2D texture = getTexture(Texture2D.TEXTURE_0);
        float[] quadPositions = texture.createQuadArray(rectangle, vertexStride, z);
        MeshBuilder.buildQuadMeshIndexed(this, program, spriteCount, quadPositions);
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
        float[] quadPositions = texture.createQuadArray(rectangle, vertexStride, 0);
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
     * @param attribute The data to set, must contain at least 4 values
     */
    public void setAttribute4(int index, VariableMapping mapping, float[] attribute) {
        ShaderVariable variable = getMaterial().getProgram().getShaderVariable(mapping);
        setAttribute4(index, variable, attribute);
    }

    /**
     * Sets attribute data for the specified sprite
     * 
     * @param index Index to the sprite to set attribute
     * @param variable The variable to set
     * @param attribute The data to set, must contain at least 4 values
     */
    public void setAttribute4(int index, ShaderVariable variable, float[] attribute) {
        // TODO Precalculate ATTRIBUTES_PER_VERTEX * VERTICES_PER_SPRITE
        int offset = index * mapper.attributesPerVertex * ShaderProgram.VERTICES_PER_SPRITE;
        offset += variable.getOffset();
        for (int i = 0; i < ShaderProgram.VERTICES_PER_SPRITE; i++) {
            attributeData[offset++] = attribute[0];
            attributeData[offset++] = attribute[1];
            attributeData[offset++] = attribute[2];
            attributeData[offset++] = attribute[3];
            offset += mapper.attributesPerVertex - 4;
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
        float[] axisAngle = null;
        float angle = 0;
        if (transform.getAxisAngle() != null) {
            axisAngle = transform.getAxisAngle().getValues();
            angle = axisAngle[AxisAngle.ANGLE];
        }
        for (int i = 0; i < ShaderProgram.VERTICES_PER_SPRITE; i++) {
            attributeData[offset + mapper.scaleOffset] = scale[0];
            attributeData[offset + mapper.scaleOffset + 1] = scale[1];
            attributeData[offset + mapper.scaleOffset + 2] = scale[2];
            attributeData[offset + mapper.translateOffset] = pos[0];
            attributeData[offset + mapper.translateOffset + 1] = pos[1];
            attributeData[offset + mapper.translateOffset + 2] = pos[2];
            if (axisAngle != null) {
                attributeData[offset + mapper.rotateOffset] = axisAngle[AxisAngle.X] * angle;
                attributeData[offset + mapper.rotateOffset + 1] = axisAngle[AxisAngle.Y] * angle;
                attributeData[offset + mapper.rotateOffset + 2] = axisAngle[AxisAngle.Z] * angle;
            }

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
