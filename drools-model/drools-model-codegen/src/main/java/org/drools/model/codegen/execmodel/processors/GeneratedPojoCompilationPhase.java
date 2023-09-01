package org.drools.model.codegen.execmodel.processors;

import org.drools.compiler.builder.impl.BuildResultCollector;
import org.drools.compiler.builder.impl.BuildResultCollectorImpl;
import org.drools.compiler.builder.impl.processors.CompilationPhase;
import org.drools.model.codegen.execmodel.CanonicalModelBuildContext;
import org.drools.model.codegen.execmodel.GeneratedClassWithPackage;
import org.drools.model.codegen.execmodel.PackageModelManager;
import org.kie.internal.builder.KnowledgeBuilderResult;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.drools.model.codegen.execmodel.generator.declaredtype.POJOGenerator.compileType;

public class GeneratedPojoCompilationPhase implements CompilationPhase {
    private final PackageModelManager packageModels;
    private final CanonicalModelBuildContext buildContext;
    private final BuildResultCollector results = new BuildResultCollectorImpl();
    private final ClassLoader classLoader;

    public GeneratedPojoCompilationPhase(PackageModelManager packageModels, CanonicalModelBuildContext buildContext, ClassLoader classLoader) {
        this.packageModels = packageModels;
        this.buildContext = buildContext;
        this.classLoader = classLoader;
    }

    @Override
    public void process() {

        List<GeneratedClassWithPackage> allGeneratedPojos =
                packageModels.values().stream()
                        .flatMap(p -> p.getGeneratedPOJOsSource().stream()
                                .map(c -> new GeneratedClassWithPackage(c, p.getName(), p.getImports(), p.getStaticImports())))
                        .collect( Collectors.toList());

        Map<String, Class<?>> allCompiledClasses = compileType(results, classLoader, allGeneratedPojos);
        buildContext.registerGeneratedPojos(allGeneratedPojos, allCompiledClasses);
    }

    @Override
    public Collection<? extends KnowledgeBuilderResult> getResults() {
        return results.getAllResults();
    }
}
