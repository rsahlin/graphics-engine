package com.graphicsengine.map;

import java.io.FileNotFoundException;
import java.io.InputStreamReader;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.graphicsengine.map.Map.Mode;
import com.nucleus.io.ExternalReference;
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
     * 
     * @param externalRef
     * @return
     */
    public static Map createMap(ExternalReference externalRef) throws FileNotFoundException {
        Gson gson = new Gson();
        try {
            Map map = gson.fromJson(new InputStreamReader(externalRef.getAsStream()),
                    Map.class);
            return map;
        } catch (JsonSyntaxException e) {
            throw new RuntimeException("Error parsing file:" + externalRef.getSource(), e);
        }
    }
}
