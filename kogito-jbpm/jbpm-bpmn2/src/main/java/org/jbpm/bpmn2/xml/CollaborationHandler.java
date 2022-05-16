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

package org.jbpm.bpmn2.xml;

import java.util.ArrayList;
import java.util.List;

import org.jbpm.bpmn2.core.Collaboration;
import org.jbpm.bpmn2.core.CorrelationKey;
import org.jbpm.compiler.xml.Handler;
import org.jbpm.compiler.xml.Parser;
import org.jbpm.compiler.xml.core.BaseAbstractHandler;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class CollaborationHandler extends BaseAbstractHandler implements Handler {

    @Override
    public Object start(String uri, String localName, Attributes attrs, Parser parser) throws SAXException {
        parser.startElementBuilder(localName, attrs);

        String collaborationPropertyId = attrs.getValue("id");
        String collaborationPropertyName = attrs.getValue("name");

        Collaboration collaboration = new Collaboration();
        collaboration.setId(collaborationPropertyId);
        collaboration.setName(collaborationPropertyName);
        HandlerUtil.collaborations(parser).put(collaborationPropertyId, collaboration);
        return collaboration;
    }

    @Override
    public Object end(String uri, String localName, Parser parser) throws SAXException {
        Element element = parser.endElementBuilder();
        Collaboration collaboration = (Collaboration) parser.getCurrent();
        buildCorrelationKeys(collaboration, element.getChildNodes());
        return null;
    }

    private void buildCorrelationKeys(Collaboration collaboration, NodeList childNodes) {
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            if ("correlationKey".equals(node.getNodeName())) {
                Element elementCorrelationKey = (Element) node;
                CorrelationKey key = new CorrelationKey();
                key.setId(elementCorrelationKey.getAttribute("id"));
                key.setName(elementCorrelationKey.getAttribute("name"));
                key.getPropertiesRef().addAll(buildPropertiesRef(elementCorrelationKey.getChildNodes()));
                collaboration.addCorrelationKey(key);
            } else if ("participant".equals(node.getNodeName())) {
                Element participant = (Element) node;
                collaboration.getProcessesRef().add(participant.getAttribute("processRef"));
            }
        }
    }

    private List<String> buildPropertiesRef(NodeList childNodes) {
        List<String> propertiesRef = new ArrayList<>();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            if ("correlationPropertyRef".equals(node.getNodeName())) {
                propertiesRef.add(node.getTextContent());
            }
        }
        return propertiesRef;
    }

    @Override
    public Class<?> generateNodeFor() {
        return Collaboration.class;
    }

}
