package com.graphicsengine.spritemesh;

import com.google.gson.annotations.SerializedName;
import com.graphicsengine.io.GraphicsEngineResourcesData;
import com.graphicsengine.sprite.Sprite;
import com.graphicsengine.sprite.SpriteFactory;
import com.graphicsengine.sprite.SpriteNode;
import com.graphicsengine.sprite.SpriteNodeFactory;
import com.nucleus.geometry.AttributeUpdater.Producer;
import com.nucleus.geometry.AttributeUpdater.PropertyMapper;
import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.texturing.Texture2D;
import com.nucleus.vecmath.Rectangle;

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
     * The rectangle defining the sprites, all sprites will have same size
     * 4 values = x1,y1 + width and height
     */
    @SerializedName("rect")
    private Rectangle rectangle;

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
        setSpriteRectangle(source.getSpriteRectangle());
    }

    @Override
    public void createSprites(NucleusRenderer renderer, SpriteMesh consumer, GraphicsEngineResourcesData resources) {
        PropertyMapper mapper = consumer.getMapper();
        float[] attributeData = consumer.getAttributeData();
        Texture2D tex = consumer.getTexture(Texture2D.TEXTURE_0);
        for (int i = 0; i < count; i++) {
            switch (tex.textureType) {
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
     * Returns the rectangle defining the sprites
     * 
     * @return Rectangle defining sprite, X1, Y1, width, height.
     */
    public Rectangle getSpriteRectangle() {
        return rectangle;
    }

    /**
     * Internal method, sets the rectangle defining each sprite
     * This will only set the size parameter, createMesh must be called to actually create the mesh
     * 
     * param rectangle values defining sprite, X1, Y1, width, height.
     */
    private void setSpriteRectangle(Rectangle rectangle) {
        this.rectangle = new Rectangle(rectangle);
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
