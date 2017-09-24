package com.graphicsengine.spritemesh;

import java.io.IOException;

import com.nucleus.SimpleLogger;
import com.nucleus.assets.AssetManager;
import com.nucleus.geometry.AttributeUpdater.Consumer;
import com.nucleus.geometry.Material;
import com.nucleus.geometry.Mesh;
import com.nucleus.geometry.MeshBuilder;
import com.nucleus.geometry.RectangleShapeBuilder;
import com.nucleus.geometry.AttributeBuffer;
import com.nucleus.opengl.GLException;
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
     * Contains attribute data for all sprites - this is a copy of the attribute buffer, since this will double-buffer
     * the attribute buffer there is no need for syncronization while writing into this buffer.
     * This data must be mapped into the mesh attribute buffer for changes to take place.
     */
    protected transient float[] attributeData;

    /**
     * Storage for 4 UV components
     */
    private transient float[] frames = new float[2 * 4];

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
     * @param texture The texture to use for sprites, must be {@link TiledTexture2D} otherwise tiling will not work.
     * @param material The material for the mesh
     * @param count Number of sprites to support
     * @param Rectangle The rectangle defining the quad for each sprite
     */
    @Override
    public void createMesh(Texture2D texture, Material material, int vertexCount, int indiceCount, Mode mode) {
        super.createMesh(texture, material, vertexCount, indiceCount, mode);
        setAttributeUpdater(this);
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

    @Override
    public void updateAttributeData() {
        if (attributeData == null) {
            throw new IllegalArgumentException(Consumer.BUFFER_NOT_BOUND);
        }
        AttributeBuffer positions = getVerticeBuffer(BufferIndex.ATTRIBUTES);
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
    public void destroy(NucleusRenderer renderer) {
        super.destroy(renderer);
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
    public void bindAttributeBuffer(AttributeBuffer buffer) {
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
