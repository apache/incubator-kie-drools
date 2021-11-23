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

import java.util.HashMap;
import java.util.Map;

import org.drools.core.xml.BaseAbstractHandler;
import org.drools.core.xml.ExtensibleXmlParser;
import org.drools.core.xml.Handler;
import org.jbpm.bpmn2.core.CorrelationSubscription;
import org.jbpm.bpmn2.core.Expression;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class CorrelationSubscriptionHandler extends BaseAbstractHandler implements Handler {

    @Override
    public Object start(String uri, String localName, Attributes attrs, ExtensibleXmlParser parser) throws SAXException {
        parser.startElementBuilder(localName, attrs);

        String correlationSubscriptionPropertyId = attrs.getValue("id");
        String correlationSubscriptionPropertyName = attrs.getValue("name");
        String correlationSubscriptionRef = attrs.getValue("correlationKeyRef");

        CorrelationSubscription correlationSubscription = new CorrelationSubscription();
        correlationSubscription.setId(correlationSubscriptionPropertyId);
        correlationSubscription.setName(correlationSubscriptionPropertyName);
        correlationSubscription.setCorrelationKeyRef(correlationSubscriptionRef);

        RuleFlowProcess process = (RuleFlowProcess) parser.getParent();
        HandlerUtil.correlationSubscription(process).put(correlationSubscriptionPropertyId, correlationSubscription);
        return correlationSubscription;
    }

    @Override
    public Object end(String uri, String localName, ExtensibleXmlParser parser) throws SAXException {
        Element element = parser.endElementBuilder();
        CorrelationSubscription correlationSubscription = (CorrelationSubscription) parser.getCurrent();
        correlationSubscription.getPropertyExpressions().putAll(buildPropertyProcessBindings(element.getChildNodes(), parser));
        return null;
    }

    private Map<String, Expression> buildPropertyProcessBindings(NodeList childNodes, ExtensibleXmlParser parser) {
        Map<String, Expression> correlationKeys = new HashMap<>();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            if ("correlationPropertyBinding".equals(node.getNodeName())) {
                Element elementBinding = (Element) node;
                correlationKeys.put(elementBinding.getAttribute("correlationPropertyRef"), buildBindingExpression(elementBinding.getChildNodes(), parser));
            }
        }
        return correlationKeys;
    }

    private Expression buildBindingExpression(NodeList childNodes, ExtensibleXmlParser parser) {
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            if ("dataPath".equals(node.getNodeName())) {
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
        return CorrelationSubscription.class;
    }

}