package org.drools.commands.runtime;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.drools.commands.jaxb.JaxbMapAdapter;

@XmlJavaTypeAdapter(JaxbMapAdapter.class)
public class ExecutionResultsMap {

    Map<String, Object> results = new HashMap<>();

    public Collection<String> keySet() {
        return results.keySet();
    }

    public Object get(String identifier) {
        return results.get(identifier);
    }

    public Map<String, Object> getResults() {
        return this.results;
    }

    public void setResults(Map<String, Object> results) {
        this.results = results;

    }

}
