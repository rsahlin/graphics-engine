package com.graphicsengine.charset;

import org.junit.Test;

import com.graphicsengine.charset.TiledSheetSetup.TiledSheetMapping;
import com.nucleus.common.StringUtils;
import com.nucleus.utils.DataSerializeUtils;

public class FTiledSheetSetupTest {

    private final static String[] data = DataSerializeUtils.createDefaultData(TiledSheetMapping.values());

    @Test
    public void testImportData() {
        TiledSheetSetup setup = (TiledSheetSetup) DataSerializeUtils.createSetup(data, new TiledSheetSetup());
        assertImportData(data, setup);
    }

    @Test
    public void testExportDataAsString() {
        TiledSheetSetup setup = (TiledSheetSetup) DataSerializeUtils.createSetup(data, new TiledSheetSetup());
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
    protected int assertExportData(TiledSheetSetup expected, String[] actual) {
        DataSerializeUtils.assertDataAsString(expected.getTileCount(), actual, TiledSheetMapping.COUNT);
        DataSerializeUtils.assertDataAsString(expected.getTextureRef(), actual, TiledSheetMapping.TEXTURESOURCE);
        DataSerializeUtils.assertDataAsString(expected.getTileZPos(), actual, TiledSheetMapping.TILEZPOS);
        DataSerializeUtils.assertDataAsString(expected.getTileWidth(), actual, TiledSheetMapping.TILEWIDTH);
        DataSerializeUtils.assertDataAsString(expected.getTileHeight(), actual, TiledSheetMapping.TILEHEIGHT);
        return TiledSheetMapping.values().length;
    }

    /**
     * Asserts the setup class as got from import data
     * 
     * @param expected
     * @param actual
     * @return number of values asserted
     */
    protected int assertImportData(String[] expected, TiledSheetSetup actual) {
        DataSerializeUtils.assertString(expected, TiledSheetMapping.COUNT, actual.getTileCount(), 0);
        DataSerializeUtils.assertString(expected, TiledSheetMapping.TEXTURESOURCE, actual.getTextureRef(), 0);
        DataSerializeUtils.assertString(expected, TiledSheetMapping.TILEZPOS, actual.getTileZPos(), 0);
        DataSerializeUtils.assertString(expected, TiledSheetMapping.TILEWIDTH, actual.getTileWidth(), 0);
        DataSerializeUtils.assertString(expected, TiledSheetMapping.TILEHEIGHT, actual.getTileHeight(), 0);

        return TiledSheetMapping.values().length;
    }
}
