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

package org.drools.process.instance.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.drools.process.instance.WorkItem;

/**
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class WorkItemImpl implements WorkItem, Serializable {

    private static final long serialVersionUID = 400L;
    
    private long id;
    private String name;
    private int state = 0;
    private Map<String, Object> parameters = new HashMap<String, Object>();
    private Map<String, Object> results = new HashMap<String, Object>();
    private long processInstanceId;
    
    public void setId(long id) {
        this.id = id;
    }
    
    public long getId() {
        return id;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public void setState(int state) {
        this.state = state;
    }
    
    public int getState() {
        return state;
    }
    
    public void setParameters(Map<String, Object> parameters) {
    	this.parameters = parameters;
    }
    
    public void setParameter(String name, Object value) {
        this.parameters.put(name, value);
    }
    
    public Object getParameter(String name) {
        return parameters.get(name);
    }
    
    public Map<String, Object> getParameters() {
        return parameters;
    }
    
    public void setResults(Map<String, Object> results) {
        if (results != null) {
            this.results = results;
        }
    }
    
    public void setResult(String name, Object value) {
        results.put(name, value);
    }
    
    public Object getResult(String name) {
        return results.get(name);
    }
    
    public Map<String, Object> getResults() {
        return results;
    }
    
    public void setProcessInstanceId(long processInstanceId) {
        this.processInstanceId = processInstanceId;
    }
    
    public long getProcessInstanceId() {
        return processInstanceId;
    }
    
    public String toString() {
    	StringBuilder b = new StringBuilder("WorkItem ");
    	b.append(id);
    	b.append(" [name=");
    	b.append(name);
    	b.append(", state=");
    	b.append(state);
    	b.append(", processInstanceId=");
    	b.append(processInstanceId);
        b.append(", parameters{");
    	for (Iterator<Map.Entry<String, Object>> iterator = parameters.entrySet().iterator(); iterator.hasNext(); ) {
    	    Map.Entry<String, Object> entry = iterator.next();
    	    b.append(entry.getKey());
    	    b.append("=");
    	    b.append(entry.getValue());
    	    if (iterator.hasNext()) {
    	        b.append(", ");
    	    }
    	}
        b.append("}]");
    	return b.toString();
    }
}