package org.drools.runtime.rule;

import org.drools.runtime.Parameters;
import org.drools.runtime.StatelessKnowledgeSessionResults;

/**
 * This interface is used as part of the StatelessKnowledSession, which is the interface returned from the KnowledgeBase.
 * Please see StatelessKnowledSession for more details on how to use this api.
 *
 * @see org.drools.runtime.StatelessKnowledgeSession
 */
public interface StatelessRuleSession {
    
    /**
     * Execute a StatelessKnowledSession inserting just a single object. If a collection (or any other Iterable) or an array is used here, it will be inserted as-is,
     * It will not be iterated and it's internal elements inserted.
     * 
     * @param object
     */
    void executeObject(Object object);

    /**
     * Execute a StatelessKnowledSession, iterate the Iterable inserting each of it's elements. If you have an array, use the Arrays.asList(...) method
     * to make that array Iterable.
     * @param objects
     */
    void executeIterable(Iterable< ? > objects);

    /**
     * Will execute as with executeObject, but also allows the use of the Parameters class.
     * 
     * @param object
     * @param parameters
     * @return
     */
    StatelessKnowledgeSessionResults executeObjectWithParameters(Object object,
                                                                 Parameters parameters);

    /**
     * Will execute as with executeIterable, but also allows the use of the Parameters class.
     * 
     * @param objects
     * @param parameters
     * @return
     */
    StatelessKnowledgeSessionResults executeIterableWithParameters(Iterable< ? > objects,
                                                                   Parameters parameters);

    /**
     * Factory method to create and return a Parameters instance, which can be used to pass in, out, inOut parameters to an execution call.
     * @return
     */
    Parameters newParameters();
}
