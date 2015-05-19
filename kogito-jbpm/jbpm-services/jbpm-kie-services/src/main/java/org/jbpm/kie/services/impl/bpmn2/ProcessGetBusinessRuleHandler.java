package org.jbpm.kie.services.impl.bpmn2;

import org.drools.core.xml.ExtensibleXmlParser;
import org.jbpm.bpmn2.xml.BusinessRuleTaskHandler;
import org.jbpm.bpmn2.xml.ImportHandler;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.node.RuleSetNode;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * This handler adds classes imported (via the &gt;extensionElement&lt;) to the list
 * of referenced classes.
 */
public class ProcessGetBusinessRuleHandler extends BusinessRuleTaskHandler {

    private BPMN2DataServiceSemanticModule module;
    private ProcessDescriptionRepository repository;

    public ProcessGetBusinessRuleHandler(BPMN2DataServiceSemanticModule module) {
        this.module = module;
        this.repository = module.getRepo();
    }

    @Override
    protected void handleNode( Node node, Element element, String uri, String localName, ExtensibleXmlParser parser )
            throws SAXException {
        // DBG Auto-generated method stub
        super.handleNode(node, element, uri, localName, parser);
        RuleSetNode ruleSetNode = (RuleSetNode) node;
        String ruleFlowGroup = ruleSetNode.getRuleFlowGroup();
        if( ruleFlowGroup != null ) { 
            String mainProcessId = module.getRepoHelper().getProcess().getId();
            this.repository.getProcessDesc(mainProcessId).getReferencedRules().add(ruleFlowGroup);
        }
    }

}
