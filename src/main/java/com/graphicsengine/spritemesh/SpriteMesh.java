package com.graphicsengine.spritemesh;

import java.io.IOException;

import com.graphicsengine.component.SpriteAttributeComponent;
import com.nucleus.assets.AssetManager;
import com.nucleus.geometry.AttributeBuffer;
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
     * Builder for sprite meshes
     *
     */
    public static class Builder extends Mesh.Builder<SpriteMesh> {

        protected final static String INVALID_TYPE = "Invalid type: ";

        protected int spriteCount;

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
            switch (renderer.getGLES().getInfo().getRenderVersion()) {
                case GLES20:
                case GLES30:
                case GLES31:
                case GLES32:
                    return new Builder(renderer);
                default:
                    throw new IllegalArgumentException(
                            "Not implemented for " + renderer.getGLES().getInfo().getRenderVersion());
            }
        }

        /**
         * Sets the number of sprites (quads) that the mesh shall support
         * 
         * @param spriteCount Number of sprites (quads) to support
         * @return
         */
        public Builder setSpriteCount(int spriteCount) {
            this.spriteCount = spriteCount;
            return this;
        }

        @Override
        public Mesh create() throws IOException, GLException {
            // Set before validating otherwise vertexcount is wrong, but don't set in setSpriteCount() method
            // since we will chose type of mesh depending on GL
            setElementMode(Mode.TRIANGLES, spriteCount * RectangleShapeBuilder.QUAD_VERTICES,
                    spriteCount * RectangleShapeBuilder.QUAD_ELEMENTS);
            validate();
            if (material.getProgram() == null) {
                ShaderProgram program = createProgram(texture);
                program = AssetManager.getInstance().getProgram(renderer.getGLES(), program);
                material.setProgram(program);
            }
            return super.create();
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
                case Texture2D:
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
     */
    public void setAttribute4(int sprite, int offset, float[] source, int sourceIndex) {
        int index = mapper.attributesPerVertex * 4 * sprite;
        AttributeBuffer attributeBuffer = getAttributeBuffer(BufferIndex.ATTRIBUTES.index);
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
        AttributeBuffer attributeBuffer = getAttributeBuffer(BufferIndex.ATTRIBUTES.index);
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
        AttributeBuffer attributeBuffer = getAttributeBuffer(BufferIndex.ATTRIBUTES.index);
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
