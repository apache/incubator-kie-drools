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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.drools.core.xml.ExtensibleXmlParser;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.node.DataAssociation;
import org.jbpm.workflow.core.node.SubProcessNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class ProcessTaskHandler extends AbstractCaseNodeHandler {

    private static final Logger logger = LoggerFactory.getLogger(ProcessTaskHandler.class);

    protected Node createNode(Attributes attrs) {
        return new SubProcessNode();
    }

    @SuppressWarnings("unchecked")
    public Class generateNodeFor() {
        return SubProcessNode.class;
    }

    protected void handleNode(final Node node,
                              final Element element,
                              final String uri,
                              final String localName,
                              final ExtensibleXmlParser parser) throws SAXException {
        super.handleNode(node, element, uri, localName, parser);

        SubProcessNode subProcessNode = (SubProcessNode) node;
        String processId = element.getAttribute("processRef");
        String isBlocking = element.getAttribute("isBlocking");
        if (isBlocking == null || isBlocking.isEmpty()) {
            isBlocking = "true";
        }
        subProcessNode.setProcessId(processId);
        subProcessNode.setWaitForCompletion(Boolean.parseBoolean(isBlocking));
        subProcessNode.setIndependent(false);

        Map<String, String> inputs = new HashMap<>();
        Map<String, String> outputs = new HashMap<>();
        Map<String, String> inputTypes = new HashMap<>();
        Map<String, String> outputTypes = new HashMap<>();
        loadDataInputsAndOutputs(element, inputs, outputs, inputTypes, outputTypes, parser);

        subProcessNode.setMetaData("DataInputs", inputTypes);
        subProcessNode.setMetaData("DataOutputs", outputTypes);
        subProcessNode.setMetaData("customAbortParent", "false");

        for (Entry<String, String> entry : inputs.entrySet()) {
            subProcessNode.addInAssociation(new DataAssociation(entry.getValue(), entry.getKey(), Collections.emptyList(), null));
        }

        for (Entry<String, String> entry : outputs.entrySet()) {

            subProcessNode.addOutAssociation(new DataAssociation(entry.getKey(), entry.getValue(), Collections.emptyList(), null));
        }
    }

}
