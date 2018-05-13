package com.graphicsengine.component;

import com.graphicsengine.spritemesh.SpriteGeometryMesh;
import com.nucleus.component.CPUComponentBuffer;
import com.nucleus.component.Component;
import com.nucleus.component.ComponentBuffer;
import com.nucleus.geometry.AttributeBuffer;
import com.nucleus.geometry.Mesh;
import com.nucleus.geometry.Mesh.Builder;
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
public class SpriteComponent extends ActorComponent<SpriteGeometryMesh> {

    transient protected AttributeBuffer attributes;

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
        this.attributes = buffer;
    }

    @Override
    public void updateAttributeData(NucleusRenderer renderer) {

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
    public void setActor(int actor, float[] data, int offset) {

    }

    @Override
    public void setPosition(int actor, float[] position, int offset) {
        // TODO Auto-generated method stub

    }

}
