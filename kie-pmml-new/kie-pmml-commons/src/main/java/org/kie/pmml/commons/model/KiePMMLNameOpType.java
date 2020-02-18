package org.kie.pmml.commons.model;

import java.util.Objects;

import org.kie.pmml.commons.model.enums.OP_TYPE;

/**
 * Helper DTO to represent a field by its name and <b>operation type</b>
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