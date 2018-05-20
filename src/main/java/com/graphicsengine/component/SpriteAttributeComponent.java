package com.graphicsengine.component;

import com.graphicsengine.spritemesh.SpriteMesh;
import com.nucleus.SimpleLogger;
import com.nucleus.component.CPUComponentBuffer;
import com.nucleus.component.CPUQuadExpander;
import com.nucleus.component.Component;
import com.nucleus.component.ComponentBuffer;
import com.nucleus.geometry.AttributeBuffer;
import com.nucleus.geometry.AttributeUpdater.Consumer;
import com.nucleus.geometry.Mesh;
import com.nucleus.geometry.Mesh.Builder;
import com.nucleus.geometry.MeshBuilder.MeshBuilderFactory;
import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.texturing.Texture2D;

/**
 * The old school sprite component, this is a collection of a number of (similar) sprite objects
 * that have the data in a shared buffer, can be rendered using one draw call.
 * SpriteData is mapped one to one for each sprite, whereas the attribute data is one -> four for a quad based sprite.
 * The intention is that the logic processing and update to attributes (quad data) can be done using a Compute shader,
 * or OpenCL
 * This class is mostly used for GLES versions prior to 3.2 when geometry shaders can be used instead.
 * 
 * The class can be serialized using gson
 * 
 * @author Richard Sahlin
 *
 */
public class SpriteAttributeComponent extends ActorComponent<SpriteMesh> implements Consumer, MeshBuilderFactory<Mesh> {

    public static final String GRAVITY = "gravity";

    public static final float DEFAULT_GRAVITY = 5;

    /**
     * The sprites attribute float data storage, this is the sprite visible (mesh) properties such as position, scale
     * and frame, plus entity data needed to process the logic.
     * This is what is generally needed in order to put sprite on screen.
     * In order to render a mesh with sprites this data is copied one -> four in the mesh.
     * TODO Use java.nio.FloatBuffer instead and perhaps move into a special class to handle 1 -> 4 mapping
     */
    transient protected CPUQuadExpander spriteExpander;

    transient protected int spritedataSize;

    @Override
    public Component createInstance() {
        return new SpriteAttributeComponent();
    }

    @Override
    public void set(Component source) {
        set((SpriteAttributeComponent) source);
    }

    private void set(SpriteAttributeComponent source) {
        super.set(source);
    }

    /**
     * Returns the buffer that holds the data for the sprite (mesh)
     * This is the position, rotation, scale data copied to mesh when {@link #updateAttributeData(NucleusRenderer)} is
     * called.
     * 
     * @return
     */
    public ComponentBuffer getSpriteBuffer() {
        return getBuffer(0);
    }

    @Override
    public ComponentBuffer getEntityBuffer() {
        return getBuffer(1);
    }

    @Override
    protected void createBuffers(EntityMapper mapper) {
        spritedataSize = mapper.attributesPerVertex;
        CPUComponentBuffer spriteData = new CPUComponentBuffer(count, mapper.attributesPerVertex * 4);
        CPUComponentBuffer entityData = new CPUComponentBuffer(count, mapper.attributesPerEntity);
        spriteExpander = new CPUQuadExpander(mesh, mapper, entityData, spriteData);
        addBuffer(0, spriteData);
        addBuffer(1, entityData);
    }

    /**
     * Returns the number of frames available in the texture
     * 
     * @return
     */
    public int getFrameCount() {
        return mesh.getTexture(Texture2D.TEXTURE_0).getFrameCount();
    }

    /**
     * Sets the color of the sprite
     * 
     * @param index
     * @param rgba Array with at least 4 float values, index 0 is RED, 1 is GREEN, 2 is BLUE, 3 is ALPHA
     */
    public void setColor(int index, float[] rgba) {
        SimpleLogger.d(getClass(), "Not implemented!!!!!!!!!");
    }

    /**
     * Sets the transform for a sprite using 3 values for xyz axis, translate.xyz, rotate.xyz, scale.xyz
     * Use this method for initialization only
     * 
     * @param sprite
     * @param transform 3 axis translate, rotate and scale values
     */
    public void setTransform(int sprite, float[] transform) {
        spriteExpander.setTransform(sprite, transform);
    }

    @Override
    public void setPosition(int actor, float[] position, int offset) {
        spriteExpander.setPosition(actor, position, offset);
    }

    @Override
    public void setEntityData(int sprite, int destOffset, float[] data) {
        ComponentBuffer entityBuffer = getEntityBuffer();
        entityBuffer.put(sprite, destOffset, data, 0, data.length);
        spriteExpander.setData(sprite, data, 0);
    }

    @Override
    public void bindAttributeBuffer(AttributeBuffer buffer) {
        spriteExpander.bindAttributeBuffer(buffer);
    }

    @Override
    public void updateAttributeData(NucleusRenderer renderer) {
        spriteExpander.updateAttributeData(renderer);
    }

    @Override
    protected Builder<Mesh> createBuilderInstance(NucleusRenderer renderer) {
        return new SpriteMesh.Builder(renderer);
    }

}