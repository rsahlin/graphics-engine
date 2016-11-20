package com.graphicsengine.sprite;

import com.google.gson.annotations.SerializedName;

/**
 * The data for actor objects, this belongs to a node where the node controlls the number of actors that are supported.
 * This class can be serialized using GSON
 * 
 * @author Richard Sahlin
 *
 */
public class ActorData {

    @SerializedName("data")
    private ActorArray[] data;

    /**
     * Creates a copy of the specified actor data
     * 
     * @param source
     */
    protected ActorData(ActorData source) {
        if (source.data != null) {
            data = new ActorArray[source.data.length];
            System.arraycopy(source.data, 0, data, 0, source.data.length);
        }

    }

    /**
     * Returs the actor array.
     * 
     * @return
     */
    public ActorArray[] getData() {
        return data;
    }

}
