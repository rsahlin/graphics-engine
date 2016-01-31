package com.graphicsengine.spritemesh;

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
    float[] frame = new float[2 * 4];

    /**
     * Creates a new sprite, using attribute data at the specified offset.
     * This constructor shall not be called directly, use TiledSpriteController to create sprites.
     * 
     * @param parrent The node containing the sprites
     * @param data Shared attribute data for positions
     * @param offset Offset into array where data for this sprite is.
     */
    UVSprite(Node parent, float[] data, int offset) {
        super(parent, data, offset);
        Texture2D tex = ((SpriteMeshNode) parent).getSpriteSheet().getTexture(Texture2D.TEXTURE_0);
        if (tex.type == TextureType.UVTexture2D) {
            uvAtlas = ((UVTexture2D) tex).getUVAtlas();
        }

    }

    @Override
    public void setFrame(int frame) {
        super.setFrame(frame);
    }

    @Override
    public void prepare() {
        super.prepare();
        int index = offset;
        int frameIndex = (int) floatData[FRAME];
        int readIndex = 0;
        uvAtlas.getUVFrame(frameIndex, frame, 0);
        for (int i = 0; i < ShaderProgram.VERTICES_PER_SPRITE; i++) {
            attributeData[index + TiledSpriteProgram.ATTRIBUTE_SPRITE_FRAMEDATA] = frame[readIndex++];
            attributeData[index + TiledSpriteProgram.ATTRIBUTE_SPRITE_FRAMEDATA + 1] = frame[readIndex++];
            index += TiledSpriteProgram.ATTRIBUTES_PER_VERTEX;
        }

    }
}
