package com.graphicsengine.map;

import java.io.IOException;

import com.google.gson.annotations.SerializedName;
import com.graphicsengine.scene.GraphicsEngineNodeType;
import com.nucleus.SimpleLogger;
import com.nucleus.geometry.Mesh;
import com.nucleus.geometry.shape.ShapeBuilder;
import com.nucleus.io.ExternalReference;
import com.nucleus.mmi.ObjectInputListener;
import com.nucleus.mmi.PointerData;
import com.nucleus.mmi.PointerMotionData;
import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.scene.LineDrawerNode;
import com.nucleus.scene.Node;
import com.nucleus.scene.NodeException;
import com.nucleus.scene.RootNode;
import com.nucleus.shader.ShaderProperty.PropertyMapper;
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
public class PlayfieldNode extends Node {

    public class PlayfieldNodeObjectInputListener implements ObjectInputListener {

        float[] rectangle = new float[4];
        float[] rgba = new float[] { 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1 };
        EventConfiguration config = new EventConfiguration();

        @Override
        public boolean onInputEvent(Node node, PointerData event) {
            float[] inverse = new float[16];
            float[] position = event.position;
            if (Matrix.invertM(inverse, 0, getModelMatrix(), 0)) {
                float[] vec2 = new float[2];
                Matrix.transformVec2(inverse, 0, position, vec2, 1);
                int[] mapPos = getMapPos(vec2);
                if (mapPos != null) {
                    map.logMapPosition(mapPos[0], mapPos[1]);
                }
            } else {
                SimpleLogger.d(getClass(), "Could not invert matrix!!!!!!!!!!!!!!!!");
            }
            return false;
        }

        @Override
        public boolean onDrag(Node node, PointerMotionData drag) {
            SimpleLogger.d(getClass(), "onDrag() " + node.getId());
            float[] down = drag.getFirstPosition();
            float[] current = drag.getCurrentPosition();
            rectangle[0] = down[0];
            rectangle[1] = down[1];
            rectangle[2] = current[0] - down[0];
            rectangle[3] = down[1] - current[1];
            LineDrawerNode lines = (LineDrawerNode) getRootNode().getNodeById("lines");
            if (lines != null) {
                lines.setRectangle(0, rectangle, 0f, rgba);
            }
            return true;
        }

        @Override
        public boolean onClick(Node node, PointerData event) {
            SimpleLogger.d(getClass(), "onClick() " + node.getId());
            return false;
        }

        @Override
        public EventConfiguration getConfiguration() {
            return config;
        }

    }

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
     * This is used when building the mesh
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

    /**
     * Used by GSON and {@link #createInstance(RootNode)} method - do NOT call directly
     */
    @Deprecated
    protected PlayfieldNode() {
        super();
    }

    private PlayfieldNode(RootNode root) {
        super(root, GraphicsEngineNodeType.playfieldNode);
    }

    @Override
    public PlayfieldNode createInstance(RootNode root) {
        PlayfieldNode copy = new PlayfieldNode(root);
        copy.set(this);
        return copy;
    }

    @Override
    public Mesh.Builder<Mesh> createMeshBuilder(NucleusRenderer renderer, Node parent, int count,
            ShapeBuilder shapeBuilder)
            throws IOException {

        PlayfieldNode playfield = (PlayfieldNode) parent;
        PlayfieldMesh.Builder builder = new PlayfieldMesh.Builder(renderer);
        builder.setMap(playfield.getMapSize(), playfield.getCharRectangle());
        builder.setOffset(playfield.getAnchorOffset());
        return super.initMeshBuilder(renderer, parent, count, shapeBuilder, builder);
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
     * @throws NodeException If referenced map can not be loaded.
     */
    public void createMap() throws NodeException {
        try {
            map = MapFactory.createMap(mapRef);
            PlayfieldMesh playfield = (PlayfieldMesh) getMesh(MeshIndex.MAIN);
            if (mapper == null) {
                mapper = new PropertyMapper(program);
            }
            if (map.getMap() != null && map.getMapSize() != null) {
                playfield.copyCharmap(mapper, map);
            }
        } catch (IOException | ClassNotFoundException e) {
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
     * Returns the number of chars in the map - this is the same as width * height
     * 
     * @return
     */
    public int getCharCount() {
        return mapSize[0] * mapSize[1];

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
                        -(getMapSize()[0] >>> 1) * getCharRectangle().getValues()[Rectangle.INDEX_WIDTH],
                        (getMapSize()[1] >>> 1) * getCharRectangle().getValues()[Rectangle.INDEX_HEIGHT] };
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

}
