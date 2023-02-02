package org.optaplanner.examples.common.app;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.optaplanner.examples.common.business.ProblemFileComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class LoggingTest {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    protected static List<File> getAllFilesRecursivelyAndSorted(File dir, Predicate<File> fileFilter) {
        try (Stream<Path> paths = Files.walk(dir.toPath())) {
            return paths.map(Path::toFile)
                    .filter(fileFilter)
                    .sorted(new ProblemFileComparator())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new IllegalStateException("Failed reading directory (" + dir + ").", e);
        }
    }

}
