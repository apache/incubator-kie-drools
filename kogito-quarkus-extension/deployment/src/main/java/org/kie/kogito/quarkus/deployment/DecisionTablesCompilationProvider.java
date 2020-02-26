package org.kie.kogito.quarkus.deployment;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.kie.api.io.ResourceType;
import org.kie.kogito.codegen.ApplicationGenerator;
import org.kie.kogito.codegen.Generator;
import org.kie.kogito.codegen.rules.IncrementalRuleCodegen;

public class DecisionTablesCompilationProvider extends KogitoCompilationProvider {

    private static final Set<String> MANAGED_EXTESIONS = Collections.unmodifiableSet( new HashSet<>( Arrays.asList( ".xls", ".xlsx", ".csv" ) ) );

    @Override
    public Set<String> handledExtensions() {
        return MANAGED_EXTESIONS;
    }

    @Override
    protected Generator addGenerator(ApplicationGenerator appGen, Set<File> filesToCompile, Context context)
            throws IOException {
        Collection<File> files = PackageWalker.getAllSiblings(filesToCompile);
        return appGen.withGenerator(
                IncrementalRuleCodegen.ofFiles(
                        files,
                        ResourceType.DTABLE))
                .withClassLoader(Thread.currentThread().getContextClassLoader())
                .withHotReloadMode();
    }
}
