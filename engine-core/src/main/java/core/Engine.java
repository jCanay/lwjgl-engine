package core;

import core.renderer.Renderer;
import input.Input;
import lombok.NonNull;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class Engine {
    private Window window;
    private Renderer renderer;
    private Application app;

    public Engine(@NonNull Application app) {
        this.app = app;
    }

    public void run() {
        init();
        loop();
        cleanup();
    }

    private void init() {
        window = Window.create();
        renderer = new Renderer(window.getId());

        app.init();
    }

    private void loop() {
        double lastTime = GLFW.glfwGetTime();
        float accumulator = 0.0f;

        while (!window.shouldClose()) {
            double currentTime = GLFW.glfwGetTime();
            float frameTime = (float) (currentTime - lastTime);
            lastTime = currentTime;

            // Limitar frameTime para evitar el "Spiral of Death" si el juego se congela
            if (frameTime > 0.25f) {
                frameTime = 0.25f;
            }

            accumulator += frameTime;
            Time.deltaTime = frameTime;
            Time.time += frameTime;
            Time.update();

            // 1. Ejecutar FixedUpdate tantas veces como sea necesario según el tiempo acumulado
            while (accumulator >= Time.fixedDeltaTime) {
                app.fixedUpdate();
                accumulator -= Time.fixedDeltaTime;
            }

            // 2. Ejecutar Update general (lógica visual, controles de usuario)
            List<Window> windows = Window.windows;
            for (int i = windows.size() - 1; i >= 0; i--) {
                Window w = windows.get(i);
                if (w == window || !w.shouldClose()) continue;
                w.cleanup();
            }
            Input.update();
            app.update();

            // 3. Renderizar la escena
            renderer.beginFrame();
            app.render(renderer);
            renderer.endFrame();

            // 4. SwapBuffers y PollEvents
            for (int i = 0; i < Window.windows.size(); i++) Window.windows.get(i).swapBuffers();
            Input.endFrame();
            GLFW.glfwPollEvents();
        }
    }

    private void cleanup() {
        app.cleanup();
        while (!Window.windows.isEmpty()) {
            Window.windows.getFirst().cleanup();
        }
        GLFW.glfwTerminate();
    }
}
