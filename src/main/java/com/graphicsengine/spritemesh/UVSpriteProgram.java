package com.graphicsengine.spritemesh;

import com.nucleus.geometry.Mesh;
import com.nucleus.shader.BlockBuffer;
import com.nucleus.shader.CommonBlockNames;
import com.nucleus.shader.FloatBlockBuffer;
import com.nucleus.texturing.Texture2D;
import com.nucleus.texturing.UVTexture2D;

/**
 * This class defines the mapping for the UV sprite vertex and fragment shaders.
 * This program has support for a number of sprites with frames defined by UV coordinates for each sprite corner,
 * this means that the sprites can have different sizes.
 * 
 * @author Richard Sahlin
 *
 */
public class UVSpriteProgram extends TiledSpriteProgram {

    protected static final String CATEGORY = "uvsprite";

    transient protected boolean initialized = false;

    public UVSpriteProgram() {
        super(null, Texture2D.Shading.textured, CATEGORY);
    }

    @Override
    public void updateUniformData(float[] destinationUniform, Mesh mesh) {
        if (!initialized) {
            initBuffers(mesh);
            initialized = true;
        }
        super.updateUniformData(destinationUniform, mesh);
    }

    @Override
    public void initBuffers(Mesh mesh) {
        BlockBuffer[] blocks = uniformBlockBuffers;
        if (blocks != null) {
            for (BlockBuffer bb : blocks) {
                CommonBlockNames blockName = CommonBlockNames.valueOf(bb.getBlockName());
                switch (blockName) {
                    case UVData:
                        /**
                         * Currently copies data into mesh block storage - could be shared if read only, which is the
                         * case for uv data
                         * TODO Share buffer from uvtexture instead of allocating and copying. UVTexture already holds
                         * native buffer with uvdata
                         */
                        bb.position(0);
                        FloatBlockBuffer source = ((UVTexture2D) mesh.getTexture(Texture2D.TEXTURE_0))
                                .getUVAtlasBuffer();
                        source.position(0);
                        float[] data = new float[source.capacity()];
                        source.get(data, 0, data.length);
                        ((FloatBlockBuffer) bb).put(data, 0, data.length);
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown variable block " + blockName);
                }
            }
        }
    }

}
