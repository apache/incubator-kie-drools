package org.kie.pmml.commons.model.tuples;

import java.util.Objects;

/**
 * Class to represent a <b>name/value (object)</b> tuple
 */
public class KiePMMLNameValue {

    private final String name;

    private final Object value;

    public KiePMMLNameValue(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "KiePMMLNameValue{" +
                "name=" + name +
                ", value=" + value +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        KiePMMLNameValue that = (KiePMMLNameValue) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value);
    }
}
