package org.kie.dmn.typesafe;

import java.util.LinkedHashMap;
import java.util.Map;

import org.kie.dmn.core.impl.FEELPropertyAccessible;
import org.kie.dmn.feel.util.EvalHelper.PropertyValueResult;

public class InputSet implements FEELPropertyAccessible {

    private Object p;

    public Object getP() {
        return p;
    }

    public void setP(Object p) {
        this.p = p;
    }

    @Override
    public PropertyValueResult getFEELProperty(String property) {
        switch (property) {
            case "p":
                return PropertyValueResult.ofValue(getP());
            default:
                return PropertyValueResult.notDefined();
        }
    }

    @Override
    public void setFEELProperty(String key, Object value) {

    }

    @Override
    public Map<String, Object> allFEELProperties() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("p", getP());
        return result;
    }

    @Override
    public void setAll(Map<String, Object> values) {

    }
}
