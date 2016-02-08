package com.graphicsengine.spritemesh;

import java.io.IOException;

import com.google.gson.annotations.SerializedName;
import com.graphicsengine.io.GraphicsEngineRootNode;
import com.graphicsengine.sprite.Sprite;
import com.graphicsengine.sprite.SpriteNode;
import com.nucleus.geometry.AttributeUpdater.Producer;
import com.nucleus.geometry.MeshBuilder;
import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.scene.RootNode;
import com.nucleus.shader.ShaderProgram;
import com.nucleus.texturing.Texture2D;

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

    /**
     * The mesh that can be redered
     */
    @SerializedName("charset")
    private SpriteMesh spriteSheet;

    /**
     * Default constructor
     */
    public SpriteMeshNode() {
        setAttributeProducer(this);
    }

    protected SpriteMeshNode(SpriteMeshNode source) {
        super(source);
        set(source);
        setAttributeProducer(this);
    }

    /**
     * Sets the data in this class from the source, do not set transient values
     * This is used when importing
     * 
     * @param source The source to copy
     */
    protected void set(SpriteMeshNode source) {
        super.set(source);
        spriteSheet = new SpriteMesh(source.getSpriteSheet());
    }

    @Override
    protected void createSprites(NucleusRenderer renderer, SpriteNode source, RootNode scene) {
        SpriteMeshNode spriteController = (SpriteMeshNode) source;
        create(spriteController.getActorData().getCount());
        int offset;
        float[] attributeData = spriteSheet.getAttributeData();
        Texture2D tex = spriteController.getSpriteSheet().getTexture(Texture2D.TEXTURE_0);
        ShaderProgram program = spriteController.getSpriteSheet().getMaterial().getProgram();
        for (int i = 0; i < count; i++) {
            offset = program.getAttributeOffset(i) * ShaderProgram.VERTICES_PER_SPRITE;
            switch (tex.type) {
            case TiledTexture2D:
                sprites[i] = new TiledSprite(this, attributeData, offset);
                MeshBuilder.prepareTiledUV(spriteController.spriteSheet.getAttributeData(), offset,
                        TiledSpriteProgram.ATTRIBUTE_SPRITE_FRAMEDATA, TiledSpriteProgram.ATTRIBUTES_PER_VERTEX);
                break;
            case UVTexture2D:
                sprites[i] = new UVSprite(this, attributeData, i);
                break;
            default:
                throw new IllegalArgumentException();
            }
            sprites[i].setPosition(0, 0);
            sprites[i].floatData[TiledSprite.SCALE] = 1;
            sprites[i].setFrame(0);
        }
        setActor(spriteController.getActorData().getData());
    }

    @Override
    protected void createMesh(NucleusRenderer renderer, SpriteNode source, ShaderProgram program, RootNode scene) {
        try {
            GraphicsEngineRootNode gScene = (GraphicsEngineRootNode) scene;
            spriteSheet = SpriteMeshFactory.create(renderer, (SpriteMeshNode) source, program, gScene);
            addMesh(spriteSheet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the number of sprites in this controller
     * 
     * @return
     */
    @Override
    public int getCount() {
        return count;
    }

    /**
     * Returns the renderable object for this spritecontroller.
     * 
     * @return
     */
    public SpriteMesh getSpriteSheet() {
        return spriteSheet;
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
