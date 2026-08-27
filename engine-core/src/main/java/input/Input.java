package input;

import core.Window;
import lombok.Getter;
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
        if (firstMouse) {
            lastX = xPos;
            lastY = yPos;
            firstMouse = false;
        }

        xOffset = xPos - lastX;
        yOffset = lastY - yPos;
        lastX = xPos;
        lastY = yPos;
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
}
