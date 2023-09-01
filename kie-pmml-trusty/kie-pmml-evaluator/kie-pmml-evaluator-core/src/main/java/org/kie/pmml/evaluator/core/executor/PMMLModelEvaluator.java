package org.kie.pmml.evaluator.core.executor;

import org.kie.api.pmml.PMML4Result;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.api.exceptions.KiePMMLInternalException;
import org.kie.pmml.api.runtime.PMMLRuntimeContext;
import org.kie.pmml.commons.model.KiePMMLModel;

public interface PMMLModelEvaluator<E extends KiePMMLModel> {

    /**
     * @return the <code>PMMLModelType</code> this <code>PMMLModelExecutor</code>
     * is specific for
     */
    PMML_MODEL getPMMLModelType();

    /**
     * Evaluate the model, given the context
     * It may be <code>null</code> for testing purpose for <b>not drools-related</b> models
     * @param model the model to evaluate
     * @param context the context with all the input variables
     * @return the result of the evaluation
     * @throws KiePMMLInternalException
     */
    PMML4Result evaluate(final E model, final PMMLRuntimeContext context);
}
