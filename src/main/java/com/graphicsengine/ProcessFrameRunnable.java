package com.graphicsengine;

import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.renderer.NucleusRenderer.FrameListener;

/**
 * Runnable to call {@link FrameListener#processFrame(float)} after synchronization with rendering.
 * 
 * @author Richard Sahlin
 *
 */
public class ProcessFrameRunnable implements Runnable {

    private static final String NULL_RENDERER = "Renderer is null";
    private NucleusRenderer renderer;
    private boolean running = false;

    /**
     * 
     * @param renderer
     * @throws IllegalArgumentException If renderer is null
     */
    public ProcessFrameRunnable(NucleusRenderer renderer) {
        if (renderer == null) {
            throw new IllegalArgumentException(NULL_RENDERER);
        }
        this.renderer = renderer;
    }

    @Override
    public void run() {
        System.out.println("Started thread to call processFrame()");
        running = true;
        while (running) {
            synchronized (this) {
                renderer.processFrame();
                try {
                    wait();
                } catch (InterruptedException e) {

                }
            }
        }
        System.out.println("Exited call processFrame() thread");
    }

    public void destroy() {
        running = false;
        Thread.currentThread().interrupt();
    }

}
