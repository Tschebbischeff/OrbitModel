package de.tschebbischeff.visualizer;

import de.tschebbischeff.math.Quat4d;
import de.tschebbischeff.math.Vector3d;
import de.tschebbischeff.model.CelestialBody;
import de.tschebbischeff.model.Orbit;
import de.tschebbischeff.model.Scales;
import com.silvertiger.sphere.IcoSphereCreator;
import com.andreaskahler.math.Matrix4f;
import com.andreaskahler.math.Vector4f;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.awt.*;
import java.nio.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

/**
 * Visualizes the orbits and planets in an OpenGL frame.
 *
 * @author Tarek
 * @version 1.0.0
 */
public class GlVisualizer {

    /**
     * The handle for the OpenGL window.
     */
    private long window;

    /**
     * The height of the created window.
     */
    private final int WINDOW_HEIGHT;

    /**
     * The width of the created window.
     */
    private final int WINDOW_WIDTH;

    /**
     * Object of a class that handles loading, compiling and using shaders from the shaders folder.
     */
    private ShaderManager shaderManager;

    /**
     * The order in which the orbits are colored.
     */
    private Color[] colorOrderOrbits;

    /**
     * The order in which the celestial bodies are colored.
     */
    private Color[] colorOrderCelestialBodies;

    /**
     * The next index to use for orbit coloring.
     */
    private int orbitColorIndex = 0;

    /**
     * The next index to use for celestial body coloring.
     */
    private int celestialBodyColorIndex = 0;

    /**
     * The celestial bodies to draw and their VAOs
     */
    private HashMap<CelestialBody, Integer> celestialBodies = new HashMap<>();

    /**
     * The number of vertices of a celestial bodies icosphere.
     */
    private HashMap<CelestialBody, Integer> celestialBodyVertexCounts = new HashMap<>();

    /**
     * The orbits to draw and their VAOs
     */
    private HashMap<Orbit, Integer> orbits = new HashMap<>();

    /**
     * Orbit resolution, how many vertices per orbit should be drawn.
     * There will be (2^orbitResolution) vertices to build the elliptical orbit.
     * orbitResolution has to be at least 3.
     */
    private int orbitResolution = 7;

    /**
     * Celestial body resolution, how many angles per sphere should be drawn.
     * celestialBodyResolution has to be at least 1.
     */
    private int celestialBodyResolution = 2;

    /**
     * The speed with which to accelerate time for the simulation.
     */
    private double visualizationSpeed = 1.0d;

    /**
     * The alpha to draw orbits with.
     */
    private float orbitColorAlpha = 1.0f;

    /**
     * The alpha to draw celestial bodies with.
     */
    private float celestialBodyColorAlpha = 1.0f;

    /**
     * The current time in the simulation.
     */
    private double currentTime = 0.0d;

    /**
     * The celestial body on which to look with the camera.
     */
    private CelestialBody lookAtFixed = null;

    /**
     * The distance from which to look at the fixed object.
     */
    private double lookAtDistance = Scales.astronomicalUnit();

    /**
     * The camera translation.
     */
    private Vector3d cameraTranslation = new Vector3d(0.0d, 0.0d, 2.0d*Scales.astronomicalUnit());

    /**
     * How much the camera is turned horizontally.
     */
    private double cameraAzimuth = 0.0d;

    /**
     * How much the camera is turned vertically.
     */
    private double cameraZenith = 0.0d;

    /**
     * The cameras total current orientation.
     */
    private Quat4d cameraRotation = Quat4d.identity();

    /**
     * The speed with which the camera moves.
     */
    private double cameraSpeed = Scales.astronomicalUnit() / 5.0d;

    /**
     * The speed with which the camera turns.
     */
    private double cameraTurnSpeed = 5.0d;

    /**
     * The cursors x-position.
     */
    private double[] xPos = new double[]{0d};

    /**
     * The cursors y-position.
     */
    private double[] yPos = new double[]{0d};

