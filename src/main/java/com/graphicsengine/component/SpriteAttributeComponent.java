package com.graphicsengine.component;

import com.graphicsengine.component.ActorComponent.EntityData;
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
import com.nucleus.geometry.shape.RectangleShapeBuilder;
import com.nucleus.geometry.shape.ShapeBuilder;
import com.nucleus.geometry.shape.ShapeBuilderFactory;
import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.texturing.Texture2D;

/**
 * The old school sprite component, this is a collection of a number of (similar) sprite objects
 * that have the data in a shared buffer, can be rendered using one draw call.
 * SpriteData is mapped one to one for each sprite, whereas the attribute data is one -> four for a quad based sprite.
 * The intention is that the logic processing and update to attributes (quad data) can be done using a Compute shader,
 * or OpenCL
 * This class is mostly used for GLES versions prior to 3.2 when geometry shaders can be used instead.
 * Deprecated - use {@link SpriteComponent} instead
 * 
 * The class can be serialized using gson
 * 
 * @author Richard Sahlin
 *
 */
@Deprecated
public class SpriteAttributeComponent extends ActorComponent<SpriteMesh>
        implements Consumer, MeshBuilderFactory<Mesh>, EntityData {

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
    transient protected CPUComponentBuffer spriteBuffer;
    transient protected CPUComponentBuffer entityBuffer;

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
        return spriteBuffer;
    }

    @Override
    public ComponentBuffer getEntityBuffer() {
        return entityBuffer;
    }

    @Override
    protected void createBuffers(EntityIndexer mapper) {
        spritedataSize = mapper.attributesPerVertex;
        spriteBuffer = new CPUComponentBuffer(count, mapper.attributesPerVertex * 4);
        entityBuffer = new CPUComponentBuffer(count, mapper.attributesPerEntity);
        spriteExpander = new CPUQuadExpander(mesh.getTexture(Texture2D.TEXTURE_0), mapper, entityBuffer, spriteBuffer);
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
        if (renderer.getGLES().getInfo().getRenderVersion().major > 2) {
            SimpleLogger.d(getClass(), "Target supports GLES 3 - use SpriteComponent instead!");
        }
        return new SpriteMesh.Builder(renderer);
    }

    @Override
    protected ShapeBuilder<Mesh> createShapeBuilder() {
        // Need to know the builder or config impl so the setEnableVertex() index can be called.
        ShapeBuilder<Mesh> builder = ShapeBuilderFactory.getInstance().createBuilder(shape, count, 0);
        ((RectangleShapeBuilder) builder).setEnableVertexIndex(true);
        return builder;
    }

    @Override
    public void setEntity(int entity, int entityOffset, float[] data, int offset, int length) {
        entityBuffer.put(entity, entityOffset, data, offset, length);
        spriteExpander.setData(entity, entityOffset, data, offset, length);
    }

}
