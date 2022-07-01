package org.kie.drl.engine.compilation.service;

import java.util.Collections;
import java.util.List;

import org.kie.drl.engine.compilation.model.DrlFileSetResource;
import org.kie.efesto.compilationmanager.api.exceptions.KieCompilerServiceException;
import org.kie.efesto.compilationmanager.api.model.EfestoCompilationOutput;
import org.kie.efesto.compilationmanager.api.model.EfestoResource;
import org.kie.efesto.compilationmanager.api.service.KieCompilerService;
import org.kie.memorycompiler.KieMemoryCompiler;

import static org.kie.drl.engine.compilation.utils.DrlCompilerHelper.getDrlCallableClassesContainer;

public class KieCompilerServiceDrl implements KieCompilerService {

    @Override
    public <T extends EfestoResource> boolean canManageResource(T toProcess) {
        return toProcess instanceof DrlFileSetResource;
    }

    @Override
    public <T extends EfestoResource, E extends EfestoCompilationOutput> List<E> processResource(T toProcess, KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader) {
        if (!canManageResource(toProcess)) {
            throw new KieCompilerServiceException(String.format("%s can not process %s",
                    this.getClass().getName(),
                    toProcess.getClass().getName()));
        }
        return Collections.singletonList( (E) getDrlCallableClassesContainer((DrlFileSetResource) toProcess, memoryCompilerClassLoader) );
    }

}
