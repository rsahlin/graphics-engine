package com.graphicsengine.sprite;

import com.graphicsengine.sprite.Sprite.Logic;
import com.nucleus.renderer.BaseRenderer;
import com.nucleus.scene.Node;

/**
 * Controller for a set of sprites.
 * This can be added as a node to the scenegraph.
 * 
 * @author Richard Sahlin
 *
 */
public abstract class SpriteController extends Node {

    private final static String LOGICRESOLVER_NOT_SET = "LogicResolver not set, must set before calling.";
    private final static String LOGIC_NOT_FOUND_ERROR = "Logic not found for id: ";

    /**
     * Interface used to find a Sprite logic class from String/Binary id
     * 
     * @author Richard Sahlin
     *
     */
    public interface LogicResolver {

        /**
         * Returns the sprite logic class for the specified id, this is normally done when loading scene or when
         * creating logic from loaded data.
         * 
         * @param id The id of the sprite object
         * @return The sprite logic object or null if not found
         */
        Logic getLogic(String id);

    }

    protected Sprite[] sprites;
    protected int count;
    protected LogicResolver logicResolver;

    /**
     * Creates a TiledSpriteController with an array of the specified size.
     * Each sprite must be created by calling createSprites()
     * 
     * @param id Id of the node
     * @param count Number of sprites to create.
     * 
     */
    protected void create(String id, int count) {
        setId(id);
        this.count = count;
        sprites = new Sprite[count];
    }

    /**
     * Adds a resolver to find implementing logic (sprite) classes from ids.
     * This shall be done programatically (from the code) to avoid binding classname to the leveldata, as this will
     * prevent proper obfuscation of the code.
     * 
     * @param resolver The resolver to add, used when createSprites() is called.
     */
    public void setLogicResolver(LogicResolver resolver) {
        logicResolver = resolver;
    }

    /**
     * Internal method to create all the sprite instances for the controller.
     * When this method returns all objects in the array shall be created and ready to be used.
     * Before calling this method it is necessary to add logic resolvers. Do this separate from loaded leveldata, ie
     * from code, to prevent referencing classnames in leveldata.
     * 
     * @param renderer The renderer to use with this controller
     * @param setup The logic instance for sprite classes.
     * @throws IllegalArgumentException If a logic resolver has not been set.
     */
    public abstract void createSprites(BaseRenderer renderer, SpriteControllerSetup setup);

    /**
     * Internal method to check if a logic resolver has been set, call this in implementations of the createSprites()
     * method to check that a resolver exist.
     */
    protected void validateResolver() {
        if (logicResolver == null) {
            throw new IllegalArgumentException(LOGICRESOLVER_NOT_SET);
        }
    }

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

    /**
     * Sets the logic for the sprites as defined in the setup class.
     * The sprites must be created before calling this method.
     * 
     * @param setup Setup containing the logic id, offset and count, for the sprites.
     */
    public void setLogic(SpriteControllerSetup setup) {
        String[] logicId = setup.getLogicId();
        int[] offsets = setup.getLogicOffset();
        int[] counts = setup.getLogicCount();
        setLogic(logicId, offsets, counts);

    }

    /**
     * Sets the logic for sprite objects, the sprites must be created before calling this method.
     * 
     * @param logicIds The logic ids for logic, used together with offsets and counts.
     * @param offsets The offset into sprite list for the logic to set.
     * @param counts The number of sprite logic to set at offset.
     */
    public void setLogic(String[] logicIds, int[] offsets, int[] counts) {
        for (int i = 0; i < logicIds.length; i++) {
            int offset = offsets[i];
            int count = counts[i];
            Logic l = logicResolver.getLogic(logicIds[i]);
            if (l == null) {
                throw new IllegalArgumentException(LOGIC_NOT_FOUND_ERROR + logicIds[i]);
            }
            for (int loop = 0; loop < count; loop++) {
                sprites[offset++].logic = l;
            }
        }

    }

}
