package com.graphicsengine.tiledsprite;

import com.graphicsengine.charset.TiledSheetSetup;
import com.graphicsengine.sprite.SpriteControllerSetup;

/**
 * The data for tiled sprite (controller) setup.
 * This class can be used with serialization to decouple io from implementation
 * 
 * @author Richard Sahlin
 *
 */
public class TiledSpriteSetup extends SpriteControllerSetup {

    /**
     * Ref to tiled setup, as needed by factory.
     */
    TiledSheetSetup tiledSetup;

    public TiledSpriteSetup() {
        super();
    }

    public TiledSpriteSetup(TiledSpriteController spriteController) {
        super(spriteController);
    }

    /**
     * Sets the tiled setup, as needed by when creating a tiled sprite controller (mesh)
     * 
     * @param tiledSetup
     */
    public void setTiledSetup(TiledSheetSetup tiledSetup) {
        this.tiledSetup = tiledSetup;
    }

    /**
     * Returns the tiled setup, needed when creating a tiled sprite controller.
     * 
     * @return
     */
    public TiledSheetSetup getTiledSetup() {
        return tiledSetup;
    }

}
