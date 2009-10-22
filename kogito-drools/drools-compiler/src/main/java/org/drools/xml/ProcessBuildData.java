/**
 * 
 */
package org.drools.xml;

import java.util.HashMap;
import java.util.Map;

import org.drools.definition.process.Process;
import org.drools.workflow.core.Node;

public class ProcessBuildData {
    
    private Process process;
    private Map<Long, Node> nodes = new HashMap<Long, Node>();
    private Map<String, Object> metaData = new HashMap<String, Object>();
    
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
    
    public Object getMetaData(String name) {
        return metaData.get(name);
    }
    
    public void setMetaData(String name, Object data) {
        this.metaData.put(name, data);
    }
}