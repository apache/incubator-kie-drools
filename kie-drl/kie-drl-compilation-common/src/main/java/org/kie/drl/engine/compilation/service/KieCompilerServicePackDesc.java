package org.kie.drl.engine.compilation.service;

import org.drools.drl.ast.descr.PackageDescr;
import org.kie.efesto.compilationmanager.api.exceptions.KieCompilerServiceException;
import org.kie.efesto.compilationmanager.api.model.EfestoCompilationOutput;
import org.kie.efesto.compilationmanager.api.model.EfestoResource;
import org.kie.efesto.compilationmanager.api.model.EfestoSetResource;
import org.kie.efesto.compilationmanager.api.service.KieCompilerService;
import org.kie.drl.engine.compilation.model.DrlPackageDescrSetResource;
import org.kie.memorycompiler.KieMemoryCompiler;

import java.util.Collections;
import java.util.List;

import static org.kie.drl.engine.compilation.utils.DrlCompilerHelper.getDrlCallableClassesContainer;

public class KieCompilerServicePackDesc implements KieCompilerService {

    @Override
    public <T extends EfestoResource> boolean canManageResource(T toProcess) {
        return toProcess instanceof DrlPackageDescrSetResource || (toProcess instanceof EfestoSetResource  && ((EfestoSetResource)toProcess).getContent().iterator().next() instanceof PackageDescr);
    }

    @Override
    public <T extends EfestoResource, E extends EfestoCompilationOutput> List<E> processResource(T toProcess, KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader) {
        if (!canManageResource(toProcess)) {
            throw new KieCompilerServiceException(String.format("%s can not process %s",
                    this.getClass().getName(),
                    toProcess.getClass().getName()));
        }
        return (List<E>) Collections.singletonList(getDrlCallableClassesContainer((EfestoSetResource<PackageDescr>) toProcess, memoryCompilerClassLoader));
    }

}
