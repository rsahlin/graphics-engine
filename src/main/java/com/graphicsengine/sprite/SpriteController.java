package com.graphicsengine.sprite;

import com.google.gson.annotations.SerializedName;
import com.nucleus.logic.ActorContainer;
import com.nucleus.logic.ActorItem;
import com.nucleus.logic.ActorNode;
import com.nucleus.logic.ActorResolver;
import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.scene.NodeData;
import com.nucleus.scene.RootNode;

/**
 * Controller for a set of sprites.
 * This can be added as a node to the scenegraph.
 * This class can be serialized using GSON
 * 
 * @author Richard Sahlin
 *
 */
public abstract class SpriteController extends ActorNode {

    private final static String LOGICRESOLVER_NOT_SET = "LogicResolver not set, must set before calling.";
    private final static String LOGIC_NOT_FOUND_ERROR = "Logic not found for id: ";

    /**
     * The data used to create the logic
     */
    @SerializedName("logicdata")
    LogicData logicdata;

    transient protected Sprite[] sprites;
    transient protected int count;
    transient protected ActorResolver logicResolver;

    /**
     * Default constructor
     */
    protected SpriteController() {
        super();
    }

    protected SpriteController(SpriteController source) {
        set(source);
    }

    /**
     * Sets the data in this class from the source, do not set the transient values
     * This is used when importing.
     * 
     * @param source The source to copy
     */
    protected void set(SpriteController source) {
        super.set(source);
        logicdata = new LogicData(source.getLogicData());
    }

    /**
     * Creates a TiledSpriteController with an array of the specified size.
     * Each sprite must be created by calling createSprites()
     * 
     * @param count Number of sprites to create.
     * 
     */
    protected void create(int count) {
        this.count = count;
        sprites = new Sprite[count];
    }

    /**
     * Returns the logic data, this is used when importing and exporting
     * 
     * @return
     */
    public LogicData getLogicData() {
        return logicdata;
    }

    @Override
    public ActorContainer[] getLogicContainer() {
        return sprites;
    }

    /**
     * Adds a resolver to find implementing logic (sprite) classes from ids.
     * This shall be done programatically (from the code) to avoid binding classname to the leveldata, as this will
     * prevent proper obfuscation of the code.
     * 
     * @param resolver The resolver to add, used when createSprites() is called.
     */
    public void setLogicResolver(ActorResolver resolver) {
        logicResolver = resolver;
    }

    /**
     * Creates the logic sprites, this will only create the logic and data for the logic it will not create the
     * mesh/textures
     * 
     * @see #createMesh(NucleusRenderer, NodeData, RootNode)
     * 
     * @param renderer
     * @param source
     * @param scene
     */
    public abstract void createSprites(NucleusRenderer renderer, SpriteController source,
            RootNode scene);

    /**
     * Creates the renderable sprite (mesh)
     * After this call this node can be rendered
     * 
     * @param renderer
     * @param spriteController
     * @param scene
     */
    public abstract void createMesh(NucleusRenderer renderer, SpriteController spriteController, RootNode scene);

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
            ActorItem l = logicResolver.getLogic(logicIds[i]);
            if (l == null) {
                throw new IllegalArgumentException(LOGIC_NOT_FOUND_ERROR + logicIds[i]);
            }
            for (int loop = 0; loop < count; loop++) {
                sprites[offset++].logic = l;
            }
        }
    }

    /**
     * Sets the sprite logic data, the logic type will be set at the index and count.
     * 
     * @param logic
     */
    public void setLogic(LogicArray logic) {
        ActorItem l = null;
        String classname = logic.getClassName();
        if (classname != null) {
            try {
                l = (ActorItem) Class.forName(classname).newInstance();
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                e.printStackTrace();
                // Cannot recover
                throw new RuntimeException(e);
            }
        } else {
            if (logicResolver == null) {
                throw new IllegalArgumentException(LOGICRESOLVER_NOT_SET);
            }
            l = logicResolver.getLogic(logic.getInstance());
            if (l == null) {
                throw new IllegalArgumentException(LOGIC_NOT_FOUND_ERROR + logic.getInstance());
            }
        }
        int offset = logic.getIndex();
        int count = logic.getCount();
        if (count == -1) {
            count = sprites.length - offset;
        }
        for (int loop = 0; loop < count; loop++) {
            sprites[offset++].logic = l;
        }
    }

    /**
     * Sets the initial logic for the sprites, this will unpack the values in the logic array and set the correct logic
     * object in the sprites.
     * The sprites must be created before calling this method.
     * 
     * @param logic
     */
    public void setLogic(LogicArray[] logic) {
        for (LogicArray data : logic) {
            setLogic(data);
        }
    }

}
