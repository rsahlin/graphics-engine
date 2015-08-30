package com.graphicsengine.charset;

import com.graphicsengine.tiledsprite.TiledSpriteController;
import com.graphicsengine.tiledsprite.TiledSpriteSheet;
import com.nucleus.assets.AssetManager;
import com.nucleus.common.StringUtils;
import com.nucleus.io.DataSetup;
import com.nucleus.io.ExternalReference;
import com.nucleus.texturing.Texture2D;
import com.nucleus.texturing.TiledTextureSetup;
import com.nucleus.types.DataType;

/**
 * The data for a tiled sheet, used for sprites or characters
 * This class can be used with serialization to decouple io from implementation
 * 
 * @author Richard Sahlin
 *
 */
public class TiledSheetSetup extends DataSetup {

    /**
     * Provides the mapping between external data and the data for a tiled set.
     * 
     * @author Richard Sahlin
     *
     */
    public enum TiledSheetMapping implements DataIndexer {

        COUNT(0, DataType.INT),
        TILEZPOS(1, DataType.FLOAT),
        TILEWIDTH(2, DataType.FLOAT),
        TILEHEIGHT(3, DataType.FLOAT),
        TEXTURESOURCE(4, DataType.STRING);

        private final int index;
        private final DataType type;

        private TiledSheetMapping(int index, DataType type) {
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
    public TiledSheetSetup() {
        super();
    }

    /**
     * Creates a new tiled setup with id.
     * 
     * @param id
     */
    public TiledSheetSetup(String id) {
        super(id);
    }

    /**
     * Creates the setup from an existing tiled sprite controller.
     * 
     * @param sprites
     */
    public TiledSheetSetup(TiledSpriteController sprites) {
        Texture2D texture = sprites.getSpriteSheet().getTexture(Texture2D.TEXTURE_0);
        TiledSpriteSheet mesh = sprites.getSpriteSheet();
        setup(sprites.getId(), sprites.getSpriteSheet().getCount(), mesh.getAnchor()[2], mesh.getSize()[0],
                mesh.getSize()[1], texture);
    }

    /**
     * Creates the setup data from the specified parameters, this can be used to create a tiled sheet.
     * 
     * @param id
     * @param count
     * @param zpos
     * @param width
     * @param height
     * @param textureRef
     */
    public void setup(String id, int count, float zpos, float width, float height, Texture2D textureRef) {
        this.setId(id);
        this.count = count;
        this.tileZPos = zpos;
        this.tileWidth = width;
        this.tileHeight = height;
        setTextureRef(textureRef);
    }

    /**
     * Sets the texture source reference from an existing texture, use when exporting.
     * 
     * @param texture
     * @throws IllegalArgumentException If texture source could not be found for the id.
     */
    void setTextureRef(Texture2D texture) {
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
        count = getInt(data, offset, TiledSheetMapping.COUNT);
        tileZPos = getFloat(data, offset, TiledSheetMapping.TILEZPOS);
        tileWidth = getFloat(data, offset, TiledSheetMapping.TILEWIDTH);
        tileHeight = getFloat(data, offset, TiledSheetMapping.TILEHEIGHT);
        textureRef = getString(data, offset, TiledSheetMapping.TEXTURESOURCE);
        return TiledSheetMapping.values().length;
    }

    @Override
    public String exportDataAsString() {
        return StringUtils.getString(exportDataAsStringArray());
    }

    @Override
    public String[] exportDataAsStringArray() {
        String[] strArray = new String[TiledSheetMapping.values().length];
        setData(strArray, TiledSheetMapping.COUNT, count);
        setData(strArray, TiledSheetMapping.TILEZPOS, tileZPos);
        setData(strArray, TiledSheetMapping.TILEWIDTH, tileWidth);
        setData(strArray, TiledSheetMapping.TILEHEIGHT, tileHeight);
        setData(strArray, TiledSheetMapping.TEXTURESOURCE, textureRef);
        return strArray;
    }
}
