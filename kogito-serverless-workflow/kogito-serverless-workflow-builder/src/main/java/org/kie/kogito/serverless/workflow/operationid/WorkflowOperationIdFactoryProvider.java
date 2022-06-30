/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.serverless.workflow.operationid;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkflowOperationIdFactoryProvider {

    private static final Logger logger = LoggerFactory.getLogger(WorkflowOperationIdFactoryProvider.class);
    public static final String PROPERTY_NAME = "kogito.sw.operationIdStrategy";

    private static final WorkflowOperationIdFactoryType defaultType = WorkflowOperationIdFactoryType.FILE_NAME;

    public static WorkflowOperationIdFactory getFactory(Optional<String> propValue) {
        return propValue.map(WorkflowOperationIdFactoryProvider::safeValueOf).orElse(defaultType.factory());
    }

    private static WorkflowOperationIdFactory safeValueOf(String name) {
        try {
            return WorkflowOperationIdFactoryType.valueOf(name.toUpperCase()).factory();
        } catch (IllegalArgumentException ex) {
            logger.error("Wrong value for property {}. Expected values are {}. Using default ", name, WorkflowOperationIdFactoryType.values(), ex);
            return defaultType.factory();
        }
    }

    private WorkflowOperationIdFactoryProvider() {
    }
}
