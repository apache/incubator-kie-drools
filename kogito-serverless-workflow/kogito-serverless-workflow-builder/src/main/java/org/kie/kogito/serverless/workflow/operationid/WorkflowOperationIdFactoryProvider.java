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
package org.kie.kogito.serverless.workflow.operationid;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;

public class WorkflowOperationIdFactoryProvider {

    /**
     * (string) strategy for generating the configuration key of open API specifications; possible values are:
     * file_name: uses the last element of the spec uri
     * full_uri: uses the full path of the uri
     * spec_title: uses the spec title
     * function_name: uses the function name
     *
     */
    public static final String PROPERTY_NAME = "kogito.sw.operationIdStrategy";

    private static final Map<String, WorkflowOperationIdFactory> operationIds = new HashMap<>();

    static {
        for (WorkflowOperationIdFactory factory : ServiceLoader.load(WorkflowOperationIdFactory.class)) {
            for (String propName : factory.propertyValues()) {
                operationIds.put(propName, factory);
            }
        }
    }

    public static WorkflowOperationIdFactory getFactory(Optional<String> propValue) {
        Optional<WorkflowOperationIdFactory> factory = propValue.map(String::toUpperCase).map(operationIds::get);
        return factory.orElse(operationIds.get(FileNameWorkflowOperationIdFactory.FILE_PROP_VALUE));
    }

    private WorkflowOperationIdFactoryProvider() {
    }
}
