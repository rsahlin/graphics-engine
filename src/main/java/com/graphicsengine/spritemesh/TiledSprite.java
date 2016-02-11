package com.graphicsengine.spritemesh;

import com.graphicsengine.sprite.Sprite;
import com.nucleus.geometry.AttributeUpdater.PropertyMapper;
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
     * Creates a new TiledSprite, using attribute data at the specified offset.
     * This constructor shall not be called directly, use TiledSpriteController to create sprites.
     * 
     * @param parent The node containing the sprites
     * @param mapper The attribute property mappings
     * @param data Shared attribute data for positions
     * @param offset Offset into array where data for this sprite is.
     */
    TiledSprite(Node parent, PropertyMapper mapper, float[] data, int offset) {
        super(parent, mapper, data, offset);
    }

    @Override
    public void updateAttributeData() {
        // TODO This the attributeData mapping should match the mapping in the sprite object so that only
        // one copy is needed.
        float xpos = floatData[X_POS];
        float ypos = floatData[Y_POS];
        float zpos = floatData[Z_POS];
        float rotate = floatData[ROTATION];
        int index = offset;
        for (int i = 0; i < ShaderProgram.VERTICES_PER_SPRITE; i++) {
            attributeData[index + mapper.TRANSLATE_INDEX] = xpos;
            attributeData[index + mapper.TRANSLATE_INDEX + 1] = ypos;
            attributeData[index + mapper.TRANSLATE_INDEX + 2] = zpos;
            attributeData[index + mapper.ROTATE_INDEX + 2] = rotate;
            index += mapper.ATTRIBUTES_PER_VERTEX;
        }
    }

}
