package com.graphicsengine.map;

import com.nucleus.geometry.Mesh;
import com.nucleus.geometry.shape.RectangleShapeBuilder;
import com.nucleus.shader.ShaderProperty.PropertyMapper;

/**
 * Special shapebuilder for playfield charmap - will also setup the charmap.
 * Only use for {@link PlayfieldMesh} building.
 * 
 *
 */
public class CharmapBuilder extends RectangleShapeBuilder {

    protected PropertyMapper mapper;
    protected float[] offset;

    public CharmapBuilder(RectangleConfiguration configuration, PropertyMapper mapper, float[] offset) {
        super(configuration);
        this.mapper = mapper;
        this.offset = offset;

    }

    @Override
    public void build(Mesh mesh) {
        super.build(mesh);
        ((PlayfieldMesh) mesh).setupCharmap(mapper, configuration.getRectangle().getSize(), offset);
    }

}