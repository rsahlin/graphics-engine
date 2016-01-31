package com.graphicsengine.spritemesh;

import com.graphicsengine.sprite.Sprite;
import com.nucleus.scene.Node;
import com.nucleus.shader.ShaderProgram;
import com.nucleus.texturing.TiledTexture2D;
import com.nucleus.texturing.UVTexture2D;

/**
 * A tiled sprite object, this is a type of sprite that uses one Mesh (drawcall) to draw all sprites.
 * The sprite frame is chosen depending on the texture type:
 * For {@link TiledTexture2D} the frame is calculated by the shader.
 * For {@link UVTexture2D} the UV data must be set.
 * It is created by the {@link SpriteMeshNode}.
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
     * @param parent The node containing the sprites
     * @param data Shared attribute data for positions
     * @param offset Offset into array where data for this sprite is.
     */
    TiledSprite(Node parent, float[] data, int offset) {
        super(parent);
        this.attributeData = data;
        this.offset = offset;
    }

    @Override
    public void prepare() {
        float xpos = floatData[X_POS];
        float ypos = floatData[Y_POS];
        float zpos = floatData[Z_POS];
        int index = offset;
        int frameIndex = (int) floatData[FRAME];
        float rotation = floatData[ROTATION];
        float scale = floatData[SCALE]; // Uniform scale

        for (int i = 0; i < ShaderProgram.VERTICES_PER_SPRITE; i++) {
            attributeData[index + TiledSpriteProgram.ATTRIBUTE_SPRITE_TRANSLATE_INDEX] = xpos;
            attributeData[index + TiledSpriteProgram.ATTRIBUTE_SPRITE_TRANSLATE_INDEX + 1] = ypos;
            attributeData[index + TiledSpriteProgram.ATTRIBUTE_SPRITE_TRANSLATE_INDEX + 2] = zpos;
            attributeData[index + TiledSpriteProgram.ATTRIBUTE_SPRITE_FRAMEDATA + 2] = frameIndex;
            attributeData[index + TiledSpriteProgram.ATTRIBUTE_SPRITE_ROTATION_INDEX + 2] = rotation;
            attributeData[index + TiledSpriteProgram.ATTRIBUTE_SPRITE_SCALE_INDEX] = scale;
            attributeData[index + TiledSpriteProgram.ATTRIBUTE_SPRITE_SCALE_INDEX + 1] = scale;
            index += TiledSpriteProgram.ATTRIBUTES_PER_VERTEX;
        }
    }

}
