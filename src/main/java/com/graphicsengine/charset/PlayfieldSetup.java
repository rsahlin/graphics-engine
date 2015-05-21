package com.graphicsengine.charset;

import com.graphicsengine.dataflow.ArrayInput;

/**
 * The data needed to create a charmap, use this to make it easier to abstract seralization/creation of maps
 * from loaded data.
 * 
 * @author Richard Sahlin
 *
 */
public class PlayfieldSetup extends TiledSetup {

    /**
     * Number of components for playfield data.
     */
    private final static int COMPONENTS = 1;

    /**
     * The data, ie the chars for the playfield.
     */
    float[] data;

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
     * Creates a new CharMapData from String sources, playfield data storage will be created to fit width and height.
     * 
     * @param width Integer width of map
     * @param height Integer height of map
     * @param xpos float xpos of map (origin)
     * @param ypos float ypos of map (origin)
     * @param zpos float zpos of map
     * @param charWidth float character width
     * @param charHeight float character height
     */
    public PlayfieldSetup(String width, String height, String xpos, String ypos, String zpos, String charWidth,
            String charHeight) {
        super(charWidth, charHeight);
        mapWidth = Integer.parseInt(width);
        mapHeight = Integer.parseInt(height);
        this.xpos = Float.parseFloat(xpos);
        this.ypos = Float.parseFloat(ypos);
        this.zpos = Float.parseFloat(zpos);
        createPlayFied();
    }

    /**
     * Internal method to create the playfield storage, will create storage for mapWidth * mapHeight floats.
     */
    private void createPlayFied() {
        data = new float[mapWidth * mapHeight];
    }

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

}
