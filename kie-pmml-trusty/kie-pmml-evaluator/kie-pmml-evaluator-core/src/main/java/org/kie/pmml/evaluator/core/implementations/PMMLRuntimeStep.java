package org.kie.pmml.evaluator.core.implementations;

import org.kie.pmml.api.enums.PMML_STEP;

/**
 * <code>PMMLStep</code>> common to all models, i.e. to overall execution.
 */
public class PMMLRuntimeStep extends AbstractPMMLStep {

    private static final long serialVersionUID = -881985972308818180L;
    private final PMML_STEP pmmlStep;

    public PMMLRuntimeStep(PMML_STEP pmmlStep) {
        this.pmmlStep = pmmlStep;
    }

    public PMML_STEP getPmmlStep() {
        return pmmlStep;
    }
}
