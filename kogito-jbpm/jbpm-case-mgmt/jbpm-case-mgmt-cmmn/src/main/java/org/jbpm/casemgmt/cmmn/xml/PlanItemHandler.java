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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.drools.core.xml.BaseAbstractHandler;
import org.drools.core.xml.ExtensibleXmlParser;
import org.drools.core.xml.Handler;
import org.jbpm.casemgmt.cmmn.core.PlanItem;
import org.jbpm.casemgmt.cmmn.core.Sentry;
import org.jbpm.compiler.xml.ProcessBuildData;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.workflow.core.NodeContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class PlanItemHandler extends BaseAbstractHandler implements Handler {

    private static final Logger logger = LoggerFactory.getLogger(PlanItemHandler.class);

    public PlanItemHandler() {
        if ((this.validParents == null) && (this.validPeers == null)) {
            this.validParents = new HashSet<>();
            this.validParents.add(NodeContainer.class);

            this.validPeers = new HashSet<>();
            this.validPeers.add(null);
            this.validPeers.add(PlanItem.class);
            this.validPeers.add(Sentry.class);

            this.allowNesting = false;
        }
    }

    @SuppressWarnings("unchecked")
    public Object start(final String uri,
                        final String localName,
                        final Attributes attrs,
                        final ExtensibleXmlParser parser) throws SAXException {
        parser.startElementBuilder(localName, attrs);

        String id = attrs.getValue("id");
        String definitionRef = attrs.getValue("definitionRef");

        logger.debug("Found plan item with id {} and definitionRef {}", id, definitionRef);

        // save plan item so they can be easily referenced later
        ProcessBuildData buildData = (ProcessBuildData) parser.getData();
        Map<String, PlanItem> planItems = (Map<String, PlanItem>) buildData.getMetaData("PlanItems");
        if (planItems == null) {
            planItems = new HashMap<String, PlanItem>();
            buildData.setMetaData("PlanItems", planItems);
        }
        PlanItem planItem = new PlanItem(id, definitionRef);
        planItems.put(definitionRef, planItem);

        return planItem;
    }

    @Override
    public Object end(final String uri,
                      final String localName,
                      final ExtensibleXmlParser parser) throws SAXException {
        final Element element = parser.endElementBuilder();
        PlanItem planItem = (PlanItem) parser.getCurrent();

        // save sentries so they can be easily referenced later to be filled in with language and expression
        ProcessBuildData buildData = (ProcessBuildData) parser.getData();
        Map<String, Sentry> sentries = (Map<String, Sentry>) buildData.getMetaData("Sentries");
        if (sentries == null) {
            sentries = new HashMap<String, Sentry>();
            buildData.setMetaData("Sentries", sentries);
        }

        // handle entry and exit criteria
        org.w3c.dom.Node xmlNode = element.getFirstChild();
        while (xmlNode != null) {
            String nodeName = xmlNode.getNodeName();
            if ("entryCriterion".equals(nodeName)) {
                Sentry sentryStub = readSentry(xmlNode, planItem);
                Sentry sentryStubTmp = sentries.putIfAbsent(sentryStub.getId(), sentryStub);
                if (sentryStubTmp != null) {
                    sentryStub = sentryStubTmp;
                }
                planItem.setEntryCriterion(sentryStub);
            } else if ("exitCriterion".equals(nodeName)) {
                Sentry sentryStub = readSentry(xmlNode, planItem);
                Sentry sentryStubTmp = sentries.putIfAbsent(sentryStub.getId(), sentryStub);
                if (sentryStubTmp != null) {
                    sentryStub = sentryStubTmp;
                }
                planItem.setExitCriterion(sentryStub);
            }
            xmlNode = xmlNode.getNextSibling();
        }

        return planItem;
    }

    private Sentry readSentry(Node xmlNode, PlanItem planItem) {
        String sentryRef = ((Element) xmlNode).getAttribute("sentryRef");
        return new Sentry(sentryRef, null, null);
    }

    @Override
    public Class<?> generateNodeFor() {
        return PlanItem.class;
    }

}
