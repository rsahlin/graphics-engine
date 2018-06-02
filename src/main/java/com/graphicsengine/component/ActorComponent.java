package com.graphicsengine.component;

import java.io.IOException;
import java.util.Random;

import com.google.gson.annotations.SerializedName;
import com.nucleus.component.Component;
import com.nucleus.component.ComponentBuffer;
import com.nucleus.component.ComponentException;
import com.nucleus.geometry.AttributeUpdater.Consumer;
import com.nucleus.geometry.Mesh;
import com.nucleus.geometry.Mesh.BufferIndex;
import com.nucleus.geometry.Mesh.Builder;
import com.nucleus.geometry.MeshBuilder.MeshBuilderFactory;
import com.nucleus.geometry.shape.ShapeBuilder;
import com.nucleus.opengl.GLException;
import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.scene.ComponentNode;
import com.nucleus.scene.Node;
import com.nucleus.scene.Node.MeshIndex;
import com.nucleus.shader.AttributeIndexer.Indexer;
import com.nucleus.system.System;
import com.nucleus.texturing.Texture2D;
import com.nucleus.texturing.TextureType;
import com.nucleus.texturing.UVAtlas;
import com.nucleus.texturing.UVTexture2D;
import com.nucleus.vecmath.Shape;

/**
 * The actor component, this is a collection of a number of (similar) moving/on screen objects that have the data in a
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

    /**
     * Initialization methods for entity dataset - these should NOT be used to update the data, only for init purposes.
     *
     */
    public interface EntityData {

        /**
         * Copies length number of values from data, beginning at offset and storing for the entity, beginning to write
         * at entityOffset
         * Use this method for init purposes - it is not optimized for updating a large number of entities at runtime.
         * 
         * @param entity
         * @param entityOffset
         * @param data
         * @param offset
         * @param length
         */
        public void setEntity(int entity, int entityOffset, float[] data, int offset, int length);

    }

    /**
     * Indexing of variables used by entity/actor
     * 
     * TODO How to specify the datatype (size)
     *
     */
    public static class EntityIndexer extends Indexer {

        public int moveVector;
        public int elasticity;
        public int resistance;
        public int rotateSpeed;
        public int boundingBox;
        public int attributesPerEntity;

        public EntityIndexer(Indexer source) {
            super(source);
            moveVector = source.attributesPerVertex;
            elasticity = source.attributesPerVertex + 3;
            resistance = source.attributesPerVertex + 4;
            rotateSpeed = source.attributesPerVertex + 5;
            boundingBox = source.attributesPerVertex + 6;
            // TODO This shall be calculated from variable sizes
            attributesPerEntity = boundingBox + 4;
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
    transient protected EntityIndexer mapper;
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
     * Creates the ShapeBuilder to be used to create shapes, or null if no builder shall be used - for instance
     * if mode is points.
     */
    protected abstract ShapeBuilder createShapeBuilder();

    /**
     * Returns the buffer that holds entity data, this is the object specific data that is used to handle
     * behavior.
     * 
     * @return The buffer with actor (entity) data
     */
    public abstract ComponentBuffer getEntityBuffer();

    /**
     * Internal method
     * Creates the arrays for this spritecomponent
     * TODO Should this method be moved to Component?
     * 
     * @param mapper
     * @param system
     */
    protected abstract void createBuffers(EntityIndexer mapper);

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
                    Builder<Mesh> spriteBuilder = createMeshBuilder(renderer, parent, count, createShapeBuilder());
                    // TODO - Fix generics so that cast is not needed
                    setMesh((T) spriteBuilder.create());
                    mapper = new EntityIndexer(new Indexer(parent.getProgram()));

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
        createBuffers(mapper);
        mesh.setAttributeUpdater(this);
        bindAttributeBuffer(mesh.getAttributeBuffer(BufferIndex.ATTRIBUTES.index));
    }

    @Override
    public Mesh.Builder<Mesh> createMeshBuilder(NucleusRenderer renderer, Node parent, int count,
            ShapeBuilder shapeBuilder) throws IOException {
        Mesh.Builder<Mesh> spriteBuilder = createBuilderInstance(renderer);
        parent.initMeshBuilder(renderer, parent, count, shapeBuilder, spriteBuilder);
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
    public EntityIndexer getMapper() {
        return mapper;
    }

    /**
     * Returns the shape for actors
     * 
     * @return
     */
    public Shape getShape() {
        return shape;
    }

    /**
     * Fetch 2D rectangle bounds for the shape
     * 
     * @param bounds Array of, at least, 4 values for x,y,width,height
     */
    public void get2DBounds(float[] bounds) {
        float[] values = shape.getValues();
        switch (shape.getType()) {
            case rect:
                bounds[0] = values[0];
                bounds[1] = values[1];
                bounds[2] = values[2];
                bounds[3] = values[3];
                break;
            default:
                throw new IllegalArgumentException("Not implemented for type: " + shape.getType());
        }
    }

    /**
     * ****************************************************************
     * Utility methods
     * ****************************************************************
     * TODO Where do these belong?
     */

    public static void getRandomEntityData(float[] entityData, float[] rectBounds, float rotateSpeed,
            EntityIndexer mapper, Random random) {
        entityData[mapper.moveVector] = 0;
        entityData[mapper.moveVector + 1] = 0;
        entityData[mapper.elasticity] = 0.5f + random.nextFloat() * 0.5f;
        entityData[mapper.resistance] = random.nextFloat() * 0.03f;
        entityData[mapper.rotateSpeed] = rotateSpeed * random.nextFloat();
        if (rectBounds != null) {
            java.lang.System.arraycopy(rectBounds, 0, entityData, mapper.boundingBox, 4);
        }
    }

    public static void getRandomSprite(float[] spriteData, float rotate, int frame, float scaleRandom, float minScale,
            float sceneWidth, float sceneHeight,
            Indexer mapper, Random random) {
        spriteData[mapper.vertex] = ((random.nextFloat() * sceneWidth) - sceneWidth / 2);
        spriteData[mapper.vertex + 1] = ((random.nextFloat() * sceneHeight) - sceneHeight / 2);
        spriteData[mapper.vertex + 2] = 0;
        if (mapper.rotate > -1) {
            spriteData[mapper.rotate] = 0;
            spriteData[mapper.rotate + 1] = 0;
            spriteData[mapper.rotate + 2] = rotate;
        }
        if (mapper.scale > -1) {
            float scale = scaleRandom * random.nextFloat() + minScale;
            spriteData[mapper.scale] = scale;
            spriteData[mapper.scale + 1] = scale;
            spriteData[mapper.scale + 2] = 1;
        }
        if (mapper.frame > -1) {
            spriteData[mapper.frame] = frame;
        }
    }

}
