package com.graphicsengine.sprite;

import com.nucleus.common.StringUtils;
import com.nucleus.io.DataSetup;
import com.nucleus.types.DataType;

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
    public enum ControllerMapping implements DataIndexer {
        COUNT(0, DataType.INT),
        ARRAY_SIZE(1, DataType.INT),
        LOGIC_ID(2, DataType.STRING),
        LOGIC_OFFSET(3, DataType.INT),
        LOGIC_COUNT(4, DataType.INT);

        private final int index;
        private final DataType type;

        private ControllerMapping(int index, DataType type) {
            this.index = index;
            this.type = type;
        }

        @Override
        public int getIndex() {
            return index;
        }

        @Override
        public DataType getType() {
            return type;
        }
    }

    public SpriteControllerSetup() {
        super();
    }

    /**
     * Creates setup from the existing sprite controller, used when exporting.
     * TODO not finished
     * 
     * @param spriteController
     */
    public SpriteControllerSetup(SpriteController spriteController) {
        this.count = spriteController.getCount();
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

    @Override
    public String exportDataAsString() {
        String[] strArray = new String[ControllerMapping.values().length];
        strArray[ControllerMapping.COUNT.getIndex()] = Integer.toString(count);
        strArray[ControllerMapping.ARRAY_SIZE.getIndex()] = Integer.toString(logicId.length);
        strArray[ControllerMapping.LOGIC_COUNT.getIndex()] = StringUtils.getString(logicCount);
        strArray[ControllerMapping.LOGIC_ID.getIndex()] = StringUtils.getString(logicId);
        strArray[ControllerMapping.LOGIC_OFFSET.getIndex()] = StringUtils.getString(logicOffset);
        return StringUtils.getString(strArray);
    }
}
