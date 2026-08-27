package input;

import static org.lwjgl.glfw.GLFW.*;

public class RawInput {
    // Teclado
    private static final boolean[] keys = new boolean[GLFW_KEY_LAST + 1];
    private static final boolean[] keysJustPressed = new boolean[GLFW_KEY_LAST + 1];
    private static final boolean[] keysJustReleased = new boolean[GLFW_KEY_LAST + 1];

    // Ratón
    private static final boolean[] mouseButtons = new boolean[GLFW_MOUSE_BUTTON_LAST + 1];
    private static final boolean[] mouseButtonsJustPressed = new boolean[GLFW_MOUSE_BUTTON_LAST + 1];
    private static float mouseX = 0, mouseY = 0;
    private static float mouseDeltaX = 0, mouseDeltaY = 0;
    private static float scrollX = 0, scrollY = 0;
    private static boolean isFirstMouse = true;

    // Control de foco
    private static boolean isViewportFocused = false;

    public static void registerCallbacks(long windowHandle) {
        // Callback de Teclado
        glfwSetKeyCallback(windowHandle, (window, key, scancode, action, mods) -> {
            if (key < 0 || key > GLFW_KEY_LAST) return;

            if (action == GLFW_PRESS) {
                keys[key] = true;
                keysJustPressed[key] = true;
            } else if (action == GLFW_RELEASE) {
                keys[key] = false;
                keysJustReleased[key] = true;
            }
        });

        // Callback de Posición del Ratón
        glfwSetCursorPosCallback(windowHandle, (window, xpos, ypos) -> {
            float currentX = (float) xpos;
            float currentY = (float) ypos;

            if (isFirstMouse) {
                mouseX = currentX;
                mouseY = currentY;
                isFirstMouse = false;
            }

            mouseDeltaX += currentX - mouseX;
            mouseDeltaY += currentY - mouseY;
            mouseX = currentX;
            mouseY = currentY;
        });

        // Callback de Botones del Ratón
        glfwSetMouseButtonCallback(windowHandle, (window, button, action, mods) -> {
            if (button < 0 || button > GLFW_MOUSE_BUTTON_LAST) return;

            if (action == GLFW_PRESS) {
                mouseButtons[button] = true;
                mouseButtonsJustPressed[button] = true;
            } else if (action == GLFW_RELEASE) {
                mouseButtons[button] = false;
            }
        });

        // Callback de Scroll (Rueda del ratón)
        glfwSetScrollCallback(windowHandle, (window, xoffset, yoffset) -> {
            scrollX = (float) xoffset;
            scrollY = (float) yoffset;
        });
    }

    // Se llama obligatoriamente al FINAL de cada frame en Engine.loop()
    public static void endFrame() {
        java.util.Arrays.fill(keysJustPressed, false);
        java.util.Arrays.fill(keysJustReleased, false);
        java.util.Arrays.fill(mouseButtonsJustPressed, false);

        mouseDeltaX = 0;
        mouseDeltaY = 0;
        scrollX = 0;
        scrollY = 0;
    }

    public static boolean isViewportFocused() {
        return isViewportFocused;
    }

    // Control de Foco desde el Editor (ej. ImGui)
    public static void setViewportFocused(boolean focused) {
        isViewportFocused = focused;
    }

    // --- MÉTODOS DE CONSULTA (TECLADO) ---

    public static boolean isKeyDown(int keyCode) {
        return isViewportFocused && keyCode >= 0 && keyCode <= GLFW_KEY_LAST && keys[keyCode];
    }

    public static boolean isKeyPressed(int keyCode) {
        return isViewportFocused && keyCode >= 0 && keyCode <= GLFW_KEY_LAST && keysJustPressed[keyCode];
    }

    public static boolean isKeyReleased(int keyCode) {
        return isViewportFocused && keyCode >= 0 && keyCode <= GLFW_KEY_LAST && keysJustReleased[keyCode];
    }

    // --- MÉTODOS DE CONSULTA (RATÓN) ---

    public static boolean isMouseButtonDown(int button) {
        return isViewportFocused && button >= 0 && button <= GLFW_MOUSE_BUTTON_LAST && mouseButtons[button];
    }

    public static boolean isMouseButtonPressed(int button) {
        return isViewportFocused && button >= 0 && button <= GLFW_MOUSE_BUTTON_LAST && mouseButtonsJustPressed[button];
    }

    public static float getMouseX() {
        return mouseX;
    }

    public static float getMouseY() {
        return mouseY;
    }

    public static float getMouseDeltaX() {
        return isViewportFocused ? mouseDeltaX : 0;
    }

    public static float getMouseDeltaY() {
        return isViewportFocused ? mouseDeltaY : 0;
    }

    public static float getScrollX() {
        return isViewportFocused ? scrollX : 0;
    }

    public static float getScrollY() {
        return isViewportFocused ? scrollY : 0;
    }
}