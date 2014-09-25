package org.kie.internal.runtime.manager.audit.query;

import org.kie.internal.query.ParametrizedQueryBuilder;



/**
 * This interface defines methods that are used by all of the Audit 
 * {@link ParametrizedQueryBuilder} implementations.
 *
 * @param <T> The {@link ParametrizedQueryBuilder} implementation type
 */
public interface AuditQueryBuilder<T> extends ParametrizedQueryBuilder<T> {

    /**
     * Specify one or more process instance ids as criteria in the query
     * @param processInstanceId one or more a process instance ids
     * @return The current query builder instance
     */
    public T processInstanceId(long... processInstanceId);
    
    /**
     * Specify one or more process (definition) id's as criteria in the query
     * @param processId one or more process ids
     * @return The current query builder instance
     */
    public T processId(String... processId);
    
}
