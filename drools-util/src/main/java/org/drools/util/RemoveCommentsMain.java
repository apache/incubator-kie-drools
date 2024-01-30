package org.drools.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.stream.Collectors;

public class RemoveCommentsMain {

    public static void main(String... args) {
        for (String fileName : args) {
            try {
                final String result = removeComments(fileName);
                if (result != null) {
                    Files.write(Path.of(fileName), result.getBytes());
                }
            } catch (IOException e) {
                // Ignore non-existant files.
                if (!(e instanceof NoSuchFileException)) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    static String removeComments(String fileName) {
        try (var lines = Files.lines(Path.of(fileName))) {
            return lines.filter(line -> !line.startsWith("#")).collect(Collectors.joining("\n"));
        } catch (IOException e) {
            // Ignore non-existant files.
            if (!(e instanceof NoSuchFileException)) {
                throw new RuntimeException(e);
            }
            return null;
        }
    }

}
