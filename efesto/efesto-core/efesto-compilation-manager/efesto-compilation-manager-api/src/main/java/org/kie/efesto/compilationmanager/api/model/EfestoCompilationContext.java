package org.kie.efesto.compilationmanager.api.model;

import java.nio.file.Path;
import java.util.Map;
import java.util.ServiceLoader;

import org.kie.efesto.common.api.io.IndexFile;
import org.kie.efesto.common.api.listener.EfestoListener;
import org.kie.efesto.common.api.model.EfestoContext;
import org.kie.efesto.compilationmanager.api.service.KieCompilerService;

/**
 *
 * Wrap MemoryCompilerClassLoader and convey generated classes to be used by other CompilationManager or RuntimeManager
 *
 */
public interface EfestoCompilationContext<T extends EfestoListener> extends EfestoContext<T> {

    Map<String, byte[]> compileClasses(Map<String, String> sourcesMap);

    void loadClasses(Map<String, byte[]> compiledClassesMap);
    ServiceLoader<KieCompilerService> getKieCompilerServiceLoader();

    byte[] getCode(String name);

    default Map<String, IndexFile> createIndexFiles(Path targetDirectory) {
        throw new UnsupportedOperationException();
    }
}
