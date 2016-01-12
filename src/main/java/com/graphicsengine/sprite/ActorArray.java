package com.graphicsengine.sprite;

import com.nucleus.actor.ActorResolver;

/**
 * An array of actor data, this is used when importing and exporting actor
 * This class can be serialized using GSON
 * 
 * @author Richard Sahlin
 *
 */
public class ActorArray {

    /**
     * Name of actor logic - this uses the {@link ActorResolver} to find classname
     */
    private String instance;
    /**
     * Classname of actor, does not use {@link ActorResolver} - classname used
     */
    private String classname;
    /**
     * Index where the actor shall be put
     */
    private int index;
    /**
     * Number of actor items to set
     */
    private int count;

    /**
     * Name of actor instance, uses {@link ActorResolver} to find classname.
     * May use classname instead of resolving actor
     * 
     * @return
     */
    public String getInstance() {
        return instance;
    }

    public int getIndex() {
        return index;
    }

    public int getCount() {
        return count;
    }

    /**
     * Returns the classname, if set. This is the class to instantiate for this actor
     * 
     * @return Classname to instantiate or null
     */
    public String getClassName() {
        return classname;
    }

}
