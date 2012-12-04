package org.droolsjbpm.services.impl.bpmn2;

import javax.inject.Inject;

import org.drools.xml.ExtensibleXmlParser;
import org.jbpm.bpmn2.xml.CallActivityHandler;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.node.SubProcessNode;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class GetReusableSubProcessesHandler extends CallActivityHandler {

    @Inject
    private ProcessDescriptionRepository repository;
    
    private ProcessDescRepoHelper repo;
    
    @Override
    protected void handleNode(Node node, Element element, String uri,
            String localName, ExtensibleXmlParser parser) throws SAXException {
        super.handleNode(node, element, uri, localName, parser);
        String mainProcessId = repo.getProcess().getId();
        SubProcessNode subProcess = (SubProcessNode) node;
        repository.getProcessDesc(mainProcessId).getReusableSubProcesses().add(subProcess.getProcessId());
    }

    
    public void setRepo(ProcessDescRepoHelper repo) {
        this.repo = repo;
    }
}
