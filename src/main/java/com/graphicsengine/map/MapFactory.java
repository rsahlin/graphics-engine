package com.graphicsengine.map;

import java.io.FileNotFoundException;
import java.io.InputStreamReader;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.nucleus.io.ExternalReference;

/**
 * Factory method for {@link Map}
 * This is used to create the map data objects, from different sources.
 * 
 * @author Richard Sahlin
 *
 */
public class MapFactory {
    /**
     * Creates a new map from the controller source
     * This map will contain the data from the source, the id will be the mapReference from the source.
     * Use this when exporting
     * 
     * @param source
     * @return
     */
    public static Map createMap(PlayfieldNode source) {
        return new Map(source);
    }

    /**
     * Creates a new empty map with the specified size
     * 
     * @param width
     * @param height
     * @return
     */
    public static Map createMap(int width, int height) {
        return new Map(width, height);
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
