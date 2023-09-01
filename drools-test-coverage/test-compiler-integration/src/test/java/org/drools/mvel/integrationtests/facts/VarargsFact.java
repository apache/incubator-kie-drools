package org.drools.mvel.integrationtests.facts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class VarargsFact {

    private List<Long> valueList = new ArrayList<>();

    public VarargsFact() {}

    public List<Long> getValueList() {
        return valueList;
    }

    public void setWrapperValues(Long... values) {
        valueList = Arrays.asList(values);
    }

    public void setPrimitiveValues(long... values) {
        valueList = Arrays.stream(values).boxed().collect(Collectors.toList());
    }

    public void setOneWrapperValue(Long value) {
        valueList = Arrays.asList(value);
    }

    public void setOnePrimitiveValue(long value) {
        valueList = Arrays.asList(value);
    }
}