    /**
     * The position of the model matrix in the shader.
     */
    private int uniformModelMatrix;

    /**
     * The position of the view matrix in the shader.
     */
    private int uniformViewMatrix;

    /**
     * The position of the projection matrix in the shader.
     */
    private int uniformProjectionMatrix;

    /**
     * Whether solid objects are drawn or the area of the orbit.
     */
    private int uniformMode;

    /**
     * The position of the position attribute in the shader.
     */
    private int shaderAttributePosition;

    /**
     * The position of the color attribute in the shader.
     */
    private int shaderAttributeColor;

    /**
     * Sets the color order to use for the visualization. The color order determines in which color the orbits
     * are drawn.
     *
     * @param colorOrderOrbits The new color order to use for the orbits.
     * @return This GlVisualizer for fluent method calls.
     */
    public GlVisualizer setColorOrderOrbits(Color[] colorOrderOrbits) {
        this.colorOrderOrbits = colorOrderOrbits;
        return this;
    }

    /**
     * Sets the color order to use for the visualization. The color order determines in which color the bodies
     * are drawn.
     *
     * @param colorOrderCelestialBodies The new color order to use for the bodies.
     * @return This GlVisualizer for fluent method calls.
     */
    public GlVisualizer setColorOrderCelestialBodies(Color[] colorOrderCelestialBodies) {
        this.colorOrderCelestialBodies = colorOrderCelestialBodies;
        return this;
    }

    /**
     * Sets the resolution to draw the orbits with. A total of 2^resolution vertices will be drawn for
     * each orbit. The minimum value is three.
     *
     * @param resolution The new resolution to use for orbit drawing.
     * @return This GlVisualizer for fluent method calls.
     */
    public GlVisualizer setOrbitResolution(int resolution) {
        this.orbitResolution = Math.max(3, resolution);
        return this;
    }

    /**
     * Sets the resolution to draw the celestial bodies with. The minimum value is 0.
     *
     * @param resolution The new resolution to use for body drawing.
     * @return This GlVisualizer for fluent method calls.
     */
    public GlVisualizer setCelestialBodyResolution(int resolution) {
        this.celestialBodyResolution = Math.max(0, resolution);
        return this;
    }

    /**
     * Sets the speed with which celestial bodies are animated along their orbits.
     * 1.0 means real time.
     *
     * @param speed The new speed to animate bodies with.
     * @return This GlVisualizer for fluent method calls.
     */
    public GlVisualizer setVisualizationSpeed(double speed) {
        this.visualizationSpeed = speed;
        return this;
    }

    /**
     * Gets the speed with which celestial bodies and orbits are animated along their orbits.
     * 1.0 means real time.
     *
     * @return The speed with which bodies are animated.
     */
    public double getVisualizationSpeed() {
        return this.visualizationSpeed;
    }

    /**
     * Sets the alpha value with which orbits are drawn.
     *
     * @param alpha The alpha value for orbits.
     * @return This GlVisualizer for fluent method calls.
     */
    public GlVisualizer setOrbitColorAlpha(float alpha) {
        this.orbitColorAlpha = alpha;
        return this;
    }

    /**
     * Sets the alpha value with which celestial bodies are drawn.
     *
     * @param alpha The alpha value for celestial bodies.
     * @return This GlVisualizer for fluent method calls.
     */
    public GlVisualizer setCelestialBodyColorAlpha(float alpha) {
        this.celestialBodyColorAlpha = alpha;
        return this;
    }

    /**
     * Sets the camera to always look at a specified body.
     *
     * @param fixAt The celestial body on which to fix the camera.
     * @return This GlVisualizer for fluent method calls.
     */
    public GlVisualizer setFixedCamera(CelestialBody fixAt) {
        this.lookAtFixed = fixAt;
        this.lookAtDistance = fixAt.getRadius() * 5d;
        return this;
    }

