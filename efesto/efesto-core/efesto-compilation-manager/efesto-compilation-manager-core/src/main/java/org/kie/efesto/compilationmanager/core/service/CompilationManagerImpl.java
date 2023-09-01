package org.kie.efesto.compilationmanager.core.service;

import org.kie.efesto.compilationmanager.api.model.EfestoCompilationContext;
import org.kie.efesto.compilationmanager.api.model.EfestoResource;
import org.kie.efesto.compilationmanager.api.service.CompilationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.efesto.compilationmanager.core.utils.CompilationManagerUtils.processResourceWithContext;

public class CompilationManagerImpl implements CompilationManager {

    private static final Logger logger = LoggerFactory.getLogger(CompilationManagerImpl.class.getName());

    @Override
    public void processResource(EfestoCompilationContext context, EfestoResource... toProcess) {
        for (EfestoResource efestoResource : toProcess) {
            processResourceWithContext(efestoResource, context);
        }
    }
}
