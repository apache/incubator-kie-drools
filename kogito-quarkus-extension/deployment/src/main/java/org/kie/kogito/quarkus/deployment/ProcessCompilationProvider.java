package org.kie.kogito.quarkus.deployment;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.kie.kogito.codegen.ApplicationGenerator;
import org.kie.kogito.codegen.Generator;
import org.kie.kogito.codegen.process.ProcessCodegen;

import static java.util.Arrays.asList;

public class ProcessCompilationProvider extends KogitoCompilationProvider {

    @Override
    public Set<String> handledExtensions() {
        return new HashSet<>(asList(".bpmn", ".bpmn2"));
    }

    @Override
    protected Generator addGenerator(ApplicationGenerator appGen, Set<File> filesToCompile, Context context, ClassLoader cl) {
        return appGen.withGenerator(
                ProcessCodegen.ofFiles(new ArrayList<>(filesToCompile)))
                .withClassLoader(cl);
    }
}
