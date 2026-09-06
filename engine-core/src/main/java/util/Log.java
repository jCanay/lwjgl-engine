package util;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Log {
    public static void info(String msg) {
        IO.println("\u001B[1m\u001B[34m" + DateTimeFormatter.ofPattern("hh:mm:ss").format(LocalTime.now()) + " [" + Thread.currentThread().getName() + "/INFO] " + msg + "\u001B[0m");
    }

    public static void warn(String msg) {
        IO.println("\u001B[1m\u001B[33m[" + Thread.currentThread().getName() + "/WARNING] " + msg + "\u001B[0m");
    }
}
