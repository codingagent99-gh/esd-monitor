package com.esd.util;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

public class FileFallbackUtil {

    private static final String FILE = "failed-records.log";

    public static synchronized void write(String msg) {
        try (FileWriter fw = new FileWriter(FILE, true)) {
            fw.write(LocalDateTime.now() + " " + msg + "\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static synchronized List<String> readAll() throws IOException {
        return Files.readAllLines(Paths.get(FILE));
    }

    public static synchronized void clear() throws IOException {
        Files.write(Paths.get(FILE), new byte[0]);
    }

    public static synchronized void overwrite(List<String> records)
            throws IOException {

        Files.write(Paths.get(FILE), records);
    }

}