    /**
     * Sets the speed with which the camera moves.
     * A value of 1.0 means 1.0 units per second.
     *
     * @param speed The new speed with which the camera moves.
     * @return This GlVisualizer for fluent method calls.
     */
    public GlVisualizer setCameraSpeed(double speed) {
        this.cameraSpeed = speed;
        return this;
    }

    /**
     * Sets the speed with which the camera turns.
     * A value of 1.0 means 1.0 degree per second per pixel.
     *
     * @param speed The new value with which the camera turns.
     * @return This GlVisualizer for fluent method calls.
     */
    public GlVisualizer setCameraTurnSpeed(double speed) {
        this.cameraTurnSpeed = speed;
        return this;
    }

    /**
     * Gets the speed with which the camera moves.
     * A value of 1.0 means 1.0 units per second.
     *
     * @return The speed in units per second the camera moves.
     */
    public double getCameraSpeed() {
        return this.cameraSpeed;
    }

    /**
     * Gets the speed with which the camera turns.
     * A value of 1.0 means 1.0 degrees per second.
     *
     * @return The speed in degrees per second the camera turns.
     */
    public double getCameraTurnSpeed() {
        return this.cameraTurnSpeed;
    }

    /**
     * Creates a new GlVisualizer object. The visualization can be started with the run method.
     *
     * @param windowWidth The width of the created window.
     * @param windowHeight The height of the created window.
     */
    public GlVisualizer(int windowWidth, int windowHeight) {
        this.WINDOW_WIDTH = windowWidth;
        this.WINDOW_HEIGHT = windowHeight;
        this.colorOrderOrbits = new Color[]{
                new Color(1.0f, 0.0f, 0.0f),
                new Color(1.0f, 0.5f, 0.0f),
                new Color(1.0f, 1.0f, 0.0f),
                new Color(0.5f, 1.0f, 0.0f),
                new Color(0.0f, 1.0f, 0.0f),
                new Color(0.0f, 1.0f, 0.5f),
                new Color(0.0f, 1.0f, 1.0f),
                new Color(0.0f, 0.5f, 1.0f),
                new Color(0.0f, 0.0f, 1.0f),
                new Color(0.5f, 0.0f, 1.0f),
                new Color(1.0f, 0.0f, 1.0f),
                new Color(1.0f, 0.0f, 0.5f),
        };
        this.colorOrderCelestialBodies = new Color[]{
                new Color(1.0f, 1.0f, 1.0f),
                new Color(1.0f, 0.0f, 0.0f),
                new Color(1.0f, 0.5f, 0.0f),
                new Color(1.0f, 1.0f, 0.0f),
                new Color(0.5f, 1.0f, 0.0f),
                new Color(0.0f, 1.0f, 0.0f),
                new Color(0.0f, 1.0f, 0.5f),
                new Color(0.0f, 1.0f, 1.0f),
                new Color(0.0f, 0.5f, 1.0f),
                new Color(0.0f, 0.0f, 1.0f),
                new Color(0.5f, 0.0f, 1.0f),
                new Color(1.0f, 0.0f, 1.0f),
                new Color(1.0f, 0.0f, 0.5f),
        };
        this.shaderManager = new ShaderManager();
        this.initialize();
        GL.createCapabilities();

        glLineWidth(1.0f);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        this.shaderManager.loadAndCompileShaderPair("default");
        int shaderProgram = this.shaderManager.useShaders("default");

        this.shaderAttributePosition = glGetAttribLocation(shaderProgram, "position");
        this.shaderAttributeColor = glGetAttribLocation(shaderProgram, "color");
        this.uniformModelMatrix = glGetUniformLocation(shaderProgram, "model");
        this.uniformViewMatrix = glGetUniformLocation(shaderProgram, "view");
        this.uniformProjectionMatrix = glGetUniformLocation(shaderProgram, "projection");
        this.uniformMode = glGetUniformLocation(shaderProgram, "mode");
    }

