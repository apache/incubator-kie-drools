package org.kie.pmml.api.runtime;

import java.util.List;
import java.util.Optional;

import org.kie.api.pmml.PMML4Result;
import org.kie.pmml.api.models.PMMLModel;

public interface PMMLRuntime {

    /**
     * Evaluate the model, given the context
     * @param modelName the name of the model to evaluate
     * @param context the context with all the input variables
     * @return the result of the evaluation
     */
    PMML4Result evaluate(final String modelName, final PMMLRuntimeContext context);

    /**
     * Returns a list of all models available to this runtime
     * @return the list of available models. An empty list in
     * case no model is available.
     */
    List<PMMLModel> getPMMLModels(final PMMLRuntimeContext context);

    /**
     * Returns the model registered with the given model name.
     * @param modelName the name of the model
     * @return the corresponding an <code>Optional</code> with
     * the <code>PMMLModel</code> retrieved, or an <b>empty</b> one if none
     * is registered with the given name.
     */
    Optional<PMMLModel> getPMMLModel(final String fileName, final String modelName, PMMLRuntimeContext context);

}
