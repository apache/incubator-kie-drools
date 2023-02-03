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

package org.kie.dmn.api.core;

import java.util.Collection;
import java.util.Set;

import org.kie.api.io.Resource;
import org.kie.dmn.api.core.ast.BusinessKnowledgeModelNode;
import org.kie.dmn.api.core.ast.DecisionNode;
import org.kie.dmn.api.core.ast.DecisionServiceNode;
import org.kie.dmn.api.core.ast.InputDataNode;
import org.kie.dmn.api.core.ast.ItemDefNode;
import org.kie.dmn.model.api.Definitions;

public interface DMNModel extends DMNMessageContainer {

    String getNamespace();

    String getName();

    Definitions getDefinitions();

    InputDataNode getInputById(String id);

    InputDataNode getInputByName(String name);

    Set<InputDataNode> getInputs();

    DecisionNode getDecisionById(String id);

    DecisionNode getDecisionByName(String name);

    Set<DecisionNode> getDecisions();

    Set<InputDataNode> getRequiredInputsForDecisionName(String decisionName );

    Set<InputDataNode> getRequiredInputsForDecisionId( String decisionId );

    BusinessKnowledgeModelNode getBusinessKnowledgeModelById(String id);

    BusinessKnowledgeModelNode getBusinessKnowledgeModelByName(String name);

    Set<BusinessKnowledgeModelNode> getBusinessKnowledgeModels();

    ItemDefNode getItemDefinitionById(String id);

    ItemDefNode getItemDefinitionByName(String name);

    Set<ItemDefNode> getItemDefinitions();

    /**
     * If the model was created from a {@link Resource}, provide the original resource; null otherwise.
     */
    Resource getResource();

    Collection<DecisionServiceNode> getDecisionServices();

}
