package  org.kie.pmml.models.tree.evaluator;

import java.util.Map;

import org.kie.api.pmml.PMML4Result;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.api.runtime.PMMLRuntimeContext;
import org.kie.pmml.evaluator.core.executor.PMMLModelEvaluator;
import org.kie.pmml.models.tree.model.KiePMMLTreeModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.pmml.api.enums.ResultCode.OK;
import static org.kie.pmml.evaluator.core.utils.Converter.getUnwrappedParametersMap;

/**
 * Default <code>PMMLModelExecutor</code> for <b>Tree</b>
 */
public class PMMLTreeModelEvaluator implements PMMLModelEvaluator<KiePMMLTreeModel> {

    private static final Logger logger = LoggerFactory.getLogger(PMMLTreeModelEvaluator.class);

    @Override
    public PMML_MODEL getPMMLModelType(){
        return PMML_MODEL.TREE_MODEL;
    }

    @Override
    public PMML4Result evaluate(final KiePMMLTreeModel model,
                                final PMMLRuntimeContext pmmlContext) {
        logger.trace("evaluate {} {}", model, pmmlContext);
        final Map<String, Object> requestData =
                getUnwrappedParametersMap(pmmlContext.getRequestData().getMappedRequestParams());
        PMML4Result toReturn = new PMML4Result();
        String targetField = model.getTargetField();
        Object result = model.evaluate(requestData, pmmlContext);
        toReturn.addResultVariable(targetField, result);
        toReturn.setResultObjectName(targetField);
        toReturn.setResultCode(OK.getName());
        return toReturn;
    }
}
