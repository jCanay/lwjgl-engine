package util;

import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;

import static org.lwjgl.opengl.GL33.*;

/**
 * A utility class for loading files in the Java Resources folder
 */
public class ResourceLoader {
    public interface BufferLoader extends AutoCloseable {
        ByteBuffer get();

        @Override
        void close();
    }

    public static byte[] loadBytes(String path) {
        try (InputStream stream = ResourceLoader.class.getModule().getResourceAsStream(path)) {
            if (stream == null) throw new IllegalArgumentException("Could not find the resource");
            return stream.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String loadString(String path) {
        return new String(loadBytes(path), StandardCharsets.UTF_8);
    }

    /**
     * Reads a resource file by opening a {@link ByteBuffer} that is then passed to a {@link Function},
     * ensuring that the memory allocated is freed after using the buffer.
     *
     * @param path     The relative resource path.
     * @param function The callback function.
     * @return The return value of the callback function.
     * @see ByteBuffer
     */
    public static <T> T useBuffer(String path, Function<ByteBuffer, T> function) {
        byte[] bytes = loadBytes(path);
        ByteBuffer buffer = MemoryUtil.memAlloc(bytes.length);
        try {
            buffer.put(bytes).flip();
            return function.apply(buffer);
        } finally {
            MemoryUtil.memFree(buffer);
        }
    }

    public static BufferLoader getBuffer(String path) {
        byte[] bytes = loadBytes(path);
        ByteBuffer buffer = MemoryUtil.memAlloc(bytes.length);
        buffer.put(bytes).flip();

        return new BufferLoader() {
            @Override
            public ByteBuffer get() {
                return buffer;
            }

            @Override
            public void close() {
                MemoryUtil.memFree(buffer);
            }
        };
    }

    public static int loadTexture(String resourcePath) {
        Log.info("Loading texture from resource: " + resourcePath);
        
        try (MemoryStack stack = MemoryStack.stackPush(); BufferLoader loader = getBuffer(resourcePath)) {
            IntBuffer widthBuffer = stack.mallocInt(1);
            IntBuffer heightBuffer = stack.mallocInt(1);
            IntBuffer channelsBuffer = stack.mallocInt(1);

            STBImage.stbi_set_flip_vertically_on_load(true);

            ByteBuffer img = STBImage.stbi_load_from_memory(loader.get(), widthBuffer, heightBuffer, channelsBuffer, 4);

            if (img == null) {
                throw new RuntimeException("Failed to load texture  " + resourcePath + ": " + STBImage.stbi_failure_reason());
            }

            int id = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, id);

            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR); // Cambiar a GL_NEAREST para quitar suavizado (pixel art)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

            int width = widthBuffer.get(0);
            int height = heightBuffer.get(0);

            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, img);
            glGenerateMipmap(GL_TEXTURE_2D);

            // Liberar recursos de la memoria
            STBImage.stbi_image_free(img);

            return id;
        }
    }

    public static int loadTexture(String texFileName, String directory) {
        String path = directory.concat("/").concat(texFileName);
        Log.info("Loading texture from absolute path: " + texFileName);

        ByteBuffer buffer = null;
        try (MemoryStack stack = MemoryStack.stackPush();) {
            IntBuffer widthBuffer = stack.mallocInt(1);
            IntBuffer heightBuffer = stack.mallocInt(1);
            IntBuffer channelsBuffer = stack.mallocInt(1);

            byte[] bytes = Files.readAllBytes(Path.of(path));
            buffer = MemoryUtil.memAlloc(bytes.length);
            buffer.put(bytes).flip();

            STBImage.stbi_set_flip_vertically_on_load(true);

            ByteBuffer img = STBImage.stbi_load_from_memory(buffer, widthBuffer, heightBuffer, channelsBuffer, 4);

            if (img == null) {
                throw new RuntimeException("Failed to load texture  " + path + ": " + STBImage.stbi_failure_reason());
            }

            int id = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, id);

            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR); // Cambiar a GL_NEAREST para quitar suavizado (pixel art)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

            int width = widthBuffer.get(0);
            int height = heightBuffer.get(0);

            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, img);
            glGenerateMipmap(GL_TEXTURE_2D);

            // Liberar recursos de la memoria
            STBImage.stbi_image_free(img);

            return id;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (buffer != null) {
                MemoryUtil.memFree(buffer);
            }
        }
    }
}
