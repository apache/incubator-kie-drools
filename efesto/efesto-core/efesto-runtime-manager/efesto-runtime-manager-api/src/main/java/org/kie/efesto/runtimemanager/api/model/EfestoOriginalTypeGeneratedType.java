package org.kie.efesto.runtimemanager.api.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Class to represent a <b>original type/generated type</b> tupla
 */
public class EfestoOriginalTypeGeneratedType implements Serializable {

    private static final long serialVersionUID = 3887366581807183963L;
    private final String originalType;
    private final String generatedType;

    public EfestoOriginalTypeGeneratedType(String originalType, String generatedType) {
        this.originalType = originalType;
        this.generatedType = generatedType;
    }

    public String getOriginalType() {
        return originalType;
    }

    public String getGeneratedType() {
        return generatedType;
    }

    @Override
    public String toString() {
        return "EfestoOriginalTypeGeneratedType{" +
                "originalType='" + originalType + '\'' +
                ", generatedType='" + generatedType + '\'' +
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
        EfestoOriginalTypeGeneratedType that = (EfestoOriginalTypeGeneratedType) o;
        return Objects.equals(originalType, that.originalType) &&
                Objects.equals(generatedType, that.generatedType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(originalType, generatedType);
    }
}