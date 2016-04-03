package com.graphicsengine.map;

import com.google.gson.annotations.SerializedName;
import com.graphicsengine.dataflow.ArrayInputData;
import com.graphicsengine.io.GraphicsEngineResourcesData;
import com.nucleus.geometry.AttributeUpdater.PropertyMapper;
import com.nucleus.scene.Node;
import com.nucleus.vecmath.Axis;
import com.nucleus.vecmath.Rectangle;

/**
 * The playfield controller that contains a playfield (mesh) and can be put in a scene.
 * It has a map that make up the chars in the playfield.
 * This is the main node for a tiled charmap (playfield) that can be rendered.
 * 
 * @author Richard Sahlin
 *
 */
public class PlayfieldNode extends Node {

    @SerializedName("mapRef")
    /**
     * Reference to map data
     */
    private String mapRef;
    @SerializedName("mapSize")
    /**
     * The size of the map in this controller
     */
    private int[] mapSize = new int[2];

    /**
     * X and Y offset for map, this controls where the first char of the map is.
     */
    @SerializedName("offset")
    private float[] offset;

    /**
     * The rectangle defining the chars, all chars will have same size
     * 4 values = x1,y1 + width and height
     */
    @SerializedName("rect")
    private Rectangle rectangle;

    /**
     * The map data used by this controller.
     */
    transient private int[] mapData;

    public PlayfieldNode() {
    }

    @Override
    public PlayfieldNode createInstance() {
        PlayfieldNode copy = new PlayfieldNode();
        return copy;
    }

    @Override
    public PlayfieldNode copy() {
        PlayfieldNode copy = createInstance();
        copy.set(this);
        return copy;
    }

    /**
     * Copies the values from the source node to this node, this will not copy transient values.
     * 
     * @param source
     */
    protected void set(PlayfieldNode source) {
        super.set(source);
        mapRef = source.mapRef;
        setMapSize(source.mapSize);
        setCharRectangle(source.rectangle);
        setMapOffset(source.offset);

    }
    
    /**
     * Sets the playfield in this controller, creating the map storage if needed, and updates the mesh to contain
     * the charset.
     * 
     * @param resources The scene resources
     */
    public void createPlayfield(GraphicsEngineResourcesData resources) {
        PropertyMapper mapper = new PropertyMapper(getMeshById(getMeshRef()).getMaterial().getProgram());
        Playfield playfieldData = resources.getPlayfield(getMapRef());
        createMap(getMapSize());
        ArrayInputData id = playfieldData.getArrayInput();
        PlayfieldMesh playfield = (PlayfieldMesh) getMeshById(getMeshRef());
        if (id != null) {
            if (mapData == null) {
                mapData = new int[mapSize[Axis.WIDTH.index] * mapSize[Axis.HEIGHT.index]];
            }
            id.copyArray(mapData,
                    mapSize[Axis.WIDTH.index],
                    mapSize[Axis.HEIGHT.index], 0, 0);
            playfield.setCharmap(mapper, getMapData(), 0, 0, getMapData().length);
        } else {
            if (playfieldData.getMap() != null && playfieldData.getMapSize() != null) {
                playfield.setCharmap(mapper, playfieldData, mapSize);
            }
        }
    }

    /**
     * Returns a reference to the map size, do NOT modify these values
     * 
     * @return
     */
    public int[] getMapSize() {
        return mapSize;
    }

    /**
     * Returns a reference to the rectangle defining each char
     * 
     * @return
     */
    public Rectangle getCharRectangle() {
        return rectangle;
    }

    /**
     * Returns a reference to the map data, do NOT modify these values
     * 
     * @return
     */
    public int[] getMapData() {
        return mapData;
    }

    /**
     * Returns the map offset if set, or null
     * The map offset controls where the first char in the map is.
     * 
     * @return
     */
    public float[] getMapOffset() {
        return offset;
    }

    /**
     * Returns the name of the map for this playfieldnode, this is used when importing
     * 
     * @return
     */
    public String getMapRef() {
        return mapRef;
    }

    /**
     * Sets the mapsize from the source values, internal method.
     * This will not create storage for map.
     * 
     * @param mapSize
     */
    private void setMapSize(int[] mapSize) {
        System.arraycopy(mapSize, 0, this.mapSize, 0, 2);
    }

    /**
     * Sets the map offset to that of the offset, values are copied.
     * 
     * @param offset Offset values, or null to remove.
     */
    private void setMapOffset(float[] offset) {
        if (offset == null) {
            this.offset = null;
        }
        if (this.offset == null) {
            this.offset = new float[offset.length];
            System.arraycopy(offset, 0, this.offset, 0, offset.length);
        }
    }

    /**
     * Sets the rectangle defining each char
     * 
     * @param rectangle defining each char
     */
    private void setCharRectangle(Rectangle rectangle) {
        this.rectangle = new Rectangle(rectangle);

    }

    /**
     * Creates the map storage
     * 
     * @param mapSize
     */
    private void createMap(int[] mapSize) {
        mapData = new int[mapSize[Axis.WIDTH.index] * mapSize[Axis.HEIGHT.index]];
    }
}
