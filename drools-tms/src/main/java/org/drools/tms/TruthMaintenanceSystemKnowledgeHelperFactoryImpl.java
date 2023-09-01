package org.drools.tms;

import org.drools.core.common.ReteEvaluator;
import org.drools.core.rule.consequence.KnowledgeHelper;
import org.drools.kiesession.consequence.DefaultKnowledgeHelper;
import org.drools.kiesession.factory.KnowledgeHelperFactory;

public class TruthMaintenanceSystemKnowledgeHelperFactoryImpl implements KnowledgeHelperFactory {

    @Override
    public KnowledgeHelper createKnowledgeHelper(ReteEvaluator reteEvaluator) {
        return reteEvaluator.isTMSEnabled() ? new TruthMaintenanceSystemKnowledgeHelper( reteEvaluator ) : new DefaultKnowledgeHelper( reteEvaluator );
    }
}
