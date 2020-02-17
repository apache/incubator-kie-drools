package org.kie.kogito.codegen.rules.singleton;

import java.util.Objects;

public class Datum {

    private String value;

    public Datum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Datum datum = (Datum) o;
        return Objects.equals(value, datum.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "Datum{" +
                "value='" + value + '\'' +
                '}';
    }
}
