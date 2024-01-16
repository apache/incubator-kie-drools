/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.commands.runtime;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.drools.commands.jaxb.JaxbMapAdapter;
import org.kie.api.runtime.ExecutionResults;

@XmlRootElement(name="execution-results")
@XmlAccessorType(XmlAccessType.NONE)
public class ExecutionResultImpl implements ExecutionResults, Serializable {

    private static final long serialVersionUID = 510l;

    @XmlJavaTypeAdapter(JaxbMapAdapter.class)
    @XmlElement(name="results")
    Map<String, Object> results = new HashMap<>();

    @XmlJavaTypeAdapter(JaxbMapAdapter.class)
    @XmlElement(name="facts")
    Map<String, Object> facts = new HashMap<>();
    
    @Override
    public Collection<String> getIdentifiers() {
        return this.results.keySet();
    }

    @Override
    public Object getValue(String identifier) {
        return this.results.get( identifier );
    }

    @Override
    public Object getFactHandle(String identifier) {
        return this.facts.get( identifier );
    }

    @Override
    public Map<String, Object> getResults() {
        return this.results;
    }

    @Override
    public void setResult(String identifier, Object result) {
        this.results.put( identifier, result );
    }

    public void setResults(Map<String, Object> results) {
        this.results = results;
    }
    
    public Map<String, Object> getFactHandles() {
        return this.facts;
    }
    
    public void setFactHandles(HashMap<String, Object> facts) {
        this.facts = facts;
    }
}
