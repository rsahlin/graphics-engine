package com.graphicsengine.map;

import com.google.gson.annotations.SerializedName;
import com.graphicsengine.dataflow.ArrayInputData;
import com.nucleus.io.BaseReference;
import com.nucleus.vecmath.Axis;

/**
 * The map for a playfield
 * This class can be serialized using GSON
 * 
 * @author Richard Sahlin
 *
 */
public class Playfield extends BaseReference {

    /**
     * The size of the map in this controller
     */
    @SerializedName("mapSize")
    private int[] mapSize;
    /**
     * The map data
     */
    @SerializedName("mapData")
    private int[] mapData;

    @SerializedName("arrayInput")
    private ArrayInputData arrayInput;

    /**
     * Creates a new playfield from the controller source
     * This playfield will contain the mapdata from the source, the id will be the mapReference from the source.
     * This is used when exporting
     * 
     * @param source
     */
    Playfield(PlayfieldNode source) {
        mapSize = new int[2];
        setId(source.getMapRef());
        createMap(source);
    }

    /**
     * Creates the mapdata in this class from the source, this will copy map size and the data in the map.
     * 
     * @param source
     */
    void createMap(PlayfieldNode source) {
        setSize(source.getMapSize());
        if (source.getMapData() != null) {
            mapData = new int[mapSize[Axis.WIDTH.index] * mapSize[Axis.HEIGHT.index]];
            System.arraycopy(source.getMapData(), 0, mapData, 0, mapData.length);
        }
    }

    /**
     * Returns the mapsize, width and height
     */
    public int[] getMapSize() {
        return mapSize;
    }

    /**
     * Copies the size from the source array
     * 
     * @param size Array with at least 2 values, width and height
     */
    private void setSize(int[] size) {
        mapSize[Axis.WIDTH.index] = size[Axis.WIDTH.index];
        mapSize[Axis.HEIGHT.index] = size[Axis.HEIGHT.index];
    }

    /**
     * Returns the map data, this is a reference to the map array - any changes will be reflected in this class
     * 
     * @return Array with map data, or null if not set
     */
    public int[] getMap() {
        return mapData;
    }

    /**
     * Returns the array input if set
     * 
     * @return Array input data or null
     */
    public ArrayInputData getArrayInput() {
        return arrayInput;
    }

}
