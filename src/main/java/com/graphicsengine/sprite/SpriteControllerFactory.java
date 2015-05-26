package com.graphicsengine.sprite;

import com.graphicsengine.sprite.SpriteController.LogicResolver;
import com.graphicsengine.tiledsprite.TiledSpriteController;

/**
 * Used to create instances of spritecontrollers, the controllers need to have the logic resolvers setup according
 * to implementation needs.
 * 
 * @author Richard Sahlin
 *
 */
public class SpriteControllerFactory {

    private final static String LOGICRESOLVER_NOT_SET_ERROR = "LogicResolver not set, must call setLogicResolver()";

    /**
     * The defined controllers that this factory can create
     * 
     * @author Richard Sahlin
     *
     */
    public enum SpriteControllers {
        TILED(TiledSpriteController.class);

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

    static LogicResolver logicResolver;
    /**
     * Tiled sprite controller instance.
     */
    public final static String TILED = "tiled";

    /**
     * Creates a SpriteController instance.
     * 
     * @param controller SpriteController instance to create
     * @return
     */
    public static SpriteController create(SpriteControllers controller) throws IllegalAccessException,
            InstantiationException, ClassNotFoundException {
        if (logicResolver == null) {
            throw new IllegalArgumentException(LOGICRESOLVER_NOT_SET_ERROR);
        }
        SpriteController impl = (SpriteController) controller.getControllerClass().newInstance();
        impl.setLogicResolver(logicResolver);
        return impl;
    }

    /**
     * Sets the logic resolver to use for spritecontrollers, this must be called before a call to create() is made.
     * 
     * @param logicResolver
     */
    public static void setLogicResolver(LogicResolver logicResolver) {
        SpriteControllerFactory.logicResolver = logicResolver;
    }

}
