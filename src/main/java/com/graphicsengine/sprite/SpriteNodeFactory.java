package com.graphicsengine.sprite;

import com.graphicsengine.spritemesh.SpriteMeshNode;
import com.nucleus.actor.ActorResolver;

/**
 * Used to create instances of spritecontrollers (nodes), the controllers need to have the actor resolvers setup
 * according to implementation needs, do this by calling {@link #setActorResolver(ActorResolver)}
 * 
 * @author Richard Sahlin
 *
 */
public class SpriteNodeFactory {

    /**
     * The defined controllers that this factory can create
     * 
     * @author Richard Sahlin
     *
     */
    public enum SpriteControllers {
        TILED(SpriteMeshNode.class);

        @SuppressWarnings("rawtypes")
        private final Class clazz;

        @SuppressWarnings("rawtypes")
        private SpriteControllers(Class clazz) {
            this.clazz = clazz;
        };

        @SuppressWarnings("rawtypes")
        public Class getControllerClass() {
            return clazz;
        }

    }

    static ActorResolver actorResolver;
    /**
     * Tiled sprite controller instance.
     */
    public final static String TILED = "tiled";

    /**
     * Creates a SpriteController instance and setting the current actor resolver.
     * 
     * @param controller SpriteController instance to create
     * @return New instance of the specified controller or null if instantiation fails
     */
    public static SpriteNode create(SpriteControllers controller) {
        SpriteNode impl;
        try {
            impl = (SpriteNode) controller.getControllerClass().newInstance();
            impl.setActorResolver(actorResolver);
            return impl;
        } catch (InstantiationException | IllegalAccessException e) {
            // Print error and return null.
            System.err.println("Could not create SpriteNode");
            return null;
        }
    }

    /**
     * Sets the actor resolver to use for spritecontrollers, this must be called before a call to create() is made.
     * 
     * @param actorResolver
     */
    public static void setActorResolver(ActorResolver actorResolver) {
        SpriteNodeFactory.actorResolver = actorResolver;
    }

}
