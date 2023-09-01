package org.kie.pmml.compiler;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.compilationmanager.core.model.EfestoCompilationContextImpl;
import org.kie.memorycompiler.KieMemoryCompiler;
import org.kie.pmml.api.compilation.PMMLCompilationContext;
import org.kie.pmml.api.runtime.PMMLListener;

import static org.kie.efesto.common.api.identifiers.LocalUri.SLASH;
import static org.kie.pmml.commons.Constants.PMML_SUFFIX;

public class PMMLCompilationContextImpl extends EfestoCompilationContextImpl<PMMLListener> implements PMMLCompilationContext {

    public static final AtomicInteger ID_GENERATOR = new AtomicInteger(0);

    private final String name;

    private final String fileName;

    private final String fileNameNoSuffix;

    private final Map<String, Object> map = new ConcurrentHashMap<>();

    private final Set<PMMLListener> pmmlListeners = new HashSet<>();

    public PMMLCompilationContextImpl(final String fileName,
                                      final KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader) {
        super(memoryCompilerClassLoader);
        name = "Context_" + ID_GENERATOR.incrementAndGet();
        if (!fileName.endsWith(PMML_SUFFIX)) {
            this.fileName = fileName + PMML_SUFFIX;
        } else {
            this.fileName = fileName;
        }
        this.fileNameNoSuffix = this.fileName.substring(0, this.fileName.lastIndexOf('.'));
    }

    public PMMLCompilationContextImpl(final String fileName,
                                      final Set<PMMLListener> pmmlListeners,
                                      final KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader) {
        this(fileName, memoryCompilerClassLoader);
        this.pmmlListeners.addAll(pmmlListeners);
    }

    @Override
    public Set<ModelLocalUriId> getModelLocalUriIdsForFile() {
        Set<ModelLocalUriId> localUriIds = localUriIdKeySet();
        String matchingBase = SLASH + fileNameNoSuffix;
        return localUriIds.stream().filter(modelLocalUriId -> modelLocalUriId.basePath().startsWith(matchingBase)).collect(Collectors.toSet());
    }

    @Override
    public String getName() {
        return this.name;
    }


    @Override
    public Object get(String identifier) {
        if (identifier == null || identifier.equals("")) {
            return null;
        }

        Object object = null;
        if (map.containsKey(identifier)) {
            object = map.get(identifier);
        }
        return object;
    }

    @Override
    public Class<?> loadClass(String className) throws ClassNotFoundException {
        return memoryCompilerClassLoader.loadClass(className);
    }

    @Override
    public void set(String identifier, Object value) {
        map.put(identifier, value);
    }

    @Override
    public void remove(String identifier) {
        map.remove(identifier);
    }

    public boolean has(String identifier) {
        return map.containsKey(identifier);
    }
}
