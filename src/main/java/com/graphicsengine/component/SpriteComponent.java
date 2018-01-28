package com.graphicsengine.component;

import java.io.IOException;

import com.google.gson.annotations.SerializedName;
import com.graphicsengine.spritemesh.SpriteMesh;
import com.nucleus.SimpleLogger;
import com.nucleus.component.Component;
import com.nucleus.component.ComponentException;
import com.nucleus.component.ComponentNode;
import com.nucleus.geometry.AttributeBuffer;
import com.nucleus.geometry.AttributeUpdater;
import com.nucleus.geometry.AttributeUpdater.Consumer;
import com.nucleus.geometry.AttributeUpdater.PropertyMapper;
import com.nucleus.geometry.Mesh.BufferIndex;
import com.nucleus.geometry.QuadExpander;
import com.nucleus.geometry.RectangleShapeBuilder;
import com.nucleus.opengl.GLException;
import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.scene.Node.MeshType;
import com.nucleus.shader.ShaderProgram;
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
     * TODO Use java.nio.FloatBuffer instead and perhaps move into a special class to handle 1 -> 4 mapping
     */
    transient protected QuadExpander spriteExpander;
    transient protected float[] spriteData;

    // TODO move into floatdata
    transient public Vector2D[] moveVector;
    /**
     * This is the destination mesh attribute buffer
     */
    protected transient AttributeBuffer attributeBuffer;

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
        spriteData = new float[spritedataSize * count];
        spriteExpander = new QuadExpander(spriteMesh.getTexture(Texture2D.TEXTURE_0), mapper, spriteData,
                spritedataSize, count, 4);
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
        } catch (IOException | GLException e) {
            throw new ComponentException("Could not create component: " + e.getMessage());
        }
        mapper = spriteMesh.getMapper();
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
        parent.addMesh(spriteMesh, MeshType.MAIN);
        createBuffers(system);
        spriteMesh.setAttributeUpdater(this);
        bindAttributeBuffer(spriteMesh.getVerticeBuffer(BufferIndex.ATTRIBUTES.index));
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
     * Do not use this method
     * Returns the sprite object data, this is the data needed to get the sprite on screen.
     * This does not contain specific logic data
     * 
     * @return
     * @deprecated
     */
    @Deprecated
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
     * Sets the color of the sprite
     * 
     * @param index
     * @param rgba Array with at least 4 float values, index 0 is RED, 1 is GREEN, 2 is BLUE, 3 is ALPHA
     */
    public void setColor(int index, float[] rgba) {
        SimpleLogger.d(getClass(), "Not implemented!!!!!!!!!");
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
     * 
     * @param index
     * @param x
     * @param y
     * @param z
     * @param scaleX
     * @param scaleY
     * @param scaleZ
     * @param rotate
     * @param frame
     */
    public void setSprite(int index, float x, float y, float z, float scaleX, float scaleY, float scaleZ, float rotate,
            int frame) {
        spriteExpander.setData(index, x, y, z, rotate, scaleX, scaleY, scaleZ, frame);
    }

    @Override
    public void bindAttributeBuffer(AttributeBuffer buffer) {
        attributeBuffer = buffer;
        spriteExpander.bindAttributeBuffer(buffer);
    }

    @Override
    public void updateAttributeData() {
        spriteExpander.updateAttributeData();
    }
}
