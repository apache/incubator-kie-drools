package org.drools.smf.mock;

import org.drools.rule.Rule;
import org.drools.smf.Configuration;
import org.drools.smf.FactoryException;
import org.drools.smf.ReturnValueEvaluatorFactory;
import org.drools.spi.ReturnValueEvaluator;
import org.drools.spi.RuleBaseContext;

public class MockReturnValueEvaluatorFactory
    implements
    ReturnValueEvaluatorFactory {

    public ReturnValueEvaluator[] newReturnValueEvaluator(Rule rule,
                                                          RuleBaseContext context,
                                                          Configuration config) throws FactoryException {        
        return null;
    }

}
