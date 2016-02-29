package com.graphicsengine.ui;

import com.graphicsengine.io.GraphicsEngineRootNode;
import com.graphicsengine.scene.GraphicsEngineNodeType;
import com.nucleus.geometry.MeshFactory;
import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.scene.Node;

/**
 * Factory class for creating the UI nodes
 * 
 * @author Richard Sahlin
 *
 */
public class UINodeFactory {

    public static Node createButton(NucleusRenderer renderer, Node source, MeshFactory meshFactory,
            GraphicsEngineRootNode scene) {
        String reference = source.getReference();
        // try {
        Button refNode = (Button) scene.getResources().getNode(
                GraphicsEngineNodeType.button, reference);
        Button button = new Button(refNode);
        button.copyTransform(source);

        // SpriteMeshNode spriteNode = (SpriteMeshNode) SpriteNodeFactory.create(
        // SpriteControllers.TILED);
        // spriteNode.set(refNode);
        // spriteNode.create();
        // spriteNode.toReference(source, spriteNode);
        return button;
        // } catch (IllegalAccessException | InstantiationException | ClassNotFoundException e) {
        // throw new RuntimeException(e);
        // }

    }
}