    /**
     * Adds an orbit to the list of orbits that are drawn.
     *
     * @param orbit The orbit to add to the list.
     * @return This GlVisualizer for fluent method calls.
     */
    public GlVisualizer addOrbit(Orbit orbit) {
        int vao = glGenVertexArrays();
        glBindVertexArray(vao);
        try (MemoryStack stack = MemoryStack.stackPush()) {
            DoubleBuffer vertices = stack.mallocDouble((Math.round((float) Math.pow(2, this.orbitResolution))) * 7);
            Vector3d vertex;
            double trueAnomaly = 0d;
            for (int step = 0; step < Math.pow(2, this.orbitResolution); step++) {
                trueAnomaly = step * ((2.0d * Math.PI) / Math.pow(2, this.orbitResolution));
                vertex = orbit.getOrbitalPositionByTrueAnomaly(trueAnomaly);
                vertices.put(vertex.getX()).put(vertex.getY()).put(vertex.getZ())
                        .put(this.colorOrderOrbits[this.orbitColorIndex].getRed() / 255d).put(this.colorOrderOrbits[this.orbitColorIndex].getGreen() / 255d).put(this.colorOrderOrbits[this.orbitColorIndex].getBlue() / 255d).put(this.orbitColorAlpha);
            }
            vertices.flip();
            int vbo = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        }
        glEnableVertexAttribArray(this.shaderAttributePosition);
        glVertexAttribPointer(this.shaderAttributePosition, 3, GL_DOUBLE, false, 7 * Double.BYTES, 0);
        glEnableVertexAttribArray(this.shaderAttributeColor);
        glVertexAttribPointer(this.shaderAttributeColor, 4, GL_DOUBLE, false, 7 * Double.BYTES, 3 * Double.BYTES);
        this.orbits.put(orbit, vao);
        this.orbitColorIndex = (this.orbitColorIndex+1) % this.colorOrderOrbits.length;
        return this;
    }

    /**
     * Adds a celestial body to the list of orbits that are drawn.
     *
     * @param body The body to add to the list.
     * @return This GlVisualizer for fluent method calls.
     */
    public GlVisualizer addCelestialBody(CelestialBody body) {
        int vao = glGenVertexArrays();
        glBindVertexArray(vao);
        ArrayList<Vector3d> sphereMesh = new IcoSphereCreator().createIcoSphere(this.celestialBodyResolution);
        try (MemoryStack stack = MemoryStack.stackPush()) {
            DoubleBuffer vertices = stack.mallocDouble((sphereMesh.size()) * 7);
            for (Vector3d vertex: sphereMesh) {
                vertices.put(vertex.getX()*body.getRadius()).put(vertex.getY()*body.getRadius()).put(vertex.getZ()*body.getRadius())
                        .put(this.colorOrderCelestialBodies[this.celestialBodyColorIndex].getRed() / 255d).put(this.colorOrderCelestialBodies[this.celestialBodyColorIndex].getGreen() / 255d).put(this.colorOrderCelestialBodies[this.celestialBodyColorIndex].getBlue() / 255d).put(this.celestialBodyColorAlpha);
            }
            vertices.flip();
            int vbo = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        }
        glEnableVertexAttribArray(this.shaderAttributePosition);
        glVertexAttribPointer(this.shaderAttributePosition, 3, GL_DOUBLE, false, 7 * Double.BYTES, 0);
        glEnableVertexAttribArray(this.shaderAttributeColor);
        glVertexAttribPointer(this.shaderAttributeColor, 4, GL_DOUBLE, false, 7 * Double.BYTES, 3 * Double.BYTES);
        this.celestialBodies.put(body, vao);
        this.celestialBodyVertexCounts.put(body, sphereMesh.size());
        this.celestialBodyColorIndex = (this.celestialBodyColorIndex+1) % this.colorOrderOrbits.length;
        return this;
    }

