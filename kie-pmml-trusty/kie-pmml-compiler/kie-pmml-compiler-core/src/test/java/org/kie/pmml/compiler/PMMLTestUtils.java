package org.kie.pmml.compiler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PMMLTestUtils {

    private PMMLTestUtils() {
    }

    /**
     * Collect drl files under `startPath`
     */
    public static Set<File> collectFiles(String startPath, String suffix) throws IOException {
        try (Stream<Path> paths = Files.walk(Paths.get(startPath))) {
            String dottedSuffix = suffix.startsWith(".") ? suffix : "." + suffix;
            return paths.map(Path::toFile)
                    .filter(File::isFile)
                    .filter(f -> f.getName().endsWith(dottedSuffix))
                    .collect(Collectors.toSet());
        }
    }
}
