package util;

public class Log {
    public static void info(String msg) {
        IO.println("\u001B[1m\u001B[34m[INFO] " + msg + "\u001B[0m");
    }

    public static void warn(String msg) {
        IO.println("\u001B[1m\u001B[33m[WARNING] " + msg + "\u001B[0m");
    }
}
