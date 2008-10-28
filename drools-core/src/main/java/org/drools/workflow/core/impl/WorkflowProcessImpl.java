package org.drools.workflow.core.impl;

/*
 * Copyright 2005 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.drools.process.core.impl.ProcessImpl;
import org.drools.workflow.core.Node;
import org.drools.workflow.core.NodeContainer;
import org.drools.workflow.core.WorkflowProcess;

/**
 * Default implementation of a RuleFlow process.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class WorkflowProcessImpl extends ProcessImpl implements WorkflowProcess {

    private static final long   serialVersionUID = 400L;

    private NodeContainer nodeContainer;
    private List<String> imports;
    private List<String> functionImports;
    private Map<String, String> globals;
    
    public WorkflowProcessImpl() {
        nodeContainer = createNodeContainer();
    }
    
    protected NodeContainer createNodeContainer() {
        return new NodeContainerImpl();
    }
    
    public Node[] getNodes() {
        return nodeContainer.getNodes();
    }

    public Node getNode(final long id) {
        return nodeContainer.getNode(id);
    }
    
    public Node internalGetNode(long id) {
    	return getNode(id);
    }

    public void removeNode(final Node node) {
        nodeContainer.removeNode(node);
        node.setNodeContainer(null);
    }

    public void addNode(final Node node) {
        nodeContainer.addNode(node);
        node.setNodeContainer(this);
    }

    public List<String> getImports() {
        return imports;
    }

    public void setImports(List<String> imports) {
        this.imports = imports;
    }
    
    public List<String> getFunctionImports() {
        return functionImports;
    }

    public void setFunctionImports(List<String> functionImports) {
        this.functionImports = functionImports;
    }
    
    public Map<String, String> getGlobals() {
        return globals;
    }

    public void setGlobals(Map<String, String> globals) {
        this.globals = globals;
    }

    public String[] getGlobalNames() {
        final List<String> result = new ArrayList<String>();
        if (this.globals != null) {
            for ( Iterator<String> iterator = this.globals.keySet().iterator(); iterator.hasNext(); ) {
                result.add(iterator.next());
            }
        }
        return result.toArray(new String[result.size()]);
    }
    
}
