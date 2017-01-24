package com.graphicsengine.component;

import java.io.IOException;

import com.google.gson.annotations.SerializedName;
import com.graphicsengine.spritemesh.SpriteMesh;
import com.graphicsengine.spritemesh.SpriteMeshFactory;
import com.graphicsengine.system.SpriteSystem;
import com.nucleus.actor.ComponentNode;
import com.nucleus.component.Component;
import com.nucleus.component.ComponentException;
import com.nucleus.geometry.AttributeUpdater.Consumer;
import com.nucleus.geometry.AttributeUpdater.PropertyMapper;
import com.nucleus.geometry.VertexBuffer;
import com.nucleus.io.ResourcesData;
import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.shader.ShaderProgram;
import com.nucleus.system.System;
import com.nucleus.vecmath.Rectangle;
import com.nucleus.vecmath.Vector2D;

/**
 * The old school sprite component, this is a collection of a number of (similar) sprite components
 * that have the data in a shared buffer.
 * The component can be seen as a container for the data needed to process the sprites - but not the behavior itself.
 * This class is used by the {@linkplain SpriteSystem} to process behavior, the System is where the logic is.
 * 
 * This component will hold data for the sprite properties, such as position, movement, frame.
 * The class can be serialized using gson
 * 
 * TODO Shall this class have a reference to {@linkplain SpriteMesh} or just reference the attribute data (as is now)
 * TODO Make it possible to controll which {@linkplain System} is used to process logic.
 * 
 * @author Richard Sahlin
 *
 */
public class SpriteComponent extends Component implements Consumer {

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

    public enum ProgramType {
        /**
         * Using tiled texture and program
         */
        TILED(),
        /**
         * Using UV texture and program
         */
        UV();
    }

    /**
     * The rectangle defining the sprites, all sprites will have same size
     * 4 values = x1,y1 + width and height
     */
    @SerializedName("rect")
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
    transient protected SpriteMesh spriteMesh;

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
        this.floatData = new float[SpriteComponent.SpriteData.getSize() * count];
        this.moveVector = new Vector2D[count];
        for (int i = 0; i < count; i++) {
            moveVector[i] = new Vector2D();
        }
    }

    @Override
    public void create(NucleusRenderer renderer, ResourcesData resources, ComponentNode parent)
            throws ComponentException {
        try {
            spriteMesh = SpriteMeshFactory.createSpriteMesh(renderer, parent.getTextureRef(), parent.getMaterial(),
                    count,
                    rectangle);
        } catch (IOException e) {
            throw new ComponentException("Could not create component: " + e.getMessage());
        }
        mapper = spriteMesh.getMapper();
        attributeData = spriteMesh.getAttributeData();
        parent.addMesh(spriteMesh);
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
     * Returns the attributedata owned by this component
     * 
     * @return
     */
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
     * Sets the x, y and z position
     * 
     * @param index The sprite number
     * @param x X position
     * @param y Y position
     * @param z Z position
     */
    public void setPosition(int index, float x, float y, float z) {
        int offset = index * SpriteData.getSize();
        floatData[offset++ + SpriteData.TRANSLATE.index] = x;
        floatData[offset++ + SpriteData.TRANSLATE.index] = y;
        floatData[offset++ + SpriteData.TRANSLATE.index] = z;
        offset = index * mapper.ATTRIBUTES_PER_VERTEX * ShaderProgram.VERTICES_PER_SPRITE;
        for (int i = 0; i < ShaderProgram.VERTICES_PER_SPRITE; i++) {
            attributeData[offset + mapper.TRANSLATE_INDEX] = x;
            attributeData[offset + mapper.TRANSLATE_INDEX + 1] = y;
            attributeData[offset + mapper.TRANSLATE_INDEX + 2] = z;
            offset += mapper.ATTRIBUTES_PER_VERTEX;
        }
    }

    /**
     * Sets the frame number, must be used for instance by sprites using the UVTexture otherwise
     * UV coordinates are not updated.
     * 
     * @param index The sprite number
     * @param frame The frame number to set
     */
    public void setFrame(int index, int frame) {
        int offset = index * SpriteData.getSize();
        floatData[offset + SpriteData.FRAME.index] = frame;
        offset = index * mapper.ATTRIBUTES_PER_VERTEX * ShaderProgram.VERTICES_PER_SPRITE;
        for (int i = 0; i < ShaderProgram.VERTICES_PER_SPRITE; i++) {
            attributeData[offset + mapper.FRAME_INDEX] = frame;
            offset += mapper.ATTRIBUTES_PER_VERTEX;
        }
    }

    public void setRotateSpeed(int index, float speed) {
        int offset = index * SpriteData.getSize();
        floatData[offset + SpriteData.ROTATE_SPEED.index] = speed;
    }

    public void setElasticity(int index, float elasticity) {
        int offset = index * SpriteData.getSize();
        floatData[offset + SpriteData.ELASTICITY.index] = elasticity;
    }

    /**
     * Sets the scale in x and y axis
     * 
     * @param index The sprite number
     * @param x X axis scale
     * @param y Y axis scale
     */
    public void setScale(int index, float x, float y) {
        int offset = index * SpriteData.getSize();
        floatData[offset++ + SpriteData.SCALE.index] = x;
        floatData[offset + SpriteData.SCALE.index] = y;
        offset = index * mapper.ATTRIBUTES_PER_VERTEX * ShaderProgram.VERTICES_PER_SPRITE;
        for (int i = 0; i < ShaderProgram.VERTICES_PER_SPRITE; i++) {
            attributeData[offset + mapper.SCALE_INDEX] = x;
            attributeData[offset + mapper.SCALE_INDEX + 1] = y;
            offset += mapper.ATTRIBUTES_PER_VERTEX;
        }
    }

    /**
     * 
     * @param index The sprite number
     * @param rotation
     */
    public void setRotation(int index, float rotation) {
        int offset = index * SpriteData.getSize();
        floatData[offset + SpriteData.ROTATE.index] = rotation;
        offset = index * mapper.ATTRIBUTES_PER_VERTEX * ShaderProgram.VERTICES_PER_SPRITE;
        for (int i = 0; i < ShaderProgram.VERTICES_PER_SPRITE; i++) {
            attributeData[offset + mapper.ROTATE_INDEX] = rotation;
            offset += mapper.ATTRIBUTES_PER_VERTEX;
        }
    }

    @Override
    public void updateAttributeData() {
        // TODO Auto-generated method stub
    }

    @Override
    public void bindAttributeBuffer(VertexBuffer buffer) {
    }
}
