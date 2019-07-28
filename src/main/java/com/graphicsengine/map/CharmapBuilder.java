package com.graphicsengine.map;

import com.nucleus.geometry.Mesh;
import com.nucleus.geometry.shape.RectangleShapeBuilder;
import com.nucleus.shader.GraphicsShader;

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
    public void build(Mesh mesh, GraphicsShader program) {
        super.build(mesh, program);
        ((PlayfieldMesh) mesh).setupCharmap(program.getFunction().getIndexer(), configuration.getRectangle().getSize(),
                offset);
    }

}
