package org.drools.beliefs.bayes.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.io.Serializable;
import java.util.List;

public class VariableXml implements Serializable {

    @XStreamImplicit(itemFieldName = "VALUE")
    private List<String> value;

    @XStreamAlias("TYPE")
    private String type;

    public List<String> getValue() {
        return value;
    }

    public void setValue(List<String> value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}

