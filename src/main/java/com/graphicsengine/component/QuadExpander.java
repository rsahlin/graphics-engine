package com.graphicsengine.component;

import com.graphicsengine.spritemesh.SpriteMesh;
import com.nucleus.common.Constants;
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
    /**
     * Only used if uniform block no uniform block in shader
     */
    private transient float[][] uvData;
    /**
     * Only used if uniform block no uniform block in shader
     */
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
            // If mesh has block buffers then frames will be in uniform block - do not copy here
            if (spriteMesh.getBlockBuffers() == null) {
                copyUVAtlas(((UVTexture2D) texture).getUVAtlas());
                entityData = new float[mapper.attributesPerVertex];
            }
        }
        // expanderShader = ((TiledSpriteProgram) spriteMesh.getMaterial().getProgram()).getExpanderShader();
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
        // Use special case if shader does not support uniform block where the frames are.
        if (texture.getTextureType() == TextureType.UVTexture2D && entityData != null) {
            int uvIndex = 0;
            int frame;
            buffer.setBufferPosition(0);
            for (int i = 0; i < data.getSizePerEntity(); i++) {
                uvIndex = 0;
                frame = (int) entityData[mapper.frameOffset];
                data.get(i, entityData);
                for (int expand = 0; expand < multiplier; expand++) {
                    // Store the UV for the vertex
                    entityData[mapper.frameOffset] = uvData[frame][uvIndex++];
                    entityData[mapper.frameOffset + 1] = uvData[frame][uvIndex++];
                    buffer.put(entityData);
                }
            }
        } else {
            super.updateAttributeData(renderer);
        }
    }

    public void setColor(int quad, float[] color) {
        data.put(quad, mapper.colorOffset, color, 0, 4);
    }

    public void setFrame(int quad, int frame) {
        if (mapper.frameOffset != Constants.NO_VALUE) {
            data.put(quad, mapper.frameOffset, new float[] { frame }, 0, 1);
        }
    }

}
