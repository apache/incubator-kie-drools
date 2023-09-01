package org.kie.drl.engine.compilation.service;

import java.util.Collections;
import java.util.List;

import org.drools.drl.ast.descr.PackageDescr;
import org.kie.drl.engine.compilation.model.DrlCompilationContext;
import org.kie.drl.engine.compilation.model.DrlPackageDescrSetResource;
import org.kie.efesto.compilationmanager.api.exceptions.KieCompilerServiceException;
import org.kie.efesto.compilationmanager.api.model.EfestoCompilationContext;
import org.kie.efesto.compilationmanager.api.model.EfestoCompilationOutput;
import org.kie.efesto.compilationmanager.api.model.EfestoResource;
import org.kie.efesto.compilationmanager.api.model.EfestoSetResource;
import org.kie.efesto.compilationmanager.api.service.KieCompilerService;
import org.kie.efesto.compilationmanager.core.model.EfestoCompilationContextImpl;

import static org.kie.drl.engine.compilation.utils.DrlCompilerHelper.pkgDescrToExecModel;

public class KieCompilerServicePackDesc implements KieCompilerService<EfestoCompilationOutput, EfestoCompilationContext> {

    @Override
    public boolean canManageResource(EfestoResource toProcess) {
        return toProcess instanceof DrlPackageDescrSetResource || (toProcess instanceof EfestoSetResource  && ((EfestoSetResource)toProcess).getContent().iterator().next() instanceof PackageDescr);
    }

    @Override
    public List<EfestoCompilationOutput> processResource(EfestoResource toProcess, EfestoCompilationContext context) {
        if (!canManageResource(toProcess)) {
            throw new KieCompilerServiceException(String.format("%s can not process %s",
                    this.getClass().getName(),
                    toProcess.getClass().getName()));
        }
        if (!(context instanceof DrlCompilationContext)) {
            context =  getDrlCompilationContext(context);
        }
        return Collections.singletonList( pkgDescrToExecModel((EfestoSetResource<PackageDescr>) toProcess, (DrlCompilationContext) context) );
    }


    private DrlCompilationContext getDrlCompilationContext(EfestoCompilationContext context) {
        if (!(context instanceof EfestoCompilationContextImpl)) {
            throw new KieCompilerServiceException("Expected an EfestoCompilationContextImpl, but got " + context.getClass().getCanonicalName());
        }
        return DrlCompilationContext.buildWithEfestoCompilationContext((EfestoCompilationContextImpl) context);
    }

    @Override
    public String getModelType() {
        return "drl";
    }
}
