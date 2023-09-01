package org.kie.drl.engine.compilation.service;

import java.util.Collections;
import java.util.List;

import org.kie.drl.engine.compilation.model.DrlCompilationContext;
import org.kie.drl.engine.compilation.model.DrlFileSetResource;
import org.kie.efesto.compilationmanager.api.exceptions.KieCompilerServiceException;
import org.kie.efesto.compilationmanager.api.model.EfestoCompilationContext;
import org.kie.efesto.compilationmanager.api.model.EfestoCompilationOutput;
import org.kie.efesto.compilationmanager.api.model.EfestoResource;
import org.kie.efesto.compilationmanager.api.service.KieCompilerService;

import static org.kie.drl.engine.compilation.utils.DrlCompilerHelper.drlToPackageDescrs;

public class KieCompilerServiceDrl implements KieCompilerService<EfestoCompilationOutput,
        EfestoCompilationContext> {

    @Override
    public boolean canManageResource(EfestoResource toProcess) {
        return toProcess instanceof DrlFileSetResource;
    }

    @Override
    public List<EfestoCompilationOutput> processResource(EfestoResource toProcess, EfestoCompilationContext context) {
        if (!canManageResource(toProcess)) {
            throw new KieCompilerServiceException(String.format("%s can not process %s",
                                                                this.getClass().getName(),
                                                                toProcess.getClass().getName()));
        }
        if (!(context instanceof DrlCompilationContext)) {
            throw new KieCompilerServiceException("context has to be DrlCompilationContext");
        }
        return Collections.singletonList(drlToPackageDescrs((DrlFileSetResource) toProcess, (DrlCompilationContext) context));
    }

    @Override
    public String getModelType() {
        return "drl";
    }
}
