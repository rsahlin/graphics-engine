package com.graphicsengine.component;

import com.nucleus.component.Component;
import com.nucleus.component.ComponentFactory;

/**
 * Creates component implementations - used when deserializing from JSON
 * 
 * @author Richard Sahlin
 *
 */
public class GraphicsEngineComponentFactory extends ComponentFactory {

    public enum Type {
        spritecomponent(SpriteComponent.class);
        private final Class<?> theClass;

        private Type(Class<?> theClass) {
            this.theClass = theClass;
        }

        /**
         * Returns the class to instantiate for the different types
         * 
         * @return
         */
        public Class<?> getTypeClass() {
            return theClass;
        }
    }

    @Override
    public Component create(String type) throws InstantiationException, IllegalAccessException {
        Type t = Type.valueOf(type);
        return (Component) t.theClass.newInstance();
    }
    
}
