/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.core.api.event;

import org.kie.dmn.core.ast.BusinessKnowledgeModelNode;
import org.kie.dmn.core.ast.DecisionNode;
import org.kie.dmn.core.impl.DMNResultImpl;

import java.util.List;

public interface InternalDMNRuntimeEventManager extends DMNRuntimeEventManager {

    void fireBeforeEvaluateDecision(DecisionNode decision, DMNResultImpl result);

    void fireAfterEvaluateDecision(DecisionNode decision, DMNResultImpl result);

    void fireBeforeEvaluateBKM(BusinessKnowledgeModelNode bkm, DMNResultImpl result);

    void fireAfterEvaluateBKM(BusinessKnowledgeModelNode bkm, DMNResultImpl result);

    void fireBeforeEvaluateDecisionTable(String nodeName, String dtName, DMNResultImpl result);

    void fireAfterEvaluateDecisionTable(String nodeName, String dtName, DMNResultImpl result, List<Integer> matches, List<Integer> fired );
}
