package org.kie.drl.engine.compilation.model;

import org.kie.efesto.compilationmanager.api.model.EfestoCompilationContext;
import org.kie.efesto.compilationmanager.core.model.EfestoCompilationContextImpl;
import org.kie.efesto.compilationmanager.core.model.EfestoCompilationContextUtils;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.kie.memorycompiler.KieMemoryCompiler;

public interface DrlCompilationContext extends EfestoCompilationContext {

    static DrlCompilationContext buildWithParentClassLoader(ClassLoader parentClassLoader) {
        return new DrlCompilationContextImpl(new KieMemoryCompiler.MemoryCompilerClassLoader(parentClassLoader));
    }

    static DrlCompilationContext buildWithMemoryCompilerClassLoader(KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader) {
        return new DrlCompilationContextImpl(memoryCompilerClassLoader);
    }

    static DrlCompilationContext buildWithEfestoCompilationContext(EfestoCompilationContextImpl context) {
        return (DrlCompilationContext) EfestoCompilationContextUtils.buildFromContext(context, DrlCompilationContextImpl.class);
    }

    KnowledgeBuilderConfiguration newKnowledgeBuilderConfiguration();
}
