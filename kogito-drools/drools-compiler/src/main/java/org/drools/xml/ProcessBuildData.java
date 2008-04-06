/**
 * 
 */
package org.drools.xml;

import java.util.HashMap;
import java.util.Map;

import org.drools.process.core.Process;
import org.drools.workflow.core.Node;

public class ProcessBuildData {
    
    private Process process;
    private Map<Long, Node> nodes = new HashMap<Long, Node>();               
    
    public Process getProcess() {
        return process;
    }
    
    public void setProcess(Process process) {
        this.process = process;
    }
    
    public Map<Long, Node> getNodes() {
        return nodes;
    }
    public boolean addNode(Node node) {
        return( this.nodes.put( node.getId(), node ) != null );
    }               
    
    public Node getNode(Long id) {
        return this.nodes.get( id );
    }
}