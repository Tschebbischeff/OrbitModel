package de.tschebbischeff.visualizer;

import org.lwjgl.glfw.GLFWMouseButtonCallbackI;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Listens for mouse input
 *
 * @author Tarek
 * @version 1.0.0
 */
public class MouseListener implements GLFWMouseButtonCallbackI {

    /**
     * The GlVisualizer object calling, for backwards references.
     */
    private GlVisualizer glVisualizer = null;

    /**
     * Creates a new Mouse listener, which automatically saves the keys, that are pressed in an array.
     *
     * @param glVisualizer The visualizer registering the listener.
     */
    public MouseListener(GlVisualizer glVisualizer) {
        this.glVisualizer = glVisualizer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void invoke(long window, int button, int action, int mods) {
        if (action == GLFW_RELEASE) {
            if (button == GLFW_MOUSE_BUTTON_LEFT) {
                this.glVisualizer.fixedCameraNext();
            }
            if (button == GLFW_MOUSE_BUTTON_RIGHT) {
                this.glVisualizer.fixedCameraPrevious();
            }
            if (button == GLFW_MOUSE_BUTTON_MIDDLE) {
                this.glVisualizer.unsetFixedCamera();
            }
        }
    }
}
