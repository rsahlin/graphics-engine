package com.graphicsengine.sprite;

import java.util.HashMap;

import com.graphicsengine.sprite.Sprite.Logic;
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
        setupLogic(spriteController.getSprites());
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
     * Internal method to setup the logic ids, number of logic ids/counts and the offsets.
     * This is used when creating a setup class from existing spritecontroller.
     * 
     * @param sprites
     */
    private void setupLogic(Sprite[] sprites) {
        // Check how many different logic objects
        HashMap<Logic, Integer> logics = new HashMap<Sprite.Logic, Integer>();
        for (Sprite sprite : sprites) {
            Integer i = logics.get(sprite.logic);
            if (i == null) {
                logics.put(sprite.logic, 1);
            } else {
                logics.put(sprite.logic, (i + 1));
            }
        }
        int arrayCount = logics.size();
        createLogicArrays(arrayCount);
        int index = 0;
        int offset = 0;
        for (Logic logic : logics.keySet()) {
            int itemCount = logics.get(logic);
            logicOffset[index] = offset;
            logicCount[index] = itemCount;
            logicId[index] = logic.getLogicId();
        }
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

    /**
     * Internal method that creates the logic arrays containing info regarding logic - it does not
     * add any data. Just creates the arrays.
     * 
     * @param count
     */
    private void createLogicArrays(int count) {
        logicId = new String[count];
        logicOffset = new int[count];
        logicCount = new int[count];
    }

    @Override
    public int importData(String[] data, int offset) {
        count = getInt(data, offset, ControllerMapping.COUNT);
        int arraySize = getInt(data, offset, ControllerMapping.ARRAY_SIZE);
        createLogicArrays(arraySize);
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
        return StringUtils.getString(exportDataAsStringArray());
    }

    @Override
    public String[] exportDataAsStringArray() {
        String[] strArray = new String[logicId.length * 3 + 2];
        strArray[ControllerMapping.COUNT.getIndex()] = Integer.toString(count);
        strArray[ControllerMapping.ARRAY_SIZE.getIndex()] = Integer.toString(logicId.length);
        int offset = 0;
        for (int i = 0; i < logicId.length; i++) {
            strArray[ControllerMapping.LOGIC_COUNT.getIndex() + offset] = Integer.toString(logicCount[i]);
            strArray[ControllerMapping.LOGIC_ID.getIndex() + offset] = logicId[i];
            strArray[ControllerMapping.LOGIC_OFFSET.getIndex() + offset] = Integer.toString(logicOffset[i]);
            offset += 3;
        }
        return strArray;
    }
}
