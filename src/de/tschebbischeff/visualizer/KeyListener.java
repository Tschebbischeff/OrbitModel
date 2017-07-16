package de.tschebbischeff.visualizer;

import org.lwjgl.glfw.GLFWKeyCallbackI;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Listens for keyboard input.
 *
 * @author Tarek
 * @version 1.0.0
 */
public class KeyListener implements GLFWKeyCallbackI {

    /**
     * Saves which keys are currently pressed and which are released.
     */
    private static boolean[] keys = new boolean[65536];
    /**
     * The GlVisualizer object calling, for backwards references.
     */
    private GlVisualizer glVisualizer = null;
    /**
     * The normal camera speed at the beginning of the visualization
     */
    private double normalCameraSpeed;

    /**
     * Creates a new Key listener, which automatically saves the keys, that are pressed in an array.
     *
     * @param glVisualizer The visualizer registering the listener.
     */
    public KeyListener(GlVisualizer glVisualizer) {
        this.glVisualizer = glVisualizer;
        this.normalCameraSpeed = this.glVisualizer.getCameraSpeed();
    }

    /**
     * Usable to determine if a certain key is currently pressed.
     *
     * @param keycode The keycode to check for being currently pressed.
     * @return True if the key specified by keycode is currently pressed, false otherwise.
     */
    public static boolean isKeyDown(int keycode) {
        return keys[keycode];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void invoke(long window, int key, int scancode, int action, int mods) {
        keys[key] = action != GLFW_RELEASE;
        if (isKeyDown(GLFW_KEY_LEFT_SHIFT)) {
            this.glVisualizer.setCameraSpeed(this.normalCameraSpeed * 8.0d);
        } else if (isKeyDown(GLFW_KEY_LEFT_CONTROL)) {
            this.glVisualizer.setCameraSpeed(this.normalCameraSpeed / 8.0d);
        } else {
            this.glVisualizer.setCameraSpeed(this.normalCameraSpeed);
        }
        //Close window on ESCAPE
        if (action == GLFW_RELEASE) {
            if (key == GLFW_KEY_ESCAPE) {
                glfwSetWindowShouldClose(window, true);
            }
        }
    }
}
