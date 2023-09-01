package org.kie.pmml.commons;

import org.kie.efesto.compilationmanager.api.model.EfestoCompilationOutput;

/**
 * Interface used to decouple <code>PMMLCompilerService</code> from <code>KiePMMLDroolsModelWithSources</code>
 */
public interface HasRedirectOutput<T> {

    EfestoCompilationOutput getRedirectOutput();
}
