package org.drools.drlonyaml.cli.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.function.Consumer;

public class Utils {
    private Utils() {
        // only static utilities methods.
    }
    
    /**
     * Returns an InputStream reading the file content when provided as a parameter, otherwise from STDIN until `\\A` when null.
     */
    public static InputStream conventionInputStream(File inputFile) throws FileNotFoundException {
        if (inputFile == null) {
            try (Scanner scanner = new Scanner(System.in).useDelimiter("\\A")) {
                if (scanner.hasNext()) {
                    return new ByteArrayInputStream(scanner.next().getBytes());
                }
            }
        } else {
            if (!inputFile.exists()) {
                throw new IllegalArgumentException(inputFile + " does not exists.");
            }
            return new FileInputStream(inputFile);
        }
        throw new IllegalStateException();
    }
    
    /**
     * Returns an Consumer which writes content to the file when provided as a parameter, otherwise consumer emits to STDOUT when null.
     */
    public static Consumer<String> conventionOutputConsumer(File archive) {
        return archive != null ? new FileWriteStrategy(archive) : System.out::println;
    }
}
