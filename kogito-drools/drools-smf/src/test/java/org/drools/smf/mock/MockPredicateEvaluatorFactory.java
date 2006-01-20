package org.drools.smf.mock;

import org.drools.rule.Rule;
import org.drools.smf.Configuration;
import org.drools.smf.FactoryException;
import org.drools.smf.PredicateEvaluatorFactory;
import org.drools.spi.PredicateEvaluator;
import org.drools.spi.RuleBaseContext;

public class MockPredicateEvaluatorFactory
    implements
    PredicateEvaluatorFactory {

    public PredicateEvaluator[] newPredicateEvaluator(Rule rule,
                                                      RuleBaseContext context,
                                                      Configuration config) throws FactoryException {        
        return null;
    }

}
