package com.graphicsengine.charset;

import com.graphicsengine.dataflow.ArrayInput;
import com.nucleus.texturing.Texture2D;
import com.nucleus.texturing.TiledTexture2D;

/**
 * The data needed to create a charmap, use this to make it easier to abstract seralization/creation of maps
 * from loaded data.
 * This class can be used with serialization to decouple io from implementation
 * 
 * @author Richard Sahlin
 *
 */
public class PlayfieldSetup extends TiledSetup {

    public enum PlayfieldMapping implements Indexer {
        PLAYFIELDSOURCE(0),
        WIDTH(1),
        HEIGHT(2),
        XPOS(3),
        YPOS(4),
        ZPOS(5);

        private final int index;

        PlayfieldMapping(int index) {
            this.index = index;
        }

        @Override
        public int getIndex() {
            return index;
        }

    }

    /**
     * Number of components for playfield data.
     */
    private final static int COMPONENTS = 1;

    /**
     * The data, ie the chars for the playfield.
     */
    int[] data;

    /**
     * Width of map in characters
     */
    int mapWidth;
    /**
     * Height of map in characters
     */
    int mapHeight;
    /**
     * Map origin x
     */
    float xpos;
    /**
     * Map origin y
     */
    float ypos;
    /**
     * Map z position
     */
    float zpos;

    /**
     * Reference to node containing the data for the playfield.
     */
    String playfieldSource;

    /**
     * Empty constructor, fill with data by calling importData() method.
     * 
     * @param id The id of this object
     */
    public PlayfieldSetup(String id) {
        super(id);
    }

    /**
     * Internal method to create the playfield storage, will create storage for mapWidth * mapHeight floats.
     */
    private void createPlayFied() {
        data = new int[mapWidth * mapHeight];
    }

    /**
     * Creates a setup from existing playfield, this is used when exporting.
     * 
     * @param playfield
     */
    public PlayfieldSetup(Playfield playfield) {
        setup(playfield);
    }

    /**
     * Sets the data from an existing playfield, used when exporting.
     * 
     * @param playfield
     */
    void setup(Playfield playfield) {
        this.setId(playfield.getId());
        float[] pos = new float[2];
        playfield.getPosition(0, pos, 0);
        setup(playfield.getWidth(), playfield.getHeight(), pos[0], pos[1], playfield.getZPos(),
                playfield.getTileWidth(), playfield.getTileHeight());
        setTextureRef((TiledTexture2D) playfield.getTexture(Texture2D.TEXTURE_0));
    }

    /**
     * Playfield setup
     * 
     * @param width
     * @param height
     * @param xpos Maps xposition, note - not tile xpos
     * @param ypos Maps yposition, note - not tile xpos
     * @param zpos Maps zposition, note - not tile xpos
     * @param charWidth
     * @param charHeight
     */
    void setup(int width, int height, float xpos, float ypos, float zpos, float charWidth, float charHeight) {
        super.setup(width * height, charWidth, charHeight);
        this.mapWidth = width;
        this.mapHeight = height;
        this.xpos = xpos;
        this.ypos = ypos;
        this.zpos = zpos;
        createPlayFied();
    }

    /**
     * Copies data from the array input into the playfield data
     * 
     * @param source
     */
    public void setPlayFieldData(ArrayInput source) {
        source.copyArray(data, COMPONENTS, mapWidth, mapHeight);
    }

    @Override
    public int importData(String[] data, int offset) {
        int read = super.importData(data, offset);
        offset += read;
        playfieldSource = getString(data, offset, PlayfieldMapping.PLAYFIELDSOURCE);
        mapWidth = getInt(data, offset, PlayfieldMapping.WIDTH);
        mapHeight = getInt(data, offset, PlayfieldMapping.HEIGHT);
        this.data = new int[mapWidth * mapHeight];
        xpos = getFloat(data, offset, PlayfieldMapping.XPOS);
        ypos = getFloat(data, offset, PlayfieldMapping.YPOS);
        zpos = getFloat(data, offset, PlayfieldMapping.ZPOS);
        return read + PlayfieldMapping.values().length;
    }

    @Override
    public String exportDataAsString() {
        String str = super.exportDataAsString();
        // If the playfieldSource is null then set a default name.
        if (playfieldSource == null) {
            playfieldSource = "playfield-data-" + getId();
        }
        String d = DEFAULT_DELIMITER;
        return str + d + playfieldSource + d + toString(mapWidth) + d + toString(mapHeight) + d + toString(xpos) + d
                + toString(ypos) + d + toString(zpos);
    }

    /**
     * Returns the name of the playfield source data
     * 
     * @return
     */
    public String getPlayfieldSource() {
        return playfieldSource;
    }

}
