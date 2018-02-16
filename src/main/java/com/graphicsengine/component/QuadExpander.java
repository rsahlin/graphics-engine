package com.graphicsengine.component;

import com.graphicsengine.spritemesh.SpriteMesh;
import com.graphicsengine.spritemesh.TiledSpriteProgram;
import com.nucleus.component.AttributeExpander;
import com.nucleus.component.ComponentBuffer;
import com.nucleus.geometry.AttributeUpdater.PropertyMapper;
import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.shader.QuadExpanderShader;
import com.nucleus.texturing.Texture2D;
import com.nucleus.texturing.TextureType;
import com.nucleus.texturing.UVAtlas;
import com.nucleus.texturing.UVTexture2D;

/**
 * Sprite / Quad expander, same as AttributeExpander but adds methods for setting frame / color
 *
 */
public class QuadExpander extends AttributeExpander {

    protected static QuadExpanderShader expanderShader;

    private transient Texture2D texture;
    private transient float[][] uvData;
    private transient float[] entityData;

    /**
     * 
     * @param spriteMesh
     * @param mapper
     * @param data
     */
    public QuadExpander(SpriteMesh spriteMesh, PropertyMapper mapper, ComponentBuffer data) {
        super(mapper, data, 4);
        this.texture = spriteMesh.getTexture(Texture2D.TEXTURE_0);
        if (texture.getTextureType() == TextureType.UVTexture2D) {
            copyUVAtlas(((UVTexture2D) texture).getUVAtlas());
            entityData = new float[mapper.attributesPerVertex];
        }
        expanderShader = ((TiledSpriteProgram) spriteMesh.getMaterial().getProgram()).getExpanderShader();
    }

    private void copyUVAtlas(UVAtlas uvAtlas) {
        int frames = uvAtlas.getFrameCount();
        uvData = new float[frames][];
        for (int i = 0; i < frames; i++) {
            uvData[i] = new float[8];
            uvAtlas.getUVFrame(i, uvData[i], 0);
        }
    }

    @Override
    public void updateAttributeData(NucleusRenderer renderer) {
        if (texture.getTextureType() == TextureType.UVTexture2D) {
            int uvIndex = 0;
            int frame;
            buffer.getBuffer().position(0);
            float[] uv = new float[8];
            for (int i = 0; i < data.getSizePerEntity(); i++) {
                uvIndex = 0;
                data.get(i, entityData);
                frame = (int) entityData[mapper.frameOffset];
                for (int expand = 0; expand < multiplier; expand++) {
                    // Store the UV for the vertex
                    data.put(i, mapper.frameOffset, uvData[frame], uvIndex, 2);
                    data.get(i, buffer);
                    uvIndex += 2;
                }
            }
            buffer.setDirty(true);
        } else {
            super.updateAttributeData(renderer);
        }
    }

    public void setColor(int quad, float[] color) {
        data.put(quad, mapper.colorOffset, color, 0, 4);
    }

    public void setFrame(int quad, int frame) {
        if (texture.textureType == TextureType.TiledTexture2D) {
            // TODO - this is highly unoptimized
            data.put(quad, mapper.frameOffset, new float[] { frame }, 0, 1);
        } else if (texture.textureType == TextureType.UVTexture2D) {
            // DO nothing
        }
    }

}
