package com.graphicsengine.component;

import java.io.IOException;

import com.graphicsengine.spritemesh.SpriteMesh;
import com.nucleus.SimpleLogger;
import com.nucleus.component.CPUComponentBuffer;
import com.nucleus.component.CPUQuadExpander;
import com.nucleus.component.Component;
import com.nucleus.component.ComponentBuffer;
import com.nucleus.geometry.AttributeBuffer;
import com.nucleus.geometry.AttributeUpdater.Consumer;
import com.nucleus.geometry.AttributeUpdater.PropertyMapper;
import com.nucleus.geometry.shape.ShapeBuilder;
import com.nucleus.geometry.Material;
import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.scene.ComponentNode;
import com.nucleus.texturing.Texture2D;
import com.nucleus.texturing.TextureType;
import com.nucleus.vecmath.Shape;

/**
 * The old school sprite component, this is a collection of a number of (similar) sprite objects
 * that have the data in a shared buffer.
 * SpriteData is mapped one to one for each sprite, whereas the attribute data is one -> four for a quad based sprite.
 * The intention is that the logic processing and update to attributes (quad data) can be done using a Compute shader,
 * or OpenCL
 * This class is mostly used for GLES versions prior to 3.2 when geometry shaders can be used instead.
 * 
 * The class can be serialized using gson
 * 
 * TODO Shall this class have a reference to {@linkplain SpriteMesh} or just reference the attribute data (as is now)
 * 
 * @author Richard Sahlin
 *
 */
public class SpriteAttributeComponent extends ActorComponent<SpriteMesh> implements Consumer {

    public static final String GRAVITY = "gravity";

    public static final float DEFAULT_GRAVITY = 5;

    /**
     * The sprites attribute float data storage, this is the sprite visible (mesh) properties such as position, scale
     * and frame, plus entity data needed to process the logic.
     * This is what is generally needed in order to put sprite on screen.
     * In order to render a mesh with sprites this data is copied one -> four in the mesh.
     * TODO Use java.nio.FloatBuffer instead and perhaps move into a special class to handle 1 -> 4 mapping
     */
    transient protected CPUQuadExpander spriteExpander;

    transient protected int spritedataSize;

    @Override
    public Component createInstance() {
        return new SpriteAttributeComponent();
    }

    @Override
    public void set(Component source) {
        set((SpriteAttributeComponent) source);
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

    @Override
    public ComponentBuffer getEntityBuffer() {
        return getBuffer(1);
    }

    private void set(SpriteAttributeComponent source) {
        super.set(source);
        this.count = source.count;
        if (source.shape != null) {
            this.shape = Shape.createInstance(source.shape);
        } else {
            shape = null;
        }
    }

    @Override
    protected void createBuffers(com.nucleus.system.System system) {
        spritedataSize = mapper.attributesPerVertex;
        CPUComponentBuffer spriteData = new CPUComponentBuffer(count, mapper.attributesPerVertex * 4);
        CPUComponentBuffer entityData = new CPUComponentBuffer(count,
                system.getEntityDataSize() + mapper.attributesPerVertex);
        spriteExpander = new CPUQuadExpander(mesh, mapper, entityData, spriteData);
        addBuffer(0, spriteData);
        addBuffer(1, entityData);
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
    @Override
    public SpriteMesh.Builder createMeshBuilder(NucleusRenderer renderer, ComponentNode parent, int count,
            ShapeBuilder shapeBuilder) throws IOException {
        SpriteMesh.Builder spriteBuilder = SpriteMesh.Builder.createBuilder(renderer);
        spriteBuilder.setTexture(parent.getTextureRef());
        spriteBuilder.setMaterial(parent.getMaterial() != null ? parent.getMaterial() : new Material());
        spriteBuilder.setSpriteCount(count);
        spriteBuilder.setShapeBuilder(shapeBuilder);
        return spriteBuilder;
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
     * Returns the number of frames available in the texture
     * 
     * @return
     */
    public int getFrameCount() {
        return mesh.getTexture(Texture2D.TEXTURE_0).getFrameCount();
    }

    /**
     * Returns the propertymapper for the Mesh used by this component.
     * If {@link #create(NucleusRenderer, ComponentNode)} has not been called null is returned.
     * 
     * @return The PropertyMapper for the Mesh in the node used by this component, or null.
     */
    public PropertyMapper getMapper() {
        if (mesh != null) {
            return mesh.getMapper();
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
     * If the component uses an expander this is called to expand data.
     * Use this method for initialization only
     * 
     * @param sprite
     * @param data
     */
    public void setSprite(int sprite, float[] data) {
        spriteExpander.setData(sprite, data);
        spriteExpander.expandQuadData(sprite);
    }

    /**
     * Sets the x and y position
     * If the component uses an expander this is called to expand data.
     * 
     * @param sprite
     * @param x
     * @param y
     */
    public void setPosition(int sprite, float x, float y) {
        spriteExpander.setPosition(sprite, x, y);
    }

    @Override
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
