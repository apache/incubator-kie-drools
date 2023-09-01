package org.kie.pmml.evaluator.core.testingutils;

import org.kie.api.pmml.PMML4Result;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.api.runtime.PMMLRuntimeContext;
import org.kie.pmml.commons.testingutility.KiePMMLTestingModel;
import org.kie.pmml.evaluator.core.executor.PMMLModelEvaluator;

import static org.kie.pmml.api.enums.ResultCode.OK;

public class PMMLTestingModelEvaluator implements PMMLModelEvaluator<KiePMMLTestingModel> {
    @Override
    public PMML_MODEL getPMMLModelType() {
        return PMML_MODEL.TEST_MODEL;
    }

    @Override
    public PMML4Result evaluate(KiePMMLTestingModel model, PMMLRuntimeContext context) {
        PMML4Result toReturn = new PMML4Result(context.getRequestData().getCorrelationId());
        toReturn.setResultCode(OK.getName());
        return toReturn;
    }
}
