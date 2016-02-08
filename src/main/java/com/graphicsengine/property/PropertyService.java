package com.graphicsengine.property;

import java.util.HashMap;
import java.util.Set;

/**
 * Singleton to handle key/value lookups.
 * The intended usage are properties that are persistent or semi-persistent.
 * There is no callback or event mechanism to act when a value is updated.
 * 
 * 
 * @author Richard Sahlin
 *
 */
public class PropertyService {

    private static PropertyService propertyService = null;

    /**
     * Map for String values.
     */
    private HashMap<String, String> stringMap = new HashMap<String, String>();
    /**
     * Map for Integer values.
     */
    private HashMap<String, Integer> integerMap = new HashMap<String, Integer>();

    /**
     * Returns the PropertyService instance, this will always be the same.
     * 
     * @return The PropertyService instance (singleton)
     */
    public PropertyService getInstance() {
        if (propertyService == null) {
            propertyService = new PropertyService();
        }
        return propertyService;
    }

    /**
     * Sets the key to String value, pass value 'null' to clear a value.
     * 
     * @param key The String key to set value for.
     * @param value The String value
     */
    public void setProperty(String key, String value) {
        stringMap.put(key, value);
    }

    /**
     * Returns the String value for the key, or null if not set.
     * 
     * @param key
     * @return The value, or null.
     */
    public String getStringProperty(String key) {
        return stringMap.get(key);
    }

    /**
     * Sets the key to Integer value, pass 'null' to clear a value.
     * 
     * @param key
     * @param value
     */
    public void setProperty(String key, Integer value) {
        integerMap.put(key, value);
    }

    /**
     * Returns the Integer property for key.
     * 
     * @param key
     * @return
     */
    public Integer getIntegerProperty(String key) {
        return integerMap.get(key);
    }

    /**
     * Returns a Set with all keys for String values
     * 
     * @return Set containing all keys for String values
     */
    public Set<String> getStringKeys() {
        return stringMap.keySet();
    }

}
