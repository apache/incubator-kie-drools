package org.kie.pmml.models.drools.executor;

import java.util.Map;

import org.kie.api.KieBase;
import org.kie.api.pmml.PMML4Result;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.evaluator.api.exceptions.KiePMMLModelException;
import org.kie.pmml.evaluator.api.executor.PMMLContext;
import org.kie.pmml.evaluator.core.executor.PMMLModelEvaluator;
import org.kie.pmml.models.drools.commons.model.KiePMMLDroolsModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.pmml.evaluator.core.utils.Converter.getUnwrappedParametersMap;

public abstract class DroolsModelEvaluator implements PMMLModelEvaluator {

    private static final Logger logger = LoggerFactory.getLogger(DroolsModelEvaluator.class.getName());

    @Override
    public PMML4Result evaluate(final KieBase knowledgeBase, KiePMMLModel model, PMMLContext pmmlContext) {
        if (!(model instanceof KiePMMLDroolsModel)) {
            throw new KiePMMLModelException("Expected a KiePMMLDroolsModel, received a " + model.getClass().getName());
        }
        final KiePMMLDroolsModel droolsModel = (KiePMMLDroolsModel) model;
        final Map<String, Object> requestData = getUnwrappedParametersMap(pmmlContext.getRequestData().getMappedRequestParams());
        return (PMML4Result) droolsModel.evaluate(knowledgeBase, requestData);
    }
}
