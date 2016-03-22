package com.graphicsengine.spritemesh;

import com.graphicsengine.sprite.SpriteFactory;
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
     * Do not create sprites directly, use {@link SpriteFactory}
     */
    public UVSprite() {
    }

    @Override
    protected void setup(Node parent, PropertyMapper mapper, float[] data, int index) {
        super.setup(parent, mapper, data, index);
        Texture2D tex = ((SpriteMeshNode) parent).getMeshById(parent.getMeshRef()).getTexture(Texture2D.TEXTURE_0);
        if (tex.type == TextureType.UVTexture2D) {
            uvAtlas = ((UVTexture2D) tex).getUVAtlas();
        }
    }

    @Override
    public void setFrame(int frame) {
        // TODO Not necessary to call super, does UVSprite really need the framenumber?
        super.setFrame(frame);
        int index = offset;
        int readIndex = 0;
        uvAtlas.getUVFrame(frame, frames, 0);
        for (int i = 0; i < ShaderProgram.VERTICES_PER_SPRITE; i++) {
            attributeData[index + mapper.UV_INDEX] = frames[readIndex++];
            attributeData[index + mapper.UV_INDEX + 1] = frames[readIndex++];
            index += mapper.ATTRIBUTES_PER_VERTEX;
        }
    }

    @Override
    public void updateAttributeData() {
        super.updateAttributeData();
    }
}
