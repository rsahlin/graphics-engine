package com.graphicsengine.map;

import java.io.IOException;
import java.io.ObjectInputStream;

import com.graphicsengine.map.Map.Mode;
import com.nucleus.io.ExternalReference;
import com.nucleus.profiling.FrameSampler;
import com.nucleus.types.DataType;

/**
 * Factory method for {@link Map}
 * This is used to create the map data objects, from different sources.
 * 
 * @author Richard Sahlin
 *
 */
public class MapFactory {

    /**
     * Creates a new empty map with the specified size.
     * 
     * @param width
     * @param height
     * @param ambientMode Storage mode for ambient material
     * @param ambientFormat Datatype for ambient material VEC3 or VEC4
     * @throws IllegalArgumentException If ambient is null or ambientFormat is not VEC3 or VEC4
     * @return The created map
     */
    public static Map createMap(int width, int height, Mode ambientMode, DataType ambientFormat) {
        return new Map(width, height, ambientMode, ambientFormat);
    }

    /**
     * Load a map from an external reference
     * TODO - should the maps be stored like textures?
     * 
     * @param externalRef
     * @return
     */
    public static Map createMap(ExternalReference externalRef) throws IOException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(externalRef.getAsStream());
        try {
            long start = System.currentTimeMillis();
            Map map = (Map) in.readObject();
            FrameSampler.getInstance().logTag(FrameSampler.Samples.LOAD_MAP, start, System.currentTimeMillis());
            return map;
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }
}
