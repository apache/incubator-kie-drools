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

import java.util.Map;

import org.drools.core.xml.ExtensibleXmlParser;
import org.jbpm.casemgmt.cmmn.core.PlanItem;
import org.jbpm.compiler.xml.ProcessBuildData;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.node.MilestoneNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class MilestoneHandler extends AbstractCaseNodeHandler {

    private static final Logger logger = LoggerFactory.getLogger(MilestoneHandler.class);

    protected Node createNode(Attributes attrs) {
        return new MilestoneNode();
    }

    protected void handleNode(final Node node,
                              final Element element,
                              final String uri,
                              final String localName,
                              final ExtensibleXmlParser parser) throws SAXException {
        super.handleNode(node, element, uri, localName, parser);
        MilestoneNode milestoneNode = (MilestoneNode) node;
        ProcessBuildData buildData = (ProcessBuildData) parser.getData();
        Map<String, PlanItem> planItems = (Map<String, PlanItem>) buildData.getMetaData("PlanItems");

        PlanItem milestonePlanItem = planItems.get(milestoneNode.getMetaData("UniqueId"));
        if (milestonePlanItem != null && milestonePlanItem.getExitCriterion() != null) {

            milestoneNode.setConstraint(milestonePlanItem.getExitCriterion().getExpression());
        }

    }

    @Override
    public Class<?> generateNodeFor() {
        return MilestoneNode.class;
    }

}
