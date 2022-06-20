package org.kie.pmml.models.drools.executor;

import org.kie.api.pmml.PMML4Result;
import org.kie.pmml.api.runtime.PMMLContext;
import org.kie.pmml.models.drools.commons.model.KiePMMLDroolsModel;
import org.kie.pmml.runtime.core.executor.PMMLModelEvaluator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static org.kie.pmml.runtime.core.utils.Converter.getUnwrappedParametersMap;

public abstract class DroolsModelEvaluator implements PMMLModelEvaluator<KiePMMLDroolsModel> {

    private static final Logger logger = LoggerFactory.getLogger(DroolsModelEvaluator.class.getName());

    @Override
    public PMML4Result evaluate(final KiePMMLDroolsModel model, final PMMLContext pmmlContext) {
        final Map<String, Object> requestData = getUnwrappedParametersMap(pmmlContext.getRequestData().getMappedRequestParams());
        return (PMML4Result) model.evaluate(requestData, pmmlContext);
    }

}
