package org.kie.pmml.commons.model.tuples;

import java.util.Objects;

import org.kie.pmml.api.enums.OP_TYPE;

/**
 * Class to represent a <b>name/operation type</b> tupla
 */
public class KiePMMLNameOpType {

    private final String name;
    private final OP_TYPE opType;

    public KiePMMLNameOpType(String name, OP_TYPE opType) {
        this.name = name;
        this.opType = opType;
    }

    public String getName() {
        return name;
    }

    public OP_TYPE getOpType() {
        return opType;
    }

    @Override
    public String toString() {
        return "NameOpType{" +
                "name='" + name + '\'' +
                ", opType=" + opType +
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
        KiePMMLNameOpType that = (KiePMMLNameOpType) o;
        return Objects.equals(name, that.name) &&
                opType == that.opType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, opType);
    }
}