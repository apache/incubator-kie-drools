package org.drools.bpel.core;

import org.drools.process.core.Work;
import org.drools.process.core.impl.WorkImpl;
import org.drools.workflow.core.node.WorkItemNode;

/**
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class BPELInvoke extends WorkItemNode implements BPELBasicActivity, BPELFaultHandlerContainer {

    private static final long serialVersionUID = 400L;
    
    private static final String PARTNER_LINK = "PartnerLink";
    private static final String PORT_TYPE = "PortType";
    private static final String OPERATION = "Operation";
    private static final String INPUT = "Message";
    private static final String OUTPUT = "Result";
    
    private String[] sourceLinks;
    private String[] targetLinks;
    
    public BPELInvoke() {
        Work work = new WorkImpl();
        work.setName("WebServiceInvocation");
        setWork(work);
    }
    
    public String getPartnerLink() {
        return (String) getWork().getParameter(PARTNER_LINK);
    }

    public void setPartnerLink(String partnerLink) {
        getWork().setParameter(PARTNER_LINK, partnerLink);
    }

    public String getPortType() {
        return (String) getWork().getParameter(PORT_TYPE);
    }

    public void setPortType(String porttype) {
        getWork().setParameter(PORT_TYPE, porttype);
    }

    public String getOperation() {
        return (String) getWork().getParameter(OPERATION);
    }

    public void setOperation(String operation) {
        getWork().setParameter(OPERATION, operation);
    }

    public String getInputVariable() {
        return getInMapping(INPUT);
    }

    public void setInputVariable(String inputVariable) {
        addInMapping(INPUT, inputVariable);
    }

    public String getOutputVariable() {
        return getOutMapping(OUTPUT);
    }

    public void setOutputVariable(String outputVariable) {
        addOutMapping(OUTPUT, outputVariable);
    }

    public String[] getSourceLinks() {
        return sourceLinks;
    }

    public void setSourceLinks(String[] sourceLinks) {
        this.sourceLinks = sourceLinks;
    }

    public String[] getTargetLinks() {
        return targetLinks;
    }

    public void setTargetLinks(String[] targetLinks) {
        this.targetLinks = targetLinks;
    }

}
