package org.kie.pmml.commons.model;

import java.util.List;

/**
 * Interface to be implemented by <b>code-generated</b> classes to
 * retrieve <code>List&lt;KiePMMLModel&gt;</code>s
 * from kjar inside <code>PMMLAssemblerService</code>.
 */
public interface KiePMMLModelFactory {

    List<KiePMMLModel> getKiePMMLModels();
}
