/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.runtime.impl;

import org.drools.core.xml.jaxb.util.JaxbMapAdapter;
import org.kie.api.runtime.ExecutionResults;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@XmlRootElement(name="execution-results")
@XmlAccessorType(XmlAccessType.NONE)
public class ExecutionResultImpl implements ExecutionResults, Serializable {

    private static final long serialVersionUID = 510l;

    @XmlJavaTypeAdapter(JaxbMapAdapter.class)
    @XmlElement(name="results")
    HashMap<String, Object> results = new HashMap<String, Object>();

    @XmlJavaTypeAdapter(JaxbMapAdapter.class)
    @XmlElement(name="facts")
    HashMap<String, Object> facts = new HashMap<String, Object>();
    
    /* (non-Javadoc)
     * @see org.kie.batchexecution.BatchExecutionResult#getIdentifiers()
     */
    public Collection<String> getIdentifiers() {
        return this.results.keySet();
    }
    
    public Object getValue(String identifier) {
        return this.results.get( identifier );
    }
    
    public Object getFactHandle(String identifier) {
        return this.facts.get( identifier );
    }

    /* (non-Javadoc)
     * @see org.kie.batchexecution.BatchExecutionResult#getResults()
     */
    public Map<String, Object> getResults() {
        return this.results;
    }

    public void setResult(String identifier, Object result) {
        this.results.put( identifier, result );
    }

    public void setResults(HashMap<String, Object> results) {
        this.results = results;
    }
    
    public Map<String, Object> getFactHandles() {
        return this.facts;
    }
    
    public void setFactHandles(HashMap<String, Object> facts) {
        this.facts = facts;
    }
}
