package org.kie.pmml.evaluator.utils;

import java.io.File;

import org.kie.efesto.compilationmanager.api.model.EfestoFileResource;
import org.kie.efesto.compilationmanager.api.model.EfestoResource;
import org.kie.efesto.compilationmanager.api.service.CompilationManager;
import org.kie.efesto.compilationmanager.api.utils.SPIUtils;
import org.kie.memorycompiler.KieMemoryCompiler;
import org.kie.pmml.api.PMMLRuntimeFactory;
import org.kie.pmml.api.compilation.PMMLCompilationContext;
import org.kie.pmml.api.runtime.PMMLRuntime;
import org.kie.pmml.compiler.PMMLCompilationContextImpl;
import org.kie.pmml.evaluator.core.service.PMMLRuntimeInternalImpl;

import static org.drools.util.FileUtils.getFile;

/**
 * Publicly-available facade to hide internal implementation details
 */
public class PMMLRuntimeFactoryImpl implements PMMLRuntimeFactory {

    private static final CompilationManager compilationManager = SPIUtils.getCompilationManager(false).get();

    @Override
    public PMMLRuntime getPMMLRuntimeFromFile(File pmmlFile) {
        EfestoResource<File> efestoFileResource = new EfestoFileResource(pmmlFile);
        KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader =
                new KieMemoryCompiler.MemoryCompilerClassLoader(Thread.currentThread().getContextClassLoader());
        PMMLCompilationContext pmmlContext = new PMMLCompilationContextImpl(pmmlFile.getName(), memoryCompilerClassLoader);
        compilationManager.processResource(pmmlContext, efestoFileResource);
        return new PMMLRuntimeInternalImpl(pmmlContext.getGeneratedResourcesMap());
    }

    @Override
    public PMMLRuntime getPMMLRuntimeFromClasspath(String pmmlFileName) {
        File pmmlFile = getFile(pmmlFileName);
        return getPMMLRuntimeFromFile(pmmlFile);
    }


}
