package com.graphicsengine.component;

import java.io.IOException;
import java.util.Random;

import com.google.gson.annotations.SerializedName;
import com.nucleus.component.Component;
import com.nucleus.component.ComponentBuffer;
import com.nucleus.component.ComponentException;
import com.nucleus.geometry.AttributeUpdater.Consumer;
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
import com.nucleus.shader.ShaderProperty.PropertyMapper;
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

    public static class EntityMapper extends PropertyMapper {

        public int moveVectorOffset;
        public int elasticityOffset;
        public int resistanceOffset;
        public int rotateSpeedOffset;
        public int attributesPerEntity;

        public EntityMapper(PropertyMapper source) {
            super(source);
            moveVectorOffset = source.attributesPerVertex;
            elasticityOffset = source.attributesPerVertex + 3;
            resistanceOffset = source.attributesPerVertex + 4;
            rotateSpeedOffset = source.attributesPerVertex + 5;
            attributesPerEntity = rotateSpeedOffset + 1;
        }

    }

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
    transient protected EntityMapper mapper;
    transient protected TextureType textureType;
    transient protected UVAtlas uvAtlas;

    /**
     * Creates the instance of a mesh to be used in {@link #createMeshBuilder(NucleusRenderer, Node, int, ShapeBuilder)}
     * 
     * @param renderer
     * @return
     */
    protected abstract Mesh.Builder<Mesh> createBuilderInstance(NucleusRenderer renderer);

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
     * TODO Should this method be moved to Component?
     * 
     * @param system
     */
    protected abstract void createBuffers();

    /**
     * Sets actor position
     * If the component uses an expander this is called to expand data.
     * 
     * @param actor The actor index to update
     * @param position x,y and z
     * @param offset Offset into position where values are read.
     */
    public abstract void setPosition(int actor, float[] position, int offset);

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
        mapper = new EntityMapper(mesh.getMapper());
    }

    @Override
    public void create(NucleusRenderer renderer, ComponentNode parent)
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
        createBuffers();
        mesh.setAttributeUpdater(this);
        bindAttributeBuffer(mesh.getAttributeBuffer(BufferIndex.ATTRIBUTES.index));
    }

    @Override
    public Mesh.Builder<Mesh> createMeshBuilder(NucleusRenderer renderer, Node parent, int count,
            ShapeBuilder shapeBuilder) throws IOException {
        Mesh.Builder<Mesh> spriteBuilder = createBuilderInstance(renderer);
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
     * Returns the entity mapper for this component.
     * If {@link #create(NucleusRenderer, ComponentNode)} has not been called null is returned.
     * 
     * @return The entitymapper used by this component, or null.
     */
    public EntityMapper getMapper() {
        return mapper;
    }

    @Override
    public int getEntityDataSize() {
        EntityMapper mapper = getMapper();
        return mapper.attributesPerEntity;
    }

    /**
     * ****************************************************************
     * Utility methods
     * ****************************************************************
     * TODO Where do these belong?
     */

    public static void getRandomEntityData(float[] entityData, EntityMapper mapper, Random random) {
        entityData[mapper.moveVectorOffset] = 0;
        entityData[mapper.moveVectorOffset + 1] = 0;
        entityData[mapper.elasticityOffset] = 0.5f + random.nextFloat() * 0.5f;
        entityData[mapper.resistanceOffset] = random.nextFloat() * 0.03f;
        entityData[mapper.rotateSpeedOffset] = 0;
    }

    public static void getRandomSprite(float[] spriteData, float rotate, int frame, float sceneWidth, float sceneHeight,
            EntityMapper mapper, Random random) {
        spriteData[mapper.translateOffset] = ((random.nextFloat() * sceneWidth) - sceneWidth / 2);
        spriteData[mapper.translateOffset + 1] = ((random.nextFloat() * sceneHeight) - sceneHeight / 2);
        spriteData[mapper.translateOffset + 2] = 1;
        spriteData[mapper.rotateOffset] = 0;
        spriteData[mapper.rotateOffset + 1] = 0;
        spriteData[mapper.rotateOffset + 2] = rotate;
        spriteData[mapper.scaleOffset] = 1;
        spriteData[mapper.scaleOffset + 1] = 1;
        spriteData[mapper.scaleOffset + 2] = 1;
        spriteData[mapper.frameOffset] = frame;
    }

}
