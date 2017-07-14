package de.tschebbischeff.visualizer;

import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Listens for keyboard input.
 *
 * @author Tarek
 * @version 1.0.0
 */
public class KeyListener implements GLFWKeyCallbackI {

    private GlVisualizer glVisualizer = null;

    public KeyListener(GlVisualizer glVisualizer) {
        this.glVisualizer = glVisualizer;
    }

    /**
     * Saves which keys are currently pressed and which are released.
     */
    private static boolean[] keys = new boolean[65536];

    /**
     * {@inheritDoc}
     */
    @Override
    public void invoke(long window, int key, int scancode, int action, int mods) {
        if (action == GLFW_RELEASE) {
            if (key == GLFW_KEY_I) {
                this.glVisualizer.setCameraSpeed(this.glVisualizer.getCameraSpeed()*2.0d);
            }
            if (key == GLFW_KEY_K) {
                this.glVisualizer.setCameraSpeed(this.glVisualizer.getCameraSpeed()/2.0d);
            }
            if (key == GLFW_KEY_ESCAPE) {
                glfwSetWindowShouldClose(window, true);
            }
        }
        keys[key] = action != GLFW_RELEASE;
    }

    /**
     * Usable to determine if a certain key is currently pressed.
     * @param keycode The keycode to check for being currently pressed.
     * @return True if the key specified by keycode is currently pressed, false otherwise.
     */
    public static boolean isKeyDown(int keycode) {
        return keys[keycode];
    }
}
