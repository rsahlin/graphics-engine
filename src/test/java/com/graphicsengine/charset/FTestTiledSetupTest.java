package com.graphicsengine.charset;

import org.junit.Assert;
import org.junit.Test;

import com.graphicsengine.charset.TiledSheetSetup.TiledSheetMapping;
import com.nucleus.common.StringUtils;
import com.nucleus.utils.DataSerializeUtils;

public class FTestTiledSetupTest {

    @Test
    public void testImportData() {
        String[] data = DataSerializeUtils.createDefaultData(TiledSheetMapping.values());
        TiledSheetSetup setup = createSetup(data);
        assertImportData(data, setup);
    }

    @Test
    public void testExportDataAsString() {
        String[] data = DataSerializeUtils.createDefaultData(TiledSheetMapping.values());
        TiledSheetSetup setup = createSetup(data);
        String[] result = StringUtils.getStringArray(setup.exportDataAsString());
        assertExportData(setup, result);
    }

    private TiledSheetSetup createSetup(String[] data) {
        TiledSheetSetup setup = new TiledSheetSetup();
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
        Assert.assertEquals(expected[TiledSheetMapping.COUNT.getIndex()], Integer.toString(actual.getTileCount()));
        Assert.assertEquals(expected[TiledSheetMapping.TEXTURESOURCE.getIndex()], actual.getTextureRef());
        Assert.assertEquals(expected[TiledSheetMapping.TILEZPOS.getIndex()], Float.toString(actual.getTileZPos()));
        Assert.assertEquals(expected[TiledSheetMapping.TILEWIDTH.getIndex()], Float.toString(actual.getTileWidth()));
        Assert.assertEquals(expected[TiledSheetMapping.TILEHEIGHT.getIndex()], Float.toString(actual.getTileHeight()));
        return TiledSheetMapping.values().length;
    }
}
