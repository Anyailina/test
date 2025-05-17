package org.annill.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Stream;

public class FileUtils {

    public static List<Path> searchFile(String path, String endWith) throws IOException {
        try (Stream<Path> paths = Files.walk(Path.of(path))) {
            return paths.filter(Files::isRegularFile)
                .filter(p -> p.toString().endsWith(endWith))
                .toList();
        }
    }

    public static void writeToFile(Path path, String content) throws IOException {
        if (!Files.exists(path)) {
            Files.createDirectories(path.getParent());
            Files.createFile(path);
        }
        Files.writeString(path, content + "\n", StandardOpenOption.APPEND);
    }
}
