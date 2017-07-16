package de.tschebbischeff.visualizer;

import org.lwjgl.glfw.GLFWScrollCallbackI;

/**
 * Listens for scroll wheel input.
 *
 * @author Tarek
 * @version 1.0.0
 */
public class ScrollListener implements GLFWScrollCallbackI {

    /**
     * Saves how many steps the mouse wheel was scrolled, since the last query.
     */
    private static int scrollSteps = 0;
    /**
     * The GlVisualizer object calling, for backwards references.
     */
    private GlVisualizer glVisualizer = null;

    /**
     * Creates a new Mouse listener, which automatically saves the keys, that are pressed in an array.
     *
     * @param glVisualizer The visualizer registering the listener.
     */
    public ScrollListener(GlVisualizer glVisualizer) {
        this.glVisualizer = glVisualizer;
    }

    /**
     * Returns the number of scroll actions performed since the last query.
     *
     * @return The number of scrolls. Upward scroll will result in positive, downward in negative numbers.
     */
    public static int getMouseScroll() {
        int result = scrollSteps;
        scrollSteps = 0;
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void invoke(long window, double xoffset, double yoffset) {
        if (this.glVisualizer.isCameraFixed()) {
            if (yoffset > 0) {
                scrollSteps++;
            } else if (yoffset < 0) {
                scrollSteps--;
            }
        }
    }
}
