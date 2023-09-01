package org.kie.pmml.models.clustering.evaluator;

import java.util.Map;

import org.kie.api.pmml.PMML4Result;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.api.runtime.PMMLRuntimeContext;
import org.kie.pmml.evaluator.core.executor.PMMLModelEvaluator;
import org.kie.pmml.models.clustering.model.KiePMMLClusteringModel;

import static org.kie.pmml.api.enums.ResultCode.OK;
import static org.kie.pmml.evaluator.core.utils.Converter.getUnwrappedParametersMap;

/**
 * Default <code>PMMLModelExecutor</code> for <b>Clustering</b>
 */
public class PMMLClusteringModelEvaluator implements PMMLModelEvaluator<KiePMMLClusteringModel> {

    @Override
    public PMML_MODEL getPMMLModelType() {
        return PMML_MODEL.CLUSTERING_MODEL;
    }

    @Override
    public PMML4Result evaluate(final KiePMMLClusteringModel model,
                                final PMMLRuntimeContext context) {
        final Map<String, Object> requestData =
                getUnwrappedParametersMap(context.getRequestData().getMappedRequestParams());

        Object result = model.evaluate(requestData, context);

        String targetField = model.getTargetField();
        PMML4Result toReturn = new PMML4Result();
        toReturn.addResultVariable(targetField, result);
        toReturn.setResultObjectName(targetField);
        toReturn.setResultCode(OK.getName());
        return toReturn;
    }
}
