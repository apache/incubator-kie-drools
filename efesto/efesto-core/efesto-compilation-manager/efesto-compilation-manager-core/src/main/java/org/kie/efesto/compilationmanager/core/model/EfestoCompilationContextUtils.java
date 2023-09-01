package org.kie.efesto.compilationmanager.core.model;

import org.kie.efesto.compilationmanager.api.exceptions.EfestoCompilationManagerException;
import org.kie.efesto.compilationmanager.api.model.EfestoCompilationContext;
import org.kie.memorycompiler.KieMemoryCompiler;

public class EfestoCompilationContextUtils {

    private EfestoCompilationContextUtils() {
    }

    public static EfestoCompilationContext buildWithParentClassLoader(ClassLoader parentClassLoader) {
        return new EfestoCompilationContextImpl(new KieMemoryCompiler.MemoryCompilerClassLoader(parentClassLoader));
    }

    public static EfestoCompilationContext buildFromContext(EfestoCompilationContextImpl original, Class<?
            extends EfestoCompilationContext> toInstantiate) {
        try {
            EfestoCompilationContext toReturn =
                    toInstantiate.getDeclaredConstructor(KieMemoryCompiler.MemoryCompilerClassLoader.class).newInstance(original.memoryCompilerClassLoader);
            toReturn.getGeneratedResourcesMap().putAll(original.getGeneratedResourcesMap());
            return toReturn;
        } catch (Exception e) {
            throw new EfestoCompilationManagerException("Failed to instantiate " + toInstantiate.getName(), e);
        }
    }
}
