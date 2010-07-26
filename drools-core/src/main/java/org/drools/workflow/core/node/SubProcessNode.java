/**
 * Copyright 2010 JBoss Inc
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

package org.drools.workflow.core.node;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.drools.definition.process.Connection;
import org.drools.process.core.context.variable.Mappable;

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


/**
 * Default implementation of a sub-flow node.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class SubProcessNode extends StateBasedNode implements Mappable {

	private static final long serialVersionUID = 400L;
	
	private String processId;
	private boolean waitForCompletion = true;
    private Map<String, String> inMapping = new HashMap<String, String>();
    private Map<String, String> outMapping = new HashMap<String, String>();
    private boolean independent = true;

    public void setProcessId(final String processId) {
        this.processId = processId;
    }

    public String getProcessId() {
        return this.processId;
    }

    public boolean isWaitForCompletion() {
        return waitForCompletion;
    }

    public void setWaitForCompletion(boolean waitForCompletion) {
        this.waitForCompletion = waitForCompletion;
    }

    public void addInMapping(String subVariableName, String variableName) {
        inMapping.put(subVariableName, variableName);
    }
    
    public void setInMappings(Map<String, String> inMapping) {
        this.inMapping = inMapping;
    }
    
    public String getInMapping(String parameterName) {
        return inMapping.get(parameterName);
    }

    public Map<String, String> getInMappings() {
        return Collections.unmodifiableMap(inMapping);
    }
    
    public void addOutMapping(String subVariableName, String variableName) {
        outMapping.put(subVariableName, variableName);
    }
    
    public void setOutMappings(Map<String, String> outMapping) {
        this.outMapping = outMapping;
    }
    
    public String getOutMapping(String parameterName) {
        return outMapping.get(parameterName);
    }

    public Map<String, String> getOutMappings() {
        return Collections.unmodifiableMap(outMapping);
    }

    public boolean isIndependent() {
        return independent;
    }

    public void setIndependent(boolean independent) {
        this.independent = independent;
    }

    public void validateAddIncomingConnection(final String type, final Connection connection) {
        super.validateAddIncomingConnection(type, connection);
        if (!org.drools.workflow.core.Node.CONNECTION_DEFAULT_TYPE.equals(type)) {
            throw new IllegalArgumentException(
                "This type of node only accepts default incoming connection type!");
        }
        if (getFrom() != null) {
            throw new IllegalArgumentException(
                "This type of node cannot have more than one incoming connection!");
        }
    }

    public void validateAddOutgoingConnection(final String type, final Connection connection) {
        super.validateAddOutgoingConnection(type, connection);
        if (!org.drools.workflow.core.Node.CONNECTION_DEFAULT_TYPE.equals(type)) {
            throw new IllegalArgumentException(
                "This type of node only accepts default outgoing connection type!");
        }
        if (getTo() != null) {
            throw new IllegalArgumentException(
                "This type of node cannot have more than one outgoing connection!");
        }
    }
    
}
