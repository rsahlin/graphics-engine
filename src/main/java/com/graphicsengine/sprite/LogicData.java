package com.graphicsengine.sprite;

import com.google.gson.annotations.SerializedName;

/**
 * The data for logic objects
 * This class can be serialized using GSON
 * 
 * @author Richard Sahlin
 *
 */
public class LogicData {

    @SerializedName("count")
    private int count;
    @SerializedName("data")
    private LogicArray[] data;

    /**
     * Creates a copy of the specified logic data
     * 
     * @param source
     */
    protected LogicData(LogicData source) {
        count = source.count;
        if (source.data != null) {
            data = new LogicArray[source.data.length];
            System.arraycopy(source.data, 0, data, 0, source.data.length);
        }

    }

    public int getCount() {
        return count;
    }

    public LogicArray[] getData() {
        return data;
    }

}
