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

package org.jbpm.serverless.workflow.api;

public interface WorkflowValidator {

    WorkflowValidator setWorkflowManager(WorkflowManager workflowManager);

    void setJson(String json);

    void setYaml(String yaml);

    boolean isValid();

    void setEnabled(boolean enabled);

    void setSchemaValidationEnabled(boolean schemaValidationEnabled);

    void setStrictValidationEnabled(boolean strictValidationEnabled);

    void reset();
}