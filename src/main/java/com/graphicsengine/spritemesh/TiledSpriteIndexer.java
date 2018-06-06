package com.graphicsengine.spritemesh;

import com.nucleus.geometry.Mesh.BufferIndex;
import com.nucleus.shader.ShaderVariable.VariableType;
import com.nucleus.shader.VariableIndexer;

/**
 * Layout for the data needed by the tiled sprite program
 * 
 *
 */
public class TiledSpriteIndexer extends VariableIndexer {

    protected final static String[] NAMES = new String[] { "aVertex", "aTexCoord", "aTranslate", "aRotate", "aScale",
            "aColor", "aFrameData" };
    protected final static int[] OFFSETS = new int[] { 0, 4, 0, 3, 6, 9, 12 };
    protected final static VariableType[] TYPES = new VariableType[] { VariableType.ATTRIBUTE, VariableType.ATTRIBUTE,
            VariableType.ATTRIBUTE, VariableType.ATTRIBUTE, VariableType.ATTRIBUTE, VariableType.ATTRIBUTE,
            VariableType.ATTRIBUTE };
    protected final static BufferIndex[] BUFFERINDEXES = new BufferIndex[] { BufferIndex.ATTRIBUTES_STATIC,
            BufferIndex.ATTRIBUTES_STATIC, BufferIndex.ATTRIBUTES, BufferIndex.ATTRIBUTES, BufferIndex.ATTRIBUTES,
            BufferIndex.ATTRIBUTES, BufferIndex.ATTRIBUTES };
    protected final static int[] SIZEPERVERTEX = new int[] { 18, 6 };

    public TiledSpriteIndexer() {
        super(NAMES, OFFSETS, TYPES, BUFFERINDEXES, SIZEPERVERTEX);
    }

}
