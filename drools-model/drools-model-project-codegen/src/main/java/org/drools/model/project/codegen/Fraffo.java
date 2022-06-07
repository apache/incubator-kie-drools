package org.drools.model.project.codegen;

import org.drools.compiler.builder.impl.BuildResultCollector;
import org.drools.compiler.builder.impl.BuildResultCollectorImpl;
import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.lang.descr.CompositePackageDescr;
import org.drools.modelcompiler.tool.ExplicitCanonicalModelCompiler;

import java.util.Collection;

public class Fraffo {
    private final ExplicitCanonicalModelCompiler<KogitoPackageSources> compiler;
    private final BuildResultCollector buildResultCollector;

    public Fraffo(Collection<CompositePackageDescr> packages, KnowledgeBuilderConfigurationImpl config) {
        this.compiler = ExplicitCanonicalModelCompiler.of(packages, config, KogitoPackageSources::dumpSources);
        this.buildResultCollector = new BuildResultCollectorImpl();
    }

    public void build() {
        compiler.process();
    }

    public BuildResultCollector getBuildResults() {
        return buildResultCollector;
    }

    public Collection<KogitoPackageSources> getPackageSources() {
        return compiler.getPackageSources();
    }
}
