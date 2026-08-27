package core.renderer;

import lombok.Getter;

import static org.lwjgl.opengl.GL30.*;

public class Renderer {
    @Getter
    DebugGui debugGui = new DebugGui();
    long windowId;

    public Renderer(long windowId) {
        this.windowId = windowId;
        init();
    }

    private void init() {
        // Habilitar test de profundidad para renderizado 3D
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
//        glEnable(GL_MULTISAMPLE);

        debugGui.init(windowId);
    }

    public void beginFrame() {
        // Limpiar el buffer de color y el de profundidad en cada frame
        float[] bgColor = debugGui.getBgColor();
        glClearColor(bgColor[0], bgColor[1], bgColor[2], bgColor[3]);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // Enable wireframe mode
        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL); // Set GL_LINE to GL_FILL to disable it

        debugGui.beginFrame();
    }

    public void render() {
        
    }

    public void endFrame() {
        // Punto de extensión para postprocesado, librerías de UI (ImGui), etc.
        debugGui.endFrame();
    }

    public void cleanup() {
        // Limpieza de estados globales del renderizador si fuera necesario
        debugGui.cleanup();
    }
}
