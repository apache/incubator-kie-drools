package org.kie.efesto.runtimemanager.core.model;

import java.util.Map;

import org.kie.efesto.common.api.model.GeneratedResources;
import org.kie.efesto.runtimemanager.api.model.EfestoRuntimeContext;
import org.kie.memorycompiler.KieMemoryCompiler;

public class EfestoRuntimeContextUtils {

    private EfestoRuntimeContextUtils() {
    }

    public static EfestoRuntimeContext buildWithParentClassLoader(ClassLoader parentClassLoader) {
        return new EfestoRuntimeContextImpl(new KieMemoryCompiler.MemoryCompilerClassLoader(parentClassLoader));
    }

    public static EfestoRuntimeContext buildWithParentClassLoader(ClassLoader parentClassLoader, Map<String, GeneratedResources> generatedResourcesMap) {
        return new EfestoRuntimeContextImpl(new KieMemoryCompiler.MemoryCompilerClassLoader(parentClassLoader), generatedResourcesMap);
    }

}
