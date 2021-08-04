/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.util.process;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Map;

import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.process.CorrelationAwareProcessRuntime;
import org.kie.internal.process.CorrelationKey;

public class StartCorrelatedProcess {

    public static ProcessInstance startProcess(KieSession ksession,
                                               String processId,
                                               CorrelationKey correlationKey,
                                               Map<String, Object> parameters,
                                               String... nodeIds) {

        ProcessInstance processInstance = null;
        try {
            if (nodeIds.length != 0) {
                processInstance = ((CorrelationAwareProcessRuntime) ksession).startProcessFromNodeIds(processId, correlationKey, parameters, nodeIds);

            } else {
                processInstance = ((CorrelationAwareProcessRuntime) ksession).startProcess(processId, correlationKey, parameters);
            }
        } catch (RuntimeException e) {
            Throwable t = e.getCause();
            SQLIntegrityConstraintViolationException constraintViolationException = null;

            while ((t != null) && !(t instanceof SQLIntegrityConstraintViolationException)) {
                t = t.getCause();
                constraintViolationException = (SQLIntegrityConstraintViolationException) t;
            }
            if (constraintViolationException != null && (constraintViolationException.getSQLState() == "23000" || e.getMessage().contains("already exists"))) {
                throw new IllegalArgumentException(e.getMessage());
            } else {
                throw e;
            }

        }
        return processInstance;

    }

}
