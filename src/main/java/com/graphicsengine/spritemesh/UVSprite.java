package com.graphicsengine.spritemesh;

import com.nucleus.geometry.AttributeUpdater.PropertyMapper;
import com.nucleus.scene.Node;
import com.nucleus.shader.ShaderProgram;
import com.nucleus.texturing.Texture2D;
import com.nucleus.texturing.TextureType;
import com.nucleus.texturing.UVAtlas;
import com.nucleus.texturing.UVTexture2D;

/**
 * A sprite that uses the {@link UVTexture2D}, otherwise same behavior as {@link TiledSprite}
 * 
 * @author Richard Sahlin
 *
 */
public class UVSprite extends TiledSprite {

    UVAtlas uvAtlas;
    /**
     * Storage for 4 UV components
     */
    float[] frames = new float[2 * 4];

    /**
     * Creates a new sprite, using attribute data at the specified offset.
     * This constructor shall not be called directly, use TiledSpriteController to create sprites.
     * 
     * @param parrent The node containing the sprites
     * @param mapper Attribute property mapper for the property indexes
     * @param data Shared attribute data for positions
     * @param offset Offset into array where data for this sprite is.
     */
    UVSprite(Node parent, PropertyMapper mapper, float[] data, int offset) {
        super(parent, mapper, data, offset);
        Texture2D tex = ((SpriteMeshNode) parent).getSpriteSheet().getTexture(Texture2D.TEXTURE_0);
        if (tex.type == TextureType.UVTexture2D) {
            uvAtlas = ((UVTexture2D) tex).getUVAtlas();
        }

    }

    @Override
    public void setFrame(int frame) {
        super.setFrame(frame);
        int index = offset;
        int readIndex = 0;
        uvAtlas.getUVFrame(frame, frames, 0);
        for (int i = 0; i < ShaderProgram.VERTICES_PER_SPRITE; i++) {
            attributeData[index + mapper.FRAME_INDEX] = frames[readIndex++];
            attributeData[index + mapper.FRAME_INDEX + 1] = frames[readIndex++];
            index += mapper.ATTRIBUTES_PER_VERTEX;
        }
    }

    @Override
    public void updateAttributeData() {
        super.updateAttributeData();
    }
}
