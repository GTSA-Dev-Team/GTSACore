package pl.epsi.gtsacore.api.renderer.shader;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL45;
import pl.epsi.gtsacore.util.SACUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;

public class SACShaderProgram {

    public int ID;

    public SACShaderProgram(ResourceLocation vertex, ResourceLocation fragment) {
        this(SACUtils.asInputStream(vertex), SACUtils.asInputStream(fragment));
    }

    private SACShaderProgram(InputStream vertexStream, InputStream fragmentStream) {
        RenderSystem.recordRenderCall(() -> {
            try {
                String vCode = SACUtils.readFile(vertexStream);
                String fCode = SACUtils.readFile(fragmentStream);

                int vertex, fragment;

                vertex = GL45.glCreateShader(GL45.GL_VERTEX_SHADER);
                GL45.glShaderSource(vertex, vCode);
                GL45.glCompileShader(vertex);
                checkErrors(vertex, "SHADER-VERTEX");

                fragment = GL45.glCreateShader(GL45.GL_FRAGMENT_SHADER);
                GL45.glShaderSource(fragment, fCode);
                GL45.glCompileShader(fragment);
                checkErrors(fragment, "SHADER-FRAGMENT");

                ID = GL45.glCreateProgram();
                GL45.glAttachShader(ID, vertex);
                GL45.glAttachShader(ID, fragment);
                GL45.glLinkProgram(ID);
                checkErrors(ID, "PROGRAM");

                GL45.glDeleteShader(vertex);
                GL45.glDeleteShader(fragment);
            } catch (IOException e) {
                System.err.println("An error occurred when building the shader");
                e.printStackTrace();
            }
        });
    }

    public void uniformMat4f(String name, Matrix4f mat) {
        int location = GL45.glGetUniformLocation(this.ID, name);
        FloatBuffer buf = BufferUtils.createFloatBuffer(16);
        mat.get(buf);
        GL45.glUniformMatrix4fv(location, false, buf);
    }

    public void uniformTexture(String name, int slot) {
        int location = GL45.glGetUniformLocation(this.ID, name);
        GL45.glUniform1i(location, slot);
    }

    public void uniformFloat2(String name, float f1, float f2) {
        int location = GL45.glGetUniformLocation(this.ID, name);
        GL45.glUniform2f(location, f1, f2);
    }

    public void uniformIntArray(String name, int[] array) {
        int location = GL45.glGetUniformLocation(this.ID, name);
        GL45.glUniform1iv(location, array);
    }

    public void uniformFloat(String name, float f) {
        int location = GL45.glGetUniformLocation(this.ID, name);
        GL45.glUniform1f(location, f);
    }

    public void use() {
        GL45.glUseProgram(ID);
    }

    private void checkErrors(int shader, String type) {
        if (type.equals("PROGRAM")) {
            if (!GL45.glGetProgramInfoLog(shader).isEmpty()) {
                System.out.println(GL45.glGetProgramInfoLog(shader));
                throw new RuntimeException("Program failed!");
            }
        } else {
            if (!GL45.glGetShaderInfoLog(shader).isEmpty()) {
                System.out.println(GL45.glGetShaderInfoLog(shader));
                throw new RuntimeException("Shader " + type + " didn't load properly!");
            }
        }
    }

}
