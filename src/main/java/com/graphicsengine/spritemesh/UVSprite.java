package com.graphicsengine.spritemesh;

import com.nucleus.texturing.UVTexture2D;

/**
 * A sprite that uses the {@link UVTexture2D}, otherwise same behavior as {@link TiledSprite}
 * 
 * @author Richard Sahlin
 *
 */
public class UVSprite extends TiledSprite {

    /**
     * Creates a new sprite, using attribute data at the specified offset.
     * This constructor shall not be called directly, use TiledSpriteController to create sprites.
     * 
     * @param data Shared attribute data for positions
     * @param offset Offset into array where data for this sprite is.
     */
    UVSprite(float[] data, int offset) {
        super(data, offset);
    }

    @Override
    public void setFrame(int frame) {
        super.setFrame(frame);
    }

}
