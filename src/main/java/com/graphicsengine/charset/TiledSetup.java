package com.graphicsengine.charset;

import com.nucleus.io.DataSetup;
import com.nucleus.texturing.TextureSetup;

/**
 * The data for a tiled sheet, used for sprites or characters
 * This class can be used with serialization to decouple io from implementation
 * 
 * @author Richard Sahlin
 *
 */
public class TiledSetup extends DataSetup {

    /**
     * Provides the mapping between external data and the data for a tiled set.
     * 
     * @author Richard Sahlin
     *
     */
    public enum TiledMapping implements Indexer {

        COUNT(0),
        TILEZPOS(1),
        TILEWIDTH(2),
        TILEHEIGHT(3),
        TEXTURESOURCE(4),
        TEXTURE_FRAMES_X(5),
        TEXTURE_FRAMES_Y(6);

        private final int index;

        private TiledMapping(int index) {
            this.index = index;
        }

        @Override
        public int getIndex() {
            return index;
        }

    }

    /**
     * Number of tiles
     */
    int count;
    /**
     * Base zpos for the tile
     */
    float tileZPos;
    /**
     * Width of one tile in world coordinates
     */
    float tileWidth;
    /**
     * Height of one tile in world coordinates
     */
    float tileHeight;
    /**
     * The texture source name
     */
    String textureRef;
    /**
     * Number of frames horizontally in texture
     */
    int textureFramesX;
    /**
     * Number of frames vertically in texture
     */
    int textureFramesY;

    /**
     * This must be imported separately by using textureRef, exactly how this is implemented is up to the
     * importer/exporter
     */
    TextureSetup textureSetup;

    /**
     * Default constructor
     */
    public TiledSetup() {
        super();
    }

    /**
     * 
     * @param count
     * @param width
     * @param height
     */
    public TiledSetup(int count, float width, float height) {
        this.count = count;
        tileWidth = width;
        tileHeight = height;
    }

    /**
     * Returns the number of tiles to create.
     * 
     * @return
     */
    public int getTileCount() {
        return count;
    }

    /**
     * Returns the width of one tile.
     * 
     * @return The width of one tile, in world coordinates
     */
    public float getTileWidth() {
        return tileWidth;
    }

    /**
     * Returns the height of one tile.
     * 
     * @return The height ofone tile, in world coordinates
     */
    public float getTileHeight() {
        return tileHeight;
    }

    /**
     * Returns the texture source
     * 
     * @return
     */
    public String getTextureRef() {
        return textureRef;
    }

    /**
     * Returns the number of frames horizontally in the texture, ie 10 if there are 10 frames horizontally in the
     * texture.
     * 
     * @return Number of frames on x axis in texture.
     */
    public int getFramesX() {
        return textureFramesX;
    }

    /**
     * Returns the number of frames vertically in the texture, ie 10 if there are 10 frames horizontally in the
     * texture.
     * 
     * @return Number of frames on y axis in texture.
     */
    public int getFramesY() {
        return textureFramesY;
    }

    /**
     * Returns the base zposition for each tile.
     * 
     * @return
     */
    public float getTileZPos() {
        return tileZPos;
    }

    /**
     * Sets the texture setup for this tiled object, the texture setup must be imported.
     * 
     * @param textureSetup texture setup data, shall contain imported data.
     */
    public void setTexture(TextureSetup textureSetup) {
        this.textureSetup = textureSetup;
    }

    /**
     * Returns the texture setup if one has been set, otherwise null.
     * 
     * @return The texture setup, or null if not set.
     */
    public TextureSetup getTexture() {
        return textureSetup;
    }

    @Override
    public int importData(String[] data, int offset) {
        // int read = super.importData(data, offset);
        // offset += read;
        count = getInt(data, offset, TiledMapping.COUNT);
        tileZPos = getFloat(data, offset, TiledMapping.TILEZPOS);
        tileWidth = getFloat(data, offset, TiledMapping.TILEWIDTH);
        tileHeight = getFloat(data, offset, TiledMapping.TILEHEIGHT);
        textureRef = getString(data, offset, TiledMapping.TEXTURESOURCE);
        textureFramesX = getInt(data, offset, TiledMapping.TEXTURE_FRAMES_X);
        textureFramesY = getInt(data, offset, TiledMapping.TEXTURE_FRAMES_Y);
        return TiledMapping.values().length;
    }

    @Override
    public String exportDataAsString() {
        // TODO Auto-generated method stub
        return null;
    }
}
