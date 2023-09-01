package org.kie.drl.engine.compilation.model;

import org.kie.efesto.compilationmanager.core.model.EfestoCompilationContextImpl;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.memorycompiler.KieMemoryCompiler;

public class DrlCompilationContextImpl extends EfestoCompilationContextImpl implements DrlCompilationContext {

    public DrlCompilationContextImpl(KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader) {
        super(memoryCompilerClassLoader);
    }

    @Override
    public KnowledgeBuilderConfiguration newKnowledgeBuilderConfiguration() {
        return KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration(memoryCompilerClassLoader);
    }
}
