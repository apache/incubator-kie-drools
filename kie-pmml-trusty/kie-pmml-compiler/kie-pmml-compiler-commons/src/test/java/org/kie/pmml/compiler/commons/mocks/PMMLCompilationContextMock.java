package org.kie.pmml.compiler.commons.mocks;

import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;

import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.compilationmanager.api.service.KieCompilerService;
import org.kie.memorycompiler.KieMemoryCompiler;
import org.kie.memorycompiler.KieMemoryCompilerException;
import org.kie.pmml.api.compilation.PMMLCompilationContext;

public class PMMLCompilationContextMock implements PMMLCompilationContext {

    private final KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader;

    public PMMLCompilationContextMock() {
        this.memoryCompilerClassLoader =
                new KieMemoryCompiler.MemoryCompilerClassLoader(Thread.currentThread().getContextClassLoader());
    }

    @Override
    public Set<ModelLocalUriId> getModelLocalUriIdsForFile() {
        return localUriIdKeySet();
    }

    @Override
    public Map<String, byte[]> compileClasses(Map<String, String> sourcesMap) {
        return KieMemoryCompiler.compileNoLoad(sourcesMap, memoryCompilerClassLoader);
    }

    @Override
    public void loadClasses(Map<String, byte[]> compiledClassesMap) {
        for (Map.Entry<String, byte[]> entry : compiledClassesMap.entrySet()) {
            memoryCompilerClassLoader.addCode(entry.getKey(), entry.getValue());
            try {
                loadClass(entry.getKey());
            } catch (ClassNotFoundException e) {
                throw new KieMemoryCompilerException(e.getMessage(), e);
            }
        }
    }

    @Override
    public Class<?> loadClass(String className) throws ClassNotFoundException {
        return memoryCompilerClassLoader.loadClass(className);
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Object get(String identifier) {
        return null;
    }

    @Override
    public void set(String identifier, Object value) {

    }

    @Override
    public void remove(String identifier) {

    }

    @Override
    public boolean has(String identifier) {
        return false;
    }

    @Override
    public ServiceLoader<KieCompilerService> getKieCompilerServiceLoader() {
        return ServiceLoader.load(KieCompilerService.class, memoryCompilerClassLoader);
    }

    @Override
    public byte[] getCode(String name) {
        return memoryCompilerClassLoader.getCode(name);
    }
}
