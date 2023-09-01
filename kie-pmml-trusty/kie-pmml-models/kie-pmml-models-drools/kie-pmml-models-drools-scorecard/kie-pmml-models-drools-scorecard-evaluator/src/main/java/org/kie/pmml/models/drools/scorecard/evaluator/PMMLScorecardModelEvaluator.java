package org.kie.pmml.models.drools.scorecard.evaluator;

import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.models.drools.executor.DroolsModelEvaluator;

/**
 * Default <code>PMMLModelExecutor</code> for <b>Scorecard</b>
 */
public class PMMLScorecardModelEvaluator extends DroolsModelEvaluator {

    @Override
    public PMML_MODEL getPMMLModelType() {
        return PMML_MODEL.SCORECARD_MODEL;
    }
}
