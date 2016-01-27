package com.graphicsengine.sprite;

import com.graphicsengine.spritemesh.SpriteMeshNode;
import com.nucleus.actor.ActorResolver;
import com.nucleus.scene.Node;

/**
 * Used to create instances of spritecontrollers, the controllers need to have the logic resolvers setup according
 * to implementation needs.
 * 
 * @author Richard Sahlin
 *
 */
public class SpriteControllerFactory {

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
     * Creates a SpriteController instance.
     * 
     * @param controller SpriteController instance to create
     * @param source The nodedata source
     * @return
     */
    public static SpriteNode create(SpriteControllers controller, Node source)
            throws IllegalAccessException,
            InstantiationException, ClassNotFoundException {
        SpriteNode impl = (SpriteNode) controller.getControllerClass().newInstance();
        impl.setActorResolver(actorResolver);
        impl.set(source);
        return impl;
    }

    /**
     * Sets the actor resolver to use for spritecontrollers, this must be called before a call to create() is made.
     * 
     * @param actorResolver
     */
    public static void setActorResolver(ActorResolver actorResolver) {
        SpriteControllerFactory.actorResolver = actorResolver;
    }

}
