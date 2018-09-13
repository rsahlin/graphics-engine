package com.graphicsengine.map;

import com.nucleus.geometry.AttributeBuffer;
import com.nucleus.geometry.ElementBuffer;
import com.nucleus.geometry.Mesh.Mode;
import com.nucleus.geometry.shape.RectangleShapeBuilder;
import com.nucleus.shader.VariableIndexer.Indexer;
import com.nucleus.texturing.Texture2D;

/**
 * Special shapebuilder for playfield charmap - will also setup the charmap.
 * Only use for {@link PlayfieldMesh} building.
 * 
 *
 */
public class CharmapBuilder extends RectangleShapeBuilder {

    protected Indexer mapper;
    protected float[] offset;

    public CharmapBuilder(RectangleConfiguration configuration, Indexer mapper, float[] offset) {
        super(configuration);
        this.mapper = mapper;
        this.offset = offset;
    }

    @Override
    public void build(AttributeBuffer attributes, Texture2D texture, ElementBuffer indices, Mode mode) {
       super.build(attributes, texture, indices, mode);
//        ((PlayfieldMesh) mesh).setupCharmap(mapper, configuration.getRectangle().getSize(), offset);
       throw new IllegalArgumentException("Not implemented");
    }

}
