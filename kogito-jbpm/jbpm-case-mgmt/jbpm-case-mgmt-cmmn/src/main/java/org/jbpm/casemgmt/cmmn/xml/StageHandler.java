/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.casemgmt.cmmn.xml;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.drools.core.xml.ExtensibleXmlParser;
import org.jbpm.casemgmt.cmmn.core.PlanItem;
import org.jbpm.compiler.xml.ProcessBuildData;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.node.DynamicNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class StageHandler extends AbstractCaseNodeHandler {

    private static final Logger logger = LoggerFactory.getLogger(StageHandler.class);

    public static final String AUTOCOMPLETE_COMPLETION_CONDITION = "autocomplete";
    public static final List<String> AUTOCOMPLETE_EXPRESSIONS = Arrays.asList(
                                                                              "getActivityInstanceAttribute(\"numberOfActiveInstances\") == 0", AUTOCOMPLETE_COMPLETION_CONDITION);

    protected Node createNode(Attributes attrs) {
        DynamicNode result = new DynamicNode();
        VariableScope variableScope = new VariableScope();
        result.addContext(variableScope);
        result.setDefaultContext(variableScope);
        return result;
    }

    @SuppressWarnings("unchecked")
    public Class generateNodeFor() {
        return DynamicNode.class;
    }

    protected void handleNode(final Node node,
                              final Element element,
                              final String uri,
                              final String localName,
                              final ExtensibleXmlParser parser) throws SAXException {
        super.handleNode(node, element, uri, localName, parser);
        DynamicNode stageNode = (DynamicNode) node;
        // by default it should not autocomplete as it's adhoc
        stageNode.setAutoComplete(false);
        stageNode.setLanguage("rule");

        ProcessBuildData buildData = (ProcessBuildData) parser.getData();
        Map<String, PlanItem> planItems = (Map<String, PlanItem>) buildData.getMetaData("PlanItems");

        PlanItem stagePlanItem = planItems.get(stageNode.getMetaData("UniqueId"));
        if (stagePlanItem != null && stagePlanItem.getEntryCriterion() != null) {
            if ("autostart".equalsIgnoreCase(stagePlanItem.getEntryCriterion().getExpression())) {
                stageNode.setMetaData("customAutoStart", "true");
            } else {
                stageNode.setActivationExpression(stagePlanItem.getEntryCriterion().getExpression());
            }
        }

        if (stagePlanItem != null && stagePlanItem.getExitCriterion() != null) {
            if (AUTOCOMPLETE_EXPRESSIONS.contains(stagePlanItem.getExitCriterion().getExpression())) {
                stageNode.setAutoComplete(true);
            } else {
                stageNode.setCompletionExpression(stagePlanItem.getExitCriterion().getExpression());
            }
        }

    }

}
