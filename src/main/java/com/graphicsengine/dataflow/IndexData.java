package com.graphicsengine.dataflow;

/**
 * Holds index and count, for instance for destination copy.
 * 
 * @author Richard Sahlin
 *
 */
public class IndexData {

    /**
     * Index of the data in the array
     *
     */
    public enum Index {
        /**
         * The offset value
         */
        offset(0),
        /**
         * The count value
         */
        count(1);
        public final int index;

        private Index(int index) {
            this.index = index;
        }

    }

    private int[] data;
}
