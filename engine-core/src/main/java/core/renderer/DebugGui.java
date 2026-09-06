package core.renderer;

import core.Time;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiColorEditFlags;
import imgui.flag.ImGuiConfigFlags;
import imgui.flag.ImGuiDockNodeFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL30;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static imgui.ImGui.*;

public class DebugGui {
    private final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
    private final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();

    public static float[] viewportColor = new float[]{0.0f, 0.0f, 0.0f, 1.0f};
    public static float[] dirLightIntensity = new float[]{0.5f};
    public static float[] dirLightColor = new float[]{0.25f, 0.25f, 0.25f, 1.0f};
    public static float[] pointLightColor = new float[]{1.0f, 0.75f, 0.0f};
    public static float[] pointLightIntensity = new float[]{2.5f};
    public static float[] pointLightRadius = new float[]{20.0f};

    public void init(long windowId) {
        ImGui.createContext();
        ImGuiIO io = ImGui.getIO();

        io.addConfigFlags(ImGuiConfigFlags.NavEnableKeyboard);
        io.addConfigFlags(ImGuiConfigFlags.DockingEnable);
        io.addConfigFlags(ImGuiConfigFlags.NoMouseCursorChange);

        imGuiGlfw.init(windowId, true);
        imGuiGl3.init("#version 330 core");
    }

    private void updateGamepadSupport() {
        ImGuiIO io = ImGui.getIO();

        // Verificamos si el joystick 1 está presente Y tiene un mapeo válido de Gamepad
        boolean gamepadConnected = GLFW.glfwJoystickPresent(GLFW.GLFW_JOYSTICK_1)
                && GLFW.glfwJoystickIsGamepad(GLFW.GLFW_JOYSTICK_1);

        if (gamepadConnected) {
            io.addConfigFlags(ImGuiConfigFlags.NavEnableGamepad);
        } else {
            io.removeConfigFlags(ImGuiConfigFlags.NavEnableGamepad);
        }
    }

    public void beginFrame() {
        imGuiGlfw.newFrame();
        imGuiGl3.newFrame();
        updateGamepadSupport();
        ImGui.newFrame();

        dockSpaceOverViewport(0, ImGui.getMainViewport(), ImGuiDockNodeFlags.PassthruCentralNode);

        renderStats();
    }

    private void renderStats() {
        setNextWindowBgAlpha(0.66f);
        begin("Stats", ImGuiWindowFlags.NoSavedSettings | ImGuiWindowFlags.AlwaysAutoResize);
        text("GPU: " + GL30.glGetString(GL30.GL_RENDERER));
        text("FPS: " + Time.fps);
        text("Frametime: " + DateTimeFormatter.ofPattern("ss.SS").format(LocalTime.ofNanoOfDay((long) (Time.frameTime * 1000000000d))) + "ms");
        text("Time: " + DateTimeFormatter.ofPattern("HH:mm:ss.SS").format(LocalTime.ofNanoOfDay((long) (Time.time * 1000000000d))));

        newLine();
        separatorText("Light settings");
        colorEdit4("viewport color", viewportColor, ImGuiColorEditFlags.AlphaPreview | ImGuiColorEditFlags.NoInputs);
        colorEdit3("dirLight color", dirLightColor, ImGuiColorEditFlags.NoInputs);
        sliderFloat("dirLight intensity", dirLightIntensity, 0, 2.0f);
        colorEdit3("pointLight color", pointLightColor, ImGuiColorEditFlags.NoInputs);
        sliderFloat("pointLight intensity", pointLightIntensity, 0, 10.0f);
        sliderFloat("pointLight radius", pointLightRadius, 0, 100.0f);

        //        float[] color = new float[26000];
        end();
    }

    public void endFrame() {
        ImGui.render();

        imGuiGl3.renderDrawData(ImGui.getDrawData());
    }

    public void cleanup() {
        imGuiGl3.shutdown();
        imGuiGlfw.shutdown();
        ImGui.destroyContext();
    }
}
