package com.graphicsengine.sprite;

import com.nucleus.io.DataSetup;

/**
 * The setup for a sprite class, this should hold information for composing the correct Sprite logic.
 * This class can be used with serialization to decouple io from implementation
 * 
 * @author Richard Sahlin
 *
 */
public class SpriteControllerSetup extends DataSetup {

    /**
     * Mapping between imported data and this class.
     * 
     * @author Richard Sahlin
     *
     */
    public enum ControllerMapping implements Indexer {
        COUNT(0),
        ARRAY_SIZE(1),
        LOGIC_ID(2),
        LOGIC_OFFSET(3),
        LOGIC_COUNT(4);

        private final int index;

        private ControllerMapping(int index) {
            this.index = index;
        }

        @Override
        public int getIndex() {
            return index;
        }
    }

    /**
     * Total number of sprites in controller
     */
    int count;

    /**
     * Logic id's of sprites to create
     */
    String[] logicId;
    /**
     * Offset of sprites to create
     */
    int[] logicOffset;
    /**
     * Number of sprites to create
     */
    int[] logicCount;

    public SpriteControllerSetup() {
        super();
    }

    /**
     * Returns the logic ids to be used for a sprite, this is an array with String ids for the logic.
     * Use together with offset and count arrays to know how many sprites to set, and at what offset, with the specific
     * id.
     * 
     * @return Array containing the logic id
     */
    public String[] getLogicId() {
        return logicId;
    }

    /**
     * Returns the offsets for the sprite logic, ie the offset where a sprite logic shall be set (in the sprite
     * controller list)
     * 
     * @return
     */
    public int[] getLogicOffset() {
        return logicOffset;
    }

    /**
     * Returns the count for sprite logic, ie the number of sprites to set with the corresponding logic.
     * 
     * @return
     */
    public int[] getLogicCount() {
        return logicCount;
    }

    /**
     * Returns the total number of sprites for the controller
     * 
     * @return
     */
    public int getCount() {
        return count;
    }

    @Override
    public int importData(String[] data, int offset) {
        count = getInt(data, offset, ControllerMapping.COUNT);
        int arraySize = getInt(data, offset, ControllerMapping.ARRAY_SIZE);
        logicId = new String[arraySize];
        this.logicOffset = new int[arraySize];
        logicCount = new int[arraySize];
        int read = 2;
        for (int i = 0; i < arraySize; i++) {
            logicId[i] = getString(data, offset, ControllerMapping.LOGIC_ID);
            this.logicOffset[i] = getInt(data, offset, ControllerMapping.LOGIC_OFFSET);
            logicCount[i] = getInt(data, offset, ControllerMapping.LOGIC_COUNT);
            offset += 3;
            read += 3;
        }
        return read;
    }
}
