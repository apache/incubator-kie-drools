package org.kie.pmml.models.drools.executor;

import java.util.Map;

import org.kie.api.KieBase;
import org.kie.api.pmml.PMML4Result;
import org.kie.pmml.api.runtime.PMMLContext;
import org.kie.pmml.evaluator.core.executor.PMMLModelEvaluator;
import org.kie.pmml.models.drools.commons.model.KiePMMLDroolsModel;

import static org.kie.pmml.evaluator.core.utils.Converter.getUnwrappedParametersMap;

public abstract class DroolsModelEvaluator implements PMMLModelEvaluator<KiePMMLDroolsModel> {

    @Override
    public PMML4Result evaluate(final KieBase knowledgeBase, KiePMMLDroolsModel model, PMMLContext pmmlContext) {
        final Map<String, Object> requestData = getUnwrappedParametersMap(pmmlContext.getRequestData().getMappedRequestParams());
        return (PMML4Result) model.evaluate(knowledgeBase, requestData);
    }
}
