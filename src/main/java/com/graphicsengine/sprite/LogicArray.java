package com.graphicsengine.sprite;

public class LogicArray {

    /**
     * Name of logic - this uses the logicresolver to find classname
     */
    private String instance;
    /**
     * Classname of logic, does not use logicresolver - classname used
     */
    private String classname;
    /**
     * Index where the logic shall be put
     */
    private int index;
    /**
     * Number of logic items to set
     */
    private int count;

    /**
     * Name of logic instance, uses logicresolver to find classname.
     * May use classname instead of resolving logic
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
     * Returns the classname, if set. This is the class to instantiate for this logic
     * 
     * @return Classname to instantiate or null
     */
    public String getClassName() {
        return classname;
    }

}
