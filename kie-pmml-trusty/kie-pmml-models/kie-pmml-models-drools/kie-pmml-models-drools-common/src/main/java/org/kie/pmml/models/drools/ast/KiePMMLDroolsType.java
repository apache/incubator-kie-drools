package org.kie.pmml.models.drools.ast;

import java.util.Objects;

/**
 * Class representing data needed to declare type.
 * For the moment being, only one field is managed, whose name - by default -is <b>"value"</b>
 */
public class KiePMMLDroolsType {

    private final String name;
    private final String type;

    public KiePMMLDroolsType(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "KiePMMLDroolsType{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
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
        KiePMMLDroolsType that = (KiePMMLDroolsType) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type);
    }
}
