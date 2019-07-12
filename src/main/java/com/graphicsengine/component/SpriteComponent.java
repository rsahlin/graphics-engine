package com.graphicsengine.component;

import com.graphicsengine.component.ActorComponent.EntityData;
import com.graphicsengine.spritemesh.SpriteGeometryMesh;
import com.nucleus.component.CPUComponentBuffer;
import com.nucleus.component.Component;
import com.nucleus.component.ComponentBuffer;
import com.nucleus.geometry.AttributeBuffer;
import com.nucleus.geometry.AttributeUpdater.BufferIndex;
import com.nucleus.geometry.Mesh;
import com.nucleus.geometry.MeshBuilder;
import com.nucleus.geometry.shape.ShapeBuilder;
import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.shader.VariableIndexer;
import com.nucleus.texturing.Texture2D;
import com.nucleus.texturing.TextureType;

/**
 * Sprite component, this is a collection of a number of (similar) sprite objects that have entity (actor) data and can
 * be rendered using one draw call.
 * Target usage is geometry shaders (GLES 3.2) or compute shaders (GLES 3.1)
 * 
 * The class can be serialized using gson
 * 
 * @author Richard Sahlin
 *
 */
public class SpriteComponent extends ActorComponent<SpriteGeometryMesh> implements EntityData {

    transient protected AttributeBuffer attributes;
    transient protected CPUComponentBuffer entityData;
    transient protected CPUComponentBuffer spriteData;
    transient protected VariableIndexer mapper;

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
    protected void createBuffers(VariableIndexer mapper) {
        int size = mapper.getSizePerVertex(BufferIndex.ATTRIBUTES.index);
        spriteData = new CPUComponentBuffer(count, size);
        entityData = new CPUComponentBuffer(count, size + ActorVariables.SIZE.offset);
        this.mapper = mapper;
    }

    @Override
    public ComponentBuffer getEntityBuffer() {
        return entityData;
    }

    @Override
    public void bindAttributeBuffer(AttributeBuffer buffer) {
        this.attributes = buffer;
    }

    @Override
    public void updateAttributeData(NucleusRenderer renderer) {
        attributes.setBufferPosition(0);
        attributes.put(spriteData.getData());
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
    protected MeshBuilder<Mesh> createBuilderInstance(NucleusRenderer renderer) {
        return new SpriteGeometryMesh.Builder(renderer);
    }

    @Override
    protected ShapeBuilder createShapeBuilder() {
        // We are using point mode - do not create shapebuilder
        return null;
    }

    @Override
    public void setEntity(int entity, int entityOffset, float[] data, int offset, int length) {
        /**
         * TODO - entitydata buffer shall not contain attribute data in spriteData buffer.
         */
        spriteData.put(entity, entityOffset, data, offset,
                mapper.getSizePerVertex(BufferIndex.ATTRIBUTES.index) - entityOffset);
        entityData.put(entity, entityOffset, data, offset, length);
    }

}
