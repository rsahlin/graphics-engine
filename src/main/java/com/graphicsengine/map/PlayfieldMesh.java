package com.graphicsengine.map;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.graphicsengine.map.Map.MapColor;
import com.graphicsengine.spritemesh.SpriteMesh;
import com.nucleus.assets.AssetManager;
import com.nucleus.bounds.Bounds;
import com.nucleus.bounds.RectangularBounds;
import com.nucleus.geometry.Mesh;
import com.nucleus.geometry.shape.RectangleShapeBuilder;
import com.nucleus.geometry.shape.RectangleShapeBuilder.RectangleConfiguration;
import com.nucleus.opengl.GLException;
import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.shader.ShaderProperty.PropertyMapper;
import com.nucleus.texturing.Texture2D;
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

    public static class Builder extends Mesh.Builder<Mesh> {

        protected int[] mapSize;
        protected Rectangle charRectangle;
        /**
         * Map offset, upper left char will be offset by this amount.
         */
        protected float[] offset;

        /**
         * Sets the size, width and height, of the map
         * 
         * @param width
         * @param height
         * @return
         */
        public Builder setMapSize(int width, int height) {
            mapSize = new int[] { width, height };
            return this;
        }

        /**
         * Sets the size, width and height, of the map
         * This will set the mode, vertice and element count in #{@link Builder}
         * 
         * @param mapSize width and height of map
         * @param charRect size of each char
         * @return
         */
        public Builder setMap(int[] mapSize, Rectangle charRect) {
            this.mapSize = new int[] { mapSize[0], mapSize[1] };
            this.charRectangle = charRect;
            int charCount = mapSize[0] * mapSize[1];
            setElementMode(Mode.TRIANGLES, charCount * RectangleShapeBuilder.QUAD_VERTICES, 0,
                    charCount * RectangleShapeBuilder.QUAD_ELEMENTS);
            return this;
        }

        /**
         * Sets the x and y offset of the starting position for the chars in the map.
         * 
         * @param x
         * @param y
         * @return
         */
        public Builder setOffset(float x, float y) {
            this.offset = new float[] { x, y };
            return this;
        }

        /**
         * Sets the x and y offset of the starting position for the chars in the map.
         * 
         * @param offset
         * @return
         */
        public Builder setOffset(float[] offset) {
            this.offset = new float[] { offset[0], offset[1] };
            return this;
        }

        /**
         * Internal constructor - avoid using directly if the mesh should belong to a specific node type.
         * Use
         * {@link PlayfieldNode#createMeshBuilder(NucleusRenderer, PlayfieldNode)}
         * 
         * @param renderer
         */
        public Builder(NucleusRenderer renderer) {
            super(renderer);
        }

        @Override
        protected void validate() {
            super.validate();
            if (mapSize == null || charRectangle == null) {
                throw new IllegalArgumentException("Null argument: " + mapSize + ", " + charRectangle);
            }
        }

        @Override
        public Mesh create() throws IOException, GLException {
            if (program == null) {
                program = AssetManager.getInstance().getProgram(renderer.getGLES(), new PlayfieldProgram());
            }
            RectangleConfiguration configuration = new RectangleShapeBuilder.RectangleConfiguration(charRectangle,
                    RectangleShapeBuilder.DEFAULT_Z, mapSize[0] * mapSize[1], 0);
            configuration.enableVertexIndex(true);
            RectangleShapeBuilder shapeBuilder = new RectangleShapeBuilder(configuration);
            setShapeBuilder(shapeBuilder);
            PlayfieldMesh mesh = (PlayfieldMesh) super.create();
            mesh.playfieldSize = mapSize;
            mesh.setupCharmap(new PropertyMapper(program), charRectangle.getSize(), offset);
            return mesh;
        }

        @Override
        protected Mesh createMesh() {
            return new PlayfieldMesh();
        }

        @Override
        public Bounds createBounds() {
            // Return rectangle covering the map
            float[] charSize = charRectangle.getSize();
            Rectangle rect = new Rectangle(offset[0] - (charSize[0] / 2), offset[1] + (charSize[1] / 2),
                    charSize[0] * mapSize[0],
                    charSize[1] * mapSize[1]);
            return new RectangularBounds(rect);
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
     * Creates and positions the characters using width and height number of chars, starting at xpos, ypos
     * This will set the position for each character, the map can be moved by translating
     * the node it is attached to.
     * Use this method to layout the characters on your visible screen as needed.
     * The chars will be laid out sequentially across the x axis (row based)
     * Before rendering the attributes in the mesh must be updated with attribute data from this class.
     * 
     * @param mapper
     * @param charSize width and height of each char
     * @param offset Start position of the upper left char, ie the upper left char will have this position.
     * @throws IllegalArgumentException If the size of the map does not match number of chars in this class
     */
    public void setupCharmap(PropertyMapper mapper, float[] charSize, float[] offset) {
        if (playfieldSize == null || playfieldSize[0] == 0 || playfieldSize[1] == 0) {
            throw new IllegalArgumentException(
                    "Invalid map size " + (playfieldSize != null ? playfieldSize[0] + playfieldSize[1] : "null"));
        }
        int charNumber = 0;
        float[] position = new float[] { offset[0], offset[1], 0 };
        float startY = offset[1];
        for (int y = 0; y < playfieldSize[1]; y++) {
            position[1] = startY;
            for (int x = 0; x < playfieldSize[0]; x++) {
                setAttribute3(charNumber++, mapper.translateOffset, position, 0, mapper.attributesPerVertex);
                position[0] += charSize[Axis.WIDTH.index];
            }
            position[0] = offset[0];
            // TODO handle Y axis going other direction?
            startY -= charSize[Axis.HEIGHT.index];
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
     * @param emissive Emissive material properties
     * @param sourceOffset Offset into source where data is read
     * @param destOffset Offset where data is written in this class
     * @param count Number of chars to copy
     * @throws ArrayIndexOutOfBoundsException If source or destination does not contain enough data.
     */
    public void copyCharmap(PropertyMapper mapper, IntBuffer map, ByteBuffer flags, MapColor emissive, int sourceOffset,
            int destOffset, int count) {
        int emissiveStride = emissive.getVertexStride();
        int sizePerChar = emissive.getSizePerChar();
        FloatBuffer color = emissive.getColor();
        color.position(0);
        for (int i = 0; i < count; i++) {
            setChar(mapper, destOffset, map.get(sourceOffset), flags.get(sourceOffset));
            setEmissive(mapper, destOffset, color, destOffset * sizePerChar, emissiveStride);
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
        MapColor emissive = source.getEmissive();
        if (emissive != null) {
            for (int y = 0; y < height; y++) {
                copyCharmap(mapper, source.getMap(), source.getFlags(), emissive,
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
        setAttribute2(pos, mapper.frameOffset, new float[] { chr, flags }, 0, mapper.attributesPerVertex);
    }

    /**
     * Internal method to set emissive material property at a character position
     * This method shall not be used for a large number of chars or when performance is important.
     * 
     * @param mapper Property mapper for attribute indexes
     * @param pos The playfield position, from 0 to width * height.
     * @param emissive Emissive material
     * @param index Index into emissive array where material should be read
     * @param stride Emissive stride to get to values for next emissive, either 0 or size of emissive data.
     */
    private void setEmissive(PropertyMapper mapper, int pos, FloatBuffer emissive, int index, int stride) {
        float[] color = new float[4];
        emissive.position(index);
        emissive.get(color);
        setAttribute4(pos, mapper.emissiveOffset, color, 0, mapper.attributesPerVertex);
    }

    @Override
    public void destroy(NucleusRenderer renderer) {
        super.destroy(renderer);
        map = null;
        playfieldSize = null;
    }

}
