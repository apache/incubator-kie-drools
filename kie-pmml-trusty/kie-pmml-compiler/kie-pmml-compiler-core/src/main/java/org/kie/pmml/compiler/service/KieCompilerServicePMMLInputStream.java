package org.kie.pmml.compiler.service;

import java.util.List;

import org.kie.efesto.compilationmanager.api.exceptions.KieCompilerServiceException;
import org.kie.efesto.compilationmanager.api.model.EfestoCompilationContext;
import org.kie.efesto.compilationmanager.api.model.EfestoCompilationOutput;
import org.kie.efesto.compilationmanager.api.model.EfestoInputStreamResource;
import org.kie.efesto.compilationmanager.api.model.EfestoResource;
import org.kie.efesto.compilationmanager.api.service.KieCompilerService;

import static org.kie.pmml.commons.Constants.PMML_STRING;
import static org.kie.pmml.compiler.service.PMMLCompilerServicePMMLInputStream.getEfestoCompilationOutputPMML;

public class KieCompilerServicePMMLInputStream implements KieCompilerService<EfestoCompilationOutput,
        EfestoCompilationContext> {

    @Override
    public boolean canManageResource(EfestoResource toProcess) {
        return toProcess instanceof EfestoInputStreamResource && ((EfestoInputStreamResource) toProcess).getModelType().equalsIgnoreCase(PMML_STRING);
    }

    @Override
    public List<EfestoCompilationOutput> processResource(EfestoResource toProcess, EfestoCompilationContext context) {
        if (!canManageResource(toProcess)) {
            throw new KieCompilerServiceException(String.format("%s can not process %s",
                                                                this.getClass().getName(),
                                                                toProcess.getClass().getName()));
        }
        return getEfestoCompilationOutputPMML((EfestoInputStreamResource) toProcess, context);
    }

    @Override
    public String getModelType() {
        return PMML_STRING;
    }
}
