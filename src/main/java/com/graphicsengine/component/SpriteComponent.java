package com.graphicsengine.component;

import java.io.IOException;

import com.google.gson.annotations.SerializedName;
import com.graphicsengine.spritemesh.SpriteMesh;
import com.nucleus.component.Component;
import com.nucleus.component.ComponentException;
import com.nucleus.component.ComponentNode;
import com.nucleus.geometry.AttributeUpdater;
import com.nucleus.geometry.AttributeUpdater.Consumer;
import com.nucleus.geometry.AttributeUpdater.PropertyMapper;
import com.nucleus.geometry.VertexBuffer;
import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.scene.Node.MeshType;
import com.nucleus.shader.ShaderProgram;
import com.nucleus.shader.VariableMapping;
import com.nucleus.texturing.Texture2D;
import com.nucleus.texturing.TextureType;
import com.nucleus.texturing.UVAtlas;
import com.nucleus.texturing.UVTexture2D;
import com.nucleus.vecmath.Rectangle;
import com.nucleus.vecmath.Vector2D;

/**
 * The old school sprite component, this is a collection of a number of (similar) sprite objects
 * that have the data in a shared buffer.
 * The component can be seen as a container for the data needed to process the sprites - but not the behavior itself.
 * This class is used by implementations of {@link System} to process behavior, the System is where the logic is and
 * this class can be
 * used as a container for the data.
 * 
 * This component will hold data for the sprite properties, such as position, movement, frame - this data is held in the
 * attribute buffer that can be fetched using {@link #getAttributeData()} and must match the data used by the shader
 * program.
 * 
 * The class can be serialized using gson
 * 
 * TODO Shall this class have a reference to {@linkplain SpriteMesh} or just reference the attribute data (as is now)
 * 
 * @author Richard Sahlin
 *
 */
public class SpriteComponent extends Component implements Consumer {

    /**
     * This is the data defined for each sprite, some of these are the same as defined in the
     * {@linkplain AttributeUpdater} and should probably be put together instead of as separate defines.
     * 
     * @author Richard Sahlin
     *
     */
    public enum SpriteData {
        TRANSLATE(0),
        TRANSLATE_X(0),
        TRANSLATE_Y(1),
        ROTATE(3),
        SCALE(6),
        FRAME(9),
        MOVE_VECTOR_X(10),
        MOVE_VECTOR_Y(11),
        MOVE_VECTOR_Z(12),
        ELASTICITY(13),
        ROTATE_SPEED(14);
        public final int index;

        SpriteData(int index) {
            this.index = index;
        }

        /**
         * Returns the size in floats of the data store for each sprite
         * 
         * @return The size in float for each sprite datastore.
         */
        public static int getSize() {
            SpriteData[] values = values();
            return values[values.length - 1].index + 1;
        }
    }

    /**
     * The rectangle defining the sprites, all sprites will have same size
     * 4 values = x1,y1 + width and height
     */
    @SerializedName(Rectangle.RECT)
    private Rectangle rectangle;
    /**
     * Number of sprites
     */
    @SerializedName("count")
    protected int count;
    /**
     * The sprites float data storage, this is the sprite properties such as position, movement and frame
     */
    transient public float[] floatData;
    /**
     * This is a reference to the spritemesh attribute data.
     */
    transient private float[] attributeData;
    // TODO move into floatdata
    transient public Vector2D[] moveVector;
    // TODO move to renderable component
    transient protected PropertyMapper mapper;
    /**
     * This is the mesh that holds all sprite objects, it is drawn using one drawcall.
     */
    transient protected SpriteMesh spriteMesh;
    transient protected TextureType textureType;
    transient protected UVAtlas uvAtlas;
    transient protected float[] uvFrame = new float[ShaderProgram.VERTICES_PER_SPRITE * 2];
    transient protected int spritedataSize = SpriteData.getSize();

    @Override
    public Component createInstance() {
        return new SpriteComponent();
    }

    @Override
    public void set(Component source) {
        set((SpriteComponent) source);
    }

    private void set(SpriteComponent source) {
        super.set(source);
        this.count = source.count;
        if (source.rectangle != null) {
            this.rectangle = new Rectangle(source.rectangle);
        } else {
            rectangle = null;
        }
    }

    /**
     * Internal method
     * Creates the arrays for this spritecomponent
     */
    private void createBuffers() {
        this.floatData = new float[spritedataSize * count];
        this.moveVector = new Vector2D[count];
        for (int i = 0; i < count; i++) {
            moveVector[i] = new Vector2D();
        }
    }

    @Override
    public void create(NucleusRenderer renderer, ComponentNode parent)
            throws ComponentException {
        try {
            SpriteMesh.Builder spriteBuilder = new SpriteMesh.Builder(renderer);
            spriteMesh = spriteBuilder.setTextureRef(parent.getTextureRef()).setMaterial(parent.getMaterial())
                    .setCount(count).setRectangle(rectangle).create();
            this.textureType = spriteMesh.getTexture(Texture2D.TEXTURE_0).getTextureType();
            switch (textureType) {
            case TiledTexture2D:
                break;
            case UVTexture2D:
                uvAtlas = ((UVTexture2D) spriteMesh.getTexture(Texture2D.TEXTURE_0)).getUVAtlas();
                break;
            default:
                break;
            }
        } catch (IOException e) {
            throw new ComponentException("Could not create component: " + e.getMessage());
        }
        mapper = spriteMesh.getMapper();
        attributeData = spriteMesh.getAttributeData();
        parent.addMesh(spriteMesh, MeshType.MAIN);
        createBuffers();
    }

