package com.graphicsengine.component;

import java.io.IOException;

import com.google.gson.annotations.SerializedName;
import com.graphicsengine.spritemesh.SpriteMesh;
import com.nucleus.component.Component;
import com.nucleus.component.ComponentException;
import com.nucleus.component.ComponentNode;
import com.nucleus.geometry.AttributeBuffer;
import com.nucleus.geometry.AttributeUpdater;
import com.nucleus.geometry.AttributeUpdater.Consumer;
import com.nucleus.geometry.AttributeUpdater.PropertyMapper;
import com.nucleus.geometry.Mesh.BufferIndex;
import com.nucleus.geometry.RectangleShapeBuilder;
import com.nucleus.opengl.GLException;
import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.scene.Node.MeshType;
import com.nucleus.shader.ShaderProgram;
import com.nucleus.shader.ShaderVariables;
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
 * this class can be used as a container for the data.
 * 
 * This component will hold data for the sprite properties (SpriteData), such as position, movement, frame and other
 * logic related fields.
 * Note that this is not the same as the attribute data needed for a Mesh to be rendererd.
 * SpriteData is mapped one to one for each sprite, whereas the attribute data is one -> four for a quad based sprited.
 * The intention is that the locic processing and update to attributes (quad data) can be done using a Compute shader,
 * or OpenCL
 * 
 * The class can be serialized using gson
 * 
 * TODO Shall this class have a reference to {@linkplain SpriteMesh} or just reference the attribute data (as is now)
 * 
 * @author Richard Sahlin
 *
 */
public class SpriteComponent extends Component implements Consumer {

    public static final String COUNT = "count";
    public static final String GRAVITY = "gravity";

    public static final float DEFAULT_GRAVITY = 5;

    /**
     * Data that is held in the sprite component and used to update the attributes in the mesh, this data
     * is copied to each vertice in the quad.
     * 
     * This shall match the shader variables used, normally in {@link ShaderVariables}
     */
    public enum SpriteData {
        TRANSLATE(0),
        TRANSLATE_X(0),
        TRANSLATE_Y(1),
        TRANSLATE_Z(2),
        ROTATE(3),
        SCALE(6),
        // COLOR(9),
        FRAME(9);
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
     * This is the data defined for each sprite, some of these are the same as defined in the
     * {@linkplain AttributeUpdater} and should probably be put together instead of as separate defines.
     * 
     * @author Richard Sahlin
     *
     */
    public enum EntityData {
        MOVE_VECTOR_X(16),
        MOVE_VECTOR_Y(17),
        MOVE_VECTOR_Z(18),
        ELASTICITY(19),
        ROTATE_SPEED(20),
        RESISTANCE(21);
        public final int index;

        EntityData(int index) {
            this.index = index;
        }

        /**
         * Returns the size in floats of the data store for each sprite
         * 
         * @return The size in float for each sprite datastore.
         */
        public static int getSize() {
            EntityData[] values = values();
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
    @SerializedName(COUNT)
    protected int count;

    // TODO this is not a property of the SpriteComponent
    @SerializedName(GRAVITY)
    public float gravity = DEFAULT_GRAVITY;

    /**
     * The sprites common float data storage, this is the sprite visible (mesh) properties such as position, scale and
     * frame, plus entity data needed to process the logic.
     * This is what is generally needed in order to put sprite on screen.
     * In order to render a mesh with sprites this data is copied one -> four in the mesh.
     */
    transient public float[] spriteData;

    /**
     * Data related to a sprite entity - this is used by the System to
     * process behavior and to update the mesh.
     */
    transient public float[] floatData;
    // TODO move into floatdata
    transient public Vector2D[] moveVector;
    /**
     * This is the destination mesh attribute buffer
     */
    protected transient AttributeBuffer attributeBuffer;
    /**
     * TEMP BUFFER
     */
    protected transient float[] attributeData;

    protected transient PropertyMapper mapper;
    /**
     * This is the mesh that holds all sprite objects, it is drawn using one drawcall.
     */
    transient protected SpriteMesh spriteMesh;
    transient protected TextureType textureType;
    transient protected UVAtlas uvAtlas;
    transient protected float[] uvFrame = new float[ShaderProgram.VERTICES_PER_SPRITE * 2];
    transient protected int spritedataSize;

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
        this.gravity = source.gravity;
        if (source.rectangle != null) {
            this.rectangle = new Rectangle(source.rectangle);
        } else {
            rectangle = null;
        }
    }

    /**
     * Internal method
     * Creates the arrays for this spritecomponent
     * 
     * @param system
     */
    private void createBuffers(com.nucleus.system.System system) {
        spritedataSize = EntityData.getSize() + system.getEntityDataSize();
        this.spriteData = new float[spritedataSize * count];
    }

