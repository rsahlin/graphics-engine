package com.graphicsengine.sprite;

import com.nucleus.vecmath.VecMath;
import com.nucleus.vecmath.Vector2D;

/**
 * Base sprite class, a sprite is a 2D geometry object
 * Has data needed for movement, animation and graphics - increase size of data storage and use together
 * with logic. This is to avoid a very large number of Sprite subclasses for logic.
 * 
 * @author Richard Sahlin
 *
 */
public abstract class Sprite {

    public interface Logic {
        /**
         * Do the processing of the sprite, this shall be called at intervals to do the logic processing.
         * The sprite data containing data is decoupled from the behavior
         * 
         * @param sprite The sprite to perform behavior for.
         * @param deltaTime Time in millis since last call.
         */
        public void process(Sprite sprite, float deltaTime);

        /**
         * Returns the name of the logic, ie the name of the implementing logic class.
         * This name is the same for all logic object of the same class, it is not instance name.
         * This shall be the same name that was used when the sprite logic was resolved.
         * 
         * @return The name of the implementing logic class
         */
        public String getLogicId();
    }

    public final static String INVALID_DATACOUNT_ERROR = "Invalid datacount";

    /**
     * Store the data used by subclasses into this class to prepare for rendering.
     * For some implementations this may do nothing, others may need to copy data from this class.
     */
    public abstract void prepare();

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

    /**
     * Number of float data values reserved for sprite, first free index is SPRITE_FLOAT_COUNT
     */
    public final static int SPRITE_FLOAT_COUNT = ROTATION + 1;

    /**
     * The sprite logic implementation
     */
    public Logic logic;

    /**
     * All sprites can move using a vector
     */
    public Vector2D moveVector = new Vector2D();
    public float[] floatData;
    public int[] intData;
    public final static int MIN_FLOAT_COUNT = 16;
    public final static int MIN_INT_COUNT = 0;

    /**
     * Creates a new sprite with storage for MIN_FLOAT_COUNT floats and MIN_INT_COUNT ints
     */
    public Sprite() {
        createArrays(MIN_FLOAT_COUNT, MIN_INT_COUNT);
    }

    /**
     * Creates a new sprite with storage for the specified number of float and ints.
     * 
     * @param floatCount
     * @param intCount
     */
    public Sprite(int floatCount, int intCount) {
        createArrays(floatCount, intCount);
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
     * Applies movement and gravity to position, then prepare() is called to let subclasses update
     * 
     * @param deltaTime
     */
    public void move(float deltaTime) {
        floatData[X_POS] += deltaTime * moveVector.vector[VecMath.X] * moveVector.vector[Vector2D.MAGNITUDE] +
                floatData[MOVE_VECTOR_X] * deltaTime;
        floatData[Y_POS] += deltaTime * moveVector.vector[VecMath.Y] * moveVector.vector[Vector2D.MAGNITUDE] +
                floatData[MOVE_VECTOR_Y] * deltaTime;
        prepare();
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
        prepare();
    }

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
    public void accelerate(float x, float y, float deltaTime) {
        floatData[MOVE_VECTOR_X] += x * deltaTime;
        floatData[MOVE_VECTOR_Y] += y * deltaTime;
    }

}
