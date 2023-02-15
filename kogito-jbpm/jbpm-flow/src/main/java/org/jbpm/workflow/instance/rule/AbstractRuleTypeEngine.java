/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.workflow.instance.rule;

import java.util.Map;
import java.util.Optional;

import org.jbpm.workflow.core.impl.NodeIoHelper;
import org.jbpm.workflow.instance.node.RuleSetNodeInstance;
import org.kie.api.runtime.KieRuntime;

public interface AbstractRuleTypeEngine {

    default Map<String, Object> getInputs(RuleSetNodeInstance rsni) {
        return NodeIoHelper.processInputs(rsni, rsni::getVariable);
    }

    default KieRuntime getKieRuntime(RuleSetNodeInstance rsni) {
        return Optional.ofNullable(rsni.getRuleSetNode().getKieRuntime()).orElse(() -> rsni.getProcessInstance().getKnowledgeRuntime()).get();
    }
}
