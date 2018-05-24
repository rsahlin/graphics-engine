package com.graphicsengine.component;

import com.graphicsengine.component.ActorComponent.EntityData;
import com.graphicsengine.spritemesh.SpriteGeometryMesh;
import com.nucleus.component.CPUComponentBuffer;
import com.nucleus.component.Component;
import com.nucleus.component.ComponentBuffer;
import com.nucleus.geometry.AttributeBuffer;
import com.nucleus.geometry.Mesh;
import com.nucleus.geometry.Mesh.Builder;
import com.nucleus.geometry.shape.ShapeBuilder;
import com.nucleus.renderer.NucleusRenderer;
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
    transient protected EntityMapper mapper;

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
    protected void createBuffers(EntityMapper mapper) {
        spriteData = new CPUComponentBuffer(count, mapper.attributesPerVertex);
        entityData = new CPUComponentBuffer(count, mapper.attributesPerEntity);
        addBuffer(0, spriteData);
        addBuffer(1, entityData);
        this.mapper = mapper;
    }

    @Override
    public ComponentBuffer getEntityBuffer() {
        return getBuffer(1);
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
    protected Builder<Mesh> createBuilderInstance(NucleusRenderer renderer) {
        return new SpriteGeometryMesh.Builder(renderer);
    }

    @Override
    protected ShapeBuilder createShapeBuilder() {
        // We are using point mode - do not create shapebuilder
        return null;
    }

    @Override
    public void setEntity(int entity, int entityOffset, float[] data, int offset, int length) {
        entityData.put(entity, entityOffset, data, offset, length);
        if (entityOffset < mapper.attributesPerVertex) {
            spriteData.put(entity, entityOffset, data, offset, mapper.attributesPerVertex - entityOffset);

        }
    }

}
