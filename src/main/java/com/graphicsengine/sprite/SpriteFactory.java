package com.graphicsengine.sprite;

import com.nucleus.geometry.AttributeUpdater.PropertyMapper;
import com.nucleus.scene.Node;

/**
 * Creates the sprites
 * 
 * @author Richard Sahlin
 *
 */
public class SpriteFactory {

    /**
     * Creates a new TiledSprite, using attribute data at the specified offset.
     * 
     * @param clazz
     * @param parent The node containing the sprites
     * @param mapper The attribute property mappings
     * @param data Shared attribute data for positions
     * @param index of this sprite, used to find offset into attributes.
     * @return The created sprite or null if an exception
     */
    public static Sprite create(Class clazz, Node parent, PropertyMapper mapper, float[] attributeData, int index) {
        Sprite sprite;
        try {
            sprite = (Sprite) clazz.newInstance();
            sprite.setup(parent, mapper, attributeData, index);
            return sprite;
        } catch (InstantiationException | IllegalAccessException e) {
            System.err.println("Could not create sprite " + clazz + "\n" + e);
        }
        return null;
    }

}
