package org.kie.kogito.quarkus.deployment;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import io.quarkus.deployment.dev.JavaCompilationProvider;
import org.kie.kogito.codegen.GeneratedFile;
import org.kie.kogito.codegen.Generator;
import org.kie.kogito.codegen.decision.DecisionCodegen;

import static org.kie.kogito.quarkus.deployment.KogitoCompilationProvider.pathOf;

public class DMNCompilationProvider extends JavaCompilationProvider {

    @Override
    public Set<String> handledExtensions() {
        return Collections.singleton(".dmn");
    }

    @Override
    public final void compile(Set<File> filesToCompile, Context context) {
        File outputDirectory = context.getOutputDirectory();
        try {
            Generator generator = DecisionCodegen.ofPath(context.getProjectDirectory().toPath().resolve("src/main/resources"));
            Collection<GeneratedFile> generatedFiles = generator.generate();

            Set<File> generatedSourceFiles = new HashSet<>();
            for (GeneratedFile file : generatedFiles) {
                Path path = pathOf(outputDirectory.getPath(), file.relativePath());
                Files.write(path, file.contents());
                generatedSourceFiles.add(path.toFile());
            }
            super.compile(generatedSourceFiles, context);
        } catch (IOException e) {
            throw new KogitoCompilerException(e);
        }

    }
}
