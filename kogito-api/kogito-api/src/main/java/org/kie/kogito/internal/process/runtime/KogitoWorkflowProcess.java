/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.internal.process.runtime;

import java.util.List;

import org.kie.api.definition.process.Node;
import org.kie.api.definition.process.WorkflowProcess;

public interface KogitoWorkflowProcess extends WorkflowProcess {
    String PUBLIC_VISIBILITY = "Public";
    String PRIVATE_VISIBILITY = "Private";
    String NONE_VISIBILITY = "None";
    String BPMN_TYPE = "BPMN";
    String SW_TYPE = "SW";
    String RULEFLOW_TYPE = "RuleFlow";

    String getVisibility();

    List<Node> getNodesRecursively();
}
