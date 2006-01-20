package org.drools.smf;

import org.drools.rule.Rule;
import org.drools.spi.ReturnValueEvaluator;
import org.drools.spi.RuleBaseContext;

/**
 * Implementing modules must provide an return evaluator factory.
 * Return evaluators return values from expressions.
 * 
 * @author Michael Neale
 */
public interface ReturnValueEvaluatorFactory {

    ReturnValueEvaluator[] newReturnValueEvaluator(Rule rule,
                                                   RuleBaseContext context,
                                                   Configuration config) throws FactoryException;        
    
}
