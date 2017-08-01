/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
