package org.kie.internal.task.query;

import org.kie.internal.runtime.manager.audit.query.AuditDateDeleteBuilder;


public interface AuditTaskDeleteBuilder extends AuditDateDeleteBuilder<AuditTaskDeleteBuilder> {

    /**
     * Specify one or more deployment ids to use as a criteria.
     * @param deploymentId one or more string deployment ids
     * @return The current query builder instance
     */
    public AuditTaskDeleteBuilder deploymentId(String... deploymentId);



}
