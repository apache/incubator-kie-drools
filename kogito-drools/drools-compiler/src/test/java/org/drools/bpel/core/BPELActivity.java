package org.drools.bpel.core;

import org.drools.workflow.core.Node;

/**
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public interface BPELActivity extends Node {

    String[] getSourceLinks();
    
    void setSourceLinks(String[] sourceLinks);
    
    String[] getTargetLinks();
    
    void setTargetLinks(String[] targetLinks);
    
}
