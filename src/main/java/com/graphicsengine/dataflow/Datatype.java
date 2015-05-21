package com.graphicsengine.dataflow;

public enum Datatype {

    INT(1),
    FLOAT(2),
    SHORT(3);

    private final int type;

    private Datatype(int type) {
        this.type = type;
    }

    /**
     * Returns the datatype as int
     * 
     * @return
     */
    public int getType() {
        return type;
    }

}
