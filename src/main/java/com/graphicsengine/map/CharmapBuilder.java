package com.graphicsengine.map;

import com.nucleus.geometry.Mesh;
import com.nucleus.geometry.shape.RectangleShapeBuilder;
import com.nucleus.shader.VariableIndexer.Indexer;

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
    public void build(Mesh mesh) {
        super.build(mesh);
        // ((PlayfieldMesh) mesh).setupCharmap(mapper, configuration.getRectangle().getSize(), offset);
        throw new IllegalArgumentException("Not implemented");
    }

}
