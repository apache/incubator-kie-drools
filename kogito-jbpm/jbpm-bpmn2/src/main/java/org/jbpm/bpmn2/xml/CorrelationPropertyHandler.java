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

import org.drools.core.xml.BaseAbstractHandler;
import org.drools.core.xml.ExtensibleXmlParser;
import org.drools.core.xml.Handler;
import org.jbpm.bpmn2.core.CorrelationProperty;
import org.jbpm.bpmn2.core.Expression;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class CorrelationPropertyHandler extends BaseAbstractHandler implements Handler {

    @Override
    public Object start(String uri, String localName, Attributes attrs, ExtensibleXmlParser parser) throws SAXException {
        parser.startElementBuilder(localName, attrs);

        String correlationPropertyId = attrs.getValue("id");
        String correlationPropertyName = attrs.getValue("name");
        String type = attrs.getValue("type");

        CorrelationProperty correlationProperty = new CorrelationProperty();
        correlationProperty.setId(correlationPropertyId);
        correlationProperty.setName(correlationPropertyName);
        correlationProperty.setType(HandlerUtil.definitions(parser).get(type).getStructureRef());

        HandlerUtil.correlationProperties(parser).put(correlationPropertyId, correlationProperty);
        return correlationProperty;
    }

    @Override
    public Object end(String uri, String localName, ExtensibleXmlParser parser) throws SAXException {
        Element element = parser.endElementBuilder();
        CorrelationProperty correlationProperty = (CorrelationProperty) parser.getCurrent();

        NodeList nodeList = element.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if ("correlationPropertyRetrievalExpression".equals(node.getNodeName())) {
                String messageRef = ((Element) node).getAttribute("messageRef");
                correlationProperty.setRetrievalExpression(messageRef, buildMessagePathExpression(node.getChildNodes(), parser));
            }
        }
        return null;
    }

    private Expression buildMessagePathExpression(NodeList childNodes, ExtensibleXmlParser parser) {
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            if ("messagePath".equals(node.getNodeName())) {
                Element expressionElement = (Element) node;
                Expression expression = new Expression();
                expression.setId(expressionElement.getAttribute("id"));
                expression.setLang(expressionElement.getAttribute("language"));
                expression.setScript(expressionElement.getTextContent());
                expression.setOutcomeType(HandlerUtil.definitions(parser).get(expressionElement.getAttribute("evaluatesToTypeRef")).getStructureRef());
                return expression;
            }
        }
        throw new RuntimeException("message Path not found for correlation property " + parser.getCurrent());
    }

    @Override
    public Class<?> generateNodeFor() {
        return CorrelationProperty.class;
    }

}