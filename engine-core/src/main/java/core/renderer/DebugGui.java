package core.renderer;

import core.Time;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiColorEditFlags;
import imgui.flag.ImGuiConfigFlags;
import imgui.flag.ImGuiDockNodeFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import lombok.Getter;
import org.lwjgl.glfw.GLFW;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static imgui.ImGui.*;

@Getter
public class DebugGui {
    private final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
    private final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();

    private float[] bgColor = new float[]{0.05f, 0.05f, 0.05f, 1.0f};

    public void init(long windowId) {
        ImGui.createContext();
        ImGuiIO io = ImGui.getIO();

        io.addConfigFlags(ImGuiConfigFlags.NavEnableKeyboard);
//        io.addConfigFlags(ImGuiConfigFlags.NavEnableGamepad);
        io.addConfigFlags(ImGuiConfigFlags.DockingEnable);

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

        begin("Stats");
        text("FPS: " + Time.fps);
        text("Frametime: " + DateTimeFormatter.ofPattern("ss.SS").format(LocalTime.ofNanoOfDay((long) (Time.frameTime * 1000000000d))) + "ms");
        text("Time: " + DateTimeFormatter.ofPattern("HH:mm:ss.SS").format(LocalTime.ofNanoOfDay((long) (Time.time * 1000000000d))));
//        beginGroup();
//        if (button("800x600")) {
//            Input.getFocusedWindow().setWidth(800);
//            Input.getFocusedWindow().setHeight(600);
//        }
//        if (button("1200x900")) {
//            Input.getFocusedWindow().setWidth(1200);
//            Input.getFocusedWindow().setHeight(900);
//        }
//        endGroup();
//        beginGroup();
//        if (button("854x480")) {
//            Input.getFocusedWindow().setWidth(854);
//            Input.getFocusedWindow().setHeight(480);
//        }
//        if (button("1280x720")) {
//            Input.getFocusedWindow().setWidth(1280);
//            Input.getFocusedWindow().setHeight(720);
//        }
//        endGroup();

        //        float[] color = new float[26000];
        colorEdit4("Background color", bgColor, ImGuiColorEditFlags.AlphaPreview | ImGuiColorEditFlags.NoInputs);
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
