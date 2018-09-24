package com.graphicsengine.component;

import java.io.IOException;
import java.util.Random;

import com.google.gson.annotations.SerializedName;
import com.nucleus.component.Component;
import com.nucleus.component.ComponentBuffer;
import com.nucleus.component.ComponentException;
import com.nucleus.geometry.AttributeUpdater.BufferIndex;
import com.nucleus.geometry.AttributeUpdater.Consumer;
import com.nucleus.geometry.Material;
import com.nucleus.geometry.Mesh;
import com.nucleus.geometry.Mesh.Builder;
import com.nucleus.geometry.MeshBuilder.MeshBuilderFactory;
import com.nucleus.geometry.shape.ShapeBuilder;
import com.nucleus.io.ExternalReference;
import com.nucleus.opengl.GLES20Wrapper;
import com.nucleus.opengl.GLException;
import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.scene.AbstractNode.MeshIndex;
import com.nucleus.scene.ComponentNode;
import com.nucleus.scene.Node;
import com.nucleus.scene.RenderableNode;
import com.nucleus.shader.ShaderProgram;
import com.nucleus.shader.VariableIndexer.Indexer;
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

        /**
         * Internal constructor
         * 
         * @param values
         */
        protected EntityIndexer(int[] values) {
            super(values);
            moveVector = attributesPerVertex;
            elasticity = attributesPerVertex + 3;
            resistance = attributesPerVertex + 4;
            rotateSpeed = attributesPerVertex + 5;
            boundingBox = attributesPerVertex + 6;
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
    transient private ComponentNode parent;

    /**
     * The mapper used - this shall be set in the {@link #create(NucleusRenderer, ComponentNode, System)} method
     */
    transient protected EntityIndexer mapper;
    @Deprecated
    transient protected ShaderProgram program;
    transient protected TextureType textureType;
    transient protected UVAtlas uvAtlas;

    /**
     * Creates the instance of a mesh to be used in {@link #createMeshBuilder(GLES20Wrapper, Node, int, ShapeBuilder)}
     * 
     * @param gles
     * @return
     */
    protected abstract Mesh.Builder<Mesh> createBuilderInstance(GLES20Wrapper gles);

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
    public void create(GLES20Wrapper gles, ComponentNode parent)
            throws ComponentException {
        this.parent = parent;
        try {
            if (shape == null) {
                throw new IllegalArgumentException("Component " + parent.getId() + " must define 'shape'");
            }
            switch (shape.getType()) {
                case rect:
                    Builder<Mesh> spriteBuilder = createMeshBuilder(gles, createShapeBuilder());
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
        program = parent.getProgram();
    }

    @Override
    public Builder<Mesh> createMeshBuilder(GLES20Wrapper gles, ShapeBuilder shapeBuilder)
            throws IOException {
        Mesh.Builder<Mesh> spriteBuilder = createBuilderInstance(gles);
        initMeshBuilder(gles, parent, parent.getTextureRef(), count, shapeBuilder, spriteBuilder);
        return spriteBuilder;
    }

    /**
     * Sets texture, material and shapebuilder from the parent node - if not already set in builder.
     * Sets objectcount and attribute per vertex size.
     * If parent does not have program the
     * {@link com.nucleus.geometry.Mesh.Builder#createProgram(com.nucleus.opengl.GLES20Wrapper)}
     * method is called to create a suitable program.
     * The returned builder shall have needed values to create a mesh.
     * 
     * @param gles
     * @param parent
     * @param count Number of objects
     * @param shapeBuilder
     * @param builder
     * @throws IOException
     */
    protected Mesh.Builder<Mesh> initMeshBuilder(GLES20Wrapper gles, RenderableNode<Mesh> parent,
            ExternalReference textureRef, int count,
            ShapeBuilder shapeBuilder, Mesh.Builder<Mesh> builder)
            throws IOException {
        if (builder.getTexture() == null) {
            builder.setTexture(textureRef);
        }
        if (builder.getMaterial() == null) {
            builder.setMaterial(parent.getMaterial() != null ? parent.getMaterial() : new Material());
        }
        builder.setObjectCount(count);
        if (builder.getShapeBuilder() == null) {
            builder.setShapeBuilder(shapeBuilder);
        }
        if (parent.getProgram() == null) {
            parent.setProgram(builder.createProgram());
        }
        builder.setAttributesPerVertex(parent.getProgram().getAttributeSizes());
        return builder;
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

    @Deprecated
    public ShaderProgram getProgram() {
        return program;
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
        if (rectBounds != null && mapper.scale != -1) {
            entityData[mapper.boundingBox] = rectBounds[0] * entityData[mapper.scale];
            entityData[mapper.boundingBox + 1] = rectBounds[1] * entityData[mapper.scale + 1];
            entityData[mapper.boundingBox + 2] = rectBounds[2] * entityData[mapper.scale];
            entityData[mapper.boundingBox + 3] = rectBounds[3] * entityData[mapper.scale + 1];
        }
    }

    public static void getRandomPos(float[] spriteData, float xMax, float yMax, float zMax, Indexer mapper,
            Random random) {
        spriteData[mapper.vertex] = ((random.nextFloat() * xMax) - xMax / 2);
        spriteData[mapper.vertex + 1] = ((random.nextFloat() * yMax) - yMax / 2);
        spriteData[mapper.vertex + 2] = random.nextFloat() * zMax;

    }

    public static void setRotate(float[] spriteData, float x, float y, float z, Indexer mapper) {
        if (mapper.rotate > -1) {
            spriteData[mapper.rotate] = x;
            spriteData[mapper.rotate + 1] = y;
            spriteData[mapper.rotate + 2] = z;
        }
    }

    public static void setScale2D(float[] spriteData, float scaleRandom, float minScale, Indexer mapper,
            Random random) {
        if (mapper.scale > -1) {
            float scale = scaleRandom * random.nextFloat() + minScale;
            spriteData[mapper.scale] = scale;
            spriteData[mapper.scale + 1] = scale;
            spriteData[mapper.scale + 2] = 1;
        }

    }

    public static void getRandomSprite(float[] spriteData, float rotate, int frame, float scaleRandom, float minScale,
            float sceneWidth, float sceneHeight,
            Indexer mapper, Random random) {
        ActorComponent.getRandomPos(spriteData, sceneWidth, sceneHeight, 0, mapper, random);
        ActorComponent.setRotate(spriteData, 0, 0, rotate, mapper);
        ActorComponent.setScale2D(spriteData, scaleRandom, minScale, mapper, random);
        if (mapper.frame > -1) {
            spriteData[mapper.frame] = frame;
        }
    }

    public static void getRandomSprite3D(float[] spriteData, float rotate, int frame, float scaleRandom, float minScale,
            float sceneWidth, float sceneHeight, float maxZ, Indexer mapper, Random random) {
        ActorComponent.getRandomPos(spriteData, sceneWidth, sceneHeight, maxZ, mapper, random);
        ActorComponent.setRotate(spriteData, 0, 0, rotate, mapper);
        ActorComponent.setScale2D(spriteData, scaleRandom, minScale, mapper, random);
        if (mapper.frame > -1) {
            spriteData[mapper.frame] = frame;
        }
    }

}
