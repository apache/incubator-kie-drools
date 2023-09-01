package org.kie.internal.runtime.manager.audit.query;


public interface VariableInstanceLogDeleteBuilder extends AuditDateDeleteBuilder<VariableInstanceLogDeleteBuilder>{

    /**
     * Specify one or more external ids to use as a criteria. In some cases,
     * the external id is the deployment unit id or runtime manager id.
     * @param externalId one or more string external ids
     * @return The current query builder instance
     */
    public VariableInstanceLogDeleteBuilder externalId(String... externalId);

}
