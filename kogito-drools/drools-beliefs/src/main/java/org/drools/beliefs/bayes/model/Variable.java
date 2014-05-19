package org.drools.beliefs.bayes.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@XStreamAlias("VARIABLE")
public class Variable extends VariableXml implements Serializable {
    @XStreamAlias("NAME")
    private String name;

    @XStreamImplicit(itemFieldName = "OUTCOME")
    private List<String> outComes;

    @XStreamImplicit(itemFieldName = "PROPERTY")
    private List<String> properties;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getOutComes() {
        return outComes;
    }

    public void setOutComes(List<String> outComes) {
        this.outComes = outComes;
    }

    public List<String> getProperties() {
        return properties;
    }

    public void setProperties(List<String> properties) {
        this.properties = properties;
    }
}

