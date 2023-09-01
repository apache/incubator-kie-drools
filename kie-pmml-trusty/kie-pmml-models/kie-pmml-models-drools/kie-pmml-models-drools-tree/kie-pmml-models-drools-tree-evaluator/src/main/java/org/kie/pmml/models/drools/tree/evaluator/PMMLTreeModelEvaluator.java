package org.kie.pmml.models.drools.tree.evaluator;

import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.models.drools.executor.DroolsModelEvaluator;

/**
 * Default <code>PMMLModelExecutor</code> for <b>Tree</b>
 */
public class PMMLTreeModelEvaluator extends DroolsModelEvaluator {

    @Override
    public PMML_MODEL getPMMLModelType() {
        return PMML_MODEL.TREE_MODEL;
    }
}
