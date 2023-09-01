package org.kie.pmml.compiler.service;

import java.util.List;

import org.kie.efesto.compilationmanager.api.exceptions.KieCompilerServiceException;
import org.kie.efesto.compilationmanager.api.model.EfestoCompilationOutput;
import org.kie.efesto.compilationmanager.api.model.EfestoFileResource;
import org.kie.efesto.compilationmanager.api.model.EfestoResource;
import org.kie.efesto.compilationmanager.api.service.KieCompilerService;
import org.kie.pmml.api.compilation.PMMLCompilationContext;

import static org.kie.pmml.commons.Constants.PMML_STRING;
import static org.kie.pmml.compiler.service.PMMLCompilerServicePMMLFile.getEfestoCompilationOutputPMML;

public class KieCompilerServicePMMLFile implements KieCompilerService<EfestoCompilationOutput, PMMLCompilationContext> {

    @Override
    public boolean canManageResource(EfestoResource toProcess) {
        return toProcess instanceof EfestoFileResource && ((EfestoFileResource) toProcess).getModelType().equalsIgnoreCase(PMML_STRING);
    }

    @Override
    public List<EfestoCompilationOutput> processResource(EfestoResource toProcess, PMMLCompilationContext context) {
        if (!canManageResource(toProcess)) {
            throw new KieCompilerServiceException(String.format("%s can not process %s",
                                                                this.getClass().getName(),
                                                                toProcess.getClass().getName()));
        }
        return getEfestoCompilationOutputPMML((EfestoFileResource) toProcess, context);
    }

    @Override
    public String getModelType() {
        return PMML_STRING;
    }
}
