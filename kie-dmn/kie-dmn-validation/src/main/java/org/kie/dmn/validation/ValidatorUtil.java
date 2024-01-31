/**
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
package org.kie.dmn.validation;

import java.util.List;
import java.util.stream.Collectors;

import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.model.api.Association;
import org.kie.dmn.model.api.AuthorityRequirement;
import org.kie.dmn.model.api.DMNElement;
import org.kie.dmn.model.api.DecisionTable;
import org.kie.dmn.model.api.Definitions;
import org.kie.dmn.model.api.FunctionDefinition;
import org.kie.dmn.model.api.InformationRequirement;
import org.kie.dmn.model.api.ItemDefinition;
import org.kie.dmn.model.api.KnowledgeRequirement;
import org.kie.dmn.model.api.NamedElement;
import org.kie.dmn.model.v1_5.KieDMNModelInstrumentedBase;

import javax.xml.namespace.QName;

public final class ValidatorUtil {

    public static final QName KIE_MODEL_NAME_QNAME = new QName(KieDMNModelInstrumentedBase.URI_KIE, "modelName");

    public static String rightOfHash(final String input) {
        return input.substring(input.indexOf("#") + 1);
    }

    public static String leftOfHash(final String input) {
        return input.substring(0, input.indexOf("#"));
    }

    public static ItemDefinition getRootItemDef(final ItemDefinition id) {
        ItemDefinition root = id;
        while (!(root.getParent() instanceof Definitions)) {
            root = (ItemDefinition) root.getParent();
        }
        return root;
    }

    public static String formatMessages(final List<DMNMessage> messages) {
        return messages.stream().map(Object::toString).collect( Collectors.joining( System.lineSeparator() ) );
    }

    public static boolean doesDefinitionsContainIdForDMNEdge(Definitions definitions, String id) {
        boolean result = definitions.getArtifact().stream().anyMatch(a -> (a.getId().equals(id) && a instanceof Association));
        if (result) {
            return true;
        }
        return definitions.getDrgElement()
                          .stream()
                          .flatMap(e -> e.getChildren().stream())
                          .filter(e -> (e instanceof InformationRequirement || e instanceof KnowledgeRequirement || e instanceof AuthorityRequirement))
                          .map(DMNElement.class::cast)
                          .anyMatch(e -> (e.getId().equals(id)));
    }
    
    public static String nameOrIDOfTable(DecisionTable sourceDT) {
        if (sourceDT.getOutputLabel() != null && !sourceDT.getOutputLabel().isEmpty()) {
            return sourceDT.getOutputLabel();
        } else if (sourceDT.getParent() instanceof NamedElement) { // DT is decision logic of Decision, and similar cases.
            return ((NamedElement) sourceDT.getParent()).getName();
        } else if (sourceDT.getParent() instanceof FunctionDefinition && sourceDT.getParent().getParent() instanceof NamedElement) { // DT is decision logic of BKM.
            return ((NamedElement) sourceDT.getParent().getParent()).getName();
        } else {
            return new StringBuilder("[ID: ").append(sourceDT.getId()).append("]").toString();
        }
    }

    private ValidatorUtil() {
        // It is forbidden to create new instances of util classes.
    }
}
