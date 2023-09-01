package org.drools.commands.runtime;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

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
