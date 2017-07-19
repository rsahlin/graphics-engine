package com.graphicsengine.map;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.graphicsengine.map.Map.MapColor;
import com.graphicsengine.spritemesh.SpriteMesh;
import com.nucleus.assets.AssetManager;
import com.nucleus.geometry.Material;
import com.nucleus.geometry.Mesh;
import com.nucleus.geometry.VertexBuffer;
import com.nucleus.renderer.BufferObjectsFactory;
import com.nucleus.renderer.Configuration;
import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.shader.ShaderProgram;
import com.nucleus.texturing.Texture2D;
import com.nucleus.texturing.TiledTexture2D;
import com.nucleus.vecmath.Axis;
import com.nucleus.vecmath.Rectangle;

/**
 * Old school charactermap based rendering using a texture and quad mesh, the normal way to use the charmap is to create
 * with specified number of chars to cover a certain area.
 * This class has no real functionality besides being able to be rendered - to use the charmap, see
 * {@link PlayfieldNode}
 * 
 * @author Richard Sahlin
 *
 */
public class PlayfieldMesh extends SpriteMesh {

    /**
     * playfield character data, one value for each char - this is the source map that can be used for collision etc.
     * This MUST be in sync with {@link #attributeData}
     */
    private transient Map map;

    private transient int[] playfieldSize = new int[2];

    public static class Builder {

        private NucleusRenderer renderer;
        private PlayfieldNode parent;

        /**
         * Creates a new builder
         * 
         * @param renderer
         * @throws IllegalArgumentException If renderer is null
         */
        public Builder(NucleusRenderer renderer) {
            if (renderer == null) {
                throw new IllegalArgumentException("Renderer may not be null");
            }
            this.renderer = renderer;
        }

        protected void validate() {
            if (parent == null) {
                throw new IllegalArgumentException("Parent has not been set");
            }
        }

        /**
         * Factory method for creating the playfield mesh, after this call the playfield can be rendered, it must
         * be filled with map data.
         * The arguments for creating the mesh are taken from the parent node.
         * 
         * @param The parent node that holds the arguments for creating the mesh.
         * @return The mesh that can be rendered to produce a playfield
         */
        public PlayfieldMesh create(PlayfieldNode parent) throws IOException {
            if (parent == null) {
                throw new IllegalArgumentException("Parent may not be null");
            }
            // TODO Should use assetmanager to fetch program.
            PlayfieldProgram program = new PlayfieldProgram();
            renderer.createProgram(program);
            parent.getMaterial().setProgram(program);
            Texture2D texture = AssetManager.getInstance().getTexture(renderer, parent.getTextureRef());
            PlayfieldMesh playfieldMesh = new PlayfieldMesh();
            playfieldMesh.createMesh(texture, parent.getMaterial(), parent.getMapSize(),
                    parent.getCharRectangle());
            float[] offset = parent.getAnchorOffset();
            Rectangle bounds = playfieldMesh.setupCharmap(parent.getMapSize(), parent.getCharRectangle().getSize(),
                    offset);
            parent.initBounds(bounds);
            if (Configuration.getInstance().isUseVBO()) {
                BufferObjectsFactory.getInstance().createVBOs(renderer, playfieldMesh);
            }
            return playfieldMesh;
        }
    }

    /**
     * Creates a new instance of an empty playfield mesh.
     */
    protected PlayfieldMesh() {
        super();
    }

    /**
     * Creates a copy of the playfield - NOTE this will ONLY create the character and attribute storage.
     * {@link #createMesh(PlayfieldProgram, Texture2D, float[], float[])} and
     * {@link #setupCharmap(int, int, float, float)} MUST be called in order to setup all values.
     * 
     * @param source The source, id, textureRef and character count is taken from here.
     */
    protected PlayfieldMesh(Mesh source) {
        super(source);
        set(source);
    }

    /**
     * Sets the values from the source playfield mesh.
     * Note, this will NOT create the Mesh, it will just set the values so that the mesh can be created.
     * 
     * @param source
     */
    public void set(PlayfieldMesh source) {
        setId(source.getId());
        this.textureRef = source.textureRef;
    }

    /**
     * Creates the mesh for this charmap, each char has the specified width and height, z position.
     * Texture UV is set using 1 / framesX and 1/ framesY
     * 
     * @param texture If tiling should be used this must be instance of {@link TiledTexture2D}
     * @param material
     * @param playfieldSize Number of chars to support in the mesh
     * @param rectangle The rectangle defining a char, all chars will have same size.
     */
    public void createMesh(Texture2D texture, Material material, int[] playfieldSize,
            Rectangle rectangle) {
        int count = playfieldSize[0] * playfieldSize[1];
        this.playfieldSize[Axis.WIDTH.index] = playfieldSize[Axis.WIDTH.index];
        this.playfieldSize[Axis.HEIGHT.index] = playfieldSize[Axis.HEIGHT.index];
        super.createMesh(texture, material, count, rectangle);
    }

