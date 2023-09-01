package org.kie.internal.runtime.manager.audit.query;

import org.kie.internal.query.ParametrizedQueryBuilder;
import org.kie.internal.query.ProcessInstanceIdQueryBuilder;

/**
 * This interface defines methods that are used by all of the Audit
 * {@link ParametrizedQueryBuilder} implementations.
 *
 * @param <T> The {@link ParametrizedQueryBuilder} implementation type
 * @param <R> The entity type on which is being queried
 */
public interface ProcessIdQueryBuilder<T,R> extends ProcessInstanceIdQueryBuilder<T,R> {

    /**
     * Specify one or more process (definition) id's as criteria in the query
     * @param processId one or more process ids
     * @return The current query builder instance
     */
    public T processId(String... processId);

}
