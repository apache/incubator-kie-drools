/*
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
package org.jbpm.bpmn2.xml;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.jbpm.bpmn2.core.*;
import org.jbpm.bpmn2.core.Error;
import org.jbpm.compiler.xml.Handler;
import org.jbpm.compiler.xml.Parser;
import org.jbpm.compiler.xml.ProcessBuildData;
import org.jbpm.process.core.ContextContainer;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import static org.jbpm.ruleflow.core.Metadata.TEXT_ANNOTATIONS;
import static org.w3c.dom.Node.ELEMENT_NODE;

public class TextAnnotationHandler extends org.jbpm.compiler.xml.core.BaseAbstractHandler implements Handler {

    public TextAnnotationHandler() {
        if (this.validParents == null && this.validPeers == null) {
            this.validParents = new HashSet<>();
            this.validParents.add(ContextContainer.class);
            this.validParents.add(Definitions.class);
            this.validPeers = new HashSet<>();
            this.validPeers.add(null);
            this.validPeers.add(ItemDefinition.class);
            this.validPeers.add(Message.class);
            this.validPeers.add(Interface.class);
            this.validPeers.add(Escalation.class);
            this.validPeers.add(Error.class);
            this.validPeers.add(Signal.class);
            this.validPeers.add(DataStore.class);
            this.validPeers.add(RuleFlowProcess.class);
            this.validPeers.add(SequenceFlow.class);
            this.validPeers.add(TextAnnotation.class);
            this.allowNesting = false;
        }
    }

    @Override
    public Object start(String uri, String localName, Attributes attrs, Parser parser) throws SAXException {
        parser.startElementBuilder(localName, attrs);
        String id = attrs.getValue("id");
        TextAnnotation annotation = new TextAnnotation();
        annotation.setId(id);
        Map<String, TextAnnotation> annotations =
                (Map<String, TextAnnotation>) ((ProcessBuildData) parser.getData()).getMetaData(TEXT_ANNOTATIONS);

        if (annotations == null) {
            annotations = new HashMap<>();
            ((ProcessBuildData) parser.getData()).setMetaData(TEXT_ANNOTATIONS, annotations);
        }
        annotations.put(id, annotation);
        if (parser.getParent() instanceof RuleFlowProcess) {
            RuleFlowProcess proc = (RuleFlowProcess) parser.getParent();
            if (proc.getMetaData(TEXT_ANNOTATIONS) == null) {
                proc.setMetaData(TEXT_ANNOTATIONS, annotations);
            }
        }

        return annotation;
    }

    @Override
    public Object end(String uri, String localName, Parser parser) throws SAXException {
        Element el = parser.endElementBuilder();
        TextAnnotation ta = (TextAnnotation) parser.getCurrent();
        for (Node n = el.getFirstChild(); n != null; n = n.getNextSibling())
            if (n.getNodeType() == ELEMENT_NODE && ("text".equals(n.getNodeName()) || n.getNodeName().endsWith(":text")))
                ta.setText(n.getTextContent());
        return ta;
    }

    @Override
    public Class<?> generateNodeFor() {
        return TextAnnotation.class;
    }
}
