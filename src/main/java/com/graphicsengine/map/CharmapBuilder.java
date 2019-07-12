package com.graphicsengine.map;

import com.nucleus.GraphicsPipeline;
import com.nucleus.geometry.Mesh;
import com.nucleus.geometry.shape.RectangleShapeBuilder;

/**
 * Special shapebuilder for playfield charmap - will also setup the charmap.
 * Only use for {@link PlayfieldMesh} building.
 * 
 *
 */
public class CharmapBuilder extends RectangleShapeBuilder {

    protected float[] offset;

    public CharmapBuilder(RectangleConfiguration configuration, float[] offset) {
        super(configuration);
        this.offset = offset;
    }

    @Override
    public void build(Mesh mesh, GraphicsPipeline pipeline) {
        super.build(mesh, pipeline);
        ((PlayfieldMesh) mesh).setupCharmap(pipeline.getLocationMapping(), configuration.getRectangle().getSize(),
                offset);
    }

}
