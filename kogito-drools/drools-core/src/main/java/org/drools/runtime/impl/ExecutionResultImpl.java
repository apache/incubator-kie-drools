package org.drools.runtime.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.drools.runtime.ExecutionResults;
import org.drools.xml.jaxb.util.JaxbMapAdapter;

@XmlRootElement(name="execution-results")
@XmlAccessorType(XmlAccessType.NONE)
public class ExecutionResultImpl implements ExecutionResults, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@XmlJavaTypeAdapter(JaxbMapAdapter.class)
	@XmlElement(name="results")
    HashMap<String, Object> results = new HashMap<String, Object>();

	@XmlJavaTypeAdapter(JaxbMapAdapter.class)
	@XmlElement(name="facts")
	HashMap<String, Object> facts = new HashMap<String, Object>();
    
    /* (non-Javadoc)
     * @see org.drools.batchexecution.BatchExecutionResult#getIdentifiers()
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
     * @see org.drools.batchexecution.BatchExecutionResult#getResults()
     */
    public Map<String, Object> getResults() {
        return this.results;
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
