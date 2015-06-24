package com.graphicsengine.charset;

import junit.framework.Assert;

import org.junit.Test;

import com.graphicsengine.charset.PlayfieldSetup.PlayfieldMapping;
import com.graphicsengine.charset.TiledSheetSetup.TiledSheetMapping;
import com.nucleus.common.StringUtils;
import com.nucleus.utils.DataSerializeUtils;

public class FPlayfieldSetupTest extends FTestTiledSetupTest {
    @Override
    @Test
    public void testImportData() {
        String[] data = DataSerializeUtils.createDefaultData(TiledSheetMapping.values(), PlayfieldMapping.values());
        PlayfieldSetup setup = createSetup(data);
        assertImportData(data, setup);
    }

    @Override
    @Test
    public void testExportDataAsString() {
        String[] data = DataSerializeUtils.createDefaultData(TiledSheetMapping.values(), PlayfieldMapping.values());
        PlayfieldSetup setup = createSetup(data);
        String[] result = StringUtils.getStringArray(setup.exportDataAsString());
        assertExportData(setup, result);
    }

    private PlayfieldSetup createSetup(String[] data) {
        PlayfieldSetup setup = new PlayfieldSetup();
        setup.importData(data, 0);
        return setup;
    }

    /**
     * Asserts the String data as got from export data as string.
     * 
     * @param expected
     * @param actual
     */
    protected int assertExportData(PlayfieldSetup expected, String[] actual) {
        Assert.assertEquals(TiledSheetMapping.values().length + PlayfieldMapping.values().length, actual.length);
        int offset = super.assertExportData(expected, actual);
        DataSerializeUtils.assertDataAsString(expected.getPlayfieldSource(), actual, PlayfieldMapping.PLAYFIELDSOURCE,
                offset);
        DataSerializeUtils.assertDataAsString(expected.getMapWidth(), actual, PlayfieldMapping.WIDTH, offset);
        DataSerializeUtils.assertDataAsString(expected.getMapHeight(), actual, PlayfieldMapping.HEIGHT, offset);
        DataSerializeUtils.assertDataAsString(expected.getOriginX(), actual, PlayfieldMapping.XPOS, offset);
        DataSerializeUtils.assertDataAsString(expected.getOriginY(), actual, PlayfieldMapping.YPOS, offset);
        DataSerializeUtils.assertDataAsString(expected.getOriginZ(), actual, PlayfieldMapping.ZPOS, offset);
        return offset + PlayfieldMapping.values().length;
    }

    /**
     * Asserts the setup class as got from import data
     * 
     * @param expected
     * @param actual
     * @return number of values asserted
     */
    protected int assertImportData(String[] expected, PlayfieldSetup actual) {
        Assert.assertEquals(TiledSheetMapping.values().length + PlayfieldMapping.values().length, expected.length);
        int offset = super.assertImportData(expected, actual);
        Assert.assertEquals(expected[offset + PlayfieldMapping.PLAYFIELDSOURCE.getIndex()], actual.getPlayfieldSource());
        Assert.assertEquals(expected[offset + PlayfieldMapping.WIDTH.getIndex()],
                Integer.toString(actual.getMapWidth()));
        Assert.assertEquals(expected[offset + PlayfieldMapping.HEIGHT.getIndex()],
                Integer.toString(actual.getMapHeight()));
        Assert.assertEquals(expected[offset + PlayfieldMapping.XPOS.getIndex()], Float.toString(actual.getOriginX()));
        Assert.assertEquals(expected[offset + PlayfieldMapping.YPOS.getIndex()], Float.toString(actual.getOriginY()));
        Assert.assertEquals(expected[offset + PlayfieldMapping.ZPOS.getIndex()], Float.toString(actual.getOriginZ()));
        return offset + PlayfieldMapping.values().length;
    }

}
