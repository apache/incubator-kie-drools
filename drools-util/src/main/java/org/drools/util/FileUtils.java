package org.drools.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Utility to access files
 */
public class FileUtils {

    private FileUtils() {
        // Avoid instantiating class
    }

    /**
     * Retrieve the <code>File</code> of the given <b>file</b>
     * @param fileName
     * @return
     */
    public static File getFile(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf('.') + 1);
        File toReturn = ResourceHelper.getFileResourcesByExtension(extension)
                .stream()
                .filter(file -> file.getName().equals(fileName))
                .findFirst()
                .orElse(null);
        if (toReturn == null) {
            throw new IllegalArgumentException("Failed to find file " + fileName);
        }
        return toReturn;
    }

    /**
     * Retrieve the <code>FileInputStream</code> of the given <b>file</b>
     * @param fileName
     * @return
     * @throws IOException
     */
    public static FileInputStream getFileInputStream(String fileName) throws IOException {
        File sourceFile = getFile(fileName);
        return new FileInputStream(sourceFile);
    }

    /**
     * Retrieve the <b>content</b> of the given <b>file</b>
     * @param fileName
     * @return
     * @throws IOException
     */
    public static String getFileContent(String fileName) throws IOException {
        File file = getFile(fileName);
        Path path = file.toPath();
        Stream<String> lines = Files.lines(path);
        String toReturn = lines.collect(Collectors.joining("\n"));
        lines.close();
        return toReturn;
    }

    /**
     * @param fileName
     * @param classLoader
     * @return
     *
     */
    public static Optional<InputStream> getInputStreamFromFileNameAndClassLoader(String fileName, ClassLoader classLoader) {
        return Optional.ofNullable(classLoader.getResourceAsStream(fileName));
    }

    /**
     * delete a directory and all its content
     * @param path path to the directory to delete
     */
    public static void deleteDirectory(Path path) {
        try {
            if (Files.exists(path)) {
                try (Stream<Path> walk = Files.walk(path)) {
                    walk.sorted(Comparator.reverseOrder())
                            .map(Path::toFile)
                            .forEach(File::delete);
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
