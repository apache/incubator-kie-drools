/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.workflow.core.node;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jbpm.workflow.core.WorkflowProcess;
import org.kie.api.definition.process.Node;


public class DynamicNode extends CompositeContextNode {

	private static final long serialVersionUID = 510l;
	
	private String activationExpression;
	private String completionExpression;
	private String language;
			
    @Override
    public boolean acceptsEvent(String type, Object event, Function<String, String> resolver) {
        if (type.equals(getActivationEventName())) {
            return true;
        }

        for (Node node : getNodes()) {
            if (resolver.apply(node.getName()).contains(type) && node.getIncomingConnections().isEmpty()) {
                return true;
            }
        }
        return super.acceptsEvent(type, event);
    }

    public boolean acceptsEvent(String type, Object event) {
        if (type.equals(getActivationEventName())) {
            return true;
        }

        for (Node node : getNodes()) {
            if (type.equals(node.getName()) && node.getIncomingConnections().isEmpty()) {
                return true;
            }
        }
        return super.acceptsEvent(type, event);
    }
	
    public Node internalGetNode(long id) {
    	try {
    		return getNode(id);
    	} catch (IllegalArgumentException e) {
    		return null;
    	}
    }

	public String getCompletionExpression() {
		return completionExpression;
	}

	public void setCompletionExpression(String expression) {
		this.completionExpression = expression;
	}		
    
    public String getActivationExpression() {
        return activationExpression;
    }
    
    public void setActivationExpression(String activationExpression) {
        this.activationExpression = activationExpression;
    }

    public String getLanguage() {
        return language;
    }
   
    public void setLanguage(String language) {
        this.language = language;
    }

    public List<Node> getAutoStartNodes() {
  
        List<Node> nodes = Arrays.stream(getNodes())
                .filter(n -> n.getIncomingConnections().isEmpty() && "true".equalsIgnoreCase((String)n.getMetaData().get("customAutoStart")))
                .collect(Collectors.toList());
                
        return nodes;
    }
    
    public String getActivationEventName() {
        if (activationExpression == null || activationExpression.isEmpty()) {
            return null;
        }
        String activationSignalName = "RuleFlow-AdHocActivate-" + ((WorkflowProcess)getNodeContainer()).getId() + "-" + getUniqueId();
        
        return activationSignalName;
    }
}
