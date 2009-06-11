package org.drools.runtime.rule;


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
    void execute(Object object);

    /**
     * Execute a StatelessKnowledSession, iterate the Iterable inserting each of it's elements. If you have an array, use the Arrays.asList(...) method
     * to make that array Iterable.
     * @param objects
     */
    void execute(Iterable objects);

}
