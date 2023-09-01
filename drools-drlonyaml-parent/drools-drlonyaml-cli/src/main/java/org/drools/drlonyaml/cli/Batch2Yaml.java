package org.drools.drlonyaml.cli;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

/**
 * Note: beyond different annotations, Parameters and Options are managed per subcommand,
 * in order to have them listed after the specific subcommand on the CLI.
 */
@Command(name="batch2yaml", description="Converts all .drl files to YAML from the given directory, recursively. Converted files will get postfixed with .yml in their names.")
public class Batch2Yaml implements Callable<Integer> {
    private static final Logger LOG = LoggerFactory.getLogger(Batch2Yaml.class);

    @Parameters(index = "0", paramLabel = "INPUT_DIR", description = "The directory containing .yml files; the directory is walked recursively.")
    private File inputDir;
    
    @Override
    public Integer call() throws Exception {
        inputDir.toPath();
        try (Stream<Path> walk = Files.walk(inputDir.toPath().toAbsolutePath()) ) {
            walk.filter(Files::isRegularFile)
                .filter(p -> p.getFileName().toString().endsWith(".drl"))
                .forEach(p -> convertFile(p.toAbsolutePath()));
        }
        return 0;
    }
    
    public static void convertFile(Path drlFile) {
        try {
            String drlTxt = Files.readAllLines(drlFile).stream().collect(Collectors.joining("\n"));
            Path to = Path.of(drlFile.toString() + ".yml");
            LOG.info("writing to: {}", to);
            String fileContent = "# Automatically generated from: " + drlFile.toFile().getName().toString() + "\n" + Drl2Yaml.drl2yaml(drlTxt);
            Files.writeString(to, fileContent, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
