package de.tschebbischeff.visualizer;

import com.andreaskahler.math.Matrix4f;
import com.andreaskahler.math.Vector4f;
import com.silvertiger.sphere.IcoSphereCreator;
import de.tschebbischeff.math.Quat4d;
import de.tschebbischeff.math.Vector3d;
import de.tschebbischeff.model.CelestialBody;
import de.tschebbischeff.model.Orbit;
import de.tschebbischeff.model.Scales;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.awt.*;
import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * Visualizes the orbits and planets in an OpenGL frame.
 *
 * @author Tarek
 * @version 1.0.0
 */
public class GlVisualizer {

    /**
     * The height of the created window.
     */
    private final int WINDOW_HEIGHT;
    /**
     * The width of the created window.
     */
    private final int WINDOW_WIDTH;
    /**
     * The handle for the OpenGL window.
     */
    private long window;
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
     * VAO for the three spatial axes, when drawn as lines, represent a coordinate axis
     */
    private int coordinateSystemVao;

    /**
     * The celestial bodies to draw and their VAOs
     */
    private HashMap<CelestialBody, Integer> celestialBodies = new HashMap<>();

    /**
     * The celestial bodies in the order they were added
     */
    private ArrayList<CelestialBody> celestialBodyOrder = new ArrayList<>();

    /**
     * The number of vertices of a celestial bodies icosphere.
     */
    private HashMap<CelestialBody, Integer> celestialBodyVertexCounts = new HashMap<>();

    /**
     * The orbits to draw and their VAOs
     */
    private HashMap<Orbit, Integer> orbits = new HashMap<>();

    /**
     * Scales the rendered bodies to make them more visible on their orbits.
     */
    private double scaleBodies = 1.0d;

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
     * How many steps away the camera should currently be positioned form the looked at object in fixed mode.
     */
    private double cameraFixedDistanceSteps = 0;

    /**
     * The camera translation.
     */
    private Vector3d cameraTranslation = new Vector3d(0.0d, 0.0d, 2.0d * Scales.astronomicalUnit());

    /**
     * How much the camera is turned horizontally.
     */
    private double cameraAzimuth = 0.0d;

    /**
     * How much the camera is turned vertically.
     */
    private double cameraZenith = 0.0d;

    /**
     * How much the camera is turned around itself.
     */
    private double cameraRoll = 0.0d;

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
     * Creates a new GlVisualizer object. The visualization can be started with the run method.
     *
     * @param windowWidth  The width of the created window.
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
        glEnable(GL_DEPTH_TEST);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        this.shaderManager.loadAndCompileShaderPair("default");
        int shaderProgram = this.shaderManager.useShaders("default");

        this.shaderAttributePosition = glGetAttribLocation(shaderProgram, "position");
        this.shaderAttributeColor = glGetAttribLocation(shaderProgram, "color");
        this.uniformModelMatrix = glGetUniformLocation(shaderProgram, "model");
        this.uniformViewMatrix = glGetUniformLocation(shaderProgram, "view");
        this.uniformProjectionMatrix = glGetUniformLocation(shaderProgram, "projection");
        this.uniformMode = glGetUniformLocation(shaderProgram, "mode");

