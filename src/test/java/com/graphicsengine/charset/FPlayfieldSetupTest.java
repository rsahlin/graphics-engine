package com.graphicsengine.charset;

import org.junit.Assert;
import org.junit.Test;

import com.graphicsengine.charset.PlayfieldSetup.PlayfieldMapping;
import com.graphicsengine.charset.TiledSheetSetup.TiledSheetMapping;
import com.nucleus.common.StringUtils;
import com.nucleus.utils.DataSerializeUtils;

public class FPlayfieldSetupTest extends FTiledSheetSetupTest {

    private final static String[] data = DataSerializeUtils.createDefaultData(TiledSheetMapping.values(),
            PlayfieldMapping.values());

    @Override
    @Test
    public void testImportData() {
        PlayfieldSetup setup = (PlayfieldSetup) DataSerializeUtils.createSetup(data, new PlayfieldSetup());
        assertImportData(data, setup);
    }

    @Override
    @Test
    public void testExportDataAsString() {
        PlayfieldSetup setup = (PlayfieldSetup) DataSerializeUtils.createSetup(data, new PlayfieldSetup());
        String[] result = StringUtils.getStringArray(setup.exportDataAsString());
        assertExportData(setup, result);
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
        DataSerializeUtils
                .assertString(expected, PlayfieldMapping.PLAYFIELDSOURCE, actual.getPlayfieldSource(), offset);
        DataSerializeUtils.assertString(expected, PlayfieldMapping.WIDTH, actual.getMapWidth(), offset);
        DataSerializeUtils.assertString(expected, PlayfieldMapping.HEIGHT, actual.getMapHeight(), offset);
        DataSerializeUtils.assertString(expected, PlayfieldMapping.XPOS, actual.getOriginX(), offset);
        DataSerializeUtils.assertString(expected, PlayfieldMapping.YPOS, actual.getOriginY(), offset);
        DataSerializeUtils.assertString(expected, PlayfieldMapping.ZPOS, actual.getOriginZ(), offset);
        return offset + PlayfieldMapping.values().length;
    }

}
