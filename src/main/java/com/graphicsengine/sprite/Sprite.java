package com.graphicsengine.sprite;

import com.nucleus.actor.ActorContainer;
import com.nucleus.actor.ActorItem;
import com.nucleus.geometry.AttributeUpdater.Producer;
import com.nucleus.scene.Node;
import com.nucleus.vecmath.VecMath;
import com.nucleus.vecmath.Vector2D;

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
     * Index to x position.
     */
    public final static int X_POS = 0;
    /**
     * Index to y position.
     */
    public final static int Y_POS = 1;
    /**
     * Index to z position.
     */
    public final static int Z_POS = 2;

    public final static int MOVE_VECTOR_X = 3;
    public final static int MOVE_VECTOR_Y = 4;
    public final static int MOVE_VECTOR_Z = 5;
    public final static int FRAME = 6;
    public final static int ROTATION = 7; // z axis rotation angle
    public final static int SCALE = 8; // Uniform scale
    /**
     * Number of float data values reserved for sprite, first free index is SPRITE_FLOAT_COUNT
     */
    public final static int SPRITE_FLOAT_COUNT = SCALE + 1;

    /**
     * The sprite actor implementation
     */
    public ActorItem actor;

    /**
     * The parent node, ie the node containing the sprite.
     */
    public Node parent;

    /**
     * Creates a new sprite with storage for MIN_FLOAT_COUNT floats and MIN_INT_COUNT ints
     * 
     * @param parent The node containing the sprite
     */
    protected Sprite(Node parent) {
        this.parent = parent;
        createArrays(MIN_FLOAT_COUNT, MIN_INT_COUNT);
    }

    /**
     * Creates a new sprite with storage for the specified number of float and ints.
     * 
     * @param floatCount
     * @param intCount
     */
    protected Sprite(int floatCount, int intCount) {
        createArrays(floatCount, intCount);
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
     * @param intCount
     * @throws IllegalArgumentException if floatCount < MIN_FLOAT_COUNT or intCount < MIN_INT_COUNT
     */
    private void createArrays(int floatCount, int intCount) {
        if (floatCount < MIN_FLOAT_COUNT || intCount < MIN_INT_COUNT) {
            throw new IllegalArgumentException(INVALID_DATACOUNT_ERROR);
        }
        floatData = new float[floatCount];
        intData = new int[intCount];
    }

    /**
     * Sets the x, y position and frame of this sprite.
     * 
     * @param x
     * @param y
     */
    public void setPosition(float x, float y) {
        floatData[X_POS] = x;
        floatData[Y_POS] = y;
    }

    /**
     * Sets the frame number, must be used for instance by sprites using the UVTexture otherwise
     * UV coordinates are not updated.
     * 
     * @param frame
     */
    public void setFrame(int frame) {
        floatData[FRAME] = frame;
    }

    public void setRotation(float rotation) {
        floatData[ROTATION] = rotation;
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

    /**
     * Updates the movement according to the specified acceleration (x and y axis) and time
     * 
     * @param x Acceleration on x axis
     * @param y Acceleration on y axis
     * @param deltaTime Time since last time movement was updated, ie elapsed time.
     */
    public static void accelerate(ActorContainer sprite, float x, float y, float deltaTime) {
        float[] floatData = sprite.floatData;
        floatData[MOVE_VECTOR_X] += x * deltaTime;
        floatData[MOVE_VECTOR_Y] += y * deltaTime;
    }

    /**
     * Applies movement and gravity to position
     * 
     * @param deltaTime
     */
    public static void move(ActorContainer sprite, float deltaTime) {
        float[] floatData = sprite.floatData;
        Vector2D moveVector = sprite.moveVector;
        floatData[X_POS] += deltaTime * moveVector.vector[VecMath.X] * moveVector.vector[Vector2D.MAGNITUDE] +
                floatData[MOVE_VECTOR_X] * deltaTime;
        floatData[Y_POS] += deltaTime * moveVector.vector[VecMath.Y] * moveVector.vector[Vector2D.MAGNITUDE] +
                floatData[MOVE_VECTOR_Y] * deltaTime;
    }

}
