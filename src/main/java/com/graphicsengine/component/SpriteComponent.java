package com.graphicsengine.component;

import java.io.IOException;

import com.graphicsengine.spritemesh.SpriteGeometryMesh;
import com.nucleus.component.CPUComponentBuffer;
import com.nucleus.component.Component;
import com.nucleus.component.ComponentBuffer;
import com.nucleus.geometry.AttributeBuffer;
import com.nucleus.geometry.Material;
import com.nucleus.geometry.Mesh;
import com.nucleus.geometry.shape.ShapeBuilder;
import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.scene.ComponentNode;
import com.nucleus.texturing.Texture2D;
import com.nucleus.texturing.TextureType;

public class SpriteComponent extends ActorComponent<SpriteGeometryMesh> {

    @Override
    public Component createInstance() {
        return new SpriteComponent();
    }

    @Override
    public void set(Component source) {
        set((SpriteComponent) source);
    }

    private void set(SpriteComponent source) {
        super.set(source);
    }

    @Override
    protected void createBuffers(com.nucleus.system.System system) {
        CPUComponentBuffer entityData = new CPUComponentBuffer(count,
                system.getEntityDataSize() + mapper.attributesPerVertex);
        addBuffer(0, entityData);
    }

    @Override
    public void setEntityData(int sprite, int destOffset, float[] data) {
        ComponentBuffer entityBuffer = getEntityBuffer();
        entityBuffer.put(sprite, destOffset, data, 0, data.length);
    }

    @Override
    public ComponentBuffer getEntityBuffer() {
        return getBuffer(0);
    }

    @Override
    public void bindAttributeBuffer(AttributeBuffer buffer) {
    }

    @Override
    public void updateAttributeData(NucleusRenderer renderer) {
    }

    @Override
    public Mesh.Builder<SpriteGeometryMesh> createMeshBuilder(NucleusRenderer renderer,
            ComponentNode parent, int count,
            ShapeBuilder shapeBuilder) throws IOException {
        SpriteGeometryMesh.Builder spriteBuilder = SpriteGeometryMesh.Builder.createBuilder(renderer);
        spriteBuilder.setTexture(parent.getTextureRef());
        spriteBuilder.setMaterial(parent.getMaterial() != null ? parent.getMaterial() : new Material());
        spriteBuilder.setObjectCount(count).setShapeBuilder(shapeBuilder);
        return spriteBuilder;
    }

    /**
     * Returns the texture type used for this component.
     * TODO: Shall this be stored as a Component enum instead?
     * 
     * @return Type of texture used
     */
    public TextureType getTextureType() {
        return textureType;
    }

    /**
     * Returns the number of frames available in the texture
     * 
     * @return
     */
    public int getFrameCount() {
        return mesh.getTexture(Texture2D.TEXTURE_0).getFrameCount();
    }

    @Override
    public void setActor(int actor, float[] data) {
    }

}
