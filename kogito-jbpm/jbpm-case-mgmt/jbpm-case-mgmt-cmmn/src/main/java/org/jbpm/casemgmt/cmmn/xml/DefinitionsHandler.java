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
import java.util.List;
import java.util.Map;

import org.drools.core.xml.BaseAbstractHandler;
import org.drools.core.xml.ExtensibleXmlParser;
import org.drools.core.xml.Handler;
import org.jbpm.casemgmt.cmmn.core.Decision;
import org.jbpm.casemgmt.cmmn.core.Definitions;
import org.jbpm.compiler.xml.ProcessBuildData;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.workflow.core.node.RuleSetNode;
import org.jbpm.workflow.core.node.SubProcessNode;
import org.kie.api.definition.process.Node;
import org.kie.api.definition.process.NodeContainer;
import org.kie.api.definition.process.Process;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class DefinitionsHandler extends BaseAbstractHandler implements Handler {

    @SuppressWarnings("unchecked")
    public DefinitionsHandler() {
        if ((this.validParents == null) && (this.validPeers == null)) {
            this.validParents = new HashSet();
            this.validParents.add(null);
            this.validPeers = new HashSet();
            this.validPeers.add(null);
            this.allowNesting = false;
        }
    }

    public Object start(final String uri,
                        final String localName,
                        final Attributes attrs,
                        final ExtensibleXmlParser parser) throws SAXException {
        parser.startElementBuilder(localName, attrs);
        return new Definitions();
    }

    public Object end(final String uri,
                      final String localName,
                      final ExtensibleXmlParser parser) throws SAXException {
        final Element element = parser.endElementBuilder();
        Definitions definitions = (Definitions) parser.getCurrent();
        String id = element.getAttribute("id");
        String namespace = element.getAttribute("targetNamespace");
        ProcessBuildData buildData = (ProcessBuildData) parser.getData();
        List<Process> processes = ((ProcessBuildData) parser.getData()).getProcesses();

        String namespaceN1 = (String) parser.getNamespaceURI("ns2");
        
        
        
        for (Process process : processes) {
            RuleFlowProcess ruleFlowProcess = (RuleFlowProcess) process;
            ruleFlowProcess.setMetaData("TargetNamespace", namespace);
            postProcessNodes(ruleFlowProcess, ruleFlowProcess, buildData, parser);
        }
        definitions.setId(id);
        definitions.setTargetNamespace(namespace);
        return definitions;
    }

    public Class<?> generateNodeFor() {
        return Definitions.class;
    }
    
    private void postProcessNodes(RuleFlowProcess process, NodeContainer container, ProcessBuildData buildData, ExtensibleXmlParser parser) {
        for (Node node : container.getNodes()) {

            if (node instanceof SubProcessNode) {
                Map<String, String> processes = (Map<String, String>) buildData.getMetaData("ProcessElements");
                if (processes != null) {

                    SubProcessNode subprocessNode = (SubProcessNode) node;
                    subprocessNode.setProcessId(processes.getOrDefault(subprocessNode.getProcessId(), subprocessNode.getProcessId()));    
                }
            } else if (node instanceof RuleSetNode) {
                Map<String, Decision> decisions = (Map<String, Decision>) buildData.getMetaData("DecisionElements");
                RuleSetNode ruleSetNode = (RuleSetNode) node;
                if (decisions != null && decisions.containsKey(ruleSetNode.getRuleFlowGroup())) {
                    Decision decision = decisions.get(ruleSetNode.getRuleFlowGroup());
                    ruleSetNode.setRuleFlowGroup(null);
                    ruleSetNode.setLanguage(RuleSetNode.DMN_LANG);
                    ruleSetNode.setNamespace((String) parser.getNamespaceURI(decision.getNamespace()));
                    ruleSetNode.setModel(decision.getModel());
                    ruleSetNode.setDecision(decision.getDecision());
                }
            }

            if (node instanceof NodeContainer) {                
                postProcessNodes(process, (NodeContainer) node, buildData, parser);
            }
        }
    }

}
