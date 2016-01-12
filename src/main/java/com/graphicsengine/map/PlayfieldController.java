package com.graphicsengine.map;

import java.io.IOException;

import com.google.gson.annotations.SerializedName;
import com.graphicsengine.dataflow.ArrayInputData;
import com.graphicsengine.io.GraphicsEngineRootNode;
import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.scene.Node;
import com.nucleus.scene.RootNode;
import com.nucleus.vecmath.Axis;

/**
 * The playfield controller that contains a playfield (mesh) and can be put in a scene.
 * It has a map that make up the chars in the playfield.
 * This is the main node for a tiled charmap (playfield) that can be rendered.
 * 
 * @author Richard Sahlin
 *
 */
public class PlayfieldController extends Node {

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
     * The map data used by this controller.
     */
    transient private int[] mapData;
    /**
     * The mesh that can be rendered
     * TODO Unify controllers that renders a Mesh, with methods for creating the mesh
     */
    @SerializedName("playfield")
    private PlayfieldMesh playfield;

    /**
     * Creates a new empty playfield controller.
     * 
     * @param source
     */
    PlayfieldController(Node source) {
        super(source);
    }

    /**
     * Creates a new playfieldcontroller from the specified source node
     * The created node will have the same id and properties but it will not contain the
     * data, ie the mesh and mapdata will not be copied.
     * Call {@link #createMesh(NucleusRenderer, PlayfieldController, RootNode)} to create the mesh.
     * 
     * @source
     */
    PlayfieldController(PlayfieldController source) {
        super(source);
        mapRef = source.mapRef;
        setMapSize(source.mapSize);
        playfield = new PlayfieldMesh(source.getPlayfieldMesh());
    }

    /**
     * Creates the renderable playfield (mesh)
     * After this call this node can be rendered but the mesh must be filled with data.
     * 
     * @param renderer
     * @param source
     * @param scene
     * @throws IOException
     */
    public void createMesh(NucleusRenderer renderer, PlayfieldController source,
            GraphicsEngineRootNode scene)
            throws IOException {
        playfield = PlayfieldFactory.create(renderer, source, scene);
        addMesh(playfield);
    }

    /**
     * Sets the playfield in this controller, creating the map storage if needed, and updates the mesh to contain
     * the charset.
     * 
     * @param source
     * @param scene
     */
    public void createPlayfield(PlayfieldController source, GraphicsEngineRootNode scene) {
        Playfield playfieldData = scene.getResources().getPlayfield(source.getMapRef());
        createMap(source.getMapSize());
        mapRef = source.getMapRef();
        Playfield p = scene.getResources().getPlayfield(mapRef);
        ArrayInputData id = playfieldData.getArrayInput();
        if (id != null) {
            if (mapData == null) {
                mapData = new int[mapSize[Axis.WIDTH.index] * mapSize[Axis.HEIGHT.index]];
            }
            id.copyArray(mapData,
                    mapSize[Axis.WIDTH.index],
                    mapSize[Axis.HEIGHT.index], 0, 0);
            playfield.setCharmap(getMapData(), 0, 0, getMapData().length);
        } else {
            if (playfieldData.getMap() != null && playfieldData.getMapSize() != null) {
                playfield.setCharmap(playfieldData);
            }
        }
    }

    /**
     * Returns the playfield mesh
     * 
     * @return
     */
    public PlayfieldMesh getPlayfieldMesh() {
        return playfield;
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
     * Returns a reference to the map data, do NOT modify these values
     * 
     * @return
     */
    public int[] getMapData() {
        return mapData;
    }

    /**
     * Returns the name of the map for this playfieldcontroller.
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
     * Creates the map storage and sets the mapsize
     * 
     * @param mapSize
     */
    private void createMap(int[] mapSize) {
        mapData = new int[mapSize[Axis.WIDTH.index] * mapSize[Axis.HEIGHT.index]];
        setMapSize(mapSize);
    }
}
