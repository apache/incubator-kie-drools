package org.drools.kiesession.factory;

import org.drools.core.common.ReteEvaluator;
import org.drools.core.rule.consequence.KnowledgeHelper;
import org.drools.kiesession.consequence.DefaultKnowledgeHelper;
import org.kie.api.internal.utils.KieService;

public interface KnowledgeHelperFactory extends KieService {

    KnowledgeHelper createKnowledgeHelper(ReteEvaluator reteEvaluator);

    class Holder {
        private static final KnowledgeHelperFactory INSTANCE = createInstance();

        static KnowledgeHelperFactory createInstance() {
            KnowledgeHelperFactory factory = KieService.load( KnowledgeHelperFactory.class );
            return factory != null ? factory : new KnowledgeHelperFactoryImpl();
        }
    }

    static KnowledgeHelperFactory get() {
        return KnowledgeHelperFactory.Holder.INSTANCE;
    }

    class KnowledgeHelperFactoryImpl implements KnowledgeHelperFactory {

        @Override
        public KnowledgeHelper createKnowledgeHelper(ReteEvaluator reteEvaluator) {
            return new DefaultKnowledgeHelper( reteEvaluator );
        }
    }
}
