package org.drools.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.stream.Collectors;

public class RemoveCommentsMain {

    private static final Logger LOGGER = LoggerFactory.getLogger(RemoveCommentsMain.class);

    public static void main(String... args) {
        final String ignoreMissingFilesArgument = args[0];
        final boolean ignoreMissingFiles = Boolean.parseBoolean(ignoreMissingFilesArgument);
        for (int i = 0; i < args.length; i++) {
            // If the ignoreMissingFiles argument is defined, skip it in this iteration.
            if ((ignoreMissingFiles || "false".equalsIgnoreCase(ignoreMissingFilesArgument)) && i == 0) {
                continue;
            } else {
                try {
                    final String fileName = args[i];
                    final String result = removeComments(fileName, ignoreMissingFiles);
                    if (result != null) {
                        Files.write(Path.of(fileName), result.getBytes());
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    static String removeComments(String fileName, final boolean ignoreMissingFiles) {
        try (var lines = Files.lines(Path.of(fileName))) {
            return lines.filter(line -> !line.startsWith("#")).collect(Collectors.joining("\n"));
        } catch (IOException e) {
            // Ignore non-existant files.
            if (ignoreMissingFiles && e instanceof NoSuchFileException) {
                LOGGER.info("Ignoring file that doesn't exist: " + fileName);
                return null;
            } else {
                throw new RuntimeException(e);
            }
        }
    }

}
