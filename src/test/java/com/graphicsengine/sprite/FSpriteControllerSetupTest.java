package com.graphicsengine.sprite;

import org.junit.Test;

import com.graphicsengine.sprite.SpriteControllerSetup.ControllerMapping;
import com.nucleus.common.StringUtils;
import com.nucleus.utils.DataSerializeUtils;

public class FSpriteControllerSetupTest {

    private final static String[] data = DataSerializeUtils.createDefaultData(ControllerMapping.values());

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
        assertExportData(setup, result);
    }

    /**
     * Asserts the String data as got from export data as string.
     * 
     * @param expected
     * @param actual
     * @return number of values asserted
     */
    protected int assertExportData(SpriteControllerSetup expected, String[] actual) {
        DataSerializeUtils.assertDataAsString(expected.getCount(), actual, ControllerMapping.COUNT);
        DataSerializeUtils.assertDataAsString(expected.getLogicCount(), actual, ControllerMapping.LOGIC_COUNT);
        DataSerializeUtils.assertDataAsString(expected.getLogicOffset(), actual, ControllerMapping.LOGIC_OFFSET);
        DataSerializeUtils.assertDataAsString(expected.getLogicId(), actual, ControllerMapping.LOGIC_ID);
        return ControllerMapping.values().length;
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
        DataSerializeUtils.assertString(expected, ControllerMapping.LOGIC_COUNT, actual.getLogicCount(), 0);
        DataSerializeUtils.assertString(expected, ControllerMapping.LOGIC_OFFSET, actual.getLogicOffset(), 0);
        DataSerializeUtils.assertString(expected, ControllerMapping.LOGIC_ID, actual.getLogicId(), 0);
        return ControllerMapping.values().length;
    }

}
