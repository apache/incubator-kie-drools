package org.kie.internal.runtime.manager.audit.query;

import org.kie.internal.query.ParametrizedQueryBuilder;

/**
 * This interface defines methods that are used by all of the Audit
 * {@link ParametrizedQueryBuilder} implementations.
 *
 * @param <T> The {@link ParametrizedQueryBuilder} implementation type
 * @param <R> The entity type on which is being queried
 */
public interface AuditLogQueryBuilder<T,R> extends ProcessIdQueryBuilder<T,R> {

    /**
     * Specify which field to use when ordering the results, in ascending order.
     * </p>
     * If this method is not used, the results will be ordered in ascending order
     * by the id field.
     * @param field the field by which the query results should be ordered
     * @return The current instance of this query builder
     */
    public T ascending( OrderBy field );

    /**
     * Specify which field to use when ordering the results, in descending order.
     * </p>
     * If this method is not used, the results will be ordered in ascending order
     * by the id field.
     * @param field the field by which the query results should be ordered
     * @return The current instance of this query builder
     */
    public T descending( OrderBy field );

    public static enum OrderBy {
        // order by process instance id
        processInstanceId,
        // order by process id
        processId,
    }

}