        //basic coordinate system lines
        int vao = glGenVertexArrays();
        glBindVertexArray(vao);
        try (MemoryStack stack = MemoryStack.stackPush()) {
            DoubleBuffer vertices = stack.mallocDouble(6 * 7);
            vertices.put(0d).put(0d).put(0d)
                    .put(1d).put(0d).put(0d).put(1d);
            vertices.put(1d).put(0d).put(0d)
                    .put(1d).put(0d).put(0d).put(1d);
            vertices.put(0d).put(0d).put(0d)
                    .put(0d).put(1d).put(0d).put(1d);
            vertices.put(0d).put(1d).put(0d)
                    .put(0d).put(1d).put(0d).put(1d);
            vertices.put(0d).put(0d).put(0d)
                    .put(0d).put(0d).put(1d).put(1d);
            vertices.put(0d).put(0d).put(1d)
                    .put(0d).put(0d).put(1d).put(1d);
            vertices.flip();
            int vbo = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        }
        glEnableVertexAttribArray(this.shaderAttributePosition);
        glVertexAttribPointer(this.shaderAttributePosition, 3, GL_DOUBLE, false, 7 * Double.BYTES, 0);
        glEnableVertexAttribArray(this.shaderAttributeColor);
        glVertexAttribPointer(this.shaderAttributeColor, 4, GL_DOUBLE, false, 7 * Double.BYTES, 3 * Double.BYTES);
        this.coordinateSystemVao = vao;
    }

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
     * Gets the speed with which celestial bodies and orbits are animated along their orbits.
     * 1.0 means real time.
     *
     * @return The speed with which bodies are animated.
     */
    public double getVisualizationSpeed() {
        return this.visualizationSpeed;
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
     * Sets the factor by which all rendered celestial bodies are scaled, to make them more visible on their orbits,
     * when zooming out very far. When setting this to any value above one, all planets will be the same size.
     *
     * @param scale The factor by which to scale the bodies.
     * @return This GlVisualizer for fluent method calls.
     */
    public GlVisualizer setBodyScale(double scale) {
        this.scaleBodies = Math.max(1.0d, scale);
        return this;
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
        if (fixAt != null) {
            if (this.scaleBodies > 1.0d) {
                this.lookAtDistance = this.scaleBodies * 5d;
                this.cameraFixedDistanceSteps = this.scaleBodies * 5d / fixAt.getRadius();
            } else {
                this.lookAtDistance = fixAt.getRadius() * 5d;
                this.cameraFixedDistanceSteps = 5d;
            }
        }
        return this;
    }

    /**
     * Gets the body at which the camera is currently looking in fixed mode.
     *
     * @return The celestial body, at which the camera currently looks, null if the camera is currently in free mode.
     */
    public CelestialBody getLookedAtObject() {
        return this.lookAtFixed;
    }

    /**
     * Gets the distance from which the camera is currently looking at the body in fixed mode.
     *
     * @return The distance from the camera to the looked at celestial body.
     */
    public double getLookAtDistance() {
        return this.lookAtDistance;
    }

    /**
     * Sets the distance from which to look at the body specified in {@link GlVisualizer#setFixedCamera(CelestialBody)}.
     *
     * @param lookAtDistance The distance from which to look at the body.
     * @return This GlVisualizer for fluent method calls.
     */
    public GlVisualizer setLookAtDistance(double lookAtDistance) {
        if (this.lookAtFixed != null) {
            this.lookAtDistance = Math.max(this.lookAtFixed.getRadius() * 2d, lookAtDistance);
        }
        return this;
    }

    /**
     * Sets the camera to free mode.
     *
     * @return This GlVisualizer for fluent method calls.
     */
    public GlVisualizer unsetFixedCamera() {
        this.setFixedCamera(null);
        return this;
    }

    /**
     * Sets the camera to fix on the next celestial body after the currently fixed one.
     * In case of the camera being not-fixed or fixed on the last body in the list, the first body is focused.
     *
     * @return This GlVisualizer for fluent method calls.
     */
    public GlVisualizer fixedCameraNext() {
        if (this.celestialBodies.size() > 0) {
            int newIndex = 0;
            if (this.lookAtFixed != null) {
                newIndex = (this.celestialBodyOrder.indexOf(this.lookAtFixed) + 1) % this.celestialBodyOrder.size();
            }
            this.setFixedCamera(this.celestialBodyOrder.get(newIndex));
        }
        return this;
    }

    /**
     * Sets the camera to fix on the previous celestial body after the currently fixed one.
     * In case of the camera being not-fixed or fixed on the last body in the list, the first body is focused.
     *
     * @return This GlVisualizer for fluent method calls.
     */
    public GlVisualizer fixedCameraPrevious() {
        if (this.celestialBodies.size() > 0) {
            int newIndex = 0;
            if (this.lookAtFixed != null) {
                newIndex = (this.celestialBodyOrder.indexOf(this.lookAtFixed) - 1) % this.celestialBodyOrder.size();
                if (newIndex < 0) {
                    newIndex = this.celestialBodyOrder.size() + newIndex;
                }
            }
            this.setFixedCamera(this.celestialBodyOrder.get(newIndex));
        }
        return this;
    }

    /**
     * Checks whether the camera is set to fixed mode.
     *
     * @return True if, and only if, the camera is fixed on a celestial object.
     */
    public boolean isCameraFixed() {
        return this.lookAtFixed != null;
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
     * Gets the speed with which the camera turns.
     * A value of 1.0 means 1.0 degrees per second.
     *
     * @return The speed in degrees per second the camera turns.
     */
    public double getCameraTurnSpeed() {
        return this.cameraTurnSpeed;
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
        this.orbitColorIndex = (this.orbitColorIndex + 1) % this.colorOrderOrbits.length;
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
            for (Vector3d vertex : sphereMesh) {
                vertices.put(vertex.getX() * body.getRadius()).put(vertex.getY() * body.getRadius()).put(vertex.getZ() * body.getRadius())
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
        this.celestialBodyOrder.add(body);
        this.celestialBodyVertexCounts.put(body, sphereMesh.size());
        this.celestialBodyColorIndex = (this.celestialBodyColorIndex + 1) % this.colorOrderOrbits.length;
        return this;
    }

    /**
     * Shows the window and starts the visualization
     */
    public void run() {
        // Make the window visible
        glfwShowWindow(window);

        float aspectRatio = ((float) WINDOW_WIDTH) / ((float) WINDOW_HEIGHT);

        // Set the clear color
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        long lastTime = System.nanoTime();
        double deltaTime;
        while (!glfwWindowShouldClose(window)) {
            //all time stuff
            deltaTime = (System.nanoTime() - lastTime) / 1000000000.0d;
            lastTime = System.nanoTime();
            this.currentTime += (deltaTime * this.visualizationSpeed);

            //handle input first (camera must be reflected in this step's view matrix)
            glfwPollEvents();
            this.handleInput(deltaTime);

            //projection matrix
            Matrix4f projectionMatrix;
            if (this.lookAtFixed == null) {
                projectionMatrix = Matrix4f.perspective(90f, aspectRatio, (float) (0.05f * Scales.astronomicalUnit()), (float) (100f * Scales.astronomicalUnit()));
            } else {
                projectionMatrix = Matrix4f.perspective(90f, aspectRatio, (float) (0.05f * this.cameraFixedDistanceSteps * this.lookAtFixed.getRadius()), (float) (100f * this.cameraFixedDistanceSteps * this.lookAtFixed.getRadius()));
            }
            glUniformMatrix4fv(this.uniformProjectionMatrix, false, projectionMatrix.getData());
            //view matrix
            Matrix4f uniformMatrix = this.cameraRotation.toGlRotationMatrix().transpose().multiply(
                    new Matrix4f(
                            new Vector4f(1f, 0f, 0f, (float) -cameraTranslation.getX()),
                            new Vector4f(0f, 1f, 0f, (float) -cameraTranslation.getY()),
                            new Vector4f(0f, 0f, 1f, (float) -cameraTranslation.getZ()),
                            new Vector4f(0f, 0f, 0f, 1f)
                    ).transpose());
            glUniformMatrix4fv(this.uniformViewMatrix, false, uniformMatrix.getData());

            //drawing
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
            this.drawLoop(deltaTime);
            glfwSwapBuffers(window); // swap the color buffers
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
    private void drawLoop(double deltaTime) {
        glUniform1i(this.uniformMode, 0);
        this.drawOrbits();
        this.drawCelestialBodies();
        glUniform1i(this.uniformMode, 1);
        this.drawOrbitAreas();
    }

    /**
     * Draws all registered orbits
     */
    private void drawOrbits() {
        Vector3d translation;
        for (Map.Entry<Orbit, Integer> entry : this.orbits.entrySet()) {
            translation = entry.getKey().getParentBody().getPosition(this.currentTime);
            Matrix4f modelMatrix = Quat4d.identity().roll(90).toGlRotationMatrix().multiply( //rotate stuff to make it level on x-y plane
                    new Matrix4f(
                            new Vector4f(1f, 0f, 0f, (float) translation.getX()),
                            new Vector4f(0f, 1f, 0f, (float) translation.getY()),
                            new Vector4f(0f, 0f, 1f, (float) translation.getZ()),
                            new Vector4f(0f, 0f, 0f, 1f)
                    ).transpose()
            );
            glUniformMatrix4fv(this.uniformModelMatrix, false, modelMatrix.getData());
            glBindVertexArray(entry.getValue());
            glDrawArrays(GL_LINE_LOOP, 0, Math.round((float) Math.pow(2, this.orbitResolution)));
        }
    }

    /**
     * Draws all registered orbits
     */
    private void drawOrbitAreas() {
        Vector3d translation;
        for (Map.Entry<Orbit, Integer> entry : this.orbits.entrySet()) {
            translation = entry.getKey().getParentBody().getPosition(this.currentTime);
            Matrix4f modelMatrix = Quat4d.identity().roll(90).toGlRotationMatrix().multiply( //rotate stuff to make it level on x-y plane
                    new Matrix4f(
                            new Vector4f(1f, 0f, 0f, (float) translation.getX()),
                            new Vector4f(0f, 1f, 0f, (float) translation.getY()),
                            new Vector4f(0f, 0f, 1f, (float) translation.getZ()),
                            new Vector4f(0f, 0f, 0f, 1f)
                    ).transpose()
            );
            glUniformMatrix4fv(this.uniformModelMatrix, false, modelMatrix.getData());
            glBindVertexArray(entry.getValue());
            glDrawArrays(GL_TRIANGLE_FAN, 0, Math.round((float) Math.pow(2, this.orbitResolution)));
        }
    }

    /**
     * Draws all registered celestial bodies
     */
    private void drawCelestialBodies() {
        Vector3d translation;
        Quat4d rotation;
        Matrix4f scale;
        Matrix4f scaleCoordinateSystem;
        Matrix4f modelMatrix;
        Matrix4f modelMatrixCoordinateSystem;
        for (Map.Entry<CelestialBody, Integer> entry : this.celestialBodies.entrySet()) {
            translation = entry.getKey().getPosition(this.currentTime);
            rotation = entry.getKey().getGlobalRotation(this.currentTime);
            if (this.scaleBodies <= 1.0d) {
                scale = new Matrix4f();
                scaleCoordinateSystem = new Matrix4f(
                        new Vector4f((float) (2d * entry.getKey().getRadius()), 0f, 0f, 0f),
                        new Vector4f(0f, (float) (2d * entry.getKey().getRadius()), 0f, 0f),
                        new Vector4f(0f, 0f, (float) (2d * entry.getKey().getRadius()), 0f),
                        new Vector4f(0f, 0f, 0f, 1f)
                ).transpose();
            } else {
                scale = new Matrix4f(
                        new Vector4f((float) (this.scaleBodies / entry.getKey().getRadius()), 0f, 0f, 0f),
                        new Vector4f(0f, (float) (this.scaleBodies / entry.getKey().getRadius()), 0f, 0f),
                        new Vector4f(0f, 0f, (float) (this.scaleBodies / entry.getKey().getRadius()), 0f),
                        new Vector4f(0f, 0f, 0f, 1f)
                ).transpose();
                scaleCoordinateSystem = new Matrix4f(
                        new Vector4f((float) (2d * this.scaleBodies), 0f, 0f, 0f),
                        new Vector4f(0f, (float) (2d * this.scaleBodies), 0f, 0f),
                        new Vector4f(0f, 0f, (float) (2d * this.scaleBodies), 0f),
                        new Vector4f(0f, 0f, 0f, 1f)
                ).transpose();
            }
            modelMatrix = (Quat4d.identity().roll(90).toGlRotationMatrix().multiply( //rotate stuff to make it level on x-y plane
                    new Matrix4f(
                            new Vector4f(1f, 0f, 0f, (float) translation.getX()),
                            new Vector4f(0f, 1f, 0f, (float) translation.getY()),
                            new Vector4f(0f, 0f, 1f, (float) translation.getZ()),
                            new Vector4f(0f, 0f, 0f, 1f)
                    ).transpose().multiply(rotation.toGlRotationMatrix()).multiply(scale)
            ));
            modelMatrixCoordinateSystem = (Quat4d.identity().roll(90).toGlRotationMatrix().multiply( //rotate stuff to make it level on x-y plane
                    new Matrix4f(
                            new Vector4f(1f, 0f, 0f, (float) translation.getX()),
                            new Vector4f(0f, 1f, 0f, (float) translation.getY()),
                            new Vector4f(0f, 0f, 1f, (float) translation.getZ()),
                            new Vector4f(0f, 0f, 0f, 1f)
                    ).transpose().multiply(rotation.toGlRotationMatrix()).multiply(scaleCoordinateSystem)
            ));
            glUniformMatrix4fv(this.uniformModelMatrix, false, modelMatrix.getData());
            glBindVertexArray(entry.getValue());
            glDrawArrays(GL_TRIANGLES, 0, this.celestialBodyVertexCounts.get(entry.getKey()));
            glUniformMatrix4fv(this.uniformModelMatrix, false, modelMatrixCoordinateSystem.getData());
            glBindVertexArray(this.coordinateSystemVao);
            glDrawArrays(GL_LINES, 0, 6);
        }
    }

    /**
     * Processes mouse and continous keyboard input
     *
     * @param deltaTime The time passed since this method was last called.
     */
    private void handleInput(double deltaTime) {
        //camera rotation
        glfwGetCursorPos(window, xPos, yPos);
        glfwSetCursorPos(window, 0, 0);
        this.cameraAzimuth = (this.cameraAzimuth + (this.cameraTurnSpeed * deltaTime * xPos[0])) % 360.0d;
        this.cameraZenith = (this.cameraZenith + (this.cameraTurnSpeed * deltaTime * yPos[0])) % 360.0d;
        //this.cameraZenith = Math.max(-89d, Math.min(89d, (this.cameraZenith + (this.cameraTurnSpeed * deltaTime * yPos[0]))));
        this.cameraRotation = Quat4d.identity().yaw(this.cameraRoll).pitch(this.cameraAzimuth).roll(this.cameraZenith);
        //control visualization speed
        if (KeyListener.isKeyDown(GLFW_KEY_UP)) {
            if (KeyListener.isKeyDown(GLFW_KEY_LEFT_SHIFT)) {
                this.setVisualizationSpeed(this.getVisualizationSpeed() + Scales.day() * 7d * deltaTime);
            } else if (KeyListener.isKeyDown(GLFW_KEY_LEFT_CONTROL)) {
                this.setVisualizationSpeed(this.getVisualizationSpeed() + Scales.hour() * deltaTime);
            } else {
                this.setVisualizationSpeed(this.getVisualizationSpeed() + Scales.day() * deltaTime);
            }
        }
        if (KeyListener.isKeyDown(GLFW_KEY_DOWN)) {
            if (KeyListener.isKeyDown(GLFW_KEY_LEFT_SHIFT)) {
                this.setVisualizationSpeed(this.getVisualizationSpeed() - Scales.day() * 7d * deltaTime);
            } else if (KeyListener.isKeyDown(GLFW_KEY_LEFT_CONTROL)) {
                this.setVisualizationSpeed(this.getVisualizationSpeed() - Scales.hour() * deltaTime);
            } else {
                this.setVisualizationSpeed(this.getVisualizationSpeed() - Scales.day() * deltaTime);
            }
        }
        if (KeyListener.isKeyDown(GLFW_KEY_KP_0)) {
            this.setVisualizationSpeed(0d);
        }
        if (KeyListener.isKeyDown(GLFW_KEY_KP_1)) {
            this.setVisualizationSpeed(Scales.second());
        }
        if (KeyListener.isKeyDown(GLFW_KEY_KP_2)) {
            this.setVisualizationSpeed(Scales.hour());
        }
        if (KeyListener.isKeyDown(GLFW_KEY_KP_3)) {
            this.setVisualizationSpeed(Scales.day());
        }
        if (KeyListener.isKeyDown(GLFW_KEY_KP_4)) {
            this.setVisualizationSpeed(Scales.year());
        }
        if (!this.isCameraFixed()) { //free camera mode, control with WASD
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
            if (KeyListener.isKeyDown(GLFW_KEY_Q)) {
                this.cameraRoll -= this.cameraTurnSpeed * deltaTime * 10d;
            }
            if (KeyListener.isKeyDown(GLFW_KEY_E)) {
                this.cameraRoll += this.cameraTurnSpeed * deltaTime * 10d;
            }
        } else { //fixed camera mode
            this.cameraRoll = 0d;
            this.cameraTranslation = Quat4d.identity().roll(90).rotateVector(this.lookAtFixed.getPosition(this.currentTime)).add(
                    this.cameraRotation.rotateVector(Vector3d.Z_AXIS_NEG).scale(-1d).normalize().scale(this.lookAtDistance)
            );
            //zoom
            int mouseScroll = -ScrollListener.getMouseScroll();
            if (KeyListener.isKeyDown(GLFW_KEY_LEFT_SHIFT)) {
                this.cameraFixedDistanceSteps = Math.max(2, this.cameraFixedDistanceSteps + this.cameraFixedDistanceSteps * (Math.pow(2, mouseScroll) - 1));
            } else {
                this.cameraFixedDistanceSteps = Math.max(2, this.cameraFixedDistanceSteps + mouseScroll);
            }
            double targetLookAtDistance = this.lookAtFixed.getRadius() * ((double) this.cameraFixedDistanceSteps);
            if (targetLookAtDistance > this.lookAtDistance) {
                this.lookAtDistance += (targetLookAtDistance - this.lookAtDistance) * deltaTime;
            } else if (targetLookAtDistance < this.lookAtDistance) {
                this.lookAtDistance -= (this.lookAtDistance - targetLookAtDistance) * deltaTime;
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
        glfwSetMouseButtonCallback(window, new MouseListener(this));
        glfwSetScrollCallback(window, new ScrollListener(this));

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