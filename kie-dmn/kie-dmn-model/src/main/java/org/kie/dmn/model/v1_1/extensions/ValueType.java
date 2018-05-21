package org.kie.dmn.model.v1_1.extensions;

import org.kie.dmn.model.v1_1.DMNModelInstrumentedBase;

import java.util.ArrayList;
import java.util.List;

public class ValueType extends DMNModelInstrumentedBase {
    private String type;
    private Value value;
    private List<Component> component = new ArrayList<>();

    public Value getValue() {
        return value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setValue(Value value) {
        this.value = value;
    }

    public List<Component> getComponent() {
        return component;
    }

    public void setComponent(List<Component> component) {
        this.component = component;
    }
}
