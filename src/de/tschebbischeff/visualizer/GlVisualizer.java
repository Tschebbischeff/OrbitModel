package de.tschebbischeff.visualizer;

import de.tschebbischeff.math.Matrix3d;
import de.tschebbischeff.math.Quat4d;
import de.tschebbischeff.math.Vector3d;
import de.tschebbischeff.model.CelestialBody;
import de.tschebbischeff.model.Orbit;
import de.tschebbischeff.model.Scales;
import de.tschebbischeff.visualizer.silvertiger.math.Matrix4f;
import de.tschebbischeff.visualizer.silvertiger.math.Vector4f;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.awt.*;
import java.nio.*;
import java.util.ArrayList;

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
     * Object of a class that handles loading, compiling and using shaders from the shaders folder.
     */
    private ShaderManager shaderManager;

    /**
     * The order in which the orbits and celestial bodies are colored.
     */
    private Color[] colorOrder;

    /**
     * The celestial bodies to draw.
     */
    private ArrayList<CelestialBody> celestialBodies = new ArrayList<>();

    /**
     * The orbits to draw.
     */
    private ArrayList<Orbit> orbits = new ArrayList<Orbit>();

    /**
     * Orbit resolution, how many vertices per orbit should be drawn.
     * There will be (2^orbitResolution) vertices to build the elliptical orbit.
     * orbitResolution has to be at least 3.
     */
    private double orbitResolution = 7;

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
     * The camera translation
     */
    private Vector3d cameraTranslation = new Vector3d(0.0d, 0.0d, Scales.astronomicalUnit()/2.0d);

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
    private double cameraSpeed = Scales.astronomicalUnit() / 10.0d;

    /**
     * The speed with which the camera turns.
     */
    private double cameraTurnSpeed = 45.0d;

    /**
     * Creates a new GlVisualizer object. The visualization can be started with the run method.
     */
    public GlVisualizer() {
        this.colorOrder = new Color[]{
                new Color(1.0f, 0.0f, 0.0f),
                new Color(1.0f, 0.5f, 0.0f),
                new Color(1.0f, 1.0f, 0.0f),
                new Color(0.5f, 1.0f, 0.0f),
                new Color(0.0f, 1.0f, 0.0f),
                new Color(0.0f, 1.0f, 0.5f),
                new Color(0.0f, 1.0f, 1.0f),
                new Color(0.0f, 0.5f, 1.0f),
                new Color(0.0f, 0.0f, 1.0f)
        };
        this.shaderManager = new ShaderManager();
    }

    /**
     * Sets the color order to use for the visualization. The color order determines in which color the orbits
     * and bodies are drawn.
     *
     * @param colorOrder The new color order to use for the orbits and bodies.
     * @return This GlVisualizer for fluent method calls.
     */
    public GlVisualizer setColorOrder(Color[] colorOrder) {
        this.colorOrder = colorOrder;
        return this;
    }

    /**
     * Adds an orbit to the list of orbits that are drawn.
     *
     * @param orbit The orbit to add to the list.
     * @return This GlVisualizer for fluent method calls.
     */
    public GlVisualizer addOrbit(Orbit orbit) {
        this.orbits.add(orbit);
        return this;
    }

    /**
     * Adds a celestial body to the list of orbits that are drawn.
     *
     * @param body The body to add to the list.
     * @return This GlVisualizer for fluent method calls.
     */
    public GlVisualizer addCelestialBody(CelestialBody body) {
        this.celestialBodies.add(body);
        return this;
    }

    /**
     * Sets the resolution to draw the orbits with. A total of 2^resolution vertices will be drawn for
     * each orbit. The minimum value is three.
     *
     * @param resolution The new resolution to use for orbit drawing.
     * @return This GlVisualizer for fluent method calls.
     */
    public GlVisualizer setOrbitResolution(double resolution) {
        this.orbitResolution = resolution;
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
     * A value of 1.0 means 1.0 degree per second.
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
     * Starts the visualization
     */
    public void run() {

        this.initialize();
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        int vao = glGenVertexArrays();
        glBindVertexArray(vao);

        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer vertices = stack.mallocFloat(3 * 6);
            vertices.put(-0.6f).put(-0.4f).put(0f).put(1f).put(0f).put(0f);
            vertices.put(0.6f).put(-0.4f).put(0f).put(0f).put(1f).put(0f);
            vertices.put(0f).put(0.6f).put(0f).put(0f).put(0f).put(1f);
            vertices.flip();

            int vbo = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        }

        this.shaderManager.loadAndCompileShaderPair("default");
        int shaderProgram = this.shaderManager.useShaders("default");

        int posAttrib = glGetAttribLocation(shaderProgram, "position");
        glEnableVertexAttribArray(posAttrib);
        glVertexAttribPointer(posAttrib, 3, GL_FLOAT, false, 6 * Float.BYTES, 0);
        int colAttrib = glGetAttribLocation(shaderProgram, "color");
        glEnableVertexAttribArray(colAttrib);
        glVertexAttribPointer(colAttrib, 3, GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);

        int uniModel = glGetUniformLocation(shaderProgram, "model");
        int uniView = glGetUniformLocation(shaderProgram, "view");
        int uniProjection = glGetUniformLocation(shaderProgram, "projection");
        Matrix4f model = new Matrix4f();
        glUniformMatrix4fv(uniModel, false, model.getData());
        float ratio = 1000f / 1000f;
        //Matrix4f projection = Matrix4f.perspective(90f, ratio, -1f, 1f);
        //Matrix4f projection = Matrix4f.frustum(-1f, 1f,-1f, 1f, -1f, 1f);
        Matrix4f projection = Matrix4f.orthographic(-ratio, ratio, -1f, 1f, 0.1f, 10f);
        glUniformMatrix4fv(uniProjection, false, projection.getData());

        // Set the clear color
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        this.cameraTranslation.setData(new double[]{0.0d, 0.0d, 1.0d});
        this.cameraSpeed = 0.5d;

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        long lastTime = System.nanoTime();
        double deltaTime;
        while (!glfwWindowShouldClose(window)) {
            deltaTime = (System.nanoTime() - lastTime) / 1000000000.0d;
            lastTime = System.nanoTime();

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

            double[][] rotation = this.cameraRotation.toRotationMatrix().getData();
            Matrix4f r = new Matrix4f(
                    new Vector4f((float) rotation[0][0], (float) rotation[1][0], (float) rotation[2][0], (float) -cameraTranslation.getX()),
                    new Vector4f((float) rotation[0][1], (float) rotation[1][1], (float) rotation[2][1], (float) -cameraTranslation.getY()),
                    new Vector4f((float) rotation[0][2], (float) rotation[1][2], (float) rotation[2][2], (float) -cameraTranslation.getZ()),
                    new Vector4f(0f, 0f, 0f, 1f)
            ).transpose();
            Matrix4f t = new Matrix4f(
                    new Vector4f(1f, 0f, 0f, (float) -cameraTranslation.getX()),
                    new Vector4f(0f, 1f, 0f, (float) -cameraTranslation.getY()),
                    new Vector4f(0f, 0f, 1f, (float) -cameraTranslation.getZ()),
                    new Vector4f(0f, 0f, 0f, 1f)
            ).transpose();

            glUniformMatrix4fv(uniView, false, (t.multiply(r)).getData());

            glDrawArrays(GL_TRIANGLES, 0, 3);
            //this.loop(deltaTime);

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
        window = glfwCreateWindow(1000, 1000, "OrbitModeler Visualizer", NULL, NULL);
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

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);
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
        int colorIndex = 0;
        Vector3d position;
        glLineWidth(5.0f);
        for (Orbit orbit : this.orbits) {
            glColor4f(this.colorOrder[colorIndex].getRed(), this.colorOrder[colorIndex].getGreen(), this.colorOrder[colorIndex].getBlue(), this.orbitColorAlpha);
            glBegin(GL_LINE_STRIP);
            for (double trueAnomaly = 0; trueAnomaly <= 2 * Math.PI; trueAnomaly += (2 * Math.PI) / Math.pow(2, this.orbitResolution)) {
                position = orbit.getOrbitalPositionByTrueAnomaly(trueAnomaly);
                glVertex3d(position.getX(), position.getY(), position.getZ());
            }
            glEnd();
            colorIndex = (colorIndex + 1) % this.colorOrder.length;
        }
    }

    /**
     * Draws all registered celestial bodies
     */
    private void drawCelestialBodies() {
        int colorIndex = 0;
        Vector3d position;
        glPointSize(10.0f);
        for (CelestialBody body : this.celestialBodies) {
            position = body.getPosition(this.currentTime);
            glColor4f(this.colorOrder[colorIndex].getRed(), this.colorOrder[colorIndex].getGreen(), this.colorOrder[colorIndex].getBlue(), this.celestialBodyColorAlpha);
            glBegin(GL_POINT);
            glVertex3d(position.getX(), position.getY(), position.getZ());
            glEnd();
            colorIndex = (colorIndex + 1) % this.colorOrder.length;
        }
    }

    /**
     * Processes mouse and continous keyboard input
     *
     * @param deltaTime The time passed since this method was last called.
     */
    private void handleInput(double deltaTime) {
        if (KeyListener.isKeyDown(GLFW_KEY_W)) {
            this.cameraTranslation = this.cameraTranslation.add(this.cameraRotation.rotateVector(Vector3d.Z_AXIS_NEG).scale(this.cameraSpeed*deltaTime));
        }
        if (KeyListener.isKeyDown(GLFW_KEY_A)) {
            this.cameraTranslation = this.cameraTranslation.add(this.cameraRotation.rotateVector(Vector3d.X_AXIS_NEG).scale(this.cameraSpeed*deltaTime));
        }
        if (KeyListener.isKeyDown(GLFW_KEY_S)) {
            this.cameraTranslation = this.cameraTranslation.add(this.cameraRotation.rotateVector(Vector3d.Z_AXIS).scale(this.cameraSpeed*deltaTime));
        }
        if (KeyListener.isKeyDown(GLFW_KEY_D)) {
            this.cameraTranslation = this.cameraTranslation.add(this.cameraRotation.rotateVector(Vector3d.X_AXIS).scale(this.cameraSpeed*deltaTime));
        }
        if (KeyListener.isKeyDown(GLFW_KEY_UP)) {
            this.cameraZenith = Math.min(90.0d, this.cameraZenith + (this.cameraTurnSpeed*deltaTime));
        }
        if (KeyListener.isKeyDown(GLFW_KEY_DOWN)) {
            this.cameraZenith = Math.max(-90.0d, this.cameraZenith - (this.cameraTurnSpeed*deltaTime));
        }
        if (KeyListener.isKeyDown(GLFW_KEY_LEFT)) {
            this.cameraAzimuth = (this.cameraAzimuth - (this.cameraTurnSpeed*deltaTime)) % 360.0d;
        }
        if (KeyListener.isKeyDown(GLFW_KEY_RIGHT)) {
            this.cameraAzimuth = (this.cameraAzimuth + (this.cameraTurnSpeed*deltaTime)) % 360.0d;
        }
        this.cameraRotation = Quat4d.identity().roll(this.cameraZenith).pitch(this.cameraAzimuth);
    }
}