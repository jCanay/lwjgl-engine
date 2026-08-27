package core;

import input.Input;
import lombok.Getter;
import lombok.Setter;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
public class Window {
    public record WindowConfig(String title, int width, int height, boolean vsyncEnabled) {
        public static WindowConfig defaultConfig() {
            // <-- 16:9 -->
            //  720 × 480  (SD)
            // 1280 × 720  (HD)
            // 1366 × 768  (Laptops)
            // 1600 × 900  (HD+)
            // 1920 × 1080 (FHD)
            // 2560 × 1440 (QHD)
            // 3840 × 2160 (UHD)

            // <-- 4:3 -->
            //  640 × 480  (SD)
            //  960 × 720  (HD)
            // 1024 × 768  (Laptops)
            // 1200 × 900  (HD+)
            // 1440 × 1080 (FHD)
            // 1920 × 1440 (QHD)
            // 2880 × 2160 (UHD)

            return new WindowConfig("Window " + Window.index++, 960, 720, false);
        }
    }

    public final static List<Window> windows = new ArrayList<>();
    private static int index = 0;
    private long id;
    private String title;
    private int width, height;
    private int cursorMode = GLFW.GLFW_CURSOR_NORMAL;
    private boolean resized, vsyncEnabled, fullscreenEnabled;

    private Window(String title, int width, int height, boolean vsyncEnabled) {
        this.title = title;
        this.width = width;
        this.height = height;
        this.vsyncEnabled = vsyncEnabled;
    }

    public static Window create() {
        WindowConfig config = WindowConfig.defaultConfig();
        return create(config.title, config.width, config.height, config.vsyncEnabled, 0, false);
    }

    public static Window create(String title, int width, int height, boolean vsyncEnabled, int monitor, boolean fullscreenEnabled) {
        if (!GLFW.glfwInit()) throw new RuntimeException("Failed to initialize GLFW.");

        // Apply hints
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GLFW.GLFW_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_MAXIMIZED, GLFW.GLFW_FALSE);
//        GLFW.glfwWindowHint(GLFW.GLFW_SAMPLES, 4); // Multisampling antialiasing MSAA

        // Check monitor and fullscreen
        long monitorId = 0;
        if (fullscreenEnabled) {
            PointerBuffer monitors = GLFW.glfwGetMonitors();
            if (monitors == null || !monitors.hasRemaining()) {
                GLFW.glfwTerminate();
                throw new RuntimeException("Failed to create GLFW Window: No monitors found.");
            }

            monitorId = monitors.get(monitor);
            GLFWVidMode vidMode = GLFW.glfwGetVideoMode(monitorId);
            if (vidMode == null) {
                throw new RuntimeException("Failed to create GLFW Window: Failed to get the monitor's video mode");
            }
            width = vidMode.width();
            height = vidMode.height();
        }

        // Create window
        Window window = new Window(title, width, height, vsyncEnabled);
        window.id = (GLFW.glfwCreateWindow(window.width, window.height, window.title, fullscreenEnabled ? monitorId : 0,
                0));
        if (window.id == 0) {
            window.cleanup();
            throw new RuntimeException("Failed to create GLFW Window.");
        }

        // Resize callback
        GLFW.glfwSetFramebufferSizeCallback(window.id, (windowId, w, h) -> {
            window.width = w;
            window.height = h;

            GL30.glViewport(0, 0, w, h);
        });

        // Key callback
        GLFW.glfwSetKeyCallback(window.id, Input::setKeyCallback);

        // Mouse callback
        GLFW.glfwSetMouseButtonCallback(window.id, Input::setMouseButtonCallback);

        // Cursor callback
        GLFW.glfwSetInputMode(window.id, GLFW.GLFW_CURSOR, window.cursorMode);
        GLFW.glfwSetCursorPosCallback(window.id, Input::setCursorPosCallback);

        // Scroll callback
        GLFW.glfwSetScrollCallback(window.id, Input::setScrollCallback);

        // Set context and vsync
        if (index == 1) GLFW.glfwMakeContextCurrent(window.id);
        GLFW.glfwSwapInterval(window.vsyncEnabled ? 1 : 0); // V-Sync
        GL.createCapabilities();
        GL30.glViewport(0, 0, width, height);

        GLFW.glfwShowWindow(window.id);

        windows.add(window);
        return window;
    }

    public static Optional<Window> findById(long windowId) {
        return windows.stream().filter(w -> w.id == windowId).findFirst();
    }

    public static void closeLast() {
        if (!windows.isEmpty()) {
            windows.getLast().cleanup();
        }
    }

    public void makeContextCurrent() {
        if (id == 0) return;
        GLFW.glfwMakeContextCurrent(id);
    }

    public void swapBuffers() {
        GLFW.glfwSwapBuffers(id);
    }

    public boolean shouldClose() {
        if (id == 0) return false;
        return GLFW.glfwWindowShouldClose(id);
    }

    public void cleanup() {
        if (id == 0) return;
        Callbacks.glfwFreeCallbacks(id);
        GLFW.glfwDestroyWindow(id);
        windows.remove(this);
        id = 0;
    }

    public void setTitle(String title) {
        if (id == 0) return;
        this.title = title;
        GLFW.glfwSetWindowTitle(id, title);
    }

    public void setVsyncEnabled(boolean vsyncEnabled) {
        if (id == 0) return;
        this.vsyncEnabled = vsyncEnabled;
        GLFW.glfwSwapInterval(vsyncEnabled ? 1 : 0);
    }

    public void setWidth(int width) {
        if (id == 0) return;
        this.width = width;
        GLFW.glfwSetWindowSize(id, width, height);
    }

    public void setHeight(int height) {
        if (id == 0) return;
        this.height = height;
        GLFW.glfwSetWindowSize(id, width, height);
    }

    public void setCursorMode(int cursorMode) {
        GLFW.glfwSetInputMode(id, GLFW.GLFW_CURSOR, cursorMode);
        this.cursorMode = cursorMode;
    }
}
