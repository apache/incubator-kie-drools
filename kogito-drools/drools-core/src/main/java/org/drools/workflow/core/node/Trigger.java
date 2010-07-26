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

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.drools.process.core.context.variable.Mappable;

public class Trigger implements Mappable, Serializable {
	
	private static final long serialVersionUID = 4L;
	
	private Map<String, String> inMapping = new HashMap<String, String>();

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

}
