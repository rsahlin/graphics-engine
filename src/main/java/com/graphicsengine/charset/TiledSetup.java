package com.graphicsengine.charset;

import com.graphicsengine.assets.AssetManager;
import com.nucleus.io.DataSetup;
import com.nucleus.io.ExternalReference;
import com.nucleus.texturing.TiledTexture2D;
import com.nucleus.texturing.TiledTextureSetup;

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
        TEXTURESOURCE(4);
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
     * This must be imported separately by using textureRef, exactly how this is implemented is up to the
     * importer/exporter
     * This is only a reference used when charmap is created - not used when exporting.
     * TODO Maybe replace this with a reference to the texture ID and the texture is fetched from assetmanager.
     */
    TiledTextureSetup textureSetup;

    /**
     * Default constructor
     */
    public TiledSetup() {
        super();
    }

    /**
     * Creates a new tiled setup with id.
     * 
     * @param id
     */
    public TiledSetup(String id) {
        super(id);
    }

    void setup(String id, int count, float zpos, float width, float height) {
        this.setId(id);
        this.count = count;
        this.tileZPos = zpos;
        this.tileWidth = width;
        this.tileHeight = height;
    }

    /**
     * Sets the texture source reference from an existing texture, use when exporting.
     * 
     * @param texture
     * @throws IllegalArgumentException If texture source could not be found for the id.
     */
    void setTextureRef(TiledTexture2D texture) {
        ExternalReference source = AssetManager.getInstance().getSourceReference(texture.getId());
        textureRef = source.getId();
    }

    /**
     * Sets number of tiles, tile width and height
     * 
     * @param count
     * @param width
     * @param height
     */
    void setup(int count, float width, float height) {
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
    public void setTexture(TiledTextureSetup textureSetup) {
        this.textureSetup = textureSetup;
    }

    /**
     * Returns the texture setup if one has been set, otherwise null.
     * 
     * @return The texture setup, or null if not set.
     */
    public TiledTextureSetup getTexture() {
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
        return TiledMapping.values().length;
    }

    @Override
    public String exportDataAsString() {
        String d = DEFAULT_DELIMITER;
        return toString(count) + d + toString(tileZPos) + d + toString(tileWidth) + d + toString(tileHeight) + d
                + textureRef;
    }
}
