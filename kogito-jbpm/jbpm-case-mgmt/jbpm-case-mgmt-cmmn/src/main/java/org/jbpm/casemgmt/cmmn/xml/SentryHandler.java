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

import java.util.HashSet;
import java.util.Map;

import org.drools.core.xml.BaseAbstractHandler;
import org.drools.core.xml.ExtensibleXmlParser;
import org.drools.core.xml.Handler;
import org.jbpm.casemgmt.cmmn.core.PlanItem;
import org.jbpm.casemgmt.cmmn.core.Sentry;
import org.jbpm.compiler.xml.ProcessBuildData;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class SentryHandler extends BaseAbstractHandler implements Handler {

    private static final Logger logger = LoggerFactory.getLogger(SentryHandler.class);

    public SentryHandler() {
        if ((this.validParents == null) && (this.validPeers == null)) {
            this.validParents = new HashSet<>();
            this.validParents.add(RuleFlowProcess.class);

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
        ProcessBuildData buildData = (ProcessBuildData) parser.getData();
        Map<String, Sentry> sentries = (Map<String, Sentry>) buildData.getMetaData("Sentries");

        Sentry sentryStub = sentries.get(id);
        if (sentryStub == null) {
            sentryStub = new Sentry(id, null, null);
            sentries.put(id, sentryStub);
        }
        return sentryStub;
    }

    @Override
    public Object end(final String uri,
                      final String localName,
                      final ExtensibleXmlParser parser) throws SAXException {
        final Element element = parser.endElementBuilder();
        Sentry sentry = (Sentry) parser.getCurrent();

        org.w3c.dom.Node xmlNode = element.getFirstChild();
        while (xmlNode != null) {
            String nodeName = xmlNode.getNodeName();
            if ("ifPart".equals(nodeName)) {

                org.w3c.dom.Node subNode = xmlNode.getFirstChild();
                while (subNode != null) {
                    String subNodeName = subNode.getNodeName();
                    if ("condition".equals(subNodeName)) {
                        String language = ((Element) subNode).getAttribute("language");
                        String expression = ((Element) subNode).getTextContent();

                        sentry.setLanguage(language);
                        sentry.setExpression(expression);

                    }
                    subNode = subNode.getNextSibling();
                }
            }
            xmlNode = xmlNode.getNextSibling();
        }

        return sentry;
    }

    @Override
    public Class<?> generateNodeFor() {
        return Sentry.class;
    }

}
