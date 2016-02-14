package com.graphicsengine.sprite;

import com.nucleus.actor.ActorContainer;
import com.nucleus.actor.ActorItem;
import com.nucleus.geometry.AttributeUpdater.Producer;
import com.nucleus.geometry.AttributeUpdater.PropertyMapper;
import com.nucleus.scene.Node;
import com.nucleus.shader.ShaderProgram;

/**
 * Base sprite class, a sprite is a 2D geometry object
 * Has data needed for movement, animation and graphics - increase size of data storage and use together
 * with actor. This is to avoid a very large number of Sprite subclasses for logic.
 * 
 * @author Richard Sahlin
 *
 */
public abstract class Sprite extends ActorContainer implements Producer {

    public final static String INVALID_DATACOUNT_ERROR = "Invalid datacount";

    /**
     * Number of float data values reserved for sprite, first free index is SPRITE_FLOAT_COUNT
     */
    public final static int SPRITE_FLOAT_COUNT = SCALE + 1;

    /**
     * The sprite actor implementation
     */
    public ActorItem actor;

    protected PropertyMapper mapper;
    /**
     * Ref to sprite data, use with offset.
     * This sprites data is only one part of the whole array.
     */
    protected float[] attributeData;
    protected int offset;

    /**
     * The parent node, ie the node containing the sprite.
     */
    public Node parent;

    protected Sprite() {

    }

    /**
     * Sets the parent, mapper, attribute data and creates data arrays. After this call the sprite is ready
     * to be used.
     * 
     * @param parent
     * @param mapper
     * @param attributeData
     * @param index
     */
    protected void setup(Node parent, PropertyMapper mapper, float[] attributeData, int index) {
        this.parent = parent;
        this.attributeData = attributeData;
        offset = index * mapper.ATTRIBUTES_PER_VERTEX * ShaderProgram.VERTICES_PER_SPRITE;
        this.mapper = mapper;
        createArrays(MIN_FLOAT_COUNT);
    }

    @Override
    public void process(float deltaTime) {
        if (actor != null) {
            actor.process(this, deltaTime);
        }
    }

    /**
     * Internal method, creates the data storage.
     * 
     * @param floatCount
     * @throws IllegalArgumentException if floatCount < MIN_FLOAT_COUNT
     */
    private void createArrays(int floatCount) {
        if (floatCount < MIN_FLOAT_COUNT) {
            throw new IllegalArgumentException(INVALID_DATACOUNT_ERROR);
        }
        floatData = new float[floatCount];
    }

    /**
     * Sets the x, y position and frame of this sprite.
     * 
     * @param x
     * @param y
     * @param z
     */
    public void setPosition(float x, float y, float z) {
        int index = offset;
        for (int i = 0; i < ShaderProgram.VERTICES_PER_SPRITE; i++) {
            attributeData[index + mapper.TRANSLATE_INDEX] = x;
            attributeData[index + mapper.TRANSLATE_INDEX + 1] = y;
            attributeData[index + mapper.TRANSLATE_INDEX + 2] = z;
            index += mapper.ATTRIBUTES_PER_VERTEX;
        }

    }

    /**
     * Sets the frame number, must be used for instance by sprites using the UVTexture otherwise
     * UV coordinates are not updated.
     * 
     * @param frame
     */
    public void setFrame(int frame) {
        int index = offset;
        for (int i = 0; i < ShaderProgram.VERTICES_PER_SPRITE; i++) {
            attributeData[index + mapper.FRAME_INDEX + 2] = frame;
            index += mapper.ATTRIBUTES_PER_VERTEX;
        }
    }

    /**
     * Sets the scale in x and y axis
     * 
     * @param x
     * @param y
     */
    public void setScale(float x, float y) {
        int index = offset;
        for (int i = 0; i < ShaderProgram.VERTICES_PER_SPRITE; i++) {
            attributeData[index + mapper.SCALE_INDEX] = x;
            attributeData[index + mapper.SCALE_INDEX + 1] = y;
            index += mapper.ATTRIBUTES_PER_VERTEX;
        }
    }

    public void setRotation(float rotation) {
        int index = offset;
        for (int i = 0; i < ShaderProgram.VERTICES_PER_SPRITE; i++) {
            attributeData[index + mapper.ROTATE_INDEX] = rotation;
            index += mapper.ATTRIBUTES_PER_VERTEX;
        }
    }

    /**
     * Sets the movement vector for x, y and z axis. Use this to for instance clear the current movement, or to change
     * movement / direction
     * 
     * @param x X axis movement
     * @param y Y axis movement
     * @paran z Z axis movement
     */
    public void setMoveVector(float x, float y, float z) {
        floatData[MOVE_VECTOR_X] = x;
        floatData[MOVE_VECTOR_Y] = y;
        floatData[MOVE_VECTOR_Z] = z;
    }

}
