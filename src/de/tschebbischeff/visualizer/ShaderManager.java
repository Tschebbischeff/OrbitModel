package de.tschebbischeff.visualizer;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

/**
 * Manages loading and compiling shaders
 *
 * @author Tarek
 * @version 1.0.0
 */
public class ShaderManager {

    /**
     * The loaded sources of vertex shaders.
     */
    private HashMap<String, String> vertexShaderSources;

    /**
     * The OpenGL handles of the compiled vertex shaders.
     */
    private HashMap<String, Integer> vertexShaderHandles;

    /**
     * The loaded sources of fragment shaders.
     */
    private HashMap<String, String> fragmentShaderSources;

    /**
     * The OpenGL handles of the compiled fragment shaders.
     */
    private HashMap<String, Integer> fragmentShaderHandles;

    /**
     * The currently used shaders in a shader program object.
     */
    private Integer shaderProgram = null;

    /**
     * Creates a new shader manager, without any loaded shaders.
     */
    public ShaderManager() {
        this.vertexShaderSources = new HashMap<>();
        this.vertexShaderHandles = new HashMap<>();
        this.fragmentShaderSources = new HashMap<>();
        this.fragmentShaderHandles = new HashMap<>();
    }

    /**
     * Reads a file into a String. The file is assumed to have UTF-8 encoding.
     *
     * @param filepath The path to the file.
     * @return The contents of the file or null, if the file does not exist or couldn't be read.
     */
    private static String readFile(String filepath) {
        if (Files.exists(Paths.get(filepath), LinkOption.NOFOLLOW_LINKS)) {
            try {
                byte[] encoded = Files.readAllBytes(Paths.get(filepath));
                return new String(encoded, StandardCharsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * Reads the contents of a vertex shader from the shader folder.
     *
     * @param name The name of the file without its file ending.
     * @return The contents of the shader file.
     */
    private static String readVertexShader(String name) {
        return readFile("./res/shaders/" + name + ".vsh");
    }

    /**
     * Reads the contents of a fragment shader from the shader folder.
     *
     * @param name The name of the file without its file ending.
     * @return The contents of the shader file.
     */
    private static String readFragmentShader(String name) {
        return readFile("./res/shaders/" + name + ".fsh");
    }

    /**
     * Compiles or recompiles the vertex shader with the given name, which sources should be loaded and adds them
     * to the list of compiled, ready to use, shaders.
     *
     * @param name The name of the vertex shader.
     */
    public void compileVertexShader(String name) {
        String source = this.vertexShaderSources.get(name);
        int handle = this.vertexShaderHandles.containsKey(name) ? this.vertexShaderHandles.get(name) : glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(handle, source);
        glCompileShader(handle);
        try {
            int status = glGetShaderi(handle, GL_COMPILE_STATUS);
            if (status != GL_TRUE) {
                throw new RuntimeException(glGetShaderInfoLog(handle));
            }
            this.vertexShaderHandles.put(name, handle);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    /**
     * Compiles or recompiles the fragment shader with the given name, which sources should be loaded and adds them
     * to the list of compiled, ready to use, shaders.
     *
     * @param name The name of the fragment shader.
     */
    public void compileFragmentShader(String name) {
        String source = this.fragmentShaderSources.get(name);
        int handle = this.fragmentShaderHandles.containsKey(name) ? this.fragmentShaderHandles.get(name) : glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(handle, source);
        glCompileShader(handle);
        try {
            int status = glGetShaderi(handle, GL_COMPILE_STATUS);
            if (status != GL_TRUE) {
                throw new RuntimeException(glGetShaderInfoLog(handle));
            }
            this.fragmentShaderHandles.put(name, handle);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads a vertex and fragment shader with the same name from the shader folder,
     * compiles them and adds them to the list of available shaders.
     *
     * @param name The name of both the vertex and the fragment shader.
     */
    public void loadAndCompileShaderPair(String name) {
        String vsh = readVertexShader(name);
        String fsh = readFragmentShader(name);
        if (vsh != null && fsh != null) {
            this.vertexShaderSources.put(name, vsh);
            this.fragmentShaderSources.put(name, fsh);
            this.compileVertexShader(name);
            this.compileFragmentShader(name);
        }
    }

    /**
     * Loads a vertex shader from the shader folder,
     * compiles them and adds them to the list of available shaders.
     *
     * @param name The name of the vertex shader.
     */
    public void loadAndCompileVertexShader(String name) {
        String vsh = readVertexShader(name);
        if (vsh != null) {
            this.vertexShaderSources.put(name, vsh);
            this.compileVertexShader(name);
        }
    }

    /**
     * Loads a fragment shader from the shader folder,
     * compiles them and adds them to the list of available shaders.
     *
     * @param name The name of the fragment shader.
     */
    public void loadAndCompileFragmentShader(String name) {
        String fsh = readFragmentShader(name);
        if (fsh != null) {
            this.fragmentShaderSources.put(name, fsh);
            this.compileFragmentShader(name);
        }
    }

    /**
     * Uses the shaders specified by their names.
     *
     * @param name The name of the vertex and the fragment shader to use.
     */
    public int useShaders(String name) {
        return this.useShaders(name, name);
    }

    /**
     * Uses the shaders specified by their names.
     *
     * @param vertexName   The name of the vertex shader to use.
     * @param fragmentName The name of the fragment shader to use.
     * @return The handle for the compiled shader program.
     */
    public int useShaders(String vertexName, String fragmentName) {
        if (!this.vertexShaderHandles.containsKey(vertexName) || !this.fragmentShaderHandles.containsKey(fragmentName)) {
            throw new IllegalArgumentException("Vertex or Fragment shader with specified name does not exist.");
        }
        this.shaderProgram = glCreateProgram();
        glAttachShader(this.shaderProgram, this.vertexShaderHandles.get(vertexName));
        glAttachShader(this.shaderProgram, this.fragmentShaderHandles.get(fragmentName));
        glBindFragDataLocation(this.shaderProgram, 0, "fragColor"); //maybe don't do this here or create an interface for it?
        glLinkProgram(this.shaderProgram);
        int status = glGetProgrami(shaderProgram, GL_LINK_STATUS);
        if (status != GL_TRUE) {
            throw new RuntimeException(glGetProgramInfoLog(shaderProgram));
        }
        glUseProgram(this.shaderProgram);
        return this.shaderProgram;
    }
}
