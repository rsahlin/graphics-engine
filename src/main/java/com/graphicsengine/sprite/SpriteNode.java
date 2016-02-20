package com.graphicsengine.sprite;

import com.google.gson.annotations.SerializedName;
import com.graphicsengine.spritemesh.SpriteMesh;
import com.nucleus.actor.ActorContainer;
import com.nucleus.actor.ActorItem;
import com.nucleus.actor.ActorNode;
import com.nucleus.actor.ActorResolver;
import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.scene.RootNode;

/**
 * Controller for a set of sprites.
 * This can be added as a node to the scenegraph.
 * This class can be serialized using GSON
 * 
 * @author Richard Sahlin
 *
 */
public abstract class SpriteNode extends ActorNode {

    private final static String ACTORRESOLVER_NOT_SET = "ActorResolver not set, must set before calling.";
    private final static String ACTOR_NOT_FOUND_ERROR = "Actor not found for id: ";

    /**
     * The data used to create the actor
     */
    @SerializedName("actordata")
    ActorData actorData;

    transient protected Sprite[] sprites;
    transient protected int count;
    transient protected ActorResolver actorResolver;

    /**
     * Default constructor
     */
    public SpriteNode() {
        super();
    }

    protected SpriteNode(SpriteNode source) {
        set(source);
    }

    /**
     * Sets the data in this class from the source, do not set the transient values
     * This is used when importing.
     * 
     * @param source The source to copy
     */
    protected void set(SpriteNode source) {
        super.set(source);
        actorData = new ActorData(source.getActorData());
    }

    @Override
    public void create() {
        /*
         * Each sprite must be created by calling {@link #createSprites(NucleusRenderer, SpriteNode, RootNode)}
         */
        this.count = actorData.getCount();
        sprites = new Sprite[count];
    }

    /**
     * Returns the actor data, this is used when importing and exporting
     * 
     * @return
     */
    public ActorData getActorData() {
        return actorData;
    }

    @Override
    public ActorContainer[] getActorContainer() {
        return sprites;
    }

    /**
     * Adds a resolver to find implementing actor (sprite) classes from ids.
     * This shall be done programatically (from the code) to avoid binding classname to the leveldata, as this will
     * prevent proper obfuscation of the code and will be harder to maintain.
     * 
     * @param resolver The resolver to add, used when createSprites() is called.
     */
    public void setActorResolver(ActorResolver resolver) {
        actorResolver = resolver;
    }

    /**
     * Creates the actor sprites, this will only create the actor logic and data for the actor it will not create the
     * mesh/textures.
     * 
     * 
     * @see #createMesh(NucleusRenderer, NodeData, RootNode)
     * 
     * @param renderer
     * @param mesh
     * @param scene
     */
    protected abstract void createSprites(NucleusRenderer renderer, SpriteMesh mesh, RootNode scene);

    /**
     * Creates the renderable sprite (mesh)
     * After this call this node can be rendered.
     * 
     * @param renderer
     * @param spriteController
     * @param scene
     */
    protected abstract void createMesh(NucleusRenderer renderer, SpriteNode spriteController, RootNode scene);

    /**
     * Internal method to check if a actor resolver has been set, call this in implementations of the createSprites()
     * method to check that a resolver exist.
     */
    protected void validateResolver() {
        if (actorResolver == null) {
            throw new IllegalArgumentException(ACTORRESOLVER_NOT_SET);
        }
    }

    /**
     * Returns the number of sprites in this controller
     * 
     * @return
     */
    public int getCount() {
        return count;
    }

    /**
     * Returns the array containing the sprites.
     * Note that any changes to the array will be reflected here.
     * 
     * @return The array containing all sprites.
     */
    public Sprite[] getSprites() {
        return sprites;
    }

    /**
     * Sets the actor logic for sprite objects, the sprites must be created before calling this method.
     * 
     * @param actorIds The actor logic ids, used together with offsets and counts.
     * @param offsets The offset into sprite list for the actor to set.
     * @param counts The number of sprite actors to set at offset.
     */
    public void setActor(String[] actorIds, int[] offsets, int[] counts) {
        for (int i = 0; i < actorIds.length; i++) {
            int offset = offsets[i];
            int count = counts[i];
            ActorItem l = actorResolver.getActor(actorIds[i]);
            if (l == null) {
                throw new IllegalArgumentException(ACTOR_NOT_FOUND_ERROR + actorIds[i]);
            }
            for (int loop = 0; loop < count; loop++) {
                sprites[offset++].actor = l;
            }
        }
    }

    /**
     * Sets the sprite actor data, the actor type will be set at the index and count.
     * 
     * @param actor
     */
    public void setActor(ActorArray actor) {
        ActorItem l = null;
        String classname = actor.getClassName();
        if (classname != null) {
            try {
                l = (ActorItem) Class.forName(classname).newInstance();
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                e.printStackTrace();
                // Cannot recover
                throw new RuntimeException(e);
            }
        } else {
            if (actorResolver == null) {
                throw new IllegalArgumentException(ACTORRESOLVER_NOT_SET);
            }
            l = actorResolver.getActor(actor.getInstance());
            if (l == null) {
                throw new IllegalArgumentException(ACTOR_NOT_FOUND_ERROR + actor.getInstance());
            }
        }
        int offset = actor.getIndex();
        int count = actor.getCount();
        if (count == -1) {
            count = sprites.length - offset;
        }
        for (int loop = 0; loop < count; loop++) {
            sprites[offset++].actor = l;
        }
    }

    /**
     * Sets the initial actor logic for the sprites, this will unpack the values in the actor array and set the correct
     * actor object in the sprites.
     * The sprites must be created before calling this method.
     * 
     * @param actor
     */
    public void setActor(ActorArray[] actors) {
        for (ActorArray data : actors) {
            setActor(data);
        }
    }

}
