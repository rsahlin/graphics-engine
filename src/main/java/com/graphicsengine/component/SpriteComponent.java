package com.graphicsengine.component;

import java.io.IOException;

import com.google.gson.annotations.SerializedName;
import com.graphicsengine.spritemesh.SpriteMesh;
import com.nucleus.SimpleLogger;
import com.nucleus.component.CPUComponentBuffer;
import com.nucleus.component.CPUQuadExpander;
import com.nucleus.component.Component;
import com.nucleus.component.ComponentBuffer;
import com.nucleus.component.ComponentException;
import com.nucleus.geometry.AttributeBuffer;
import com.nucleus.geometry.AttributeUpdater.Consumer;
import com.nucleus.geometry.AttributeUpdater.PropertyMapper;
import com.nucleus.geometry.Material;
import com.nucleus.geometry.Mesh.BufferIndex;
import com.nucleus.geometry.RectangleShapeBuilder;
import com.nucleus.geometry.RectangleShapeBuilder.RectangleConfiguration;
import com.nucleus.opengl.GLException;
import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.scene.ComponentNode;
import com.nucleus.scene.Node.MeshIndex;
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
 * this class can be used as a container for the (visible) data.
 * 
 * This component will hold data for the sprite properties (SpriteData), such as position, rotation, scale, frame - the
 * visible properties.
 * Note that this is not the same as the attribute data needed for a Mesh to be rendererd.
 * SpriteData is mapped one to one for each sprite, whereas the attribute data is one -> four for a quad based sprite.
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

    /**
     * The sprites common float data storage, this is the sprite visible (mesh) properties such as position, scale and
     * frame, plus entity data needed to process the logic.
     * This is what is generally needed in order to put sprite on screen.
     * In order to render a mesh with sprites this data is copied one -> four in the mesh.
     * TODO Use java.nio.FloatBuffer instead and perhaps move into a special class to handle 1 -> 4 mapping
     */
    transient protected CPUQuadExpander spriteExpander;

    // TODO move into floatdata
    transient public Vector2D[] moveVector;

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

    public CPUQuadExpander getQuadExpander() {
        return spriteExpander;
    }

    /**
     * Returns the buffer that holds the data for the sprite (mesh)
     * This is the position, rotation, scale data copied to mesh when {@link #updateAttributeData(NucleusRenderer)} is
     * called.
     * 
     * @return
     */
    public ComponentBuffer getSpriteBuffer() {
        return getBuffer(0);
    }

    /**
     * Returns the buffer that holds entity data, this is the object specific data that is used to handle
     * behavior.
     * 
     * @return
     */
    public ComponentBuffer getEntityBuffer() {
        return getBuffer(1);
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
     * 
     * @param system
     */
    private void createBuffers(com.nucleus.system.System system) {
        spritedataSize = mapper.attributesPerVertex;
        CPUComponentBuffer spriteData = new CPUComponentBuffer(count, mapper.attributesPerVertex * 4);
        CPUComponentBuffer entityData = new CPUComponentBuffer(count,
                system.getEntityDataSize() + mapper.attributesPerVertex);
        spriteExpander = new CPUQuadExpander(spriteMesh, mapper, entityData, spriteData);
        addBuffer(0, spriteData);
        addBuffer(1, entityData);
    }

    @Override
    public void create(NucleusRenderer renderer, ComponentNode parent, com.nucleus.system.System system)
            throws ComponentException {
        try {
            SpriteMesh.Builder spriteBuilder = createMeshBuilder(renderer, parent, count, rectangle);
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
        parent.addMesh(spriteMesh, MeshIndex.MAIN);
        createBuffers(system);
        spriteMesh.setAttributeUpdater(this);
        bindAttributeBuffer(spriteMesh.getAttributeBuffer(BufferIndex.ATTRIBUTES.index));
    }

    /**
     * Creates a sprite mesh builder for a mesh to the specified componentnode.
     * 
     * @param renderer
     * @param parent The parent node for the sprite mesh
     * @param count Number of sprites
     * @param rectangle Sprite shape
     * @return
     * @throws IOException If there was an io error creating builder, probably when loading texture
     */
    public static SpriteMesh.Builder createMeshBuilder(NucleusRenderer renderer, ComponentNode parent, int count,
            Rectangle rectangle) throws IOException {
        SpriteMesh.Builder spriteBuilder = new SpriteMesh.Builder(renderer);
        spriteBuilder.setTexture(parent.getTextureRef());
        spriteBuilder.setMaterial(parent.getMaterial() != null ? parent.getMaterial() : new Material());
        spriteBuilder.setSpriteCount(count);
        RectangleConfiguration config = new RectangleShapeBuilder.RectangleConfiguration(rectangle,
                RectangleShapeBuilder.DEFAULT_Z, count, 0);
        config.enableVertexIndex(true);
        RectangleShapeBuilder shapeBuilder = new RectangleShapeBuilder(config);
        spriteBuilder.setShapeBuilder(shapeBuilder);
        return spriteBuilder;
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

    /**
     * Sets the transform for a sprite using 3 values for xyz axis, translate.xyz, rotate.xyz, scale.xyz
     * Use this method for initialization only
     * 
     * @param sprite
     * @param transform 3 axis translate, rotate and scale values
     */
    public void setTransform(int sprite, float[] transform) {
        spriteExpander.setTransform(sprite, transform);
    }

    /**
     * Sets the data for the sprite, the data shall be indexed using the mapper for the sprite component.
     * {@link #getMapper()}
     * Use this method for initialization only
     * 
     * @param sprite
     * @param data
     */
    public void setSprite(int sprite, float[] data) {
        spriteExpander.setData(sprite, data);
    }

    /**
     * Sets the entity specific data for a sprite.
     * 
     * @param sprite
     * @param destOffset Offset in destination.
     */
    public void setEntityData(int sprite, int destOffset, float[] data) {
        ComponentBuffer entityBuffer = getBuffer(1);
        entityBuffer.put(sprite, destOffset, data, 0, data.length);
    }

    @Override
    public void bindAttributeBuffer(AttributeBuffer buffer) {
        spriteExpander.bindAttributeBuffer(buffer);
    }

    @Override
    public void updateAttributeData(NucleusRenderer renderer) {
        spriteExpander.updateAttributeData(renderer);
    }
}
