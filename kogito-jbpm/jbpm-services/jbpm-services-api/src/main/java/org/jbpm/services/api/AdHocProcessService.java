package org.jbpm.services.api;

import org.kie.internal.process.CorrelationKey;

import java.util.Map;

/**
 * Created by salaboy on 06/05/15.
 */
public interface AdHocProcessService {

    /**
     * Starts a process with a map of variables
     *
     * @param deploymentId deployment information for the process's kjar
     * @param processId The process's identifier
     * @param correlationKey correlation key to be assigned to process instance - must be unique
     * @param params process variables
     * @return process instance identifier
     * @throws RuntimeException in case of encountered errors
     * @throws DeploymentNotFoundException in case deployment with given deployment id does not exist or is not active
     */
    Long startProcess(String deploymentId, String processId, CorrelationKey correlationKey, Map<String, Object> params, Long parentProcessInstanceId);
}