    @Override
    public void create(NucleusRenderer renderer, ComponentNode parent, com.nucleus.system.System system)
            throws ComponentException {
        try {
            SpriteMesh.Builder spriteBuilder = new SpriteMesh.Builder(renderer);
            spriteBuilder.setTexture(parent.getTextureRef());
            spriteBuilder.setMaterial(parent.getMaterial());
            spriteBuilder.setSpriteCount(count);
            RectangleShapeBuilder shapeBuilder = new RectangleShapeBuilder(
                    new RectangleShapeBuilder.RectangleConfiguration(rectangle, RectangleShapeBuilder.DEFAULT_Z, count,
                            0));
            spriteBuilder.setShapeBuilder(shapeBuilder);
            // TODO - Fix generics so that cast is not needed
            spriteMesh = (SpriteMesh) spriteBuilder.create();
            mapper = spriteMesh.getMapper();
            spriteMesh.setAttributeUpdater(this);
            bindAttributeBuffer(spriteMesh.getVerticeBuffer(BufferIndex.ATTRIBUTES.index));
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
        } catch (IOException | GLException e) {
            throw new ComponentException("Could not create component: " + e.getMessage());
        }
        parent.addMesh(spriteMesh, MeshType.MAIN);
        createBuffers(system);
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

    /**
     * Returns the number of frames available in the texture
     * 
     * @return
     */
    public int getFrameCount() {
        return spriteMesh.getTexture(Texture2D.TEXTURE_0).getFrameCount();
    }

    /**
     * Returns the sprite object data, this is the data needed to get the sprite on screen.
     * This does not contain specific logic data
     * 
     * @return
     */
    public float[] getSpriteData() {
        return spriteData;
    }

    /**
     * Reads the move vector for the specified sprite and stores in result.
     * 
     * @param sprite
     * @param result
     * @throws NullPointerException If result is null
     */
    public void getMoveVector(int sprite, Vector2D result) {
        int index = sprite * spritedataSize;
        result.vector[Vector2D.X] = spriteData[index + EntityData.MOVE_VECTOR_X.index];
        result.vector[Vector2D.Y] = spriteData[index + EntityData.MOVE_VECTOR_Y.index];
    }

    /**
     * Returns the propertymapper for the Mesh used by this component.
     * If {@link #create(NucleusRenderer, ComponentNode)} has not been called null is returned.
     * 
     * @return The PropertyMapper for the Mesh in the node used by this component, or null.
     */
    public PropertyMapper getMapper() {
        if (spriteMesh != null) {
            return spriteMesh.getMapper();
        }
        return null;
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
        spriteData[offset++ + SpriteData.TRANSLATE.index] = x;
        spriteData[offset++ + SpriteData.TRANSLATE.index] = y;
        spriteData[offset++ + SpriteData.TRANSLATE.index] = z;
        spriteMesh.setAttribute3(index, mapper.translateOffset, spriteData,
                index * spritedataSize + SpriteData.TRANSLATE.index);
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
        spriteData[offset + SpriteData.FRAME.index] = frame;
        spriteMesh.setAttribute1(index, mapper.frameOffset, spriteData,
                index * spritedataSize + SpriteData.FRAME.index);
    }

    /**
     * Sets attribute data for the specified sprite
     * 
     * @param index Index to the sprite to set attribute
     * @param mapping The variable to set
     * @param attribute
     */
    public void setAttribute4(int index, VariableMapping mapping, float[] attribute) {
        // spriteMesh.setAttribute4(index, mapping, attribute);
    }

    /**
     * Sets the color of the sprite, this calls {@linkplain SpriteMesh#setColor(int, float[])} to update the attribute
     * data
     * 
     * @param index
     * @param rgba Array with at least 4 float values, index 0 is RED, 1 is GREEN, 2 is BLUE, 3 is ALPHA
     */
    public void setColor(int index, float[] rgba) {
        // spriteMesh.setColor(index, rgba);
    }

    public void setRotateSpeed(int index, float speed) {
        int offset = index * spritedataSize;
        spriteData[offset + EntityData.ROTATE_SPEED.index] = speed;
    }

    public void setElasticity(int index, float elasticity) {
        int offset = index * spritedataSize;
        spriteData[offset + EntityData.ELASTICITY.index] = elasticity;
    }

    /**
     * Sets the scale in x and y axis
     * 
     * @param index The sprite number
     * @param x X axis scale
     * @param y Y axis scale
     */
    public void setScale(int index, float x, float y) {
        int offset = index * spritedataSize;
        spriteData[offset++ + SpriteData.SCALE.index] = x;
        spriteData[offset + SpriteData.SCALE.index] = y;
        spriteMesh.setAttribute2(index, mapper.scaleOffset, spriteData,
                index * spritedataSize + SpriteData.SCALE.index);
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
        spriteData[offset + SpriteData.ROTATE.index] = rotation;
        spriteMesh.setAttribute1(index, mapper.rotateOffset + 2, spriteData,
                index * spritedataSize + SpriteData.ROTATE.index);
    }

    public void setSprite(int index, float x, float y, int frame, float rotate) {
        int offset = index * spritedataSize;
        spriteData[offset + SpriteData.TRANSLATE_X.index] = x;
        spriteData[offset + SpriteData.TRANSLATE_Y.index] = y;
        spriteData[offset + SpriteData.FRAME.index] = frame;
        spriteData[offset + EntityData.MOVE_VECTOR_X.index] = 0;
        spriteData[offset + EntityData.MOVE_VECTOR_Y.index] = 0;
        spriteData[offset + EntityData.ELASTICITY.index] = 1f;
        spriteData[offset + EntityData.ROTATE_SPEED.index] = rotate;
    }

    @Override
    public void bindAttributeBuffer(AttributeBuffer buffer) {
        attributeBuffer = spriteMesh.getVerticeBuffer(BufferIndex.ATTRIBUTES.index);
        attributeData = new float[attributeBuffer.getCapacity()];
    }

    @Override
    public void updateAttributeData() {
        if (attributeData == null) {
            throw new IllegalArgumentException(Consumer.BUFFER_NOT_BOUND);
        }
        int sourceIndex = 0;
        for (int i = 0; i < count; i++) {
            spriteMesh.setData(i, 0, spriteData, sourceIndex, SpriteData.getSize());
            sourceIndex += spritedataSize;
        }
    }
}
