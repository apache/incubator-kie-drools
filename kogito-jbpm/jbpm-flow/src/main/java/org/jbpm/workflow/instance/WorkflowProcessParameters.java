/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jbpm.workflow.instance;

import java.util.function.Function;

public class WorkflowProcessParameters {

    /**
     * Allows activities to have multiple outgoing connections
     */
    public static final WorkflowProcessParameter<Boolean> WORKFLOW_PARAM_MULTIPLE_CONNECTIONS = newBooleanParameter("jbpm.enable.multi.con");
    public static final WorkflowProcessParameter<Boolean> WORKFLOW_PARAM_TRANSACTIONS = newBooleanParameter("jbpm.transactions.enable");

    public static WorkflowProcessParameter<String> newStringParameter(String name) {
        return new WorkflowProcessParameter<String>(name, Function.identity());
    }

    public static WorkflowProcessParameter<Boolean> newBooleanParameter(String name) {
        return new WorkflowProcessParameter<Boolean>(name, Boolean::parseBoolean);
    }

    public static class WorkflowProcessParameter<T> {
        private String name;
        private Function<String, T> converter;

        WorkflowProcessParameter(String name, Function<String, T> converter) {
            this.name = name;
            this.converter = converter;
        }

        public String getName() {
            return name;
        }

        public T get(org.kie.api.definition.process.Process workflowProcess) {
            return converter.apply((String) workflowProcess.getMetaData().get(name));
        }
    }
}
