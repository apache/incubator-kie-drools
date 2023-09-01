package org.drools.compiler.kproject.models;

import java.util.HashMap;
import java.util.Map;

import org.kie.api.builder.model.QualifierModel;

public class QualifierModelImpl implements QualifierModel {
    private String type;
    private String value;
    private Map<String, String> arguments = new HashMap<>();

    public QualifierModelImpl() { }

    public QualifierModelImpl(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public QualifierModel addArgument(String key, String value) {
        arguments.put(key, value);
        return this;
    }

    public Map<String, String> getArguments() {
        return arguments;
    }

    boolean isSimple() {
        return value == null && arguments.isEmpty();
    }
}