    /**
     * Shows the window and starts the visualization
     */
    public void run() {
        // Make the window visible
        glfwShowWindow(window);

        float aspectRatio = WINDOW_WIDTH / WINDOW_HEIGHT;
        Matrix4f projectionMatrix = Matrix4f.perspective(90f, aspectRatio, (float) (0.0001f * Scales.astronomicalUnit()), (float) (10f * Scales.astronomicalUnit()));
        glUniformMatrix4fv(this.uniformProjectionMatrix, false, projectionMatrix.getData());

        // Set the clear color
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        long lastTime = System.nanoTime();
        double deltaTime;
        while (!glfwWindowShouldClose(window)) {
            deltaTime = (System.nanoTime() - lastTime) / 1000000000.0d;
            lastTime = System.nanoTime();

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

            //view matrix
            double[][] rotation = this.cameraRotation.toRotationMatrix().getData();
            Matrix4f r = new Matrix4f(
                    new Vector4f((float) rotation[0][0], (float) rotation[1][0], (float) rotation[2][0], 0f),
                    new Vector4f((float) rotation[0][1], (float) rotation[1][1], (float) rotation[2][1], 0f),
                    new Vector4f((float) rotation[0][2], (float) rotation[1][2], (float) rotation[2][2], 0f),
                    new Vector4f(0f, 0f, 0f, 1f)
            );
            Matrix4f t = new Matrix4f(
                    new Vector4f(1f, 0f, 0f, (float) -cameraTranslation.getX()),
                    new Vector4f(0f, 1f, 0f, (float) -cameraTranslation.getY()),
                    new Vector4f(0f, 0f, 1f, (float) -cameraTranslation.getZ()),
                    new Vector4f(0f, 0f, 0f, 1f)
            ).transpose();
            glUniformMatrix4fv(this.uniformViewMatrix, false, (r.multiply(t)).getData());

            //draw
            this.loop(deltaTime);

            glfwSwapBuffers(window); // swap the color buffers

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
            this.handleInput(deltaTime);
        }

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    /**
     * Runs rendering related stuff.
     *
     * @param deltaTime The time elapsed since the last time this method was called, in seconds.
     */
    private void loop(double deltaTime) {
        this.currentTime += (deltaTime * this.visualizationSpeed);
        this.drawOrbits();
        this.drawCelestialBodies();
    }

    /**
     * Draws all registered orbits
     */
    private void drawOrbits() {
        Vector3d translation;
        for (Map.Entry<Orbit, Integer> entry: this.orbits.entrySet()) {
            translation = entry.getKey().getParentBody().getPosition(this.currentTime);
            Matrix4f modelMatrix = new Matrix4f(
                    new Vector4f(1f, 0f, 0f, (float) translation.getX()),
                    new Vector4f(0f, 1f, 0f, (float) translation.getY()),
                    new Vector4f(0f, 0f, 1f, (float) translation.getZ()),
                    new Vector4f(0f, 0f, 0f, 1f)
            ).transpose();
            glUniformMatrix4fv(this.uniformModelMatrix, false, modelMatrix.getData());
            glBindVertexArray(entry.getValue());
            glUniform1i(this.uniformMode, 0);
            glDrawArrays(GL_LINE_LOOP, 0, Math.round((float) Math.pow(2, this.orbitResolution)));
            glUniform1i(this.uniformMode, 1);
            glDrawArrays(GL_TRIANGLE_FAN, 0, Math.round((float) Math.pow(2, this.orbitResolution)));
        }
    }

    /**
     * Draws all registered celestial bodies
     */
    private void drawCelestialBodies() {
        Vector3d translation;
        glUniform1i(this.uniformMode, 0);
        for (Map.Entry<CelestialBody, Integer> entry: this.celestialBodies.entrySet()) {
            translation = entry.getKey().getPosition(this.currentTime);
            Matrix4f modelMatrix = new Matrix4f(
                    new Vector4f(1f, 0f, 0f, (float) translation.getX()),
                    new Vector4f(0f, 1f, 0f, (float) translation.getY()),
                    new Vector4f(0f, 0f, 1f, (float) translation.getZ()),
                    new Vector4f(0f, 0f, 0f, 1f)
            ).transpose();
            glUniformMatrix4fv(this.uniformModelMatrix, false, modelMatrix.getData());
            glBindVertexArray(entry.getValue());
            glDrawArrays(GL_TRIANGLES, 0, this.celestialBodyVertexCounts.get(entry.getKey()));
        }
    }

    /**
     * Processes mouse and continous keyboard input
     *
     * @param deltaTime The time passed since this method was last called.
     */
    private void handleInput(double deltaTime) {
        glfwGetCursorPos(window, xPos, yPos);
        glfwSetCursorPos(window, 0, 0);
        this.cameraAzimuth = (this.cameraAzimuth + (this.cameraTurnSpeed * deltaTime * xPos[0])) % 360.0d;
        this.cameraZenith = Math.max(-89d, Math.min(89d, (this.cameraZenith - (this.cameraTurnSpeed * deltaTime * yPos[0]))));
        this.cameraRotation = Quat4d.identity().pitch(this.cameraAzimuth).roll(-this.cameraZenith);
        if (KeyListener.isKeyDown(GLFW_KEY_U)) {
            this.setVisualizationSpeed(this.getVisualizationSpeed() + Scales.day() * 7d * deltaTime);
        }
        if (KeyListener.isKeyDown(GLFW_KEY_J)) {
            this.setVisualizationSpeed(this.getVisualizationSpeed() - Scales.day() * 7d * deltaTime);
        }
        if (this.lookAtFixed == null) {
            Vector3d forward = this.cameraRotation.rotateVector(Vector3d.Z_AXIS_NEG).normalize();
            Vector3d right = this.cameraRotation.rotateVector(Vector3d.X_AXIS).normalize();
            if (KeyListener.isKeyDown(GLFW_KEY_W)) {
                this.cameraTranslation = this.cameraTranslation.add(forward.scale(this.cameraSpeed * deltaTime));
            }
            if (KeyListener.isKeyDown(GLFW_KEY_A)) {
                this.cameraTranslation = this.cameraTranslation.add(right.scale(-this.cameraSpeed * deltaTime));
            }
            if (KeyListener.isKeyDown(GLFW_KEY_S)) {
                this.cameraTranslation = this.cameraTranslation.add(forward.scale(-this.cameraSpeed * deltaTime));
            }
            if (KeyListener.isKeyDown(GLFW_KEY_D)) {
                this.cameraTranslation = this.cameraTranslation.add(right.scale(this.cameraSpeed * deltaTime));
            }
        } else {
            this.cameraTranslation = this.lookAtFixed.getPosition(this.currentTime).add(
                    this.cameraRotation.rotateVector(Vector3d.Z_AXIS_NEG).scale(-1d).normalize().scale(this.lookAtDistance)
            );
            if (KeyListener.isKeyDown(GLFW_KEY_W)) {
                this.lookAtDistance -= Math.max(this.lookAtFixed.getRadius(), Scales.astronomicalUnit() * 0.1d * deltaTime);
            }
            if (KeyListener.isKeyDown(GLFW_KEY_S)) {
                this.lookAtDistance += Scales.astronomicalUnit() * 0.1d * deltaTime;
            }
        }
    }

    /**
     * Initializes OpenGL stuff.
     */
    private void initialize() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE); // the window will not be resizable
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3); // We want OpenGL 3.3
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE); //We don't want the old OpenGL
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE); // No deprecated functionality

        // Create the window
        window = glfwCreateWindow(WINDOW_WIDTH, WINDOW_HEIGHT, "OrbitModeler Visualizer", NULL, NULL);
        if (window == NULL)
            throw new RuntimeException("Failed to create the GLFW window");

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, new KeyListener(this));

        // Get the thread stack and push a new frame
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        } // the stack frame is popped automatically

        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);
    }
}