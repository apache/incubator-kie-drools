package org.kie.pmml.compiler.executor;

import java.io.InputStream;
import java.util.List;

import org.kie.pmml.api.compilation.PMMLCompilationContext;
import org.kie.pmml.api.exceptions.ExternalException;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.commons.model.KiePMMLModel;

/**
 * Actual implementations are required to convert a <b>PMML</b> xml to
 * to a <code>List&lt;KiePMMLModel&gt;</code>
 */
public interface PMMLCompiler {

    /**
     * Read the given <code>InputStream</code> to return a <code>List&lt;KiePMMLModel&gt;</code> following a
     * <b>kie-maven-plugin</b> invocation
     * @param packageName the package into which put all the generated classes out of the given <code>InputStream</code>
     * @param inputStream
     * @param fileName
     * @param pmmlContext Using <code>PMMLCompilationContext</code>
     * @return
     * @throws KiePMMLException if any <code>KiePMMLInternalException</code> has been thrown during execution
     * @throws ExternalException if any other kind of <code>Exception</code> has been thrown during execution
     */
    List<KiePMMLModel> getKiePMMLModelsWithSources(final String packageName, final InputStream inputStream,
                                                   final String fileName, final PMMLCompilationContext pmmlContext);
}
