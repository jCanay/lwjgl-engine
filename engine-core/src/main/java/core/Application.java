package core;

import core.renderer.Renderer;

public interface Application {
    void init();

    void fixedUpdate();

    void update();

    void render(Renderer renderer);

    void cleanup();
}
