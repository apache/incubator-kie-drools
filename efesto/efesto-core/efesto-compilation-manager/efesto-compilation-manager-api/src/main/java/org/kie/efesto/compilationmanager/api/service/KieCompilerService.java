package org.kie.efesto.compilationmanager.api.service;

import java.util.List;

import org.kie.efesto.compilationmanager.api.model.EfestoCompilationContext;
import org.kie.efesto.compilationmanager.api.model.EfestoCompilationOutput;
import org.kie.efesto.compilationmanager.api.model.EfestoResource;

/**
 * The compilation-related interface to be implemented by engine-plugin.
 * It will be looked for with SPI, so each engine should declare that implementation inside
 * <code>src/main/resources/META-INF/services/org.kie.efesto.compilationmanager.api.service.KieCompilerService</code> file
 */
public interface KieCompilerService<E extends EfestoCompilationOutput, U extends EfestoCompilationContext> {


    boolean canManageResource(EfestoResource toProcess);

    /**
     * Produce one <code>E</code> from the given <code>T</code>
     * <p>
     * Implementation are also required to generate a "mapping" class, i.e. a class specific for the given
     * model responsible to list all the other generated ones; engine-specific runtimes will look for such
     * class to know if it can manage given resource
     *
     * @param toProcess
     * @param context
     * @return
     */
    List<E> processResource(EfestoResource toProcess, U context);

    /**
     * Return the model type that the CompilerService handles
     *
     * @return model type
     */
    String getModelType();
}
