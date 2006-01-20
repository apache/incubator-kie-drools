package org.drools.smf;

import org.drools.rule.Rule;
import org.drools.spi.PredicateEvaluator;
import org.drools.spi.RuleBaseContext;

/**
 * Implementors must provide a Predicate evalutor factory.
 * PredicateEvaluators return true or false based on an expression.
 * 
 * @author Michael Neale
 */
public interface PredicateEvaluatorFactory {

    PredicateEvaluator[] newPredicateEvaluator(Rule rule,
                             RuleBaseContext context,
                             Configuration config) throws FactoryException;    
    
}
