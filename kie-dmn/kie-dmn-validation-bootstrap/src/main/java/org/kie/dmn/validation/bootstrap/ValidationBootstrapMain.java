package org.kie.dmn.validation.bootstrap;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ValidationBootstrapMain {

    private static final Logger LOG = LoggerFactory.getLogger(ValidationBootstrapMain.class);

    public static void main(String[] args) throws IOException {
        LOG.info("Invoked with: {}", Arrays.asList(args));
        File kieDmnValidationBaseDir = new File(args[0]);
        if (!kieDmnValidationBaseDir.isDirectory()) {
            LOG.error("The supplied base directory is not valid: {}", kieDmnValidationBaseDir);
            LOG.error("ValidationBootstrapMain terminates without generating files");
            return;
        }
        try (Stream<String> lines = Files.lines(Paths.get(kieDmnValidationBaseDir.getAbsolutePath(), "pom.xml"))) {
            if (lines.noneMatch(l -> l.contains("<artifactId>kie-dmn-validation</artifactId>"))) {
                LOG.error("Unable to find the expected pom.xml.");
                LOG.error("ValidationBootstrapMain terminates without generating files");
                return;
            }
        }
        GenerateModel generator = new GenerateModel(kieDmnValidationBaseDir);
        generator.generate();
        LOG.info("ValidationBootstrapMain finished.");
    }
}