    /**
     * Returns the rectangle defining the sprites
     * 
     * @return Rectangle defining sprite, X1, Y1, width, height.
     */
    public Rectangle getSpriteRectangle() {
        return rectangle;
    }

    /**
     * Returns the texture type used for this component.
     * TODO: Shall this be stored as a Component enum instead?
     * 
     * @return Type of texture used
     */
    public TextureType getTextureType() {
        return textureType;
    }

    /**
     * Internal method, sets the rectangle defining each sprite
     * This will only set the size parameter, createMesh must be called to actually create the mesh
     * 
     * param rectangle values defining sprite, X1, Y1, width, height.
     */
    private void setSpriteRectangle(Rectangle rectangle) {
        this.rectangle = new Rectangle(rectangle);
    }

    /**
     * Returns the number of sprites in this component
     * 
     * @return
     */
    public int getCount() {
        return count;
    }

    @Override
    public float[] getAttributeData() {
        return attributeData;
    }

    /**
     * Returns the sprite object data
     * 
     * @return
     */
    public float[] getSpriteData() {
        return floatData;
    }

    /**
     * This method shall not be used - the movement vector shall be put with the other data
     * All data shall be in one buffer or array
     * Returns the movement vectors owned by this component
     * 
     * @return
     */
    @Deprecated
    public Vector2D[] getMoveVector() {
        return moveVector;
    }

    public PropertyMapper getMapper() {
        return mapper;
    }

    /**
     * Sets the x, y and z position, this calls {@linkplain SpriteMesh#setPosition(int, float, float, float)} to update
     * the attribute data
     * 
     * @param index The sprite number
     * @param x X position
     * @param y Y position
     * @param z Z position
     */
    public void setPosition(int index, float x, float y, float z) {
        int offset = index * spritedataSize;
        floatData[offset++ + SpriteData.TRANSLATE.index] = x;
        floatData[offset++ + SpriteData.TRANSLATE.index] = y;
        floatData[offset++ + SpriteData.TRANSLATE.index] = z;
        spriteMesh.setPosition(index, x, y, z);
    }

    /**
     * Sets the frame number of the sprite index, this calls {@linkplain SpriteMesh#setFrame(int, int)} to update
     * the attribute data
     * 
     * @param index Index to the sprite object to set the frame on
     * @param frame Sprite frame number to set
     */
    public void setFrame(int index, int frame) {
        int offset = index * spritedataSize;
        floatData[offset + SpriteData.FRAME.index] = frame;
        spriteMesh.setFrame(index, frame);
    }

    /**
     * Sets attribute data for the specified sprite
     * 
     * @param index Index to the sprite to set attribute
     * @param mapping The variable to set
     * @param attribute
     */
    public void setAttribute4(int index, VariableMapping mapping, float[] attribute) {
        spriteMesh.setAttribute4(index, mapping, attribute);
    }

    /**
     * Sets the color of the sprite, this calls {@linkplain SpriteMesh#setColor(int, float[])} to update the attribute
     * data
     * 
     * @param index
     * @param rgba Array with at least 4 float values, index 0 is RED, 1 is GREEN, 2 is BLUE, 3 is ALPHA
     */
    public void setColor(int index, float[] rgba) {
        spriteMesh.setColor(index, rgba);
    }

    public void setRotateSpeed(int index, float speed) {
        int offset = index * spritedataSize;
        floatData[offset + SpriteData.ROTATE_SPEED.index] = speed;
    }

    public void setElasticity(int index, float elasticity) {
        int offset = index * spritedataSize;
        floatData[offset + SpriteData.ELASTICITY.index] = elasticity;
    }

    /**
     * Sets the scale in x and y axis, this calls {@linkplain SpriteMesh#setScale(int, float, float)} to update
     * the attribute data
     * 
     * @param index The sprite number
     * @param x X axis scale
     * @param y Y axis scale
     */
    public void setScale(int index, float x, float y) {
        int offset = index * spritedataSize;
        floatData[offset++ + SpriteData.SCALE.index] = x;
        floatData[offset + SpriteData.SCALE.index] = y;
        spriteMesh.setScale(index, x, y);
    }

    /**
     * Sets the rotation, this calls {@linkplain SpriteMesh#setRotation(int, float)} to update
     * the attribute data
     * 
     * @param index The sprite number
     * @param rotation
     */
    public void setRotation(int index, float rotation) {
        int offset = index * spritedataSize;
        floatData[offset + SpriteData.ROTATE.index] = rotation;
        spriteMesh.setRotation(index, rotation);
    }

    public void setSprite(int index, float x, float y, int frame, float rotate) {
        int offset = index * spritedataSize;
        floatData[offset + SpriteData.TRANSLATE_X.index] = x;
        floatData[offset + SpriteData.TRANSLATE_Y.index] = y;
        floatData[offset + SpriteData.MOVE_VECTOR_X.index] = 0;
        floatData[offset + SpriteData.MOVE_VECTOR_Y.index] = 0;
        floatData[offset + SpriteData.ELASTICITY.index] = 1f;
        floatData[offset + SpriteData.FRAME.index] = frame;
        floatData[offset + SpriteData.ROTATE_SPEED.index] = rotate;
    }

    @Override
    public void updateAttributeData() {
        // TODO Auto-generated method stub
    }

    @Override
    public void bindAttributeBuffer(VertexBuffer buffer) {
    }
}
