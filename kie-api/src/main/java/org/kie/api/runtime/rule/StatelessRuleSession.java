package org.kie.api.runtime.rule;


/**
 * This interface is used as part of the {@link org.kie.api.runtime.StatelessKieSession}, which is the interface
 * returned from the {@link org.kie.api.KieBase}.
 *
 * Please see {@link org.kie.api.runtime.StatelessKieSession} for more details on how to use this api.
 *
 * @see org.kie.api.runtime.StatelessKieSession
 */
public interface StatelessRuleSession {

    /**
     * Execute a StatelessKieSession inserting just a single object. If a collection (or any other Iterable) or an array is used here, it will be inserted as-is,
     * It will not be iterated and its internal elements inserted.
     *
     * @param object
     */
    void execute(Object object);

    /**
     * Execute a StatelessKieSession, iterate the Iterable inserting each of its elements. If you have an array, use the Arrays.asList(...) method
     * to make that array Iterable.
     * @param objects
     */
    void execute(Iterable objects);

}
