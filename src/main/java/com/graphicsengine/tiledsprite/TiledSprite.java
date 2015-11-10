package com.graphicsengine.tiledsprite;

import com.graphicsengine.sprite.Sprite;
import com.nucleus.geometry.MeshBuilder;

/**
 * A tiled sprite object, this is a type of sprite that uses one Mesh (drawcall) to draw all sprites.
 * It is created by the TiledSpriteController.
 * 
 * @author Richard Sahlin
 *
 */
public class TiledSprite extends Sprite {

    /**
     * Ref to sprite data, use with offset.
     * This sprites data is only one part of the whole array.
     */
    float[] attributeData;
    int offset;

    /**
     * Creates a new TiledSprite, using attribute data at the specified offset.
     * This constructor shall not be called directly, use TiledSpriteController to create sprites.
     * 
     * @param data Shared attribute data for positions
     * @param offset Offset into array where data for this sprite is.
     */
    TiledSprite(float[] data, int offset) {
        this.attributeData = data;
        this.offset = offset;
        MeshBuilder.prepareTiledUV(attributeData, offset, TiledSpriteProgram.ATTRIBUTE_SPRITE_U_INDEX,
                TiledSpriteProgram.ATTRIBUTE_SPRITE_V_INDEX, TiledSpriteProgram.ATTRIBUTES_PER_VERTEX);
    }

    /**
     * Internal method.
     * Stores the position and data of this sprite into the attribute array (in the Mesh) used when rendering this
     * sprite. This must be called before this sprite is updated on screen.
     * 
     */
    @Override
    public void prepare() {
        float xpos = floatData[X_POS];
        float ypos = floatData[Y_POS];
        float zpos = floatData[Z_POS];
        int index = offset;
        int frameIndex = (int) floatData[FRAME];
        float rotation = floatData[ROTATION];

        for (int i = 0; i < TiledSpriteProgram.VERTICES_PER_SPRITE; i++) {
            attributeData[index + TiledSpriteProgram.ATTRIBUTE_SPRITE_X_INDEX] = xpos;
            attributeData[index + TiledSpriteProgram.ATTRIBUTE_SPRITE_Y_INDEX] = ypos;
            attributeData[index + TiledSpriteProgram.ATTRIBUTE_SPRITE_Z_INDEX] = zpos;
            attributeData[index + TiledSpriteProgram.ATTRIBUTE_SPRITE_FRAME_INDEX] = frameIndex;
            attributeData[index + TiledSpriteProgram.ATTRIBUTE_SPRITE_ROTATION_INDEX] = rotation;
            index += TiledSpriteProgram.ATTRIBUTES_PER_VERTEX;
        }

    }

}
