package com.graphicsengine.map;

import com.google.gson.annotations.SerializedName;
import com.graphicsengine.dataflow.ArrayInputData;
import com.graphicsengine.io.GraphicsEngineRootNode;
import com.nucleus.geometry.AttributeUpdater.PropertyMapper;
import com.nucleus.scene.Node;
import com.nucleus.vecmath.Axis;

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
     * Width and height of each char.
     */
    @SerializedName("charSize")
    protected float[] charSize = new float[2];

    /**
     * The map data used by this controller.
     */
    transient private int[] mapData;

    public PlayfieldNode() {
    }

    /**
     * Creates a new instance of this node.
     * This will be a new empty instance.
     * 
     * @return New instance of this node
     */
    @Override
    public PlayfieldNode createInstance() {
        PlayfieldNode copy = new PlayfieldNode();
        copy.set(this);
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
        setCharSize(source.charSize);
    }
    /**
     * Sets the playfield in this controller, creating the map storage if needed, and updates the mesh to contain
     * the charset.
     * 
     * @param scene
     */
    public void createPlayfield(GraphicsEngineRootNode scene) {
        PropertyMapper mapper = new PropertyMapper(getMeshById(getMeshRef()).getMaterial().getProgram());
        Playfield playfieldData = scene.getResources().getPlayfield(getMapRef());
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
     * Returns a reference to the char size, do NOT modify these values
     * 
     * @return
     */
    public float[] getCharSize() {
        return charSize;
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
     * Sets the charsize from the source values, internal method.
     * 
     * @param charSize Width and height of chars
     */
    private void setCharSize(float[] charSize) {
        System.arraycopy(charSize, 0, this.charSize, 0, 2);

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
