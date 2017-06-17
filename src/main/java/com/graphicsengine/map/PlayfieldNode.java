package com.graphicsengine.map;

import java.io.FileNotFoundException;

import com.google.gson.annotations.SerializedName;
import com.graphicsengine.io.GraphicsEngineResourcesData;
import com.nucleus.SimpleLogger;
import com.nucleus.geometry.AttributeUpdater.PropertyMapper;
import com.nucleus.io.ExternalReference;
import com.nucleus.mmi.ClickListener;
import com.nucleus.scene.Node;
import com.nucleus.scene.NodeException;
import com.nucleus.scene.RootNode;
import com.nucleus.vecmath.Matrix;
import com.nucleus.vecmath.Rectangle;

/**
 * The playfield node that contains a mesh and can be put in a scene.
 * It has a map that make up the chars in the playfield (mesh) - the map in this class
 * can be larger than the map displayed by the mesh to create scrolling.
 * This is the main node for a tiled charmap (playfield) that can be rendered.
 * 
 * @author Richard Sahlin
 *
 */
public class PlayfieldNode extends Node implements ClickListener {
    public static final String MAPREF = "mapRef";
    public static final String ANCHOR = "anchor";

    public enum Anchor {
        CENTER_XY();
    }

    /**
     * Reference to map data
     */
    @SerializedName(MAPREF)
    private ExternalReference mapRef;
    /**
     * The size of the map in this controller
     */
    @SerializedName(Map.MAPSIZE)
    private int[] mapSize = new int[2];

    /**
     * X and Y anchor for map, this controls where the first char of the map is.
     * This is used when constructing the mesh
     */
    @SerializedName(ANCHOR)
    private Anchor anchor;

    /**
     * The rectangle defining the chars, all chars will have same size
     * 4 values = x1,y1 + width and height
     */
    @SerializedName(Rectangle.RECT)
    private Rectangle rectangle;

    /**
     * The map used by this controller.
     */
    transient private Map map;

    public PlayfieldNode() {
    }

    @Override
    public PlayfieldNode createInstance(RootNode root) {
        PlayfieldNode copy = new PlayfieldNode();
        copy.setRootNode(root);
        return copy;
    }

    @Override
    public PlayfieldNode copy(RootNode root) {
        PlayfieldNode copy = createInstance(root);
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
        setMeshAnchor(source.anchor);

    }

    /**
     * Sets the map in this node, creating the map storage if needed, and updates the mesh to contain
     * the charset.
     * 
     * @param resources The scene resources
     * @throws NodeException If referenced map can not be loaded.
     */
    public void createMap(GraphicsEngineResourcesData resources) throws NodeException {
        PropertyMapper mapper = new PropertyMapper(getMeshes().get(0).getMaterial().getProgram());
        try {
            map = MapFactory.createMap(mapRef);
            PlayfieldMesh playfield = (PlayfieldMesh) getMeshes().get(0);
            if (map.getMap() != null && map.getMapSize() != null) {
                playfield.copyCharmap(mapper, map);
            }
        } catch (FileNotFoundException e) {
            throw new NodeException(e);
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
    public Map getMap() {
        return map;
    }

    /**
     * Returns the map anchor, or null
     * The map offset controls where the first char in the map is.
     * 
     * @return
     */
    public Anchor getMeshAnchor() {
        return anchor;
    }

    /**
     * Returns the anchor value, this is the position of the first character based on the anchor, size of map and size
     * of each char.
     * 
     * @return Position of upper left char
     */
    public float[] getAnchorOffset() {
        Anchor a = anchor == null ? Anchor.CENTER_XY : anchor;
        switch (a) {
        case CENTER_XY:
            return new float[] {
                    -(getMapSize()[0] >>> 1) * getCharRectangle().getValues()[Rectangle.WIDTH],
                    (getMapSize()[1] >>> 1) * getCharRectangle().getValues()[Rectangle.HEIGHT] };
        default:
            throw new IllegalArgumentException("Not implemented for anchor: " + a);
        }

    }

    /**
     * Returns the name of the map for this playfieldnode, this is used when importing
     * 
     * @return
     */
    public ExternalReference getMapRef() {
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
     * Sets the map offset, this controls how the mesh is created from chars
     * 
     * @param anchor new anchor value
     */
    private void setMeshAnchor(Anchor anchor) {
        this.anchor = anchor;
    }

    /**
     * Sets the rectangle defining each char
     * 
     * @param rectangle defining each char
     */
    private void setCharRectangle(Rectangle rectangle) {
        this.rectangle = new Rectangle(rectangle);

    }

    @Override
    public boolean onClick(float[] position) {
        float[] inverse = new float[16];
        if (Matrix.invertM(inverse, 0, getModelMatrix(), 0)) {
            SimpleLogger.d(getClass(), "Pointer input, got inverse matrix");
            float[] vec2 = new float[2];
            Matrix.transformVec2(inverse, 0, position, vec2, 1);
            int[] mapPos = getMapPos(vec2);
            if (mapPos != null) {
                map.logMapPosition(mapPos[0], mapPos[1]);
            }
            SimpleLogger.d(getClass(), "Vec2 " + vec2[0] + ", " + vec2[1]);
        } else {
            SimpleLogger.d(getClass(), "Could not invert matrix!!!!!!!!!!!!!!!!");
        }
        return false;
    }

    /**
     * Returns the map x and y position for the specified (normalized) screen position.
     * 
     * @param position
     * @return Map x and y position for the specified screen position.
     * null if position is outside map, or model matrix can not be inversed
     * 
     */
    private int[] getMapPos(float[] position) {
        float[] inverse = new float[16];
        if (Matrix.invertM(inverse, 0, getModelMatrix(), 0)) {
            float[] vec2 = new float[2];
            Matrix.transformVec2(inverse, 0, position, vec2, 1);
            float[] offset = getAnchorOffset();
            float deltaX = position[0] - offset[0];
            // Y axis going up
            float deltaY = offset[1] - position[1];
            float[] charSize = rectangle.getSize();
            int x = (int) (deltaX / charSize[0]);
            int y = (int) (deltaY / charSize[1]);
            if ((x < 0) || (x > mapSize[0]) || (y < 0) || (y > mapSize[1])) {
                SimpleLogger.d(getClass(), "Delta:" + deltaX + ", " + deltaY);
                return null;
            }
            return new int[] { x, y };
        } else {
            SimpleLogger.d(getClass(), "Could not invert matrix!!!!!!!!!!!!!!!!");
        }
        return null;
    }

    /*
     * @Override
     * protected boolean onPointerEvent(MMIPointerEvent event) {
     * if (isClicked(event.getPointerData().getCurrentPosition())) {
     * switch (event.getAction()) {
     * case ACTIVE:
     * float[] inverse = new float[16];
     * if (Matrix.invertM(inverse, 0, getModelMatrix(), 0)) {
     * SimpleLogger.d(getClass(), "Pointer input, got inverse matrix");
     * float[] vec2 = new float[2];
     * Matrix.transformVec2(inverse, 0, event.getPointerData().getCurrentPosition(), vec2, 1);
     * SimpleLogger.d(getClass(), "Vec2 " + vec2[0] + ", " + vec2[1]);
     * } else {
     * SimpleLogger.d(getClass(), "Could not invert matrix!!!!!!!!!!!!!!!!");
     * }
     * break;
     * case INACTIVE:
     * break;
     * case MOVE:
     * break;
     * case ZOOM:
     * break;
     * }
     * }
     * return checkChildren(event);
     * }
     */
}