    /**
     * Creates and positions the characters using width and height number of chars, starting at xpos, ypos
     * This will set the position for each character, the map can be moved by translating
     * the node it is attached to.
     * Use this method to layout the characters on your visible screen as needed.
     * The chars will be laid out sequentially across the x axis (row based)
     * Before rendering the attributes in the mesh must be updated with attribute data from this class.
     * 
     * @param mapSize width and height of map, in characters
     * @param charSize width and height of each char
     * @param offset Start position of the upper left char, ie the upper left char will have this position.
     * @return Rectangle covering the map.
     * @throws IllegalArgumentException If the size of the map does not match number of chars in this class
     */
    public Rectangle setupCharmap(int[] mapSize, float[] charSize, float[] offset) {
        if (mapSize[Axis.WIDTH.index] * mapSize[Axis.HEIGHT.index] != playfieldSize[Axis.WIDTH.index]
                * playfieldSize[Axis.HEIGHT.index]) {
            throw new IllegalArgumentException("Size of map does not match number of chars in mesh");
        }
        playfieldSize[Axis.WIDTH.index] = mapSize[Axis.WIDTH.index];
        playfieldSize[Axis.HEIGHT.index] = mapSize[Axis.HEIGHT.index];
        int index = 0;
        float currentX = offset[0];
        float currentY = offset[1];
        float startY = currentY;
        for (int y = 0; y < mapSize[1]; y++) {
            currentY = startY;
            for (int x = 0; x < mapSize[0]; x++) {
                attributeData[index + mapper.translateOffset] = currentX;
                attributeData[index + mapper.translateOffset + 1] = currentY;
                index += mapper.attributesPerVertex;
                attributeData[index + mapper.translateOffset] = currentX;
                attributeData[index + mapper.translateOffset + 1] = currentY;
                index += mapper.attributesPerVertex;
                attributeData[index + mapper.translateOffset] = currentX;
                attributeData[index + mapper.translateOffset + 1] = currentY;
                index += mapper.attributesPerVertex;
                attributeData[index + mapper.translateOffset] = currentX;
                attributeData[index + mapper.translateOffset + 1] = currentY;
                index += mapper.attributesPerVertex;
                currentX += charSize[Axis.WIDTH.index];
            }
            currentX = offset[0];
            // TODO handle Y axis going other direction?
            startY -= charSize[Axis.HEIGHT.index];
        }
        //Return rectangle covering the map
        return new Rectangle(offset[0] - (charSize[0] / 2), offset[1] + (charSize[1] / 2), charSize[0] * mapSize[0],
                charSize[1] * mapSize[1]);
    }

    /**
     * Copies char frame index data from the source into this charmap, source data should only include char number.
     * Note that there will be conversion from int[] to float values as the char data is copied.
     * Do NOT use this method for a large number of data when performance is critical.
     * 
     * @param mapper The attribute property mapper
     * @param map Source map data
     * @param flags Flags
     * @param ambient Ambient material properties
     * @param sourceOffset Offset into source where data is read
     * @param destOffset Offset where data is written in this class
     * @param count Number of chars to copy
     * @throws ArrayIndexOutOfBoundsException If source or destination does not contain enough data.
     */
    public void copyCharmap(PropertyMapper mapper, IntBuffer map, ByteBuffer flags, MapColor ambient, int sourceOffset,
            int destOffset, int count) {
        int ambientStride = ambient.getVertexStride();
        int sizePerChar = ambient.getSizePerChar();
        FloatBuffer color = ambient.getColor();
        color.position(0);
        for (int i = 0; i < count; i++) {
            setChar(mapper, destOffset, map.get(sourceOffset), flags.get(sourceOffset));
            setAmbient(mapper, destOffset, color, destOffset * sizePerChar, ambientStride);
            destOffset++;
            sourceOffset++;
        }
    }

    /**
     * Copies char frame index data from the source into this charmap, source data should only include char number.
     * Note that there will be conversion from int[] to float values as the char data is copied.
     * Do NOT use this method for a large number of data when performance is critical.
     * 
     * @param mapper The attribute property mapper
     * @param map Source map data
     * @param flags Flags
     * @param sourceOffset Offset into source where data is read
     * @param destOffset Offset where data is written in this class
     * @param count Number of chars to copy
     * @throws ArrayIndexOutOfBoundsException If source or destination does not contain enough data.
     */
    public void copyCharmap(PropertyMapper mapper, IntBuffer map, ByteBuffer flags, int sourceOffset,
            int destOffset, int count) {
        for (int i = 0; i < count; i++) {
            setChar(mapper, destOffset++, map.get(sourceOffset), flags.getInt(sourceOffset));
            sourceOffset++;
        }
    }

