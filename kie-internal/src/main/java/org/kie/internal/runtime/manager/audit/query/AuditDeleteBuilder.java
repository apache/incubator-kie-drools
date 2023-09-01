package org.kie.internal.runtime.manager.audit.query;

import org.kie.internal.query.ParametrizedQueryBuilder;
import org.kie.internal.query.ParametrizedUpdate;

/**
 * This interface defines methods that are used by all of the Audit delete query builder implementations.
 * @param <T>
 *
 */
public interface AuditDeleteBuilder<T> extends ParametrizedQueryBuilder<T> {

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

    /**
     * Specify one or more status that process instance should be in as criteria in the query.
     * It might be supported or not
     * @param statuses @see org.kie.api.runtime.process.ProcessInstance
     * @return The current query builder instance
     */
    T logBelongsToProcessInStatus(Integer... statuses);

    /**
     * Specify deployment id that process instance should be in as criteria in the query.
     * It might me supported or not
     * @param the deployment id
     * @return The current query builder instance
     */
    T logBelongsToProcessInDeployment(String deploymentId);

    /**
     * Specify the number of records to be included per transaction.
     * @param numRecords number of records (0 means all records will be processed into one single transaction)
     * @return The current query builder instance
     */
    T recordsPerTransaction(int numRecords);

    /**
     * Create the {@link ParametrizedUpdate} instance that can be used
     * to execute an update or delete of the entities that this builder is for.
     * </p>
     * Further modifications to this builder instance
     * will <em>not</em> affect the query criteria used in the {@link ParametrizedUpdate}
     * produced by this method.
     *
     * @return a {@link ParametrizedUpdate} instance that can be executed.
     */
    public ParametrizedUpdate build();

}
