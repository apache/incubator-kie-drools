package org.drools.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class FileUtilsTest {

    private static final String TEST_FILE = "TestFile.txt";
    private static final String NOT_EXISTING_FILE = "NotExisting.txt";

    @Test
    public void getFileExisting() {
        final File retrieved = FileUtils.getFile(TEST_FILE);
        assertThat(retrieved).exists();
        assertThat(retrieved.getName()).isEqualTo(TEST_FILE);
    }

    @Test
    public void getFileNotExisting() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> FileUtils.getFile(NOT_EXISTING_FILE));
    }

    @Test
    public void getFileInputStreamExisting() throws IOException {
        final FileInputStream retrieved = FileUtils.getFileInputStream(TEST_FILE);
        assertThat(retrieved).isNotNull();
        retrieved.close();
    }

    @Test
    public void getFileInputStreamNotExisting() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> FileUtils.getFileInputStream(NOT_EXISTING_FILE));
    }

    @Test
    public void getInputStreamFromFileNameExisting() {
        Optional<InputStream> retrieved = FileUtils.getInputStreamFromFileNameAndClassLoader(TEST_FILE, FileUtilsTest.class.getClassLoader());
        assertThat(retrieved).isPresent();
    }

    @Test
    public void getInputStreamFromFileNameNotExisting() {
        Optional<InputStream> retrieved = FileUtils.getInputStreamFromFileNameAndClassLoader(NOT_EXISTING_FILE, FileUtilsTest.class.getClassLoader());
        assertThat(retrieved).isNotPresent();
    }

    @Test
    public void deleteDirectory() throws IOException {
        final Path tempDirectory = Files.createTempDirectory("temp");
        final Path tempFile = Files.createTempFile(tempDirectory, "temp", "temp");
        FileUtils.deleteDirectory(tempDirectory);
        assertThat(Files.exists(tempDirectory)).isFalse();
        assertThat(Files.exists(tempFile)).isFalse();
    }
}