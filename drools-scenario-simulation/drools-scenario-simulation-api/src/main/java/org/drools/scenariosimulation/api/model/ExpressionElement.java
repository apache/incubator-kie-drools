package org.drools.scenariosimulation.api.model;

import java.util.Objects;

/**
 * Single element of a expression, i.e. in person.fullName.last each component is an ExpressionElement
 */
public class ExpressionElement {

    private String step;

    public ExpressionElement() {
    }

    public ExpressionElement(String step) {
        this.step = step;
    }

    public String getStep() {
        return step;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ExpressionElement that = (ExpressionElement) o;
        return Objects.equals(getStep(), that.getStep());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getStep());
    }
}
