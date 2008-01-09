package org.drools.bpel.core;

import org.drools.workflow.core.impl.DroolsConsequenceAction;
import org.drools.workflow.core.node.ActionNode;


/**
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class BPELAssign extends ActionNode implements BPELBasicActivity {

    private static final long serialVersionUID = 400L;
    
    private String[] sourceLinks;
    private String[] targetLinks;
    
    public void setAction(String assign) {
        setAction(new DroolsConsequenceAction("mvel", assign));
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
