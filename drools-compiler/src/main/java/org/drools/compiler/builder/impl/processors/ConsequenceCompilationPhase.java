package org.drools.compiler.builder.impl.processors;

import org.drools.compiler.builder.impl.PackageRegistryCompiler;
import org.drools.compiler.rule.builder.dialect.DialectError;
import org.kie.internal.builder.KnowledgeBuilderResult;

import java.util.ArrayList;
import java.util.Collection;

public class ConsequenceCompilationPhase implements CompilationPhase {
    private PackageRegistryCompiler packageRegistryCompiler;
    private Collection<KnowledgeBuilderResult> results = new ArrayList<>();

    public ConsequenceCompilationPhase(PackageRegistryCompiler packageRegistryCompiler) {
        this.packageRegistryCompiler = packageRegistryCompiler;
    }

    @Override
    public void process() {
        this.packageRegistryCompiler.compileAll();
        try {
            this.packageRegistryCompiler.reloadAll();
        } catch (Exception e) {
            results.add(new DialectError(null, "Unable to wire compiled classes, probably related to compilation failures:" + e.getMessage()));
        }
        results.addAll(this.packageRegistryCompiler.getResults());
    }

    @Override
    public Collection<? extends KnowledgeBuilderResult> getResults() {
        return results;
    }
}
