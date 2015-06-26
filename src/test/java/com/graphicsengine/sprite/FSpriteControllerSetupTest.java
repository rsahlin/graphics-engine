package com.graphicsengine.sprite;

import junit.framework.Assert;

import org.junit.Test;

import com.graphicsengine.sprite.SpriteControllerSetup.ControllerMapping;
import com.nucleus.common.StringUtils;
import com.nucleus.utils.DataSerializeUtils;

public class FSpriteControllerSetupTest {

    @Test
    public void testImportData() {
        String[] data = DataSerializeUtils.createDefaultData(ControllerMapping.values());
        SpriteControllerSetup setup = createSetup(data);
        assertImportData(data, setup);
    }

    @Test
    public void testExportDataAsString() {
        String[] data = DataSerializeUtils.createDefaultData(ControllerMapping.values());
        SpriteControllerSetup setup = createSetup(data);
        String[] result = StringUtils.getStringArray(setup.exportDataAsString());
        assertExportData(setup, result);
    }

    private SpriteControllerSetup createSetup(String[] data) {
        SpriteControllerSetup setup = new SpriteControllerSetup();
        setup.importData(data, 0);
        return setup;
    }

    /**
     * Asserts the String data as got from export data as string.
     * 
     * @param expected
     * @param actual
     * @return number of values asserted
     */
    protected int assertExportData(SpriteControllerSetup expected, String[] actual) {
        Assert.assertEquals(ControllerMapping.values().length, actual.length);
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
        return ControllerMapping.values().length;
    }

}
