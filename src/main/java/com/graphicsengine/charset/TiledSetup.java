package com.graphicsengine.charset;

import com.nucleus.texturing.TextureSetup;

/**
 * The data for a tiled sheet, used for sprites or characters
 * 
 * @author Richard Sahlin
 *
 */
public class TiledSetup {

    /**
     * Number of tiles
     */
    int count;
    /**
     * Width of one tile in world coordinates
     */
    float tileWidth;
    /**
     * Height of one tile in world coordinates
     */
    float tileHeight;
    /**
     * The texture source data
     */
    TextureSetup textureSource;
    /**
     * Number of frames horizontally in texture
     */
    int textureFramesX;
    /**
     * Number of frames vertically in texture
     */
    int textureFramesY;

    /**
     * Creates a new tiled setup with the specified tile width and height
     * 
     * @param count Integer, total number of tiles
     * @param width Float, Width of each tile
     * @param height Float, of each tile
     */
    public TiledSetup(String count, String width, String height) {
        this.count = Integer.parseInt(count);
        this.tileWidth = Float.parseFloat(width);
        this.tileHeight = Float.parseFloat(height);
    }

    /**
     * Creates a new tiled setup with the specified tile width and height
     * 
     * @param width Float, Width of each tile
     * @param height Float, of each tile
     */
    public TiledSetup(String width, String height) {
        this.tileWidth = Float.parseFloat(width);
        this.tileHeight = Float.parseFloat(height);
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
     * Set texture source image name, number of frames in x and y, resulution bias
     * 
     * @param ource Name of the source of the texture, usually an image
     * @param framesX Integer number of frames horizontally in source image.
     * @param framesY Integer number of frames vertically in source image.
     * @param resolution The resolution of source images
     * @param levels Number of mipmap levels to use.
     */
    public void setTextureSource(TextureSetup textureSource, String framesX, String framesY) {
        this.textureSource = textureSource;
        this.textureFramesX = Integer.parseInt(framesX);
        this.textureFramesY = Integer.parseInt(framesY);
    }

}
