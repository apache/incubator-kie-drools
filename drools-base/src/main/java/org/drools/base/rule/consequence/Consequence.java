package org.drools.base.rule.consequence;

import org.drools.base.base.ValueResolver;
import org.drools.base.rule.accessor.Invoker;

/**
 * Consequence to be fired upon successful match of a <code>Rule</code>.
 */
public interface Consequence<T extends ConsequenceContext>
    extends
        Invoker {
    
    String getName();
    
    /**
     * Execute the consequence for the supplied matching <code>Tuple</code>.
     * 
     * @param knowledgeHelper
     * @param valueResolver
     *            The working memory session.
     * @throws ConsequenceException
     *             If an error occurs while attempting to invoke the
     *             consequence.
     */
    void evaluate(T knowledgeHelper,
                  ValueResolver valueResolver) throws Exception;

}
