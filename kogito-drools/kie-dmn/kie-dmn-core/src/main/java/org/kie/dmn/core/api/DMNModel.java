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

package org.kie.dmn.core.api;

import org.kie.dmn.core.ast.DecisionNode;
import org.kie.dmn.core.ast.InputDataNode;
import org.kie.dmn.core.ast.ItemDefNode;
import org.kie.dmn.feel.model.v1_1.Definitions;

import javax.xml.namespace.QName;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface DMNModel {

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

    Set<InputDataNode> getRequiredInputsForDecisionId( String decisionName );

    ItemDefNode getItemDefinitionById(String id);

    ItemDefNode getItemDefinitionByName(String name);

    Set<ItemDefNode> getItemDefinitions();

    DMNType resolveType(QName ref);

    List<DMNMessage> getMessages();

    List<DMNMessage> getMessages(DMNMessage.Severity... sevs);

    boolean hasErrors();
}
