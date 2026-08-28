package input;

import core.Window;
import lombok.Getter;
import lombok.Setter;
import org.lwjgl.glfw.GLFW;

import java.util.Arrays;

import static org.lwjgl.glfw.GLFW.*;

public class Input {
    private static long focusedWindowId;

    // Keys
    private static final boolean[] keys = new boolean[GLFW_KEY_LAST + 1];
    private static final boolean[] pressedKeys = new boolean[GLFW_KEY_LAST + 1];
    private static final boolean[] releasedKeys = new boolean[GLFW_KEY_LAST + 1];

    // Mouse
    private static final boolean[] buttons = new boolean[GLFW_MOUSE_BUTTON_LAST + 1];
    private static final boolean[] pressedButtons = new boolean[GLFW_MOUSE_BUTTON_LAST + 1];
    private static final boolean[] releasedButtons = new boolean[GLFW_MOUSE_BUTTON_LAST + 1];

    // Cursor
    @Getter
    private static double x, y;
    private static double lastX, lastY;
    public static double xOffset, yOffset;
    private static boolean cursorCaptured = false;
    private static double savedMouseX, savedMouseY;

    @Setter
    public static boolean firstMouse = true;

    // Scroll
    public static double xScrollOffset, yScrollOffset;

    public static void update() {
        focusedWindowId = Window.windows.stream()
                .map(Window::getId)
                .filter(id -> GLFW.glfwGetWindowAttrib(id, GLFW.GLFW_FOCUSED) == 1)
                .findFirst()
                .orElse(0L);
    }

    public static void endFrame() {
        if (cursorCaptured) {
            Window window = getFocusedWindow();
            if (window != null) {
                // Re-centrar el cursor constantemente en el punto de origen (savedMouseX/Y)
                GLFW.glfwSetCursorPos(window.getId(), savedMouseX, savedMouseY);
                // Prevenir que el warp genere un delta erróneo en el siguiente frame
                lastX = savedMouseX;
                lastY = savedMouseY;
            }
        }

        // Reset keys
        Arrays.fill(pressedKeys, false);
        Arrays.fill(releasedKeys, false);

        // Reset buttons
        Arrays.fill(pressedButtons, false);
        Arrays.fill(releasedButtons, false);

        // Reset cursor
        xOffset = 0;
        yOffset = 0;

        // Reset scroll
        xScrollOffset = 0;
        yScrollOffset = 0;
    }

    public static void setKeyCallback(long windowId, int key, int scancode, int action, int mods) {
        switch (action) {
            case GLFW_PRESS -> {
                keys[key] = true;
                pressedKeys[key] = true;
            }
            case GLFW_RELEASE -> {
                keys[key] = false;
                releasedKeys[key] = true;
            }
        }
    }

    public static void setMouseButtonCallback(long windowId, int button, int action, int mods) {
        switch (action) {
            case GLFW_PRESS -> {
                buttons[button] = true;
                pressedButtons[button] = true;
            }
            case GLFW_RELEASE -> {
                buttons[button] = false;
                releasedButtons[button] = true;
            }
        }
    }

    public static void setCursorPosCallback(long windowId, double xPos, double yPos) {
        x = xPos;
        y = yPos;

        if (firstMouse) {
            lastX = xPos;
            lastY = yPos;
            firstMouse = false;
            xOffset = 0;
            yOffset = 0;
            return;
        }

        xOffset = xPos - lastX;
        yOffset = lastY - yPos;
        lastX = xPos;
        lastY = yPos;
    }

    public static void setCursorCaptured(boolean captured) {
        if (cursorCaptured == captured) return;

        Window window = getFocusedWindow();
        if (window == null) return;

        cursorCaptured = captured;

        if (captured) {
            // 1. Guardar la posición fija de anclaje
            savedMouseX = x;
            savedMouseY = y;

            // 2. Ocultar el cursor sin activar la fijación nativa al centro de GLFW
            window.setCursorMode(GLFW_CURSOR_HIDDEN);
        } else {
            // 1. Volver a mostrar el cursor en su sitio
            window.setCursorMode(GLFW_CURSOR_NORMAL);

            // 2. Asegurar que está en las coordenadas originales
            GLFW.glfwSetCursorPos(window.getId(), savedMouseX, savedMouseY);
            resetFirstMouse();
        }
    }

    public static void setScrollCallback(long windowId, double xOffset, double yOffset) {
        xScrollOffset = xOffset;
        yScrollOffset = yOffset;
    }

    public static boolean isKey(int key) {
        if (focusedWindowId == 0) return false;
        return keys[key];
    }

    public static boolean isKeyDown(int key) {
        if (focusedWindowId == 0) return false;
        return keys[key] && pressedKeys[key];
    }

    public static boolean isKeyUp(int key) {
        if (focusedWindowId == 0) return false;
        return releasedKeys[key];
    }

    public static boolean isButton(int button) {
        if (focusedWindowId == 0) return false;
        return buttons[button];
    }

    public static boolean isButtonDown(int button) {
        if (focusedWindowId == 0) return false;
        return buttons[button] && pressedButtons[button];
    }

    public static boolean isButtonUp(int button) {
        if (focusedWindowId == 0) return false;
        return releasedButtons[button];
    }

    public static Window getFocusedWindow() {
        return Window.findById(focusedWindowId).orElse(null);
    }

    public static void resetFirstMouse() {
        firstMouse = true;
        xOffset = 0;
        yOffset = 0;
    }
}
