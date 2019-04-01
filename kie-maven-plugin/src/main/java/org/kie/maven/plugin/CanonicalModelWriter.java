package org.kie.maven.plugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.drools.compiler.compiler.io.memory.MemoryFile;
import org.drools.compiler.compiler.io.memory.MemoryFileSystem;

public class CanonicalModelWriter {

    private MemoryFileSystem mfs;
    private Collection<String> fileNames;
    private final String droolsModelCompilerOutputDirectory;
    private final Log log;

    public CanonicalModelWriter(
            MemoryFileSystem mfs,
            Collection<String> fileNames,
            String droolsModelCompilerOutputDirectory,
            Log log) {
        this.mfs = mfs;
        this.fileNames = fileNames;
        this.droolsModelCompilerOutputDirectory = droolsModelCompilerOutputDirectory;
        this.log = log;
    }

    public void write() throws MojoExecutionException {
        List<String> generatedFiles = fileNames
                .stream()
                .filter(f -> f.endsWith("java"))
                .collect(Collectors.toList());

        log.info(String.format("Found %d generated files in Canonical Model", generatedFiles.size()));

        List<MemoryFile> ff = fileNames
                .stream()
                .filter(f -> f.endsWith("java"))
                .map(mfs::getFile)
                .map(MemoryFile.class::cast)
                .collect(Collectors.toList());

        for (MemoryFile f : ff) {
            final Path newFile = Paths.get(droolsModelCompilerOutputDirectory,
                                           f.getPath().toPortableString());

            try {
                Files.deleteIfExists(newFile);
                Files.createDirectories(newFile.getParent());
                Files.copy(f.getContents(), newFile, StandardCopyOption.REPLACE_EXISTING);

                log.info("Generating " + newFile);
            } catch (IOException e) {
                e.printStackTrace();
                throw new MojoExecutionException("Unable to write file", e);
            }
        }
    }
}
