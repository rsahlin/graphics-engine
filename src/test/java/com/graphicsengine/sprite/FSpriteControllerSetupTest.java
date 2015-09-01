package com.graphicsengine.sprite;

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import com.graphicsengine.sprite.SpriteControllerSetup.ControllerMapping;
import com.nucleus.common.StringUtils;
import com.nucleus.utils.DataSerializeUtils;

public class FSpriteControllerSetupTest {

    protected static Random random = new Random(System.currentTimeMillis());
    private final static String[] data = createDefaultData();

    protected final static String[] createDefaultData() {

        int count = 100 + random.nextInt(10);
        int arraySize = 1 + random.nextInt(10);
        String[] data = new String[arraySize * 3 + 2];
        int index = 0;
        data[index] = Integer.toString(count);
        data[index + ControllerMapping.ARRAY_SIZE.getIndex()] = Integer.toString(arraySize);
        for (int i = 0; i < arraySize; i++) {
            data[index + ControllerMapping.LOGIC_COUNT.getIndex()] = Integer.toString((count / arraySize));
            data[index + ControllerMapping.LOGIC_OFFSET.getIndex()] = Integer.toString(index);
            data[index + ControllerMapping.LOGIC_ID.getIndex()] = "SPRITE";
            index += 3;
        }
        return data;
    }

    @Test
    public void testImportData() {
        SpriteControllerSetup setup = (SpriteControllerSetup) DataSerializeUtils.createSetup(data,
                new SpriteControllerSetup());
        assertImportData(data, setup);
    }

    @Test
    public void testExportDataAsString() {
        SpriteControllerSetup setup = (SpriteControllerSetup) DataSerializeUtils.createSetup(data,
                new SpriteControllerSetup());
        String[] result = StringUtils.getStringArray(setup.exportDataAsString());
        assertExportData(data, result);
    }

    /**
     * Asserts the String data as got from export data as string.
     * 
     * @param expected
     * @param actual
     * @return number of values asserted
     */
    protected int assertExportData(String[] expected, String[] actual) {
        Assert.assertArrayEquals(expected, actual);
        return actual.length;
    }

    /**
     * Asserts the setup class as got from import data
     * 
     * @param expected
     * @param actual
     * @return number of values asserted
     */
    protected int assertImportData(String[] expected, SpriteControllerSetup actual) {
        DataSerializeUtils.assertString(expected, ControllerMapping.COUNT, actual.getCount(), 0);
        int arraySize = actual.getLogicCount().length;
        DataSerializeUtils.assertString(expected, ControllerMapping.ARRAY_SIZE, arraySize, 0);
        int offset = 0;
        for (int i = 0; i < arraySize; i++) {
            DataSerializeUtils.assertString(expected, ControllerMapping.LOGIC_COUNT, actual.getLogicCount()[i], offset);
            DataSerializeUtils.assertString(expected, ControllerMapping.LOGIC_ID, actual.getLogicId()[i], offset);
            DataSerializeUtils.assertString(expected, ControllerMapping.LOGIC_OFFSET, actual.getLogicOffset()[i],
                    offset);
            offset += 3;
        }

        return expected.length;
    }

}
