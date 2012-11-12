/**
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

package org.jbpm.workflow.core.node;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.kie.definition.process.Connection;

/**
 * Default implementation of a RuleSet node.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class RuleSetNode extends StateBasedNode {

    private static final long serialVersionUID = 510l;

    private String ruleFlowGroup;
    private List<DataAssociation> inMapping = new LinkedList<DataAssociation>();
    private List<DataAssociation> outMapping = new LinkedList<DataAssociation>();
    
    private Map<String, Object> parameters = new HashMap<String, Object>();

    public void setRuleFlowGroup(final String ruleFlowGroup) {
        this.ruleFlowGroup = ruleFlowGroup;
    }

    public String getRuleFlowGroup() {
        return this.ruleFlowGroup;
    }

    public void validateAddIncomingConnection(final String type, final Connection connection) {
        super.validateAddIncomingConnection(type, connection);
        if (!org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE.equals(type)) {
            throw new IllegalArgumentException(
                "This type of node only accepts default incoming connection type!");
        }
        if (getFrom() != null && System.getProperty("jbpm.enable.multi.con") == null) {
            throw new IllegalArgumentException(
                 "This type of node cannot have more than one incoming connection!");
        }
    }

    public void validateAddOutgoingConnection(final String type, final Connection connection) {
        super.validateAddOutgoingConnection(type, connection);
        if (!org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE.equals(type)) {
            throw new IllegalArgumentException(
                "This type of node only accepts default outgoing connection type!");
        }
        if (getTo() != null && System.getProperty("jbpm.enable.multi.con") == null) {
            throw new IllegalArgumentException(
              "This type of node cannot have more than one outgoing connection!");
        }
    }
    
    public void addInMapping(String parameterName, String variableName) {
        inMapping.add(new DataAssociation(variableName, parameterName, null, null));
    }

    public void setInMappings(Map<String, String> inMapping) {
        this.inMapping = new LinkedList<DataAssociation>();
        for(Map.Entry<String, String> entry : inMapping.entrySet()) {
            addInMapping(entry.getKey(), entry.getValue());
        }
    }

    public String getInMapping(String parameterName) {
        return getInMappings().get(parameterName);
    }
    
    public Map<String, String> getInMappings() {
        Map<String,String> in = new HashMap<String, String>(); 
        for(DataAssociation a : inMapping) {
            if(a.getSources().size() ==1 && (a.getAssignments() == null || a.getAssignments().size()==0) && a.getTransformation() == null) {
                in.put(a.getTarget(), a.getSources().get(0));
            }
        }
        return in;
    }

    public void addInAssociation(DataAssociation dataAssociation) {
        inMapping.add(dataAssociation);
    }

    public List<DataAssociation> getInAssociations() {
        return Collections.unmodifiableList(inMapping);
    }
    
    public void addOutMapping(String parameterName, String variableName) {
        outMapping.add(new DataAssociation(parameterName, variableName, null, null));
    }

    public void setOutMappings(Map<String, String> outMapping) {
        this.outMapping = new LinkedList<DataAssociation>();
        for(Map.Entry<String, String> entry : outMapping.entrySet()) {
            addOutMapping(entry.getKey(), entry.getValue());
        }
    }

    public String getOutMapping(String parameterName) {
        return getOutMappings().get(parameterName);
    }
    
    public Map<String, String> getOutMappings() {
        Map<String,String> out = new HashMap<String, String>(); 
        for(DataAssociation a : outMapping) {
            if(a.getSources().size() ==1 && (a.getAssignments() == null || a.getAssignments().size()==0) && a.getTransformation() == null) {
                out.put(a.getSources().get(0), a.getTarget());
            }
        }
        return out;
    }
    
    public void addOutAssociation(DataAssociation dataAssociation) {
        outMapping.add(dataAssociation);
    }

    public List<DataAssociation> getOutAssociations() {
        return Collections.unmodifiableList(outMapping);
    }

    public Map<String, Object> getParameters() {
        return Collections.unmodifiableMap(parameters);
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }
    
    public void setParameter(String param, Object value) {
        this.parameters.put(param, value);
    }
    
    public Object getParameter(String param) {
        return this.parameters.get(param);
    }
    
}
