package core;

public class Time {
    public static float deltaTime = 0.0f;
    public static float fixedDeltaTime = 1.0f / 60.0f;
    public static double time = 0.0f;
    public static int fps = 0;
    public static float frameTime = 0;

    private static int frameCounter = 0;
    private static float timeAccumulator = 0.0f;

    protected static void update() {
        frameCounter++;
        timeAccumulator += deltaTime;
        frameTime = 1000f / (fps <= 0 ? 1 : fps);

        // Cada vez que pasa 1 segundo, guardamos los FPS y reiniciamos el contador
        if (timeAccumulator >= 1.0f) {
            fps = frameCounter;
            frameCounter = 0;
            timeAccumulator -= 1.0f; // Mantiene la precisión restando 1 s en vez de poner a 0
        }
    }
}