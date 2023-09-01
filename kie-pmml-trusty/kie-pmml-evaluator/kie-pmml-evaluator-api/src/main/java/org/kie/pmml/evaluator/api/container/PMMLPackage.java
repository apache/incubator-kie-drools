package org.kie.pmml.evaluator.api.container;

import java.util.Collection;
import java.util.Map;

import org.kie.api.internal.io.ResourceTypePackage;
import org.kie.pmml.commons.model.KiePMMLModel;

/**
 *
 */
public interface PMMLPackage extends ResourceTypePackage<KiePMMLModel> {

    KiePMMLModel getModelByName(String name);

    KiePMMLModel getModelByFullClassName(String fullClassName);

    Map<String, KiePMMLModel> getAllModels();

    Map<String, KiePMMLModel> getAllModelsByFullClassName();

    void addAll(Collection<KiePMMLModel> toAdd);
}
