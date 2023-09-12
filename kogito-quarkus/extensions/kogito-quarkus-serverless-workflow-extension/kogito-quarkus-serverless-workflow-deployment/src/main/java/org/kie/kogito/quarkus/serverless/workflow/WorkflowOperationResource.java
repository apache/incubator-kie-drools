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
package org.kie.kogito.quarkus.serverless.workflow;

import org.kie.kogito.serverless.workflow.io.URIContentLoader;
import org.kie.kogito.serverless.workflow.operationid.WorkflowOperationId;

public class WorkflowOperationResource {

    private final WorkflowOperationId operationId;
    private final URIContentLoader contentLoader;

    public WorkflowOperationResource(WorkflowOperationId operationId, URIContentLoader contentLoader) {
        this.operationId = operationId;
        this.contentLoader = contentLoader;
    }

    public WorkflowOperationId getOperationId() {
        return operationId;
    }

    public URIContentLoader getContentLoader() {
        return contentLoader;
    }

    @Override
    public String toString() {
        return "WorkflowOperationResource [operationId=" + operationId + ", contentLoader=" + contentLoader + "]";
    }
}
