package com.graphicsengine.component;

import java.io.IOException;

import com.google.gson.annotations.SerializedName;
import com.graphicsengine.spritemesh.SpriteMesh;
import com.nucleus.component.Component;
import com.nucleus.component.ComponentBuffer;
import com.nucleus.component.ComponentException;
import com.nucleus.geometry.AttributeUpdater.Consumer;
import com.nucleus.geometry.AttributeUpdater.PropertyMapper;
import com.nucleus.geometry.Material;
import com.nucleus.geometry.Mesh;
import com.nucleus.geometry.Mesh.BufferIndex;
import com.nucleus.geometry.Mesh.Builder;
import com.nucleus.geometry.MeshBuilder.MeshBuilderFactory;
import com.nucleus.geometry.shape.RectangleShapeBuilder;
import com.nucleus.geometry.shape.ShapeBuilder;
import com.nucleus.geometry.shape.ShapeBuilderFactory;
import com.nucleus.opengl.GLException;
import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.scene.ComponentNode;
import com.nucleus.scene.Node;
import com.nucleus.scene.Node.MeshIndex;
import com.nucleus.system.System;
import com.nucleus.texturing.Texture2D;
import com.nucleus.texturing.TextureType;
import com.nucleus.texturing.UVAtlas;
import com.nucleus.texturing.UVTexture2D;
import com.nucleus.vecmath.Shape;

/**
 * The actor component, this is a collection of a number of (similar) moving on screen objects that have the data in a
 * shared buffer.
 * The component can be seen as a container for the data needed to process the actors - but not the behavior itself.
 * This class is used by implementations of {@link System} to process behavior, the System is where the logic is and
 * this class can be used as a container for the (visible) data.
 * 
 * This component will hold data for the actor properties, such as position, rotation, scale, frame - the visible
 * properties.
 * This data is fetched by calling {@link #getEntityBuffer()} - depending on subclass this data may be copied into
 * attribute buffer (QuadExpander or shader) or a geometry mesh may be used. In either case subclasses must ensure the
 * proper data is created from the entity buffer to enable mesh to be rendered.
 * 
 * The class can be serialized using gson
 * 
 * @author Richard Sahlin
 *
 */
public abstract class ActorComponent<T extends Mesh> extends Component implements Consumer, MeshBuilderFactory<Mesh> {

    public static final String COUNT = "count";

    /**
     * Number of actors
     */
    @SerializedName(COUNT)
    protected int count;
    /**
     * The rectangle defining the sprites, all sprites will have same size
     * 4 values = x1,y1 + width and height
     */
    @SerializedName(Shape.SHAPE)
    protected Shape shape;

    transient T mesh;

    /**
     * The mapper used - this shall be set in the {@link #create(NucleusRenderer, ComponentNode, System)} method
     */
    transient protected PropertyMapper mapper;
    transient protected TextureType textureType;
    transient protected UVAtlas uvAtlas;

    /**
     * Sets data from source into this
     * 
     * @param source
     */
    protected void set(ActorComponent<T> source) {
        super.set(source);
        this.count = source.count;
        if (source.shape != null) {
            this.shape = Shape.createInstance(source.shape);
        } else {
            shape = null;
        }
    }

    protected void setMesh(T mesh) {
        this.mesh = mesh;
        mapper = mesh.getMapper();
    }

    /**
     * Returns the buffer that holds entity data, this is the object specific data that is used to handle
     * behavior.
     * 
     * @return The buffer with actor (entity) data
     */
    public abstract ComponentBuffer getEntityBuffer();

    /**
     * Sets the entity specific data for a sprite.
     * 
     * @param entity The entity index
     * @param destOffset Offset in destination where data is copied.
     */
    public abstract void setEntityData(int entity, int destOffset, float[] data);

    /**
     * Internal method
     * Creates the arrays for this spritecomponent
     * 
     * @param system
     */
    protected abstract void createBuffers(com.nucleus.system.System system);

    /**
     * Sets the data for the actor, the data shall be indexed using the mapper for the sprite component.
     * {@link #getMapper()}
     * If the component uses an expander this is called to expand data.
     * Use this method for initialization only
     * 
     * @param actor The actor index to set
     * @param data Data that can be set using the mapper
     */
    public abstract void setActor(int sprite, float[] data);

    @Override
    public void create(NucleusRenderer renderer, ComponentNode parent, com.nucleus.system.System system)
            throws ComponentException {
        try {
            if (shape == null) {
                throw new IllegalArgumentException("Component " + parent.getId() + " must define 'shape'");
            }
            switch (shape.getType()) {
                case rect:
                    ShapeBuilder shapeBuilder = ShapeBuilderFactory.createBuilder(shape,
                            new float[] { RectangleShapeBuilder.DEFAULT_Z }, count, 0);
                    Builder<Mesh> spriteBuilder = createMeshBuilder(renderer, parent, count, shapeBuilder);
                    // TODO - Fix generics so that cast is not needed
                    setMesh((T) spriteBuilder.create());
            }
        } catch (IOException | GLException e) {
            throw new ComponentException("Could not create component: " + e.getMessage());
        }
        this.textureType = mesh.getTexture(Texture2D.TEXTURE_0).getTextureType();
        switch (textureType) {
            case TiledTexture2D:
                break;
            case UVTexture2D:
                uvAtlas = ((UVTexture2D) mesh.getTexture(Texture2D.TEXTURE_0)).getUVAtlas();
                break;
            default:
                break;
        }
        parent.addMesh(mesh, MeshIndex.MAIN);
        createBuffers(system);
        mesh.setAttributeUpdater(this);
        bindAttributeBuffer(mesh.getAttributeBuffer(BufferIndex.ATTRIBUTES.index));
    }

    @Override
    public Mesh.Builder<Mesh> createMeshBuilder(NucleusRenderer renderer, Node parent, int count,
            ShapeBuilder shapeBuilder) throws IOException {
        SpriteMesh.Builder spriteBuilder = new SpriteMesh.Builder(renderer);
        spriteBuilder.setTexture(parent.getTextureRef());
        spriteBuilder.setMaterial(parent.getMaterial() != null ? parent.getMaterial() : new Material());
        spriteBuilder.setObjectCount(count).setShapeBuilder(shapeBuilder);
        return spriteBuilder;
    }

    /**
     * Returns the number of actors/entities in this component
     * 
     * @return
     */
    public int getCount() {
        return count;
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

}
