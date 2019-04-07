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

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.LinkedList;
import java.util.List;


import org.jbpm.process.core.context.variable.Mappable;

public class Trigger implements Mappable, Serializable {
	
	private static final long serialVersionUID = 510l;

	private List<DataAssociation> inMapping = new LinkedList<DataAssociation>();

    public void addInMapping(String subVariableName, String variableName) {
        inMapping.add(new DataAssociation(subVariableName, variableName, null, null));
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
    			in.put(a.getSources().get(0), a.getTarget());
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
    
    public void addOutMapping(String subVariableName, String variableName) {
        throw new IllegalArgumentException(
    		"A trigger does not support out mappings");
    }
    
    public void setOutMappings(Map<String, String> outMapping) {
        throw new IllegalArgumentException(
			"A trigger does not support out mappings");
    }
    
    public String getOutMapping(String parameterName) {
        throw new IllegalArgumentException(
			"A trigger does not support out mappings");
    }
    
    public Map<String, String> getOutMappings() {
        throw new IllegalArgumentException(
		"A trigger does not support out mappings");
    }

	public void addOutAssociation(DataAssociation dataAssociation) {
        throw new IllegalArgumentException(
		"A trigger does not support out mappings");
	}

    public List <DataAssociation> getOutAssociations() {
        throw new IllegalArgumentException(
        	"A trigger does not support out mappings");
    }
}
