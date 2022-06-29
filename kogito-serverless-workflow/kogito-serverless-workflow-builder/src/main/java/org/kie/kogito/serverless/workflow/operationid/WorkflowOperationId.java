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

import java.net.URI;

public class WorkflowOperationId {
    private final URI uri;
    private final String operation;
    private final String fileName;
    private final String packageName;
    private final String service;

    WorkflowOperationId(URI uri, String operation, String service, String fileName, String packageName) {
        this.uri = uri;
        this.operation = operation;
        this.service = service;
        this.fileName = fileName;
        this.packageName = packageName;
    }

    public URI getUri() {
        return uri;
    }

    public String getOperation() {
        return operation;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getFileName() {
        return fileName;
    }

    public String getService() {
        return service;
    }

    @Override
    public String toString() {
        return "WorkflowOperationId [uri=" + uri + ", operation=" + operation + ", fileName=" + fileName + ", packageName=" + packageName + ", service=" + service + "]";
    }
}