    /**
     * Copies the data from the source map into this class
     * The copy will be done on a row by row basis, adjusting to different size of source and destination.
     * 
     * @param mapper The attribute property mapper
     * @param source Map data will be copied from this
     */
    public void copyCharmap(PropertyMapper mapper, Map source) {
        if (source == null || source.getMapSize() == null) {
            return;
        }
        this.map = source;
        int[] sourceSize = source.getMapSize();

        int height = Math.min(playfieldSize[Axis.HEIGHT.index], sourceSize[Axis.HEIGHT.index]);
        int width = Math.min(playfieldSize[Axis.WIDTH.index], sourceSize[Axis.WIDTH.index]);
        MapColor ambient = source.getAmbient();
        if (ambient != null) {
            for (int y = 0; y < height; y++) {
                copyCharmap(mapper, source.getMap(), source.getFlags(), ambient,
                        y * sourceSize[Axis.WIDTH.index], y * playfieldSize[Axis.WIDTH.index], width);
            }
        } else {
            for (int y = 0; y < height; y++) {
                copyCharmap(mapper, source.getMap(), source.getFlags(),
                        y * sourceSize[Axis.WIDTH.index], y * playfieldSize[Axis.WIDTH.index], width);
            }

        }

    }

    /**
     * Fills a rectangular area with a specific character value.
     * This method is not performance optimized, if a large area shall be filled with high performance then consider
     * using a custom function that write into {@link #attributeData}
     * 
     * @param mapper The attribute property mapper
     * @param x Map start x of fill
     * @param y Map start y of fill
     * @param width With of area to fill
     * @param height Height of area to fill
     * @param fill Fill value
     */
    public void fill(PropertyMapper mapper, int x, int y, int width, int height, int fill, int flags, int[] mapSize) {
        if (x > mapSize[Axis.WIDTH.index] || y > mapSize[Axis.HEIGHT.index]) {
            // Completely outside
            return;
        }
        int startChar = y * width + y;
        if (x + width > mapSize[Axis.WIDTH.index]) {
            width = mapSize[Axis.WIDTH.index] - x;
        }
        if (y + height > mapSize[Axis.HEIGHT.index]) {
            height = mapSize[Axis.HEIGHT.index] - y;
        }
        while (height-- > 0) {
            for (int i = 0; i < width; i++) {
                setChar(mapper, startChar++, fill, flags);
            }
        }
    }

    /**
     * Internal method to set a char at a playfield (charmap) position.
     * This will set the data in both the attribute array and the playfield data.
     * This method shall not be used for a large number of chars or when performance is important.
     * 
     * @param mapper Property mapper for attribute indexes
     * @param pos The playfield position, from 0 to width * height.
     * @param chr The char to set.
     * @param flags Flags for the char
     */
    private void setChar(PropertyMapper mapper, int pos, int chr, int flags) {
        map.getMap().put(pos, chr);
        int destIndex = pos * mapper.attributesPerVertex * ShaderProgram.VERTICES_PER_SPRITE
                + mapper.frameOffset;
        attributeData[destIndex] = chr;
        attributeData[destIndex + 1] = flags;
        destIndex += mapper.attributesPerVertex;
        attributeData[destIndex] = chr;
        attributeData[destIndex + 1] = flags;
        destIndex += mapper.attributesPerVertex;
        attributeData[destIndex] = chr;
        attributeData[destIndex + 1] = flags;
        destIndex += mapper.attributesPerVertex;
        attributeData[destIndex] = chr;
        attributeData[destIndex + 1] = flags;
        destIndex += mapper.attributesPerVertex;
        getVerticeBuffer(BufferIndex.ATTRIBUTES).setDirty(true);
    }

    /**
     * Internal method to set ambient material property at a character position
     * This method shall not be used for a large number of chars or when performance is important.
     * 
     * @param mapper Property mapper for attribute indexes
     * @param pos The playfield position, from 0 to width * height.
     * @param ambient Ambient material
     * @param index Index into ambient array where material should be read
     * @param stride Ambient stride to get to values for next ambient, either 0 or size of ambient data.
     */
    private void setAmbient(PropertyMapper mapper, int pos, FloatBuffer ambient, int index, int stride) {
        int destIndex = pos * mapper.attributesPerVertex * ShaderProgram.VERTICES_PER_SPRITE
                + mapper.colorAmbientOffset;
        for (int i = 0; i < VertexBuffer.INDEXED_QUAD_VERTICES; i++) {
            ambient.get(attributeData, destIndex, 4);
            destIndex += mapper.attributesPerVertex;
            index += stride;
        }
        getVerticeBuffer(BufferIndex.ATTRIBUTES).setDirty(true);
    }

    @Override
    public void destroy() {
        attributeData = null;
        map = null;
        playfieldSize = null;
    }

}
