package  org.kie.pmml.models.scorecard.evaluator;

import java.util.Map;

import org.kie.api.pmml.PMML4Result;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.api.runtime.PMMLRuntimeContext;
import org.kie.pmml.evaluator.core.executor.PMMLModelEvaluator;
import org.kie.pmml.models.scorecard.model.KiePMMLScorecardModel;

import static org.kie.pmml.api.enums.ResultCode.OK;
import static org.kie.pmml.evaluator.core.utils.Converter.getUnwrappedParametersMap;

/**
 * Default <code>PMMLModelExecutor</code> for <b>Scorecard</b>
 */
public class PMMLScorecardModelEvaluator implements PMMLModelEvaluator<KiePMMLScorecardModel> {

    @Override
    public PMML_MODEL getPMMLModelType(){
        return PMML_MODEL.SCORECARD_MODEL;
    }

    @Override
    public PMML4Result evaluate(final KiePMMLScorecardModel model,
                                final PMMLRuntimeContext pmmlContext) {
        PMML4Result toReturn = new PMML4Result();
        String targetField = model.getTargetField();
        final Map<String, Object> requestData =
                getUnwrappedParametersMap(pmmlContext.getRequestData().getMappedRequestParams());
        Object result = model.evaluate(requestData, pmmlContext);
        toReturn.addResultVariable(targetField, result);
        toReturn.setResultObjectName(targetField);
        toReturn.setResultCode(OK.getName());
        return toReturn;
    }
}
