package com.graphicsengine.scene;

import java.io.IOException;

import com.google.gson.annotations.SerializedName;
import com.graphicsengine.component.SpriteAttributeComponent;
import com.nucleus.SimpleLogger;
import com.nucleus.common.Type;
import com.nucleus.geometry.Mesh;
import com.nucleus.geometry.MeshBuilder;
import com.nucleus.geometry.shape.ShapeBuilder;
import com.nucleus.opengl.GLES20Wrapper;
import com.nucleus.scene.AbstractMeshNode;
import com.nucleus.scene.Node;
import com.nucleus.scene.RootNode;
import com.nucleus.texturing.Texture2D;
import com.nucleus.texturing.TextureType;
import com.nucleus.texturing.TiledTexture2D;
import com.nucleus.vecmath.Rectangle;
import com.nucleus.vecmath.Transform;

/**
 * A Quad child that has to be appended to QuadNode in order to be rendered.
 * This node will share the mesh from the parent {@link QuadParentNode}
 * This is for objects that are mostly static, for instance UI elements, and objects that need touch events.
 * If a large number of objects with shared behavior are needed use {@link SpriteAttributeComponent} instead.
 * Object visibility cannot be controlled by the Node state value since the quad belongs to the parent mesh.
 * 
 * @author Richard Sahlin
 *
 */
public class SharedMeshQuad extends AbstractMeshNode<Mesh> {

    /**
     * The framenumber for this quad, from the texture in the referenced mesh.
     */
    @SerializedName(TiledTexture2D.FRAME)
    private int frame;
    /**
     * The index of this shared mesh quad node with it's parent.
     */
    transient private int childIndex;
    transient private QuadParentNode quadParent;
    /**
     * The rectangle defining the sprites, all sprites will have same size
     * 4 values = x1,y1 + width and height
     */
    @SerializedName(Rectangle.RECT)
    private Rectangle rectangle;

    /**
     * Used by GSON and {@link #createInstance(RootNode)} method - do NOT call directly
     */
    @Deprecated
    public SharedMeshQuad() {
        super();
    }

    private SharedMeshQuad(RootNode root) {
        super(root, GraphicsEngineNodeType.sharedMeshNode);
    }

    protected SharedMeshQuad(RootNode root, Type<Node> type) {
        super(root, type);
    }

    /**
     * Called when the parent node is created - remember that shared mesh quad does not use it's own mesh
     * TODO Need to provide size and color from scene definition.
     * 
     * @param Parent The parent node holding all quads
     * @param index
     */
    @Override
    public void onCreated() {
        // Add this to the quadparentnode
        quadParent = (QuadParentNode) getParent();
        childIndex = quadParent.addQuad(this);
        initBounds(quadParent.buildQuad(childIndex, rectangle, frame));
        if (transform == null) {
            transform = new Transform();
        }
        quadParent.getExpander().setData(childIndex, transform);
        quadParent.getExpander().setFrame(childIndex, frame);
        Mesh mesh = quadParent.getMesh(MeshIndex.MAIN);
        if (mesh.getTexture(Texture2D.TEXTURE_0).textureType == TextureType.Untextured) {
            updateAmbient();
        }
        if (getState() != null && getState() != State.ON) {
            SimpleLogger.d(getClass(),
                    "Node state is set for id " + getId() + ", state handling for shared mesh quad is not implemented");
        }
    }

    /**
     * Sets the index of this child with the parent, this is normally done by the parent.
     * 
     * @param index
     */
    public void setChildIndex(int index) {
        this.childIndex = index;
    }

    @Override
    public Node createInstance(RootNode root) {
        SharedMeshQuad copy = new SharedMeshQuad(root);
        copy.set(this);
        return copy;
    }

    /**
     * Sets the values from the source into this node
     * 
     * @param source
     */
    public void set(SharedMeshQuad source) {
        super.set(source);
        this.frame = source.frame;
        if (source.rectangle != null) {
            setQuadRectangle(source.rectangle);
        } else {
            rectangle = null;
        }
    }

    /**
     * Internal method, sets the rectangle defining each sprite
     * This will only set the size parameter, createMesh must be called to actually create the mesh
     * 
     * param rectangle values defining sprite, X1, Y1, width, height.
     */
    private void setQuadRectangle(Rectangle rectangle) {
        this.rectangle = new Rectangle(rectangle);
    }

    /**
     * Sets the visible frame
     * Note, this will not update the frame number in this class - {@link #frame}
     * 
     * @param frame Frame to display
     */
    public void setFrame(int frame) {
        quadParent.getExpander().setFrame(childIndex, frame);
    }

    /**
     * Copies the transform so that the Quad is updated on screen.
     * Call this after the transform in the Mesh has been changed.
     */
    public void updateTransform() {
        quadParent.getExpander().setData(childIndex, transform);
    }

    /**
     * If material and ambient color is set it is updated.
     */
    public void updateAmbient() {
        if (getMaterial() != null && getMaterial().getEmissive() != null) {
            quadParent.getExpander().setColor(childIndex, getMaterial().getEmissive());
        }
    }

    /**
     * Returns the number of frames supported in the parent.
     * 
     * @return
     */
    public int getFrameCount() {
        return quadParent.getMesh(MeshIndex.MAIN).getTexture(Texture2D.TEXTURE_0).getFrameCount();
    }

    /**
     * Returns the frame number for this quad.
     * 
     * @return
     */
    public int getFrame() {
        return frame;
    }

    @Override
    public void createTransient() {
        // TODO Auto-generated method stub

    }

    @Override
    public MeshBuilder<Mesh> createMeshBuilder(GLES20Wrapper gles, ShapeBuilder shapeBuilder)
            throws IOException {
        // Dont create a meshbuilder since this node uses parents Mesh
        return null;
    }

}
