package com.graphicsengine.component;

import java.io.IOException;

import com.graphicsengine.spritemesh.SpriteGeometryMesh;
import com.nucleus.component.Component;
import com.nucleus.component.ComponentBuffer;
import com.nucleus.geometry.AttributeBuffer;
import com.nucleus.geometry.Mesh.Builder;
import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.scene.ComponentNode;
import com.nucleus.system.System;
import com.nucleus.vecmath.Rectangle;

public class SpriteComponent extends ActorComponent<SpriteGeometryMesh> {

    @Override
    public void updateAttributeData(NucleusRenderer renderer) {
        // TODO Auto-generated method stub

    }

    @Override
    public void bindAttributeBuffer(AttributeBuffer buffer) {
        // TODO Auto-generated method stub

    }

    @Override
    public ComponentBuffer getEntityBuffer() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setEntityData(int entity, int destOffset, float[] data) {
        // TODO Auto-generated method stub

    }

    @Override
    public Component createInstance() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Builder<SpriteGeometryMesh> createMeshBuilder(NucleusRenderer renderer, ComponentNode parent, int count,
            Rectangle rectangle) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected void createBuffers(System system) {
        // TODO Auto-generated method stub

    }

}
