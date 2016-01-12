package com.graphicsengine.sprite;

import com.google.gson.annotations.SerializedName;

/**
 * The data for actor objects
 * This class can be serialized using GSON
 * 
 * @author Richard Sahlin
 *
 */
public class ActorData {

    @SerializedName("count")
    private int count;
    @SerializedName("data")
    private ActorArray[] data;

    /**
     * Creates a copy of the specified actor data
     * 
     * @param source
     */
    protected ActorData(ActorData source) {
        count = source.count;
        if (source.data != null) {
            data = new ActorArray[source.data.length];
            System.arraycopy(source.data, 0, data, 0, source.data.length);
        }

    }

    public int getCount() {
        return count;
    }

    public ActorArray[] getData() {
        return data;
    }

}
