package com.graphicsengine.tiledsprite;

import com.graphicsengine.charset.TiledSetup;
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
    TiledSetup tiledSetup;

    /**
     * Sets the tiled setup, as needed by when creating a tiled sprite controller (mesh)
     * 
     * @param tiledSetup
     */
    public void setTiledSetup(TiledSetup tiledSetup) {
        this.tiledSetup = tiledSetup;
    }

    /**
     * Returns the tiled setup, needed when creating a tiled sprite controller.
     * 
     * @return
     */
    public TiledSetup getTiledSetup() {
        return tiledSetup;
    }

}
