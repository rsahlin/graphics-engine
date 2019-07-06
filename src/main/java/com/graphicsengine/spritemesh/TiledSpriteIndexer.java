package com.graphicsengine.spritemesh;

import com.nucleus.geometry.AttributeUpdater.BufferIndex;
import com.nucleus.opengl.shader.Indexer;
import com.nucleus.opengl.shader.VariableIndexer;
import com.nucleus.shader.ShaderVariable.VariableType;

/**
 * Layout for the data needed by the tiled sprite program
 * 
 *
 */
public class TiledSpriteIndexer extends VariableIndexer {

    protected final static String[] NAMES = new String[] { Indexer.Property.VERTEX.name,
            Indexer.Property.UV.name, Indexer.Property.TRANSLATE.name,
            Indexer.Property.ROTATE.name, Indexer.Property.SCALE.name,
            Indexer.Property.ALBEDO.name, Indexer.Property.FRAME.name };
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
