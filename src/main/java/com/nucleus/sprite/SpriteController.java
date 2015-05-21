package com.nucleus.sprite;

/**
 * Controller for a set of sprites.
 * 
 * @author Richard Sahlin
 *
 */
public abstract class SpriteController {

    protected Sprite[] sprites;
    protected int count;

    /**
     * Creates a TiledSpriteController with an array of the specified size and creates all of the sprite objects.
     * 
     * @param count Number of sprites to create.
     * @param data Optional object passed on when createSprites() is called
     * 
     */
    public SpriteController(int count, Object data) {
        this.count = count;
        sprites = new Sprite[count];
        createSprites(data);
    }

    /**
     * Internal method to create all the sprite instances for the controller.
     * When this method returns all objects in the array shall be created and ready to be used.
     * This method will be called by the constructor in this class, ie subclasses must call
     * super(count) in their constructor.
     * 
     * @param data Optional data object
     */
    protected abstract void createSprites(Object data);

    /**
     * Returns the number of sprites in this controller
     * 
     * @return
     */
    public int getCount() {
        return count;
    }

    /**
     * Returns the array containing the sprites.
     * Note that any changes to the array will be reflected here.
     * 
     * @return The array containing all sprites.
     */
    public Sprite[] getSprites() {
        return sprites;
    }

}
