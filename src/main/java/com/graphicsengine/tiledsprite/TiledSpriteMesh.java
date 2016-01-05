package com.graphicsengine.tiledsprite;

import com.google.gson.annotations.SerializedName;
import com.nucleus.geometry.AttributeUpdater;
import com.nucleus.geometry.Mesh;
import com.nucleus.geometry.VertexBuffer;
import com.nucleus.texturing.Texture2D;
import com.nucleus.texturing.TiledTexture2D;
import com.nucleus.vecmath.Axis;
import com.nucleus.vecmath.Transform;

/**
 * A number of sprites that will be rendered using the same Mesh, ie all sprites in this class are rendered using one
 * draw call.
 * This class only contains the drawable parts of the sprites - no logic is contained in this class.
 * TODO: Create a TiledMesh class that is used by both this class and PlayfieldMesh
 * 
 * @author Richard Sahlin
 *
 */
public class TiledSpriteMesh extends Mesh implements AttributeUpdater {

    @SerializedName("count")
    int count;
    /**
     * Width and height of each sprite.
     */
    @SerializedName("size")
    private float[] size = new float[2];
    /**
     * translate for each sprite.
     */
    @SerializedName("transform")
    private Transform transform;
    /**
     * Reference to tiled texture
     */
    @SerializedName("textureref")
    private String textureRef;

    /**
     * Contains attribute data for all sprites.
     * This data must be mapped into the mesh for changes to take place.
     */
    transient float[] attributeData;

    /**
     * Creates a new sprite sheet using one mesh, the mesh must be created before being used.
     * 
     * @param spriteCount
     */
    public TiledSpriteMesh(int spriteCount) {
        setup(spriteCount);
    }

    /**
     * Creates a new instance of the tiled sprite mesh based on the source.
     * This will NOT create the mesh and sprites it will only set the values from the source.
     * {@link #createMesh(TiledSpriteProgram, Texture2D, float[], float[])}
     * 
     * @param source
     */
    public TiledSpriteMesh(TiledSpriteMesh source) {
        super();
        set(source);
    }

    /**
     * Sets the values from the source mesh, this will only set copy values it will not create the mesh.
     * 
     * @param source
     */
    public void set(TiledSpriteMesh source) {
        setId(source.getId());
        this.textureRef = source.textureRef;
        if (source.transform != null) {
            transform = new Transform(source.transform);
        }
        setSize(source.getSize());
        setup(source.getCount());

    }

    private void setup(int spriteCount) {
        count = spriteCount;
        attributeData = new float[count * TiledSpriteProgram.ATTRIBUTES_PER_SPRITE];
    }

    /**
     * Internal method, sets the size of each char.
     * This will only set the size parameter, createMesh must be called to actually create the mesh
     * 
     * @param size The size to set, or null to not set any values.
     */
    private void setSize(float[] size) {
        if (size != null) {
            this.size[Axis.WIDTH.index] = size[Axis.WIDTH.index];
            this.size[Axis.HEIGHT.index] = size[Axis.HEIGHT.index];
        }
    }

    /**
     * Creates the Mesh to be rendered, after this call the all the sprites in this controller can be rendered
     * by fetching the mesh and rendering it.
     * Note that this class will be set as AttributeUpdater in the mesh in order for the sprites to be displayed
     * properly.
     * 
     * @param program
     * @param texture The texture to use for sprites, must be {@link TiledTexture2D} otherwise tiling will not work.
     * @param width Width for each sprite
     * @param height Height for each sprite
     * @param z Zpos
     * @return
     */
    public void createMesh(TiledSpriteProgram program, Texture2D texture, float width, float height, float z) {
        size[0] = width;
        size[1] = height;
        setTranslate(-width / 2, -height / 2, z);
        program.buildMesh(this, texture, count, size, transform.getTranslate());
        setTexture(texture, Texture2D.TEXTURE_0);
        setAttributeUpdater(this);
    }

    public void setTranslate(float[] translate) {
        if (transform == null) {
            transform = new Transform();
        }
        transform.setTranslate(translate);

    }

    public void setTranslate(float x, float y, float z) {
        if (transform == null) {
            transform = new Transform();
        }
        transform.setTranslate(new float[] { x, y, z });
    }

    public void createMesh(TiledSpriteProgram program, Texture2D texture, float[] dimension, float[] translate) {
        program.buildMesh(this, texture, count, dimension, translate);

        size[0] = dimension[Axis.X.index];
        size[1] = dimension[Axis.Y.index];
        setTranslate(translate);
        setTexture(texture, Texture2D.TEXTURE_0);
        setAttributeUpdater(this);

    }

    /**
     * Returns the number of sprites for this tiled spritesheet, this is the max number of sprites (quads) that
     * can be displayed.
     * 
     * @return Number of sprites in the spritesheet.
     */
    public int getCount() {
        return count;
    }

    /**
     * Returns the transform or null if none has been set
     * 
     * @return
     */
    public Transform getTransform() {
        return transform;
    }

    @Override
    public void setAttributeData() {
        // TODO Index the numbers for vertice/attribute data instead of using 0/1
        VertexBuffer positions = getVerticeBuffer(BufferIndex.ATTRIBUTES);
        positions.setArray(getAttributeData(), 0, 0, count * TiledSpriteProgram.ATTRIBUTES_PER_SPRITE);
    }

    @Override
    public float[] getAttributeData() {
        return attributeData;
    }

    @Override
    public void destroy() {
        attributeData = null;
    }

    /**
     * Returns the dimension of the sprites, in x and y
     * 
     * @return Width and height of sprite, at index 0 and 1 respectively.
     */
    public float[] getSize() {
        return size;
    }

    /**
     * Returns the translate for the sprites.
     * 
     * @return translate for sprites, in x, y and z, or null if mesh is not created
     */
    public float[] getTranslate() {
        if (transform == null) {
            return null;
        }
        return transform.getTranslate();
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
     * Returns the texture reference or null if not set
     * 
     * @return
     */
    public String getTextureRef() {
        return textureRef;
    }

}
