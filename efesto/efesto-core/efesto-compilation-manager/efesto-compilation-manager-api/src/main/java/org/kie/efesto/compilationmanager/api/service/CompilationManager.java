package org.kie.efesto.compilationmanager.api.service;

import org.kie.efesto.compilationmanager.api.model.EfestoCompilationContext;
import org.kie.efesto.compilationmanager.api.model.EfestoResource;

public interface CompilationManager {

    /**
     * Process the given <code>EfestoResource</code>.
     * <code>EfestoCompilationContext</code> will be populated with generated classes
     *
     * @param context
     * @param toProcess
     */
    void processResource(EfestoCompilationContext context, EfestoResource... toProcess);

}
