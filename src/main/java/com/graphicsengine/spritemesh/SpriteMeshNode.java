package com.graphicsengine.spritemesh;

import com.google.gson.annotations.SerializedName;
import com.graphicsengine.sprite.Sprite;
import com.graphicsengine.sprite.SpriteFactory;
import com.graphicsengine.sprite.SpriteNode;
import com.graphicsengine.sprite.SpriteNodeFactory;
import com.nucleus.geometry.AttributeUpdater.Producer;
import com.nucleus.geometry.AttributeUpdater.PropertyMapper;
import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.scene.RootNode;
import com.nucleus.texturing.Texture2D;
import com.nucleus.vecmath.Axis;

/**
 * Controller for mesh sprites, this node creates the mesh sprite objects.
 * A mesh sprite (quad) can be drawn in one draw call together with a large number of other sprites (they share the
 * same Mesh).
 * This is to allow a very large number of sprites in just 1 draw call to the underlying render API (OpenGLES).
 * Depending on what shader program is used with this class the render properties will be different.
 * {@link TiledSpriteProgram}
 * 
 * @author Richard Sahlin
 *
 */
public class SpriteMeshNode extends SpriteNode implements Producer {


    public enum ProgramType {
        /**
         * Using tiled texture and program
         */
        TILED(),
        /**
         * Using UV texture and program
         */
        UV();
    }

    /**
     * Width and height of a sprite.
     */
    @SerializedName("spriteSize")
    private float[] spriteSize = new float[2];

    /**
     * Default constructor
     */
    public SpriteMeshNode() {
        setAttributeProducer(this);
    }

    @Override
    public SpriteMeshNode createInstance() {
        SpriteMeshNode node = new SpriteMeshNode();
        node.setActorResolver(SpriteNodeFactory.getActorResolver());
        return node;
    }

    @Override
    public SpriteMeshNode copy() {
        SpriteMeshNode copy = createInstance();
        copy.set(this);
        return copy;
    }

    /**
     * Sets the data in this class from the source, do not set transient values
     * This is used when importing
     * 
     * @param source The source to copy
     */
    protected void set(SpriteMeshNode source) {
        super.set(source);
        setSpriteSize(source.getSpriteSize());
    }

    @Override
    public void createSprites(NucleusRenderer renderer, SpriteMesh consumer, RootNode scene) {
        PropertyMapper mapper = consumer.getMapper();
        float[] attributeData = consumer.getAttributeData();
        Texture2D tex = consumer.getTexture(Texture2D.TEXTURE_0);
        for (int i = 0; i < count; i++) {
            switch (tex.type) {
            case TiledTexture2D:
                sprites[i] = SpriteFactory.create(TiledSprite.class, this, mapper, attributeData, i);
                break;
            case UVTexture2D:
                sprites[i] = SpriteFactory.create(UVSprite.class, this, mapper, attributeData, i);
                break;
            default:
                throw new IllegalArgumentException();
            }
            sprites[i].setPosition(0, 0, 0);
            sprites[i].setScale(1, 1);
            sprites[i].setFrame(0);
        }
        setActor(getActorData().getData());
    }

    /**
     * Returns the dimension of the sprites, in x and y
     * 
     * @return Width and height of sprite, at index 0 and 1 respectively.
     */
    public float[] getSpriteSize() {
        return spriteSize;
    }

    /**
     * Internal method, sets the size of each char.
     * This will only set the size parameter, createMesh must be called to actually create the mesh
     * 
     * @param size The size to set, or null to not set any values.
     */
    private void setSpriteSize(float[] size) {
        if (size != null) {
            this.spriteSize[Axis.WIDTH.index] = size[Axis.WIDTH.index];
            this.spriteSize[Axis.HEIGHT.index] = size[Axis.HEIGHT.index];
        }
    }

    @Override
    public void play() {
        controllerState = State.PLAY;
    }

    @Override
    public void pause() {
        controllerState = State.PAUSE;
    }

    @Override
    public void stop() {
        controllerState = State.STOPPED;
    }

    @Override
    public void reset() {
        controllerState = State.STOPPED;
    }

    @Override
    public void init() {
        for (Sprite sprite : sprites) {
            sprite.actor.init(sprite);
        }
        controllerState = State.INITIALIZED;
    }

    @Override
    public void updateAttributeData() {
        for (Sprite sprite : sprites) {
            sprite.updateAttributeData();
        }

    }

}
