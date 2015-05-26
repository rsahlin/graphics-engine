package com.graphicsengine.charset;

import com.graphicsengine.dataflow.ArrayInput;

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
     */
    public PlayfieldSetup() {
        super();
    }

    /**
     * Internal method to create the playfield storage, will create storage for mapWidth * mapHeight floats.
     */
    private void createPlayFied() {
        data = new int[mapWidth * mapHeight];
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
    public PlayfieldSetup(int width, int height, float xpos, float ypos, float zpos, float charWidth, float charHeight) {
        super(width * height, charWidth, charHeight);
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
        mapWidth = getInt(data, offset, PlayfieldMapping.WIDTH);
        mapHeight = getInt(data, offset, PlayfieldMapping.HEIGHT);
        this.data = new int[mapWidth * mapHeight];
        xpos = getFloat(data, offset, PlayfieldMapping.XPOS);
        ypos = getFloat(data, offset, PlayfieldMapping.YPOS);
        zpos = getFloat(data, offset, PlayfieldMapping.ZPOS);
        playfieldSource = getString(data, offset, PlayfieldMapping.PLAYFIELDSOURCE);
        return read + PlayfieldMapping.values().length;
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
