package com.graphicsengine.spritemesh;

import java.nio.FloatBuffer;

import com.nucleus.opengl.shader.BlockBuffer;
import com.nucleus.opengl.shader.CommonBlockNames;
import com.nucleus.opengl.shader.FloatBlockBuffer;
import com.nucleus.shader.Shader.Shading;
import com.nucleus.texturing.UVTexture2D;

/**
 * This class defines the mapping for the UV sprite vertex and fragment shaders.
 * This program has support for a number of sprites with frames defined by UV coordinates for each sprite corner,
 * this means that the sprites can have different sizes.
 * 
 * This shader program can only be used with UVTexture2D texture objects.
 * 
 * @author Richard Sahlin
 *
 */
public class UVSpriteProgram extends TiledSpriteProgram {

    /**
     * This uses gles 20 - deprecated in favor of geometry shader
     */
    protected static final String CATEGORY = "uvsprite20";

    transient protected boolean initialized = false;
    transient protected FloatBlockBuffer uvData;

    public UVSpriteProgram(UVTexture2D uvTexture) {
        super(null, Shading.textured, CATEGORY);
        uvData = uvTexture.getUVAtlasBuffer();
    }

    @Override
    public void initUniformData(FloatBuffer destinationUniforms) {
        setUVData(uvData);
    }

    @Override
    public void updateUniformData(FloatBuffer destinationUniform) {
        super.updateUniformData(destinationUniform);
    }

    protected void setUVData(FloatBlockBuffer source) {
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
