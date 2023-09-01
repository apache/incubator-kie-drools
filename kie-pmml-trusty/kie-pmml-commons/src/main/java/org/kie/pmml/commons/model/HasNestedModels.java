package org.kie.pmml.commons.model;

import java.util.List;

/**
 * Interface used to define if a given <code>KiePMMLModel</code> contains nested <b>KiePMMLModel</b>s
 */
public interface HasNestedModels {

    List<KiePMMLModel> getNestedModels();
}
