package org.kie.pmml.compiler.commons.factories;

import java.util.List;

import org.kie.pmml.commons.model.KiePMMLModel;

/**
 * Interface to be implemented by <b>code-generated</b> classes to
 * retrieve <code>List&lt;KiePMMLModel&gt;</code>s
 * from kjar inside <code>PMMLAssemblerService</code>.
 */
public interface KiePMMLModelFactory {

    List<KiePMMLModel> getKiePMMLModels();
}
