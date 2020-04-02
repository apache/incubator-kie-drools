/*
 *
 *   Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */
package org.jbpm.serverless.workflow.spi;

import java.util.Iterator;
import java.util.ServiceLoader;

import org.jbpm.serverless.workflow.api.WorkflowPropertySource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkflowPropertySourceProvider {

    private WorkflowPropertySource workflowPropertySource;

    private static Logger logger = LoggerFactory.getLogger(WorkflowPropertySourceProvider.class);

    public WorkflowPropertySourceProvider() {
        ServiceLoader<WorkflowPropertySource> foundPropertyContext = ServiceLoader.load(WorkflowPropertySource.class);
        Iterator<WorkflowPropertySource> it = foundPropertyContext.iterator();
        if (it.hasNext()) {
            workflowPropertySource = it.next();
            logger.info("Found property source: {}", workflowPropertySource);
        }
    }

    private static class LazyHolder {

        static final WorkflowPropertySourceProvider INSTANCE = new WorkflowPropertySourceProvider();
    }

    public static WorkflowPropertySourceProvider getInstance() {
        return WorkflowPropertySourceProvider.LazyHolder.INSTANCE;
    }

    public WorkflowPropertySource get() {
        return workflowPropertySource;
    }
}