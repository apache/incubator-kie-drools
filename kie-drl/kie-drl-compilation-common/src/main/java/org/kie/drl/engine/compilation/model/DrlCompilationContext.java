package org.kie.drl.engine.compilation.model;

import org.kie.efesto.compilationmanager.api.model.EfestoCompilationContext;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.kie.memorycompiler.KieMemoryCompiler;

public interface DrlCompilationContext extends EfestoCompilationContext {

    public static DrlCompilationContext buildWithParentClassLoader(ClassLoader parentClassLoader) {
        return new DrlCompilationContextImpl(new KieMemoryCompiler.MemoryCompilerClassLoader(parentClassLoader));
    }

    public static DrlCompilationContext buildWithMemoryCompilerClassLoader(KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader) {
        return new DrlCompilationContextImpl(memoryCompilerClassLoader);
    }

    KnowledgeBuilderConfiguration newKnowledgeBuilderConfiguration();
}
