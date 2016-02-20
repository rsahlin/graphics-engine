package com.graphicsengine.spritemesh;

import java.io.IOException;

import com.graphicsengine.io.GraphicsEngineRootNode;
import com.graphicsengine.scene.GraphicsEngineNodeType;
import com.graphicsengine.sprite.SpriteNodeFactory;
import com.graphicsengine.sprite.SpriteNodeFactory.SpriteControllers;
import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.scene.Node;

/**
 * Creates new instances of tiled sprite nodes, use this when importing data
 * 
 * @author Richard Sahlin
 *
 */
public class SpriteMeshNodeFactory {

    private final static String INVALID_TYPE = "Invalid type: ";

    /**
     * Creates a {@link SpriteMeshNode} from the specified Node source. The source reference will be fetched and
     * used to create the {@link SpriteMesh} The created node will have the mesh added so the Node can be rendered after
     * this call.
     * If the Id of the mesh is not set, the reference will be used. This makes it possible to find the Mesh
     * by the reference.
     * 
     * @param renderer
     * @param source
     * @param scene
     * @return
     * @throws IOException
     */
    public static SpriteMeshNode create(NucleusRenderer renderer, Node source, GraphicsEngineRootNode scene)
            throws IOException {
        Node refNode = scene.getResources().getNode(GraphicsEngineNodeType.spriteMeshNode, source.getReference());
        SpriteMeshNode node = (SpriteMeshNode) SpriteNodeFactory.create(SpriteControllers.TILED);
        refNode.copyTo(node);
        node.create();
        node.toReference(source, node);
        SpriteMesh spriteSheet = SpriteMeshFactory.create(renderer, node, scene);
        // Check if the mesh has an id, if not set to reference
        if (spriteSheet.getId() == null) {
            spriteSheet.setId(source.getReference());
        }
        node.addMesh(spriteSheet);

        node.copyTransform(source);
        node.createSprites(renderer, spriteSheet, scene);
        return node;
    }

    /**
     * Returns an instance copy of the source sprite controller, mesh and sprites will NOT be created.
     * Use this when exporting/importing
     * 
     * @param source
     * @return
     */
    public static SpriteMeshNode copy(SpriteMeshNode source) {
        return new SpriteMeshNode(source);
    }

}
