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
    private Map<String, Node> nodes = new HashMap<String, Node>();               
    
    public Process getProcess() {
        return process;
    }
    
    public void setProcess(Process process) {
        this.process = process;
    }
    
    public Map<String, Node> getNodes() {
        return nodes;
    }
    public boolean addNode(Node node) {
        return( this.nodes.put( node.getName(),
                               node ) != null );
    }               
    
    public Node getNode(String name) {
        return this.nodes.get( name );
    }
}