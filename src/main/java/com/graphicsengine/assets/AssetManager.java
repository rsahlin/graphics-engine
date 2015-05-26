package com.graphicsengine.assets;

import java.io.IOException;
import java.util.Hashtable;

import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.texturing.Texture2D;
import com.nucleus.texturing.TextureFactory;
import com.nucleus.texturing.TextureSetup;

/**
 * Loading and unloading assets, mainly textures.
 * 
 * @author Richard Sahlin
 *
 */
public class AssetManager {

    protected static AssetManager assetManager = null;

    /**
     * Store textures using the source image name.
     */
    private Hashtable<String, Texture2D> textures = new Hashtable<String, Texture2D>();

    /**
     * Hide the constructor
     */
    private AssetManager() {
    }

    public static AssetManager getInstance() {
        if (assetManager == null) {
            assetManager = new AssetManager();
        }
        return assetManager;
    }

    public interface Asset {
        /**
         * Loads an instance of the asset into memory, after this method returns the asset SHALL be ready to be used.
         * 
         * @param source The source of the object, it is up to implementations to decide what sources to support.
         * For images the normal usecase is InputStream
         * 
         * @return The id of the asset, this is a counter starting at 1 and increasing.
         * @throws IOException If there is an exception reading from the stream.
         */
        public int load(Object source) throws IOException;

        /**
         * Releases the asset and all allocated memory, after this method returns all memory and objects shall be
         * released.
         */
        public void destroy();
    }

    public Texture2D getTexture(NucleusRenderer renderer, TextureSetup source) throws IOException {

        Texture2D texture = textures.get(source.getSourceName());
        if (texture != null) {
            return texture;
        }

        texture = TextureFactory.createTexture(renderer.getGLES(), renderer.getImageFactory(), source);
        textures.put(source.getSourceName(), texture);
        return texture;
    }

}
