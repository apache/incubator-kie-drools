package org.drools.beliefs.bayes.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.List;

@XStreamAlias("NETWORK")
public class Network {
    @XStreamAlias("NAME")
    private String name;

    @XStreamImplicit(itemFieldName = "PROPERTY")
    private List<String> properties;

    @XStreamImplicit(itemFieldName = "VARIABLE")
    private List<Variable> variables;

    @XStreamImplicit(itemFieldName = "DEFINITION")
    private List<Definition> definitions;

    public List<Variable> getVariables() {
        return variables;
    }

    public void setVariables(List<Variable> variables) {
        this.variables = variables;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Definition> getDefinitions() {
        return definitions;
    }

    public void setDefinitions(List<Definition> definitions) {
        this.definitions = definitions;
    }

    public List<String> getProperties() {
        return properties;
    }

    public void setProperties(List<String> properties) {
        this.properties = properties;
    }
}
