package org.drools.model.codegen.execmodel.processors;

import java.util.Collection;
import java.util.function.Function;

import org.drools.compiler.builder.impl.BuildResultCollector;
import org.drools.compiler.builder.impl.BuildResultCollectorImpl;
import org.drools.compiler.builder.impl.processors.CompilationPhase;
import org.drools.model.codegen.execmodel.PackageModel;
import org.drools.model.codegen.execmodel.PackageSourceManager;
import org.kie.internal.builder.KnowledgeBuilderResult;

public class SourceCodeGenerationPhase<T> implements CompilationPhase {
    private final PackageModel pkgModel;
    private final PackageSourceManager<T> packageSources;
    private final Function<PackageModel, T> sourcesGenerator;
    private final BuildResultCollector results;

    private final boolean oneClassPerRule;

    public SourceCodeGenerationPhase(
            PackageModel pkgModel,
            PackageSourceManager<T> packageSources,
            Function<PackageModel, T> sourcesGenerator,
            boolean oneClassPerRule) {
        this.pkgModel = pkgModel;
        this.packageSources = packageSources;
        this.sourcesGenerator = sourcesGenerator;
        this.results = new BuildResultCollectorImpl();
        this.oneClassPerRule = oneClassPerRule;
    }

    @Override
    public void process() {
        pkgModel.setOneClassPerRule(oneClassPerRule);
        packageSources.put(pkgModel.getName(), sourcesGenerator.apply(pkgModel));
    }

    @Override
    public Collection<? extends KnowledgeBuilderResult> getResults() {
        return results.getAllResults();
    }
}
